package ru.dennis.systems.pojo_form;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ValidationContext implements Serializable {
    private boolean containsErrors;
    Map<String, List<ValidationResult>> data = new HashMap<>();

    public List<String> getValidationMessages(){
        List<String> res = new ArrayList<>();
        for (List<ValidationResult> key: data.values()){
           for (ValidationResult result : key){
               if (!result.getResult() && !Strings.isBlank(result.getErrorMessage())){
                   res.add(result.getErrorMessage());
               }
           }
        }
        return res;
    }

    public boolean hasErrors(String field){
        List<ValidationResult> results =  data.get(field);
        for (ValidationResult result : results){
            if (!result.getResult()){
                return true;
            }
        }
        return false;
    }

}
