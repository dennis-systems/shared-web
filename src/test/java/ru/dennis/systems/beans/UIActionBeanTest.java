package ru.dennis.systems.beans;

import ru.dennis.systems.config.DefaultSpringFormsConfig;
import ru.dennis.systems.config.ModelMapperFactory;
import ru.dennis.systems.config.WebContext;
import ru.dennis.systems.utils.PaginationRequestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest (classes = {UIActionBean.class, WebContext.class,
        DefaultSpringFormsConfig.class,
        ModelMapperFactory.class, PaginationRequestUtils.class,
        LocaleBean.class})
@RunWith(SpringJUnit4ClassRunner.class)


public class UIActionBeanTest {

    @Autowired
    public UIActionBean testBean;

    @Test
    public void testUrl(){
        String path = testBean.start().append("id", "10").append("name", "test").append("asc", "true").invert("asc", "true").get();
        assertTrue(path.contains("asc=false"));
        assertTrue(path.contains("name=test"));
    }
    @Test
    public void testUrlChangeParameter(){
        testBean.setRequest(request());
        String path = testBean.start().append("name", "index").invert("asc", "true").get();

        assertTrue(path.contains("asc=true"));
        assertTrue(path.contains("name=index"));
        assertTrue(path.contains("id=10"));

    }

    @Test
    public void testGetCurrentLocale(){
        assertEquals("en", testBean.getCurrentLang());
    }


    HttpServletRequest request (){
        return new TestHttpServletRequest();
    }

}