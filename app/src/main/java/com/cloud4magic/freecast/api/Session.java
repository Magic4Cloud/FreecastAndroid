package com.cloud4magic.freecast.api;

public class Session {

	private Object session;
	private NabtoStatus nabtoStatus;
	private String sessionId;
	private static int nSession = 0;
	
	public Session(Object _session, int _nabtoStatus)
	{
		session = _session;
		sessionId = "Session " + nSession++;
		nabtoStatus = NabtoStatus.fromInteger(_nabtoStatus);
	}
	
	public Object getSession()
	{
		return session;
	}
	
	public NabtoStatus getStatus()
	{
		return nabtoStatus;
	}
	
	@Override
	public String toString() {
		return sessionId;
	}
}
