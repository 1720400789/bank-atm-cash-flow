package org.zj.atm.gateway.config;

import lombok.Data;

import java.util.List;

/**
 * 过滤器配置
 * TODO 网关服务启动时会自动向这个类中注入 application-aggregation.yaml 中配置的 blackPathPre
 * 至于怎么配置的还在思考
 */
@Data
public class Config {

    /**
     * 黑名单前置路径
     */
    private List<String> blackPathPre;
}
