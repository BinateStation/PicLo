package rkr.binatestation.piclo.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.models.Categories;
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
        getCategories();
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
                    if (response.has("status") && response.optBoolean("status")) {
                        Log.i(tag, response.optString("message"));
                        JSONArray dataArray = response.optJSONArray("data");
                        if (dataArray != null) {
                            Categories categoriesDB = new Categories(getContext());
                            categoriesDB.open();
                            categoriesDB.deleteAll();
                            categoriesDB.insert(new Categories("0", "All"));
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.optJSONObject(i);
                                categoriesDB.insert(new Categories(
                                        dataObject.optString("categoryId"),
                                        dataObject.optString("categoryName")
                                ));
                            }
                            categoriesDB.close();
                            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString(Constants.KEY_CATEGORY_LAST_UPDATED_DATE,
                                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())).apply();
                        }
                        navigate();
                    } else {
                        navigate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e(tag, "Error :- " + error.toString());
                    Util.showProgressOrError(getSupportFragmentManager(), R.id.ASS_contentLayout, 2, "SPLASH_SCREEN_ACTIVITY_ERROR");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("date", getSharedPreferences(getPackageName(), Context.MODE_PRIVATE)
                        .getString(Constants.KEY_CATEGORY_LAST_UPDATED_DATE, ""));

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

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(tag, "Permission is granted");
                return true;
            } else {

                Log.v(tag, "Permission is revoked");
                ActivityCompat.requestPermissions(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
            navigate();
        }
    }

    public SplashScreen getContext() {
        return SplashScreen.this;
    }
}
