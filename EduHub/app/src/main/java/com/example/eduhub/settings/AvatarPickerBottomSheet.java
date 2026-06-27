package com.example.eduhub.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eduhub.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AvatarPickerBottomSheet extends BottomSheetDialogFragment {

    public static final int ACTION_TAKE_PHOTO = 1;
    public static final int ACTION_CHOOSE_GALLERY = 2;
    public static final int ACTION_DELETE = 3;

    private static final String ARG_HAS_PHOTO = "hasPhoto";

    private AvatarPickerListener listener;
    private boolean hasPhoto;

    public interface AvatarPickerListener {
        void onActionSelected(int action);
    }

    public static AvatarPickerBottomSheet newInstance(boolean hasPhoto) {
        AvatarPickerBottomSheet sheet = new AvatarPickerBottomSheet();
        Bundle args = new Bundle();
        args.putBoolean(ARG_HAS_PHOTO, hasPhoto);
        sheet.setArguments(args);
        return sheet;
    }

    public void setListener(AvatarPickerListener listener) {
        this.listener = listener;
    }

    @Override
    public int getTheme() {
        return R.style.Theme_EduHub_BottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_avatar_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            hasPhoto = getArguments().getBoolean(ARG_HAS_PHOTO, false);
        }

        TextView btnDelete = view.findViewById(R.id.btn_delete_photo);
        btnDelete.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);

        view.findViewById(R.id.btn_take_photo).setOnClickListener(v -> {
            if (listener != null) listener.onActionSelected(ACTION_TAKE_PHOTO);
            dismiss();
        });

        view.findViewById(R.id.btn_choose_gallery).setOnClickListener(v -> {
            if (listener != null) listener.onActionSelected(ACTION_CHOOSE_GALLERY);
            dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onActionSelected(ACTION_DELETE);
            dismiss();
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());
    }
}
