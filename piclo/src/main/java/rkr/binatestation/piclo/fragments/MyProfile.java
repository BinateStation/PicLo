package rkr.binatestation.piclo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.network.VolleySingleTon;
import rkr.binatestation.piclo.utils.Constants;
import rkr.binatestation.piclo.utils.Util;

/**
 * Created by RKR on 10-06-2016.
 * MyProfile.
 */
public class MyProfile extends Fragment {
    private static final String tag = MyProfile.class.getName();
    TextInputEditText username, fullNmae, email, phone;

    public MyProfile() {
    }

    public static MyProfile newInstance() {

        Bundle args = new Bundle();

        MyProfile fragment = new MyProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        username = (TextInputEditText) rootView.findViewById(R.id.FP_username);
        fullNmae = (TextInputEditText) rootView.findViewById(R.id.FP_fullName);
        email = (TextInputEditText) rootView.findViewById(R.id.FP_email);
        phone = (TextInputEditText) rootView.findViewById(R.id.FP_phone);

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.FP_edit);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.isSelected()) {
                        view.setSelected(false);
                        fab.setImageResource(R.drawable.ic_create_black_24dp);
                    } else {
                        view.setSelected(true);
                        fab.setImageResource(R.drawable.ic_save_black_24dp);
                    }

                }
            });
        }

        getUserDetails();
        return rootView;
    }

    private void getUserDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + Constants.PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(tag, "Response payload :- " + response);
                try {
                    parseResponse(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void parseResponse(JSONObject response) {
                if (response.has("status") && response.optBoolean("status")) {
                    Log.i(tag, response.optString("message"));
                    JSONObject dataObject = response.optJSONObject("data");
                    if (dataObject != null) {
                        username.setText(dataObject.optString("userName"));
                        fullNmae.setText(dataObject.optString("fullName"));
                        email.setText(dataObject.optString("email"));
                        phone.setText(dataObject.optString("mobile"));
                    }
                } else {
                    Util.alert(getActivity(), "Alert", response.optString("message"), false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag, "Error :- " + error.toString());
                Util.alert(getActivity(), "Network Error", "Please check internet connection.!", false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE)
                        .getString(Constants.KEY_USER_ID, ""));

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
        VolleySingleTon.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void updateUserDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + Constants.UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(tag, "Response payload :- " + response);
                try {
                    parseResponse(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void parseResponse(JSONObject response) {
                if (response.has("status") && response.optBoolean("status")) {
                    Log.i(tag, response.optString("message"));
                    JSONObject dataObject = response.optJSONObject("data");
                    if (dataObject != null) {
                        username.setText(dataObject.optString("userName"));
                        fullNmae.setText(dataObject.optString("fullName"));
                        email.setText(dataObject.optString("email"));
                        phone.setText(dataObject.optString("mobile"));
                    }
                } else {
                    Util.alert(getActivity(), "Alert", response.optString("message"), false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag, "Error :- " + error.toString());
                Util.alert(getActivity(), "Network Error", "Please check internet connection.!", false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE)
                        .getString(Constants.KEY_USER_ID, ""));

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
        VolleySingleTon.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

}
