package com.bo.android.crime;

import android.graphics.drawable.BitmapDrawable;
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
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);
        imageView = new ImageView(getActivity());
        imageView.setImageDrawable(image);
        return imageView;
    }

    @Override
    public void onDestroyView() {
        PictureUtils.cleanImageView(imageView);
        super.onDestroyView();
    }

}
