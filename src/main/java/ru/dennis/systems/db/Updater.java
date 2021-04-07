package ru.dennis.systems.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ru.dennis.systems.beans.IsolatedDatabaseBean;
import ru.dennis.systems.entity.db.DbInjection;
import ru.dennis.systems.entity.db.DbInjectionModel;
import ru.dennis.systems.repository.UpdateRepository;
import ru.dennis.systems.utils.SimpleEvaluator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.*;

@Service
@Slf4j
@Scope(
        value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Updater {

    private static final String DEFAULT_PROFILE = "";
    private final Environment environment;
    private final EntityManagerFactory entityManagerFactory;
    private final UpdateRepository repository;

    private final DbUpdateConfig config;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final IsolatedDatabaseBean isolatedBean;
    private final SimpleEvaluator evaluator;

    private String appName;
    private boolean isFree = true;

    public Updater(DbUpdateConfig config, Environment environment, EntityManagerFactory entityManagerFactory, UpdateRepository repository, IsolatedDatabaseBean isolatedBean, SimpleEvaluator evaluator) {
        this.config = config;
        this.environment = environment;
        this.entityManagerFactory = entityManagerFactory;
        this.repository = repository;
        this.isolatedBean = isolatedBean;
        this.evaluator = evaluator;
    }

    public List<DbInjection> updateDb() throws IOException {
        isFree = false;
        if (!config.isUseDbUpdater()) {
            log.info("update db is disabled");
            return Collections.EMPTY_LIST;
        }


        configureAppName();

        List<DbInjection> injections = loadBasics(config.getDefaultUpdateFile());
        List<DbInjection> custom = new ArrayList<>();
        try {
            custom = loadBasics(config.getProjectUpdateFile());
        } catch (Exception e) {
            log.info("Could not load resource from: " + config.getDefaultUpdateFile());
        }

        log.info("update db is enabled: " + injections.size() + " base injections found");

        for (DbInjection injection : injections) {
            if (injection.getName() == null) {
                throw new IllegalArgumentException("name should be set for the [base] script and should be unique" + injection);
            }
            for (DbInjection customInjection : custom) {
                if (customInjection.getName() == null) {
                    throw new IllegalArgumentException("name should be set for the [custom] script and should be unique" + injection);
                }
                if (injection.getName().equals(customInjection.getName())) {
                    throw new IllegalArgumentException("script name overrides defaults. Change default update file with : global.basic_update_file in application properties or change name of the script " + injection.getName());
                }
            }
        }
        log.info("Running injections of the core: " + injections.size() + " injections found");
        List<DbInjection> result = new ArrayList<>(callInjections(injections));
        log.info("Finished Core, " + result.size() + " Running injections of the project: " + custom.size() + " injections found");
        int coreCount = result.size();
        result.addAll(callInjections(custom));
        log.info("Finished injections of the project: " + (result.size() - coreCount) + " of " + custom.size() + " injections are run");
        isFree = true;

        return result;
    }

    @SneakyThrows
    private void configureAppName() {
        InetAddress ip = InetAddress.getLocalHost();
        appName = "Unknown instance  : " + ip.getHostAddress();
    }

    private List<DbInjection> callInjections(List<DbInjection> injections) {
        List<DbInjection> result = new ArrayList<>();
        for (var injection : injections) {
            log.debug("Processing injection " + injection);
            try {
                injection.setResult(true);
                if (checkIdentifier(injection)) {
                    if (execute(injection)) {
                        result.add(injection);
                    }
                } else {
                    log.info("Injection : " + injection.getName() + " is already run, no need to run");
                }
            } catch (Throwable e) {
                injection.setMessage(e.getMessage());
                injection.setResult(false);

                log.error("Error in script: ", e);
                if (injection.isFailOnExecute()) {
                    throw e;
                }
            } finally {
                if (!injection.isIsolated()) {
                    try {
                        repository.save(injection);
                    } catch (Exception e) {
                        log.info("Could not save history log for : " + injection);
                    }
                }
            }
        }

        return result;
    }

    private boolean checkIdentifier(DbInjection injection) {
        if (injection.isAlwaysToRun()) return true;

        try {

            PageRequest request = PageRequest.of(0, 1, Sort.Direction.DESC, "id");

            List<DbInjection> dbInjection = repository.getFirstByNameAndProfile(injection.getName(), activeProfile, request);
            boolean result = dbInjection.size()== 0;

            if (result) return true;

            log.info("Script " + injection.getName() + " was run, but failed, run it again IF it is  set so!");
            return !dbInjection.get(0).isResult() && dbInjection.get(0).isRestartOnFail();
        } catch (Exception e) {
            return true;
        }
    }

    private boolean execute(DbInjection injection) {
        HashMap<String, String> data = new HashMap<>();
        data.put("appName", appName);
        EntityManager session = null;
        try {
             session = entityManagerFactory.createEntityManager();

            if (injection.getProfile() != null && !injection.getProfile().equalsIgnoreCase(activeProfile)) {
                log.info("this injection is only for profile: " + injection.getProfile() + " application is run under '" + activeProfile + "' profile");
                return false;
            }

            if (injection.getProfile() == null) {
                injection.setProfile(DEFAULT_PROFILE);
            }

            if (injection.getIfSql() != null) {
                String ifSql = evaluator.evaluate(injection.getIfSql(), 0, data);

                if (isolatedBean.sqlWithResult(ifSql, injection.root)) {
                    return false;
                }

            }

            if (!injection.isIsolated()) {
                session.getTransaction().begin();
                session.createNativeQuery(evaluator.evaluate(injection.getSql(), 0, data)).executeUpdate();
                session.getTransaction().commit();
                session.close();
            } else {
                if(injection.getDb() != null){
                    isolatedBean.sqlWithConnection(evaluator.evaluate(injection.getDb(), 0, data), evaluator.evaluate(injection.getSql(), 0, data), injection.root);
                } else {
                    isolatedBean.sqlWithConnection(evaluator.evaluate(injection.getSql(), 0, data), injection.root);
                }
            }
            return true;
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }



    private List<DbInjection> loadBasics(String name) throws IOException {
        try {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());


            DbInjectionModel model = mapper.readValue(getClass().getClassLoader().getResourceAsStream(name),
                    DbInjectionModel.class);


            List<DbInjection> scripts = model.getScripts();
            for (int i = 0, scriptsSize = scripts.size(); i < scriptsSize; i++) {
                DbInjection injection = scripts.get(i);

                if (injection.getName() == null) {
                    throw new IllegalArgumentException("Injection should have name and it should be unique : script#" + i + " " + injection);
                }

                if (injection.getScript() == null && injection.getSql() == null) {
                    log.error("DB update cancelled, error");
                    throw new IllegalArgumentException("Injection field should neither contain script nor path to sql in the same field \"sql\"");
                }

                if (injection.getSql() != null && injection.getScript() != null) {
                    log.warn("! Both script and sql selected, only script parameter will be used! " + injection.getName());
                }


                if (injection.getScript() != null) {
                    injection.setSql(readFrom(injection));
                }

            }

            final Set<DbInjection> injectionSet = new HashSet<>(model.getScripts());
            if (injectionSet.size() != model.getScripts().size()) {
                throw new IllegalArgumentException("Looks like you have the same named object");
            }

            return model.getScripts();
        } catch (Exception e) {
            log.error("Could not load config file from : " + name, e);
            return Collections.emptyList();
        }
    }

    @SneakyThrows
    private String readFrom(DbInjection injection) {
        try (InputStream inputStream = new ClassPathResource(injection.getScript()).getInputStream()) {
            return FileCopyUtils.copyToString(new InputStreamReader(inputStream));
        }

    }

    public List<DbInjection> getForeignListOfScripts() {
        return Collections.emptyList();
    }

    public boolean isFree() {
        return isFree;
    }
}
