package com.github.xiaour.sapi.config;

import com.github.xiaour.sapi.annotation.Sapi;
import com.github.xiaour.sapi.annotation.SapiGroup;
import com.github.xiaour.sapi.dto.ApiField;
import com.github.xiaour.sapi.dto.ApiInfo;
import com.github.xiaour.sapi.logging.Log;
import com.github.xiaour.sapi.logging.LogFactory;

import com.github.xiaour.sapi.util.ClassScaner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.springframework.core.annotation.AnnotationAttributes.*;

@Configuration
@AutoConfigureBefore(ApiServerAutoConfigure.class)
@EnableConfigurationProperties({ApiServerAutoConfigure.class})
@ConfigurationProperties("spring.sapi")
public class SapiFactoryAutoConfigure implements ImportBeanDefinitionRegistrar{

    private final static Log LOG = LogFactory.getLog(SapiFactoryAutoConfigure.class);


    private String pack;//API类路径,分割

    private String enable;//enable SAPI

    public static List<ApiInfo> simpleApiList;

    private static final String allRequestType="POST,GET,PUT,DELETE,PATCH";



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

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        AnnotationAttributes attributes = fromMap(annotationMetadata.getAnnotationAttributes(Sapi.class.getCanonicalName()));

        String[] values;

        boolean enabled;

        //优先从配置文件取值，其次从注解取值
        if(getPack()!=null){

            values = getPack().split(",");

            enabled = StringUtils.hasText(getEnable())?Boolean.getBoolean(getEnable()):true;

        }else {

            values = attributes.getStringArray("controllers");

            enabled = attributes.getBoolean("enable");
        }

        //如果开启，则扫描接口
        if(enabled){

            if(values != null) {

                init(values);

            }else {

                LOG.error("Sapi annotations not config,Please Configured the Application class @Sapi(controllers={\"your.controller.path1\",\"...\"})");
            }
        }
    }


    public void init(String[] controllers){

        LOG.debug("Springboot SAPI init.");

        Set<Class> classes= new HashSet<Class>();
        try {

            for(String packageName:controllers){
                classes.addAll(ClassScaner.scan(packageName));
            }

            List<ApiInfo> list=new ArrayList<ApiInfo>();

            String groupTitle;//分组名称

            for(Class c:classes){
                RequestMapping requestMapping= (RequestMapping) c.getAnnotation(RequestMapping.class);

                SapiGroup sapiGroup= (SapiGroup)c.getAnnotation(SapiGroup.class);

                if(sapiGroup==null){
                    groupTitle=c.getSimpleName();
                }else{
                    groupTitle=sapiGroup.title();
                }

                SapiGroupManager.setSapiGroup(groupTitle);

                if(requestMapping==null){
                    list.addAll(getReflectAllMethod(c,new String[]{""},groupTitle));
                }else {
                    list.addAll(getReflectAllMethod(c, requestMapping.value(),groupTitle));
                }
            }

            simpleApiList= list;

        } catch (Exception e) {

            LOG.error("SAPI init exception:",e);
        }
    }


    /**
     * 获取所有的自定义方法
     * @param mLocalClass
     * @param routes
     * @param groupTitle
     * @return
     */
    private List<ApiInfo> getReflectAllMethod( Class <?> mLocalClass,String [] routes,String groupTitle){

        ParameterNameDiscoverer pnd = new LocalVariableTableParameterNameDiscoverer();

        List<ApiInfo> list = new ArrayList<ApiInfo>();

        try {
            do{
                Method methods[] = mLocalClass.getDeclaredMethods(); // 取得全部的方法

                for (Method method:methods) {

                    Class<?> paramsTypes[] = method.getParameterTypes();

                    Annotation[][] paramAnnotations = method.getParameterAnnotations();

                    int length = paramsTypes.length;

                    for(String route:routes){

                        String mod = Modifier.toString(method.getModifiers());

                        String metName = method.getName();

                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                        if (mod.equals("public") && !metName.equals("toString") && !metName.equals("equals")) {

                            if(requestMapping!=null){

                                for(String mappingName:requestMapping.value()) {

                                    String requestInfo ="";

                                    RequestMethod[] me = method.getAnnotation(RequestMapping.class).method();

                                    for (RequestMethod rm : me) {

                                        requestInfo = rm.name();
                                    }

                                    if(requestInfo == ""){

                                        requestInfo=allRequestType;

                                    }
                                    ApiInfo apiInfo = getApiInfo(method,route,mappingName,pnd,length,paramsTypes,requestInfo,paramAnnotations);

                                    apiInfo.setGroupTitle(groupTitle);

                                    list.add(apiInfo);
                                }
                            }else{

                                OtherMapping otherMapping=getMappingType(method);

                                if(otherMapping!=null){

                                    for(String mappingName:otherMapping.getMappingName()) {

                                        ApiInfo apiInfo = getApiInfo(method,route,mappingName,pnd,length,paramsTypes,otherMapping.getRequestType(),paramAnnotations);

                                        apiInfo.setGroupTitle(groupTitle);

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

    private static ApiInfo getApiInfo(Method method,
                                      String route,
                                      String mappingName,
                                      ParameterNameDiscoverer pnd,
                                      int length,
                                      Class<?> paramsTypes[],
                                      String requestType,
                                      Annotation[][] paramAnnotations){
        ApiInfo apiInfo = new ApiInfo();

        if(requestType!=null) {

            apiInfo.setRequestType(requestType);
        }

        apiInfo.setUrl(route + "/" + mappingName);

        apiInfo.setUrl(apiInfo.getUrl().replaceAll("//","/"));

        String[] paramNames = pnd.getParameterNames(method);

        List<ApiField> apiFields = getDefaultType(length, paramsTypes, paramNames,paramAnnotations);

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

    /**
     * 是否是原生的JAVA类
     * @param clz
     * @return
     */
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
    private static List<ApiField> getDefaultType(int length,Class<?> paramsTypes[],String[] paramNames,Annotation[][] paramAnnotations){

        List<ApiField> apiFields=new ArrayList<ApiField>(length);

        for(int i=0;i<length;i++){

            if(!paramsTypes[i].getName().contains("javax.servlet.")) {
                //接口参数
                if (isJavaClass(paramsTypes[i])) {
                    ApiField apiField = new ApiField();

                    String type = getTypeName(paramsTypes[i].toString());
                    apiField.setName(paramNames[i]);
                    apiField.setType(type);

                    Annotation[] annotations=paramAnnotations[i];
                    for(Annotation annotation:annotations){
                        if(annotation.annotationType().getSimpleName().equals("PathVariable")){
                            apiField.setIsPath(true);
                            break;
                        }
                        if(annotation.annotationType().getSimpleName().equals("RequestBody")){
                            apiField.setRequestBody(true);
                            break;
                        }
                    }

                    apiFields.add(apiField);

                } else {
                    //自定义实体类作为参数
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

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }
}