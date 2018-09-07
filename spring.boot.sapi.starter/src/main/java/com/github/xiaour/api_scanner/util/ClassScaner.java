package com.github.xiaour.api_scanner.util;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ClassScaner implements ResourceLoaderAware {

    //保存过滤规则要排除的注解
    private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();
    private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

    public static Set<Class> scan(String[] basePackages,
                                  Class<? extends Annotation>... annotations) {
        ClassScaner cs = new ClassScaner();

        if(annotations!=null) {
            for (Class anno : annotations) {
                cs.addIncludeFilter(new AnnotationTypeFilter(anno));
            }
        }

        Set<Class> classes = new HashSet<Class>();
        for (String s : basePackages)
            classes.addAll(cs.doScan(s));
        return classes;
    }

    public static Set<Class> scan(String basePackages, Class<? extends Annotation>... annotations) {
        return ClassScaner.scan(StringUtils.tokenizeToStringArray(basePackages, ",; \t\n"), annotations);
    }

    public final ResourceLoader getResourceLoader() {
        return this.resourcePatternResolver;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils
                .getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(
                resourceLoader);
    }

    public void addIncludeFilter(TypeFilter includeFilter) {
        this.includeFilters.add(includeFilter);
    }


    public Set<Class> doScan(String basePackage) {
        Set<Class> classes = new HashSet<Class>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils
                    .convertClassNameToResourcePath(SystemPropertyUtils
                            .resolvePlaceholders(basePackage))
                    + "/**/*.class";
            Resource[] resources = this.resourcePatternResolver
                    .getResources(packageSearchPath);

            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
                if (resource.isReadable()) {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    if ((includeFilters.size() == 0 && excludeFilters.size() == 0)
                            || matches(metadataReader)) {
                        try {
                            String simpleName=metadataReader.getClassMetadata().getClassName();
                            if(simpleName.indexOf("$")>=0){
                                simpleName=simpleName.substring(0,simpleName.indexOf("$"));
                            }
                            classes.add(Class.forName(simpleName));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException(
                    "I/O failure during classpath scanning", ex);
        }
        return classes;
    }

    protected boolean matches(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : this.includeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 传统的获取类路径
     * @param filePath
     * @return
     * @throws ClassNotFoundException
     */
    public static Set<Class> getClassName(String filePath) throws ClassNotFoundException {
        String flag= File.separator;
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

    public static void main(String[] args) {
        ClassScaner.scan("com.github.xiaour.api_scanner.config", null).forEach(clazz -> System.out.println(clazz));

        /*String s="com.github.xiaour.api_scanner.config.SapiFactoryAutoConfigure";
        System.out.println(s.substring(0,s.indexOf("$")));*/
    }
}