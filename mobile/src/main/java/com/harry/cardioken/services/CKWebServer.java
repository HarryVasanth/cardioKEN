package com.harry.cardioken.services;

import java.io.IOException;

/**
 * Created by Harry on 24/08/2016.
 */
////////////////////////////
//////// Web Server ////////
////////////////////////////

public class CKWebServer extends NanoHTTPD {

  private static final int PORT = 8080;
  public String HRate;

  public CKWebServer() throws IOException {
    super(PORT);
    start();
    System.out.println(
      "\nRunning! Point your browers to http://localhost:8080/ \n"
    );
  }

  @Override
  public Response serve(IHTTPSession session) {
    String msg = "<html><body><h1>CardioKen Server</h1>\n";
    //msg += "<p>Current HR is: 00 " + session.getUri() + " !</p>";
    msg += "<p>Current HR is: " + HRate + "</p>";
    return newFixedLengthResponse(msg + "</body></html>\n");
  }
}
