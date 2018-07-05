package com.github.xiaour.api_scanner.filter;

import com.github.xiaour.api_scanner.dto.ApiField;
import com.github.xiaour.api_scanner.dto.ApiInfo;
import com.github.xiaour.api_scanner.logging.Log;
import com.github.xiaour.api_scanner.logging.LogFactory;
import com.github.xiaour.api_scanner.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.annotation.PostConstruct;
import java.io.File;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.springframework.core.annotation.AnnotationAttributes.*;


/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/5/30 15:02
 */
@EnableConfigurationProperties({ApiProperties.class})
public class SapiFactoryAutoConfigure implements ImportBeanDefinitionRegistrar {

    private final static Log LOG = LogFactory.getLog(SapiFactoryAutoConfigure.class);

    @Autowired
    public ApiProperties properties;

    public static String simpleApiJson;

    private static String annotationName="com.github.xiaour.api_scanner.annotation.Sapi";

    @Bean
    public String init(){


        LOG.debug("The springboot sapi init.");

        Set<Class> classes= new HashSet<>();
        try {

            if(properties.getPack()==null){
                LOG.error("Sapi annotations not config,Please Configured the Application class @Spai(controllers={\"your.controller.path1\",\"...\"})");
            }

            for(String packageName:properties.getPack()){
                    classes.addAll(getClassName(packageName));
            }

            List<ApiInfo> list=new ArrayList<>();

            for(Class c:classes){
                RequestMapping requestMapping= (RequestMapping) c.getAnnotation(RequestMapping.class);

                if(requestMapping==null){
                    list.addAll(getReflectAllMethod(c,new String[]{""}));
                }else {
                    list.addAll(getReflectAllMethod(c, requestMapping.value()));
                }
            }

            simpleApiJson=JsonUtil.collectionJsonUtil(list);

            LOG.info("Springboot sapi : open link view the API page on http://127.0.0.1:{port}/{context-path}/sapi");

        } catch (Exception e) {
           LOG.error("Sapi init exception:",e);
        }
        return "SUCCESS";
    }

    private static Set<Class> getClassName(String filePath) throws ClassNotFoundException {
        Set<Class> classes= new HashSet<>();
        filePath = ClassLoader.getSystemResource("").getPath() + filePath.replace(".", "/");
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            String childFilePath = childFile.getPath();
            childFilePath = childFilePath.substring(childFilePath.indexOf("/classes") + 9,childFilePath.length());
            childFilePath=childFilePath.replaceAll(".class","");
            childFilePath=childFilePath.replaceAll("/",".");
            classes.add(Class.forName(childFilePath));
        }

        return classes;
    }




    private List<ApiInfo> getReflectAllMethod( Class <?> mLocalClass,String [] routes){
        ParameterNameDiscoverer pnd = new LocalVariableTableParameterNameDiscoverer();
        List<ApiInfo> list= new ArrayList<>();

        try {
            do{
                Method methods[] = mLocalClass.getDeclaredMethods(); // 取得全部的方法
                for (Method method:methods) {

                    for(String route:routes){
                        String mod = Modifier.toString(method.getModifiers());
                        RequestMapping requestMapping=method.getAnnotation(RequestMapping.class);
                        String metName = method.getName();
                        if(requestMapping!=null){
                            for(String mappingName:requestMapping.value()) {

                                ApiInfo apiInfo = new ApiInfo();

                                if (mod.equals("public") && !metName.equals("toString") && !metName.equals("equals")) {

                                    RequestMethod[] me = method.getAnnotation(RequestMapping.class).method();
                                    for (RequestMethod rm : me) {
                                        apiInfo.setRequestType(apiInfo.getRequestType() != "" ? apiInfo.getRequestType() + "," + rm : rm.name());
                                    }
                                    apiInfo.setUrl(route + "/" + mappingName);
                                    apiInfo.setUrl(apiInfo.getUrl().replaceAll("//","/"));
                                    Class<?> paramsTypes[] = method.getParameterTypes();
                                    String[] paramNames = pnd.getParameterNames(method);

                                    int length = paramsTypes.length;

                                    List<ApiField> apiFields = getDefaultType(length, paramsTypes, paramNames);

                                    apiInfo.setFieldList(apiFields);
                                    apiInfo.setId();
                                    list.add(apiInfo);
                                }
                            }

                        }
                    }
                }

                mLocalClass=mLocalClass.getSuperclass();

            }while(mLocalClass!=null);
        } catch (Exception e) {
            LOG.error("Sapi init exception:",e);
        }
        return list;
    }

    private static boolean isJavaClass(Class<?> clz) {
        if(clz.getName().contains("multipart.MultipartFile")){
            return true;
        }
        return clz != null && clz.getClassLoader() == null;
    }

    /**
     * 获取默认基本类型字段
     * @param length
     * @param paramsTypes
     * @param paramNames
     * @return
     */
    private static List<ApiField> getDefaultType(int length,Class<?> paramsTypes[],String[] paramNames){
        List<ApiField> apiFields=new ArrayList<>(length);

        for(int i=0;i<length;i++){
            if(isJavaClass(paramsTypes[i])) {
                String type = getTypeName(paramsTypes[i].toString());
                ApiField apiField = new ApiField();
                apiField.setName(paramNames[i]);
                apiField.setType(type);
                apiFields.add(apiField);
            }else{
                getCustomType(paramsTypes[i],apiFields);
            }
        }
        return apiFields;
    }

    /**
     * 获取自定义类的所有属性
     * @param clz
     * @return
     */
    private static void getCustomType(Class clz,List<ApiField> apiFields){
        try {
            Field[] fields = clz.getDeclaredFields();
            for(Field f : fields) {
                ApiField apiField = new ApiField();
                apiField.setName(f.getName());
                apiField.setType(getTypeName(f.getType().getName()));

                apiFields.add(apiField);
            }
        } catch(Exception e) {
            LOG.error("Sapi getCustomType exception:",e);
        }
    }

    /**
     * 截取类型
     * @param typeName
     * @return
     */
    private static String getTypeName(String typeName){
        return  typeName.toString().substring(typeName.toString().lastIndexOf(".") + 1,typeName.toString().length());
    }



    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes attributes = fromMap(annotationMetadata.getAnnotationAttributes(annotationName));
        String[] values = attributes.getStringArray("controllers");
        if(values!=null) {
            properties=new ApiProperties();
            properties.setPack(values);
            init();
        }else {
            LOG.error("Sapi annotations not config,Please Configured the Application class @Spai(controllers={\"your.controller.path1\",\"...\"})");
        }
    }
}