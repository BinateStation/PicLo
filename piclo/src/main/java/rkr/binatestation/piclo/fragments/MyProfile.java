package rkr.binatestation.piclo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rkr.binatestation.piclo.R;

/**
 * Created by RKR on 10-06-2016.
 * MyProfile.
 */
public class MyProfile extends Fragment {
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
        return rootView;
    }

}
