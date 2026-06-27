package com.example.eduhub.settings;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.eduhub.LoginActivity;
import com.example.eduhub.R;
import com.example.eduhub.appbars.BackHeaderFragment;
import com.example.eduhub.database.DBHelper;
import com.example.eduhub.domains.classes.User;
import com.example.eduhub.utils.AvatarManager;
import com.example.eduhub.ui.SyncRefreshFragment;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.HashMap;

public class SettingsFragment extends SyncRefreshFragment {

    private String userId;
    private Uri cameraPhotoUri;

    private ImageView imageAvatar;
    private TextView textAvatar;
    private FrameLayout avatarContainer;
    private boolean hasAvatar;

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) startAvatarEdit(uri);
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && cameraPhotoUri != null) startAvatarEdit(cameraPhotoUri);
            });

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allGranted = true;
                for (Boolean granted : permissions.values()) {
                    if (!granted) { allGranted = false; break; }
                }
                if (allGranted) openCamera();
            });

    private final ActivityResultLauncher<Intent> avatarEditLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    String path = result.getData().getStringExtra(AvatarEditActivity.RESULT_PATH);
                    if (path != null) {
                        saveAvatarPath(path);
                        loadAvatar(path);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            userId = getArguments().getString("userId", "");
        }
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BackHeaderFragment headerFragment = BackHeaderFragment.newInstance("Профиль");
        getChildFragmentManager().beginTransaction()
                .replace(R.id.header_fragment_container, headerFragment)
                .commit();

        imageAvatar = view.findViewById(R.id.image_avatar);
        textAvatar = view.findViewById(R.id.text_avatar);
        avatarContainer = view.findViewById(R.id.avatar_container);

        avatarContainer.setOnClickListener(v -> showAvatarPicker());

        SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        View settingsScroll = view.findViewById(R.id.settings_scroll);
        if (swipe != null && settingsScroll != null) {
            bindRefreshScroll(swipe, settingsScroll);
        }

        loadUserProfile(view);
        loadNotificationSettings(view);
        initLearningSystem(view);

        TextView btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            com.example.eduhub.network.session.UserSessionManager
                    .getInstance(requireContext()).clearSession();
            new com.example.eduhub.network.sync.UserDataSyncManager(requireContext())
                    .clearSyncState();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void reloadAfterSync() {
        View v = getView();
        if (v != null) {
            loadUserProfile(v);
            loadNotificationSettings(v);
        }
    }

    private void loadUserProfile(View view) {
        String firstName = "Иван";
        String lastName = "Иванов";
        String roleLabel = "Студент";
        boolean isTeacher = false;

        if (userId != null && !userId.isEmpty()) {
            try {
                DBHelper dbHelper = DBHelper.getInstance(requireContext());
                User user = dbHelper.findUserById(userId);
                if (user != null) {
                    firstName = user.getFirstName();
                    lastName = user.getLastName();
                    isTeacher = user.getRole() == User.UserRole.TEACHER;
                    if (isTeacher) {
                        roleLabel = "Учитель";
                    }
                    String avatarUrl = user.getAvatarUrl();
                    if (avatarUrl != null && !avatarUrl.isEmpty() && !"default_avatar".equals(avatarUrl)) {
                        loadAvatar(avatarUrl);
                    } else {
                        hasAvatar = false;
                        AvatarManager.clearAvatarView(requireContext(), imageAvatar);
                    }
                }
            } catch (Exception ignored) {}
        }

        String fullName = lastName + " " + firstName;
        String initials = String.valueOf(lastName.charAt(0)) + firstName.charAt(0);

        textAvatar.setText(initials);
        hasAvatar = AvatarManager.hasAvatar(requireContext(), userId);
        updateAvatarVisibility();

        TextView tvFullname = view.findViewById(R.id.text_fullname);
        TextView tvSubtitle = view.findViewById(R.id.text_subtitle);

        tvFullname.setText(fullName);
        String groupName = com.example.eduhub.network.session.UserSessionManager
                .getInstance(requireContext()).getGroupName();
        if (!isTeacher && groupName != null && !groupName.isEmpty()) {
            tvSubtitle.setText(roleLabel + " • " + groupName);
        } else {
            tvSubtitle.setText(roleLabel);
        }

        View rowNewGrades = view.findViewById(R.id.row_new_grades);
        if (rowNewGrades != null) {
            rowNewGrades.setVisibility(isTeacher ? View.GONE : View.VISIBLE);
        }
        View rowHomework = view.findViewById(R.id.row_homework);
        if (rowHomework != null) {
            rowHomework.setVisibility(isTeacher ? View.GONE : View.VISIBLE);
        }
    }

    private void loadAvatar(String path) {
        hasAvatar = AvatarManager.hasAvatar(requireContext(), userId)
                || (path != null && !path.isEmpty() && !"default_avatar".equals(path)
                && new java.io.File(path).exists());
        updateAvatarVisibility();
        if (hasAvatar) {
            AvatarManager.loadAvatarInto(requireContext(), imageAvatar, path);
        } else {
            AvatarManager.clearAvatarView(requireContext(), imageAvatar);
        }
    }

    private void updateAvatarVisibility() {
        if (hasAvatar) {
            textAvatar.setVisibility(View.GONE);
            imageAvatar.setVisibility(View.VISIBLE);
        } else {
            textAvatar.setVisibility(View.VISIBLE);
            imageAvatar.setVisibility(View.GONE);
        }
    }

    private void showAvatarPicker() {
        boolean hasPhoto = hasAvatar;
        AvatarPickerBottomSheet sheet = AvatarPickerBottomSheet.newInstance(hasPhoto);
        sheet.setListener(action -> {
            if (action == AvatarPickerBottomSheet.ACTION_TAKE_PHOTO) {
                checkCameraPermissionAndOpen();
            } else if (action == AvatarPickerBottomSheet.ACTION_CHOOSE_GALLERY) {
                openGallery();
            } else if (action == AvatarPickerBottomSheet.ACTION_DELETE) {
                deleteAvatar();
            }
        });
        sheet.show(getChildFragmentManager(), "AvatarPicker");
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            permissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
        }
    }

    private void openCamera() {
        try {
            File photoFile = new File(requireContext().getCacheDir(), "camera_photo.jpg");
            cameraPhotoUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider", photoFile);
            cameraLauncher.launch(cameraPhotoUri);
        } catch (Exception e) {
            cameraPhotoUri = null;
        }
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void startAvatarEdit(Uri sourceUri) {
        Intent intent = new Intent(requireContext(), AvatarEditActivity.class);
        intent.putExtra(AvatarEditActivity.EXTRA_URI, sourceUri);
        intent.putExtra(AvatarEditActivity.EXTRA_USER_ID, userId);
        avatarEditLauncher.launch(intent);
    }

    private void saveAvatarPath(String path) {
        if (userId == null || userId.isEmpty()) return;
        DBHelper dbHelper = DBHelper.getInstance(requireContext());
        dbHelper.updateUserAvatar(userId, path);
        dbHelper.close();
    }

    private void deleteAvatar() {
        AvatarManager.deleteAvatar(requireContext(), userId);
        if (userId != null && !userId.isEmpty()) {
            DBHelper dbHelper = DBHelper.getInstance(requireContext());
            dbHelper.updateUserAvatar(userId, "default_avatar");
            dbHelper.close();
        }
        hasAvatar = false;
        AvatarManager.clearAvatarView(requireContext(), imageAvatar);
        updateAvatarVisibility();
    }

    private void loadNotificationSettings(View view) {
        if (userId == null || userId.isEmpty()) return;

        DBHelper dbHelper = DBHelper.getInstance(requireContext());
        HashMap<String, Boolean> settings = dbHelper.getNotificationSettings(userId);
        dbHelper.close();

        CheckBox cbGrades = view.findViewById(R.id.checkbox_new_grades);
        CheckBox cbSchedule = view.findViewById(R.id.checkbox_schedule_changes);
        CheckBox cbHomework = view.findViewById(R.id.checkbox_homework);

        if (settings != null) {
            if (cbGrades != null) {
                Boolean val = settings.get("newGrades");
                cbGrades.setChecked(val == null || val);
                cbGrades.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    DBHelper h = DBHelper.getInstance(requireContext());
                    h.updateNotificationSetting(userId, "newGrades", isChecked);
                    h.close();
                });
            }
            if (cbSchedule != null) {
                Boolean val = settings.get("scheduleChanges");
                cbSchedule.setChecked(val == null || val);
                cbSchedule.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    DBHelper h = DBHelper.getInstance(requireContext());
                    h.updateNotificationSetting(userId, "scheduleChanges", isChecked);
                    h.close();
                });
            }
            if (cbHomework != null) {
                Boolean val = settings.get("homework");
                cbHomework.setChecked(val == null || val);
                cbHomework.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    DBHelper h = DBHelper.getInstance(requireContext());
                    h.updateNotificationSetting(userId, "homework", isChecked);
                    h.close();
                });
            }
        }
    }

    private void initLearningSystem(View view) {
        if (userId == null || userId.isEmpty()) return;

        DBHelper dbHelper = DBHelper.getInstance(requireContext());
        String current = dbHelper.getLearningSystem(userId);
        dbHelper.close();

        TextView btnTrimester = view.findViewById(R.id.btn_trimester);
        TextView btnSemester = view.findViewById(R.id.btn_semester);

        updateLearningSystemButtons(btnTrimester, btnSemester, current);

        btnTrimester.setOnClickListener(v -> {
            DBHelper h = DBHelper.getInstance(requireContext());
            h.setLearningSystem(userId, "trimester");
            h.close();
            updateLearningSystemButtons(btnTrimester, btnSemester, "trimester");
        });

        btnSemester.setOnClickListener(v -> {
            DBHelper h = DBHelper.getInstance(requireContext());
            h.setLearningSystem(userId, "semester");
            h.close();
            updateLearningSystemButtons(btnTrimester, btnSemester, "semester");
        });
    }

    private void updateLearningSystemButtons(TextView btnTrimester, TextView btnSemester, String selected) {
        if ("trimester".equals(selected)) {
            btnTrimester.setBackgroundResource(R.drawable.bg_tasks_tab_selected);
            btnTrimester.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            btnSemester.setBackgroundResource(R.drawable.bg_tasks_tab_unselected);
            btnSemester.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));
        } else {
            btnSemester.setBackgroundResource(R.drawable.bg_tasks_tab_selected);
            btnSemester.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            btnTrimester.setBackgroundResource(R.drawable.bg_tasks_tab_unselected);
            btnTrimester.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));
        }
    }
}
