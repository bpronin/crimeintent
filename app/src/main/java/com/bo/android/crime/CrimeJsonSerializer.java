package com.bo.android.crime;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import com.bo.android.crime.util.LogUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.Date;
import java.util.UUID;

public class CrimeJsonSerializer {

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_FILENAME = "filename";
    private static final String JSON_PHOTO = "photo";

    private final File file;
    private CrimeLab data;

    public CrimeJsonSerializer(CrimeLab data, Context context) {
        this.data = data;

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        path.mkdirs();

//        File path = context.getFilesDir();

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

        LogUtils.info(this, "File '" + file + "' saved");
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

            LogUtils.info(this, "File '" + file + "' loaded");
        } catch (FileNotFoundException x) {
            LogUtils.debug(this, "File does not exist");
        }
    }

    public JSONObject crimeToJson(Crime crime) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(JSON_ID, crime.getId().toString());
        json.put(JSON_TITLE, crime.getTitle());
        json.put(JSON_SOLVED, crime.isSolved());
        json.put(JSON_DATE, crime.getDate().getTime());

        Photo photo = crime.getPhoto();
        if (photo != null) {
            json.put(JSON_PHOTO, photoToJson(photo));
        }

        return json;
    }

    public JSONObject photoToJson(Photo photo) throws JSONException {
        JSONObject json = new JSONObject();
        json.putOpt(JSON_FILENAME, photo.getFilename());
        return json;
    }

    public Crime crimeFromJson(JSONObject json) throws JSONException {
        Crime crime = new Crime();

        crime.setId(UUID.fromString(json.getString(JSON_ID)));
        crime.setTitle(json.getString(JSON_TITLE));
        crime.setSolved(json.getBoolean(JSON_SOLVED));
        crime.setDate(new Date(json.getLong(JSON_DATE)));

        if (!json.isNull(JSON_PHOTO)) {
            crime.setPhoto(photoFromJson(json.getJSONObject(JSON_PHOTO)));
        }

        return crime;
    }

    @Nullable
    public Photo photoFromJson(JSONObject json) throws JSONException {
        Photo photo = new Photo();
        photo.setFilename(json.getString(JSON_FILENAME));
        return photo;
    }

}