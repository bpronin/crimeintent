package com.bo.android.crime;

import android.content.Context;
import com.bo.android.crime.util.LogUtil;
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

    private final File file;
    private CrimeLab data;

    public CrimeJsonSerializer(CrimeLab data, Context context) {
        this.data = data;

/*
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        path.mkdirs();
*/
        File path = context.getFilesDir();

        file = new File(path, "crime-data.json");
    }

    public void save() throws JSONException, IOException {
        JSONArray json = new JSONArray();
        for (Crime crime : data.getItems()) {
            json.put(toJson(crime));
        }

        /* OutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE); */
        OutputStream out = new FileOutputStream(file);
        Writer writer = new OutputStreamWriter(out);
        try {
            writer.write(json.toString());
        } finally {
            writer.close();
        }

        LogUtil.info(this, "File '" + file + "' saved");
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
                data.addItem(fromJson(array.getJSONObject(i)));
            }

            LogUtil.info(this, "File '" + file + "' loaded");
        } catch (FileNotFoundException x) {
            LogUtil.debug(this, "File does not exist");
        }
    }


    public JSONObject toJson(Crime crime) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, crime.getId().toString());
        json.put(JSON_TITLE, crime.getTitle());
        json.put(JSON_SOLVED, crime.isSolved());
        json.put(JSON_DATE, crime.getDate().getTime());

        return json;
    }

    public Crime fromJson(JSONObject json) throws JSONException {
        Crime crime = new Crime();
        crime.setId(UUID.fromString(json.getString(JSON_ID)));
        crime.setTitle(json.getString(JSON_TITLE));
        crime.setSolved(json.getBoolean(JSON_SOLVED));
        crime.setDate(new Date(json.getLong(JSON_DATE)));

        return crime;
    }

}