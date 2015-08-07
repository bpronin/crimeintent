package com.bo.android.crime;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.bo.android.crime.util.ContactUtils;
import com.bo.android.crime.util.FileUtils;
import com.bo.android.crime.util.PictureUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.provider.ContactsContract.CommonDataKinds.Phone;
import static android.provider.ContactsContract.Contacts;

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
    private ImageButton dialSuspectButton;
    private Callbacks callbacks;

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
        setupDialSuspectButton(view);

        return view;
    }

    private void setupDialSuspectButton(View view) {
        dialSuspectButton = (ImageButton) view.findViewById(R.id.crime_dial_suspect_button);
        dialSuspectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialSuspect();
            }
        });
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
                callbacks.onItemUpdated(document);
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
                callbacks.onItemUpdated(document);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        callbacks = null;
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_DATE:
                    updateDocumentDate(data);
                    break;
                case REQUEST_PHOTO:
                    updateDocumentPhoto(data);
                    break;
                case REQUEST_CONTACT:
                    updateDocumentSuspect(data);
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
        updateUI();
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

    private void updateUI() {
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

        String suspect = document.getSuspect();
        boolean hasSuspect = suspect != null && !suspect.equals("");
        if (hasSuspect) {
            suspectButton.setText(ContactUtils.getContactDisplayName(getActivity(), suspect));
        } else {
            suspectButton.setText(R.string.crime_suspect_text);
        }

        dialSuspectButton.setEnabled(hasSuspect && hasActivities(getActivity(), createDialIntent()));
    }

    private void updateDocumentPhoto(Intent data) {
        String fileName = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
        if (fileName != null) {
            Photo photo = new Photo();
            photo.setFilename(fileName);
            document.setPhoto(photo);
            updateUI();
            callbacks.onItemUpdated(document);
        }
    }

    private void updateDocumentDate(Intent data) {
        Date date = (Date) data.getSerializableExtra(DatePickerFragment.DATE_VALUE);
        document.setDate(date);
        updateUI();
        callbacks.onItemUpdated(document);
    }

    private void updateDocumentSuspect(Intent data) {
/*
        Uri contactUri = data.getData();
        String[] fields = new String[]{Contacts._ID, Contacts.DISPLAY_NAME, Contacts.PHONETIC_NAME};

        Cursor cursor = getActivity().getContentResolver().query(contactUri, fields, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            document.setSuspect(cursor.getString(0));
        }
        cursor.close();
*/
        String contactId = data.getData().getLastPathSegment();
        document.setSuspect(contactId);
        updateUI();
        callbacks.onItemUpdated(document);
    }

    private void pickSuspect() {
        startActivityForResult(createPickContactIntent(), REQUEST_CONTACT);
    }

    private void dialSuspect() {
        Intent intent = createDialIntent();
        intent.setData(Uri.parse("tel:" + ContactUtils.getContactPreferredPhoneNumber(getActivity(), document.getSuspect(),
                Phone.TYPE_WORK, Phone.TYPE_MOBILE, Phone.TYPE_HOME)));
        /* intent.setData(Uri.parse("content://contacts/people/" + document.getSuspect())); DOES NOT WORK */
        startActivity(intent);
    }

    private String createReport() {
        String solvedString = document.isSolved() ?
                getString(R.string.crime_report_solved) :
                getString(R.string.crime_report_unsolved);

        String dateString = DateFormat.format("EEE, MMM dd", document.getDate()).toString();

        String suspect = document.getSuspect() == null ?
                getString(R.string.crime_report_no_suspect) :
                getString(R.string.crime_report_suspect, ContactUtils.getContactDisplayName(getActivity(), document.getSuspect()));

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
    private Intent createDialIntent() {
        return new Intent(Intent.ACTION_DIAL);
    }

    @NonNull
    private Intent createPickContactIntent() {
        return new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
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

    public interface Callbacks {

        void onItemUpdated(Crime crime);
    }
}
