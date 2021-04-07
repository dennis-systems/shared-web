package ru.dennis.systems.utils.connections.atlassian;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Data
@ToString (exclude = "password")
@Component
@Scope("singleton")
public class LoginProvider {

    private String login;
    private String password;

    private String name;

    private String rootPath;

    @Autowired
    private Environment environment;

    @Autowired
    private AtlassianConfiguration config;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Value("${xray.confluence.path:https://confluence.allianz.de}")
    private String confluencePath;

    private LoginProvider(){

    }

    private LoginProvider(String password, String login,  String rootPath, String name){
        this.password = password;
        this.login = login;
        this.name = name;
        this.rootPath = rootPath;

    }

    public LoginProvider jira(){
        if (config == null){
            //Already initialized
            return this;
        }
        return new LoginProvider( config.getJiraPassword(), config.getJiraLogin() , config.getJiraPath() ,  " JIRA " );

    }

    public LoginProvider confluence(){
        if (config == null){
            //Already initialized
            return this;
        }
        return  new LoginProvider(config.getJiraPassword(), config.getJiraLogin() , confluencePath, " CONFLUENCE");
    }




}
