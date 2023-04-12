package com.rova.g3an.Fragment_Home;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rova.g3an.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

}
