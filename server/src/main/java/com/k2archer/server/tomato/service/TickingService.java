package com.k2archer.server.tomato.service;

import com.k2archer.server.tomato.bean.dao.Ticking;

public interface TickingService {
    long generateTickingId();

    long addTicking(Ticking ticking);

    Ticking addTicking(long userId, Ticking ticking);

    int updateTicking(Ticking ticking);

    Ticking getTickingOnClock(long userId);

    Ticking getTicking(long userId, long tickingId);
}
