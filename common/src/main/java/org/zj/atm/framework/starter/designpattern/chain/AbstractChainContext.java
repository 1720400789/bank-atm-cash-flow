package org.zj.atm.framework.starter.designpattern.chain;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.zj.atm.framework.starter.bases.ApplicationContextHolder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 抽象责任链上下文
 */
/*
 抽象上下文类，继承SpringBoot提供的CommandLineRunner接口（另外有个类似的接口ApplicationRunner）
 接口要求实现run方法，run方法会在Spring容器加载后执行
 此类需要被Spring容器管理，这里没有给@Component接口声明抽象上下文类被容器管理
 进入AbstractChainContext就知道了，AbstractChainContext实际上被DesignPatternAutoConfiguration管理
 */
public final class AbstractChainContext<T> implements CommandLineRunner {

    private final Map<String, List<AbstractChainHandler>> abstractChainHandlerContainer = new HashMap<>();

    /**
     * 责任链组件执行
     *
     * @param mark         责任链组件标识
     * @param requestParam 请求参数
     */
    public void handler(String mark, T requestParam) {
        List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(mark);
        if (CollectionUtils.isEmpty(abstractChainHandlers)) {
            throw new RuntimeException(String.format("[%s] Chain of Responsibility ID is undefined.", mark));
        }
        abstractChainHandlers.forEach(each -> each.handler(requestParam));
    }

    /**
     * Spring容器加载后即刻执行，
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        // 从Spring容器中拿到所有实现AbstractChainHandler接口（或其子接口）的Bean
        Map<String, AbstractChainHandler> chainFilterMap = ApplicationContextHolder
                .getBeansOfType(AbstractChainHandler.class);
        // 遍历BeanMap
        chainFilterMap.forEach((beanName, bean) -> {
            // 根据标识Mark的不同，对责任链Bean分类
            // 如果该标识Mark的Bean第一次遍历到，肯定不在Map：abstractChainHandlerContainer中，所以会新建一个List
            List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(bean.mark());
            // 如果Mark标识第一次出现就初始化一个新的List
            if (CollectionUtils.isEmpty(abstractChainHandlers)) {
                abstractChainHandlers = new ArrayList();
            }
            // 将对应标识Mark的bean加入List<AbstractChainHandler>
            abstractChainHandlers.add(bean);
            List<AbstractChainHandler> actualAbstractChainHandlers = abstractChainHandlers.stream()
                    .sorted(Comparator.comparing(Ordered::getOrder))
                    .collect(Collectors.toList());
            // 最后将不同Mark的List放入Map中
            abstractChainHandlerContainer.put(bean.mark(), actualAbstractChainHandlers);
        });
    }
}
