package org.eu.spacc.spaccdotweb.android.webview;

import android.net.Uri;

public class DownloadDataHolder {
    private static DownloadDataHolder instance;
    private Uri downloadUrl;
    private String userAgent;
    private String contentDisposition;
    private String mimeType;

    public static synchronized DownloadDataHolder getInstance() {
        if (instance == null) {
            instance = new DownloadDataHolder();
        }
        return instance;
    }

    public void setData(Uri downloadUrl, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        this.downloadUrl = downloadUrl;
        this.userAgent = userAgent;
        this.contentDisposition = contentDisposition;
        this.mimeType = mimeType;
    }

    public void clearData() {
        instance = new DownloadDataHolder();
    }

    public Uri getDownloadUrl() {
        return downloadUrl;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }

    public String getMimeType() {
        return mimeType;
    }
}

