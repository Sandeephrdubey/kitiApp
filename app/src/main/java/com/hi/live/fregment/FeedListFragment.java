package com.hi.live.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hi.live.R;

public class FeedListFragment extends Fragment {

    private TextView discoverTab, followTab, forYouTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.feedxmlnew, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        discoverTab = view.findViewById(R.id.discoverTab);
        followTab = view.findViewById(R.id.followTab);
        forYouTab = view.findViewById(R.id.forYouTab);

        loadFragment(new com.hi.live.fragment.DiscoverFragment());
        updateTabSelection(discoverTab);

        discoverTab.setOnClickListener(v -> {
            loadFragment(new com.hi.live.fragment.DiscoverFragment());
            updateTabSelection(discoverTab);
        });

        followTab.setOnClickListener(v -> {
            loadFragment(new com.hi.live.fragment.FollowFragment());
            updateTabSelection(followTab);
        });

        forYouTab.setOnClickListener(v -> {
            loadFragment(new com.hi.live.fragment.ForYouFragment());
            updateTabSelection(forYouTab);
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.tabContentFrame, fragment);
        transaction.commit();
    }

    private void updateTabSelection(TextView selectedTab) {
        discoverTab.setSelected(false);
        followTab.setSelected(false);
        forYouTab.setSelected(false);

        selectedTab.setSelected(true);
    }
}
