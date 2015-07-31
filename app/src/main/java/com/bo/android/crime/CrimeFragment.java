package com.bo.android.crime;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.bo.R;

import java.util.UUID;

public class CrimeFragment extends Fragment {

    public static final String ITEM_ID = CrimeFragment.class + ".item_id";
    private static final String DATE_PATTERN = "yyy-MM-dd";

    private Crime document;
    private CheckBox solvedCheckBox;
    private Button dateButton;
    private EditText titleEditor;

    public static CrimeFragment newInstance(UUID crimeId) {
        CrimeFragment fragment = new CrimeFragment();

        Bundle args = new Bundle();
        args.putSerializable(ITEM_ID, crimeId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID)getArguments().getSerializable(ITEM_ID);
        document = CrimeLab.getInstance().getById(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        createTitleEditor(view);
        createDateButton(view);
        createSolvedCheckBox(view);

        loadDocument();

        return view;
    }

    private void createTitleEditor(View view) {
        titleEditor = (EditText) view.findViewById(R.id.crime_title);
        titleEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* do nothing so far */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                document.setTitle(titleEditor.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                /* do nothing so far */
            }
        });
    }

    private void createDateButton(View view) {
        dateButton = (Button) view.findViewById(R.id.crime_date);
        dateButton.setEnabled(false);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                document.setDate();
            }
        });
    }

    private void createSolvedCheckBox(View view) {
        solvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                document.setSolved(solvedCheckBox.isChecked());
            }
        });
    }

    private void loadDocument() {
        titleEditor.setText(document.getTitle());
        dateButton.setText(DateFormat.format(DATE_PATTERN, document.getDate()));
        solvedCheckBox.setChecked(document.isSolved());
    }

}
