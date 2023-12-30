package org.zj.atm.user.service.handler.filter.user;

import org.zj.atm.framework.starter.designpattern.chain.AbstractChainHandler;
import org.zj.atm.user.common.enums.UserChainMarkEnum;
import org.zj.atm.user.dto.req.UserRegisterReqDTO;

/**
 * 用户注册责任链过滤器
 */
public interface UserRegisterCreateChainFilter<T extends UserRegisterReqDTO> extends AbstractChainHandler<UserRegisterReqDTO> {

    @Override
    default String mark() {
        return UserChainMarkEnum.USER_REGISTER_FILTER.name();
    }
}
