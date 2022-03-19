package com.k2archer.server.tomato.service.impl;

import com.google.gson.Gson;
import com.k2archer.server.tomato.bean.dao.Ticking;
import com.k2archer.common.bo.TickingInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TickingServiceImplTest {

    @Autowired
    TickingServiceImpl tickingService;

    @Test
    void addTicking() {
        long user_id = 1L;

        Ticking ticking = new Ticking();
        ticking.setTickingid(tickingService.generateTickingId());
        ticking.setTicking(25L);
        ticking.setTickingState(TickingInfo.TickingState.TICKING.getCode());
        long startTime = System.currentTimeMillis();
        ticking.setStartTime(startTime);
        ticking.setEndTime(startTime + ticking.getTicking() * 1000);
        ticking.setUserId(user_id);
        tickingService.addTicking(ticking);

        Ticking t = tickingService.getTickingOnClock(user_id);
        assertNotNull(t);
    }

    @Test
    void getTickingOnClock() {
        tickingService.getTickingOnClock(1);
    }

    @Test
    void updateTicking() {

        Ticking ticking = new Ticking();
        int result = tickingService.updateTicking(ticking);
        assertEquals(0, result);
//
        addTicking();
        Ticking t = tickingService.getTickingOnClock(1);
        ticking.setTickingid(t.getTickingid());
        ticking.setUserId(t.getUserId());
        result = tickingService.updateTicking(ticking);
        assertEquals(1, result);
//
        Ticking result_ticking = tickingService.getTicking(ticking.getUserId(), ticking.getTickingid());
        assertNotNull(result_ticking);

        String str_info = "{\"action\":\"finishTicking\"," +
                "\"endTime\":1646315440204,\"name\":\"czar\",\"startTime\":1646315435204," +
                "\"state\":1,\"ticking\":5,\"type\":1}";
        TickingInfo ti = new Gson().fromJson(str_info, TickingInfo.class);
        ticking = Ticking.BuildFromTickingInfo(ti);
        ticking.setUserId(t.getUserId());
        ticking.setTickingid(t.getTickingid());

        ticking.setUpdatedTime(new Date(System.currentTimeMillis()));

        result = tickingService.updateTicking(ticking);
        assertEquals(1, result);

        result_ticking = tickingService.getTicking(ticking.getUserId(), ticking.getTickingid());
        assertNotNull(result_ticking);
        assertEquals(ticking.getUserId(), result_ticking.getUserId());
        assertEquals(ticking.getTickingName(), result_ticking.getTickingName());
        assertEquals(ticking.getTickingType(), result_ticking.getTickingType());
        assertEquals(ticking.getStartTime().getTime(), result_ticking.getStartTime().getTime());
        assertEquals(ticking.getEndTime().getTime(), result_ticking.getEndTime().getTime());
        assertEquals(ticking.getTicking(), result_ticking.getTicking());
        assertEquals(ticking.getTickingState(), result_ticking.getTickingState());
        assertEquals(ticking.getTaskIntention(), result_ticking.getTaskIntention());
        assertEquals(ticking.getRevision(), result_ticking.getRevision());
        assertEquals(ticking.getCreatedBy(), result_ticking.getCreatedBy());
        assertEquals(ticking.getCreatedTime(), result_ticking.getCreatedTime());
        assertEquals(ticking.getUpdatedBy(), result_ticking.getUpdatedBy());
        assertEquals(ticking.getUpdatedTime(), result_ticking.getUpdatedTime());


        System.out.println(((Date) result_ticking.getStartTime()));
    }
}