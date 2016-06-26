package rkr.binatestation.piclo.activites;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.adapters.ViewPagerAdapter;
import rkr.binatestation.piclo.fragments.MainContentFragment;
import rkr.binatestation.piclo.models.Categories;
import rkr.binatestation.piclo.utils.Constants;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
                        startActivity(new Intent(getBaseContext(), UploadPicture.class));
                    } else {
                        alertForLoggingIn();
                    }
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            header = navigationView.getHeaderView(0);
            setNavigationHeader();
        }

        ViewPager mViewPager = (ViewPager) findViewById(R.id.CH_homeViewPager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.ABH_TabLayout);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);


    }

    private void setNavigationHeader() {
        TextView userName = (TextView) header.findViewById(R.id.NHH_userName);
        TextView email = (TextView) header.findViewById(R.id.NHH_userEmail);
        if (header != null && getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
            userName.setText(getSharedPreferences(getPackageName(), MODE_PRIVATE).getString(Constants.KEY_USER_FULL_NAME, "PicLo"));
            email.setText(getSharedPreferences(getPackageName(), MODE_PRIVATE).getString(Constants.KEY_USER_EMAIL, ""));
        } else {
            userName.setText(R.string.app_name);
            email.setText("");
        }
    }

    private void alertForLoggingIn() {
        try {
            new AlertDialog.Builder(getContext())
                    .setTitle("Alert")
                    .setMessage("Need sign in to proceed...!")
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(getBaseContext(), LoginActivity.class));
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Categories categoriesDB = new Categories(getContext());
        categoriesDB.open();
        for (Categories categories : categoriesDB.getAllRows()) {
            adapter.addFrag(MainContentFragment.newInstance(categories.getCategoryId(), "H"), categories.getCategoryName());
        }
        categoriesDB.close();
        viewPager.setAdapter(adapter);
    }

    private HomeActivity getContext() {
        return HomeActivity.this;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        if (getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
            menu.getItem(0).setTitle("Logout");
            setNavigationHeader();
        } else {
            menu.getItem(0).setTitle("Login");
            setNavigationHeader();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_login_logout) {
            if (item.getTitle().equals("Logout")) {
                item.setTitle("Login");
                getSharedPreferences(getPackageName(), MODE_PRIVATE).edit()
                        .putBoolean(Constants.KEY_IS_LOGGED_IN, false).apply();
                setNavigationHeader();
            } else {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_upload:
                if (getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
                    startActivity(new Intent(getBaseContext(), UploadPicture.class));
                } else {
                    alertForLoggingIn();
                }
                break;
            case R.id.nav_profile:
                if (getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
                    startActivity(new Intent(getBaseContext(), ProfileActivity.class));
                } else {
                    alertForLoggingIn();
                }
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
