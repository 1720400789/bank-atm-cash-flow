package org.zj.atm.project.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zj.atm.project.dao.serializers.DebitCardIdDesensitizationSerializer;
import org.zj.atm.project.dao.serializers.IdCardDesensitizationSerializer;
import org.zj.atm.project.dao.serializers.NameDesensitizationSerializer;
import org.zj.atm.project.dao.serializers.PhoneDesensitizationSerializer;

import java.math.BigDecimal;

/**
 * 开户返回实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardRegisterRespDTO {

    /**
     * 银行卡号
     */
//    @JsonSerialize(using = DebitCardIdDesensitizationSerializer.class)
    private String debitCardId;

    /**
     * 开户人姓名
     */
    @JsonSerialize(using = NameDesensitizationSerializer.class)
    private String realName;

    /**
     * 开户手机号
     */
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 开户人证件号
     */
    @JsonSerialize(using = IdCardDesensitizationSerializer.class)
    private String identityId;

    /**
     * 当前余额
     */
    private BigDecimal currentBalance;
}
