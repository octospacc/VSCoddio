package org.eu.spacc.spaccdotweb.android.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public class ApiUtils {

    public static void apiRun(int apiLevel, Runnable action) {
        if (Build.VERSION.SDK_INT >= apiLevel) {
            action.run();
        }
    }

    public static Boolean isInternalUrl(Uri url) {
        return url.toString().startsWith("file:///android_asset/");
    }

    public static void openOrShareUrl(Context context, Uri url) {
        if (!isInternalUrl(url)) {
            try { // Open the URL externally
                context.startActivity(new Intent(Intent.ACTION_VIEW, url));
                return;
            } catch (ActivityNotFoundException ignored) {}
        }
        // No app can handle it, so share it instead
        context.startActivity(new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, url.toString()));
    }

    public static void writeToClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(null, text);
            clipboard.setPrimaryClip(clip);
        }
    }
}
