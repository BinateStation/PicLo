package rkr.binatestation.piclo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import java.io.File;

import rkr.binatestation.piclo.fragments.ProgressError;

/**
 * Created by RKR on 27-01-2016.
 * Util.
 */
public class Util {

    /**
     * static variable for saving images in external storage directory.
     */

    private static final String captureImagePath = Environment.getExternalStorageDirectory().toString() +
            File.separator + "PicLo" + File.separator + "Images" + File.separator;

    /**
     * Method to check Weather a valid Email ID
     */

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * static method used to get the External Storage path which ensures whether it exits or not
     */

    public static String getCaptureImagePath() {
        File file = new File(captureImagePath);
        if (file.exists()) {
            return captureImagePath;
        } else {
            if (file.mkdirs()) {
                return captureImagePath;
            } else {
                return Environment.getExternalStorageDirectory().toString() + File.separator;
            }
        }
    }

    /**
     * static method used to get the path from uri
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * static method used to get the path from uri
     */
    public static Long getOriginIdFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media._ID};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            cursor.moveToFirst();
            return cursor.getLong(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static void alert(final Activity context, String title, String message, final Boolean isBack) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (isBack) {
                                context.onBackPressed();
                            }
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the progress UI and hides the content form.
     */
    public static void showProgressOrError(FragmentManager fragmentManager, int contentFormId, int type, String tag) {
        try {
            fragmentManager.beginTransaction()
                    .add(contentFormId, ProgressError.newInstance(type), tag)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the progress UI and hides the content form.
     */
    public static void hideProgressOrError(FragmentManager fragmentManager, String tag) {
        try {
            fragmentManager.beginTransaction()
                    .remove(fragmentManager.findFragmentByTag(tag))
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(final Activity activity, String title, String message, final Boolean goBack) {
        try {
            new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (goBack) {
                                activity.onBackPressed();
                            }
                        }
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
