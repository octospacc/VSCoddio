package org.eu.spacc.spaccdotweb.android.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import java.io.File;
import org.eu.spacc.spaccdotweb.android.Constants.*;

public class StorageUtils {

    public static boolean isInstalledOnExternalStorage(Context context) {
        try {
            int flags = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.flags;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                // https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/content/pm/ApplicationInfo.java#2516
                return ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0);
            }
        } catch (PackageManager.NameNotFoundException ignored) {}
        return false;
    }

    public static File getProtectedDataDir(Context context) {
        // Usually is /data/data/<package>
        return new File(Environment.getDataDirectory() + File.separator + "data" + File.separator + context.getPackageName());
    }

    public static File getInternalDataDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            // Usually is /sdcard/Android/data/<package>
            return getParentDir(context.getExternalFilesDir(null));
        } else {
            // TODO: This can actually be external storage on old Androids, we should make it return null in those cases
            return new File(Environment.getExternalStorageDirectory() + "Android" + File.separator + "data" + File.separator + context.getPackageName());
        }
    }

    public static File getExternalDataDir(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            File[] dirs = context.getExternalFilesDirs(null);
            if (dirs.length >= 2) {
                return getParentDir(dirs[1]);
            } else {
                return null;
            }
        } else {
            // TODO: We need hacks for old Android systems which have emulated internal + real external storages
            return getInternalDataDir(context);
        }
    }

    // TODO: This should not suggest to use external storage if we don't have the necessary manifest permission
    public static File getDataDir(Context context) {
        File dir = null;
        if (isInstalledOnExternalStorage(context)) {
            dir = getExternalDataDir(context);
        }
        if (dir == null) {
            dir = getProtectedDataDir(context);
        }
        return dir;
    }

    public static File dataDirFromEnum(Context context, DataLocation dataLocation) {
        switch (dataLocation) {
            case INTERNAL:
                return getProtectedDataDir(context);
            case EXTERNAL:
                return getExternalDataDir(context);
        }
        return null;
    }

    private static File getParentDir(File path) {
        return (path != null ? path.getParentFile() : null);
    }
}
