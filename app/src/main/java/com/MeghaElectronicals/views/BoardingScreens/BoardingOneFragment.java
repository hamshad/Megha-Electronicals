package com.MeghaElectronicals.views.BoardingScreens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.MeghaElectronicals.R;

public class BoardingOneFragment extends Fragment {

    public BoardingOneFragment() {
        // Required empty public constructor
    }

    public static BoardingOneFragment newInstance() {
        return new BoardingOneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_boarding_one, container, false);
    }
}