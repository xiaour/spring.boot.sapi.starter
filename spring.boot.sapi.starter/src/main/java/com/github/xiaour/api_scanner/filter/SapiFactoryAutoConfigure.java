package com.github.xiaour.api_scanner.filter;

import com.github.xiaour.api_scanner.dto.ApiField;
import com.github.xiaour.api_scanner.dto.ApiInfo;
import com.github.xiaour.api_scanner.logging.Log;
import com.github.xiaour.api_scanner.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.bind.annotation.*;

import java.io.File;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.springframework.core.annotation.AnnotationAttributes.*;

@EnableConfigurationProperties({ApiProperties.class})
public class SapiFactoryAutoConfigure implements ImportBeanDefinitionRegistrar {

    private final static Log LOG = LogFactory.getLog(SapiFactoryAutoConfigure.class);

    @Autowired
    public ApiProperties properties;

    public static boolean enable;

    public static List<ApiInfo> simpleApiList;

    private static String allRequestType="POST,GET,PUT,DELETE,PATCH";

    private static String annotationName="com.github.xiaour.api_scanner.annotation.Sapi";

    static class OtherMapping{
        private String [] mappingName;

        private String requestType;

        public String[] getMappingName() {
            return mappingName;
        }

        public void setMappingName(String[] mappingName) {
            this.mappingName = mappingName;
        }

        public String getRequestType() {
            return requestType;
        }

        public void setRequestType(String requestType) {
            this.requestType = requestType;
        }
    }


    @Bean
    public String init(){


        LOG.debug("The springboot sapi init.");

        Set<Class> classes= new HashSet<Class>();
        try {

            if(properties.getPack()==null){
                LOG.error("Sapi annotations not config,Please Configured the Application class @Spai(controllers={\"your.controller.path1\",\"...\"})");
            }

            for(String packageName:properties.getPack()){
                    classes.addAll(getClassName(packageName));
            }

            List<ApiInfo> list=new ArrayList<ApiInfo>();

            for(Class c:classes){
                RequestMapping requestMapping= (RequestMapping) c.getAnnotation(RequestMapping.class);

                if(requestMapping==null){
                    list.addAll(getReflectAllMethod(c,new String[]{""}));
                }else {
                    list.addAll(getReflectAllMethod(c, requestMapping.value()));
                }
            }

            simpleApiList= list;

            LOG.info("Springboot sapi : open link view the API page on http://127.0.0.1:{port}/{context-path}/sapi");

        } catch (Exception e) {
           LOG.error("Sapi init exception:",e);
        }
        return "SUCCESS";
    }

    private static Set<Class> getClassName(String filePath) throws ClassNotFoundException {
        String flag=File.separator;
        Set<Class> classes= new HashSet<Class>();
        filePath = ClassLoader.getSystemResource("").getPath() + filePath.replace(".", "/");
        File file = new File(filePath);

        File[] childFiles = file.listFiles();
        if(childFiles==null){
            throw new ClassNotFoundException(filePath+" is not found,Please check the aplication's annotation in @Sapi(controllers) ");
        }
        for (File childFile : childFiles) {
            String childFilePath = childFile.getPath();
            childFilePath = childFilePath.substring(childFilePath.indexOf(flag+"classes") + 9,childFilePath.length());
            childFilePath=childFilePath.replaceAll(".class","");
            childFilePath=childFilePath.replaceAll(flag,".");
            classes.add(Class.forName(childFilePath));
        }

        return classes;
    }




    private List<ApiInfo> getReflectAllMethod( Class <?> mLocalClass,String [] routes){
        ParameterNameDiscoverer pnd = new LocalVariableTableParameterNameDiscoverer();
        List<ApiInfo> list= new ArrayList<ApiInfo>();

        try {
            do{
                Method methods[] = mLocalClass.getDeclaredMethods(); // 取得全部的方法

                for (Method method:methods) {

                    Class<?> paramsTypes[] = method.getParameterTypes();

                    int length = paramsTypes.length;


                    for(String route:routes){
                        String mod = Modifier.toString(method.getModifiers());

                        String metName = method.getName();

                        RequestMapping requestMapping=method.getAnnotation(RequestMapping.class);

                        if (mod.equals("public") && !metName.equals("toString") && !metName.equals("equals")) {

                            if(requestMapping!=null){

                                for(String mappingName:requestMapping.value()) {

                                        String requestInfo="";

                                        RequestMethod[] me = method.getAnnotation(RequestMapping.class).method();

                                        for (RequestMethod rm : me) {
                                            requestInfo=rm.name();
                                        }
                                        if(requestInfo==""){
                                            requestInfo=allRequestType;
                                        }
                                        ApiInfo apiInfo =getApiInfo(method,route,mappingName,pnd,length,paramsTypes,requestInfo);
                                        list.add(apiInfo);
                                    }
                            }else{

                                OtherMapping otherMapping=getMappingType(method);

                                if(otherMapping!=null){

                                    for(String mappingName:otherMapping.getMappingName()) {

                                        ApiInfo apiInfo=getApiInfo(method,route,mappingName,pnd,length,paramsTypes,otherMapping.getRequestType());

                                        list.add(apiInfo);
                                    }
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

    private static ApiInfo getApiInfo(Method method,String route,String mappingName,ParameterNameDiscoverer pnd,int length,Class<?> paramsTypes[],String requestType){
        ApiInfo apiInfo = new ApiInfo();

        if(requestType!=null) {
            apiInfo.setRequestType(requestType);
        }

        apiInfo.setUrl(route + "/" + mappingName);

        apiInfo.setUrl(apiInfo.getUrl().replaceAll("//","/"));

        String[] paramNames = pnd.getParameterNames(method);

        List<ApiField> apiFields = getDefaultType(length, paramsTypes, paramNames);

        apiInfo.setFieldList(apiFields);

        apiInfo.setId();

        return apiInfo;
    }

    /**
     * 获取请求方式
     * @return
     */
    private static OtherMapping getMappingType(Method method){
        OtherMapping otherMapping=new OtherMapping();
        //因为方法上可能有多个注解，所以只能通过需要的注解进行判断
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        PatchMapping  patchMapping = method.getAnnotation(PatchMapping.class);
        PutMapping  putMapping = method.getAnnotation(PutMapping.class);

        if(postMapping!=null){
            otherMapping.setMappingName(postMapping.value());
            otherMapping.setRequestType(RequestMethod.POST.name());
            return otherMapping;
        }
        if(getMapping!=null){
            otherMapping.setMappingName(getMapping.value());
            otherMapping.setRequestType(RequestMethod.GET.name());
            return otherMapping;
        }
        if(deleteMapping!=null){
            otherMapping.setMappingName(deleteMapping.value());
            otherMapping.setRequestType(RequestMethod.DELETE.name());
            return otherMapping;
        }
        if(patchMapping!=null){
            otherMapping.setMappingName(patchMapping.value());
            otherMapping.setRequestType(RequestMethod.PATCH.name());
            return otherMapping;
        }
        if(putMapping!=null){
            otherMapping.setMappingName(putMapping.value());
            otherMapping.setRequestType(RequestMethod.PUT.name());
            return otherMapping;
        }
        return null;
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
        List<ApiField> apiFields=new ArrayList<ApiField>(length);

        for(int i=0;i<length;i++){
            if(!paramsTypes[i].getName().contains("javax.servlet.")) {
                if (isJavaClass(paramsTypes[i])) {
                    String type = getTypeName(paramsTypes[i].toString());
                    ApiField apiField = new ApiField();
                    apiField.setName(paramNames[i]);
                    apiField.setType(type);
                    apiFields.add(apiField);
                } else {
                    getCustomType(paramsTypes[i], apiFields);
                }
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

        enable=attributes.getBoolean("enable");
        if(enable){
            if(values!=null) {
                properties=new ApiProperties();
                properties.setPack(values);
                init();
            }else {
                LOG.error("Sapi annotations not config,Please Configured the Application class @Spai(controllers={\"your.controller.path1\",\"...\"})");
            }
        }
    }
}