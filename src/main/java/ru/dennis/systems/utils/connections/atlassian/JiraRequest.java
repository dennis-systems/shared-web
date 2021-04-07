package ru.dennis.systems.utils.connections.atlassian;

import ru.dennis.systems.db.JobElement;
import ru.dennis.systems.repository.QueryCase;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JiraRequest {
    private List<String> keys = new ArrayList<>();
    private List<QueryCase> requests = new ArrayList<>();
    private JobElement jobElement;

    private  DataRetriever dataRetriever;
    private Class<?> clazz;

    public JiraRequest(DataRetriever dataRetriever) {
        this.dataRetriever = dataRetriever;
    }


    public JiraRequest jira(Class<?> returnClass){
        return setReturnClass(returnClass);
    }

    public JiraRequest setReturnClass(Class<?> clazz){
        this.clazz = clazz;
        return this;
    }

    public JiraRequest addField(String key){
        this.keys.add(key);
        return this;
    }
    public JiraRequest job(JobElement job){
        this.jobElement = job;
        return this;
    }
    public JiraRequest addCase(QueryCase key){
        this.requests.add(key);
        return this;
    }

    public <T>T get(LoginProvider provider){
        return (T) dataRetriever.get(provider, jobElement, compile(), new Execution(), clazz);
    }

    private String compile() {
        return dataRetriever.getConfig().getJiraPath() + "?jql=" + toRequest() + "&fields=" + keysToString();
    }

    private String toRequest() {
        List<String> data = new ArrayList<>();
        for (QueryCase queryCase : requests){
            data.add(generateFromCase(queryCase));
        }
        return String.join(" and ", data );
    }

    private String generateFromCase(QueryCase queryCase) {

        switch (queryCase.getOperator()){
            case QueryCase.EQUALS_OPERATOR: {
                return  queryCase.getField() + "=" + queryCase.getValue();
            }

            case QueryCase.IN: {
                return  queryCase.getField() + " in(" + Strings.join((Iterable<?>) queryCase.getValue(), ',') + ") ";
            }
            default: throw new UnsupportedOperationException("Query case doesn't support  operator " + queryCase.getOperator());
        }
    }

    private String keysToString() {
        return String.join(",", keys);
    }

    public static void main(String[] args) {

        AtlassianConfiguration configuration = new AtlassianConfiguration(null){
            @Override
            public String getJiraPath() {
                return "http://localhost:8090";
            }
        };

        DataRetriever retriever = new DataRetriever(configuration);

        JiraRequest request = new JiraRequest(retriever);

        List<String> projects = new ArrayList<>();
        projects.add("test1");
        projects.add("test2");
        projects.add("test4");
        request.jira(String.class).addField("Test").addField("test2")
        .addCase(QueryCase.equalsOf("project", "Test"))
        .addCase(QueryCase.in("issueLink", projects));

        System.out.println(request.compile());
    }
}
