package com.harry.cardioken;

// defaults

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.harry.cardioken.services.WearListenerService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class PhoneActivity extends Activity implements View.OnClickListener {

    TextView txtHRate, txtWearStatus, txtDIP;
    ImageView imgWear;

    WearListenerService wearListenerService = new WearListenerService();


    Timer udpTimer;
    ParseData parseData;
    int frequency = 1;
    String filename = "cK_" + new SimpleDateFormat("yyyyMMdd_HHmm'.txt'").format(new Date());

    int UDP_SERVER_PORT = 11111;
    private String ipAddress = "127.0.0.1";

    public PhoneActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        // status icons and texts
        txtWearStatus = (TextView) findViewById(R.id.txtWearStatus);
        imgWear = (ImageView) findViewById(R.id.imgWear);

        txtHRate = (TextView) findViewById(R.id.txtHRate);

        txtDIP = (TextView) findViewById(R.id.txtDIP);

        udpTimer = new Timer();
        parseData = new ParseData();
        udpTimer.scheduleAtFixedRate(parseData, 0, 1000 / frequency);


    }

    @Override
    public void onClick(View v) {


    }

    public void btnConfig(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the target UDP IP address");

    // Set up the input
        final EditText input = new EditText(this);
    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

    // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ipAddress = input.getText().toString();
                txtDIP.setText(ipAddress + ":" + String.valueOf(UDP_SERVER_PORT) );
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        //Toast.makeText(getApplicationContext(), "Configuring IP", Toast.LENGTH_SHORT).show();
    }

    public void btnExit(View view) {

        Log.d("CardioKEN", "STOPPED by the USER");
        this.finish();
        System.exit(0);
    }

    public void btnInfo(View view) {
        Toast.makeText(getApplicationContext(), "© 2016 CardioKEN by Harry Vasanth", Toast.LENGTH_SHORT).show();
    }

    /////////////////////////////
    //////// UDP Sender /////////
    /////////////////////////////


    public void sendData(String wearData) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss.SSS").format(new Date());


        String udpMsg = "timestamp" + ", " + timeStamp + ", " + "hr" + ", " + wearData;
        Log.d("UDP Sent: ", "WEAR: " + wearData);

        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
            InetAddress serverAddr = InetAddress.getByName(ipAddress);

            DatagramPacket dp;
            dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), serverAddr, UDP_SERVER_PORT);
            ds.send(dp);

            Log.d("UDP Sent: ", udpMsg + " via " + UDP_SERVER_PORT + " to: " + serverAddr);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }


    /////////////////////////////
    //////// File Writer ////////
    /////////////////////////////

    public void writeData(String wearData) {

        File sdcard = Environment.getExternalStorageDirectory();
        File dir = new File(sdcard.getAbsolutePath() + "/cardioKEN/");

        // creates if doesn't exists
        dir.mkdir();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss.SSS").format(new Date());

        File sessionFile = new File(dir, filename);
        if (!sessionFile.exists()) {
            try {
                sessionFile.createNewFile();
            } catch (IOException e) {

                e.printStackTrace();
            } finally {

            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(sessionFile, true));
            buf.append(timeStamp + ", " + wearData);
            buf.newLine();
            buf.close();
        } catch (IOException e) {


            e.printStackTrace();
        } finally {

        }


    }

    ////////////////////////////
    //////// Timed Task ////////
    ////////////////////////////

    class ParseData extends TimerTask {


        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtHRate.setText(wearListenerService.gethRate());

                    if (wearListenerService.gethRate().equals("00")) {
                        imgWear.setImageResource(R.drawable.hr_off);
                        txtWearStatus.setText("Disconnected");
                        txtWearStatus.setTextColor(Color.parseColor("#f33030"));
                    } else {
                        imgWear.setImageResource(R.drawable.hr_on);
                        txtWearStatus.setText("Connected");
                        txtWearStatus.setTextColor(Color.parseColor("#82dd46"));

                    }
                }
            });

            if (txtWearStatus.getText().toString().equals("Connected")) {
                //Send to UDP
                sendData(wearListenerService.gethRate());

                //Write to File
                writeData(wearListenerService.gethRate());

            } else {
                // do something
                Log.d("UDP & Data: ", "Not sent");
            }


        }
    }
}



