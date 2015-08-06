package com.bo.android.crime;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.bo.android.crime.util.FileUtils;

import java.io.File;

/*
public class CrimeCameraActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }

}
*/

public class CrimeCameraActivity extends Activity {

    private static final int REQUEST_MAKE_PHOTO = 100;

    private File outputFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        outputFile = FileUtils.createPhotoFile(this);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
        startActivityForResult(intent, REQUEST_MAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME, outputFile.getName());
            setResult(Activity.RESULT_OK, intent);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

}

