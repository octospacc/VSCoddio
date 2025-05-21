package org.eu.spacc.spaccdotweb.android;

import org.eu.spacc.spaccdotweb.android.Constants.*;
import org.eu.spacc.spaccdotweb.android.helpers.ConfigReader;

public class Config extends Defaults {
    private ConfigReader configReader;

    public Config() {}

    public Config(ConfigReader configReader) {
        this.configReader = configReader;
    }

    public Boolean getAllowJavascript() {
        Boolean value = getBoolean("allow_javascript");
        return (value != null ? value : Defaults.ALLOW_JAVASCRIPT);
    }

    public Boolean getAllowStorage() {
        Boolean value = getBoolean("allow_storage");
        return (value != null ? value : Defaults.ALLOW_STORAGE);
    }

    public Boolean getAllowZoomControls() {
        Boolean value = getBoolean("allow_zoom_controls");
        return (value != null ? value : Defaults.ALLOW_ZOOM_CONTROLS);
    }

    public Boolean getDisplayZoomControls() {
        Boolean value = getBoolean("display_zoom_controls");
        return (value != null ? value : Defaults.DISPLAY_ZOOM_CONTROLS);
    }

    public AppIndex getAppIndex() {
        AppIndex value = (AppIndex)get("app_index");
        return (value != null ? value : Defaults.APP_INDEX);
    }

    public String getLocalIndex() {
        String value = getString("local_index");
        return (value != null ? value : Defaults.LOCAL_INDEX);
    }

    public String getRemoteIndex() {
        return getString("remote_index");
    }

    public String getStandardFontFamily() {
        return getString("standard_font_family");
    }

    public String getUserAgent() {
        return getString("user_agent");
    }

    private Object get(String key) {
        return (configReader != null
                ? configReader.get(key)
                : null);
    }

    private Boolean getBoolean(String key) {
        return (Boolean)get(key);
    }

    private String getString(String key) {
        return (String)get(key);
    }
}
