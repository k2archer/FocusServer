package com.k2archer.common;

import lombok.Getter;
import lombok.Setter;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class BaseLoginInfo {

    @Getter
    @Setter
    @NotNull(message = "用户名")
    @NotBlank(message = "用户名")
    private String username;

    @Getter
    @Setter
    @NotNull(message = "密码")
    @NotBlank(message = "密码")
    private String password;

    private String device;

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDevice() {
        return device;
    }
}

