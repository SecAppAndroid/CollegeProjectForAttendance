package com.example.divak.gncattendance;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by divak on 4/19/2018.
 */

public class HttpSendChangePassword extends AsyncTask<String, Void, String>
{

    HttpURLConnection c = null;

    @Override
    protected String doInBackground(String... str) {
        try
        {
            String get_url = str[0].replace(" ", "%20");

            int timeout = 3000;

            URL u = new URL(Gobal.CallUrl+"changepassword.php?"+get_url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return "connection Failed";
    }
}

