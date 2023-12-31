package org.zj.atm.project.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnonymizedMsg {

    /**
     * id
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String identityId;

    /**
     * 用户性别
     */
    private String gender;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 常住地址
     */
    private String address;

    /**
     * 冻结标识 0：未冻结 1：已冻结
     */
    private Integer freezeFlag;

    /**
     * 上次冻结时间
     */
    private Long freezeTime;

    /**
     * 注销时间戳
     */
    private Long deletionTime;
}
