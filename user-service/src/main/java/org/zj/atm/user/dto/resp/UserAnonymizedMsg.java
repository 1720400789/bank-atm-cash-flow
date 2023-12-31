package org.zj.atm.user.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.events.Event;
import org.zj.atm.user.dao.serializers.IdCardDesensitizationSerializer;
import org.zj.atm.user.dao.serializers.NameDesensitizationSerializer;
import org.zj.atm.user.dao.serializers.PhoneDesensitizationSerializer;

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
    @JsonSerialize(using = NameDesensitizationSerializer.class)
    private String realName;

    /**
     * 身份证号
     */
    @JsonSerialize(using = IdCardDesensitizationSerializer.class)
    private String identityId;

    /**
     * 用户性别
     */
    private String gender;

    /**
     * 联系电话
     */
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
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
