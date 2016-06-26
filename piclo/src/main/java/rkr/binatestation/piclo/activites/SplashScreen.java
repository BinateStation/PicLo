package rkr.binatestation.piclo.activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

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
        getCategories();
    }

    private void navigate() {
        startActivity(new Intent(getActivity(), HomeActivity.class));
        finish();
    }

    private void getCategories() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + Constants.CATEGORIES, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(tag, "Response payload :- " + response);
                parseResponse(response);
            }

            private void parseResponse(JSONObject response) {
                try {
                    if (response.has("status") && response.optBoolean("status")) {
                        Log.i(tag, response.optString("message"));
                        JSONArray dataArray = response.optJSONArray("data");
                        if (dataArray != null) {
                            Categories categoriesDB = new Categories(getActivity());
                            categoriesDB.open();
                            categoriesDB.insert(new Categories("0", "All"));
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.optJSONObject(i);
                                categoriesDB.insert(new Categories(
                                        dataObject.optString("categoryId"),
                                        dataObject.optString("categoryName")
                                ));
                            }
                            categoriesDB.close();
                        }
                        navigate();
                    } else {
                        Util.showProgressOrError(getSupportFragmentManager(), R.id.ASS_contentLayout, 2);
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
                    Util.showProgressOrError(getSupportFragmentManager(), R.id.ASS_contentLayout, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Log.i(tag, "Request url  :- " + jsonObjectRequest.getUrl());
        VolleySingleTon.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    public AppCompatActivity getActivity() {
        return SplashScreen.this;
    }
}
