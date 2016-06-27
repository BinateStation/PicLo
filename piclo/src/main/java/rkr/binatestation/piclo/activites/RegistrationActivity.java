package rkr.binatestation.piclo.activites;

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
                try {
                    Log.d(tag, "Response payload :- " + response);
                    Util.hideProgressOrError(getSupportFragmentManager(), "REGISTRATION_ACTIVITY_PROGRESS");
                    parseResponse(new JSONObject(response));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void parseResponse(JSONObject response) {
                try {
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
                        Util.showProgressOrError(getSupportFragmentManager(), R.id.AR_contentLayout, 2, "REGISTRATION_ACTIVITY_ERROR");
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
                    Util.hideProgressOrError(getSupportFragmentManager(), "REGISTRATION_ACTIVITY_PROGRESS");
                    Util.showProgressOrError(getSupportFragmentManager(), R.id.AR_contentLayout, 2, "REGISTRATION_ACTIVITY_ERROR");
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        Util.showProgressOrError(getSupportFragmentManager(), R.id.AR_contentLayout, 1, "REGISTRATION_ACTIVITY_PROGRESS");
    }

    private RegistrationActivity getContext() {
        return RegistrationActivity.this;
    }

}
