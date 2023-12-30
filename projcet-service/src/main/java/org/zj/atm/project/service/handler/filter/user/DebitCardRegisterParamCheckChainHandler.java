package org.zj.atm.project.service.handler.filter.user;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.zj.atm.framework.starter.convention.exception.ClientException;
import org.zj.atm.project.common.enums.DebitCardErrorCodeEnum;
import org.zj.atm.project.dto.req.DebitCardRegisterReqDTO;
import org.zj.atm.project.toolkit.IdentityVerify;

import java.util.Objects;

/**
 * 用户注册参数检验
 */
@Component
@ConditionalOnProperty(name = "atm.user-service.if_test", havingValue = "false")
public final class DebitCardRegisterParamCheckChainHandler implements DebitCardRegisterCreateChainFilter<DebitCardRegisterReqDTO> {

    /**
     * TODO 密码必须为 6 位数字 address不能为空
     * 校验了：- 姓名不能为空 -身份证号不能为空 -手机号不能为空 -身份证号不能填错 -密码不能为空 -年龄须大于等于18岁
     * 另外关于 cardType 、initailBalance 的校验，因为是预留字段，不一定使用，暂不校验
     */
    @Override
    public void handler(DebitCardRegisterReqDTO requestParam) {
        if (Objects.isNull(requestParam.getRealName())) {
            throw new ClientException(DebitCardErrorCodeEnum.REAL_NAME_NOTNULL);
        } else if (Objects.isNull(requestParam.getIdentityId())) {
            throw new ClientException(DebitCardErrorCodeEnum.ID_CARD_NOTNULL);
        } else if (Objects.isNull(requestParam.getPhone())) {
            throw new ClientException(DebitCardErrorCodeEnum.PHONE_NOTNULL);
        } else if (!IdentityVerify.checkIDNo(requestParam.getIdentityId())) {
            throw new ClientException(DebitCardErrorCodeEnum.ID_CARD_VERIFY_ERROR);
        } else if (Objects.isNull(requestParam.getPwd())) {
            throw new ClientException(DebitCardErrorCodeEnum.PASSWORD_NOTNULL);
        } else if (!IdentityVerify.checkAge(requestParam.getIdentityId())) {
            throw new ClientException(DebitCardErrorCodeEnum.AGE_TOO_YOUNG);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
