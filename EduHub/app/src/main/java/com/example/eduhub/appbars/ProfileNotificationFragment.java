package com.example.eduhub.appbars;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.eduhub.R;
import com.example.eduhub.database.DBHelper;
import com.example.eduhub.domains.classes.User;
import com.example.eduhub.notifications.NotificationsFragment;
import com.example.eduhub.utils.AvatarManager;
import com.google.android.material.appbar.MaterialToolbar;

public class ProfileNotificationFragment extends Fragment {

    private String titleText = "";
    private String userId = "";

    private MaterialToolbar profileToolbar;
    private TextView tvAvatarInitials;
    private ImageView ivAvatar;

    public static ProfileNotificationFragment newInstance(String title, String userId) {
        ProfileNotificationFragment fragment = new ProfileNotificationFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleText = getArguments().getString("title");
            userId = getArguments().getString("userId", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.appbar_profile_announcment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileToolbar = view.findViewById(R.id.appbar_profile);
        MaterialToolbar notifyToolbar = view.findViewById(R.id.appbar_notify);
        FrameLayout avatarContainer = view.findViewById(R.id.avatar_container);
        tvAvatarInitials = view.findViewById(R.id.tv_avatar_initials);
        ivAvatar = view.findViewById(R.id.iv_avatar);

        if (!titleText.isEmpty() && profileToolbar != null) {
            profileToolbar.setTitle(titleText);
        }

        loadProfileHeader();

        avatarContainer.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("userId", userId);
            Navigation.findNavController(v).navigate(R.id.settingsFragment, args);
        });

        if (notifyToolbar != null) {
            notifyToolbar.setNavigationOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("userId", userId);
                Navigation.findNavController(v).navigate(R.id.notificationsFragment, args);
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileHeader();
    }

    private void loadProfileHeader() {
        if (tvAvatarInitials == null || ivAvatar == null) {
            return;
        }

        String lastName = "";
        String firstName = "";
        String avatarPath = null;

        if (userId != null && !userId.isEmpty()) {
            try {
                DBHelper dbHelper = DBHelper.getInstance(requireContext());
                User user = dbHelper.findUserById(userId);
                if (user != null) {
                    lastName = user.getLastName();
                    firstName = user.getFirstName();
                    if (profileToolbar != null) {
                        profileToolbar.setSubtitle(lastName + " " + firstName);
                    }
                    String avatarUrl = user.getAvatarUrl();
                    if (avatarUrl != null && !avatarUrl.isEmpty() && !"default_avatar".equals(avatarUrl)) {
                        avatarPath = avatarUrl;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        String initials = "П";
        if (!lastName.isEmpty() && !firstName.isEmpty()) {
            initials = String.valueOf(lastName.charAt(0)) + firstName.charAt(0);
        }
        tvAvatarInitials.setText(initials);

        boolean hasPhoto = avatarPath != null && AvatarManager.hasAvatar(requireContext(), userId);
        if (!hasPhoto && avatarPath != null) {
            hasPhoto = new java.io.File(avatarPath).exists();
        }

        if (hasPhoto) {
            tvAvatarInitials.setVisibility(View.GONE);
            ivAvatar.setVisibility(View.VISIBLE);
            AvatarManager.loadAvatarInto(requireContext(), ivAvatar, avatarPath);
        } else {
            tvAvatarInitials.setVisibility(View.VISIBLE);
            ivAvatar.setVisibility(View.GONE);
            AvatarManager.clearAvatarView(requireContext(), ivAvatar);
        }
    }
}
