package com.example.jy.assistant;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Receive_json {
    //ServerCommunicator 와 달리 AsyncTask 인스턴스에 대해 get()을 사용하지 않음.
    //get()을 사용함으로써 프로그램 스레드 흐름이 꼬여 결국 다이얼로그가 동작하지 않으므로.

    ProgressDialog waitingDialog;
    Handler handler = new Handler();
    JSONObject jsonObject = new JSONObject();
    String url = "";

    private static Receive_json instance = new Receive_json();
    public static Receive_json getInstance() {
        return instance;
    }


    public JSONObject getResponseOf(Context ctx, JSONObject sendMsg,String url) {
        this.url = url;
        try {
//            waitingDialog = new ProgressDialog(ctx);
//            waitingDialog.setMessage("Now loading...");
//            waitingDialog.setIndeterminate(false);
//            waitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            waitingDialog.setCancelable(false);
//            waitingDialog.show();
//
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    waitingDialog.dismiss();
//                }
//            }, 1500);

            DisplayLoadingComm dlc = new DisplayLoadingComm();
            JSONObject receivedMsg = dlc.execute(sendMsg).get();

//            Log.w("test received from svr", receivedMsg.toString());
            return receivedMsg;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.w("test", "Transmit failed....");
            return null;
        }
    }

    public class DisplayLoadingComm extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {
            String data = "";

            HttpURLConnection httpURLConnection = null;
            String msg = jsonObjects[0].toString();
            try {
                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setReadTimeout(3000 /*milliseconds*/);
                httpURLConnection.setConnectTimeout(5000 /* milliseconds */);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                //make some HTTP header nicety
                httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                httpURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                httpURLConnection.setFixedLengthStreamingMode(msg.getBytes().length);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(msg);
                wr.flush();
                wr.close();
                Log.w("test", "Data sent...." + msg);

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                data = bf.readLine();
                bf.close();
                in.close();

//                int inputStreamData = inputStreamReader.read();
//                while (inputStreamData != -1) {
//                    char current = (char) inputStreamData;
//                    inputStreamData = inputStreamReader.read();
//                    data += current;
//                }
                Log.w("test", "Data received...." + data);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            try {
                jsonObject = new JSONObject(new String(data));
            } catch (Exception ex) {
                ex.printStackTrace();
                jsonObject = null;
                Log.w("test", "Transmit failed....");
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
        }
    }
}