package ru.dennis.systems.utils.connections.atlassian;

import lombok.ToString;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.el.PropertyNotFoundException;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Component
@Scope("singleton")
public class AtlassianConfiguration {
    protected final Environment environment;

    public AtlassianConfiguration(Environment environment) {
        this.environment = environment;
    }


    public boolean isDemo() {
        return environment.getProperty("xray.demo", Boolean.class, false);
    }
    public String getJiraPath(){
        return get("xray.jira.api.path");
    }

    private String get(String key) {
        return environment.getProperty(key, Strings.EMPTY);
    }

    public String getOrThrow(String key) {
        String value = environment.getProperty(key);
        if (value ==null){
            throw new PropertyNotFoundException("property not found: " + key);
        }
        return value;
    }
    public String getJiraLogin() {
        return get("xray.jira.auth.login");
    }

    public String getJiraPassword() {
        return get("xray.jira.auth.password");
    }

    public String getProxyHost() {
        return get("xray.proxy.host");
    }

    public Integer getProxyPort() {
        return environment.getProperty("xray.proxy.port", Integer.class, 0);
    }


    public HttpHost getProxy() {
        if ( !Strings.isBlank(getProxyHost() )) {
            return new HttpHost(getProxyHost(), getProxyPort());
        } else return null;
    }

    public Proxy getProxyOk() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(getProxyHost(), getProxyPort()));
    }

    public boolean restClientCacheEnabled() {
        return environment.getProperty("global.rest_client_cache_enabled", Boolean.class, false);
    }

    public String toString(){
        return "[Proxy: " + getProxyHost() + ":" + getProxyPort() + " Jira Login: " + getJiraLogin() + " jira path:  " + getJiraPath();
    }
}
