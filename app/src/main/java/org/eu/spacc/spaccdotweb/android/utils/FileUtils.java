package org.eu.spacc.spaccdotweb.android.utils;

import static android.content.Context.DOWNLOAD_SERVICE;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.webkit.CookieManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static void moveDirectory(File sourceLocation, File targetLocation, boolean deleteRoot) throws IOException {
        copyDirectory(sourceLocation, targetLocation);
        recursiveDelete(sourceLocation, deleteRoot);
    }

    /* https://subversivebytes.wordpress.com/2012/11/05/java-copy-directory-recursive-delete/ */

    public static void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
            }
        }
        else {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            try {
                byte[] buf = new byte[1024];
                int len;
                while((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                in.close();
                out.close();
            }
        }
    }

    public void recursiveDelete(File rootDir) {
        recursiveDelete(rootDir, true);
    }

    public static void recursiveDelete(File rootDir, boolean deleteRoot) {
        File[] childDirs = rootDir.listFiles();
        for (File childDir : childDirs) {
            if (childDir.isFile()) {
                childDir.delete();
            } else {
                recursiveDelete(childDir, deleteRoot);
                childDir.delete();
            }
        }
        if (deleteRoot) {
            rootDir.delete();
        }
    }

    // TODO: Handle downloads internally on old Android versions
    public static void startFileDownload(Context context, Uri downloadUrl, String userAgent, String contentDisposition, String mimeType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !downloadUrl.toString().toLowerCase().startsWith("data:")) {
            // TODO: We should handle downloading data: URIs manually
            DownloadManager.Request request = new DownloadManager.Request(downloadUrl)
                    //.setDestinationUri(fileUri)
                    .setMimeType(mimeType)
                    .addRequestHeader("User-Agent", userAgent)
                    .addRequestHeader("Content-Disposition", contentDisposition)
                    .addRequestHeader("Cookie", CookieManager.getInstance().getCookie(downloadUrl.toString()));
            ApiUtils.apiRun(11, () -> request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED));
            ((DownloadManager)context.getSystemService(DOWNLOAD_SERVICE)).enqueue(request);
        } else {
            ApiUtils.openOrShareUrl(context, downloadUrl);
        }
    }
}
