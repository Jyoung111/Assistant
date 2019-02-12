package com.example.jy.assistant;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.StringTokenizer;

public class MyPolarBleReceiver extends BroadcastReceiver {
    public final static String ACTION_GATT_CONNECTED =
            "edu.ucsd.healthware.fw.device.ble.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "edu.ucsd.healthware.fw.device.ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_HR_DATA_AVAILABLE =
            "edu.ucsd.healthware.fw.device.ble.ACTION_HR_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "edu.ucsd.healthware.fw.device.ble.EXTRA_DATA";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        final String action = intent.getAction();
        if (ACTION_GATT_CONNECTED.equals(action)) {
            Log.w(this.getClass().getName(), "####ACTION_GATT_CONNECTED");
        } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
        } else if (ACTION_HR_DATA_AVAILABLE.equals(action)) {
            //broadcastUpdate(ACTION_HR_DATA_AVAILABLE, heartRate+";"+pnnPercentage+";"+pnnCount+";"+rrThreshold+";"+bioHarnessSessionData.totalNN+";"+bioHarnessSessionData.lastRRvalue+";"+bioHarnessSessionData.sessionId);
            String data = intent.getStringExtra(EXTRA_DATA);


            StringTokenizer tokens = new StringTokenizer(data, ";");
            int heartRate = Integer.parseInt(tokens.nextToken());
            int pnnPercentage = Integer.parseInt(tokens.nextToken());
            int pnnCount = Integer.parseInt(tokens.nextToken());
            int rrThreshold = Integer.parseInt(tokens.nextToken());
            int totalNN = Integer.parseInt(tokens.nextToken());
            int lastRRvalue = Integer.parseInt(tokens.nextToken());
            String sessionId = tokens.nextToken();
            Log.w(this.getClass().getName(), "####Received heartRate: " +heartRate+" pnnPercentage: "+pnnPercentage+" pnnCount: "+pnnCount+" rrThreshold: "+rrThreshold+" totalNN: "+totalNN+" lastRRvalue: "+lastRRvalue+" sessionId: "+sessionId);



//              StringTokenizer tokens = new StringTokenizer(data, ",");
//            int epoch_time  = Integer.parseInt(tokens.nextToken());
//            double temp  = Double.parseDouble(tokens.nextToken());
//            double SN1  = Double.parseDouble(tokens.nextToken());
//            double SN2 = Double.parseDouble(tokens.nextToken());
//            double SN3 = Double.parseDouble(tokens.nextToken());
//            double SN4 = Double.parseDouble(tokens.nextToken());
//            double PM25  = Double.parseDouble(tokens.nextToken());
//            String sessionId = tokens.nextToken();
//            Log.w(this.getClass().getName(), "####Received epoch_time: " +epoch_time+" temp: "+temp+" SN1: "+SN1+" SN2: "+SN2+" SN3: "+SN3+" SN4: "+SN4+" PM25: "+PM25);



        }
    }
}
