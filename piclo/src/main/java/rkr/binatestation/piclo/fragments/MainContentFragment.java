package rkr.binatestation.piclo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.adapters.PictureAdapter;
import rkr.binatestation.piclo.models.PictureModel;
import rkr.binatestation.piclo.network.VolleySingleTon;
import rkr.binatestation.piclo.utils.AutoFitRecyclerView;
import rkr.binatestation.piclo.utils.Constants;
import rkr.binatestation.piclo.utils.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainContentFragment extends Fragment {

    private static final String tag = MainContentFragment.class.getName();
    private String categoryId;

    public MainContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param categoryId Parameter 1.
     * @return A new instance of fragment MainContentFragment.
     */
    public static MainContentFragment newInstance(String categoryId) {
        MainContentFragment fragment = new MainContentFragment();
        Bundle args = new Bundle();
        args.putString("KEY_CATEGORY_ID", categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("KEY_CATEGORY_ID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_content, container, false);
        AutoFitRecyclerView mainContentRecyclerView = (AutoFitRecyclerView) view.findViewById(R.id.FMC_mainContentRecyclerView);
        PictureAdapter pictureAdapter = null;
        if (mainContentRecyclerView != null) {
            mainContentRecyclerView.setHasFixedSize(true);
            mainContentRecyclerView.setAdapter(pictureAdapter = new PictureAdapter());
        }
        getPictures(pictureAdapter);
        return view;
    }

    private void getPictures(final PictureAdapter pictureAdapter) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + Constants.GALLERY, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(tag, "Response payload :- " + response);
                parseResponse(response);
            }

            private void parseResponse(JSONObject response) {
                if (response.has("status") && response.optInt("status") == 1) {
                    Log.i(tag, response.optString("message"));
                    JSONArray dataArray = response.optJSONArray("data");
                    if (dataArray != null) {
                        List<PictureModel> pictureModelList = new ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObject = dataArray.optJSONObject(i);
                            pictureModelList.add(new PictureModel(
                                    dataObject.optString("title"),
                                    dataObject.optString("file"),
                                    new Date(),
                                    dataObject.optString("imageId"),
                                    dataObject.optString("userId"),
                                    dataObject.optString("categoryId"),
                                    dataObject.optString("courtesy"),
                                    dataObject.optString("categoryName"),
                                    dataObject.optString("fullName")
                            ));
                        }
                        pictureAdapter.setPictureModelList(pictureModelList);
                        pictureAdapter.notifyDataSetChanged();
                    }
                } else {
                    Util.alert(getActivity(), "Alert", response.optString("message"), true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag, "Error :- " + error.toString());
                Util.alert(getActivity(), "Network Error", "Please check internet connection.!", true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("categoryId", categoryId);

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
        Log.i(tag, "Request url  :- " + jsonObjectRequest.getUrl());
        VolleySingleTon.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

}
