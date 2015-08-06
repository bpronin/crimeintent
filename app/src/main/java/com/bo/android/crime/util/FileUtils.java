package com.bo.android.crime.util;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import com.bo.android.crime.Photo;

import java.io.File;
import java.util.UUID;

public abstract class FileUtils {

    public static final String PHOTO_PREFIX = "crime_intent_";

    private FileUtils() {
    }

    @NonNull
    public static File getDataPath(Context context) {
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "test");
        path.mkdirs();
        return path;
//        return context.getFilesDir();
    }

    public static File createPhotoFile(Context context) {
        return new File(getDataPath(context), PHOTO_PREFIX + UUID.randomUUID().toString() + ".jpg");
    }

    public static File getPhotoFile(Context context, Photo photo) {
        return new File(getDataPath(context), photo.getFilename());
    }

}