package com.example.eduhub.settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import dagger.hilt.android.AndroidEntryPoint;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eduhub.R;
import com.example.eduhub.settings.widgets.ZoomImageView;
import com.example.eduhub.utils.AvatarManager;

import java.io.File;

@AndroidEntryPoint
public class AvatarEditActivity extends AppCompatActivity {

    public static final String EXTRA_URI = "image_uri";
    public static final String EXTRA_USER_ID = "user_id";
    public static final String RESULT_PATH = "avatar_path";

    private ZoomImageView imagePreview;
    private Uri imageUri;
    private String userId;
    private Bitmap originalBitmap;
    private Bitmap displayBitmap;
    private int rotationDegrees;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_edit);

        imageUri = getIntent().getParcelableExtra(EXTRA_URI);
        userId = getIntent().getStringExtra(EXTRA_USER_ID);

        imagePreview = findViewById(R.id.image_preview);

        TextView btnCancel = findViewById(R.id.btn_cancel);
        TextView btnSave = findViewById(R.id.btn_save);
        ImageButton btnRotateLeft = findViewById(R.id.btn_rotate_left);
        ImageButton btnRotateRight = findViewById(R.id.btn_rotate_right);

        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        btnSave.setOnClickListener(v -> saveAvatar());
        btnRotateLeft.setOnClickListener(v -> rotateBy(-90));
        btnRotateRight.setOnClickListener(v -> rotateBy(90));

        loadImage();
    }

    private void loadImage() {
        if (imageUri == null) return;

        originalBitmap = AvatarManager.decodeUriToBitmap(this, imageUri, 2048);
        if (originalBitmap != null) {
            updatePreview();
        }
    }

    private void rotateBy(int delta) {
        rotationDegrees = (rotationDegrees + delta + 360) % 360;
        updatePreview();
    }

    private void updatePreview() {
        if (originalBitmap == null) return;

        if (displayBitmap != null && displayBitmap != originalBitmap) {
            displayBitmap.recycle();
        }

        if (rotationDegrees == 0) {
            displayBitmap = originalBitmap;
        } else {
            displayBitmap = AvatarManager.rotateBitmap(originalBitmap, rotationDegrees);
        }

        imagePreview.setImageBitmap(displayBitmap);
    }

    private void saveAvatar() {
        Bitmap working = displayBitmap;
        if (working == null || working.isRecycled()) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        float[] cropRect = imagePreview.getVisibleCropRect(280);

        Bitmap circleBitmap = AvatarManager.cropRegion(working,
                cropRect[0], cropRect[1], cropRect[2]);

        if (circleBitmap == null) {
            circleBitmap = AvatarManager.cropToCircle(working);
        }

        File avatarFile = AvatarManager.getAvatarFile(this, userId);
        AvatarManager.deleteAvatar(this, userId);
        boolean saved = AvatarManager.saveBitmap(circleBitmap, avatarFile);

        if (!circleBitmap.isRecycled() && circleBitmap != working && circleBitmap != originalBitmap) {
            circleBitmap.recycle();
        }

        if (saved) {
            Intent result = new Intent();
            result.putExtra(RESULT_PATH, avatarFile.getAbsolutePath());
            setResult(RESULT_OK, result);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        if (displayBitmap != null && displayBitmap != originalBitmap) {
            displayBitmap.recycle();
        }
        if (originalBitmap != null) {
            originalBitmap.recycle();
        }
        super.onDestroy();
    }
}
