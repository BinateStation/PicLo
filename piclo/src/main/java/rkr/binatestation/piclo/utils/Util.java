package rkr.binatestation.piclo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by RKR on 27-01-2016.
 * Util.
 */
public class Util {

    /**
     * static variable for saving images in external storage directory.
     */

    private static final String captureImagePath = Environment.getExternalStorageDirectory().toString() +
            File.separator + "PickLo" + File.separator + "Images" + File.separator;

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
     * static method used to get the specific typeface of the app
     */

    public static Typeface getTypeFace(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "proximanova-bold-webfont.ttf");
    }

    /**
     * static method used to get the formatted date
     */

    public static String getFormattedDate(long timeInMillis) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timeInMillis);
        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mmaa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        if (now.get(Calendar.DATE) == date.get(Calendar.DATE)) {
            return "Today at " + DateFormat.format(timeFormatString, date);
        } else if (now.get(Calendar.DATE) - date.get(Calendar.DATE) == 1) {
            return "Yesterday at " + DateFormat.format(timeFormatString, date);
        } else if (date.get(Calendar.DATE) - now.get(Calendar.DATE) == 1) {
            return "Tomorrow at " + DateFormat.format(timeFormatString, date);
        } else if (now.get(Calendar.WEEK_OF_MONTH) == date.get(Calendar.WEEK_OF_MONTH)) {
            return "on " + DateFormat.format("EEEE, h:mm aa", date).toString();
        } else if (now.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
            return "on " + DateFormat.format(dateTimeFormatString, date).toString();
        } else
            return "Placed on " + DateFormat.format("MMMM dd yyyy, h:mm aa", date).toString();
    }

    /**
     * static method used to get the postfix for the day
     */

    public static String getDatePostFix(Date date) {
        String postFixDate;
        postFixDate = new SimpleDateFormat("dd", Locale.getDefault()).format(date);
        if (postFixDate.endsWith("1")) {
            postFixDate += "st";
        } else if (postFixDate.endsWith("2")) {
            postFixDate += "nd";
        } else if (postFixDate.endsWith("3")) {
            postFixDate += "rd";
        } else {
            postFixDate += "th";
        }
        return postFixDate;
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
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
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

}
