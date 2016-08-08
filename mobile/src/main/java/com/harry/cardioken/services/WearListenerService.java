package com.harry.cardioken.services;


import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.harry.cardioken.PhoneActivity;


public class WearListenerService extends WearableListenerService {
    // initiating TAG as string for debugging
    private static final String TAG = "Wear Listener Service";
    static String hRate = "00";

    /////////////////////////////
    /////// Wear Listener ///////
    /////////////////////////////



    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        String event = messageEvent.getPath();


        Log.d("Sensor Data Acquired: ", event);

        String[] hRateMessage = event.split("--");

        Log.d("hRateMessage : ", hRateMessage[0].toString());

        if (hRateMessage[0].toString().equals("HR")) {

            hRate = hRateMessage[1].toString();

        }if (hRateMessage[0].equals("None")) {
            hRate = "00";
        }else {
            Log.d("Unknown Sensor data : ", event);
        }


        /**
         /////////////////////////////
         //////// UDP Sender /////////
         /////////////////////////////

         int UDP_SERVER_PORT = 11111;

         String udpMsg = "hr" + "," + hRate  ;
         DatagramSocket ds = null;
         try {
         ds = new DatagramSocket();
         InetAddress serverAddr = InetAddress.getByName("127.0.0.1");

         DatagramPacket dp;
         dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), serverAddr, UDP_SERVER_PORT);
         ds.send(dp);

         Log.d(TAG, "UDP Sent: " + udpMsg + " via " + UDP_SERVER_PORT + "to: " + serverAddr);

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

         **/
    }
    public static String gethRate() {
        return hRate;
    }
}



