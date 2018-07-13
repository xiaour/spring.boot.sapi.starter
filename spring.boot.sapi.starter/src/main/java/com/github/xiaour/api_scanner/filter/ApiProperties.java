package com.github.xiaour.api_scanner.filter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;



@Component
@ConfigurationProperties(prefix="spring.spai")
public class ApiProperties{

    private String [] pack;//包路径

    public String[] getPack() {
        return pack;
    }

    public void setPack(String[] pack) {
        this.pack = pack;
    }

}
