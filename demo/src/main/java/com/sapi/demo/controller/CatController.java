package com.sapi.demo.controller;

import com.github.xiaour.api_scanner.annotation.SapiGroup;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/8/30 16:53
 */
@SapiGroup(title = "小猫管理")
@RestController
@RequestMapping("/cat")
public class CatController {

    @GetMapping("/get/{mouse}")
    public String get(@PathVariable String mouse,String my,int age){
        System.out.println(mouse);
        return "{\"hellokitty\":\"smile\"}";
    }

    @PostMapping("/hellokitty/")
    public String hellokitty(String user){
        return "{\"hellokitty\":\"smile\"}";
    }

    @DeleteMapping("/delete")
    public String delete(){
        return "{\"hellokitty\":\"smile\"}";
    }

    @PutMapping("/put")
    public String put(){
        return "{\"hellokitty\":\"smile\"}";
    }

    @PatchMapping("/PATCH")
    public String patch(String name){
        return "{\"hellokitty\":\""+name+"\"}";
    }

    @RequestMapping("/request")
    public String request(){
        return "{\"hellokitty\":\"smile\"}";
    }

}
