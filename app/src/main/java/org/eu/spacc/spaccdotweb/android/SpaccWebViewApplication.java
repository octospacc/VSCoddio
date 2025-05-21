package org.eu.spacc.spaccdotweb.android;

import android.app.Application;
import java.io.File;

import org.eu.spacc.spaccdotweb.android.utils.StorageUtils;

public class SpaccWebViewApplication extends Application {

    @Override
    public File getDataDir() {
        return StorageUtils.getDataDir(this);
    }

    @Override
    public File getDir(String name, int mode) {
        File dir = new File(getDataDir(), name);
        dir.mkdirs();
        return dir;
    }

    @Override
    public File getFilesDir() {
        return getDir("files", 0);
    }

    @Override
    public File getCacheDir() {
        return getDir("cache", 0);
    }

    @Override
    public File getDatabasePath(String name) {
        // TODO: should this be "app_databases"?
        return new File(getDir("databases", 0), name);
    }
}
