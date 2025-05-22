package org.eu.octt.vscoddio;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.eu.spacc.spaccdotweb.android.helpers.SharedPrefHelper;
import org.eu.spacc.spaccdotweb.android.utils.ApiUtils;
import org.eu.spacc.spaccdotweb.android.helpers.DataMoveHelper;
import org.eu.spacc.spaccdotweb.android.SpaccWebViewActivity;
import org.eu.spacc.spaccdotweb.android.webview.SpaccWebViewClient;

public class MainActivity extends SpaccWebViewActivity {
    private ActionBar actionBar = null;
    private Menu menu = null;
    SharedPrefHelper sharedPref;

    private ArrayList<String> getPagesList(boolean listFallback) {
        ArrayList<String> pages = sharedPref.getStringList("pages");
        if (pages == null) {
            pages = new ArrayList<String>();
        }
        if (pages.isEmpty() && listFallback) {
            pages = new ArrayList<String>(Arrays.asList("https://vscode.dev", "https://microsoft.github.io/monaco-editor/playground.html", "http://localhost:8080"));
        }
        return pages;
    }

    private void resetWebView() {
        webView.stopLoading();
        webView.loadUrl("about:blank");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupPagesMenu(boolean listFallback) {
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            ArrayList<String> pages = getPagesList(listFallback);
            if (pages.isEmpty()) {
                resetWebView();
            }
            if (menu != null) {
                menu.findItem(R.id.remove).setEnabled(!pages.isEmpty());
            }
            actionBar.setListNavigationCallbacks(new ArrayAdapter<>(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ? actionBar.getThemedContext() : getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, pages), (pos, id) -> {
                if (pos != 0) {
                    Collections.swap(pages, 0, pos);
                    sharedPref.setStringList("pages", pages);
                    setupPagesMenu(listFallback);
                } else {
                    resetWebView();
                    webView.loadUrl(pages.get(0));
                }
                return true;
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiUtils.apiRun(11, () -> this.actionBar = getActionBar());
        sharedPref = new SharedPrefHelper(getApplicationContext(), "VSCoddio");
        setupPagesMenu(true);

        DataMoveHelper.run(this, R.string.exit, R.string.move_app_data, R.string.move_app_data_info);

        this.webView = findViewById(R.id.webview);
        this.webView.setStrings(R.string.open, R.string.open_externally, R.string.copy_url);
        this.webView.setWebViewClient(new SpaccWebViewClient(this) {
            @SuppressLint("UseRequiresApi")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (menu != null) {
                    menu.findItem(R.id.stop).setVisible(true);
                    menu.findItem(R.id.reload).setVisible(false);
                }
            }

            @SuppressLint("UseRequiresApi")
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (menu != null) {
                    menu.findItem(R.id.stop).setVisible(false);
                    menu.findItem(R.id.reload).setVisible(true);
                }
            }
        });
        this.webView.loadConfig(this, R.xml.app_config);
        this.webView.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (actionBar == null) {
            menu.findItem(R.id.hide).setVisible(false);
        }
        return super.onCreateOptionsMenu(this.menu = menu);
    }

    @SuppressLint({"NonConstantResourceId", "UseRequiresApi"})
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<String> pages = getPagesList(true);
        switch (item.getItemId()) {
            case R.id.add:
                EditText urlText = new EditText(this);
                urlText.setSingleLine(true);
                urlText.setHint("URL");
                new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.add))
                    .setView(urlText)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        String url = urlText.getText().toString();
                        if (!url.isEmpty()) {
                            pages.add(0, url);
                            sharedPref.setStringList("pages", pages);
                            setupPagesMenu(false);
                        }
                    })
                    .setNeutralButton(R.string.cancel, null)
                    .show();
                break;
            case R.id.remove:
                new AlertDialog.Builder(this)
                    .setTitle(R.string.remove)
                    .setMessage(getResources().getString(R.string.remove) + " <" + pages.get(0) + ">?")
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        pages.remove(0);
                        sharedPref.setStringList("pages", pages);
                        setupPagesMenu(false);
                    })
                    .setNegativeButton(R.string.no, (dialogInterface, i) -> {})
                    .show();
                break;
            case R.id.stop:
                this.webView.stopLoading();
                break;
            case R.id.reload:
                this.webView.reload();
                break;
            case R.id.about_app:
                ApiUtils.openOrShareUrl(this, Uri.parse("https://gitlab.com/octospacc/VSCoddio"));
                break;
            case R.id.exec_script:
                EditText scriptText = new EditText(this);
                new AlertDialog.Builder(this)
                    .setTitle(R.string.execute_javascript)
                    .setView(scriptText)
                    .setPositiveButton("OK", (dialogInterface, i) -> webView.injectScript(scriptText.getText().toString()))
                    .setNeutralButton(R.string.cancel, null)
                    .show();
                break;
            case R.id.hide:
                actionBar.hide();
                break;
            case R.id.exit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("UseRequiresApi")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBackPressed() {
        if (actionBar == null) {
            super.onBackPressed();
        } else if (actionBar.isShowing()) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }
}
