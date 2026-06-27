package com.example.eduhub.appbars;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eduhub.R;
import com.google.android.material.appbar.MaterialToolbar;

public class BackHeaderFragment extends Fragment {

    private String titleText = "";

    public static BackHeaderFragment newInstance(String title) {
        BackHeaderFragment fragment = new BackHeaderFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleText = getArguments().getString("title");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.appbar_exit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar backToolbar = view.findViewById(R.id.appbar_back);

        if (backToolbar != null && !titleText.isEmpty()) {
            backToolbar.setTitle(titleText);
        }

        if (backToolbar != null) {
            backToolbar.setNavigationOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
    }
}
