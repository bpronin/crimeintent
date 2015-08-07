package com.bo.android.crime;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bo.android.crime.util.PictureUtils;

public class ImageFragment extends DialogFragment {

    public static final String EXTRA_IMAGE_PATH = ImageFragment.class + ".image_path";
    private ImageView imageView;

    public static ImageFragment newInstance(String imagePath) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(EXTRA_IMAGE_PATH, imagePath);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        imageView = new ImageView(getActivity());
//        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setLayoutParams(new ViewGroup.LayoutParams(800, 800));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        Bitmap bitmap = PictureUtils.getScaledRotatedBitmap(path, getActivity());
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
        return imageView;
    }

    @Override
    public void onDestroyView() {
        PictureUtils.cleanImageView(imageView);
        super.onDestroyView();
    }

}
