package com.example.bottombar.sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by iiro on 7.6.2016.
 */
public class FiveColorChangingTabsActivity extends AppCompatActivity {
    private TextView messageView;
    SharedPreferences settings;
    private List<String> fruits_list;
    private String qrcode,code1,code2,code3;
    private BottomBar bottomBar;
    private ArrayAdapter<String> arrayAdapter;
    private ListView lv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_changing_tabs);
        lv = (ListView) findViewById(R.id.lv);
        settings = FiveColorChangingTabsActivity.this.getSharedPreferences("ARUBA", Context.MODE_PRIVATE);

        // Initializing a new String Array
        String[] fruits = new String[] {
                "Welcome To Aruba Networks",

        };
//        messageView = (TextView) findViewById(R.id.messageView);

       bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        fruits_list = new ArrayList<String>(Arrays.asList(fruits));

        // Create an ArrayAdapter from List
        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, fruits_list);

        qrcode =settings.getString("QR_CODE", null);
        code1 = settings.getString("ODE39", null);
        code2 = settings.getString("CODE93", null);
        code3 = settings.getString("CODE128", null);
        // DataBind ListView with items from ArrayAdapter
        lv.setAdapter(arrayAdapter);
//        fruits_list.add("Loquat");
        try {
            if(!code1.equals(null))
            {
                fruits_list.add(code1);
            }

        }catch (Exception e){

        }
        try {
            if(!qrcode.equals(null))
            {
                fruits_list.add(qrcode);
            }

        }catch (Exception e){

        }
        try {
            if(!code2.equals(null))
            {
                fruits_list.add(code2);
            }

        }catch (Exception e){

        }
        try {
            if(!code3.equals(null))
            {
                fruits_list.add(code3);
            }

        }catch (Exception e){

        }



                /*
                    notifyDataSetChanged ()
                        Notifies the attached observers that the underlying
                        data has been changed and any View reflecting the
                        data set should refresh itself.
                 */
        arrayAdapter.notifyDataSetChanged();
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
//                messageView.setText(TabMessage.get(tabId, false));
//                lv = (ListView) findViewById(R.id.lv);
                bottomBar.setInActiveTabColor(getResources().getColor(R.color.colorPrimary));
                bottomBar.setActiveTabColor(getResources().getColor(R.color.colorPrimaryDark));
                Log.d("five",String.valueOf(TabMessage.get(tabId, false)));
                if (String.valueOf(TabMessage.get(tabId, false)).equals("Content for Clients")){
                    Intent FullScannerActivity = new  Intent(FiveColorChangingTabsActivity.this, FullScannerActivity.class);
                    startActivity(FullScannerActivity);
                }
               else if (String.valueOf(TabMessage.get(tabId, false)).equals("Content for App Rf")){
                    Intent FullScannerActivity = new  Intent(FiveColorChangingTabsActivity.this, Code_39.class);
                    startActivity(FullScannerActivity);
                }
                else if (String.valueOf(TabMessage.get(tabId, false)).equals("Content for Access Point")){
                    Intent FullScannerActivity = new  Intent(FiveColorChangingTabsActivity.this, Code_93.class);
                    startActivity(FullScannerActivity);
                }
                else if (String.valueOf(TabMessage.get(tabId, false)).equals("Content for Switches")){
                    Intent FullScannerActivity = new  Intent(FiveColorChangingTabsActivity.this, Code_128.class);
                    startActivity(FullScannerActivity);
                }
//                else if (String.valueOf(TabMessage.get(tabId, false)).equals("Content for Switches")){
//                    Intent FullScannerActivity = new  Intent(FiveColorChangingTabsActivity.this, com.example.bottombar.sample.FullScannerActivity.class);
//                    startActivity(FullScannerActivity);
//                }
//                bottomBar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
//                if (String.valueOf(TabMessage.get(tabId, false)).equals("Content for Clients")){
//                    Intent FullScannerActivity = new  Intent(FiveColorChangingTabsActivity.this, com.example.bottombar.sample.FullScannerActivity.class);
//                    startActivity(FullScannerActivity);
//                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
////        fruits_list.add("Pear");
//        qrcode =settings.getString("QR_CODE", null);
//        code1 = settings.getString("ODE39", null);
//        code2 = settings.getString("CODE93", null);
//        code3 = settings.getString("CODE128", null);
//        // DataBind ListView with items from ArrayAdapter
//        lv.setAdapter(arrayAdapter);
//
//        try {
//            if(!code1.equals(null))
//            {
//                fruits_list.add(code1);
//            }
//
//        }catch (Exception e){
//
//        }
//        try {
//            if(!qrcode.equals(null))
//            {
//                fruits_list.add(qrcode);
//            }
//
//        }catch (Exception e){
//
//        }
//        try {
//            if(!code2.equals(null))
//            {
//                fruits_list.add(code2);
//            }
//
//        }catch (Exception e){
//
//        }
//        try {
//            if(!code3.equals(null))
//            {
//                fruits_list.add(code3);
//            }
//
//        }catch (Exception e){
//
//        }
//
//        arrayAdapter.notifyDataSetChanged();
        bottomBar.selectTabWithId(R.id.tab_recents);
//        bottomBar.setInActiveTabColor(getResources().getColor(R.color.colorPrimaryDark));
//        bottomBar.setActiveTabColor(getResources().getColor(R.color.colorPrimary));

    }
}