package de.riditt.easyboxunofficial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.riditt.easyboxunofficial.Api.EasyBoxApi;
import de.riditt.easyboxunofficial.Application.EasyBoxUnofficialApplication;
import de.riditt.easyboxunofficial.Presenters.MainActivityPresenter;
import de.riditt.easyboxunofficial.Views.IMainActivityView;

public class MainActivity extends AppCompatActivity
        implements IMainActivityView, NavigationView.OnNavigationItemSelectedListener {

    @Inject
    EasyBoxApi easyBoxApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((EasyBoxUnofficialApplication) getApplication()).getNetworkComponent().inject(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MainActivityPresenter presenter = new MainActivityPresenter(this, easyBoxApi);
        presenter.performLogin();

        /*final TextView debugOutput = (TextView) findViewById(R.id.debug_output);
        easyBoxApi.initialize("192.168.2.1", new EasyBoxApi.OnApiResultListener() {
            @Override
            public void onApiResult(boolean success, Object... results) {
                if(success) {
                    easyBoxApi.establishConnection(new EasyBoxApi.OnApiResultListener() {
                        @Override
                        public void onApiResult(boolean success, Object... results) {
                            easyBoxApi.login("", new EasyBoxApi.OnApiResultListener() {
                                @Override
                                public void onApiResult(final boolean success, Object... results) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            debugOutput.setText("" + success);
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    // prompt user for server URL, check if the URL is valid using checkForValidEasyBoxUrl
                    // and then initialize again
                    easyBoxApi.initialize("192.168.2.1", this);
                }
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        /*} else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {*/

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void launchActivity(Class<?> activityClass) {
        Intent activityLaunchIntent = new Intent(this, activityClass);
        this.startActivity(activityLaunchIntent);
    }
}
