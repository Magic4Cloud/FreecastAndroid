package com.cloud4magic.freecast.api;

import android.os.AsyncTask;
import android.util.Base64;

import com.cloud4magic.freecast.utils.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * DESCRIPTION:
 * This class is used to get or set video modules' parameters.
 * There are a lot of HTTP Commands opened for you, Here is a part of them, I think these are
 * the most commonly used HTTP Commands. The specific usage please refer to our document and demo.
 * <p>
 * NOTE:
 * There are many kinds of video modules, some HTTP Commands may be different in different module.
 * So if you have any problem,please send email to: steven.tang@rakwireless.com
 */

public class ParametersConfig {

    private String _ip;
    private String _username = "admin";
    private String _password = "admin";
    private OnResultListener _onResultListener;

    /**
     * Response Struct
     */
    public class Response {
        public String body = "";
        public int statusCode = 0;
        public int type;
    }

    /**
     * Request
     */
    class HttpAsyncTask extends AsyncTask<String, Void, Response> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Response doInBackground(String... params) {
            String url = params[0];
            Logger.e("xmzd", "url: " + url);
            int type = Integer.parseInt(params[1]);
            Response response = new Response();
            response.type = type;
            InputStream inputs;
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                String way = "GET";
                String body = "";
                byte[] entity = body.getBytes();
                String basic = Base64.encodeToString((_username + ":" + _password).getBytes(), Base64.NO_WRAP);
                //Set http parameters
                conn.setDoInput(true);
                conn.setRequestProperty("Accept", "*/*");
                conn.setRequestProperty("connection", "close");
                conn.setRequestProperty("Authorization", "Basic " + basic);
                if (way.equals("POST")) {
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
                } else {
                    conn.setDoOutput(false);
                }
                conn.setRequestMethod(way);

                //Set timeout
                conn.setConnectTimeout(5000);//5S
                conn.setReadTimeout(10000);//10S
                conn.connect();
                if (way.equals("POST")) {
                    OutputStream outStream = conn.getOutputStream();
                    outStream.write(entity);
                    outStream.flush();
                    outStream.close();
                }
                inputs = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputs));
                String lines = null;
                response.statusCode = conn.getResponseCode();
                while ((lines = br.readLine()) != null) {
                    response.body += lines;
                }
                response.statusCode = 200;
                Logger.e("xmzd", "body: " + response.body);
                Logger.e("Misuzu", "body: " + response.body);
            } catch (Exception e) {
                Logger.e("xmzd", "error: " + e.toString());
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response result) {
            _onResultListener.onResult(result);
        }
    }

    /**
     * Init
     */
    public ParametersConfig(String ip, String password) {
        _ip = ip;
        if (!password.matches(""))
            _password = password;
    }

    private String urlEncode(String param) {
        String ret = param;
        try {
            ret = URLEncoder.encode(ret, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }

        return ret;
    }

    private void get(String url, int type) {
        HttpAsyncTask http = new HttpAsyncTask();
        http.execute(url, Integer.toString(type));
    }

    /**
     * Set Username
     */
    public void setUsername(String username) {
        _username = username;
    }

    /**
     * Set Password
     */
    public void setPassword(String password) {
        _password = password;
    }

    public static final int UPDATE_USERNAME_PASSWORD = 0;
    public static final int GET_USERNAME_PASSWORD = 1;
    public static final int GET_SSID_LIST = 2;
    public static final int JOIN_WIFI = 3;
    public static final int GET_VERSION = 4;
    public static final int SET_RESOLUTION = 5;
    public static final int GET_RESOLUTION = 6;
    public static final int SET_FPS = 7;
    public static final int GET_FPS = 8;
    public static final int SET_QUALITY = 9;
    public static final int GET_QUALITY = 10;
    public static final int SET_GOP = 11;
    public static final int GET_GOP = 12;
    public static final int START_SD_RECORD = 13;
    public static final int STOP_SD_RECORD = 14;
    public static final int GET_SD_RECORD_STATUS = 15;
    public static final int SET_MODULE_RTC_TIME = 16;
    public static final int GET_VIDEO_FOLDER_LIST = 17;
    public static final int GET_VIDEO_LIST = 18;
    public static final int GET_SIGNAL = 19;
    public static final int UPDATE_WIFI_PWD = 20;
    public static final int RESET_WIFI_PWD = 21;

    /**
     * Description: Update Username And Password.
     * Return:
     * {"value": "0"} -- success.
     * other means failed.
     */
    public void updateUsernameAndPassword(String newUsername, String newPassword) {
        get("http://" + _ip +
                "/param.cgi?action=update&group=login&username=" +
                newUsername + "&password=" + newPassword, UPDATE_USERNAME_PASSWORD);
    }

    /**
     * Description: Get Username And Password.
     * Return: Username and password of module.
     */
    public void getUsernameAndPassword() {
        get("http://" + _ip +
                "/param.cgi?action=list&group=login", GET_USERNAME_PASSWORD);
    }

    /**
     * Description: Get SSID List.
     * Return: SSID List from module.
     */
    public void getSsidList() {
        get("http://" + _ip +
                "/server.command?command=get_wifilist", GET_SSID_LIST);
    }

    /**
     * Description: Join Wifi, used to APConfig.
     */
    public void joinWifi(String ssid, String password) {
        ssid = urlEncode(ssid);
        password = urlEncode(password);
        get("http://" + _ip +
                "/param.cgi?action=update&group=wifi&sta_ssid=" +
                ssid + "&sta_auth_key=" + password, JOIN_WIFI);
    }

    /**
     * Description: Get Version of module.
     * Return: Version of module.
     */
    public void getVersion() {
        get("http://" + _ip +
                "/server.command?command=get_version", GET_VERSION);
    }

    /**
     * Description: Set Resolution of module.
     * Parameters:
     * type: 0--Local video resolution	1--Remote video resolution
     * resolution: 0--QVGA(320X240)	1--VGA(640X480)		2--720P(1280X720)	3--1080P(1920X1080)
     * Return:
     * {"value": "0"} -- success
     * other means failed
     */
    public void setResolution(int type, int resolution) {
        get("http://" + _ip +
                "/server.command?command=set_resol&type=h264&pipe=" +
                Integer.toString(type) +
                "&value=" + Integer.toString(resolution), SET_RESOLUTION);
    }

    /**
     * Description: Get Resolution of module.
     * Parameters:
     * type: 0--Local video resolution	1--Remote video resolution
     * Return:
     * {"value": "0"} --QVGA(320X240)
     * {"value": "1"} --VGA(640X480)
     * {"value": "2"} --720P(1280X720)
     * {"value": "3"} --1080P(1920X1080)
     */
    public void getResolution(int type) {
        get("http://" + _ip +
                "/server.command?command=get_resol&type=h264&pipe=" + Integer.toString(type), GET_RESOLUTION);
    }

    /**
     * Description: Set FPS of module.
     * Parameters:
     * type: 0--Local video resolution	1--Remote video resolution
     * fps: The FPS of module you want to set (range: 1~30)
     * Return:
     * {"value": "0"} -- success.
     * other means failed.
     */
    public void setFps(int type, int fps) {
        get("http://" + _ip +
                "/server.command?command=set_max_fps&type=h264&pipe=" + Integer.toString(type) + "&value=" + fps, SET_FPS);
    }

    /**
     * Description: Get FPS of module.
     * Parameters:
     * type: 0--Local video resolution	1--Remote video resolution
     * Return as:
     * {"value": "10"} -- 10 is the FPS of module.
     */
    public void getFps(int type) {
        get("http://" + _ip +
                "/server.command?command=get_max_fps&type=h264&pipe=" + Integer.toString(type), GET_FPS);
    }

    /**
     * Description: Set quality of module.
     * Parameters:
     * type: 0--Local video resolution	1--Remote video resolution
     * quality: The quality of module you want to set (range: 0~139)
     * Return:
     * {"value": "0"} -- success.
     * other means failed.
     */
    public void setQuality(int type, int quality) {
        get("http://" + _ip +
                "/server.command?command=set_enc_quality&type=h264&pipe=" + Integer.toString(type) + "&value=" + quality, SET_QUALITY);
    }

    /**
     * Description: Get quality of module.
     * Parameters:
     * type: 0--Local video resolution	1--Remote video resolution
     * Return as:
     * {"value": "0"} -- Auto quality.
     * or:
     * {"value": "100"} -- 100 is the quality of module.
     */
    public void getQuality(int type) {
        get("http://" + _ip +
                "/server.command?command=get_enc_quality&type=h264&pipe=" + Integer.toString(type), GET_QUALITY);
    }

    /**
     * Description: Set GOP of module.
     * Parameters:
     * Only for remote video resolution
     * gop: The GOP of module you want to set (range: 0~100)
     * Return:
     * {"value": "0"} -- success.
     * other means failed.
     */
    public void setGOP(int gop) {
        get("http://" + _ip +
                "/server.command?command=set_enc_gop&type=h264&pipe=1&value=" + gop, SET_GOP);
    }

    /**
     * Description: Get GOP of module.
     * Parameters:
     * Only for remote video resolution
     * Return as:
     * {"value": "0"} -- Auto GOP.
     * or:
     * {"value": "100"} -- 100 is the GOP of module.
     */
    public void getGOP() {
        get("http://" + _ip +
                "/server.command?command=get_enc_gop&type=h264&pipe=1", GET_GOP);
    }

    /**
     * Description: Start record video to SD-Card.
     * Parameters:
     * type: 0--Local video resolution	1--Remote video resolution
     * Return:
     * {"value": "0"} -- success.
     * {"value": "-4"} -- busy, it is recording now.
     * {"value": "-22"} -- SD card storage space is not enough.
     * other means error.
     */
    public void startSdRecord(int type) {
        get("http://" + _ip +
                "/server.command?command=start_record_pipe&type=h264&pipe=" +
                Integer.toString(type), START_SD_RECORD);
    }

    /**
     * Description: Stop record video to SD-Card.
     * Parameters:
     * type: 0--Local video resolution	1--Remote video resolution
     * Return:
     * {"value": "0"} -- success.
     * other means error.
     */
    public void stopSdRecord(int type) {
        get("http://" + _ip +
                "/server.command?command=stop_record&type=h264&pipe=" +
                Integer.toString(type), STOP_SD_RECORD);
    }

    /**
     * Description: Get SD-Card Record Status.
     * type: 0--Local video resolution	1--Remote video resolution
     * Return:
     * {"value": "0"} -- free.
     * {"value": "1"} -- busy, it is recording now.
     */
    public void getSdRecordStatus(int type) {
        get("http://" + _ip +
                "/server.command?command=is_pipe_record&type=h264&pipe=" +
                Integer.toString(type), GET_SD_RECORD_STATUS);
    }

    /**
     * Description: Set RTC of module, used to SD-Card record time.
     * Parameters:
     * date: set date
     * hour: set hour
     * minute: set minute
     * second: set second
     * timezone: set timezone, as GMT-8:00
     * Return:
     * {"value": "0"} -- success.
     * other means error.
     */
    public void SetModuleRtcTime(String date, String hour, String minute, String second, String timezone) {
        get("http://" + _ip +
                "/SkyEye/ctrlServerConfig.ncgi?doAction=write&Date=" + date + "&Hour=" + hour + "&Minute="
                + minute + "&Second=" + second + "&SetNow=0&TimeZone=GMT" + timezone, SET_MODULE_RTC_TIME);
    }

    /**
     * Description: Get video folder list form SD-Card.
     * Return:
     * Video folder list of SD-Card.
     */
    public void getVideoFolderList() {
        get("http://" + _ip +
                "/param.cgi?action=list&group=videodir&fmt=link&pipe=0&type=0", GET_VIDEO_FOLDER_LIST);
    }

    /**
     * Description: Get video list form SD-Card.
     * Parameters:
     * folder: Video folder name of SD-Card.
     * Return:
     * Video list of video folder in SD-Card.
     */
    public void GetVideoList(String folder) {
        get("http://" + _ip +
                "/param.cgi?action=list&group=file&fmt=link&pipe=0&type=0&folder=" + folder, GET_VIDEO_LIST);
    }

    /**
     * Description: Get signal of module.
     * Return as:
     * {"ssid":"TP_LINK","signal_level":"-60"}
     */
    public void getSignal() {
        get("http://" + _ip +
                "/server.command?command=get_signal_level", GET_SIGNAL);
    }

    /**
     * 更新wifi密码
     */
    public void updateWifiPassword(String password)
    {
        get("http://" + _ip +
                "/param.cgi?action=update&group=wifi&ap_auth_key="+password+"&ap_auth_mode=OPEN&ap_hide_ssid=0&ap_channel=36",UPDATE_WIFI_PWD);
    }

    /**
     * 重置wifi密码
     */
    public void resetWifiPassword()
    {
        get("http://" + _ip +"/server.command?command=reset_wifi",RESET_WIFI_PWD);
    }

    //region event
    public void setOnResultListener(OnResultListener listener) {
        _onResultListener = listener;
    }

    public static interface OnResultListener {
        public void onResult(Response result);
    }
    //endregion
}
