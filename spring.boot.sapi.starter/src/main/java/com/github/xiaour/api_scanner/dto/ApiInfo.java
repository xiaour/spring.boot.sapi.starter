package com.github.xiaour.api_scanner.dto;

import java.util.List;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/5/31 18:19
 */
public class ApiInfo {

    private String url;//URL

    private String requestType="";//请求类型

    private List<ApiField> fieldList;//字段

    private String success;//成功返回值

    private String error;//错误返回值

    private String other;//其他返回值


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public List<ApiField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<ApiField> fieldList) {
        this.fieldList = fieldList;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
