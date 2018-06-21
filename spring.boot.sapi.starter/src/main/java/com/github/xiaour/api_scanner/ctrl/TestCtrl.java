package com.github.xiaour.api_scanner.ctrl;

import com.github.xiaour.api_scanner.dto.UserInfo;
import com.github.xiaour.api_scanner.util.JsonUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/5/30 13:59
 */
@RestController
@RequestMapping({"/test"})
public class TestCtrl {


    @RequestMapping(value="/getName",method =RequestMethod.POST)
    public String getName(String name, Integer age, Integer sex, String subject, BigDecimal cost){
        System.out.println(cost);
        Map<String,Object> data= new HashMap<>();
        data.put("name",name);
        data.put("age",age);
        data.put("sex",sex);
        data.put("subject",subject);
        return JsonUtil.mapJsonUtil(data);
    }

    @RequestMapping(value="/getSchool",method ={RequestMethod.POST,RequestMethod.GET})
    public String getSchool(String name){
        return " {\"code\":0,\"data\":“”,\"message\":\"getSchool调用成功\"}";
    }

    @RequestMapping(value="/getUser",method ={RequestMethod.POST,RequestMethod.GET})
    public String getUser(UserInfo user){
        return " {\"code\":0,\"data\":“”,\"message\":\"getUser调用成功\"}";
    }


}
