package com.MeghaElectronicals.views.BoardingScreens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.MeghaElectronicals.databinding.FragmentBoardingSecondBinding;
import com.MeghaElectronicals.views.LoginActivity;

public class BoardingSecondFragment extends Fragment {

    private static final String TAG = "BoardingSecondFragment";
    FragmentBoardingSecondBinding ui;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean o) {
            Log.d(TAG, "onActivityResult: " + o);
        }
    });


    public BoardingSecondFragment() {
        // Required empty public constructor
    }

    public static BoardingSecondFragment newInstance() {
        return new BoardingSecondFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ui = FragmentBoardingSecondBinding.inflate(inflater);

        return ui.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            ui.materialButton.setVisibility(View.VISIBLE);
            ui.nextButton.setVisibility(View.GONE);

            ui.materialButton.setOnClickListener(v ->
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS));


        } else {

            ui.materialButton.setVisibility(View.INVISIBLE);
            ui.nextButton.setVisibility(View.VISIBLE);

            ui.nextButton.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                requireActivity().startActivity(intent);
                requireActivity().finish();
            });
        }
    }
}