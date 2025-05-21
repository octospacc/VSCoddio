package org.eu.spacc.spaccdotweb.android.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.eu.spacc.spaccdotweb.android.Config;
import org.eu.spacc.spaccdotweb.android.Constants;
import org.eu.spacc.spaccdotweb.android.utils.ApiUtils;

public class SpaccWebViewClient extends WebViewClient {
    private final Context context;
    private Config config;

    public SpaccWebViewClient(Context context) {
        super();
        this.context = context;
    }

    public void applyConfig(Config config) {
        this.config = config;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        ((SpaccWebView)view).setLoaded(false);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        ((SpaccWebView)view).setLoaded(true);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // TODO: This should not override all HTTP links if the app loads from remote (which will allow proper internal navigation and file downloads)
        // NOTE: It seems like the WebView overrides loading of data: URIs before we can get it here...
        // List<String> externalProtocols = Arrays.asList("data", "http", "https", "mailto", "ftp");
        String protocol = url.toLowerCase().split(":")[0];
        if (protocol.equals("file") || (config.getAppIndex() == Constants.AppIndex.REMOTE && Arrays.asList("http", "https").contains(protocol))) {
            return super.shouldOverrideUrlLoading(view, url);
        } else if (protocol.equals("intent")) {
            ApiUtils.apiRun(4, () -> {
                try {
                    // TODO: Should this handle broadcasts and services differently?
                    context.startActivity(Intent.parseUri(url, 0));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
            return true;
        } else {
            ApiUtils.openOrShareUrl(context, Uri.parse(url));
            return true;
        }
    }
}
