package com.mohaa.eldokan.Controllers.fragments_home;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mohaa.eldokan.R;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashFragment extends Fragment {


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public DashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment


        return view;
    }
}
