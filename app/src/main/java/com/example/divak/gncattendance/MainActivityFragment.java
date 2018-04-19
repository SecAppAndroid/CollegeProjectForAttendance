package com.example.divak.gncattendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.FileOutputStream;

/**
 * Created by divak on 4/19/2018.
 */

public class MainActivityFragment extends Fragment {
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  v=inflater.inflate(R.layout.fragment_login, container, false);

        final EditText userId=(EditText)v.findViewById(R.id.loginFragmentUseridEditText);
        final EditText password=(EditText)v.findViewById(R.id.loginFragmentPasswordEditText);
        final Button login=(Button)v.findViewById(R.id.loginButton);
        try {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                    getString(R.string.preference_file_key), getActivity().MODE_PRIVATE);
            String sessionvalue = sharedPref.getString(getString(R.string.sharedpreference_session), getString(R.string.sharedpreference_session_default_value));
        }catch (Exception e){
            Log.e("Error session",e.toString());
        }
        /*TelephonyManager telephonyManager = (TelephonyManager)v.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();*/
//        String uri = "@drawable/error_warning_icon.png";
//        int imageResource = getResources().getIdentifier(uri, null,null);
//        final Drawable res = getResources().getDrawable(imageResource);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String co="";
                login.setEnabled(false);
                if(userId.getText().toString().trim().length()>=6){
                    if(password.getText().toString().trim().length()>=4) {
                        co=loginCall(userId.getText().toString().trim(),password.getText().toString().trim());
                        if (co.equals("1")) {
                            Fragment fg = DashboardFragment.newInstance();
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.fragmentholder, fg);
                            InputMethodManager inputManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                            //userId1.setTitle(userId.getText().toString().trim()+"\nLogout");
                            transaction.commit();
                        }else if(co.equals("2")) {
                            Toast.makeText(v.getContext(), "Connection Error contact IT team", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(v.getContext(), "Invalid Password or User Id", Toast.LENGTH_LONG).show();
                            password.setText("");
                        }
                    }
                    else{
                        password.setError("Minimum password's length 4");
                    }
                }else{
                    userId.setError("Minimum user Id's length 6");
                }

                login.setEnabled(true);
            }
        });

        return v;
    }
    public static android.support.v4.app.Fragment newInstance() {
        MainActivityFragment mFrgment;
        mFrgment = new MainActivityFragment();
        return mFrgment;
    }

    //login Server call starts
    String a1="";
    JSONObject a=null;
    boolean isThreadComplete=false;

    private String loginCall(final String userId,final String password) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpSend http = new HttpSend("?rollNumber=" + userId + "&password=" + password);
                    a=new JSONObject(String.valueOf(http.execute().get()));
                    Log.d("Respone",a.toString());
                    a1= a.getString("login");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isThreadComplete = true;
            }
        });
        isThreadComplete=false;
        thread.start();
        FileOutputStream outputStream;
        String filename=getString(R.string.josn_file_name);
        while( ! isThreadComplete );
        if(a1.equals("1")){
            try {
                outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(a.toString().getBytes());
                outputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            return "2";
        }
        return a1;
    }
    //Login Server Call Ends
}
