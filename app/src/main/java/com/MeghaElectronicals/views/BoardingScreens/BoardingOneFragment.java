package com.MeghaElectronicals.views.BoardingScreens;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.MeghaElectronicals.databinding.FragmentBoardingOneBinding;

public class BoardingOneFragment extends Fragment {

    private static final String TAG = "BoardingOneFragment";
    FragmentBoardingOneBinding ui;
    OnNextScreenListener listener = null;

    private final ActivityResultLauncher<Intent> requestOverlayPermission = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            Log.d(TAG, "OVERLAY PERMISSION: " + Settings.canDrawOverlays(requireContext()));
        }
    });

    public BoardingOneFragment() {
        // Required empty public constructor
    }

    public static BoardingOneFragment newInstance() {
        return new BoardingOneFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNextScreenListener)
            listener = (OnNextScreenListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ui = FragmentBoardingOneBinding.inflate(inflater);
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            ui.permission.setVisibility(View.GONE);
            ui.miuiPermission.setVisibility(View.VISIBLE);
        } else {
            ui.permission.setVisibility(View.VISIBLE);
            ui.miuiPermission.setVisibility(View.GONE);
        }


        return ui.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Settings.canDrawOverlays(requireContext())) {

            ui.materialButton.setVisibility(View.INVISIBLE);
            ui.nextButton.setVisibility(View.VISIBLE);

            ui.nextButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNextScreen();
                }
            });

        } else {
            ui.materialButton.setVisibility(View.VISIBLE);
            ui.nextButton.setVisibility(View.GONE);

            ui.materialButton.setOnClickListener(v -> {
                if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
                    Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                    intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                    intent.putExtra("extra_pkgname", requireContext().getPackageName());
                    startActivity(intent);
//                Toast.makeText(this, Build.MANUFACTURER, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + requireContext().getPackageName()));
                    requestOverlayPermission.launch(intent);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}