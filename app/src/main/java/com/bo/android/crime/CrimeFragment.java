package com.bo.android.crime;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_PHOTO = 1;
    public static final int REQUEST_CONTACT = 2;
    public static final String EXTRA_ITEM_ID = CrimeFragment.class + ".item_id";
    private static final String DATE_PATTERN = "yyy-MM-dd";
    private static final String DIALOG_IMAGE = "image";

    private Crime document;
    private CheckBox solvedCheckBox;
    private Button dateButton;
    private EditText titleEditor;
    private CrimeLab store;
    private ImageView photoPreview;
    private Button suspectButton;

    public static CrimeFragment newInstance(UUID crimeId) {
        CrimeFragment fragment = new CrimeFragment();

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ITEM_ID, crimeId);
        fragment.setArguments(args);

        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        store = CrimeLab.getInstance(getActivity());
        document = store.getById((UUID) getArguments().getSerializable(EXTRA_ITEM_ID));
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
        setupReportButton(view);
        setupPickSuspectButton(view);

        return view;
    }

    private void setupPickSuspectButton(View view) {
        suspectButton = (Button) view.findViewById(R.id.crime_suspect_button);
        suspectButton.setEnabled(hasActivities(getActivity(), createPickContactIntent()));
        suspectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pickSuspect();
            }
        });
    }

    private void setupReportButton(View view) {
        Button button = (Button) view.findViewById(R.id.crime_report_button);
        button.setEnabled(hasActivities(getActivity(), createSendToIntent()));
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendReport();
            }
        });
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
                    showDateEditor(data);
                    break;
                case REQUEST_PHOTO:
                    updatePhoto(data);
                    break;
                case REQUEST_CONTACT:
                    updateSuspect(data);
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
            case R.id.menu_item_new_crime:
                addItem();
                return true;
            case R.id.menu_item_remove_crime:
                removeItem();
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

    private void goBack() {
        if (NavUtils.getParentActivityName(getActivity()) != null) {
            NavUtils.navigateUpFromSameTask(getActivity());
        }
    }

    private void addItem() {
        Crime crime = new Crime();
        store.add(crime);

        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_ITEM_ID, crime.getId());
        startActivity(intent);
    }

    private void removeItem() {
        store.remove(document);
        goBack();
    }

    private void updateControls() {
        titleEditor.setText(document.getTitle());
        dateButton.setText(DateFormat.format(DATE_PATTERN, document.getDate()));
        solvedCheckBox.setChecked(document.isSolved());

        Bitmap bitmap = null;
        Photo photo = document.getPhoto();
        if (photo != null) {
            String photoPath = FileUtils.getPhotoFile(getActivity(), photo).getAbsolutePath();
            bitmap = PictureUtils.getScaledRotatedBitmap(photoPath, getActivity());
        }
        photoPreview.setImageBitmap(bitmap);

        if (!"".equals(document.getSuspect())) {
            suspectButton.setText(document.getSuspect());
        } else {
            suspectButton.setText(R.string.crime_suspect_text);
        }
    }

    private void updatePhoto(Intent data) {
        String fileName = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
        if (fileName != null) {
            Photo photo = new Photo();
            photo.setFilename(fileName);
            document.setPhoto(photo);
            updateControls();
        }
    }

    private void showDateEditor(Intent data) {
        Date date = (Date) data.getSerializableExtra(DatePickerFragment.DATE_VALUE);
        document.setDate(date);
        updateControls();
    }

    private void updateSuspect(Intent data) {
        Uri contactUri = data.getData();
        String[] fields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};

        Cursor cursor = getActivity().getContentResolver().query(contactUri, fields, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            document.setSuspect(cursor.getString(0));
            cursor.close();
            updateControls();
        } else {
            cursor.close();
        }
    }

    private void pickSuspect() {
        startActivityForResult(createPickContactIntent(), REQUEST_CONTACT);
    }

    private String createReport() {
        String solvedString = document.isSolved() ?
                getString(R.string.crime_report_solved) :
                getString(R.string.crime_report_unsolved);

        String dateString = DateFormat.format("EEE, MMM dd", document.getDate()).toString();

        String suspect = document.getSuspect() == null ?
                getString(R.string.crime_report_no_suspect) :
                getString(R.string.crime_report_suspect, document.getSuspect());

        return getString(R.string.crime_report, document.getTitle(), dateString, solvedString, suspect);
    }

    private void sendReport() {
        Intent intent = createSendToIntent();
        intent.putExtra(Intent.EXTRA_TEXT, createReport());
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
        intent = Intent.createChooser(intent, getString(R.string.send_report));

        startActivity(intent);
    }

    @NonNull
    private Intent createPickContactIntent() {
        return new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    }

    @NonNull
    private Intent createSendToIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        return intent;
    }

    private boolean hasActivities(Activity activity, Intent intent) {
        List<ResolveInfo> activities = activity.getPackageManager().queryIntentActivities(intent, 0);
        return activities.size() > 0;
    }

}
