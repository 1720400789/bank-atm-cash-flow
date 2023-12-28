package org.zj.atm.framework.starter.bases;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Application context holder.
 * 明明可以直接通过 Spring 容器获取 或者通过自动注入的方式获取，但是这里还是自己搞了一个 Spring 上下文的类，就是考虑到有些类不能加载进 Spring 容器
 * 不受 Ioc 容器管理自然也不能被注入，所以 Spring 上下文就是来应对这个问题的
 * 重点是继承 Spring 提供的 ApplicationContextAware 并实现 setApplicationContext，这里就可以通过 setApplicationContext 的参数拿到 Spring 容器了
 * 当 Spring 容器完成后，就会启动这里的 setApplicationContext 将 Spring 上下文给 CONTEXT 变量
 * 不用担心 ApplicationContextHolder 不受 Spring 管理，详情请看 ApplicationBaseAutoConfiguration
 */
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.CONTEXT = applicationContext;
    }

    /**
     * Get ioc container bean by type.
     */
    public static <T> T getBean(Class<T> clazz) {
        return CONTEXT.getBean(clazz);
    }

    /**
     * Get ioc container bean by name.
     */
    public static Object getBean(String name) {
        return CONTEXT.getBean(name);
    }

    /**
     * Get ioc container bean by name and type.
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return CONTEXT.getBean(name, clazz);
    }

    /**
     * Get a set of ioc container beans by type.
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return CONTEXT.getBeansOfType(clazz);
    }

    /**
     * Find whether the bean has annotations.
     */
    public static <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        return CONTEXT.findAnnotationOnBean(beanName, annotationType);
    }

    /**
     * Get application context.
     */
    public static ApplicationContext getInstance() {
        return CONTEXT;
    }
}
