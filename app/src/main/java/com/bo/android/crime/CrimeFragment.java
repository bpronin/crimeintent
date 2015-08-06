package com.bo.android.crime;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.*;
import android.widget.*;
import com.bo.android.R;
import com.bo.android.crime.util.ActionBarUtils;
import com.bo.android.crime.util.FileUtils;
import com.bo.android.crime.util.PictureUtils;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    public static final String ITEM_ID = CrimeFragment.class + ".item_id";
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_PHOTO = 1;
    private static final String DATE_PATTERN = "yyy-MM-dd";
    private static final String DIALOG_IMAGE = "image";

    private Crime document;
    private CheckBox solvedCheckBox;
    private Button dateButton;
    private EditText titleEditor;
    private CrimeLab store;
    private ImageView photoPreview;

    public static CrimeFragment newInstance(UUID crimeId) {
        CrimeFragment fragment = new CrimeFragment();

        Bundle args = new Bundle();
        args.putSerializable(ITEM_ID, crimeId);
        fragment.setArguments(args);

        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        store = CrimeLab.getInstance(getActivity());
        document = store.getById((UUID) getArguments().getSerializable(ITEM_ID));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        setupActionBar();
        setupTitleEditor(view);
        setupDateButton(view);
        setupSolvedCheckBox(view);
        setupCameraButton(view);
        setupPhotoPreview(view);

        return view;
    }

    private void setupPhotoPreview(View view) {
        photoPreview = (ImageView) view.findViewById(R.id.crime_photo_preview);
        photoPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Photo photo = document.getPhoto();
                if (photo != null) {
                    String photoPath = FileUtils.getPhotoFile(getActivity(), photo).getAbsolutePath();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    ImageFragment.newInstance(photoPath).show(fm, DIALOG_IMAGE);
                }
            }
        });
    }

    private void setupActionBar() {
        if (NavUtils.getParentActivityName(getActivity()) != null) {
            ActionBarUtils.setDisplayHomeAsUpEnabled(getActivity(), true);
        }
    }

    private void setupTitleEditor(View view) {
        titleEditor = (EditText) view.findViewById(R.id.crime_title);
        titleEditor.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                document.setTitle(titleEditor.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* do nothing */
            }

            @Override
            public void afterTextChanged(Editable s) {
                /* do nothing */
            }
        });
    }

    private void setupDateButton(View view) {
        dateButton = (Button) view.findViewById(R.id.crime_date);
        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(document.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, "date_dialog");
            }
        });
    }

    private void setupSolvedCheckBox(View view) {
        solvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                document.setSolved(solvedCheckBox.isChecked());
            }
        });
    }

    private void setupCameraButton(View view) {
        ImageButton photoButton = (ImageButton) view.findViewById(R.id.crime_camera_button);

        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });

        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            photoButton.setEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_DATE:
                    Date date = (Date) data.getSerializableExtra(DatePickerFragment.DATE_VALUE);
                    document.setDate(date);
                    updateControls();
                    break;
                case REQUEST_PHOTO:
                    String fileName = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
                    if (fileName != null) {
                        Photo photo = new Photo();
                        photo.setFilename(fileName);
                        document.setPhoto(photo);
                        updateControls();
                    }
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onGoBack();
                return true;
            case R.id.menu_item_remove_crime:
                onRemoveItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateControls();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(photoPreview);
    }

    @Override
    public void onPause() {
        super.onPause();
        store.save();
    }

    private void onGoBack() {
        if (NavUtils.getParentActivityName(getActivity()) != null) {
            NavUtils.navigateUpFromSameTask(getActivity());
        }
    }

    private void onRemoveItem() {
        store.remove(document);
        onGoBack();
    }

    private void updateControls() {
        titleEditor.setText(document.getTitle());
        dateButton.setText(DateFormat.format(DATE_PATTERN, document.getDate()));
        solvedCheckBox.setChecked(document.isSolved());

        BitmapDrawable bitmap = null;
        Photo photo = document.getPhoto();
        if (photo != null) {
            String photoPath = FileUtils.getPhotoFile(getActivity(), photo).getAbsolutePath();
            bitmap = PictureUtils.getScaledDrawable(getActivity(), photoPath);
        }
        photoPreview.setImageDrawable(bitmap);
    }

}
