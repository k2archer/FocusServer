package com.k2archer.server.tomato.websocket.controller;

import com.google.gson.Gson;
import com.k2archer.common.ResponseStateCode;
import com.k2archer.common.web_socket.WebSocketMessage;
import com.k2archer.server.tomato.bean.dao.Ticking;
import com.k2archer.server.tomato.bean.dto.User;
import com.k2archer.server.tomato.service.TickingService;
import com.k2archer.common.web_socket.response_action.TickingAction;
import com.k2archer.common.bo.TickingInfo;
import com.k2archer.common.web_socket.WebSocketResponse;
import com.k2archer.server.tomato.websocket.mode.LastTicking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TickingController {

    @Autowired
    private TickingService tickingService;

    private Map<Long, Integer> lastType = new HashMap<>();

    private final Map<Long, LastTicking> lastTicking = new HashMap<>();

    private static final Long lock = new Long(0);

    /**
     * @api {WebSocket} /websocket/{token}
     * @apiGroup 任务
     */
    /**
     * @api {WebSocket} /websocket/{token}|startTicking 开始任务
     * @apiGroup 任务
     * @apiDescription 开始一个番茄任务，同步通知更新到在线终端
     * @apiParam {String} token 请求 Token
     * @apiParam {String=startTicking} action=startTicking 请求动作
     * @apiParam {Number={0...}} [ticking]长时(秒) 默认：60*25
     * @apiParamExample {json} Request-Example:
     * {
     * "action": "startTicking",
     * "ticking": 15
     * }
     * @apiSuccess {Number} tickingId 任务ID
     * @apiSuccess {Number} startTime 任务开始(UTC)时间
     * @apiSuccess {Number} ticking 任务时长
     * @apiSuccessExample {json} Response-Example:
     * {
     * "code": 1,
     * "msg": "",
     * "data": {
     * "tickingId": 1,
     * "startTime": 123,
     * "ticking": 15
     * }
     * }
     */
    public WebSocketResponse<TickingInfo> startTicking(User user, TickingInfo tickingInfo) {

        // todo 判断是否有 ticking 任务运行中
        /** todo check ticking_state
         *
         */

        // todo ... 启动一个任务
//        tickingInfo.setTickingId(tickingService.generateTickingId());
        tickingInfo.setAction(TickingAction.START_TICKING);
        tickingInfo.setStartTime(System.currentTimeMillis());

        // 切换 Ticking 类型： 在 休息 与 非休息 切换
        if (tickingInfo.getType() == 0) {
            Integer type = lastType.get(user.getId());
            if (type != null && type == TickingInfo.TICKING_TYPE.RESTING) {
                tickingInfo.setType(TickingInfo.TICKING_TYPE.WORKING);
            } else {
                tickingInfo.setType(TickingInfo.TICKING_TYPE.RESTING);
            }
        }
        // 根据 Ticking 类型设置 Ticking 时间
        if (tickingInfo.getTicking() == 0) {
            if (tickingInfo.getType() == TickingInfo.TICKING_TYPE.WORKING) {
//                tickingInfo.setTicking(25 * 60);
                tickingInfo.setTicking(25);
            } else if (tickingInfo.getType() == TickingInfo.TICKING_TYPE.RESTING) {
//                tickingInfo.setTicking(5 * 60);
                tickingInfo.setTicking(15);
            }
        }
        tickingInfo.setTicking(tickingInfo.getTicking());
        tickingInfo.setEndTime(System.currentTimeMillis() + tickingInfo.getTicking() * 1000);
        tickingInfo.setState(TickingInfo.TickingState.TICKING);
        tickingInfo.setName(tickingInfo.getName());
        tickingInfo.setName("FirstTask"); // todo

        // 记录当前 Ticking 类型
        lastType.put(user.getId(), tickingInfo.getType());

        synchronized (TickingController.class) {
//        synchronized (lastTicking) {
            LastTicking last = lastTicking.get(user.getId());
            if (last != null
                    && last.getEndTime() > System.currentTimeMillis()
            ) {
                TickingInfo t = new TickingInfo();
                t.setAction(TickingAction.ON_TICKING);
                t.setTickingId(last.getTickingId());
                t.setEndTime(last.getEndTime());
                WebSocketResponse<TickingInfo> response = new WebSocketResponse<TickingInfo>(
                        ResponseStateCode.FAILURE, new Gson().toJson(last) + " is Ticking " + new Date(t.getEndTime()), WebSocketMessage.MessageAction.TICKING, t);
                return response;
            }


            tickingInfo.setTickingId(tickingService.generateTickingId());
            if (lastTicking.get(user.getId()) == null) {
                lastTicking.put(user.getId(), new LastTicking());
            }
            last = lastTicking.get(user.getId());
            last.setTickingId(tickingInfo.getTickingId());
            last.setEndTime(tickingInfo.getEndTime());

            Ticking ticking = Ticking.BuildFromTickingInfo(tickingInfo);
            Ticking resultTicking = tickingService.addTicking(user.getId(), ticking);
            WebSocketResponse<TickingInfo> response = null;
            if (resultTicking != null) {
                tickingInfo.setTickingId(resultTicking.getTickingid());
                tickingInfo.setEffect(new Date(tickingInfo.getEndTime()) + "");
                return new WebSocketResponse<TickingInfo>(ResponseStateCode.SUCCESS, "", WebSocketResponse.Action.TICKING, tickingInfo);
            } else {
                return new WebSocketResponse<TickingInfo>(ResponseStateCode.FAILURE, "创建 Ticking 失败", "", null);
            }
        }

        //

        // response

    }


    /**
     * @api {WebSocket} /websocket/{token}|cancelTicking 取消任务
     * @apiGroup 任务
     * @apiDescription 取消一个番茄任务，同步通知更新到在线终端
     * @apiParam {String} token 请求 Token
     * @apiParam {String=cancelTicking} action=cancelTicking 请求动作
     * @apiParam {Number} tickingId 任务ID
     * @apiParamExample {json} Request-Example:
     * {
     * "action": "cancelTicking",
     * "tickingId": 15
     * }
     * @apiSuccessExample {json} Response-Example:
     * {
     * "code": 1,
     * "msg": "",
     * "data": {}
     * }
     */
    public WebSocketResponse<TickingInfo> cancelTicking(User user, TickingInfo tickingInfo) {

        // todo 判断是否有 ticking 任务运行中

        // todo ... 取消一个任务
        tickingInfo.setAction(TickingAction.CANCEL_TICKING);
        tickingInfo.setState(TickingInfo.TickingState.CANCEL);

        Ticking ticking = tickingService.getTicking(user.getId(), tickingInfo.getTickingId());
        if (ticking == null) {
            return new WebSocketResponse<TickingInfo>(ResponseStateCode.FAILURE,
                    "取消 Ticking 失败 " + tickingInfo.getTickingId(), "", null);
        }

        ticking.setTickingState(tickingInfo.getState());

        synchronized (TickingController.class) {
//        synchronized (lastTicking) {
            LastTicking last = lastTicking.get(user.getId());
            if (last != null && last.getEndTime() < System.currentTimeMillis()) {

                WebSocketResponse<TickingInfo> response = new WebSocketResponse<TickingInfo>(
                        ResponseStateCode.FAILURE, new Gson().toJson(last) + " is finish", "", null);
                return response;
            }

            lastTicking.remove(user.getId());
            ticking.setUpdatedBy(user.getName());
            ticking.setUpdatedTime(System.currentTimeMillis());
            int updateResult = tickingService.updateTicking(ticking);
        }

        // response
        return new WebSocketResponse(com.k2archer.common.ResponseStateCode.SUCCESS, "",
                WebSocketResponse.Action.TICKING, tickingInfo);
    }

    public WebSocketResponse finishTicking(User user, TickingInfo tickingInfo) {

        String errorMessage = null;
        if (tickingInfo == null) {
            errorMessage = "Ticking 信息不能为空";
        } else {
            if (tickingInfo.getName() == null || tickingInfo.getName().length() == 0) {
                errorMessage = "Ticking 名称不能为空";
            } else if (tickingInfo.getEffect() == null || tickingInfo.getEffect().length() == 0) {
                errorMessage = "Ticking 内容不能为空";
            }
        }
        if (errorMessage != null) {
            return new WebSocketResponse<String>(ResponseStateCode.FAILURE, errorMessage, "", "");
        }

        // todo 更新 Ticking 状态
        tickingInfo.setAction(TickingAction.UPDATE_TICKING);
        tickingInfo.setState(TickingInfo.TickingState.FINISH);

        Ticking ticking = tickingService.getTicking(user.getId(), tickingInfo.getTickingId());
        if (ticking == null) {
            return new WebSocketResponse<String>(ResponseStateCode.FAILURE, "更新 Ticking 失败", "", tickingInfo.getTickingId() + "");
        }
        ticking.setTickingName(tickingInfo.getName());
        ticking.setTickingType(tickingInfo.getType());
        ticking.setTickingState(tickingInfo.getState());
        ticking.setTaskEffect(tickingInfo.getEffect());
//        ticking.setTaskIntention(info.getIntention());
        ticking.setUpdatedBy(user.getName());
        ticking.setUpdatedTime(new Date(System.currentTimeMillis()));

        try {
            tickingService.updateTicking(ticking);
        } catch (TransientDataAccessResourceException e) {
//            e.printStackTrace();
            return new WebSocketResponse<String>(ResponseStateCode.FAILURE, "更新 Ticking 失败", "", e.getMessage());
        }

        // response
        if (tickingService.updateTicking(ticking) > 0) {
            return new WebSocketResponse<TickingInfo>(ResponseStateCode.SUCCESS, "", WebSocketResponse.Action.TICKING, tickingInfo);
        } else {
            return new WebSocketResponse<TickingInfo>(ResponseStateCode.FAILURE, "更新 Ticking 失败", "", null);
        }
    }
}
