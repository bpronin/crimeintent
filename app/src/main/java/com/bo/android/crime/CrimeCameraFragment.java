package com.bo.android.crime;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.ImageButton;
import com.bo.android.R;
import com.bo.android.crime.util.FileUtils;
import com.bo.android.crime.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
public class CrimeCameraFragment extends Fragment {

    public static final String EXTRA_PHOTO_FILENAME = CrimeCameraFragment.class + "photo_file_name";
    private static int counter;

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

    private void setupSurface(View view) {
        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.camera_surface_view);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new CameraSurfaceCallback());
    }

    private void setupProgressBar(View view) {
        progressContainer = view.findViewById(R.id.camera_progress_bar_container);
        progressContainer.setVisibility(View.INVISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onResume() {
        super.onResume();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                camera = Camera.open(0);
            } else {
                camera = Camera.open();
            }
        } catch (Exception x) {
            LogUtils.error(this, x);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
                LogUtils.error(this, x);
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
            if (camera == null) return;

            Camera.Parameters parameters = camera.getParameters();
            Camera.Size pvSize = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
            parameters.setPreviewSize(pvSize.width, pvSize.height);
            Camera.Size pcSize = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
            parameters.setPictureSize(pcSize.width, pcSize.height);
            camera.setParameters(parameters);

            try {
                camera.startPreview();
            } catch (Exception x) {
                LogUtils.error(this, x);
                camera.release();
                camera = null;
            }
        }

        private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
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
                    LogUtils.info(this, "JPEG saved at " + file);

                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_PHOTO_FILENAME, file.getName());
                    getActivity().setResult(Activity.RESULT_OK, intent);
                } catch (Exception x) {
                    LogUtils.error(this, "Error writing to file " + file, x);
                } finally {
                    try {
                        out.close();
                    } catch (Exception x) {
                        LogUtils.error(this, "Error closing file " + file, x);
                    }
                }
            } catch (Exception x) {
                LogUtils.error(this, "Error opening file stream" + file, x);
            }

            getActivity().finish();
        }
    }
}
