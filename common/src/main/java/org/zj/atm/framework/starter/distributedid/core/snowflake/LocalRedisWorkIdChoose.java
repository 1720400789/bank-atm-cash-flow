package org.zj.atm.framework.starter.distributedid.core.snowflake;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.zj.atm.framework.starter.bases.ApplicationContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用 Redis 获取雪花 WorkId
 */
@Slf4j
public class LocalRedisWorkIdChoose extends AbstractWorkIdChooseTemplate implements InitializingBean {

    private RedisTemplate stringRedisTemplate;

    public LocalRedisWorkIdChoose() {
        this.stringRedisTemplate = ApplicationContextHolder.getBean(StringRedisTemplate.class);
    }

    @Override
    public WorkIdWrapper chooseWorkId() {
        // 执行lua脚本，准备拿取下一个标识位
        DefaultRedisScript redisScript = new DefaultRedisScript();
        // lua脚本源
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/chooseWorkIdLua.lua")));
        List<Long> luaResultList = null;
        try {
            // 脚本返回值
            redisScript.setResultType(List.class);
            // lua脚本执行结果
            luaResultList = (ArrayList) this.stringRedisTemplate.execute(redisScript, null);
        } catch (Exception ex) {
            log.error("Redis Lua 脚本获取 WorkId 失败", ex);
        }
        // lua返回值非空则正常返回一个WorkIdWrapper对象（分别为workId和dataCenterId），否则返回一个根据随机数取得的雪花算法ID
        return CollUtil.isNotEmpty(luaResultList) ? new WorkIdWrapper(luaResultList.get(0), luaResultList.get(1)) : new RandomWorkIdChoose().chooseWorkId();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        chooseAndInit();
    }
}
