package ru.dennis.systems.utils.connections.atlassian;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
public class AtlassianClient {

    //todo reimplement with bean ? implement clear?
    public static final  AgnosticHttpCookieJar JAR = new AgnosticHttpCookieJar();
    public static  OkHttp3ClientHttpRequestFactory factory ;

    private RestTemplate template;
    private Class<?> returnPojo;
    private boolean ready;
    private HttpHeaders headers;

    private AtlassianClient() {
    }

    public static AtlassianClient create(AtlassianConfiguration config, boolean switchToOk) {
        if (switchToOk) {
            log.debug("Using HttpOk instead of apache, Reason of using cookies or similar");
            return createOK(config);
        } else {
            log.debug("Using Apache Connector");
            return createApache(config);
        }
    }

    private AtlassianClient setRestTemplate(RestTemplate template) {
        this.template = template;
        return this;
    }


    public static AtlassianClient create(AtlassianConfiguration config, LoginProvider provider) {
        return create(config, false);
    }

    public static AtlassianClient createOK(AtlassianConfiguration config) {

        RestTemplate template = new RestTemplate();

        log.debug("CONFIG: " + config.toString());

        template.setRequestFactory(factory == null ? factory = new OkHttp3ClientHttpRequestFactory(new OkHttpClient.Builder().proxy(config.getProxyOk()).cookieJar(JAR)
                .connectTimeout(Duration.ofSeconds(10)).writeTimeout(Duration.ofSeconds(10))
                .connectionPool(new ConnectionPool()).readTimeout(Duration.ofSeconds(10)).build()) : factory);
        return new AtlassianClient().setRestTemplate(template);
    }

    public static AtlassianClient createApache(AtlassianConfiguration config) {
        log.debug("--------------------------------------- CONFIG: -----------------------------------------------------");
        log.debug("| " + config.toString());
        log.debug("--------------------------------------- END CONFIG: -------------------------------------------------");
        RestTemplate template = new RestTemplate();

        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        HttpHost proxy = config.getProxy();
        if (proxy != null) {
            requestBuilder.setProxy(proxy);
        }
        HttpClient httpClient = HttpClients.custom().setProxy(config.getProxy()).setDefaultRequestConfig(requestBuilder.build()).build();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        return new AtlassianClient().setRestTemplate(template);
    }


    public AtlassianClient auth(LoginProvider provider) {
        log.debug("  [AC] auth method called");
        log.debug("  [AC] Authorization for: " + (provider == null ? "Null" : provider.getName() + " | -> " + provider.getLogin()));
        headers = new HttpHeaders();
        if (provider == null) {
            return this;
        }
        headers.setBasicAuth(provider.getLogin(), provider.getPassword());
        return this;
    }

    public void setHeader(String header, String value) {
        if (header == null) {
            return;
        }
        if (headers == null) {
            headers = new HttpHeaders();
        }
        headers.add(header, value);
    }

    public AtlassianClient setReturnPojo(Class<?> returnPojo) {
        log.debug("  [AC] set return pojo called: " + returnPojo.getName());
        this.returnPojo = returnPojo;
        this.ready = true;
        return this;
    }

    public <T> ResponseEntity<T> get(String path) {
        log.info("*******************************");
        log.info("*    RETRIEVING PATH: " + path);
        log.info("*******************************");
        if (!this.ready) {
            log.warn("  [Auth client JIRA: ]   NOT YET READY OBJECT, CALL Method setReturnPojo Before start ");
            throw new IllegalArgumentException("The execute method of the template is not able to be done without create method running and return pojo");
        }
        HttpEntity<T> entity = null;
        if (headers != null) {
            entity = new HttpEntity<>(headers);
        }
        return (ResponseEntity<T>) this.template.exchange(path, HttpMethod.GET, entity, returnPojo);
    }

    public String getContent(String path) {


        return (String) this.setReturnPojo(String.class).get(path).getBody();
    }

    public byte[] getByteContent(String path) {
        log.info("*******************************");
        log.info("*    RETRIEVING PATH: " + path);
        log.info("*******************************");
        if (!this.ready) {
            log.warn("  [Auth client JIRA: ]   NOT YET READY OBJECT, CALL Method setReturnPojo Before start ");
            throw new IllegalArgumentException("The execute method of the template is not able to be done without create method running and return pojo");
        }

        return (byte[]) this.setReturnPojo(byte[].class).get(path).getBody();
    }

    public String getPostContent(String path, Object postObject, HttpHeaders headers) {
        log.info("*******************************");
        log.info("*    RETRIEVING PATH: " + path);
        log.info("*******************************");
        log.info(" ******************* Object : " + postObject.toString());
        log.info("*******************************");
        if (!this.ready) {
            log.warn("  [Auth client JIRA: ]   NOT YET READY OBJECT, CALL Method setReturnPojo Before start ");
            throw new IllegalArgumentException("The execute method of the template is not able to be done without create method running and return pojo");
        }

        if (this.headers != null) {
            headers.addAll(this.headers);
        }
        HttpEntity<Object> entity = new HttpEntity<>(postObject, headers);
        log.info("Traced element: " + entity + "headers " + headers);
        return this.template.postForObject(path, entity, String.class);
    }
}
