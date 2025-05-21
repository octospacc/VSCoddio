package org.eu.spacc.spaccdotweb.android.webview;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.ArrayList;
import java.util.Arrays;

import org.eu.spacc.spaccdotweb.android.Config;
import org.eu.spacc.spaccdotweb.android.SpaccWebViewActivity;
import org.eu.spacc.spaccdotweb.android.helpers.ConfigReader;
import org.eu.spacc.spaccdotweb.android.utils.ApiUtils;

public class SpaccWebView extends WebView {
    private Config config;
    private Context context;
    private SpaccWebViewClient webViewClient;

    private int openString;
    private int openExternallyString;
    private int copyUrlString;

    private Boolean isLoaded = false;
    protected ArrayList<String> scriptQueue = new ArrayList<>();

    public SpaccWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.webViewClient = new SpaccWebViewClient(context);
        this.setWebViewClient(webViewClient);
        this.setWebChromeClient(new SpaccWebChromeClient((SpaccWebViewActivity)context));
        this.setDownloadListener(new DownloadListener(context));
        this.config = new Config();
        this.applyConfig(context);
    }

    public void setStrings(int open, int openExternally, int copyUrl) {
        openString = open;
        openExternallyString = openExternally;
        copyUrlString = copyUrl;
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
        webViewClient = (SpaccWebViewClient)client;
        webViewClient.applyConfig(config);
    }

    // TODO: Implement context menu (long-press on links, images, etc...)
    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
        HitTestResult result = getHitTestResult();
        /*switch (result.getType()) {
            case HitTestResult.UNKNOWN_TYPE:
            case HitTestResult.IMAGE_TYPE:
            case HitTestResult.SRC_ANCHOR_TYPE:
            case HitTestResult.SRC_IMAGE_ANCHOR_TYPE:*/
                String href = result.getExtra();
                if (href != null) {
                    menu.setHeaderTitle(href);
                    menu.add(openString).setOnMenuItemClickListener(menuItem -> {
                        if (!webViewClient.shouldOverrideUrlLoading(this, href)) {
                            this.loadUrl(href);
                        }
                        return false;
                    });
                    if (!ApiUtils.isInternalUrl(Uri.parse(href))) {
                        menu.add(openExternallyString).setOnMenuItemClickListener(menuItem -> {
                            ApiUtils.openOrShareUrl(context, Uri.parse(href));
                            return false;
                        });
                    }
                    menu.add(copyUrlString).setOnMenuItemClickListener(menuItem -> {
                        ApiUtils.writeToClipboard(context, href);
                        return false;
                    });
                }
                /*break;
        }*/
    }

    public void injectScript(String script) {
        if (isLoaded) {
            this.evaluateJavascript(script, null);
        } else {
            scriptQueue.add(script);
        }
    }

    public void injectStyle(String style) {
        injectScript("document.head.appendChild(Object.assign(document.createElement('style'),{innerHTML:\"" + style + "\"}))");
    }

    protected void setLoaded(Boolean loaded) {
        if (isLoaded = loaded) {
            scriptQueue.forEach(this::injectScript);
            scriptQueue.clear();
        }
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection ic = super.onCreateInputConnection(outAttrs);

        // Disable keyboard autocorrect & buffering
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            outAttrs.inputType &= ~EditorInfo.TYPE_MASK_VARIATION;
            ApiUtils.apiRun(5, () -> outAttrs.inputType |= InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            ApiUtils.apiRun(11, () -> outAttrs.inputType |= InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
            ApiUtils.apiRun(11, () -> outAttrs.imeOptions |= EditorInfo.IME_FLAG_NO_FULLSCREEN);
            ApiUtils.apiRun(26, () -> outAttrs.imeOptions |= EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING);
            outAttrs.imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        }

        // Make Shift+ArrowKeys work
        if (ic != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            return new InputConnectionWrapper(ic, true) {
                private boolean shiftPressed = false;

                @Override
                public boolean sendKeyEvent(KeyEvent event) {
                    int code = event.getKeyCode();
                    int action = event.getAction();

                    if (code == KeyEvent.KEYCODE_SHIFT_LEFT || code == KeyEvent.KEYCODE_SHIFT_RIGHT) {
                        shiftPressed = (action == KeyEvent.ACTION_DOWN);
                        return super.sendKeyEvent(event);
                    }

                    if (Arrays.asList(KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN).contains(code) && action == KeyEvent.ACTION_DOWN && shiftPressed) {
                        return super.sendKeyEvent(new KeyEvent(
                            event.getDownTime(),
                            event.getEventTime(),
                            action,
                            code,
                            event.getRepeatCount(),
                            event.getMetaState() | KeyEvent.META_SHIFT_ON,
                            event.getDeviceId(),
                            event.getScanCode(),
                            event.getFlags() | KeyEvent.FLAG_SOFT_KEYBOARD));
                    }

                    return super.sendKeyEvent(event);
                }
            };
        } else {
            return ic;
        }
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true; // Should tell the system to prefer opening the keyboard
    }

    private void applyConfig(Context context) {
        WebSettings webSettings = this.getSettings();

        webSettings.setJavaScriptEnabled(config.getAllowJavascript());

        boolean allowStorage = config.getAllowStorage();
        ApiUtils.apiRun(7, () -> webSettings.setDomStorageEnabled(allowStorage));
        ApiUtils.apiRun(5, () -> webSettings.setDatabaseEnabled(allowStorage));
        if (allowStorage) {
            ApiUtils.apiRun(5, () -> webSettings.setDatabasePath(context.getDir("databases", 0).getAbsolutePath()));
        }

        ApiUtils.apiRun(3, () -> webSettings.setAllowFileAccess(false));

        webSettings.setStandardFontFamily(config.getStandardFontFamily());
        ApiUtils.apiRun(3, () -> webSettings.setUserAgentString(config.getUserAgent()));

        ApiUtils.apiRun(3, () -> webSettings.setBuiltInZoomControls(config.getAllowZoomControls()));
        ApiUtils.apiRun(11, () -> webSettings.setDisplayZoomControls(config.getDisplayZoomControls()));

        webViewClient.applyConfig(config);
    }

    public void loadConfig(Context context, int configResource) {
        this.config = new Config(new ConfigReader(context, configResource));
        this.applyConfig(context);
    }

    public void loadAppIndex() {
        String url = null;
        switch (config.getAppIndex()) {
            case LOCAL:
                url = ("file:///android_asset/" + config.getLocalIndex());
                break; 
            case REMOTE:
                url = config.getRemoteIndex();
                break;
        }
        this.loadUrl(url);
    }
}
