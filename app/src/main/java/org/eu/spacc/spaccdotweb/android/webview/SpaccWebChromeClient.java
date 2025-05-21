package org.eu.spacc.spaccdotweb.android.webview;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.eu.spacc.spaccdotweb.android.Constants;
import org.eu.spacc.spaccdotweb.android.SpaccWebViewActivity;

public class SpaccWebChromeClient extends WebChromeClient {
    private final SpaccWebViewActivity activity;

    public SpaccWebChromeClient(SpaccWebViewActivity activity) {
        super();
        this.activity = activity;
    }

    // TODO: Android < 4 support

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
        activity.filesUploadCallback = valueCallback;
        activity.startActivityForResult(fileChooserParams.createIntent(), Constants.ActivityCodes.UPLOAD_FILE.ordinal());
        return true;
    }

    //@Override // Android 4.1+
    protected void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        activity.fileUploadCallback = valueCallback;
        activity.startActivityForResult(Intent.createChooser(intent, null), Constants.ActivityCodes.UPLOAD_FILE.ordinal());
    }
}
