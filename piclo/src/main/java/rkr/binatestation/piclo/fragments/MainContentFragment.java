package rkr.binatestation.piclo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.adapters.PictureAdapter;
import rkr.binatestation.piclo.utils.AutoFitRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainContentFragment extends Fragment {

    private int categoryId;


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
    public static MainContentFragment newInstance(int categoryId) {
        MainContentFragment fragment = new MainContentFragment();
        Bundle args = new Bundle();
        args.putInt("KEY_CATEGORY_ID", categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt("KEY_CATEGORY_ID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_content, container, false);
        AutoFitRecyclerView mainContentRecyclerView = (AutoFitRecyclerView) view.findViewById(R.id.FMC_mainContentRecyclerView);
        if (mainContentRecyclerView != null) {
            mainContentRecyclerView.setHasFixedSize(true);
            mainContentRecyclerView.setAdapter(new PictureAdapter());
        }
        return view;
    }

}
