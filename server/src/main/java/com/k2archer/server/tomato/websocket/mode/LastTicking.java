package com.k2archer.server.tomato.websocket.mode;

public class LastTicking {
    private long endTime;
    private long tickingId;

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setTickingId(long tickingId) {
        this.tickingId = tickingId;
    }

    public long getTickingId() {
        return tickingId;
    }
}
