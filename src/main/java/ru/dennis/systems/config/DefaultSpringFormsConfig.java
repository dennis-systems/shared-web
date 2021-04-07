package ru.dennis.systems.config;

import ru.dennis.systems.beans.LocaleBean;
import ru.dennis.systems.utils.PaginationRequestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.Collections;
import java.util.Locale;

@Configuration
public class DefaultSpringFormsConfig implements WebMvcConfigurer {

    private final LocaleBean bean;

    private final Environment environment;

    private final PaginationRequestUtils paginationRequestUtils;
    private final ApplicationContext context;

    @Value("${global.templates.paths:.}")
    private String additionalPaths;

    @Value("${global.templates.encoding:UTF-8}")
    private String baseEncoding;

    private ModelMapperFactory modelMapper;

    public DefaultSpringFormsConfig(ModelMapperFactory mapper, LocaleBean bean, Environment environment, PaginationRequestUtils paginationRequestUtils, ApplicationContext context) {
        this.modelMapper = mapper;
        this.bean = bean;
        this.environment = environment;
        this.paginationRequestUtils = paginationRequestUtils;
        this.context = context;
    }

    @Bean
    @ConditionalOnExpression("${global.user_universal_bean:true}")
    public SpringResourceTemplateResolver templateResolver() {
        // SpringResourceTemplateResolver automatically integrates with Spring's own
        // resource resolution infrastructure, which is highly recommended.
        UniversalSpringResourceResolver templateResolver = new UniversalSpringResourceResolver();
        templateResolver.additionalPaths(additionalPaths);
        templateResolver.setPrefix("classpath:templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        templateResolver.setApplicationContext(context);

        // HTML is the default value, added here for the sake of clarity.
        templateResolver.setTemplateMode(templateResolver.getTemplateMode());
        // Template cache is true by default. Set to false if you want
        // templates to be automatically updated when modified.
        templateResolver.setCacheable(false);
        templateResolver.setNonCacheablePatterns(Collections.singleton("**"));
        return templateResolver;
    }

    @Bean
    public ISpringTemplateEngine templateEngine(){
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.setEnableSpringELCompiler(true);
        springTemplateEngine.setTemplateResolver(templateResolver());
        return springTemplateEngine;
    }

    @Bean
    public ViewResolver viewResolver(){
        ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
        thymeleafViewResolver.setTemplateEngine(templateEngine());
        thymeleafViewResolver.setCharacterEncoding(baseEncoding);
        return thymeleafViewResolver;
    }


    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor() {

            @Override
            protected Locale parseLocaleValue(String localeValue) {
                localeValue = bean.transform(localeValue);
                bean.setLocaleCurrent(localeValue);
                return StringUtils.parseLocale(localeValue);
            }
        };
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);

        return slr;
    }


    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding(baseEncoding);
        return messageSource;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/webjars/**",
                "/img/**",
                "/templates/**",
                "/css/**",
                "/static/js/**")
                .addResourceLocations(
                        "classpath:/META-INF/resources/webjars/",
                        "classpath:/templates/**",
                        "classpath:/static/img/",
                        "classpath:/static/css/",
                        "classpath:/static/js/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName(WebConstants.asPage("", WebConstants.WEB_PAGE_INDEX));
    }

    public Environment getEnvironment(){
        return environment;
    }

    public PaginationRequestUtils paginationRequestUtils(){
        return paginationRequestUtils;
    }

    public ModelMapperFactory modelMapper(){
        return modelMapper;
    }

}
