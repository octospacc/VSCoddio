package org.eu.spacc.spaccdotweb.android.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.io.IOException;

import org.eu.spacc.spaccdotweb.android.Constants;
import org.eu.spacc.spaccdotweb.android.utils.StorageUtils;
import org.eu.spacc.spaccdotweb.android.utils.FileUtils;

public class DataMoveHelper {

    public static void run(Context context, int labelExit, int dialogTitle, int dialogMessage) {
        Activity activity = (Activity)context;
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(context);
        Constants.DataLocation dataLocationReal = (StorageUtils.isInstalledOnExternalStorage(context) ? Constants.DataLocation.EXTERNAL : Constants.DataLocation.INTERNAL);
        Integer dataLocationSaved = sharedPrefHelper.getInt("data_location");
        if (dataLocationSaved == null) {
            sharedPrefHelper.setInt("data_location", dataLocationReal.ordinal());
        } else if (!dataLocationSaved.equals(dataLocationReal.ordinal())) {
            new AlertDialog.Builder(context)
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setNegativeButton(labelExit, (dialogInterface, i) -> ((Activity)context).finish())
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    // TODO: Check that the storage locations are all present to copy, and implement an error dialog
                    try {
                        FileUtils.moveDirectory(StorageUtils.dataDirFromEnum(context, Constants.DataLocation.values()[dataLocationSaved]), StorageUtils.dataDirFromEnum(context, dataLocationReal), false);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    sharedPrefHelper.setInt("data_location", dataLocationReal.ordinal());
                    restartActivity(context);
                })
            .show();
        }
    }

    private static void restartActivity(Context context) {
        Activity activity = (Activity)context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activity.recreate();
        } else {
            Intent intent = activity.getIntent();
            activity.finish();
            context.startActivity(intent);
        }
    }
}
