package com.hotel.util;

public class Html {

    public static String escape(Object value) {
        if (value == null) {
            return "";
        }

        String text = value.toString();
        text = text.replace("&", "&amp;");
        text = text.replace("<", "&lt;");
        text = text.replace(">", "&gt;");
        text = text.replace("\"", "&quot;");
        text = text.replace("'", "&#39;");
        return text;
    }
}
