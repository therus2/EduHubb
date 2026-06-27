package com.example.eduhub.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AvatarManager {

    private static final int MAX_SIZE = 512;
    private static final int COMPRESS_QUALITY = 80;
    private static final String AVATAR_DIR = "avatars";

    public static File getAvatarFile(Context context, String userId) {
        File dir = new File(context.getFilesDir(), AVATAR_DIR);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, userId + ".jpg");
    }

    public static String getAvatarPath(Context context, String userId) {
        return getAvatarFile(context, userId).getAbsolutePath();
    }

    public static boolean hasAvatar(Context context, String userId) {
        return getAvatarFile(context, userId).exists();
    }

    public static boolean deleteAvatar(Context context, String userId) {
        return getAvatarFile(context, userId).delete();
    }

    public static void clearAvatarView(android.content.Context context, ImageView target) {
        Glide.with(context).clear(target);
        target.setImageDrawable(null);
    }

    public static void loadAvatarInto(android.content.Context context, ImageView target, String path) {
        if (path == null || path.isEmpty() || "default_avatar".equals(path)) {
            clearAvatarView(context, target);
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            clearAvatarView(context, target);
            return;
        }
        Glide.with(context).clear(target);
        Glide.with(context)
                .load(file)
                .signature(new ObjectKey(file.lastModified()))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transform(new CircleCrop())
                .into(target);
    }

    public static Bitmap decodeUriToBitmap(Context context, Uri uri, int maxSize) {
        try {
            ContentResolver resolver = context.getContentResolver();
            int orientation = ExifInterface.ORIENTATION_NORMAL;
            InputStream exifStream = resolver.openInputStream(uri);
            if (exifStream != null) {
                try {
                    ExifInterface exif = new ExifInterface(exifStream);
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                } finally {
                    exifStream.close();
                }
            }

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            InputStream is = resolver.openInputStream(uri);
            if (is == null) return null;
            BitmapFactory.decodeStream(is, null, opts);
            is.close();

            int sampleSize = 1;
            while (opts.outWidth / sampleSize > maxSize || opts.outHeight / sampleSize > maxSize) {
                sampleSize *= 2;
            }

            BitmapFactory.Options decodeOpts = new BitmapFactory.Options();
            decodeOpts.inSampleSize = sampleSize;
            is = resolver.openInputStream(uri);
            if (is == null) return null;
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, decodeOpts);
            is.close();

            if (bitmap == null) return null;

            bitmap = applyExifOrientation(bitmap, orientation);

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            if (w <= maxSize && h <= maxSize) return bitmap;

            float scale = Math.min((float) maxSize / w, (float) maxSize / h);
            Matrix m = new Matrix();
            m.setScale(scale, scale);
            Bitmap scaled = Bitmap.createBitmap(bitmap, 0, 0, w, h, m, true);
            if (scaled != bitmap) bitmap.recycle();
            return scaled;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap rotateBitmap(Bitmap src, int degrees) {
        if (src == null || degrees == 0) return src;
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap rotated = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return rotated;
    }

    private static Bitmap applyExifOrientation(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.postRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.postRotate(270);
                matrix.postScale(-1, 1);
                break;
            default:
                return bitmap;
        }
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (result != bitmap) bitmap.recycle();
        return result;
    }

    public static Bitmap cropToCircle(Bitmap src) {
        int size = Math.min(src.getWidth(), src.getHeight());
        int x = (src.getWidth() - size) / 2;
        int y = (src.getHeight() - size) / 2;

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Matrix matrix = new Matrix();
        matrix.preTranslate(-x, -y);
        paint.getShader().setLocalMatrix(matrix);

        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        return output;
    }

    public static Bitmap cropRegion(Bitmap src, float cropX, float cropY, float cropSize) {
        int bx = Math.round(cropX);
        int by = Math.round(cropY);
        int bSize = Math.round(cropSize);

        if (bx < 0) bx = 0;
        if (by < 0) by = 0;
        if (bx + bSize > src.getWidth()) bSize = src.getWidth() - bx;
        if (by + bSize > src.getHeight()) bSize = src.getHeight() - by;

        if (bSize <= 0) return null;

        Bitmap cropped = Bitmap.createBitmap(src, bx, by, bSize, bSize);
        return cropToCircle(cropped);
    }

    public static boolean saveBitmap(Bitmap bitmap, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
