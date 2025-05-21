package org.eu.spacc.spaccdotweb.android.webview;

import android.content.Context;
import android.net.Uri;

import org.eu.spacc.spaccdotweb.android.utils.FileUtils;

public class DownloadListener implements android.webkit.DownloadListener {
    private final Context context;

    public DownloadListener(Context context) {
        this.context = context;
    }

    // TODO: Read file name from download="..." HTML <a> attribute when present
    // TODO: Implement file destination path picking (requires Android < 5 with SAF Intent)
    @Override
    public void onDownloadStart(String downloadUrl, String userAgent, String contentDisposition, String mimeType, long contentLength) {
//        String[] nameParts = downloadUrl.split("/");
//        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT)
//                .addCategory(Intent.CATEGORY_OPENABLE)
//                .setType(mimeType)
//                .putExtra(Intent.EXTRA_TITLE, nameParts[nameParts.length - 1]);
//        DownloadDataHolder.getInstance().setData(Uri.parse(downloadUrl), userAgent, contentDisposition, mimeType, contentLength);
//        ((Activity)context).startActivityForResult(intent, Constants.CREATE_FILE_REQUEST_CODE);
        FileUtils.startFileDownload(context, Uri.parse(downloadUrl), userAgent, contentDisposition, mimeType);
    }
}
