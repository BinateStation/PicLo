package rkr.binatestation.piclo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rkr.binatestation.piclo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressError extends Fragment {


    Integer type;

    public ProgressError() {
        // Required empty public constructor
    }

    public static ProgressError newInstance(Integer type) {

        Bundle args = new Bundle();
        args.putInt("KEY_TYPE", type);

        ProgressError fragment = new ProgressError();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_progress_error, container, false);
        type = getArguments().getInt("KEY_TYPE");
        View progress = view.findViewById(R.id.FPE_progress);
        View error = view.findViewById(R.id.FPE_error);
        if (type == 1) {
            progress.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
        }
        return view;
    }

}
