package rkr.binatestation.piclo.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.activities.UploadPicture;
import rkr.binatestation.piclo.adapters.PictureAdapter;
import rkr.binatestation.piclo.database.PicloContract;
import rkr.binatestation.piclo.models.PictureModel;
import rkr.binatestation.piclo.network.VolleySingleTon;
import rkr.binatestation.piclo.utils.AutoFitRecyclerView;
import rkr.binatestation.piclo.utils.Constants;

import static rkr.binatestation.piclo.models.PictureModel.getPictureModels;
import static rkr.binatestation.piclo.utils.Constants.CONTENT_LOADER_PICS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainContentFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainContentFragment";


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
        args.putString(Constants.KEY_CATEGORY_ID, categoryId);
        args.putString(Constants.KEY_PARENT_ACTIVITY, parentActivity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_content, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.FMC_swipeRefresh);
        AutoFitRecyclerView mainContentRecyclerView = (AutoFitRecyclerView) view.findViewById(R.id.FMC_mainContentRecyclerView);

        if (getArguments() != null) {
            categoryId = getArguments().getString(Constants.KEY_CATEGORY_ID);
            parentActivity = getArguments().getString(Constants.KEY_PARENT_ACTIVITY);
        }

        if (mainContentRecyclerView != null) {
            mainContentRecyclerView.setAdapter(pictureAdapter = new PictureAdapter(MainContentFragment.this));
        }
        getPictures();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPictures();
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
        initPictureLoad();
        return view;
    }


    private void getPictures() {
        swipeRefreshLayout.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + Constants.GALLERY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response payload :- " + response);
                try {
                    swipeRefreshLayout.setRefreshing(false);
                    parseResponse(new JSONObject(response));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void parseResponse(JSONObject response) {
                try {
                    if (response.has(Constants.KEY_JSON_STATUS) && response.optBoolean(Constants.KEY_JSON_STATUS)) {
                        Log.i(TAG, response.optString(Constants.KEY_JSON_MESSAGE));
                        JSONArray dataArray = response.optJSONArray(Constants.KEY_JSON_DATA);
                        if (dataArray != null && dataArray.length() > 0) {
                            saveImagesIntoDB(dataArray);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e(TAG, "Error :- " + error.toString());
                    swipeRefreshLayout.setRefreshing(false);
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

                Log.d(TAG, getUrl() + " : Request payload :- " + params.toString());
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

    private void saveImagesIntoDB(final JSONArray dataArray) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                getContext().getContentResolver().bulkInsert(
                        PicloContract.PicturesEntry.CONTENT_URI,
                        getContentValues(dataArray)
                );
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                initPictureLoad();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void initPictureLoad() {
        getActivity().getSupportLoaderManager().initLoader(CONTENT_LOADER_PICS, null, this);
    }

    private ContentValues[] getContentValues(JSONArray dataArray) {
        if (dataArray != null) {
            ContentValues[] contentValues = new ContentValues[dataArray.length()];
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.optJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(PicloContract.PicturesEntry.COLUMN_TITLE, dataObject.optString(Constants.KEY_JSON_TITLE));
                values.put(PicloContract.PicturesEntry.COLUMN_FILE, dataObject.optString(Constants.KEY_JSON_FILE));
                values.put(PicloContract.PicturesEntry.COLUMN_UPDATED_DATE, new Date().getTime());
                values.put(PicloContract.PicturesEntry.COLUMN_IMAGE_ID, dataObject.optString(Constants.KEY_JSON_IMAGE_ID));
                values.put(PicloContract.PicturesEntry.COLUMN_USER_ID, dataObject.optString(Constants.KEY_JSON_USER_ID));
                values.put(PicloContract.PicturesEntry.COLUMN_CATEGORY_ID, dataObject.optString(Constants.KEY_JSON_CATEGORY_ID));
                values.put(PicloContract.PicturesEntry.COLUMN_COURTESY, dataObject.optString(Constants.KEY_JSON_COURTESY));
                values.put(PicloContract.PicturesEntry.COLUMN_CATEGORY_NAME, dataObject.optString(Constants.KEY_JSON_CATEGORY_NAME));
                values.put(PicloContract.PicturesEntry.COLUMN_FULL_NAME, dataObject.optString(Constants.KEY_JSON_FULL_NAME));

                contentValues[i] = values;
            }
            return contentValues;
        }
        return new ContentValues[0];
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CONTENT_LOADER_PICS:
                return new CursorLoader(
                        getContext(),
                        PicloContract.PicturesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader != null) {
            switch (loader.getId()) {
                case CONTENT_LOADER_PICS:
                    setPictureAdapter(getPictureModels(data));
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setPictureAdapter(List<PictureModel> pictureModelList) {
        try {
            if (pictureAdapter != null) {
                pictureAdapter.setPictureModelList(pictureModelList);
                pictureAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
