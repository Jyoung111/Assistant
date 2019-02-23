package com.example.jy.assistant;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SensorListActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList = new ArrayList<String>();
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


    public ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.mytext);
        title.setText("Sensor List");

        list = (ListView) findViewById(R.id.listView);

//        //Show Device list
//        Dev_ItemData temp = new Dev_ItemData();
//        temp.dev_name = Status.dev_name;
//        temp.mac_addr = Status.mac_addr;
//        arrayList.add(temp);
//
//        adapter = new DeviceCustomAdapter(arrayList, SensorListActivity.this);
//        list.setAdapter(adapter);

        arrayList.add("hi\n");


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
//                            //Show Device list
//
//                            //Show Device list
//                            Dev_ItemData temp = new Dev_ItemData();
//                            temp.dev_name = Status.dev_name;
//                            temp.mac_addr = Status.mac_addr;
//                            arrayList.add(temp);
//
//
//                            adapter = new DeviceCustomAdapter(arrayList,SensorListActivity.this);
//                            list.setAdapter(adapter);


//


//                            Dev_ItemData temp = new Dev_ItemData();
//                            temp.dev_name = Status.dev_name;
//                            temp.mac_addr = Status.mac_addr;
//                            arrayList.add(temp);
//
//
//                            if(arrayList.size()>0) {
//                                adapter = new DeviceCustomAdapter1(arrayList, SensorListActivity.this);
//                                list.setAdapter(adapter);
//                            }


                            // heart_btn.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.pulse));
//                            mConversationArrayAdapter.clear();
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
//                        Dev_ItemData temp = new Dev_ItemData();
//                        temp.dev_name = Status.dev_name;
//                        temp.mac_addr = Status.mac_addr;
//                        arrayList.add(temp);
//
//
//                        adapter = new ArrayAdapter<String>(SensorListActivity.this,android.R.layout.activity_list_item,arrayList);
//                        list.setAdapter(adapter);
//
//
//                        adapter.notifyDataSetChanged();
//                        adapter.notifyDataSetInvalidated();
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





//
//public class DeviceCustomAdapter extends BaseAdapter {
    //
//        LayoutInflater inflater = null;
//        private ArrayList<Dev_ItemData> home_data = null, temp_data = new ArrayList<>();
//        private int hListCnt = 0;
////        Button deleteBtn;
//        Context ctx;
//        TextView dev_name, mac_addr;
//        int pos = 0;
//
//        public DeviceCustomAdapter(ArrayList<Dev_ItemData> _home_Data, Context ctx) {
//            this.home_data = _home_Data;
//            this.hListCnt = home_data.size();
//            this.ctx = ctx;
//            inflater = LayoutInflater.from(ctx);
//        }
//
//        @Override
//        public int getCount() {
//            return hListCnt;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//
//            View view_ly = convertView;
//            try {
//
//
//                if(home_data.size()>0) {
//
//                    inflater = (LayoutInflater)ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
//
//
//                    view_ly = inflater.inflate(R.layout.home_custom_listview_item, null);
//
//
//                    dev_name = (TextView) view_ly.findViewById(R.id.dev_name);
//                    mac_addr = (TextView) view_ly.findViewById(R.id.mac_addr);
//
//                    if (position != -1 && position < home_data.size()) {
//                        dev_name.setText(home_data.get(position).dev_name);
//                        mac_addr.setText(home_data.get(position).mac_addr);
//                    }
//
////                    deleteBtn = (Button) view_ly.findViewById(R.id.btn_delete);
////                    deleteBtn.setTag(position);
//
//                    view_ly.setOnClickListener(new View.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View v)
//                        {
//
//
//                        }
//                    });
//
////                    deleteBtn.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////
////
////                            removeItemFromList(position);
////
////                        }
////                    });
//                }
//                }catch (Exception e)
//                    {
//                        Log.i("Exception==", e.toString());
//                    }
//
//            return view_ly;
//
//        }
//
//        protected void removeItemFromList(int position) {
//            final int deletePosition = position;
//            pos = position;
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//            builder.setTitle("Alert!");
//            builder.setMessage("Do you want delete this Device?")
//                    .setCancelable(false)
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//
//                            home_data.remove(deletePosition);
//
//                            adapter.notifyDataSetChanged();
//                            adapter.notifyDataSetInvalidated();
//
//
//
////                        if(home_data.size()==0)
////                        {
////                            dev_name.setVisibility(View.INVISIBLE);
////                            mac_addr.setVisibility(View.INVISIBLE);
////                            deleteBtn.setVisibility(View.INVISIBLE);
////                        }
//
//                        }
//                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    });
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//
//        }
//
//    }
    protected void removeItemFromList(int position) {
        final int deletePosition = position;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert!");
        builder.setMessage("Do you want delete this Device?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        arrayList.remove(deletePosition);

                        adapter.notifyDataSetChanged();
                        adapter.notifyDataSetInvalidated();



//                        if(home_data.size()==0)
//                        {
//                            dev_name.setVisibility(View.INVISIBLE);
//                            mac_addr.setVisibility(View.INVISIBLE);
//                            deleteBtn.setVisibility(View.INVISIBLE);
//                        }

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


}
