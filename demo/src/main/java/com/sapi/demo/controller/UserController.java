package com.sapi.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/8/27 17:05
 */
@RestController
@RequestMapping(value="/user")
public class UserController {

    @RequestMapping(value = "/getUserInfo",method = RequestMethod.GET)
    public String getUserInfo(String userName,Integer id){

        return "{\n" + "\"employees\": [\n" + "{ \"firstName\":\"Bill\" , \"lastName\":\"Gates\" },\n" + "{ \"firstName\":\"George\" , \"lastName\":\"Bush\" },\n" + "{ \"firstName\":\"Thomas\" , \"lastName\":\"Carter\" }\n" + "]\n" + "}";
    }

    @RequestMapping(value = "/addUserInfo",method = RequestMethod.POST)
    public String addUserInfo(String userName, Integer age, String address,String title, MultipartFile avatar){

        return "{\n" + "\"employees\": [\n" + "{ \"firstName\":\"Bill\" , \"lastName\":\"Gates\" },\n" + "{ \"firstName\":\"George\" , \"lastName\":\"Bush\" },\n" + "{ \"firstName\":\"Thomas\" , \"lastName\":\"Carter\" }\n" + "]\n" + "}";
    }



}