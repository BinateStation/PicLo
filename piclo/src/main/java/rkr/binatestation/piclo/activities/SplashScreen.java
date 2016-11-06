package rkr.binatestation.piclo.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.database.PicloContract;
import rkr.binatestation.piclo.network.VolleySingleTon;
import rkr.binatestation.piclo.utils.Constants;
import rkr.binatestation.piclo.utils.Util;

public class SplashScreen extends AppCompatActivity {

    private static final String tag = SplashScreen.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        if (isStoragePermissionGranted()) {
            getCategories();
        }
    }

    private void navigate() {
        if (isStoragePermissionGranted()) {
            startActivity(new Intent(getContext(), HomeActivity.class));
            finish();
        }
    }

    private void getCategories() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + Constants.CATEGORIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(tag, "Response payload :- " + response);
                try {
                    parseResponse(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                    navigate();
                }
            }

            private void parseResponse(JSONObject response) {
                try {
                    if (response.has(Constants.KEY_JSON_STATUS) && response.optBoolean(Constants.KEY_JSON_STATUS)) {
                        Log.i(tag, response.optString(Constants.KEY_JSON_MESSAGE));
                        JSONArray dataArray = response.optJSONArray(Constants.KEY_JSON_DATA);
                        if (dataArray != null && dataArray.length() > 0) {
                            saveCategoriesInToDB(dataArray);
                        } else {
                            navigate();
                        }
                    } else {
                        navigate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    navigate();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag, "Error :- " + error.toString());
                navigate();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String date = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE)
                        .getString(Constants.KEY_CATEGORY_LAST_UPDATED_DATE, "");
                try {
                    Date savedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
                    Date changedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2016-11-06");
                    if (changedDate.after(savedDate)) {
                        date = "";
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(date)) {
                    params.put("date", date);
                }

                Log.d(tag, getUrl() + " : Request payload :- " + params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        Log.i(tag, "Request url  :- " + stringRequest.getUrl());
        VolleySingleTon.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void saveCategoriesInToDB(final JSONArray dataArray) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                getContentResolver().delete(PicloContract.CategoriesEntry.CONTENT_URI, null, null);
                getContentResolver().bulkInsert(PicloContract.CategoriesEntry.CONTENT_URI, getContentValues(dataArray));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                getSharedPreferences(getPackageName(), MODE_PRIVATE).edit()
                        .putString(Constants.KEY_CATEGORY_LAST_UPDATED_DATE,
                                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())).apply();
                navigate();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private ContentValues[] getContentValues(JSONArray dataArray) {
        if (dataArray != null) {
            ContentValues[] contentValues = new ContentValues[(dataArray.length() + 1)];
            ContentValues allValue = new ContentValues();
            allValue.put(PicloContract.CategoriesEntry.COLUMN_CATEGORY_ID, 0);
            allValue.put(PicloContract.CategoriesEntry.COLUMN_CATEGORY_NAME, "All");
            contentValues[0] = allValue;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.optJSONObject(i);
                if (dataObject != null) {
                    ContentValues values = new ContentValues();
                    values.put(PicloContract.CategoriesEntry.COLUMN_CATEGORY_ID, dataObject.optInt(Constants.KEY_JSON_CATEGORY_ID));
                    values.put(PicloContract.CategoriesEntry.COLUMN_CATEGORY_NAME, dataObject.optString(Constants.KEY_JSON_CATEGORY_NAME));
                    contentValues[(i + 1)] = values;
                }
            }
            return contentValues;
        }
        return new ContentValues[0];
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(tag, "Permission is granted");
                return true;
            } else {

                Log.v(tag, "Permission is revoked");
                ActivityCompat.requestPermissions(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(tag, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(tag, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            getCategories();
        } else {
            Util.showAlert(getContext(), "Alert", "Permission for storage and phone state is just for saving your piclo, please allow it.", true);
        }
    }

    public SplashScreen getContext() {
        return SplashScreen.this;
    }
}
