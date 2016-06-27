package rkr.binatestation.piclo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    TextInputEditText username, fullName, email, phone;
    FloatingActionButton editSave;

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

        setHasOptionsMenu(true);

        username = (TextInputEditText) rootView.findViewById(R.id.FP_username);
        fullName = (TextInputEditText) rootView.findViewById(R.id.FP_fullName);
        email = (TextInputEditText) rootView.findViewById(R.id.FP_email);
        phone = (TextInputEditText) rootView.findViewById(R.id.FP_phone);

        editSave = (FloatingActionButton) rootView.findViewById(R.id.FP_edit);
        if (editSave != null) {
            editSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setViewEditable(!view.isSelected());
                    if (view.isSelected()) {
                        view.setSelected(false);
                        updateUserDetails();
                    } else {
                        view.setSelected(true);
                        editSave.setImageResource(R.drawable.ic_save_black_24dp);
                    }

                }
            });
        }

        getUserDetails();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_changePassword) {
            getChildFragmentManager().beginTransaction()
                    .addToBackStack("Change password")
                    .replace(R.id.FP_contentLayout, ChangePassword.newInstance())
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setViewEditable(Boolean flag) {
        fullName.setEnabled(flag);
        fullName.setFocusableInTouchMode(flag);
        fullName.setSelection(fullName.getText().length());
        email.setEnabled(flag);
        email.setFocusableInTouchMode(flag);
        email.setSelection(email.getText().length());
        phone.setEnabled(flag);
        phone.setFocusableInTouchMode(flag);
        phone.setSelection(phone.getText().length());
    }

    private void getUserDetails() {
        Util.showProgressOrError(getFragmentManager(), R.id.FP_contentLayout, 1, "MY_PROFILE_ACTIVITY_PROGRESS");
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
                try {
                    Util.hideProgressOrError(getFragmentManager(), "MY_PROFILE_ACTIVITY_PROGRESS");
                    if (response.has("status") && response.optBoolean("status")) {
                        Log.i(tag, response.optString("message"));
                        JSONObject dataObject = response.optJSONObject("data");
                        if (dataObject != null) {
                            username.setText(dataObject.optString("userName"));
                            fullName.setText(dataObject.optString("fullName"));
                            email.setText(dataObject.optString("email"));
                            phone.setText(dataObject.optString("mobile"));
                        }
                    } else {
                        Util.showProgressOrError(getFragmentManager(), R.id.FP_contentLayout, 2, "MY_PROFILE_ACTIVITY_ERROR");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Util.hideProgressOrError(getFragmentManager(), "MY_PROFILE_ACTIVITY_PROGRESS");
                    Log.e(tag, "Error :- " + error.toString());
                    Util.showProgressOrError(getFragmentManager(), R.id.FP_contentLayout, 2, "MY_PROFILE_ACTIVITY_ERROR");
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
        Util.showProgressOrError(getFragmentManager(), R.id.FP_contentLayout, 1, "MY_PROFILE_ACTIVITY_PROGRESS");
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
                try {
                    Util.hideProgressOrError(getFragmentManager(), "MY_PROFILE_ACTIVITY_PROGRESS");
                    if (response.has("status") && response.optBoolean("status")) {
                        Log.i(tag, response.optString("message"));
                        JSONObject dataObject = response.optJSONObject("data");
                        if (dataObject != null) {
                            fullName.setText(dataObject.optString("fullName"));
                            email.setText(dataObject.optString("email"));
                            phone.setText(dataObject.optString("mobile"));
                            editSave.setImageResource(R.drawable.ic_create_black_24dp);
                        }
                    } else {
                        Util.showProgressOrError(getFragmentManager(), R.id.FP_contentLayout, 2, "MY_PROFILE_ACTIVITY_ERROR");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Util.hideProgressOrError(getFragmentManager(), "MY_PROFILE_ACTIVITY_PROGRESS");
                    Log.e(tag, "Error :- " + error.toString());
                    Util.showProgressOrError(getFragmentManager(), R.id.FP_contentLayout, 2, "MY_PROFILE_ACTIVITY_ERROR");
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
                params.put("fullName", fullName.getText().toString());
                params.put("email", email.getText().toString());
                params.put("mobile", phone.getText().toString());

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
