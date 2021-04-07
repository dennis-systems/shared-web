package ru.dennis.systems.beans;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Objects;

@Service
@SessionScope
@Data
public class LocaleBean {

    private String localeCurrent;
    private String localeName;

    public boolean isSelected(String localeCurrent){
        return Objects.requireNonNullElse(this.localeCurrent, "en").equalsIgnoreCase(transform(localeCurrent));
    }
    public String transform(String localeName){
        this.localeName = localeName;
        if ("en".equalsIgnoreCase(localeName) ){
            return "en_US";
        } else {
            return "de_DE";
        }
    }

}
