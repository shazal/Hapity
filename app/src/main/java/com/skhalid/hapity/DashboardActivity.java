package com.skhalid.hapity;

import android.app.SearchManager;
import android.content.Context;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.skhalid.hapity.fragments.BottomFragment;
import com.skhalid.hapity.fragments.BroadcastListFragment;
import com.skhalid.hapity.fragments.LoginFragment;
import com.skhalid.hapity.fragments.MyListsFragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class DashboardActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static ActionBar action_bar;
    public static Fragment bottom_fragment;
    private static Dialog customProgressDialog;
    private LoginFragment login;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager lm;
    private Location mCurrentLocation;

    public static SharedPreferences hapityPref;
    public static final String PREFS_NAME = "HapityPreferences";

    public static String pfBID;
    public static String pfSURL;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hapityPref = getSharedPreferences(PREFS_NAME, 0);

        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.skhalid.hapity",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Toast.makeText(DashboardActivity.this, Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setFullscreen(true);
        setContentView(R.layout.activity_dashboard);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        action_bar = getSupportActionBar();
        action_bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6EAA54")));
        action_bar.hide();

        bottom_fragment =  getSupportFragmentManager().findFragmentById(R.id.bottom_fragment);
        bottom_fragment.getView().setVisibility(View.INVISIBLE);


        login = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dash_container, login);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//		transaction.addToBackStack("login");
//      getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction.commit();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.menu_array);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));




//        // Recycle the typed array
//        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
//        // set up the drawer's list view with items and click listener
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        action_bar.setDisplayHomeAsUpEnabled(true);
        action_bar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        buildGoogleApiClient();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        login.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BottomFragment.isHomeActive = false;
        BottomFragment.isTypesActive = false;
        BottomFragment.isAlertActive = false;
        BottomFragment.isMyListsActive = false;
        
        mGoogleApiClient.disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch(Exception e){
            e.getLocalizedMessage();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            changeModetoHighAccuracy();

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//            updateUI();
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.

            startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && location.getAccuracy() > 30)
            changeModetoHighAccuracy();
        else if(location.getAccuracy() < 100) {
            mCurrentLocation = location;
            hapityPref.edit().putString("Lattitude", String.valueOf(mCurrentLocation.getLatitude())).putString("Longitude", String.valueOf(mCurrentLocation.getLongitude())).commit();
//            Toast.makeText(this, getResources().getString(R.string.location_updated_message) + mCurrentLocation.getAccuracy() + " meters",
//                    Toast.LENGTH_SHORT).show();
//            sendMessage(Constants.MSG_LOCATION_CHANGED);

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void changeModetoHighAccuracy()
    {

    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments

        switch (position){
            case 0:
                if(!BottomFragment.isHomeActive)
                {

                    try {

                        BottomFragment.isHomeActive = true;
                        BottomFragment.homeButton.setImageDrawable(getResources().getDrawable(R.drawable.lists_pressed));
                        BottomFragment.isTypesActive = false;
                        BottomFragment.topics_btn.setImageDrawable(getResources().getDrawable(R.drawable.browse_normal));
                        BottomFragment.isAlertActive = false;
                        BottomFragment.alertButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_normal));
                        BottomFragment.isMyListsActive = false;
                        BottomFragment.profileButton.setImageDrawable(getResources().getDrawable(R.drawable.mylist_normal));

                        BroadcastListFragment broadcastListFragment = new BroadcastListFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.dash_container, broadcastListFragment);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//					transaction.addToBackStack("posts");
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        transaction.commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            case 1:
                if(!BottomFragment.isMyListsActive)
                {
                    try {
                        BottomFragment.isHomeActive = false;
                        BottomFragment.homeButton.setImageDrawable(getResources().getDrawable(R.drawable.lists_normal));
                        BottomFragment.isTypesActive = false;
                        BottomFragment.topics_btn.setImageDrawable(getResources().getDrawable(R.drawable.browse_normal));
                        BottomFragment.isAlertActive = false;
                        BottomFragment.alertButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_normal));
                        BottomFragment.isMyListsActive = true;
                        BottomFragment.profileButton.setImageDrawable(getResources().getDrawable(R.drawable.mylist_pressed));

                        MyListsFragment myListsFragment = new MyListsFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.dash_container, myListsFragment);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//					transaction.addToBackStack("profile");
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        transaction.commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            case 2:
                break;
            case 3:
                LoginManager.getInstance().logOut();
                hapityPref.edit().putString("loggedin", "0").commit();
                action_bar.hide();

                bottom_fragment =  getSupportFragmentManager().findFragmentById(R.id.bottom_fragment);
                bottom_fragment.getView().setVisibility(View.INVISIBLE);


                login = new LoginFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.dash_container, login);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//		transaction.addToBackStack("login");
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.commit();
                break;
        }
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        action_bar.setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setFullscreen(boolean fullscreen)
    {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullscreen)
        {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        else
        {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        getWindow().setAttributes(attrs);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public static void showCustomProgress(final Context context, String Msg, Boolean isCancelable) {
        if (customProgressDialog != null)
            while (customProgressDialog.isShowing())
                customProgressDialog.dismiss();

        customProgressDialog = new Dialog(context, R.style.customDialogTheme);
        customProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customProgressDialog.setContentView(R.layout.waitlayout);
        customProgressDialog.setCancelable(isCancelable);

        if (Msg.length() > 1) {
            TextView tv = (TextView) customProgressDialog.findViewById(R.id.customprogress_text);
            tv.setVisibility(View.VISIBLE);
            tv.setText(Msg);
        }

        customProgressDialog.show();
    }

    public static void dismissCustomProgress(){
        if(customProgressDialog.isShowing())
            customProgressDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
//        changeModetoHighAccuracy();
    }
}
