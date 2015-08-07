package com.bo.android.crime;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import com.bo.android.R;
import com.bo.android.crime.util.FileUtils;
import com.bo.android.crime.util.PictureUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class CrimeCameraFragment extends Fragment {

    private static final String TAG = CrimeCameraFragment.class.getName();
    public static final String EXTRA_PHOTO_FILENAME = CrimeCameraFragment.class + "photo_file_name";
    public static final int CAMERA_ID = 0;

    private Camera camera;
    private View progressContainer;
    private final Camera.ShutterCallback shutterCallback = new CameraShutterCallback();
    private final Camera.PictureCallback jpegCallback = new CameraPictureCallback();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_camera, parent, false);

        setupButton(view);
        setupSurface(view);
        setupProgressBar(view);

        return view;
    }

    private void setupButton(View view) {
        ImageButton takePictureButton = (ImageButton) view.findViewById(R.id.camera_take_button);
        takePictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (camera != null) {
                    camera.takePicture(shutterCallback, null, jpegCallback);
                }
            }
        });
    }

    private void setupProgressBar(View view) {
        progressContainer = view.findViewById(R.id.camera_progress_bar_container);
        progressContainer.setVisibility(View.INVISIBLE);
    }

    private void setupSurface(View view) {
        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.camera_surface_view);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new CameraSurfaceCallback());
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onResume() {
        super.onResume();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                camera = Camera.open(CAMERA_ID);
            } else {
                camera = Camera.open();
            }
        } catch (Exception x) {
            Log.e(TAG, x.getMessage(), x);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private class CameraSurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if (camera != null) {
                    camera.setPreviewDisplay(holder);
                }
            } catch (IOException x) {
                Log.e(TAG, x.getMessage(), x);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (camera != null) {
                Log.d(TAG, "surfaceChanged ");

                try {
                    camera.stopPreview();
                } catch (Exception x) {
                    Log.i(TAG, "Cannot stop camera preview: " + x.getMessage());
                }

                Camera.Parameters params = camera.getParameters();
                Camera.Size pvSize = PictureUtils.getCameraBestSupportedSize(params.getSupportedPreviewSizes(), width, height);
                params.setPreviewSize(pvSize.width, pvSize.height);
                Camera.Size pcSize = PictureUtils.getCameraBestSupportedSize(params.getSupportedPictureSizes(), width, height);
                params.setPictureSize(pcSize.width, pcSize.height);
                camera.setParameters(params);

                PictureUtils.updateCameraDisplayOrientation(getActivity(), CAMERA_ID, camera);

                try {
                    camera.startPreview();
                } catch (Exception x) {
                    Log.d(TAG, "Error starting camera preview: " + x.getMessage());
                    releaseCamera();
                }
            }
        }

    }

    private class CameraShutterCallback implements Camera.ShutterCallback {

        @Override
        public void onShutter() {
            progressContainer.setVisibility(View.VISIBLE);
        }
    }

    private class CameraPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
//            String filename = UUID.randomUUID().toString() + ".jpg";
//            boolean success = true;
//            FileOutputStream os = null;
//            try {
//                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
//                os.write(data);
//            } catch (Exception x) {
//                LogUtils.error(this, "Error writing to file " + filename, x);
//                success = false;
//            } finally {
//                try {
//                    if (os != null) {
//                        os.close();
//                    }
//                } catch (Exception x) {
//                    LogUtils.error(this, "Error closing file " + filename, x);
//                    success = false;
//                }
//            }
//
//            if (success) {
//                LogUtils.info(this, "JPEG saved at " + filename);
//            }

            getActivity().setResult(Activity.RESULT_CANCELED);

            File file = FileUtils.createPhotoFile(getActivity());
            try {
                FileOutputStream out = new FileOutputStream(file);
                try {
                    out.write(data);
                    Log.i(TAG, "JPEG saved at " + file);

                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_PHOTO_FILENAME, file.getName());
                    getActivity().setResult(Activity.RESULT_OK, intent);
                } catch (Exception x) {
                    Log.e(TAG, "Error writing to file " + file, x);
                } finally {
                    try {
                        out.close();
                    } catch (Exception x) {
                        Log.e(TAG, "Error closing file " + file, x);
                    }
                }
            } catch (Exception x) {
                Log.e(TAG, "Error opening file stream" + file, x);
            }

            getActivity().finish();
        }
    }
}
