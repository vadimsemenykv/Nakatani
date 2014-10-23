package com.vadim.nakatani.fragments;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.vadim.nakatani.R;
import com.vadim.nakatani.activitys.PatientCardActivity;


/**
 *
 */
public class CardFile extends Fragment implements TextWatcher {
    /* the fragment initialization parameters, e.g. ARG_ITEM_NUMBER*/
    private static final String SAVED_TEXT_KEY = "SavedText";

    private String mCardFindAutoCompleteText;

    private AutoCompleteTextView mAutoCompleteTextView;
    private ListView mDrawerPatientList;

    private ArrayAdapter<String> arrayAdapter;

    //TODO delete this array, in future work with data from SQLite
    final String[] contacts = {"Jacob", "Emily", "Jacym", "jacob", "Ernst", "Michael"};

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cardFindAutoCompleteText
     * @return A new instance of fragment CardFile
     */
    public static CardFile newInstance(String cardFindAutoCompleteText) {
        CardFile fragment = new CardFile();
        Bundle args = new Bundle();
        args.putString(SAVED_TEXT_KEY, cardFindAutoCompleteText);
        fragment.setArguments(args);
        return fragment;
    }

    public CardFile() {
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

        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.drawer_navigation_list_item, contacts);
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
            mAutoCompleteTextView.setText(arrayAdapter.getItem(position));
            Intent intent = new Intent();
            intent.setClass(getActivity() ,PatientCardActivity.class);
            startActivity(intent);

            Log.e(this.getClass().getName(), "selected = " + position + " id = " + id);
        }
    }
}
