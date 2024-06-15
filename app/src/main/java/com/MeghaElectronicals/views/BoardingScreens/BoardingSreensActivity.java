package com.MeghaElectronicals.views.BoardingScreens;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.MeghaElectronicals.databinding.ActivityBoardingSreensBinding;

import java.util.ArrayList;
import java.util.List;

public class BoardingSreensActivity extends AppCompatActivity {

    private static final String TAG = "BoardingSreensActivity";
    ActivityBoardingSreensBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = ActivityBoardingSreensBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());

        ui.viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle(), new ArrayList<Fragment>(List.of(BoardingOneFragment.newInstance(), BoardingOneFragment.newInstance()))));

    }

    public class ViewPagerAdapter extends FragmentStateAdapter {

        private final ArrayList<Fragment> fragments;

        public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<Fragment> fragments) {
            super(fragmentManager, lifecycle);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Log.d("TAG", "createFragment: " + position);
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }

}