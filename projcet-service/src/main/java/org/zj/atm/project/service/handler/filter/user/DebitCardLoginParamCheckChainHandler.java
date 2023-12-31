package org.zj.atm.project.service.handler.filter.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.zj.atm.framework.starter.convention.exception.ClientException;
import org.zj.atm.project.common.enums.DebitCardErrorCodeEnum;
import org.zj.atm.project.dto.req.DebitCardLoginReqDTO;
import org.zj.atm.project.toolkit.IOS15DebitCardCreate;
import org.zj.atm.project.toolkit.IdentityVerify;

import java.util.Objects;

/**
 * 用户使用银行卡登录参数检验
 */
@Component
@ConditionalOnProperty(name = "atm.user-service.if_test", havingValue = "false")
public class DebitCardLoginParamCheckChainHandler implements DebitCardLoginCreateChainFilter<DebitCardLoginReqDTO> {

    @Value("${atm.debit_card.bank_card_prefix}")
    private String debitCardPrefix;

    @Override
    public void handler(DebitCardLoginReqDTO requestParam) {
        if (Objects.isNull(requestParam.getPwd())) {
            throw new ClientException(DebitCardErrorCodeEnum.PASSWORD_NOTNULL);
        } else if (!IdentityVerify.checkStrLength(requestParam.getPwd(), 6)) {
            throw new ClientException(DebitCardErrorCodeEnum.PASSWORD_MUST_SIX);
        } else if (!IOS15DebitCardCreate.checkIDNo(requestParam.getDebitCardId(), debitCardPrefix)) {
            throw new ClientException(DebitCardErrorCodeEnum.DEBIT_CARD_REGIX_ERROR);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
