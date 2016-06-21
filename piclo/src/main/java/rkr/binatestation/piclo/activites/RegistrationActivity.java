package rkr.binatestation.piclo.activites;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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

public class RegistrationActivity extends AppCompatActivity {

    private static final String tag = RegistrationActivity.class.getName();
    TextInputEditText username, fullName, email, phone, password, confirmPassword;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        username = (TextInputEditText) findViewById(R.id.AR_username);
        fullName = (TextInputEditText) findViewById(R.id.AR_fullName);
        email = (TextInputEditText) findViewById(R.id.AR_email);
        phone = (TextInputEditText) findViewById(R.id.AR_phone);
        password = (TextInputEditText) findViewById(R.id.AR_password);
        confirmPassword = (TextInputEditText) findViewById(R.id.AR_confirmPassword);
        mProgressDialog = new ProgressDialog(getContext());

        FloatingActionButton register = (FloatingActionButton) findViewById(R.id.AR_register);
        if (register != null) {
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    validateInputs();
                }
            });
        }
    }

    public void showProgressDialog(Boolean aBoolean) {
        if (mProgressDialog != null) {
            if (aBoolean) {
                mProgressDialog.setMessage("Please wait ...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            } else {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void validateInputs() {
        if (TextUtils.isEmpty(username.getText().toString())) {
            username.setError("Please provide your user name");
            username.requestFocus();
        } else if (TextUtils.isEmpty(fullName.getText().toString())) {
            fullName.setError("Please provide your name");
            fullName.requestFocus();
        } else if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Please provide your email id");
            email.requestFocus();
        } else if (!Util.isValidEmail(email.getText().toString().trim())) {
            email.setError("Please provide a valid email id");
            email.requestFocus();
        } else if (TextUtils.isEmpty(phone.getText().toString())) {
            phone.setError("Please provide your phone number");
            phone.requestFocus();
        } else if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Please provide your password");
            password.requestFocus();
        } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            confirmPassword.setError("Password mismatch.");
            confirmPassword.requestFocus();
        } else {
            register();
        }
    }

    private void register() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + Constants.REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(tag, "Response payload :- " + response);
                showProgressDialog(false);
                try {
                    parseResponse(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void parseResponse(JSONObject response) {
                if (response.has("status") && response.optBoolean("status")) {
                    Log.i(tag, response.optString("message"));
                    JSONObject data = response.optJSONObject("data");
                    if (data != null) {
                        getSharedPreferences(getPackageName(), MODE_PRIVATE).edit()
                                .putString(Constants.KEY_USER_ID, data.optString("userId"))
                                .putString(Constants.KEY_USER_FULL_NAME, data.optString("fullName"))
                                .putString(Constants.KEY_USER_EMAIL, data.optString("email"))
                                .putString(Constants.KEY_MOBILE, data.optString("mobile"))
                                .putString(Constants.KEY_USER_NAME, data.optString("userName"))
                                .putBoolean(Constants.KEY_IS_LOGGED_IN, true)
                                .apply();
                        onBackPressed();
                    }
                } else {
                    Util.alert(getContext(), "Alert", response.optString("message"), false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag, "Error :- " + error.toString());
                showProgressDialog(false);
                Util.alert(getContext(), "Network Error", "Please check internet connection.!", false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", phone.getText().toString().trim());
                params.put("email", email.getText().toString().trim());
                params.put("fullName", fullName.getText().toString().trim());
                params.put("userName", username.getText().toString().trim());
                params.put("password", password.getText().toString().trim());

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
        VolleySingleTon.getInstance(getContext()).addToRequestQueue(stringRequest);
        showProgressDialog(true);
    }

    private RegistrationActivity getContext() {
        return RegistrationActivity.this;
    }

}
