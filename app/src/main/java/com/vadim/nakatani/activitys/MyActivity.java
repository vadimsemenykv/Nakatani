package com.vadim.nakatani.activitys;

import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.vadim.nakatani.R;
import com.vadim.nakatani.fragments.diagnostics_screen.EKSValueFragment;
import com.vadim.nakatani.fragments.diagnostics_screen.EvaluationFragment;
import com.vadim.nakatani.fragments.diagnostics_screen.RadiorakyMapFragment;
import com.vadim.nakatani.fragments.diagnostics_screen.RecommendationsFragment;
import com.vadim.nakatani.fragments.home_screen.CardFileFragment;

public class MyActivity extends Activity implements ActionBar.TabListener {

    private String[] mTabTitles;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private TabsPagerAdapter mSectionsPagerAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // Initilization
        mTabTitles = getResources().getStringArray(R.array.diagnostics_screen_array);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mSectionsPagerAdapter = new TabsPagerAdapter(getFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        /*// Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }*/
        /* For each of the sections in the app, add a tab to the action bar.*/
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class TabsPagerAdapter  extends FragmentPagerAdapter {

        public TabsPagerAdapter (FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public Fragment getItem(int index) {

            switch (index) {
                case 0:
                    return EKSValueFragment.newInstance("param1", "param2");
                case 1:
                    return RadiorakyMapFragment.newInstance();
                case 2:
                    return EvaluationFragment.newInstance("1", "2");
                case 3:
                    return RecommendationsFragment.newInstance("param1", "param2");
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return (mTabTitles[position]).toUpperCase(l);
                case 1:
                    return (mTabTitles[position]).toUpperCase(l);
                case 2:
                    return (mTabTitles[position]).toUpperCase(l);
                case 3:
                    return (mTabTitles[position]).toUpperCase(l);
            }
            return null;
        }
    }
}
