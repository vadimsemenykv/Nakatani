package com.vadim.nakatani;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.widget.DrawerLayout;
import android.view.MenuInflater;

import com.vadim.nakatani.activitys.MyActivity;
import com.vadim.nakatani.entity.PatientEntity;
import com.vadim.nakatani.fragments.home_screen.CardFileFragment;
import com.vadim.nakatani.fragments.home_screen.MedicalHistoryFragment;
import com.vadim.nakatani.fragments.PatientPrivateInfo;
import com.vadim.nakatani.fragments.home_screen.TestingFragment;


public class MainActivity extends ActionBarActivity {

    private String[] mScreenTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout mDrawerListContainer;

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private int mCurrentSelectedPosition;
    private String cardFindAutoCompleteText;

    private boolean isPatientFragmentWasActive;
    private PatientEntity patientEntity;

    private static final String IS_PATIENT_FRAGMENT_ACTIVE = "is patient fargment active";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String SAVED_TEXT_KEY = "SavedText";
//    private static final String IS_PATIENT_NOT_NULL = "patient not null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         *  Restore arguments from bundle
         *  mCurrentSelectedPosition
         *  cardFindAutoCompleteText for CardFileFragment fragment
         */
        mCurrentSelectedPosition = ((savedInstanceState != null) && savedInstanceState.containsKey(ARG_SECTION_NUMBER))?savedInstanceState.getInt(ARG_SECTION_NUMBER):0;
        cardFindAutoCompleteText = ((savedInstanceState != null) && savedInstanceState.containsKey(SAVED_TEXT_KEY))?savedInstanceState.getString(SAVED_TEXT_KEY):"";
        isPatientFragmentWasActive = ((savedInstanceState != null) && savedInstanceState.containsKey(IS_PATIENT_FRAGMENT_ACTIVE))?savedInstanceState.getBoolean(IS_PATIENT_FRAGMENT_ACTIVE):false;

        mScreenTitles = getResources().getStringArray(R.array.screen_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_lw);

        mDrawerListContainer = (LinearLayout) findViewById(R.id.left_drawer);

        /**
         * Set the adapter for the list view
         * Set the list's click listener
         */
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_navigation_list_item, mScreenTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mTitle = mDrawerTitle = getTitle();

        mDrawerToggle = new ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
                R.string.navigation_drawer_open, /* "open drawer" description */
                R.string.navigation_drawer_close /* "close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
            }
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
            }
        };

        /* Set the drawer toggle as the DrawerListener*/
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        if(isPatientFragmentWasActive) {
            swapFragmentToPrivateInfo();
        } else {
            selectItem(mCurrentSelectedPosition);
        }
//        Log.e(this.getClass().getName(), "from onCreate mCurrentSelectedPosition = " + mCurrentSelectedPosition);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu*/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
//        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /* If the nav drawer is open, hide action items related to the content view*/
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerListContainer);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * Pass the event to ActionBarDrawerToggle, if it returns
         * true, then it has handled the app icon touch event
         */
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        /* Handle action buttons*/
        switch(item.getItemId()) {
            case R.id.action_search:
                // Show toast about click.
                Toast.makeText(this, R.string.action_search, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
      * Per the navigation drawer design guidelines, updates the action bar to show the global app
      * 'context', rather than just what's in the current screen.
      */
//    private void showGlobalContextActionBar() {
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setTitle(R.string.app_name);
//    }

    /**
     * The click listener for ListView in the navigation drawer
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            Log.e(this.getClass().getName(), "selected = " + position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    public void swapFragmentToPrivateInfo() {
        isPatientFragmentWasActive = true;
        Fragment fragment = new PatientPrivateInfo();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment).commit();
        mDrawerLayout.closeDrawer(mDrawerListContainer);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        /* Update the main content by replacing fragments*/
        mCurrentSelectedPosition = position;
        isPatientFragmentWasActive = false;
//        Log.e(this.getClass().getName(), "from selectItem mCurrentSelectedPosition = " + mCurrentSelectedPosition);
        Fragment fragment = null;
//        fragment = ShowDataFromUSB.newInstance(this);
        position = 2;
        switch (position) {
            case 0:
                mTitle = mScreenTitles[mCurrentSelectedPosition];
                fragment = CardFileFragment.newInstance(cardFindAutoCompleteText);
                break;
            case 1:
                mTitle = mScreenTitles[mCurrentSelectedPosition];
                cardFindAutoCompleteText = "";
                fragment = new MedicalHistoryFragment();
                break;
            case 2:
//                mTitle = mScreenTitles[mCurrentSelectedPosition];
//                cardFindAutoCompleteText = "";
//                fragment = new DiagnosticsFragment();
                Intent intent = new Intent();
            intent.setClass(this ,MyActivity.class);
            startActivity(intent);
                break;
            case 3:
                mTitle = mScreenTitles[mCurrentSelectedPosition];
                cardFindAutoCompleteText = "";
                fragment = TestingFragment.newInstance();
                break;
            default:
                break;
        }

        /**
         * Insert the fragment by replacing any existing fragment
         */
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment).commit();

            /* Highlight the selected item, update the title, and close the drawer*/
            mDrawerList.setItemChecked(position, true);
            setTitle(mScreenTitles[position]);
//            mDrawerLayout.closeDrawer(mDrawerList);
            mDrawerLayout.closeDrawer(mDrawerListContainer);
        } else {
            // Error
            Log.e(this.getClass().getName(), "Error. Fragment is not created");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        /* Sync the toggle state after onRestoreInstanceState has occurred.*/
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /* Pass any configuration change to the drawer toggles*/
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        /* Saving variables*/
        savedInstanceState.putInt(ARG_SECTION_NUMBER, mCurrentSelectedPosition);
        savedInstanceState.putBoolean(IS_PATIENT_FRAGMENT_ACTIVE, isPatientFragmentWasActive);
        if(findViewById(R.id.autoCompleteTextView) != null){
            savedInstanceState.putString(SAVED_TEXT_KEY, ((AutoCompleteTextView)findViewById(R.id.autoCompleteTextView)).getText().toString());
        }

        /* Call at the end*/
        super.onSaveInstanceState(savedInstanceState);
        Log.e(this.getClass().getName(), "onSaveInstanceState with mCurrentSelectedPosition = " + mCurrentSelectedPosition);
    }

//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState){
//        // Call at the start
//        super.onRestoreInstanceState(savedInstanceState);
//
//        // Retrieve variables
//        mCurrentSelectedPosition = savedInstanceState.getInt(ARG_SECTION_NUMBER);
//        Log.e(this.getClass().getName(), "onRestoreInstanceState with mCurrentSelectedPosition = " + mCurrentSelectedPosition);
//    }
}