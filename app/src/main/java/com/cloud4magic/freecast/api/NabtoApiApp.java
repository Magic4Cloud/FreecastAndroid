package com.cloud4magic.freecast.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Collection;

public class NabtoApiApp {
    private Context context;
    private NabtoApi nabtoApi;
    private Session session;
    
    public NabtoApiApp(Context context) {
        this.context = context;
        this.nabtoApi = new NabtoApi(context);
    }

    public NabtoStatus init(String email, String password) {
        nabtoApi.init(email, password);
        NabtoStatus status = openSession(email, password);
        if (status == NabtoStatus.NO_PROFILE || 
            status == NabtoStatus.OPEN_CERT_OR_PK_FAILED) {
            status = createProfile(email, password);
            if (status == NabtoStatus.OK) {
                status = openSession(email, password);
            }
        }
        return status;
    }

    public UrlFetchResult fetchUrl(String url) {
        return nabtoApi.fetchUrl(url, session);
    }
    
    public UrlFetchResult nabtoSubmitPostData(String nabtoUrl, byte[] postData, String postMimeType) {
        return nabtoApi.nabtoSubmitPostData(nabtoUrl, postData, postMimeType, session);
        
    }
    
    public NabtoStatus resetPassword(String email) {
        if (!getNetworkAccess()) {
            return NabtoStatus.NO_NETWORK;
        }
        NabtoStatus status = nabtoApi.nabtoResetAccountPassword(email);
        if (status == NabtoStatus.API_NOT_INITIALIZED) {
            initializeApi();
            status = nabtoApi.nabtoResetAccountPassword(email);
        }
        return status;
    } 

    public NabtoStatus nabtoSignup(String email, String password) {
        if (!getNetworkAccess()) {
            return NabtoStatus.NO_NETWORK;
        }
        NabtoStatus status = nabtoApi.nabtoSignup(email, password);
        if (status == NabtoStatus.API_NOT_INITIALIZED) {
            initializeApi();
            status = nabtoApi.nabtoSignup(email, password);
        }
        return status;
    }

    private NabtoStatus openSession(String email, String password) {
    	session = nabtoApi.openSession(email, password);
        if (session.getStatus() == NabtoStatus.API_NOT_INITIALIZED) {
            initializeApi();
            session = nabtoApi.openSession(email, password);
        }
        return session.getStatus();
    }

    private NabtoStatus createProfile(String email, String password) {
        if (!getNetworkAccess()) {
            return NabtoStatus.NO_NETWORK;
        }
        NabtoStatus status = nabtoApi.createProfile(email, password);
        if (status == NabtoStatus.API_NOT_INITIALIZED) {
            initializeApi();
            status = nabtoApi.createProfile(email, password);
        }
        return status;
    }

    private void initializeApi() {
    	NabtoStatus status;
    	
    	status = nabtoApi.setStaticResourceDir();
    	Log.d(this.getClass().getSimpleName(), "API Static Resource Dir set: " + status);
    	
    	status = nabtoApi.startup();
    	Log.d(this.getClass().getSimpleName(), "API Initialization: " + status);
    }

    private boolean getNetworkAccess() {
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NabtoStatus status = probeNetwork();
        if (status == NabtoStatus.OK) {
            return true;
        } else {
            if (cm != null) {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnectedOrConnecting()) {
                    for (int i = 0; i < 10; i++) {
                        if (ni.isConnected())
                            return true;
                        probeNetwork();
                    }
                }
            }
        }
        return false;
    }

    private NabtoStatus probeNetwork() {
        NabtoStatus status = nabtoApi.nabtoProbeNetwork(2000, null);
        if (status == NabtoStatus.API_NOT_INITIALIZED) {
            initializeApi();
            status = nabtoApi.nabtoProbeNetwork(2000, null);
        }
        return status;
    }
    
    public Collection<String> getProtocolPrefixes() {
        return nabtoApi.getProtocolPrefixes();
    }
    
    public String getSessionToken() {
        return nabtoApi.getSessionToken(session);
    }

    public void pause() {
        nabtoApi.pause(session);
    }
    
    public boolean resume() {
    	Session s = nabtoApi.resume();
    	if (s != null) {
    		session = s;
    		return true;
    	}
        return false; 
    }
}