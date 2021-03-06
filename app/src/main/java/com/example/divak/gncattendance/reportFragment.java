package com.example.divak.gncattendance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by divak on 4/19/2018.
 */

public class reportFragment extends Fragment {
    ListView listView;
    String result = "";
    List<String> listReport;
    boolean isThreadComplete = false;
    boolean isThreadComplete1 = false;
    ArrayAdapter<String> adapter;
    String offline="",a="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_report, container, false);
        listView=(ListView)v.findViewById(R.id.reportFragmentListView);

        FileManagement fm=new FileManagement();
        String js=fm.getTextFromFile(getString(R.string.josn_file_name_offline_att));
        offline= URLEncoder.encode(fm.getTextFromFile(getString(R.string.josn_file_name_offline_att)));
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                HttpSendOfflineAttendance http=new HttpSendOfflineAttendance();
                a=http.doInBackground("offline="+offline);
                isThreadComplete1=true;
            }
        });
        if(offline.equals("")){

        }else {
            try {
                isThreadComplete1 = false;
                thread.start();
                while (!isThreadComplete1) ;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(a.trim().equals("success")){
                FileOutputStream outputStream;
                String filename=getString(R.string.josn_file_name_offline_att);
                try {
                    outputStream = v.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(("").getBytes());
                    outputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                Toast.makeText(v.getContext(),"Offline data updated no need to worry",Toast.LENGTH_SHORT).show();
            }

        }
        Thread threadReport=new Thread(new Runnable() {
            @Override
            public void run() {
                String rollNumber= "";
                FileManagement fm=new FileManagement();
                String js=fm.getTextFromFile(getString(R.string.josn_file_name));
                try{
                    JSONObject jsonObject=new JSONObject(js);
                    rollNumber=jsonObject.optString("staffrollnumber");
                }catch (Exception e){
                    e.printStackTrace();
                }
                HttpReport http=new HttpReport("staffRollNumber="+rollNumber);
                //result=http.doInBackground("staffRollNumber="+rollNumber);
                try {
                    result=http.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                isThreadComplete=true;
            }
        });
        isThreadComplete=false;
        threadReport.start();
        while (!isThreadComplete);
        listReport=new ArrayList<String>();
        if(result.equals("") || result.trim().equals("connection Failed")){
            final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
            alert.setTitle("Please check the signal");
            alert.setMessage("Please Contact your IT team");
            alert.setCancelable(false);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.show();
        }else {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("report");
                if(jsonArray.length()!=0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        listReport.add(jsonObject1.optString("class"));
                    }
                }else{
                    final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                    alert.setTitle("Attendance Taken is Empty");
                    alert.setMessage("No attendance taken in Last 14 days");
                    alert.setCancelable(false);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(v.getContext(),"Error in Data",Toast.LENGTH_SHORT).show();
            }
            adapter = new ArrayAdapter<String>(v.getContext(),
                    android.R.layout.simple_list_item_1, listReport);
            listView.setAdapter(adapter);
        }
        return v;
    }
    public static android.support.v4.app.Fragment newInstance() {
        reportFragment mFrgment = new reportFragment();
        return mFrgment;
    }

}
