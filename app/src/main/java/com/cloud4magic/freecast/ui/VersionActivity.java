package com.cloud4magic.freecast.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cloud4magic.freecast.MyApplication;
import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.utils.Logger;
import com.cloud4magic.freecast.utils.RetrofitHelper;
import com.cloud4magic.freecast.utils.ToastUtil;
import com.cloud4magic.freecast.widget.LoadingDialogFragment;
import com.demo.sdk.Scanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
    public String mDeviceIp = "";
    private String mDeviceName = "";
    private String mDevicePassword = "";
    public boolean isInitDevice = false;
    public boolean isWifiConnected; // 外网wifi是否连接
    LoadingDialogFragment mLoadingDialogFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mLoadingDialogFragment = LoadingDialogFragment.newInstance();
        mLoadingDialogFragment.show(getFragmentManager(), "");
        scanDevice();
        //        File upgradeFile = new File(getExternalFilesDir(null) + File.separator + "upgrade.tar");
        //        String dirPath = getExternalFilesDir(null) + File.separator + "ungrade";
        //        try {
        ////            TarManager.deTarArchive(upgradeFile, dirPath);
        //            FilesUtils filesUtils = new FilesUtils();
        //            filesUtils.getFileList(dirPath);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
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
                            mLoadingDialogFragment.dismiss();
                            break;
                        }
                    }
                    if (!found) {
                        ToastUtil.show(MyApplication.INSTANCE, MyApplication.INSTANCE.getString(R.string.device_not_found));
                        mLoadingDialogFragment.dismiss();
                    }
                } else {
                    // device not found
                    ToastUtil.show(MyApplication.INSTANCE, MyApplication.INSTANCE.getString(R.string.device_not_found));
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
                if (getWifiConnectStatus()) {
                    launchAppDetail(getPackageName(), "");
                    return;
                }
                new AlertDialog.Builder(this)
                        .setMessage(getResources().getText(R.string.switch_wifi))
                        .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
                break;
        }
    }

    /**
     * 启动到应用商店app详情界面
     *
     * @param appPkg    目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
     */
    public void launchAppDetail(String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) return;

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断外网wifi是否连接
     */
    private boolean getWifiConnectStatus() {
        isWifiConnected = false;
        RetrofitHelper.getInstance()
                .getService()
                .getDownloadLink("http://www.cv-hd.com/upgrade.txt")
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        isWifiConnected = true;
                    }
                });
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isWifiConnected;
    }


    /**
     * 获取下载链接 并下载文件
     */
    private void getDownLoadLink() {
        RetrofitHelper.getInstance()
                .getService()
                .getDownloadLink("http://www.cv-hd.com/upgrade.txt")
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(ResponseBody responseBody) {
                        try {
                            String[] array = responseBody.string().split("\n");
                            return RetrofitHelper.getInstance().getService().downloadFile(array[1]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return Observable.empty();
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        writeResponseBodyToDisk(responseBody);
                    }
                });
    }


    /**
     * 写入文件到本地
     */
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            File upgradeFile = new File(getExternalFilesDir(null) + File.separator + "upgrade.tar");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(upgradeFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Logger.e("Misuzu", "file download: " + fileSizeDownloaded + " of " + fileSize + upgradeFile.getAbsolutePath());
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


}
