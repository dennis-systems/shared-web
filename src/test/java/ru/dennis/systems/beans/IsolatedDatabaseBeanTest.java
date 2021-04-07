package ru.dennis.systems.beans;

import org.junit.*;
import org.junit.runner.OrderWith;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Alphanumeric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = IsolatedDatabaseBean.class)
@TestPropertySource("classpath:application.properties")
@RunWith(SpringJUnit4ClassRunner.class)
@OrderWith(Alphanumeric.class)
public class IsolatedDatabaseBeanTest {

    @Value("${spring.datasource.database}")
    public String DATABASE_NAME;

    @Autowired
    IsolatedDatabaseBean bean;


    @Test
    public void test_3_ConnectionResultSet() {
        bean.sqlWithConnection("create table "  + DATABASE_NAME + "_table (id int) ", false);

       Assert.assertFalse(  bean.sqlWithResult("select 0 from " + DATABASE_NAME+ "_table ", false));

        bean.sqlWithConnection("insert into "  + DATABASE_NAME + "_table values(1) ", false);
        Assert.assertTrue(  bean.sqlWithResult("select 0 from " + DATABASE_NAME+ "_table ", false));

    }

    @Before
    public void setUp(){
        bean.sqlWithConnection("create database " + DATABASE_NAME, true);
    }


    @After
    public void tearDown(){
        bean.sqlWithConnection("drop database " + DATABASE_NAME,true);
    }


}
