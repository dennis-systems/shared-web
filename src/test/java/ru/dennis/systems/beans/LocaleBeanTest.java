package ru.dennis.systems.beans;

import org.junit.Test;

import static org.junit.Assert.*;

public class LocaleBeanTest {

    private LocaleBean test = new LocaleBean();

    @Test
    public void testGetLocaleCurrent(){
        assertEquals(null, test.getLocaleCurrent());
    }
    @Test
    public void testSetLocaleCurrent(){
        test.setLocaleCurrent("de_DE");
        test.setLocaleName("de");
        assertEquals("de_DE",  test.getLocaleCurrent());
        assertTrue(test.isSelected("de"));
        assertFalse(test.isSelected("en"));
    }
}