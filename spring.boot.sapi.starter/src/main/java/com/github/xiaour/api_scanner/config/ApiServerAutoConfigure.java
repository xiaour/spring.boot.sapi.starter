package com.github.xiaour.api_scanner.config;

import com.github.xiaour.api_scanner.logging.Log;
import com.github.xiaour.api_scanner.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
@ConditionalOnClass(SapiFactoryAutoConfigure.class)
@ConfigurationProperties("spring.sapi.server")
public class ApiServerAutoConfigure implements ApplicationListener<WebServerInitializedEvent> {

    private final static Log LOG = LogFactory.getLog(ApiServerAutoConfigure.class);


    public static Set<String> apiRouter=new HashSet<String>();


    private String url;
    private String username;
    private String password;

    private static String contextPath;
    private static int serverPort;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {

        this.serverPort=webServerInitializedEvent.getWebServer().getPort();

        this.contextPath=webServerInitializedEvent.getApplicationContext().getApplicationName();

        apiRouter.add(contextPath+"/sapidata/apiList");

        apiRouter.add(contextPath+"/sapidata/group");

        LOG.info("SAPI page url:http://127.0.0.1:"+getPort()+getContextPath()+"/sapi");
    }

    public static int getPort() {
        return serverPort;
    }

    public static String getContextPath() {
        return contextPath;
    }



    @Bean
    @ConditionalOnMissingBean
    public String initBean() {

        return "";
    }
}
