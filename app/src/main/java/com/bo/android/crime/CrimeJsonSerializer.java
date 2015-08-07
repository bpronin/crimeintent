package com.bo.android.crime;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import com.bo.android.crime.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.Date;
import java.util.UUID;

public class CrimeJsonSerializer {

    private static final String TAG = "CrimeJsonSerializer";

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_FILENAME = "filename";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_SUSPECT = "suspect";

    private final File file;
    private final File path;
    private CrimeLab data;

    public CrimeJsonSerializer(CrimeLab data, Context context) {
        this.data = data;
        path = FileUtils.getDataPath(context);
        file = new File(path, "crime-data.json");
    }

    public void save() throws JSONException, IOException {
        JSONArray json = new JSONArray();
        for (Crime crime : data.getAll()) {
            json.put(crimeToJson(crime));
        }

        /* OutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE); */
        OutputStream out = new FileOutputStream(file);
        Writer writer = new OutputStreamWriter(out);
        try {
            writer.write(json.toString());
        } finally {
            writer.close();
        }

        Log.i(TAG, "File '" + file + "' saved");

        purgePhotos();
    }

    public void load() throws JSONException, IOException {
        try {
            /* FileInputStream in = context.openFileInput(fileName); */
            FileInputStream in = new FileInputStream(file);

            StringBuilder json = new StringBuilder();
            String line;

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            try {
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
            } finally {
                reader.close();
            }

            JSONArray array = (JSONArray) new JSONTokener(json.toString()).nextValue();

            data.clear();
            for (int i = 0; i < array.length(); i++) {
                data.add(crimeFromJson(array.getJSONObject(i)));
            }

            Log.i(TAG, "File '" + file + "' loaded");
        } catch (FileNotFoundException x) {
            Log.d(TAG, "File does not exist");
        }
    }

    public JSONObject crimeToJson(Crime crime) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(JSON_ID, crime.getId().toString());
        json.putOpt(JSON_TITLE, crime.getTitle());
        json.putOpt(JSON_SOLVED, crime.isSolved());
        json.putOpt(JSON_DATE, crime.getDate().getTime());
        json.putOpt(JSON_SUSPECT, crime.getSuspect());
        json.putOpt(JSON_PHOTO, photoToJson(crime.getPhoto()));

        return json;
    }

    public JSONObject photoToJson(Photo photo) throws JSONException {
        if (photo != null) {
            JSONObject json = new JSONObject();
            json.putOpt(JSON_FILENAME, photo.getFilename());
            return json;
        } else {
            return null;
        }
    }

    public Crime crimeFromJson(JSONObject json) throws JSONException {
        Crime crime = new Crime();

        crime.setId(UUID.fromString(json.getString(JSON_ID)));
        crime.setTitle(json.optString(JSON_TITLE));
        crime.setSolved(json.optBoolean(JSON_SOLVED));
        crime.setDate(new Date(json.getLong(JSON_DATE)));
        crime.setSuspect(json.optString(JSON_SUSPECT));
        crime.setPhoto(photoFromJson(json.optJSONObject(JSON_PHOTO)));

        return crime;
    }

    @Nullable
    public Photo photoFromJson(JSONObject json) throws JSONException {
        if (json.has(JSON_FILENAME)) {
            Photo photo = new Photo();
            photo.setFilename(json.getString(JSON_FILENAME));
            return photo;
        }else{
            return null;
        }
    }

    private void purgePhotos() {
        File[] photos = path.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith(FileUtils.PHOTO_PREFIX);
            }
        });

        for (File file : photos) {
            if (!hasItemForPhoto(file.getName())) {
                if (!file.delete()) {
                    Log.w(TAG, "Cannot delete file '" + file + "'");
                } else {
                    Log.i(TAG, "Removed image file '" + file + "'");
                }
            }
        }
    }

    private boolean hasItemForPhoto(String photoFilename) {
        for (Crime crime : data.getAll()) {
            Photo photo = crime.getPhoto();
            if (photo != null && photoFilename.equals(photo.getFilename())) {
                return true;
            }
        }
        return false;
    }

}