package com.miaxis.sm93m;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.miaxis.justouch.JustouchFingerAPI;
import com.miaxis.sm93m.databinding.ActivityMainBinding;
import com.miaxis.sm93m.fragment.BaseBindingFragment;
import com.miaxis.sm93m.fragment.FragmentExport;
import com.miaxis.sm93m.fragment.FragmentInfo;
import com.miaxis.sm93m.fragment.FragmentSetting;
import com.miaxis.sm93m.fragment.FragmentTemplate;
import com.miaxis.sm93m.fragment.FragmentTemplateHost;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.zz.jni.FingerLiveApi;
import org.zz.jni.mxComFingerDriver;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, View.OnClickListener {
    private static final String TAG = "MainActivity";

    @SuppressLint("SimpleDateFormat")
    public static final DateFormat DATEFORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
    //public static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
    private ActivityMainBinding mBinding;
    private MainViewModel mMainViewModel;
    private final List<BaseBindingFragment<?>> mFragments = new ArrayList<>();

    private static final String[] PERMISSIONS_CAMERA_AND_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.app_name) + "-V" + BuildConfig.VERSION_NAME);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setOnClickListener(this);
        mMainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mBinding.setViewModel(mMainViewModel);
        mBinding.setLifecycleOwner(this);

        FingerLiveApi.initAlg("");
        mMainViewModel.setJustouchApi(new JustouchFingerAPI());
        mMainViewModel.setContext(getApplicationContext());

        mFragments.add(new FragmentSetting("Setting"));
        mFragments.add(new FragmentTemplateHost("Template(Host)"));
        mFragments.add(new FragmentTemplate("Template(Module)"));
        mFragments.add(new FragmentExport("Export"));
        mFragments.add(new FragmentInfo("Info"));

        mBinding.tlTab.addOnTabSelectedListener(this);
        for (int i = 0; i < mFragments.size(); i++) {
            BaseBindingFragment<?> fragment = mFragments.get(i);
            String title = fragment.getTitle();
            TabLayout.Tab tab = mBinding.tlTab.newTab();
            tab.setText(title);
            tab.setTag(fragment);
            mBinding.tlTab.addTab(tab, i == 0);
        }
        registerReceiver();

        mMainViewModel.bm.observe(this, bitmap -> {
            if (bitmap == null) {
                mBinding.ivFinger.setImageResource(R.drawable.ic_fingerprint);
            } else {
                mBinding.ivFinger.setImageBitmap(bitmap);
            }
        });
        //open log
        com.miaxis.common.LogUtils.setLogLevel(-1);

        requestPermissions(this);

        //Power supply for Miaxis equipment
        //Intent intent = new Intent("com.miaxis.power");
        //intent.putExtra("type",0x12);
        //intent.putExtra("value",true);
        //sendBroadcast(intent);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.e(TAG, "onTabSelected" + tab.getText());
        Fragment tag = (Fragment) tab.getTag();
        if (tag != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, tag).commit();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Log.e(TAG, "onTabUnselected" + tab.getText());
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Log.e(TAG, "onTabReselected" + tab.getText());
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mUsbReceiver = new USBReceiver();
        registerReceiver(mUsbReceiver, filter);
    }

    private USBReceiver mUsbReceiver;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == mBinding.btnOpen.getId()) {
            if (Boolean.TRUE.equals(mMainViewModel.firstInit.getValue())) {
                mMainViewModel.setFingerDriverApi(new mxComFingerDriver());
                mMainViewModel.firstInit.setValue(false);
            }
            mMainViewModel.checkConnected();
        } else if (id == mBinding.btnCapture.getId()) {
            mMainViewModel.getFinalImage();
        } else if (id == mBinding.btnDetectFinger.getId()) {
            mMainViewModel.detectFinger();
        } else if (id == mBinding.btnVideo.getId()) {
            if (mMainViewModel.video.getValue() != null && mMainViewModel.video.getValue()) {
                mMainViewModel.viewOff();
            } else {
                mMainViewModel.viewOn();
            }
        }
    }

    private class USBReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 这里可以拿到插入的USB设备对象
            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            Log.e(TAG, "onReceive: " + usbDevice.toString());
            int interfaceCount = usbDevice.getInterfaceCount();
            for (int i = 0; i < interfaceCount; i++) {
                UsbInterface anInterface = usbDevice.getInterface(i);
                Log.e(TAG, "UsbInterface: " + anInterface.toString());
                int interfaceClass = anInterface.getInterfaceClass();
                Log.e(TAG, "interfaceClass: " + interfaceClass);
            }
            switch (intent.getAction()) {
                case UsbManager.ACTION_USB_DEVICE_ATTACHED: // 插入USB设备
                    Log.e(TAG, "onReceive:  插入USB设备");
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED: // 拔出USB设备
                    Log.e(TAG, "onReceive:  拔出USB设备");
                    //if (usbDevice.getVendorId() == 1046 && usbDevice.getProductId() == 20497){
                    //    mMainViewModel.closeDevice();
                    //}
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
        mFragments.clear();
        FingerLiveApi.freeAlg();
        mMainViewModel.getJustouchApi().free();
    }

    public void unregisterReceiver() {
        unregisterReceiver(mUsbReceiver);
    }

    public static boolean requestPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int storagePermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);

            if (storagePermission != PackageManager.PERMISSION_GRANTED ||
                    cameraPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(PERMISSIONS_CAMERA_AND_STORAGE, 0x01);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0x01) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //getData(AcRed,"rider.txt");
            } else {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Please add file read and write permissions", Toast.LENGTH_LONG).show());
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}