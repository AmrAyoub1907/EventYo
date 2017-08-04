package com.amrayoub.eventyo;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myAccount_button:
                startActivity(new Intent(MainActivity.this,MyAccountActivity.class));
                return true;
            case R.id.signout_button:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.search_button:
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText(getString(R.string.Category_Tab_Name));
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_public, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getString(R.string.Calender_Tab_Name));
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_event, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText(getString(R.string.Going_Tab_Name));
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CategoryFragment(), getString(R.string.Category_Tab_Name));
        adapter.addFragment(new CalenderFragment(), getString(R.string.Calender_Tab_Name));
        adapter.addFragment(new GoingFragment(), getString(R.string.Going_Tab_Name));
        viewPager.setAdapter(adapter);
    }
    public void createEvent(View view) {
        startActivity(new Intent(MainActivity.this,CreateEventActivity.class));
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    @Override
    public void onBackPressed() {
        this.finishAffinity();
        //Toast.makeText(this, "Back button Pressed", Toast.LENGTH_SHORT).show();
    }
}