package com.k2archer.server.tomato.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.k2archer.common.ResponseStateCode;
import com.k2archer.server.tomato.common.utils.SpringUtil;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = StoreException.class)
    @ResponseBody
    public ExceptionResponseBody<String> jsonErrorHandler(HttpServletRequest request, StoreException e) throws Exception {
        ExceptionResponseBody<String> errorInfo = new ExceptionResponseBody<>();

        errorInfo.setMessage(e.getMessage());
        errorInfo.setCode(ResponseStateCode.BAD_REQUEST);
        errorInfo.setData(null);
        errorInfo.setUrl(request.getRequestURI());

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String message = gson.toJson(errorInfo);

        log.error(message + " data: " + e.getData());

        return errorInfo;
    }

    ///////////////////////////////// SQL 异常 /////////////////////////////////
    // region


    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public ExceptionResponseBody<String> SQLException(HttpServletRequest request, HttpServletResponse response,
                                                      SQLNonTransientException e) {
        return defaultExceptionHandleToErrorInfo(request, response, e);
    }

    /**
     *
     */
    @ExceptionHandler(SQLNonTransientException.class)
    @ResponseBody
    public ExceptionResponseBody<String> SQLNonTransientException(HttpServletRequest request, HttpServletResponse response,
                                                                  SQLNonTransientException e) {
        return defaultExceptionHandleToErrorInfo(request, response, e);
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    @ResponseBody
    public ExceptionResponseBody<String> SQLSyntaxErrorException(HttpServletRequest request, HttpServletResponse response,
                                                                 SQLSyntaxErrorException e) {
        return defaultExceptionHandleToErrorInfo(request, response, e);
    }
    // endregion

    ///////////////////////////////// Web 异常 /////////////////////////////////

    /**
     *
     */
    // region
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ExceptionResponseBody<String> methodNotSupportHandle(HttpServletRequest request, HttpServletResponse response,
                                                                HttpRequestMethodNotSupportedException e) {
        return defaultExceptionHandleToErrorInfo(request, response, e);
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    @ResponseBody
    public ExceptionResponseBody<String> notFoundException(HttpServletRequest request, HttpServletResponse response,
                                                           NoHandlerFoundException e) {
        return defaultExceptionHandleToErrorInfo(request, response, e);
    }

    // 这里处理MissingServletRequestParameterException的异常
    @ExceptionHandler({MissingServletRequestParameterException.class})
    // 返回JSON数据
    @ResponseBody
    // 特别注意第二个参数为要处理的异常
    public ExceptionResponseBody<String> requestExceptionHandler(HttpServletRequest request, HttpServletResponse response, MissingServletRequestParameterException e) {
        Exception ex = SpringUtil.isDev() ? e : new Exception("参数异常");
        return defaultExceptionHandleToErrorInfo(request, response, ex);
    }
    // endregion

    ///////////////////////////////// 其他未处理的异常 /////////////////////////////////

    /**
     *
     */
    // region
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ExceptionResponseBody<String> exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        return defaultExceptionHandleToErrorInfo(request, response, e);
    }
    // endregion

    /**
     * 格式化异常输出
     */
    private ExceptionResponseBody<String> defaultExceptionHandleToErrorInfo(HttpServletRequest request,
                                                                            HttpServletResponse response, Exception e) {
        ExceptionResponseBody<String> errorInfo = new ExceptionResponseBody<>();

        errorInfo.setCode(ResponseStateCode.BAD_REQUEST);
        if (SpringUtil.isDev()) {
            errorInfo.setMessage(e.toString() + " at " + e.getStackTrace()[0].getClassName() + e.getStackTrace()[0].getMethodName() + "("
                    + e.getStackTrace()[0].getFileName() + ":" + e.getStackTrace()[0].getLineNumber() + ")");
        } else {
            errorInfo.setMessage(e.getMessage());
        }

        errorInfo.setUrl(request.getRequestURI());

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String message = gson.toJson(errorInfo);

        log.error(message);
        e.printStackTrace();

        return errorInfo;
    }

    private String defaultExceptionHandleToJson(HttpServletRequest request, HttpServletResponse response, Exception e) {
        ExceptionResponseBody<String> errorInfo = defaultExceptionHandleToErrorInfo(request, response, e);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String message = gson.toJson(errorInfo);

        return message;
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionResponseBody<String> handleValidationExceptions(
            HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        StringBuilder messageBuilder = new StringBuilder();
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        for (ObjectError error : allErrors) {
//            String code = error.getCode();
//            String e = code.equals("NotNull") || code.equals("NotBlank") ? "不能为空" : "错误";
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            break;
        }
        for (Map.Entry<String, String> stringStringEntry : errors.entrySet()) {
            messageBuilder.append(stringStringEntry.getValue());
        }
        messageBuilder.append("错误");
        ExceptionResponseBody<String> errorInfo = new ExceptionResponseBody<>();

        errorInfo.setCode(ResponseStateCode.FAILURE);
//        errorInfo.setUrl(request.getRequestURI());
        errorInfo.setMessage(messageBuilder.toString());

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String message = gson.toJson(errorInfo);

        log.info("handleValidationExceptions: " + message);
//        ex.getLocalizedMessage();
//        ex.printStackTrace();

        return errorInfo;
    }

}