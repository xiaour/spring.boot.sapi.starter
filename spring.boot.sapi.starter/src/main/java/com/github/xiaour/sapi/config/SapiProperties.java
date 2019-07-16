package com.github.xiaour.sapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: zhangtao@suiyueyule.com
 * @Date: 2019-07-16 10:35
 * @version: v1.0
 * @Description:
 */
@ConfigurationProperties(prefix = "spring.sapi")
public class SapiProperties {

    private String enable;//enable SAPI

    private String controllers;

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getControllers() {
        return controllers;
    }

    public void setControllers(String controllers) {
        this.controllers = controllers;
    }
}
