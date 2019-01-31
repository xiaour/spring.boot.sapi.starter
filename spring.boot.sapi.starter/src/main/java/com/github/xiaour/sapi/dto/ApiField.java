package com.github.xiaour.sapi.dto;

public class ApiField {

    private String type;//字段类型

    private String name;//字段名

    private String notNull;//是否为空

    private String remark;//备注

    private boolean isPath=false;//是否路径参数

    private boolean isRequestBody=false;//是否Body参数

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotNull() {
        return notNull;
    }

    public void setNotNull(String notNull) {
        this.notNull = notNull;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean getIsPath() {
        return isPath;
    }

    public boolean getIsRequestBody() {
        return isRequestBody;
    }

    public void setRequestBody(boolean requestBody) {
        isRequestBody = requestBody;
    }

    public void setIsPath(boolean isPath) {
        this.isPath = isPath;
    }
}
