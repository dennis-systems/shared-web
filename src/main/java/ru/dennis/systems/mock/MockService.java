package ru.dennis.systems.mock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;


@RestController
@RequestMapping("rest/api/2/search")
@Slf4j
public class MockService {

    @Autowired(required = false)
    private PathToDataConverter converter;


    @GetMapping (produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody byte[] get(@RequestParam("jql") String query) throws IOException {
        if (!query.contains(".")){
            query = query + ".json";
        }
        return getFile(query, null, HttpMethod.GET );

    }

    @GetMapping (produces = TEXT_HTML_VALUE)
    public @ResponseBody String string(@RequestParam("jql") String query) throws IOException {
        if (!query.contains(".")){
            query = query + ".json";
        }
        return new String( getFile(query,null, HttpMethod.GET ));

    }

    @RequestMapping (value = "/post/", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public @ResponseBody String stringPost(@RequestParam(value = "jql", required = false) String query, @RequestBody String data) throws IOException {
        if (query != null && !query.contains(".")){
            query = query + ".json";
        }
        return new String( getFile(query, data, HttpMethod.POST));

    }

    public @ResponseBody byte[] getFile(String query, Object data, HttpMethod method) throws IOException {
        if (converter != null){
            log.info("**** Incoming path:" + query);
            query = converter.convert(query, method,  data);
            log.info("**** out coming path: " + query + " method: " + method + " dat: " + data);
        }

        log.info("Requested file: " + query );
        try (InputStream in = new ClassPathResource(query).getInputStream()) {
            return IOUtils.toByteArray(in);
        }
        catch (Exception e){
            log.error("can't fetch file " + query  + " data " + data + " method: "+ method );
        }
        return new byte[]{};
    }

}
