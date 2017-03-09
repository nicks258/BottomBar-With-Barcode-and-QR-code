package com.example.bottombar.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Code_39 extends BaseScannerActivity implements MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static int flag = 0;
    private CoordinatorLayout coordinatorLayout;
    private static int error = 0;
    private static int serial = 0;
    private String Result ="";
    private static int mac = 0;
    private Snackbar snackbar;
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private SharedPreferences settings;
    SharedPreferences.Editor editor;
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private TextView MacTv,SerialTv;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if(state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }

        setContentView(R.layout.activity_code_39);
        setupToolbar();
        settings = this.getSharedPreferences("ARUBA", Context.MODE_PRIVATE);
        MacTv= (TextView)findViewById(R.id.textView1);
        SerialTv = (TextView) findViewById(R.id.textView);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
//        if (flag==0) {
//            final ProgressDialog progress = new ProgressDialog(this);
//            progress.setTitle("Connecting");
//            progress.setMessage("Please wait while we connect to devices...");
//            progress.show();
//
//            Runnable progressRunnable = new Runnable() {
//
//                @Override
//                public void run() {
//                    progress.cancel();
//                }
//            };
//
//            Handler pdCanceller = new Handler();
//            pdCanceller.postDelayed(progressRunnable, 3000);
//
//        }
        mScannerView = new ZXingScannerView(this);
        setupFormats();
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Resume","Resume");
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;

        if(mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);


        if(mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if(mFlash) {
                    item.setTitle(R.string.flash_on);
                } else {
                    item.setTitle(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);
                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if(mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on);
                } else {
                    item.setTitle(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                return true;
            case R.id.menu_formats:
                DialogFragment fragment = FormatSelectorDialogFragment.newInstance(this, mSelectedIndices);
                fragment.show(getSupportFragmentManager(), "format_selector");
                return true;
            case R.id.menu_camera_selector:
                mScannerView.stopCamera();
                DialogFragment cFragment = CameraSelectorDialogFragment.newInstance(this, mCameraId);
                cFragment.show(getSupportFragmentManager(), "camera_selector");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
        }

//        showMessageDialog("Contents = " + rawResult.getText() + ", Format = " + rawResult.getBarcodeFormat().toString());
        if (flag == 0) {
            boolean s = validate(rawResult.getText());
            Log.i("validMAc", String.valueOf(s));
//            showMessageDialog("MAC Address added Successfully");
//            StringAlignUtils util = new StringAlignUtils(180, StringAlignUtils.Alignment.RIGHT);
            StringAlignUtils utill = new StringAlignUtils(10, StringAlignUtils.Alignment.RIGHT);
//            System.out.println( util.format(sampleText) );
                Result = rawResult.getText();
            if (s == true) {
                mac++;
                flag++;
                editor = settings.edit();
                editor.putString("CODE39", rawResult.getText());
                Log.d("CODE39", rawResult.getText());
                editor.commit();
                MacTv.setText("Mac: " + rawResult.getText());
                mScannerView.resumeCameraPreview(this);
//                snackbar = Snackbar
//                        .make(coordinatorLayout, ("MAC Address :       ") + (rawResult.getText()), Snackbar.LENGTH_INDEFINITE)
//                        .setAction("Continue", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                snackbar.dismiss();
//                                if (flag == 1) {
//                                    if (error == 0) {
//                                        final ProgressDialog progress = new ProgressDialog(Code_39.this);
//                                        progress.setTitle("Validating");
//                                        progress.setMessage("Please wait while we Validating MAC Address ...");
//                                        progress.show();
//
//                                        Runnable progressRunnable = new Runnable() {
//
//                                            @Override
//                                            public void run() {
//                                                progress.cancel();
//                                            }
//                                        };
//
//                                        Handler pdCanceller = new Handler();
//                                        pdCanceller.postDelayed(progressRunnable, 3000);
//                                        mScannerView.resumeCameraPreview(Code_39.this);
//                                    }
//
//
//                                } else
//                                    Code_39.this.finish();
//
//                            }
//                        });


//                // Changing message text color
//                snackbar.setActionTextColor(Color.WHITE);
//
//                // Changing action button text color
//                View sbView = snackbar.getView();
//                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//                textView.setTextColor(Color.WHITE);
//
//                snackbar.show();

            } else {
                flag++;
                serial++;
                editor = settings.edit();
                editor.putString("CODE93", rawResult.getText());
                Log.d("CODE93", rawResult.getText());
                editor.commit();
                SerialTv.setText("SN: " + rawResult.getText());
                mScannerView.resumeCameraPreview(this);
//                snackbar = Snackbar
//                        .make(coordinatorLayout, ("Serial Number :       ") + (rawResult.getText()), Snackbar.LENGTH_INDEFINITE)
//                        .setAction("Continue", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                snackbar.dismiss();
//                                if (flag == 1) {
//                                    if (error == 0) {
//                                        final ProgressDialog progress = new ProgressDialog(Code_39.this);
//                                        progress.setTitle("Validating");
//                                        progress.setMessage("Please wait while we Validating MAC Address ...");
//                                        progress.show();
//
//                                        Runnable progressRunnable = new Runnable() {
//
//                                            @Override
//                                            public void run() {
//                                                progress.cancel();
//                                            }
//                                        };
//
//                                        Handler pdCanceller = new Handler();
//                                        pdCanceller.postDelayed(progressRunnable, 3000);
//                                        mScannerView.resumeCameraPreview(Code_39.this);
//                                    }
//
//
//                                } else
//                                    Code_39.this.finish();
//
//                            }
//                        });
//
//
//                // Changing message text color
//                snackbar.setActionTextColor(Color.WHITE);
//
//                // Changing action button text color
//                View sbView = snackbar.getView();
//                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//                textView.setTextColor(Color.WHITE);
//
//                snackbar.show();
            }
        } else if (flag == 1) {
//            if (settings.getString("CODE39", null).equals(settings.getString("CODE93", null))) {
//                mScannerView.resumeCameraPreview(this);
//            }
//            else if (settings.getString("CODE93", null).equals(settings.getString("CODE39", null))) {
//                mScannerView.resumeCameraPreview(this);
//
//            }
             {
                if (serial == 0 && !validate(rawResult.getText())) {

                    editor = settings.edit();
                    editor.putString("CODE93", rawResult.getText());
                    editor.commit();
                    Code_39.this.finish();
                    flag++;
                    SerialTv.setText("SN: " + rawResult.getText());
//                if (settings.getString("CODE39", null).equals(settings.getString("CODE93", null))) {
//                    showMessageDialog("Barcode Already Scanned");
//                    error = 1;
//                    mScannerView.resumeCameraPreview(this);
//
//                } else {
//                    flag++;
//                    showMessageDialog("Device Added Successfully");
//                }
                } else if (validate(rawResult.getText()) && mac == 1) {
                    mScannerView.resumeCameraPreview(this);
                    Toast.makeText(this,"Mac Address Already Scanned",Toast.LENGTH_SHORT).show();

                } else if (!validate(rawResult.getText())) {
                    Log.d("Invalid", "Invalid");
                    Toast.makeText(this,"Serial Number Already Scanned",Toast.LENGTH_SHORT).show();
                    mScannerView.resumeCameraPreview(this);
                    Log.d("Invalid", "Invalid");

                } else if (validate(rawResult.getText()) && mac == 0) {
                    MacTv.setText("Mac: " + rawResult.getText());
                    editor = settings.edit();
                    editor.putString("CODE39", rawResult.getText());
                    editor.commit();
                    Code_39.this.finish();
                }
            }

        }
    }
    public void showMessageDialog(String message) {
        DialogFragment fragment = MessageDialogFragment.newInstance("Scan Results", message, this);
        fragment.show(getSupportFragmentManager(), "scan_results");
    }

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if(fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Resume the camera
//        Intent Home = new Intent(Code_39.this,FiveColorChangingTabsActivity.class);
//        startActivity(Home);

        if (flag==1) {
            if (error == 0) {
                final ProgressDialog progress = new ProgressDialog(this);
                progress.setTitle("Validating");
                progress.setMessage("Please wait while we Validating MAC Address ...");
                progress.show();

                Runnable progressRunnable = new Runnable() {

                    @Override
                    public void run() {
                        progress.cancel();
                    }
                };

                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 3000);
                mScannerView.resumeCameraPreview(this);
            }


        }
        else
            this.finish();
    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if(mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            mSelectedIndices.add(5);
//            for(int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
//
//                Log.i("Scanner",String.valueOf(ZXingScannerView.ALL_FORMATS.size()));
//            }
        }

        for(int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if(mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }
    public boolean validate(String mac) {
        Pattern p = Pattern.compile("^([0-9A-Fa-f]{2}){5}([0-9A-Fa-f]{2})$");
        Matcher m = p.matcher(mac);
        return m.find();
    }
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        closeMessageDialog();
        closeFormatsDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag=0;
        error =0;
        mac=0;
        serial = 0;
    }
}
