package org.eu.spacc.spaccdotweb.android.helpers;

import android.content.Context;
import android.content.res.XmlResourceParser;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;

import org.eu.spacc.spaccdotweb.android.Constants.*;

public class ConfigReader {
    private final Map<String, Object> configData = new HashMap<>();

    public ConfigReader(Context context, int configResource) {
        try {
            parseConfigData(context, configResource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object get(String key) {
        return configData.get(key);
    }

    private void parseConfigData(Context context, int configResource) throws IOException, XmlPullParserException {
        XmlResourceParser parser = context.getResources().getXml(configResource);
        int eventType = parser.getEventType();
        while (eventType != XmlResourceParser.END_DOCUMENT) {
            if (eventType == XmlResourceParser.START_TAG) {
                String type = parser.getName();
                if (!type.equals("config")) {
                    String name = parser.getAttributeValue(null, "name");
                    String value = parser.nextText();
                    configData.put(name, parseValue(type, value));
                }
            }
            eventType = parser.next();
        }
    }

    private Object parseValue(String type, String value) {
        switch (type) {
            case "boolean":
                return Boolean.parseBoolean(value);
            case "string":
                return value;
            default:
                value = value.toUpperCase();
                try {
                    switch (type) {
                        case "AppIndex":
                            return AppIndex.valueOf(value);
                    }
                } catch (IllegalArgumentException ignored) {}
        }
        return null;
    }
}
