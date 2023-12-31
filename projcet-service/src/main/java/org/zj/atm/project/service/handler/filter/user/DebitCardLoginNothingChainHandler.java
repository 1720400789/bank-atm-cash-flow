package org.zj.atm.project.service.handler.filter.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.zj.atm.project.dto.req.DebitCardLoginReqDTO;

@Slf4j
@Component
@ConditionalOnProperty(name = "atm.user-service.if_test", havingValue = "true")
public class DebitCardLoginNothingChainHandler implements DebitCardLoginCreateChainFilter<DebitCardLoginReqDTO> {
    @Override
    public void handler(DebitCardLoginReqDTO requestParam) {
        log.debug("测试环境的用户注册责任链");
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
