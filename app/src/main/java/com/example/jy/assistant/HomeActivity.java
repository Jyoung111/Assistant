package com.example.jy.assistant;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vaibhavlakhera.circularprogressview.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {


    String url = "http://teamb-iot.calit2.net/da/receiveSensorData";
    JSONObject jsonObject, sensor_data_result_json, hr_data_result_json,realtime_marker_result_json;

    Handler handler = new Handler();
    Intent intent, nav_header_intent;
    private GoogleMap mMap;
    private Toolbar toolbar;

    /////////////////////////////////////////////////////////////////////////////////////////
 //   public static final String TAG = "MainActivity";

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;


    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    LatLng currentLatLng;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;


    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;


    //Now Location Variables
    /////////////////////
    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition = new LatLng(32.883837, -117.232740);

    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


    TextView cloth_text;

    private Circle mCircle;
    private ImageButton heart_btn;

    //HeartRate Image variable
    SupportMapFragment mapFragment;

//    http://remote.ktyri.com:3000/message
//    teamb-iot.calit2.net

    private static String my_URL = "http://teamb-iot.calit2.net";
    ImageView weather_img,cloth_img;

    AppController app ;

    //CircleView
    CircularProgressView so2_progressView, pm_progressView, no2_progressView, co_progressView, o3_progressView ;

    TextView temperature_text ;



    private ActionBarDrawerToggle mDrawerToggle;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleMarginStart(150);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();





        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Save Login data
        SharedPreferences prefs = getSharedPreferences("activity_login",0);
        String saved_email = prefs.getString("email","");
        String saved_pwd = prefs.getString("password","");
        if(saved_email.equals("") && saved_pwd.equals("")){
            //Not Login
            navigationView.getMenu().findItem(R.id.nav_login_logout).setIcon(R.drawable.login);
            navigationView.getMenu().findItem(R.id.nav_login_logout).setTitle("Login");
        }else{
            //Now Login Status
            navigationView.getMenu().findItem(R.id.nav_login_logout).setIcon(R.drawable.logout);
            navigationView.getMenu().findItem(R.id.nav_login_logout).setTitle("Logout");
        }





        //Google Map
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.onResume();

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        activatePolar();


        //Now Location Settings
        mActivity = this;


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //ImageButton Settings
        heart_btn = (ImageButton)findViewById(R.id.heart_btn);

//
//        heart_btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));

        cloth_img = (ImageView)findViewById(R.id.cloth_img);
        weather_img = (ImageView)findViewById(R.id.weahter_img);

        app = AppController.getInstance();


        //CircleView
        so2_progressView = (CircularProgressView)findViewById(R.id.so2_progressView);
        pm_progressView = (CircularProgressView)findViewById(R.id.pm_progressView);
        no2_progressView = (CircularProgressView)findViewById(R.id.no2_progressView);
        co_progressView = (CircularProgressView)findViewById(R.id.co_progressView);
        o3_progressView = (CircularProgressView)findViewById(R.id.o3_progressView);


        temperature_text = (TextView)findViewById(R.id.temperature_text);

        //Timer
        WakeupTimer wakeupTimer = new WakeupTimer();

        //img settings

        cloth_text = (TextView) findViewById(R.id.cloth_text);



    }


    private final MyPolarBleReceiver mPolarBleUpdateReceiver = new MyPolarBleReceiver() {
    };

    protected void activatePolar() {
        Log.w(this.getClass().getName(), "activatePolar()");
        registerReceiver(mPolarBleUpdateReceiver, makePolarGattUpdateIntentFilter());
    }

    protected void deactivatePolar() {
        unregisterReceiver(mPolarBleUpdateReceiver);
    }

    private static IntentFilter makePolarGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyPolarBleReceiver.ACTION_GATT_CONNECTED);
        intentFilter.addAction(MyPolarBleReceiver.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(MyPolarBleReceiver.ACTION_HR_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }


        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){

            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }



    }


    @Override
    protected void onStop() {

        if (mRequestingLocationUpdates) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if ( mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
        deactivatePolar();
    }

    @Override
    public void onResume() {
        super.onResume();


        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }

        //Now Location Settings

        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }


        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }



    }


    private void setupChat() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            heart_btn.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.pulse));
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            heart_btn.clearAnimation();
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Toast.makeText(HomeActivity.this, writeMessage, Toast.LENGTH_SHORT).show();
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    Toast.makeText(HomeActivity.this, readMessage,Toast.LENGTH_SHORT).show();
                    setProgressView(readMessage);


                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != this) {
                        Toast.makeText(HomeActivity.this, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != this) {
                        Toast.makeText(HomeActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private void setStatus(int resId) {
        TextView dev_status = (TextView) toolbar.findViewById(R.id.device_status);
        /*actionBar.setSubtitle(resId);*/
        dev_status.setText(resId);
    }

    private void setStatus(CharSequence subTitle) {
        TextView dev_status = (TextView) toolbar.findViewById(R.id.device_status);
        /*actionBar.setSubtitle(resId);*/
        dev_status.setText(subTitle);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);

                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    this.finish();
                }

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");


                        if ( mGoogleApiClient.isConnected() == false ) {

                            Log.d( TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }


/////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        LatLng ucsd = new LatLng(32.883837, -117.232740);
//
//        // Add a marker in UCSD, and move the camera.
//        mMap.addMarker(new MarkerOptions().position(ucsd).title("Marker in UCSD"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(ucsd));
//        //Move the camera to the user's location and zoom in!
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ucsd, 18));
//
//        // Drawing circle on the map
//        drawCircle(ucsd);


        ////////////////////////////////////////////////////////

        mGoogleMap = googleMap;

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(new LatLng(32.879950, -117.236215));
        markerOptions1.title("Sensor2");
        markerOptions1.draggable(true);
        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mGoogleMap.addMarker(markerOptions1);

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(new LatLng(32.881371, -117.235649));
        markerOptions2.title("Sensor3");
        markerOptions2.draggable(true);
        markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mGoogleMap.addMarker(markerOptions2);

        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(new LatLng(32.880596, -117.235376));
        markerOptions3.title("Sensor4");
        markerOptions3.draggable(true);
        markerOptions3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mGoogleMap.addMarker(markerOptions3);


        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 UCSD로 이동
        setDefaultLocation();

        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d(TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                moveCameraNow();
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d(TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {

                if (mMoveMapByUser == true && mRequestingLocationUpdates) {

                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {


            }
        });


    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
        getMenuInflater().inflate(R.menu.home, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bluetooth) {
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
            nav_header_intent = new Intent(this, MyProfileActivity.class);
            startActivity(nav_header_intent);
        } else if (id == R.id.nav_aqi) {
            nav_header_intent = new Intent(this, AQI_IndexActivity.class);
            startActivity(nav_header_intent);
        } else if (id == R.id.nav_login_logout) {

            //Show Dialog


                Status.USN = -1;
                Status.EMAIL = "";

                //Current Login Data Reset
                SharedPreferences prefs = getSharedPreferences("activity_login",0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();

                nav_header_intent = new Intent(this, LoginActivity.class);
                startActivity(nav_header_intent);




        }
//        else if (id == R.id.nav_settings) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.heart_btn:
                intent = new Intent(this, HeartRateHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.pm_btn:
                intent = new Intent(this, AirHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.co_btn:
                intent = new Intent(this, AirHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.so2_btn:
                intent = new Intent(this, AirHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.no2_btn:
                intent = new Intent(this, AirHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.o3_btn:
                intent = new Intent(this, AirHistoryActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            Toast.makeText(HomeActivity.this, "signup_result_json", Toast.LENGTH_SHORT);
        }
    }

    ///////////////Now Location Settings
    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : not have permission");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);

        }

    }



    private void stopLocationUpdates() {

        Log.d(TAG,"stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }



    @Override
    public void onLocationChanged(Location location) {

        currentPosition
                = new LatLng( location.getLatitude(), location.getLongitude());


        Log.d(TAG, "onLocationChanged : ");

        String markerTitle = getCurrentAddress(currentPosition);
        String markerSnippet = "lat:" + String.valueOf(location.getLatitude())
                + " log:" + String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성하고 이동
        setCurrentLocation(location, markerTitle, markerSnippet);

        mCurrentLocatiion = location;

        try {

            TextView location_title = (TextView) toolbar.findViewById(R.id.title);

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.ENGLISH);
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.isEmpty()) {
                location_title.setText("Waiting for Location...");
            }
            else {
                if (addresses.size() > 0) {
                    Log.i("adress",""+addresses.get(0));
                    location_title.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality());
                }
            }


        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        if ( mRequestingLocationUpdates == false ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                } else {

                    Log.d(TAG, "onConnected : already have permission");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            }else{

                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed");
        setDefaultLocation();
    }


    @Override
    public void onConnectionSuspended(int cause) {

        Log.d(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }


    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "Geocoder service unavailable ", Toast.LENGTH_LONG).show();
            return "Geocoder service unavailable";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "Incorrect GPS coordinates", Toast.LENGTH_LONG).show();
            return "Incorrect GPS coordinates";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "Address not found", Toast.LENGTH_LONG).show();
            return "Address not found";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void moveCameraNow(){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f);
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        mapFragment.onStart();
        mapFragment.onResume();

        mMoveMapByUser = false;



        if (currentMarker != null) currentMarker.remove();


        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        currentMarker = mGoogleMap.addMarker(markerOptions);


        if ( mMoveMapByAPI ) {

            Log.d( TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude() ) ;
             CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f);
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.moveCamera(cameraUpdate);

        }
    }


    public void setDefaultLocation() {

        mMoveMapByUser = false;


        //디폴트 위치, UCSD
        LatLng DEFAULT_LOCATION = new LatLng(32.883837, -117.232740);
        String markerTitle = "Could not get location info";
        String markerSnippet = "Check your location permissions and GPS activity ";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 16f);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("If you want to run an app, You must granted");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("Permission Denied + Don't ask again " +
                    "\n" +
                    "If the checkbox is set, Permission must be granted in the setting. ");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {


            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");

            if ( mGoogleApiClient.isConnected() == false) {

                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {


                if ( mGoogleApiClient.isConnected() == false) {

                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }



            } else {

                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Location Service Disabled");
        builder.setMessage("If you want to use this app, Please turn on GPS.\n"
                + "Would you like to turn on GPS?");
        builder.setCancelable(true);
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    //HR Timer Class
    public class WakeupTimer {
        Timer timer;

        public WakeupTimer() {
            timer = new Timer();
            timer.schedule(new RemindTask(),
                    0,        //initial delay
                    2000);  //subsequent rate
        }

        class RemindTask extends TimerTask {
            public void run() {
                app = AppController.getInstance();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int temp = app.heartRate;
                        SharedPreferences prefs = getSharedPreferences("activity_login",0);
                        int seq_num = prefs.getInt("USN",-1);
                        if(temp != 0 && seq_num != -1){
                            setText();
                            sendHRJSON();
                        }
                    }
                }, 100);

//                Log.w("signup_result_json",app.sessionId+" "+app.heartRate+" "+app.lastRRvalue+" "+app.pnnCount+" "+app.pnnPercentage+" "+app.rrThreshold+" "+app.totalNN);
            }
        }
    }




    public void setText(){
        app = AppController.getInstance();
        TextView hrtext = (TextView) this.findViewById(R.id.hr_text);
        hrtext.setText(""+app.heartRate);
    }

    private void setProgressView(String msg){
        Log.d("token_test",msg);
        StringTokenizer tokens = new StringTokenizer(msg, ",");
        String type = tokens.nextToken();

//HH:mm:ss


        String epoch_time = tokens.nextToken().trim();

        //SN1 = no2, SN2 = O3, SN3 = CO, SN4 = SO2
        double temp  = Double.parseDouble(tokens.nextToken().trim());
        double SN1  = Double.parseDouble(tokens.nextToken().trim());
        double SN2 = Double.parseDouble(tokens.nextToken().trim());
        double SN3 = Double.parseDouble(tokens.nextToken().trim());
        double SN4 = Double.parseDouble(tokens.nextToken().trim());
        double PM25  = Double.parseDouble(tokens.nextToken().trim());
        double raw_SN1  = Double.parseDouble(tokens.nextToken().trim());
        double raw_SN2 = Double.parseDouble(tokens.nextToken().trim());
        double raw_SN3 = Double.parseDouble(tokens.nextToken().trim());
        double raw_SN4 = Double.parseDouble(tokens.nextToken().trim());
        double raw_PM25  = Double.parseDouble(tokens.nextToken().trim());


        //SN1 = no2, SN2 = O3, SN3 = CO, SN4 = SO2
        double  [] aqi_arr = {SN1, SN2,SN3,SN4,PM25};

        CircularProgressView  [] cpvArr  = {no2_progressView,o3_progressView,co_progressView,so2_progressView,pm_progressView};


        Log.w(this.getClass().getName(), "####Received epoch_time: " +epoch_time+" temp: "+temp+" SN1: "+SN1+" SN2: "+SN2+" SN3: "+SN3+" SN4: "+SN4+" PM25: "+PM25);

        int cnt = 0;
        for(double now : aqi_arr){
            int now_data = (int)now;
            if(  0 <= now_data &&  now_data <= 50)
            {
                 cpvArr[cnt].setProgressColor(getResources().getColor(R.color.good));
            }else if(  51 <= now_data &&  now_data<= 100)
            {
                cpvArr[cnt].setProgressColor(getResources().getColor(R.color.Moderate));
            }else if(  101 <= now_data &&  now_data <= 150)
            {
                cpvArr[cnt].setProgressColor(getResources().getColor(R.color.Unhealthy_for_Sensitive_Groups));
            }
            else if(  151 <= now_data &&  now_data <= 200)
            {
                cpvArr[cnt].setProgressColor(getResources().getColor(R.color.Unhealthy));
            }
            else if(  201 <= now_data &&  now_data <= 300)
            {
                cpvArr[cnt].setProgressColor(getResources().getColor(R.color.Very_Unhealthy));

            }
            else
            {
                cpvArr[cnt].setProgressColor(getResources().getColor(R.color.Hazardous));
            }
            cpvArr[cnt].setProgress((int)aqi_arr[cnt],true);
            cnt++;
        }

        temperature_text.setText((int)temp+" ℉");

        String [] cloth_str = {"Padding","Coat","Trench coats", "Cardigans","Thin knitwear","Long-sleeved","Short-sleeved","Sleeveless shirt"};

        //temp img setting
        int comp_temp = (int)temp;
        if(comp_temp <= 39){
            cloth_img.setImageResource(R.drawable.cloth1);
            cloth_text.setText(cloth_str[0]);
        }else if(comp_temp >= 39 && comp_temp < 46){
            cloth_img.setImageResource(R.drawable.cloth2);
            cloth_text.setText(cloth_str[1]);
        }else if(comp_temp >= 46 && comp_temp < 51){
            cloth_img.setImageResource(R.drawable.cloth3);
            cloth_text.setText(cloth_str[2]);
        }else if(comp_temp >= 51 && comp_temp < 60){
            cloth_img.setImageResource(R.drawable.cloth4);
            cloth_text.setText(cloth_str[3]);
        }else if(comp_temp >=  60 && comp_temp < 66){
            cloth_img.setImageResource(R.drawable.cloth5);
            cloth_text.setText(cloth_str[4]);
        }else if(comp_temp >= 66 && comp_temp < 71){
            cloth_img.setImageResource(R.drawable.cloth6);
            cloth_text.setText(cloth_str[5]);
        }else if(comp_temp >= 71 && comp_temp < 80){
            cloth_img.setImageResource(R.drawable.cloth7);
            cloth_text.setText(cloth_str[6]);
        }else if(comp_temp >= 80){
            cloth_img.setImageResource(R.drawable.cloth8);
            cloth_text.setText(cloth_str[7]);
        }

        sendAQIJSON(epoch_time, temp,SN1,SN2,SN3,SN4,PM25,raw_SN1,raw_SN2,raw_SN3,raw_SN4,raw_PM25);

    }
//epoch_time, temp,SN1,SN2,SN3,SN4,PM25
    public void sendAQIJSON(String epoch_time, double temp, double SN1, double SN2, double SN3, double SN4, double PM25, double raw_SN1, double raw_SN2, double raw_SN3, double raw_SN4, double raw_PM25){
            url = "http://teamb-iot.calit2.net/da/receiveSensorData";
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datetime = dateformat.format(c.getTime());

            jsonObject = new JSONObject();
            try {
                //Make Sensor data to JSON
                //UDOO Board
                //SN1 = no2, SN2 = O3, SN3 = CO, SN4 = SO2
                SharedPreferences prefs = getSharedPreferences("activity_login",0);
                jsonObject.put("type", "AQDS-REQ");
                jsonObject.put("epoch_time", datetime);
                jsonObject.put("temp",temp);
                jsonObject.put("SO2", (int)SN4);
                jsonObject.put("CO", (int)SN3);
                jsonObject.put("NO2",(int)SN1 );
                jsonObject.put("O3", (int)SN2);
                jsonObject.put("PM25", (int)PM25);
                jsonObject.put("raw_SO2", raw_SN4);
                jsonObject.put("raw_CO", raw_SN3);
                jsonObject.put("raw_NO2",raw_SN1 );
                jsonObject.put("raw_O3", raw_SN2);
                jsonObject.put("raw_PM25", raw_PM25);
                jsonObject.put("user_seq_num", prefs.getInt("USN",-1));

                //Location data
                jsonObject.put("lat", currentPosition.latitude);
                jsonObject.put("lon", currentPosition.longitude);


                Receive_json receive_json = new Receive_json();
                sensor_data_result_json = receive_json.getResponseOf(HomeActivity.this, jsonObject, url);

                if(sensor_data_result_json != null) {
                    if (sensor_data_result_json.getString("success_or_fail").equals("receivesuccess")) {
                        Log.w("sensor_data_send...","");
                    }
                    else {
                        Log.w("sensor_data_fail...","");
//                        Toast.makeText(HomeActivity.this, "Data send fail", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void sendHRJSON(){
        url = "http://teamb-iot.calit2.net/da/receiveHRData";
        jsonObject = new JSONObject();
        try {
            //Make HeartRate data to JSON
            //date, hr, user_seq_num,lat,lon
            app = AppController.getInstance();
            int temp = app.heartRate;

            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datetime = dateformat.format(c.getTime());

            SharedPreferences prefs = getSharedPreferences("activity_login",0);
            jsonObject.put("type", "HRDA-REQ");
            jsonObject.put("epoch_time",datetime);
            jsonObject.put("user_seq_num", prefs.getInt("USN",-1));
            jsonObject.put("heart_rate", temp);
            //Location data
            jsonObject.put("lat", currentPosition.latitude);
            jsonObject.put("lon", currentPosition.longitude);


            Receive_json receive_json = new Receive_json();
            hr_data_result_json = receive_json.getResponseOf(HomeActivity.this, jsonObject, url);

            if(hr_data_result_json != null) {
                if (hr_data_result_json.getString("success_or_fail").equals("HR_receivesuccess")) {
                    Log.w("HR data send...","");
                }
                else {
                    Log.w("sensor_data_fail...","");
//                    Toast.makeText(HomeActivity.this, "Data send fail", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
