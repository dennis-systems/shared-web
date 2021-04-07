package ru.dennis.systems.utils.connections.atlassian;

import ru.dennis.systems.db.JobElement;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;

@Service
@Slf4j
@Scope ("prototype")
public class DataRetriever {

    private final AtlassianConfiguration config;


    private final HashMap<String, Object> cache = new HashMap<>();
    private boolean switchToOk;

    private HttpHeaders headers = new HttpHeaders();

    @Value("${server.port}")
    private int serverPort;

    public DataRetriever(@Qualifier("atlassianConfiguration") AtlassianConfiguration config) {
        this.config = config;
    }

    public void clear() {
        cache.clear();
    }

    public DataRetriever switchToOk3() {

        if (config.isDemo()) {
            return this;
        }
        this.switchToOk = true;
        return this;
    }

    public DataRetriever switchToApache() {
        this.switchToOk = false;
        return this;
    }

    public DataRetriever addHeader(String header, String value) {
        headers.add(header, value);
        return this;
    }


    @SneakyThrows
    public <T> T get(LoginProvider provider, JobElement job, String path, Execution execution, Class<T> c) {

        log.info("Started client on get Request, path: " + path);
        if (config.restClientCacheEnabled() && cache.get(path) != null) {
            log.info("Used value from cache for path: " + path);
            return (T) cache.get(path);
        }

        if (execution == null){
            execution = new Execution();
        }
        if (job == null){
            job = (JobElement) () -> 0L;
        }
        Execution subExecution = execution.addExecution(" Fetching jira path: " + path + " job -> " + job.getId());
        log.info("              Parser started");
        if (config.isDemo()) {
            log.debug("                  Current config leads to demo, bug_v3.json, localhost");
            String res = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + "/rest/api/2/search").queryParam("jql", path).build().toUriString();
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<T> data = restTemplate.getForEntity(res, c);
            log.debug("                  ... finished");
            subExecution.finish();
            if (config.restClientCacheEnabled()) {
                cache.put(path, data.getBody());
            }
            log.info("set value to cache for path: " + path);
            return data.getBody();
        } else {
            log.debug("                  Current config leads to production, using server");
            AtlassianClient client = AtlassianClient.create(config, switchToOk);

            addHeaders(client);
            client.setReturnPojo(c).auth(provider);

            try (subExecution) {
                T result = (T) client.get(path).getBody();
                if (config.restClientCacheEnabled()) {
                    cache.put(path, result);
                }
                return result;
            } catch (Exception e) {
                log.error("Cannot load data for path: " + path, e);
                onError(e, job, path);
                return c.getConstructor().newInstance();
            }
        }
    }

    private void addHeaders(AtlassianClient client) {

        headers.keySet().forEach(x -> client.setHeader(x, headers.get(x).get(0)));
        log.debug("Headers [" + headers.size() + "] had been added to request");
        headers.clear();
    }

    @SneakyThrows
    public String getContent(LoginProvider provider, JobElement job, String path, Execution execution) {
        return get(provider, job, path, execution, String.class);
    }

    @SneakyThrows
    public String getPostContent(LoginProvider provider, JobElement job, String path, Execution execution, Object postObject) {

        //AGR-303 not really this issue, but was changed because of it. Post content should not orient on path , but should be oriented on form data, or not
        //Used cache data at all (current implementation)
        if (execution == null) execution = new Execution();
        if (job == null){
            job = (JobElement) () -> 0L;
        }
        Execution subExecution = execution.addExecution(" Fetching  path: " + path + " job -> " + job.getId());
        log.info("              Parser started");
        if (config.isDemo()) {
            log.debug("                  Current config leads to demo, bug_v3.json, localhost");
            String res = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + "/rest/api/2/search/post/").build().toUriString();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            String data = restTemplate.postForObject(res, new HttpEntity<>(postObject, requestHeaders), String.class);
            log.debug("                  ... finished");
            subExecution.finish();

            log.info("set value to cache for path: " + path);
            return data;
        } else {
            log.debug("                  Current config leads to production, using server");
            AtlassianClient client = AtlassianClient.create(config, switchToOk);

            addHeaders(client);
            client.setReturnPojo(String.class).auth(provider);
            headers.setContentType(MediaType.APPLICATION_JSON);
            try (subExecution) {
                return client.getPostContent(path, postObject, headers );
            } catch (Exception e) {
                log.error("Cannot load data for path: " + path, e);
                onError(e, job, path);
                return "";
            }
        }
    }
    @SneakyThrows
    public byte[] getBytes(LoginProvider provider, JobElement job, String path, Execution execution) {
        return getBytes(provider, job, path, execution, 0);
    }

    @SneakyThrows
    public byte[] getBytes(LoginProvider provider, JobElement job, String path, Execution execution, int retries) {
        try {
            return get(provider, job, path, execution, byte[].class);
        } catch (Exception e){
            if (retries > 0){
                Thread.sleep(5000);
                return getBytes(provider, job, path, execution, retries-1);
            } else {
                throw e;
            }
        }
    }

    public void onError(Exception e, JobElement job, String path) {
        log.error("Error : " + path + " -> " + e.getMessage(), e);
    }

    public AtlassianConfiguration getConfig() {
        return this.config;
    }

}
