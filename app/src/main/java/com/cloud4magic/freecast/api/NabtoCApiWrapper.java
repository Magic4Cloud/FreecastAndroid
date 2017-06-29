package com.cloud4magic.freecast.api;

public class NabtoCApiWrapper {
    static {
    	//System.loadLibrary("nabto_client_api");
    	System.loadLibrary("nabto_client_api_jni");
    }

    public static native int nabtoStartup(String nabtoHomeDir);
    public static native int nabtoStartupDefaultHomeDir();
    public static native int nabtoSetStaticResourceDir(String nabtoResDir);
    public static native int nabtoShutdown();
    public static native String nabtoLookupExistingProfile();
    public static native int nabtoCreateProfile(String email, String password);
    public static native Session nabtoOpenSession(String email, String password);
    public static native int nabtoCloseSession(Object session);
    public static native UrlFetchResult nabtoFetchUrl(String url, Object session);
    public static native int nabtoSignup(String email, String password);
    public static native int nabtoResetAccountPassword(String email);
    public static native int nabtoProbeNetwork(int timeoutMillis,
            String hostname);
    public static native UrlFetchResult nabtoSubmitPostData(String nabtoUrl,
            byte[] postData, String postMimeType, Object session);
    public static native String[] nabtoGetProtocolPrefixes();
    public static native String nabtoGetSessionToken(Object session);

    // streaming
    public static native Stream nabtoStreamOpen(Object session, String nabtoHost);
    public static native int nabtoStreamClose(Object Stream);
    public static native StreamReadResult nabtoStreamRead(Object Stream);
    public static native int nabtoStreamWrite(Object stream, byte[] data);
    public static native StreamConnectionTypeResult nabtoStreamConnectionType(Object stream);
    public static native int nabtoStreamSetOption(Object stream, int optionName, byte[] option);
    
    // tunnelling
    public static native Tunnel nabtoTunnelOpenTcp(int localPort, String nabtoHost, String remoteHost, int remotePort, Object session);
    public static native int nabtoTunnelCloseTcp(Object tunnel);
    public static native TunnelInfo nabtoTunnelInfo(Object tunnel);
    public static native String nabtoVersion();
    public static native String[] nabtoGetLocalDevices();
}
