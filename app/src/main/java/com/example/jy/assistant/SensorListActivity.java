package com.example.jy.assistant;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class SensorListActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList = new ArrayList<String>();

    String url = "http://teamb-iot.calit2.net/da/receiveSensorData";
    JSONObject jsonObject, dev_regi_result_json,dev_deregi_result_json,all_dev_list_result_json;
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

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;


    String mac_addr,dev_name;

    public ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.mytext);
        title.setText("Sensor Registration");

        list = (ListView) findViewById(R.id.listView);

        receive_all_dev();

        adapter = new ArrayAdapter<String>(SensorListActivity.this,android.R.layout.simple_list_item_1,arrayList);
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeItemFromList(position);
                return true;
            }
        });


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
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
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

                            break;
                        case BluetoothChatService.STATE_CONNECTING:

                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:

                            //heart_btn.clearAnimation();
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Toast.makeText(SensorListActivity.this, writeMessage, Toast.LENGTH_SHORT).show();
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    Toast.makeText(HomeActivity.this, readMessage,Toast.LENGTH_SHORT).show();



                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != this) {
                        Toast.makeText(SensorListActivity.this, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        //Show Device list

                        dev_name = Status.dev_name;
                        mac_addr = Status.mac_addr;
                        arrayList.add(dev_name + "\n" + mac_addr);
                        dev_regi(dev_name,mac_addr);
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != this) {
                        Toast.makeText(SensorListActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };



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

    protected void removeItemFromList(final int position) {
        final int deletePosition = position;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert!");
        builder.setMessage("Do you want delete this Device?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dev_deregi(arrayList.get(position),position);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void dev_regi(String dev_n, String mac_ad)
    {
        url = "http://teamb-iot.calit2.net/da/receivesensor_list";

        jsonObject = new JSONObject();
        try {
            //Make Sensor data to JSON
            //UDOO Board
            //SN1 = no2, SN2 = O3, SN3 = CO, SN4 = SO2
            SharedPreferences prefs = getSharedPreferences("activity_login",0);
            jsonObject.put("type", "SRP-REQ");
            jsonObject.put("mac_address", mac_ad);
            jsonObject.put("dev_name",dev_n);
            jsonObject.put("user_seq_num", prefs.getInt("USN",-1));


            Receive_json receive_json = new Receive_json();
            dev_regi_result_json = receive_json.getResponseOf(SensorListActivity.this, jsonObject, url);

            if(dev_regi_result_json != null) {
                if (dev_regi_result_json.getString("success_or_fail").equals("insertsuccess")) {
                    adapter = new ArrayAdapter<String>(SensorListActivity.this,android.R.layout.simple_list_item_1,arrayList);
                    list.setAdapter(adapter);
                    Log.w("sensor_data_send...","");
                }
                else {
                    Log.w("sensor_data_fail...","");
                    Toast.makeText(SensorListActivity.this, "Device Registration Fail", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void dev_deregi(String dev_n, int po)
    {
        url = "http://teamb-iot.calit2.net/da/sensor_DeregistAndroid";

        jsonObject = new JSONObject();
        try {
            //Make Sensor data to JSON
            //UDOO Board
            //SN1 = no2, SN2 = O3, SN3 = CO, SN4 = SO2
            SharedPreferences prefs = getSharedPreferences("activity_login",0);
            StringTokenizer tokens = new StringTokenizer(dev_n, "\n");
            String dev_na = tokens.nextToken();
            String mac_a = tokens.nextToken();

            jsonObject.put("type", "SDP-REQ");
            jsonObject.put("mac_address", mac_a);
            jsonObject.put("user_seq_num", prefs.getInt("USN",-1));


            Receive_json receive_json = new Receive_json();
            dev_deregi_result_json = receive_json.getResponseOf(SensorListActivity.this, jsonObject, url);

            if(dev_deregi_result_json != null) {
                if (dev_deregi_result_json.getString("success_or_fail").equals("deletesuccess")) {
                    arrayList.remove(po);
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetInvalidated();

                }
                else {
                    Log.w("sensor_data_fail...","");
                    Toast.makeText(SensorListActivity.this, "Device Deregistration Fail", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void receive_all_dev(){
        url = "http://teamb-iot.calit2.net/da/sendalldevlist";

        jsonObject = new JSONObject();
        try {
            //Make Sensor data to JSON
            //UDOO Board
            //SN1 = no2, SN2 = O3, SN3 = CO, SN4 = SO2
            SharedPreferences prefs = getSharedPreferences("activity_login", 0);

            jsonObject.put("type", "SDP-REQ");
            jsonObject.put("user_seq_num", prefs.getInt("USN", -1));


            Receive_json receive_json = new Receive_json();
            all_dev_list_result_json = receive_json.getResponseOf(SensorListActivity.this, jsonObject, url);

            if (all_dev_list_result_json != null) {

                    if (all_dev_list_result_json.getString("success_or_fail").equals("devlistsuccess")) {
                        JSONArray cast = all_dev_list_result_json.getJSONArray("dev_list");
                        if (cast.length() > 0) {
                            for (int i = 0; i < cast.length(); i++) {
                                JSONObject actor = cast.getJSONObject(i);
                                //date, pm, co,so2,no2,o3
                                String received_name = actor.get("dev_name").toString();
                                String received_mac = actor.get("mac_addr").toString();

                                //Save Date
                                arrayList.add(received_name + "\n" + received_mac);
                            }
                        } else {
                            Log.w("sensor_data_fail...", "");
                            Toast.makeText(SensorListActivity.this, "Device List Loading Fail", Toast.LENGTH_SHORT).show();
                        }
                    }

            }
        }catch (JSONException e) {
                e.printStackTrace();
        }
    }







}