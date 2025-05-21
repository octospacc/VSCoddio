package org.eu.spacc.spaccdotweb.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.ValueCallback;
import java.io.File;

import org.eu.spacc.spaccdotweb.android.webview.SpaccWebChromeClient;
import org.eu.spacc.spaccdotweb.android.webview.SpaccWebView;

public class SpaccWebViewActivity extends Activity {
    protected SpaccWebView webView;
    public ValueCallback<Uri> fileUploadCallback;
    public ValueCallback<Uri[]> filesUploadCallback;

    @TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constants.CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            Uri fileUri = data.getData();
//            if (fileUri != null) {
//                enqueueDownload(Uri.parse(fileUri.toString()));
//            }
//        }
        if (requestCode == Constants.ActivityCodes.UPLOAD_FILE.ordinal()) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && filesUploadCallback != null) {
                    filesUploadCallback.onReceiveValue(SpaccWebChromeClient.FileChooserParams.parseResult(resultCode, data));
                } else if (fileUploadCallback != null) {
                    fileUploadCallback.onReceiveValue(data.getData());
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && filesUploadCallback != null) {
                    filesUploadCallback.onReceiveValue(null);
                } else if (fileUploadCallback != null) {
                    fileUploadCallback.onReceiveValue(null);
                }
            }
            fileUploadCallback = null;
            filesUploadCallback = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (this.webView.canGoBack()) {
            this.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

//    // TODO: Find some way to download to any storage location with DownloadManager, since it doesn't take content:// URIs
//    private void enqueueDownload(Uri fileUri) {
//        DownloadDataHolder downloadDataHolder = DownloadDataHolder.getInstance();
//        FileUtils.startFileDownload(this,
//                downloadDataHolder.getDownloadUrl(),
//                downloadDataHolder.getContentDisposition(),
//                downloadDataHolder.getUserAgent(),
//                downloadDataHolder.getMimeType());
//    }

    @SuppressLint("NewApi") // We have our custom implementation
    @Override
    public File getDataDir() {
        return getApplicationContext().getDataDir();
    }

    @Override
    public File getDir(String name, int mode) {
        return getApplicationContext().getDir(name, mode);
    }

    @Override
    public File getFilesDir() {
        return getApplicationContext().getFilesDir();
    }

    @Override
    public File getCacheDir() {
        return getApplicationContext().getCacheDir();
    }

    @Override
    public File getDatabasePath(String name) {
        return getApplicationContext().getDatabasePath(name);
    }
}
