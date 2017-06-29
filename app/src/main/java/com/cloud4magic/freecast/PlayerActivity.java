package com.cloud4magic.freecast;

import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloud4magic.freecast.api.ParametersConfig;
import com.cloud4magic.freecast.api.RemoteTunnel;
import com.cloud4magic.freecast.component.DeviceEntity;
import com.cloud4magic.freecast.utils.Logger;
import com.cloud4magic.freecast.utils.ToastUtil;
import com.demo.sdk.Controller;
import com.demo.sdk.DisplayView;
import com.demo.sdk.Enums;
import com.demo.sdk.Module;
import com.demo.sdk.Player;
import com.demo.sdk.Scanner;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * LiveStream Video View
 * Date    2017/6/29
 * Author  xiaomao
 */
public class PlayerActivity extends AppCompatActivity {

    private final String FREECAST_ROOT = "/Freecast";
    private final String FREECAST_PHOTO = "/Freecast/Photo";
    private final String FREECAST_VIDEO = "/Freecast/Video";
    private final String FREECAST_VOICE = "/Freecast/Voice";
    private final String FREECAST_PLAYBACK = "/Freecast/Playback";
    private String mPathPhoto = null;
    private String mPathVideo = null;
    private String mPathVoice = null;
    private String mPathPlayback = null;
    private Scanner mScanner = null;
    private ParametersConfig mParametersConfigConnect = null;
    private ParametersConfig mParametersConfigPlay = null;
    private RemoteTunnel mRemoteTunnelConnect = null;
    private RemoteTunnel mRemoteTunnelPlay = null;
    private RemoteTunnel mRemoteTunnelAudio = null;

    private String mDeviceId = "";
    private String mDeviceIp = "";
    private String mDeviceName = "";
    private String mDevicePassword = "";
    private int mDevicePort = 554;
    private int mVoicePort = 80;
    private int mFps = 20;
    private String mVersion = "";
    // hardware decode SurfaceView: 2, software decode SurfaceView: 0, software decode TextureView: 1
    private int mDecoderType = 0;
    // H264: 0, MJPEG: 1
    private int mVideoType = 0;
    // single screen: 1, two screens: 2
    private int mVideoScreen = 1;
    private int mViewWidth;
    private int mViewHeight;
    private boolean mIsLx520 = true;

    private boolean mStopTraffic = false;
    private Player mPlayer = null;
    private static Module mModule = null;
    private Controller mController = null;
    private boolean mRecording = false;
    private Enums.Pipe mPipe = Enums.Pipe.H264_PRIMARY;
    private Thread mTrafficThread = null;
    private long mTraffic = 0;
    private long mLastTraffic = 0;
    private boolean mGetTraffic = false;
    private boolean mOpenVoice = false;
    private long mVideoTime = 0;
    private FileOutputStream mFos;
    private int mConnectTime = 0;

    private Unbinder mUnbinder = null;
    @BindView(R.id.layout_loading)
    LinearLayout mLoadView;
    @BindView(R.id.video_content)
    RelativeLayout mContentView;
    @BindView(R.id.video_player)
    DisplayView mDisplayView;
    @BindView(R.id.record_time)
    TextView mRecordTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_player);
        mUnbinder = ButterKnife.bind(this);
        // show loading view
        mLoadView.setVisibility(View.VISIBLE);

        createSDCardDir();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanDevice();
    }

    /**
     * create dir on SDCard
     */
    private void createSDCardDir() {
        File sdcardDir = Environment.getExternalStorageDirectory();
        // make root dir
        mkdirs(sdcardDir.getPath() + FREECAST_ROOT);
        // init path
        mPathPhoto = mkdirs(sdcardDir.getPath() + FREECAST_PHOTO);
        mPathVideo = mkdirs(sdcardDir.getPath() + FREECAST_VIDEO);
        mPathVoice = mkdirs(sdcardDir.getPath() + FREECAST_VOICE);
        mPathPlayback = mkdirs(sdcardDir.getPath() + FREECAST_PLAYBACK);
    }

    /**
     * make directory
     *
     * @param dir String
     * @return String
     */
    private String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * scan device
     */
    private void scanDevice() {
        mScanner = new Scanner(this);
        mScanner.setOnScanOverListener(new Scanner.OnScanOverListener() {
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
                            Logger.e("xmzd", "device id: " + mDeviceId);
                            Logger.e("xmzd", "device ip: " + mDeviceIp);
                            Logger.e("xmzd", "device name: " + mDeviceName);
                            found = true;
                            initParametersConfigConnect();
                            break;
                        }
                    }
                    if (!found) {
                        ToastUtil.show(PlayerActivity.this, "Device not found 1!");
                    }
                } else {
                    // device not found
                    ToastUtil.show(PlayerActivity.this, "Device not found 2!");
                }
            }
        });
        mScanner.scanAll();
    }

    /**
     * config and start connect
     */
    private void initParametersConfigConnect() {
        mDevicePassword = DeviceEntity.getDevicePasswordFromId(PlayerActivity.this, mDeviceId);
        mDevicePassword = "admin";
        mParametersConfigConnect = new ParametersConfig(mDeviceId + ":" + 80, mDevicePassword);
        mParametersConfigConnect.setOnResultListener(new ParametersConfig.OnResultListener() {
            @Override
            public void onResult(ParametersConfig.Response result) {
                if (result == null) {
                    // TODO: 2017/6/29
                    return;
                }
                switch (result.type) {
                    case ParametersConfig.GET_USERNAME_PASSWORD:
                        Logger.e("xmzd", "GET_USERNAME_PASSWORD");
                        getPassword(result);
                        break;
                    case ParametersConfig.SET_MODULE_RTC_TIME:
                        Logger.e("xmzd", "SET_MODULE_RTC_TIME");
                        setRtcTime(result);
                        break;
                    case ParametersConfig.GET_FPS:
                        Logger.e("xmzd", "GET_FPS");
                        getFps(result);
                        break;
                    case ParametersConfig.GET_VERSION:
                        Logger.e("xmzd", "GET_VERSION");
                        getVersion(result);
                        break;
                }
            }
        });
        connect();
    }

    /**
     * connect device
     */
    private void connect() {
        if ("127.0.0.1".equals(mDeviceIp)) {
            if (mRemoteTunnelConnect == null) {
                mRemoteTunnelConnect = new RemoteTunnel(getApplicationContext());
            }
            mRemoteTunnelConnect.openTunnel(0, 80, 80, mDeviceId);
            mRemoteTunnelConnect.setOnResultListener(new RemoteTunnel.OnResultListener() {
                @Override
                public void onResult(int id, final String result) {
                    if (result == null || "CONNECT_TIMEOUT".equals(result) || "NTCS_CLOSED".equals(result) ||
                            "NTCS_UNKNOWN".equals(result) || "FAILED".equals(result)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(getApplicationContext(), result);
                            }
                        });
                        if (mRemoteTunnelConnect != null) {
                            mRemoteTunnelConnect.closeTunnels();
                            mRemoteTunnelConnect = null;
                        }
                    } else {
                        getFpsAndVersion();
                    }
                }
            });
        } else {
            getFpsAndVersion();
        }
    }

    /**
     * fps version
     */
    private void getFpsAndVersion() {
        mParametersConfigConnect.getVersion();
    }

    /**
     * ParametersConfig.GET_VERSION
     */
    private void getVersion(ParametersConfig.Response result) {
        if (result.statusCode == 200) {
            mVersion = result.body.replace(" ", "").toLowerCase();
            String ff = result.body.replace(" ", "");
            String keyStr = "\"value\":\"";
            int index = ff.indexOf(keyStr);
            if (index != -1) {
                int index2 = ff.indexOf("\"", index + keyStr.length());
                if (index2 != -1) {
                    mVersion = ff.substring(index + keyStr.length(), index2);
                }
            }
        }
        mParametersConfigConnect.getFps(0);
    }

    /**
     * ParametersConfig.GET_FPS
     */
    private void getFps(ParametersConfig.Response result) {
        if (result.statusCode == 200) {
            String ff = result.body.replace(" ", "");
            String keyStr = "\"value\":\"";
            int index = ff.indexOf(keyStr);
            if (index != -1) {
                int index2 = ff.indexOf("\"", index + keyStr.length());
                if (index2 != -1) {
                    String fpsStr = ff.substring(index + keyStr.length(), index2);
                    mFps = Integer.parseInt(fpsStr);
                }
            }
        }
        if (mRemoteTunnelConnect != null) {
            mRemoteTunnelConnect.closeTunnels();
            mRemoteTunnelConnect = null;
        }
        //Save Password
        DeviceEntity.modifyDevicePasswordById(PlayerActivity.this, mDeviceId, mDevicePassword);
        // start play
        startPlay();
    }

    /**
     * ParametersConfig.SET_MODULE_RTC_TIME
     */
    private void setRtcTime(ParametersConfig.Response result) {
        if (result.statusCode != 200) {
            ToastUtil.show(PlayerActivity.this, "Sync SDCard failed");
        }
        if (mRemoteTunnelConnect != null) {
            mRemoteTunnelConnect.closeTunnels();
            mRemoteTunnelConnect = null;
        }
        //Save Password
        DeviceEntity.modifyDevicePasswordById(PlayerActivity.this, mDeviceId, mDevicePassword);
        // start play
        startPlay();
    }

    /**
     * ParametersConfig.GET_USERNAME_PASSWORD
     */
    private void getPassword(ParametersConfig.Response result) {
        if (result.statusCode == 200) {
            String psd = DeviceEntity.Find_Str(result.body, DeviceEntity._passwordKey);
            if (psd.equals(mDevicePassword)) {
                Date curDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String date = sdf.format(curDate);
                String hour = curDate.getHours() + "";
                if (hour.length() == 1)
                    hour = "0" + hour;
                String min = curDate.getMinutes() + "";
                if (min.length() == 1)
                    min = "0" + min;
                String sec = curDate.getSeconds() + "";
                if (sec.length() == 1)
                    sec = "0" + sec;
                TimeZone tz = TimeZone.getDefault();
                String tzStr = tz.getDisplayName(false, TimeZone.SHORT);
                tzStr = tzStr.replace("格林尼治标准时间", "");
                // Set Sd Time
                mParametersConfigConnect.SetModuleRtcTime(date, hour, min, sec, tzStr);
            } else {
                ToastUtil.show(PlayerActivity.this, "Connect failed with error password!");
            }
        } else {
            ToastUtil.show(PlayerActivity.this, "Connect failed !");
        }
    }

    /**
     * start play
     */
    private void startPlay() {
        if (mVersion.toLowerCase().contains("wifiv")) {
            mIsLx520 = false;
        } else {
            mIsLx520 = true;
        }
        if ("127.0.0.1".equals(mDeviceIp)) {
            if (mRemoteTunnelPlay == null) {
                mRemoteTunnelPlay = new RemoteTunnel(getApplicationContext());
            }
            mRemoteTunnelPlay.openTunnel(1, mDevicePort, mDevicePort, mDeviceId);
            mRemoteTunnelPlay.setOnResultListener(new RemoteTunnel.OnResultListener() {
                @Override
                public void onResult(int id, String result) {
                    if ("CONNECT_TIMEOUT".equals(result) || "NTCS_CLOSED".equals(result) ||
                            "NTCS_UNKNOWN".equals(result) || "FAILED".equals(result)) {
                        ToastUtil.show(PlayerActivity.this, "Connect Failed !");
                        if (mRemoteTunnelPlay != null) {
                            mRemoteTunnelPlay.closeTunnels();
                            mRemoteTunnelPlay = null;
                        }
                        stop();
                        finish();
                    } else {
                        playVideo();
                        audioRemoteConnect();
                        Logger.e("xmzd", "mDevicePort: " + mDevicePort);
                    }
                }
            });
        } else {
            mVoicePort = 80;
            initParametersConfigPlay();
            // get sd card recode state
            mParametersConfigPlay.getSdRecordStatus(0);
            playVideo();
        }
    }

    /**
     * play video
     */
    public void playVideo() {
        if (!mIsLx520) {
            getResolution();
        }

        mConnectTime = 0;

        if (mModule == null) {
            mModule = new Module(this);
        } else {
            mModule.setContext(this);
        }
        mModule.setLogLevel(Enums.LogLevel.VERBOSE);
        mModule.setUsername("admin");
        mModule.setPassword(mDevicePassword);
        mModule.setPlayerPort(mDevicePort);
        mModule.setModuleIp(mDeviceIp);
        mController = mModule.getController();
        mPlayer = mModule.getPlayer();
        mPlayer.setRecordFrameRate(mFps);
        mPlayer.setAudioOutput(mOpenVoice);
        mPlayer.setTimeout(20000);
        mPlayer.setOnTimeoutListener(new Player.OnTimeoutListener() {
            @Override
            public void onTimeout() {
                // TODO player timeout
            }
        });
        mRecording = mPlayer.isRecording();
        mPlayer.setDisplayView(getApplication(), mDisplayView, null, mDecoderType);
        mPlayer.setOnStateChangedListener(new Player.OnStateChangedListener() {
            @Override
            public void onStateChanged(Enums.State state) {
                updateState(state);
            }
        });
        mPlayer.setOnVideoSizeChangedListener(new Player.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(int width, int height) {

            }

            @Override
            public void onVideoScaledSizeChanged(int arg0, int arg1) {
                // TODO Auto-generated method stub

            }
        });

        if (mPlayer.getState() == Enums.State.IDLE) {
            if ("127.0.0.1".equals(mDeviceIp)) {
                if (mVideoType == 0) {
                    mPipe = Enums.Pipe.H264_SECONDARY;
                } else {
                    mPipe = Enums.Pipe.MJPEG_PRIMARY;
                }
                try {
                    mPlayer.setImageSize(320, 240);
                    if ("www.sunnyoptical.com".equals(mDeviceId)) {
                        // to support specific module
                        String url = "rtsp://" + mDeviceIp + "/live1.sdp";
                        mPlayer.playUrl(url, Enums.Transport.TCP);
                    } else {
                        mPlayer.play(mPipe, Enums.Transport.TCP);
                    }
                } catch (Exception e) {
                    Logger.e("xmzd", "TCP play flied with password error");
                }
            } else {
                if (mVideoType == 0) {
                    mPipe = Enums.Pipe.H264_PRIMARY;
                } else {
                    mPipe = Enums.Pipe.MJPEG_PRIMARY;
                }
                try {
                    mPlayer.setImageSize(1280, 720);
                    if ("www.sunnyoptical.com".equals(mDeviceId)) {
                        String url = "rtsp://" + mDeviceIp + "/live1.sdp";
                        mPlayer.playUrl(url, Enums.Transport.UDP);
                    } else {
                        mPlayer.play(mPipe, Enums.Transport.UDP);
                    }
                } catch (Exception e) {
                    Logger.e("xmzd", "UDP play flied with password error");
                }
            }
        } else {
            if (mPlayer != null)
                mPlayer.stop();
        }
        updateState(mPlayer.getState());
        final int id = android.os.Process.myUid();
        mLastTraffic = TrafficStats.getUidRxBytes(id);
        // start a thread
        mTrafficThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    if (mStopTraffic) {
                        break;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Reconnect when disconnected
                            if (mPlayer != null) {
                                Logger.e("xmzd", "Reconnect...");
                                if (mPlayer.getState() == Enums.State.IDLE) {
                                    mLoadView.setVisibility(View.VISIBLE);
                                    mContentView.setVisibility(View.GONE);
                                    mPlayer.stop();
                                    if ("127.0.0.1".equals(mDeviceIp)) {
                                        if (mVideoType == 0) {
                                            mPipe = Enums.Pipe.H264_SECONDARY;
                                        } else {
                                            mPipe = Enums.Pipe.MJPEG_PRIMARY;
                                        }
                                        if ("www.sunnyoptical.com".equals(mDeviceId)) {
                                            String url = "rtsp://" + mDeviceIp + "/live1.sdp";
                                            mPlayer.playUrl(url, Enums.Transport.TCP);
                                        } else {
                                            mPlayer.play(mPipe, Enums.Transport.TCP);
                                        }
                                    } else {
                                        if (mVideoType == 0) {
                                            mPipe = Enums.Pipe.H264_PRIMARY;
                                        } else {
                                            mPipe = Enums.Pipe.MJPEG_PRIMARY;
                                        }
                                        if ("www.sunnyoptical.com".equals(mDeviceId)) {
                                            String url = "rtsp://" + mDeviceIp + "/live1.sdp";
                                            mPlayer.playUrl(url, Enums.Transport.UDP);
                                        } else {
                                            mPlayer.play(mPipe, Enums.Transport.UDP);
                                        }
                                    }
                                }
                            }
                            // recording
                            if (mRecording) {
                                mVideoTime++;
                                mRecordTime.setVisibility(View.VISIBLE);
                                mRecordTime.setText("REC " + showTimeCount(mVideoTime));
                            } else {
                                mVideoTime = 0;
                                mRecordTime.setVisibility(View.INVISIBLE);
                            }

                            /*if (mIsSDRecord) {
                                mSDVideoTime++;
                                video_sd_record_time.setVisibility(View.VISIBLE);
                                video_sd_record_time.setText("REC " + showTimeCount(sdvideotime));
                            } else {
                                sdvideotime = 0;
                                video_sd_record_time.setVisibility(View.INVISIBLE);
                            }*/
                        }
                    });
                    // sleep 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    if (mPlayer.getState() != Enums.State.PLAYING) {
                        mConnectTime++;
                        if (mConnectTime > 30) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stop();
                                    finish();
                                }
                            });
                        }
                        Logger.e("xmzd", "connect time: " + mConnectTime);
                    }
                }
            }
        });
        mTrafficThread.start();
    }

    /**
     * audio remote connect
     */
    private void audioRemoteConnect() {
        if (mRemoteTunnelAudio == null) {
            mRemoteTunnelAudio = new RemoteTunnel(getApplicationContext());
        }
        mRemoteTunnelAudio.openTunnel(0, 80, 3333, mDeviceId);
        mRemoteTunnelAudio.setOnResultListener(new RemoteTunnel.OnResultListener() {
            @Override
            public void onResult(int id, String result) {
                if ("CONNECT_TIMEOUT".equals(result) || "NTCS_CLOSED".equals(result) ||
                        "NTCS_UNKNOWN".equals(result) || "FAILED".equals(result)) {
                    ToastUtil.show(getApplicationContext(), result);
                    if (mRemoteTunnelAudio != null) {
                        mRemoteTunnelAudio.closeTunnels();
                        mRemoteTunnelAudio = null;
                    }
                } else {
                    mVoicePort = 3333;
                    initParametersConfigPlay();
                    mParametersConfigPlay.getSdRecordStatus(0);//获取SD卡录制状态
                    Logger.e("xmzd", "VoicePort: " + mVoicePort);
                }
            }
        });
    }

    /**
     * init config play
     */
    private void initParametersConfigPlay() {
        mParametersConfigPlay = new ParametersConfig(mDeviceIp + ":" + mVoicePort, mDevicePassword);
        mParametersConfigPlay.setOnResultListener(new ParametersConfig.OnResultListener() {
            @Override
            public void onResult(ParametersConfig.Response result) {
                if (result.statusCode == 200) {
                    if (result.type == ParametersConfig.GET_RESOLUTION) {
                        Logger.e("xzmd", "ParametersConfig.GET_RESOLUTION");
                        /*String ff = result.body.replace(" ", "");
                        Log.e("Get_Resolution==>", ff);
                        String keyStr = "\"value\":\"";
                        int index = ff.indexOf(keyStr);
                        if (index != -1) {
                            int index2 = ff.indexOf("\"", index + keyStr.length());
                            if (index2 != -1) {
                                _pipe_not_520 = ff.substring(index + keyStr.length(), index2);
                                if (_pipe_not_520.equals("0")) {
                                    _videoPipe.setText(getString(R.string.video_BD));
                                } else if (_pipe_not_520.equals("1")) {
                                    _videoPipe.setText(getString(R.string.video_BD));
                                } else if (_pipe_not_520.equals("2")) {
                                    _videoPipe.setText(getString(R.string.video_HD));
                                } else if (_pipe_not_520.equals("3")) {
                                    _videoPipe.setText(getString(R.string.video_VHD));
                                }
                            }
                        }*/
                    } else if (result.type == ParametersConfig.GET_SD_RECORD_STATUS) {
                        Logger.e("xzmd", "ParametersConfig.GET_SD_RECORD_STATUS");
                        /*Log.e("result.body", result.body);
                        Log.e("==>", "Get sd-record status success");
                        int index = result.body.indexOf(value);
                        if (index != -1) {
                            int index1 = result.body.indexOf(end, index + value.length());
                            if (index1 != -1) {
                                String rString = result.body.substring(index + value.length(), index1);
                                if (rString.equals("0")) {
                                    Is_Sd_Record = false;
                                    videoSdRecordImg.setImageResource(R.drawable.ico_sdcard);
                                } else if (rString.equals("1")) {
                                    Is_Sd_Record = true;
                                    videoSdRecordImg.setImageResource(R.drawable.ico_sdcarding);
                                } else {
                                    Is_Sd_Record = false;
                                    videoSdRecordImg.setImageResource(R.drawable.ico_sdcard);
                                    if (rString.equals("-1")) {//打开文件错误
                                    } else if (rString.equals("-2")) {//打开设备错误
                                    }
                                }
                            }
                        } else {
                            Log.e("==>", "Get sd-record status failed");
                        }*/
                    } else if (result.type == ParametersConfig.START_SD_RECORD) {
                        Logger.e("xzmd", "ParametersConfig.START_SD_RECORD");
                        /*int index = result.body.indexOf(value);
                        if (index != -1) {
                            int index1 = result.body.indexOf(end, index + value.length());
                            if (index1 != -1) {
                                String rString = result.body.substring(index + value.length(), index1);
                                int c = Integer.parseInt(rString);
                                if (c < 0) {
                                    Is_Sd_Record = false;
                                    videoSdRecordImg.setImageResource(R.drawable.ico_sdcard);
                                    if (c == -4) {
                                        Toast.show(VideoPlay.this, "Sd-card is recording");
                                    } else {
                                        Toast.show(VideoPlay.this, "Sd-card not found");
                                    }
                                } else {
                                    Is_Sd_Record = true;
                                    videoSdRecordImg.setImageResource(R.drawable.ico_sdcarding);
                                    Toast.show(VideoPlay.this, "Start Sd-Record success");
                                }
                            }
                        }*/
                    } else if (result.type == ParametersConfig.STOP_SD_RECORD) {
                        Logger.e("xzmd", "ParametersConfig.STOP_SD_RECORD");
                        /*int index = result.body.indexOf(value);
                        if (index != -1) {
                            int index1 = result.body.indexOf(end, index + value.length());
                            if (index1 != -1) {
                                String rString = result.body.substring(index + value.length(), index1);
                                if (rString.equals("0")) {
                                    Is_Sd_Record = false;
                                    Toast.show(VideoPlay.this, "Stop Sd-Record success");
                                    videoSdRecordImg.setImageResource(R.drawable.ico_sdcard);
                                } else {
                                    Is_Sd_Record = true;
                                    videoSdRecordImg.setImageResource(R.drawable.ico_sdcarding);
                                    Toast.show(VideoPlay.this, "Stop Sd-Record failed");
                                }
                            }
                        }*/
                    }
                } else {// status code not 200
                    /*if (result.type == ParametersConfig.START_SD_RECORD) {
                        Is_Sd_Record = false;
                        Toast.show(VideoPlay.this, "Start Sd-Record failed");
                    }
                    if (result.type == ParametersConfig.STOP_SD_RECORD) {
                        Is_Sd_Record = true;
                        Toast.show(VideoPlay.this, "Stop Sd-Record failed");
                    }*/
                }
            }
        });
    }

    /**
     * get video resolution
     */
    private void getResolution() {
        mParametersConfigPlay.getResolution(0);
    }

    /**
     * update state
     */
    private void updateState(Enums.State state) {
        switch (state) {
            case IDLE:
                break;
            case PREPARING:
                break;
            case PLAYING:
                mGetTraffic = true;
                mContentView.setVisibility(View.VISIBLE);
                mLoadView.setVisibility(View.GONE);
            case STOPPED:
                mGetTraffic = false;
                break;
        }
    }

    /**
     * recording time
     */
    private String showTimeCount(long time) {
        if (time >= 360000) {
            return "00:00:00";
        }
        String timeCount = "";
        long hourc = time / 3600;
        String hour = "0" + hourc;
        hour = hour.substring(hour.length() - 2, hour.length());

        long minuec = (time - hourc * 3600) / (60);
        String minue = "0" + minuec;
        minue = minue.substring(minue.length() - 2, minue.length());

        long secc = (time - hourc * 3600 - minuec * 60);
        String sec = "0" + secc;
        sec = sec.substring(sec.length() - 2, sec.length());
        timeCount = minue + ":" + sec;
        return timeCount;
    }

    /**
     * stop
     */
    private void stop() {
        mStopTraffic = true;
        if (mPlayer != null) {
            mPlayer.stop();
        }
        if (mRemoteTunnelPlay != null) {
            mRemoteTunnelPlay.closeTunnels();
            mRemoteTunnelPlay = null;
        }
        if (mRemoteTunnelAudio != null) {
            mRemoteTunnelAudio.closeTunnels();
            mRemoteTunnelAudio = null;
        }
        if (mRemoteTunnelConnect != null) {
            mRemoteTunnelConnect.closeTunnels();
            mRemoteTunnelConnect = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
