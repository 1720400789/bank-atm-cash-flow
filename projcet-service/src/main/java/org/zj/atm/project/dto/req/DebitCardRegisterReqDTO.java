package org.zj.atm.project.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 开户请求实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardRegisterReqDTO {

    /**
     * 身份证号
     */
    private String identityId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 想要申请的卡号密码
     */
    private String pwd;

    /**
     * 住址
     */
    private String address;

    /**
     * 想要申请的卡的类型
     */
    private Integer cardType;

    /**
     * 开户时存入的金额
     */
    private BigDecimal initailBalance;

}
