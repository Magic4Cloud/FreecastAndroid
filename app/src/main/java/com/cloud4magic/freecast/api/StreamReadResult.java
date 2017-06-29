package com.cloud4magic.freecast.api;

public class StreamReadResult {
    private byte[] data;
    private NabtoStatus nabtoStatus;

    public StreamReadResult(byte[] data, int nabtoStatus) {
        this.data = data;
        this.nabtoStatus = NabtoStatus.fromInteger(nabtoStatus);
    }

    public byte[] getData() {
        return data;
    }

    public NabtoStatus getStatus() {
        return nabtoStatus;
    }
}
