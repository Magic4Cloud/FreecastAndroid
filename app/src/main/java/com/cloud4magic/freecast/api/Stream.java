package com.cloud4magic.freecast.api;

public class Stream {
    private Object stream;
    private NabtoStatus status;

    public Stream(Object stream,  int status) {
        this.stream = stream;
        this.status = NabtoStatus.fromInteger(status);
    }

    public Object getStream() {
        return stream;
    }

    public NabtoStatus getStatus() {
        return status;
    }
}
