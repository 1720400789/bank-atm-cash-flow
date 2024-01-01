package org.zj.atm.user.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户注册请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterReqDTO {

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String identityId;

    /**
     * 住址
     */
    private String address;
}
