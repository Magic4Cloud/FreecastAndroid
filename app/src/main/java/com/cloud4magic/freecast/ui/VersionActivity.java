package com.cloud4magic.freecast.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.cloud4magic.freecast.MyAplication;
import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.utils.Logger;
import com.cloud4magic.freecast.utils.ToastUtil;
import com.cloud4magic.freecast.widget.LoadingDialogFragment;
import com.demo.sdk.Scanner;

import java.net.InetAddress;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 软硬件版本
 * Date   2017/7/11
 * Editor  Misuzu
 */

public class VersionActivity extends AppCompatActivity {

    @BindView(R.id.device_id)
    TextView mDeviceIdText;
    @BindView(R.id.device_ip)
    TextView mDeviceIpText;
    @BindView(R.id.check_firmware)
    TextView mCheckFirmware;
    @BindView(R.id.check_version)
    TextView mCheckVersion;

    private String mDeviceId = "";
    public  String mDeviceIp = "";
    private String mDeviceName = "";
    private String mDevicePassword = "";
    public  boolean  isInitDevice = false;
    LoadingDialogFragment mLoadingDialogFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        ButterKnife.bind(this);
        initView();
    }

    private void initView()
    {
        mLoadingDialogFragment = LoadingDialogFragment.newInstance();
        mLoadingDialogFragment.show(getFragmentManager(),"");
        scanDevice();
    }


    /**
     * scan device
     */
    private void scanDevice() {
        if (isInitDevice) {
            return;
        }
        Scanner scanner = new Scanner(this);
        scanner.setOnScanOverListener(new Scanner.OnScanOverListener() {
            @Override
            public void onResult(Map<InetAddress, String> map, InetAddress inetAddress) {
                // found device
                if (map != null) {
                    boolean found = false;
                    for (Map.Entry<InetAddress, String> entry : map.entrySet()) {
                        if (entry != null) {
                            mDeviceId = entry.getValue();
                            mDeviceIp = entry.getKey().getHostAddress();
                            mDeviceName = mDeviceId;
                            int indexStart = mDeviceId.indexOf(".");
                            if (indexStart != -1) {
                                int indexEnd = mDeviceId.indexOf(".", indexStart + 1);
                                if (indexEnd != -1) {
                                    mDeviceName = mDeviceId.substring(indexStart + 1, indexEnd);
                                }
                            }
                            Logger.e("Misuzu", "device id: " + mDeviceId);
                            Logger.e("Misuzu", "device ip: " + mDeviceIp);
                            Logger.e("Misuzu", "device name: " + mDeviceName);
                            found = true;
                            isInitDevice = true;
                            mDeviceIdText.setText(mDeviceId);
                            mDeviceIpText.setText(mDeviceIp);
                            break;
                        }
                    }
                    if (!found) {
                        ToastUtil.show(MyAplication.INSTANCE, MyAplication.INSTANCE.getString(R.string.device_not_found));
                        mLoadingDialogFragment.dismiss();
                    }
                } else {
                    // device not found
                    ToastUtil.show(MyAplication.INSTANCE, MyAplication.INSTANCE.getString(R.string.device_not_found));
                    mLoadingDialogFragment.dismiss();
                }
            }
        });
        scanner.scanAll();
    }

    @OnClick({R.id.declare_back, R.id.check_firmware, R.id.check_version})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.declare_back:
                finish();
                break;
            case R.id.check_firmware:
                new AlertDialog.Builder(this)
                        .setMessage(getResources().getText(R.string.switch_wifi))
                        .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.check_version:
                new AlertDialog.Builder(this)
                        .setMessage(getResources().getText(R.string.switch_wifi))
                        .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
                break;
        }
    }
}
