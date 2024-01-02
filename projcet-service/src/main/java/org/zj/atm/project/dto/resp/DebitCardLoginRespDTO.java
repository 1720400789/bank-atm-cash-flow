package org.zj.atm.project.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zj.atm.project.dao.serializers.DebitCardIdDesensitizationSerializer;
import org.zj.atm.project.dao.serializers.IdCardDesensitizationSerializer;
import org.zj.atm.project.dao.serializers.NameDesensitizationSerializer;

import java.math.BigDecimal;

/**
 * 用户使用银行卡登录返回数据实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DebitCardLoginRespDTO {

    /**
     * 身份证号
     */
//    @JsonSerialize(using = IdCardDesensitizationSerializer.class)
    private String identityId;

    /**
     * 真实姓名
     */
//    @JsonSerialize(using = NameDesensitizationSerializer.class)
    private String realName;

    /**
     * 银行卡号
     */
//    @JsonSerialize(using = DebitCardIdDesensitizationSerializer.class)
    private String debitCardId;

    /**
     * 账户余额
     */
    private BigDecimal accountBalance;

    /**
     * 返回给前端的令牌 Token
     */
    private String accessToken;


}
