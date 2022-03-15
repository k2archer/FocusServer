package com.k2archer.server.tomato.dao;

import com.k2archer.server.tomato.bean.dao.Ticking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import org.apache.ibatis.annotations.*;

import java.util.Date;

import static org.apache.ibatis.type.JdbcType.TIMESTAMP;

@Component
@Mapper
public interface TickingMapper {

    @Select("CALL GetUniqueID()")
    long generateTickingId();

    @Update("UPDATE t_ticking SET ticking_name=#{tickingName},  ticking_type=#{tickingType},  start_time=#{startTime},  end_time=#{endTime}," +
            "  ticking=#{ticking}, ticking_state=#{tickingState},  " +
            "task_intention=#{taskIntention},  task_effect=#{taskEffect},  revision=#{revision},  created_by=#{createdBy}," +
            "  created_time=#{createdTime},  updated_by= #{updatedBy},  updated_time= #{updatedTime} " +
            " WHERE tickingID=#{tickingid} and user_id=#{userId}  ")
    int updateTicking(Ticking ticking);

    @Select("SELECT * FROM t_ticking WHERE tickingId = #{tickingId}")
    @Results(id = "tickingMap", value = {
            @Result(id = true, column = "tickingid", property = "tickingid"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "ticking_name", property = "tickingName"),
            @Result(column = "ticking_type", property = "tickingType"),
            @Result(column = "start_time", property = "startTime"),
            @Result(column = "end_time", property = "endTime"),
            @Result(column = "ticking", property = "ticking"),
            @Result(column = "ticking_state", property = "tickingState"),
            @Result(column = "task_intention", property = "taskIntention"),
            @Result(column = "task_effect", property = "taskEffect"),
            @Result(column = "revision", property = "revision"),
            @Result(column = "end_time", property = "taskEffect"),
            @Result(column = "created_by", property = "createdBy"),
            @Result(column = "created_time", property = "createdTime"),
            @Result(column = "updated_by", property = "updatedBy"),
            @Result(column = "updated_time", property = "updatedTime"),
    })
    Ticking getTicking(long tickingId);

    @Insert("INSERT t_ticking(`tickingID`,  `user_id`,  `ticking_name`,  `ticking_type`,  `start_time`,  `end_time`,  `ticking`,  " +
            "`ticking_state`,  `task_intention`,  `task_effect`,  `revision`,  `created_by`,  `created_time`,  `updated_by`,  `updated_time`) " +
            " VALUES(#{tickingid}, #{userId}, #{tickingName},  #{tickingType}, #{startTime}, #{endTime}, #{ticking}," +
            " #{tickingState}, #{taskIntention}, #{taskEffect}, #{revision}, #{createdBy}, #{createdTime}, #{updatedBy}, #{updatedTime})")
    @SelectKey(statement = "select tickingid from t_ticking where tickingid= #{tickingid} ", keyProperty = "tickingid", before = false, resultType =long.class )
    long addTicking(Ticking ticking);

    @Select("SELECT * FROM t_ticking WHERE user_id=#{user_id} and (NOW() - end_time)  <= ticking and ticking_state=1 LIMIT 1")
    @Results( value = {
            @Result(id = true, column = "tickingid", property = "tickingid"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "ticking_name", property = "tickingName"),
            @Result(column = "ticking_type", property = "tickingType"),
            @Result(column = "start_time", property = "startTime"),
            @Result(column = "end_time", property = "endTime"),
            @Result(column = "ticking", property = "ticking"),
            @Result(column = "ticking_state", property = "tickingState"),
            @Result(column = "task_intention", property = "taskIntention"),
            @Result(column = "task_effect", property = "taskEffect"),
            @Result(column = "revision", property = "revision"),
            @Result(column = "end_time", property = "taskEffect"),
            @Result(column = "created_by", property = "createdBy"),
            @Result(column = "created_time", property = "createdTime"),
            @Result(column = "updated_by", property = "updatedBy"),
            @Result(column = "updated_time", property = "updatedTime"),
    })
    Ticking getTickingOnClock(@Param("user_id")long userId);
}
