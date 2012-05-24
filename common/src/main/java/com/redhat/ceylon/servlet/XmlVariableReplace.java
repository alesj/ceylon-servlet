package com.redhat.ceylon.servlet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class XmlVariableReplace {

    //Workaround as AS 7.1.0 does not replace all variables from xml
    public static String replaceVar(String string) {
        Matcher m = Pattern.compile("\\$\\{(.*?)\\}").matcher(string);
        while (m.find()) {
            String var = m.group(1);
            String val;
            if (var.startsWith("env.")) {
                val = System.getenv(var.replaceFirst("env\\.", ""));
            } else {
                val = System.getProperty(var);
            }

            if (val != null) {
                string = string.replace(m.group(0), val);
            }
        }
        return string;
    }

}
