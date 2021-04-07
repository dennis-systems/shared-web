package ru.dennis.systems.config;

import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class ModelMapperFactory {
    private final ModelMapper mapper = new ModelMapper();

    public <T> List<T> map(List<?> items, Class<T> clas){
        List<T> result = new ArrayList<>();
        for (Object item : items){
            result.add(mapper.map(item, clas));
        }
        return  result;
    }

    public <T, E>T map(E object, Class<T> clas){
        return mapper.map(object, clas);
    }
}
