package org.zj.atm.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String identityId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 银行卡号
     */
    private String debitCardId;

    /**
     * 返回给前端的令牌 Token
     */
    private String accessToken;


}
