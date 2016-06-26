package rkr.binatestation.piclo.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.activites.UploadPicture;
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
    SwipeRefreshLayout swipeRefreshLayout;
    PictureAdapter pictureAdapter;
    private String categoryId, parentActivity;

    public MainContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param categoryId     Parameter 1.
     * @param parentActivity parent activity which the fragment is called
     * @return A new instance of fragment MainContentFragment.
     */
    public static MainContentFragment newInstance(String categoryId, String parentActivity) {
        MainContentFragment fragment = new MainContentFragment();
        Bundle args = new Bundle();
        args.putString("KEY_CATEGORY_ID", categoryId);
        args.putString("KEY_PARENT_ACTIVITY", parentActivity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("KEY_CATEGORY_ID");
            parentActivity = getArguments().getString("KEY_PARENT_ACTIVITY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_content, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.FMC_swipeRefresh);
        AutoFitRecyclerView mainContentRecyclerView = (AutoFitRecyclerView) view.findViewById(R.id.FMC_mainContentRecyclerView);
        if (mainContentRecyclerView != null) {
            mainContentRecyclerView.setHasFixedSize(true);
            mainContentRecyclerView.setAdapter(pictureAdapter = new PictureAdapter());
        }
        if (pictureAdapter != null) {
            getPictures(pictureAdapter);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (pictureAdapter != null) {
                    getPictures(pictureAdapter);
                }
            }
        });
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.FMC_upload);
        if (categoryId.equals("100")) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE).getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
                        startActivity(new Intent(getContext(), UploadPicture.class));
                    }
                }
            });
        } else {
            fab.setVisibility(View.GONE);
        }

        return view;
    }

    private void getPictures(final PictureAdapter pictureAdapter) {
        swipeRefreshLayout.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + Constants.GALLERY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(tag, "Response payload :- " + response);
                swipeRefreshLayout.setRefreshing(false);
                try {
                    parseResponse(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void parseResponse(JSONObject response) {
                try {
                    if (response.has("status") && response.optBoolean("status")) {
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
                        Util.showProgressOrError(getFragmentManager(), R.id.FMC_contentLayout, 2);
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
                    swipeRefreshLayout.setRefreshing(false);
                    Util.showProgressOrError(getFragmentManager(), R.id.FMC_contentLayout, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("category", categoryId);
                if (parentActivity.equals("P")) {
                    params.put("userId", getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE).getString(Constants.KEY_USER_ID, ""));
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
        VolleySingleTon.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
