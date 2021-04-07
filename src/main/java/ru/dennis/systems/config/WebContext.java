package ru.dennis.systems.config;

import ru.dennis.systems.utils.PaginationRequestUtils;
import ru.dennis.systems.repository.QueryCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@AllArgsConstructor
@Data
@Service
@Slf4j
public class WebContext {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private DefaultSpringFormsConfig config;

    @Autowired
    private ResourceBundleMessageSource messages;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PaginationRequestUtils paginationRequestUtils;


    private String getMessageTranslation( String header){
        final String message=  messages.getMessage(
                header.toLowerCase(),
                new Object[]{},
                "?_" + header + "_?",
                config.localeResolver().resolveLocale(request));

        if (message == null || (message.startsWith("?_") && message.endsWith("_?"))){
            log.warn(header + " not found !");
        }
        return message;

    }

    private String getMessageTranslation( String header, Object ... params){
        final String message=  messages.getMessage(
                header.toLowerCase(),
                params,
                "?_" + header + "_?",
                config.localeResolver().resolveLocale(request));

        if (message == null || (message.startsWith("?_") && message.endsWith("_?"))){
            log.warn(header + " not found !");
        }
        return message;

    }
    private String getMessageTranslation( String header, Locale locale, Object ... params){
        final String message=  messages.getMessage(
                header.toLowerCase(),
                params,
                "?_" + header + "_?",
                locale);

        if (message == null || (message.startsWith("?_") && message.endsWith("_?"))){
            log.warn(header + " not found !");
        }
        return message;

    }

    private ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    public static class LocalWebContext {
        private final WebContext webContext;
        private String scope;

        public HttpServletRequest getRequest(){
            return webContext.getRequest();
        }

        public String httpParam(String param){
            return webContext.getRequest().getParameter(param);
        }

        public DefaultSpringFormsConfig getConfig(){
            return webContext.getConfig();
        }

        public String getMessageTranslation( String header) {
            return webContext.getMessageTranslation(
                    scope + "."
                            + header.toLowerCase() );
        }
        public String getMessageTranslation( String header, Object ... params) {
            return webContext.getMessageTranslation(
                    scope + "."
                            + header.toLowerCase() , params);
        }

        private LocalWebContext(String scope, WebContext context){
            this.scope = scope;
            this.webContext = context;
        }
        public static LocalWebContext of(String scope, WebContext context){
            return new LocalWebContext(scope, context);

        }

        public Specification getRequestSpecification(Class<?> pojo){
            return getRequestSpecification( pojo, Collections.emptyList());
        }
        public Specification getRequestSpecification(Class<?> pojo, List<QueryCase> caseList){
            return webContext.getPaginationRequestUtils().getFilteringParams(this, pojo, caseList);
        }

        public <T>T transform(Object item, Class<? extends T> testPlanViewClass) {
           return getConfig().modelMapper().map(item, testPlanViewClass);
        }

        public String getScoped(String header) {
            return scope + "." + header;
        }

        public <T>T getAttribute(String attribute){
            return (T) getRequest().getSession().getAttribute(attribute);
        }

        public void removeAttribute(String attribute) {
            getRequest().getSession().removeAttribute(attribute);
        }

        public  void setAttribute(String attribute, Serializable value) {
            getRequest().getSession().setAttribute(attribute, value);
        }

        public <T>T getBean(Class <T> bean){
            return webContext.getApplicationContext().getBean(bean);
        }
    }

}
