package org.zj.atm.project.service.handler.filter.user;

import org.zj.atm.framework.starter.designpattern.chain.AbstractChainHandler;
import org.zj.atm.project.common.enums.DebitChainMarkEnum;
import org.zj.atm.project.dto.req.DebitCardLoginReqDTO;

public interface DebitCardLoginCreateChainFilter<T extends DebitCardLoginReqDTO> extends AbstractChainHandler<DebitCardLoginReqDTO> {

    @Override
    default String mark() {
        return DebitChainMarkEnum.DEBIT_CARD_LOGIN_FILTER.name();
    }
}
