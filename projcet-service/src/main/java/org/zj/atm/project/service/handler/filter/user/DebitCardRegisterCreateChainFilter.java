package org.zj.atm.project.service.handler.filter.user;

import org.zj.atm.framework.starter.designpattern.chain.AbstractChainHandler;
import org.zj.atm.project.common.enums.DebitChainMarkEnum;
import org.zj.atm.project.dto.req.DebitCardRegisterReqDTO;

/**
 * 用户注册责任链过滤器
 */
public interface DebitCardRegisterCreateChainFilter<T extends DebitCardRegisterReqDTO> extends AbstractChainHandler<DebitCardRegisterReqDTO> {

    @Override
    default String mark() {
        return DebitChainMarkEnum.DEBIT_CARD_REGISTER_FILTER.name();
    }
}
