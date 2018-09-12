package com.github.xiaour.api_scanner.config;

import java.util.HashSet;
import java.util.Set;


public class SapiGroupManager {

    private static Set<String> sapiGroup =new HashSet<String>();


    public static Set<String> getSapiGroup() {
        return sapiGroup;
    }

    public static void setSapiGroup(String groupTitle){
        sapiGroup.add(groupTitle);
    }
}
