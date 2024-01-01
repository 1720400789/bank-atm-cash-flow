package org.zj.atm.project.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zj.atm.project.dao.serializers.DebitCardIdDesensitizationSerializer;
import org.zj.atm.project.dao.serializers.DebitCardPwdDesensitizationSerializer;
import org.zj.atm.project.dao.serializers.PhoneDesensitizationSerializer;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardAnonymizedMsgDTO{

    /**
     * 银行卡号
     */
    @JsonSerialize(using = DebitCardIdDesensitizationSerializer.class)
    private String debitCardId;

    /**
     * 密码
     */
    @JsonSerialize(using = DebitCardPwdDesensitizationSerializer.class)
    private String pwd;

    /**
     * 联系电话
     */
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 开户时存入的金额
     */
    private BigDecimal initalBalance;

    /**
     * 现存金额
     */
    private BigDecimal accountBalance;

    /**
     * 卡的类型
     */
    private Integer cardType;

    /**
     * 利率
     */
    private BigDecimal rate;

    /**
     * 卡的状态
     */
    private Integer cardStatus;
}
