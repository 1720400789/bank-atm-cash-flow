package org.zj.atm.user.service.handler.filter.user;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.zj.atm.framework.starter.convention.exception.ClientException;
import org.zj.atm.user.common.enums.UserRegisterErrorCodeEnum;
import org.zj.atm.user.dto.req.UserRegisterReqDTO;
import org.zj.atm.user.toolkit.IdentityVerify;

import java.util.Objects;

/**
 * 用户注册参数检验
 */
@Component
@ConditionalOnProperty(name = "atm.user-service.if_test", havingValue = "false")
public final class UserRegisterParamCheckChainHandler implements UserRegisterCreateChainFilter<UserRegisterReqDTO> {

    @Override
    public void handler(UserRegisterReqDTO requestParam) {
        if (Objects.isNull(requestParam.getRealName())) {
            throw new ClientException(UserRegisterErrorCodeEnum.REAL_NAME_NOTNULL);
        } else if (Objects.isNull(requestParam.getIdentityId())) {
            throw new ClientException(UserRegisterErrorCodeEnum.ID_CARD_NOTNULL);
        } else if (Objects.isNull(requestParam.getAddress())) {
            throw new ClientException(UserRegisterErrorCodeEnum.ADDRESS_NOTNULL);
        } else if (!IdentityVerify.checkIDNo(requestParam.getIdentityId())) {
            throw new ClientException(UserRegisterErrorCodeEnum.ID_CARD_VERIFY_ERROR);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
