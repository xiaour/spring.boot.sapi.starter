package com.github.xiaour.api_scanner.dto;

import java.util.Map;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/6/9 18:54
 */
public class UserInfo {

    private String name;

    private Integer age;

    private Map<String,Object> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
