package com.vadim.nakatani.fragments.home_screen;


import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.vadim.nakatani.DatabaseHelper;
import com.vadim.nakatani.NakataniApplication;
import com.vadim.nakatani.PatientListAdapter;
import com.vadim.nakatani.R;
import com.vadim.nakatani.entity.PatientEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class CardFileFragment extends Fragment implements TextWatcher {
    /* the fragment initialization parameters, e.g. ARG_ITEM_NUMBER*/
    private static final String SAVED_TEXT_KEY = "SavedText";
    private static final String IS_PATIENT_FRAGMENT_ACTIVE = "is patient fargment active";

    private String mCardFindAutoCompleteText;

    private AutoCompleteTextView mAutoCompleteTextView;
    private ListView mListViewPatientList;

    private PatientListAdapter arrayAdapter;

    private List<String> patientsList = new ArrayList<String>();
    private SQLiteDatabase newDB;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cardFindAutoCompleteText Text that was entered in autocomplete field
     * @return A new instance of fragment CardFileFragment
     */
    public static CardFileFragment newInstance(String cardFindAutoCompleteText) {
        CardFileFragment fragment = new CardFileFragment();
        Bundle args = new Bundle();
        args.putString(SAVED_TEXT_KEY, cardFindAutoCompleteText);
        fragment.setArguments(args);
        return fragment;
    }

    public CardFileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCardFindAutoCompleteText = getArguments().getString(SAVED_TEXT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_card_file, container, false);

        mAutoCompleteTextView = (AutoCompleteTextView)rootView.findViewById(R.id.autoCompleteTextView);
        mAutoCompleteTextView.addTextChangedListener(this);

        mListViewPatientList = (ListView) rootView.findViewById(R.id.patientListView);
        mListViewPatientList.setOnItemClickListener(new PatientListClickListener());

//        openAndQueryDatabase();
        //TODO load data from db

        ArrayList<PatientEntity> patientEntities = new ArrayList<PatientEntity>();
        PatientEntity patientEntity1 = new PatientEntity();
        patientEntity1.setCode("ПЕТ0001");
        patientEntity1.setLastName("Петренко");
        patientEntity1.setFirstName("Иван");
        patientEntity1.setMiddleName("Сидорович");
        PatientEntity patientEntity2 = new PatientEntity();
        patientEntity2.setCode("АНД0001");
        patientEntity2.setLastName("Андреев");
        patientEntity2.setFirstName("Игорь");
        patientEntity2.setMiddleName("Валерьевич");

        patientEntities.add(patientEntity1);
        patientEntities.add(patientEntity2);

        arrayAdapter = new PatientListAdapter(getActivity(), patientEntities);
        mListViewPatientList.setAdapter(arrayAdapter);

        mAutoCompleteTextView.setText(mCardFindAutoCompleteText);

        return rootView;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        arrayAdapter.getFilter().filter(charSequence);
        Log.e(this.getClass().getName(), "call with " + charSequence.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    /**
     * The click listener for ListView in the navigation drawer
     */
    private class PatientListClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NakataniApplication nakataniApplication = (NakataniApplication) getActivity().getApplicationContext();
            nakataniApplication.setPatientEntity(((PatientEntity)arrayAdapter.getItem(position)));
            Log.d(this.getClass().getName(), ((PatientEntity)arrayAdapter.getItem(position)).getLastName());
        }
    }

    private void openAndQueryDatabase() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
            dbHelper.createDataBase();
            newDB = dbHelper.getWritableDatabase();
            Cursor cursor = newDB.rawQuery("SELECT " + DatabaseHelper.CAT_NAME_COLUMN + " FROM " + DatabaseHelper.DB_TABLE, null);
            if (cursor != null ) {
                if  (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CAT_NAME_COLUMN));
                        patientsList.add(name);
                    }while (cursor.moveToNext());
                }
            }
        } catch (SQLiteException sqLiteException ) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Exception in DB HELPER when create database");
        } finally {
            if (newDB != null) newDB.close();
        }

    }
}
