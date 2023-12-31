package org.zj.atm.project.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户使用银行卡登录请求实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardLoginReqDTO {

    /**
     * 银行卡号
     */
    private String debitCardId;

    /**
     * 密码
     */
    private String pwd;
}
