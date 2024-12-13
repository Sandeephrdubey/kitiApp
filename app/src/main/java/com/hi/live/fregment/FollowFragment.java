package com.hi.live.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hi.live.R;

import java.util.Arrays;
import java.util.List;

public class FollowFragment extends Fragment {

    private RecyclerView followRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_follow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        followRecyclerView = view.findViewById(R.id.followRecyclerView);

        // Dummy user list
        List<String> userList = Arrays.asList("Samry", "User 1", "User 2", "User 3", "User 4", "User 5");

        // Corresponding image resource IDs
        List<Integer> imageList = Arrays.asList(
                R.drawable.image1,
                R.drawable.image2,
                R.drawable.image3,
                R.drawable.image4,
                R.drawable.image5,
                R.drawable.image_placeholder
        );

        // Set up RecyclerView
        com.hi.live.fragment.FollowAdapter adapter = new com.hi.live.fragment.FollowAdapter(userList, imageList);
        followRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        followRecyclerView.setAdapter(adapter);
    }
}
