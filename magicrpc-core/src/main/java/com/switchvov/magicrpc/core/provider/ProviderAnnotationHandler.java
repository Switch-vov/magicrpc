package com.switchvov.magicrpc.core.provider;

import com.switchvov.magicrpc.core.annotation.EnableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.StackWalker.StackFrame;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * @author switch
 * @since 2024/3/22
 */
public class ProviderAnnotationHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProviderAnnotationHandler.class);

    /**
     * @param scanClass
     * @return
     */
    public List<Class<?>> handle(Class<? extends Annotation>[] scanClass) {
        Class<?> configClass = deduceMainApplicationClass();
        String[] basePackages = new String[]{""};
        List<Class<?>> classes = new ArrayList<>();
        if (!Objects.isNull(configClass)) {
            EnableProvider configuration = configClass.getAnnotation(EnableProvider.class);
            if (!Objects.isNull(configuration)) {
                basePackages = configuration.scanBasePackages();
            }
        }
        for (String basePackage : basePackages) {
            try {
                String packageDirName = basePackage.replaceAll("\\.", "/");
                Enumeration<URL> dirs = Thread.currentThread()
                        .getContextClassLoader().getResources(packageDirName);
                while (dirs.hasMoreElements()) {
                    URL url = dirs.nextElement();
                    LOGGER.debug("find has annotations:{} class from {}", scanClass, url);
                    String protocol = url.getProtocol();
                    if ("file".equals(protocol)) {
                        String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                        // 以文件的方式扫描整个包下的文件 并添加到集合中
                        List<Class<?>> classInFile = findClassByFile(basePackage, filePath, scanClass);
                        classes.addAll(classInFile);
                    } else if ("jar".equals(protocol)) {
                        List<Class<?>> classInJar = findClassByJar(url, packageDirName, scanClass);
                        classes.addAll(classInJar);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (Class<?> clazz : classes) {
            LOGGER.debug("find has annotations:{} get class:{}", scanClass, clazz);
        }
        return classes;
    }

    /**
     * @return
     */
    private Class<?> deduceMainApplicationClass() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(this::findMainClass)
                .orElse(null);
    }

    /**
     * @param stack
     * @return
     */
    private Optional<Class<?>> findMainClass(Stream<StackFrame> stack) {
        return stack.filter((frame) -> Objects.equals(frame.getMethodName(), "main"))
                .findFirst()
                .map(StackFrame::getDeclaringClass);
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param scanClass
     * @return
     */
    private List<Class<?>> findClassByFile(String packageName, String packagePath, Class<? extends Annotation>[] scanClass) {

        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirFiles = Optional.ofNullable(dir.listFiles(file ->
                (file.isDirectory()) || (file.getName().endsWith(".class")))).orElse(new File[]{});
        // 循环所有文件
        List<Class<?>> classes = new ArrayList<>();
        for (File file : dirFiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                String name = file.getName();
                if (!packageName.isEmpty()) {
                    name = packageName + "." + file.getName();
                }
                List<Class<?>> classInDir = findClassByFile(name, file.getAbsolutePath(), scanClass);
                classes.addAll(classInDir);
            } else {
                try {
                    // 如果是java类文件 去掉后面的.class 只留下类名
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    String classPath = className;
                    if (!packageName.isEmpty()) {
                        classPath = packageName + '.' + className;
                    }
                    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(classPath);
                    if (classFilter(clazz, scanClass)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return classes;
    }

    /**
     * @param url
     * @param packageDirName
     * @param scanClass
     * @return
     */
    private List<Class<?>> findClassByJar(URL url, String packageDirName, Class<? extends Annotation>[] scanClass) {
        try {
            // 如果是jar包文件, 获取jar
            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
            // 从此jar包 得到一个枚举类
            Enumeration<JarEntry> entries = jar.entries();
            // 同样的进行循环迭代
            List<Class<?>> classes = new ArrayList<>();
            while (entries.hasMoreElements()) {
                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                // 如果是以/开头的
                if (name.charAt(0) == '/') {
                    // 获取后面的字符串
                    name = name.substring(1);
                }
                // 如果前半部分和定义的包名相同
                if (name.startsWith(packageDirName)) {
                    int idx = name.lastIndexOf('/');
                    String packageName = "";
                    // 如果以"/"结尾 是一个包
                    if (idx != -1) {
                        // 获取包名 把"/"替换成"."
                        packageName = name.substring(0, idx).replace('/', '.');
                    }
                    // 如果可以迭代下去 并且是一个包
                    if ((idx != -1)) {
                        // 如果是一个.class文件 而且不是目录
                        if (name.endsWith(".class") && !entry.isDirectory()) {
                            // 去掉后面的".class" 获取真正的类名
                            String className = name.substring(
                                    packageName.length() + 1, name.length() - 6);
                            try {
                                // 添加到classes
                                String classPath = packageName + '.' + className;
                                Class<?> clazz = Class.forName(classPath);
                                if (classFilter(clazz, scanClass)) {
                                    classes.add(clazz);
                                }
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                }
            }
            return classes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 类过滤
     *
     * @param clazz
     * @param annotationClasses
     * @return
     */
    private boolean classFilter(Class<?> clazz, Class<? extends Annotation>[] annotationClasses) {
        // 类上注解过滤
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            Annotation annotation = clazz.getAnnotation(annotationClass);
            if (annotation != null) {
                return true;
            }
        }
        return false;
    }
}
