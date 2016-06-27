package rkr.binatestation.piclo.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
 * A simple {@link Fragment} subclass.
 */
public class ChangePassword extends Fragment {

    private static final String tag = MyProfile.class.getName();
    TextInputEditText newPassword, confirmPassword;
    FloatingActionButton send;

    public ChangePassword() {
        // Required empty public constructor
    }

    public static ChangePassword newInstance() {

        return new ChangePassword();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        newPassword = (TextInputEditText) view.findViewById(R.id.FCP_newPassword);
        confirmPassword = (TextInputEditText) view.findViewById(R.id.FCP_confirmPassword);
        send = (FloatingActionButton) view.findViewById(R.id.FCP_send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });

        return view;
    }

    private void validateInput() {
        if (TextUtils.isEmpty(newPassword.getText().toString())) {
            newPassword.setError("Please enter your new password..!");
            newPassword.requestFocus();
        } else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            confirmPassword.setError("Password miss match..!");
            confirmPassword.requestFocus();
        } else {
            changePassword();
        }
    }

    private void changePassword() {
        Util.showProgressOrError(getFragmentManager(), R.id.FP_contentLayout, 1, "CHANGE_PASSWORD_ACTIVITY_PROGRESS");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + Constants.CHANGE_PASSWORD, new Response.Listener<String>() {
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
                try {
                    Util.hideProgressOrError(getFragmentManager(), "CHANGE_PASSWORD_ACTIVITY_PROGRESS");
                    if (response.has("status") && response.optBoolean("status")) {
                        Log.i(tag, response.optString("message"));
                        Util.alert(getActivity(), "Alert", response.optString("message"), true);
                    } else {
                        Util.showProgressOrError(getFragmentManager(), R.id.FCP_contentLayout, 2, "CHANGE_PASSWORD_ACTIVITY_ERROR");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Util.hideProgressOrError(getFragmentManager(), "CHANGE_PASSWORD_ACTIVITY_PROGRESS");
                    Log.e(tag, "Error :- " + error.toString());
                    Util.showProgressOrError(getFragmentManager(), R.id.FCP_contentLayout, 2, "CHANGE_PASSWORD_ACTIVITY_ERROR");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE)
                        .getString(Constants.KEY_USER_ID, ""));
                params.put("password", newPassword.getText().toString());

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
