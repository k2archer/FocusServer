package com.k2archer.server.tomato.bean.dao;

import com.k2archer.common.bo.TickingInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Ticking implements Serializable, Cloneable {

    @Getter
    @Setter
    /* TickingID */
    private Long tickingid;


    @Getter
    @Setter
    private Long userId;

    @Getter
    @Setter
    /* 名称 */
    private String tickingName;

    @Getter
    @Setter
    /* 类型 */
    private Integer tickingType;

    @Getter
    /* 开始时间 */
    private Timestamp startTime;

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = new java.sql.Timestamp(startTime);
    }

    @Getter
    /* 结束时间 */
    private Date endTime;

    public void setEndTime(long endTime) {
        this.endTime = new Date(endTime);
    }


    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Getter
    @Setter
    /* 秒数 */
    private Long ticking;

    @Getter
    @Setter
    /* 状态 */
    private Integer tickingState;

    @Getter
    @Setter
    /* 任务目标 */
    private String taskIntention;

    @Getter
    @Setter
    /* 任务结果 */
    private String taskEffect;

    @Getter
    @Setter
    /* 乐观锁 */
    private Integer revision;

    @Getter
    @Setter
    /* 创建人 */
    private String createdBy;

    @Getter
    @Setter
    /* 创建时间 */
    private Date createdTime;

    @Getter
    @Setter
    /* 更新人 */
    private String updatedBy;

    @Getter
//    @Setter
    /* 更新时间 */
    private Date updatedTime;

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = new Date(updatedTime);
    }
    public void setUpdatedTime(Date date) {
        this.updatedTime = date;
    }


    public static Ticking BuildFromTickingInfo(TickingInfo info) {
        Ticking ticking = new Ticking();
        ticking.setTickingid(info.getTickingId());
        ticking.setTickingName(info.getName());
        ticking.setTickingType(info.getType());
        ticking.setStartTime(info.getStartTime());
        ticking.setEndTime(info.getEndTime());
        ticking.setTicking(info.getTicking());
        ticking.setTickingState(info.getState());
        ticking.setTaskEffect(info.getEffect());
//        ticking.setTaskIntention(info.getIntention());

        return ticking;
    }

}
