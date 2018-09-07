package com.sapi.demo.controller;

import com.github.xiaour.api_scanner.annotation.SapiGroup;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/8/27 17:05
 */
@SapiGroup(title = "Vip用户信息")
@RestController
@RequestMapping(value="/userInfo")
public class UserInfoController {

    /**
     * 获取用户信息
     * @param userName
     * @param id
     * @return
     */
    @RequestMapping(value = "/getUserInfo",method = RequestMethod.GET)
    public String getUserInfo(String userName,Integer id){

        return "{\n" + "\"employees\": [\n" + "{ \"firstName\":\"Bill\" , \"lastName\":\"Gates\" },\n" + "{ \"firstName\":\"George\" , \"lastName\":\"Bush\" },\n" + "{ \"firstName\":\"Thomas\" , \"lastName\":\"Carter\" }\n" + "]\n" + "}";
    }

    @PostMapping(value = "/addUserInfo")
    public String addUserInfo(String userName, Integer age, String address,String title, MultipartFile avatar){

        return "{\n" + "\"employees\": [\n" + "{ \"firstName\":\"Bill\" , \"lastName\":\"Gates\" },\n" + "{ \"firstName\":\"George\" , \"lastName\":\"Bush\" },\n" + "{ \"firstName\":\"Thomas\" , \"lastName\":\"Carter\" }\n" + "]\n" + "}";
    }



}
