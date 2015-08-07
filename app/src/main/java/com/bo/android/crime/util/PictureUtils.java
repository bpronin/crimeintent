package com.bo.android.crime.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

public abstract class PictureUtils {

    private static final String TAG = "PictureUtils";

    private PictureUtils() {
    }

    public static void cleanImageView(ImageView view) {
        if (view.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) view.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            if (bitmap != null) {
                bitmap.recycle();
            }
            view.setImageDrawable(null);
        }
    }

    /*
        @SuppressWarnings("deprecation")
        public static BitmapDrawable getScaledDrawable(Activity a, String path) {
            try {
                ExifInterface exif = new ExifInterface(path);
                String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            } catch (IOException x) {
                LogUtils.error(PictureUtils.class, x);
            }

            Display display = a.getWindowManager().getDefaultDisplay();
            float destWidth = display.getWidth();
            float destHeight = display.getHeight();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            float srcWidth = options.outWidth;
            float srcHeight = options.outHeight;

            int inSampleSize = 1;
            if (srcHeight > destHeight || srcWidth > destWidth) {
                if (srcWidth > srcHeight) {
                    inSampleSize = Math.round(srcHeight / destHeight);
                } else {
                    inSampleSize = Math.round(srcWidth / destWidth);
                }
            }

            options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);

            return new BitmapDrawable(a.getResources(), bitmap);
        }
    */

    public static Bitmap getScaledBitmap(String path, Point target) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / target.x, photoH / target.y);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(path, bmOptions);
    }

    public static Bitmap getScaledRotatedBitmap(String path, Point size) {
        Bitmap bitmap = getScaledBitmap(path, size);

        Matrix matrix = new Matrix();
        matrix.postRotate(getRotationAngle(path));
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return bitmap;
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        // Get the dimensions of the View
        Point target = getScreenSize(activity);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / target.x, photoH / target.y);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(path, bmOptions);
    }

    public static Bitmap getScaledRotatedBitmap(String path, Activity activity) {
        Matrix matrix = new Matrix();
        matrix.postRotate(getRotationAngle(path));

        Bitmap bitmap = getScaledBitmap(path, activity);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return bitmap;
    }

    public static float getRotationAngle(String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        } catch (IOException x) {
            Log.e(TAG, x.getMessage(), x);
        }

        return 0;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @NonNull
    public static Point getScreenSize(Activity activity) {
        Point p = new Point();
        Display display = activity.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(p);
        } else {
            p.set(display.getWidth(), display.getHeight());
        }
        return p;
    }

    @SuppressWarnings("deprecation")
    public static Camera.Size getCameraBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size size : sizes) {
            int area = size.width * size.height;
            if (area > largestArea) {
                bestSize = size;
                largestArea = area;
            }
        }
        return bestSize;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("deprecation")
    public static void updateCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
    }
}
