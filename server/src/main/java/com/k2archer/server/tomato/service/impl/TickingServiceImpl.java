package com.k2archer.server.tomato.service.impl;

import com.k2archer.server.tomato.bean.dao.Ticking;
import com.k2archer.server.tomato.dao.TickingMapper;
import com.k2archer.server.tomato.service.TickingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class TickingServiceImpl implements TickingService {

    @Autowired
    TickingMapper tickingMapper;

    @Override
    public long generateTickingId() {
        return tickingMapper.generateTickingId();
    }

    @Override
    public long addTicking(Ticking ticking) {
//        ticking.setTickingid(generateTickingId()); // 生成 ID
        ticking.setCreatedTime(new Date(System.currentTimeMillis()));        // 创建时间
        return tickingMapper.addTicking(ticking);
    }

    @Override
    public Ticking addTicking(long userId, Ticking ticking) {
        ticking.setUserId(userId);
        return addTicking(ticking) == 1 ? ticking : null;
    }

    @Override
    public int updateTicking(Ticking ticking) {
        return tickingMapper.updateTicking(ticking);
    }

    @Override
    public Ticking getTickingOnClock(long userId) {
        return tickingMapper.getTickingOnClock(userId);
    }

    @Override
    public Ticking getTicking(long userId, long tickingId) {
        return tickingMapper.getTicking(tickingId);
    }


}
