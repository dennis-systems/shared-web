package ru.dennis.systems.beans;

import ru.dennis.systems.config.WebContext;
import ru.dennis.systems.pojo_view.PojoListView;
import ru.dennis.systems.pojo_view.UIAction;
import ru.dennis.systems.pojo_view.UIActionParameter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.dennis.systems.pojo_view.PojoViewField;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Component("ui")
@Scope("singleton")
/**
 * Contains a number of UI methods which are related to the
 * automatic form creation, and some HttpRequest methods
 *
 * @see PojoListView , {@link PojoViewField}
 */
public class UIActionBean {

    private HttpServletRequest request;

    private final WebContext context;
    private LocaleBean localeBean;

    public UIActionBean(HttpServletRequest request, WebContext context, LocaleBean localeBean) {
        this.request = request;
        this.context = context;
        this.localeBean = localeBean;
    }

    void setRequest(HttpServletRequest request){
        this.request = request;
    }

    /**
     * Returns a parameter from the UIAction in {@link PojoViewField}
     * @param action
     * @param attribute
     * @return value of the attribute or empty string if not found
     */
    public String get(UIAction action, String attribute) {
        UIActionParameter[] parameters = action.parameters();
        for (UIActionParameter parameter : parameters) {
            if (parameter.key().equals(attribute)) {
                return parameter.value();
            }
        }

        return Strings.EMPTY;

    }

    /**
     * Returns a value from {@link org.springframework.core.env.Environment}, or empty string
     * @param param Name of the parameter to be used
     * @return
     */
    public String getSystemParam(String param) {

        return context.getConfig().getEnvironment().getProperty(param, "");

    }

    /**
     * Returns currect Locale or by default "en"
     * @return value from {@link LocaleBean}
     */

    public String getCurrentLang(){

        try {
            return localeBean == null || localeBean.getLocaleCurrent() == null ? "en" : localeBean.getLocaleCurrent().substring(0, 2);
        } catch (Exception e){
            //e is ignored
            return "en";
        }
    }

    /**
     * Create a call to JS as a text
     * @param action action of the {@link PojoViewField}
     * @param attribute - an attribute to fetch data
     * @param functionParameters - parameter values of the function
     * @return Empty if not found, or a String presentation of function call
     */

    public String getJsFunction(UIAction action, String attribute, String functionParameters) {
        UIActionParameter[] parameters = action.parameters();
        for (UIActionParameter parameter : parameters) {
            if (parameter.key().equals(attribute)) {
                return parameter.value() + " (" + functionParameters + " )";
            }
        }

        return Strings.EMPTY;

    }

    /**
     * Formats date, and sets locale to CET
     * @param date - source date
     * @param pattern - patter to format
     * @return S string representation of given date and pattern
     */
    public String formatDate(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        final SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone("CET"));
        return formatter.format(date);
    }



    public int getPlusOne(String key){
        String value = getFromRequest(key);
        if (value == null){
            value = "0";
        }
        int res = Integer.parseInt(value);

        return  res + 1;
    }


    /**
     * URL GENERATOR
     *
     * creates an url, from given parameters, removing duplicates
     */
    public class UIQuery {

        private StringBuffer uri;
        private Map<String, String[]> params;
        boolean invertingDefault = false;


        public UIQuery(HttpServletRequest request) {
            this.params = new HashMap<>(request.getParameterMap());
            uri = new StringBuffer(request.getRequestURI());
        }

        public UIQuery appendPlusOne(String key){
            String value = getFromRequest(key);
            if (value == null){
                value = "0";
            }
            int res = Integer.parseInt(value);

            append(key, String.valueOf(res + 1));
            return this;
        }


        public UIQuery append(String key, String value) {
            if (params.get(key) != null) {
                if (!params.get(key)[0].equals(value)) {
                    setInvertingDefault();
                }
            }
            params.put(key, new String[]{value});
            return this;
        }

        private void setInvertingDefault() {
            this.invertingDefault = true;
        }

        /**
         * Reverse a correspondent parameter, for example from true to false.
         * Mainly was created to revers search directions
         * @param key - key to revert
         * @param defaultValue - default value if not parameter yet set
         * @return
         */
        public UIQuery invert(String key, String defaultValue) {
            if (invertingDefault) {
                params.put(key, new String[]{defaultValue});
                invertingDefault = false;
                return this;
            }
            try {
                boolean value = Boolean.parseBoolean(params.get(key)[0]);
                params.put(key, new String[]{String.valueOf(!value)});
            } catch (Exception e) {
                params.put(key, new String[]{defaultValue});
            }
            return this;
        }

        /**
         * Returns a valid url
         * @return
         */

        public String get() {
            boolean containsParameters = false;
            for (String key : params.keySet()) {
                if (!containsParameters) {
                    uri.append("?");
                    containsParameters = true;
                } else {
                    uri.append("&");
                }
                uri.append(key).append("=").append(params.get(key)[0]);
            }
            return uri.toString();
        }

    }

    public UIQuery start() {
        return new UIQuery(request);
    }

    public String getFromRequest(String param) {
        return request.getParameter(param);
    }

    public boolean getParamIs(String param, String value) {
        return request.getParameter(param) != null && request.getParameter(param).equalsIgnoreCase(value);
    }

    public boolean getParamIs(String param, String value, String valueWhenTrue) {
        if (request.getParameter(param) == null && value.equalsIgnoreCase(valueWhenTrue)) {
            return true;
        }
        return request.getParameter(param) != null && request.getParameter(param).equalsIgnoreCase(value);
    }

}
