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

public class BoardingScreensActivity extends AppCompatActivity implements OnNextScreenListener {

    private static final String TAG = "BoardingSreensActivity";
    ActivityBoardingSreensBinding ui;
    public static ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = ActivityBoardingSreensBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());

        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(BoardingOneFragment.newInstance());
        fragmentArrayList.add(BoardingSecondFragment.newInstance());

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle(), fragmentArrayList);

        ui.viewPager.setAdapter(adapter);
        ui.viewPager.setHorizontalScrollBarEnabled(false);

    }

    @Override
    public void onNextScreen() {
        int currentItem = ui.viewPager.getCurrentItem();
        if (currentItem < adapter.getItemCount() - 1) {
            ui.viewPager.setCurrentItem(currentItem + 1, true);
        }
    }

    public static class ViewPagerAdapter extends FragmentStateAdapter {

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