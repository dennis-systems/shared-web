package ru.dennis.systems.config;

public class WebConstants {

    public static final String WEB_PAGE_JOB_BASE = "/job";
    public static final String WEB_PAGE_NOTIFICATION_BASE = "/notification";
    public static final String WEB_PAGE_LIST = "/list";
    public static final String WEB_PAGE_INDEX = "/index";
    public static final String WEB_API_ADD = "/add";
    public static final String WEB_API_EDIT = "/edit/{id}";
    public static final String WEB_API_DOWNLOAD = "/download/{id}";
    public static final String WEB_API_DELETE = "/delete/{id}";
    public static final String WEB_API_LIST = "/list";
    public static final String WEB_PAGE_ADD = "/add";

    public static final String WEB_OBJECT_MODEL_ATTRIBUTE = "objectItem";
    public static final String WEB_FORM_DESCRIPTOR_ATTRIBUTE = "form";
    public static final String WEB_FORM_FIELD_MODEL_ATTRIBUTE = "formFields";
    public static final String  WEB_VALIDATION_CONTEXT_ATTRIBUTE = "validationContext";
    public static final String WEB_FORM_VALUE_ATTRIBUTE = "values";

    public static String withMessage(String message, String path){
        return path + "/?message=" + message;
    }

    public static String asRedirect(String path, String directory){
        return "redirect:" + path+ directory;
    }

    public static String asPage(String path, String directory){
        return "/pages" +path + directory ;
    }
    public static String asService(String path, String directory){
        return path + directory ;
    }
}