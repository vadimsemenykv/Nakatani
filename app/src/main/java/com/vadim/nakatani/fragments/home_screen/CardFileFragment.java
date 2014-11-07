package com.vadim.nakatani.fragments.home_screen;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.vadim.nakatani.DatabaseHelper;
import com.vadim.nakatani.MainActivity;
import com.vadim.nakatani.R;
import com.vadim.nakatani.fragments.Leftmenu_private_info_blockFragment;

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
    private ListView mDrawerPatientList;

    private ArrayAdapter<String> arrayAdapter;

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
        mDrawerPatientList = (ListView) rootView.findViewById(R.id.patientListView);

        mAutoCompleteTextView.addTextChangedListener(this);
        mDrawerPatientList.setOnItemClickListener(new PatientListClickListener());

        openAndQueryDatabase();

//        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.drawer_navigation_list_item, contacts);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.drawer_navigation_list_item, patientsList);
        mDrawerPatientList.setAdapter(arrayAdapter);

        mAutoCompleteTextView.setText(mCardFindAutoCompleteText);

        return rootView;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        arrayAdapter.getFilter().filter(charSequence);
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
//            mAutoCompleteTextView.setText(arrayAdapter.getItem(position));
//            Intent intent = new Intent();
//            intent.setClass(getActivity() ,PatientCardActivity.class);
//            startActivity(intent);
            Fragment fragment = new Leftmenu_private_info_blockFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.private_info, fragment).commit();

            DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            LinearLayout mDrawerListContainer = (LinearLayout) getActivity().findViewById(R.id.left_drawer);

            mDrawerLayout.closeDrawer(mDrawerListContainer);

//            Bundle bundle = getActivity().getIntent().getExtras();
//            bundle.putBoolean(IS_PATIENT_FRAGMENT_ACTIVE, true);

            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.swapFragmentToPrivateInfo();

//            fragment = new PatientPrivateInfo();
//            fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.content_frame, fragment).commit();
//            mDrawerLayout.closeDrawer(mDrawerListContainer);

            Log.e(this.getClass().getName(), "selected = " + position + " id = " + id);
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
