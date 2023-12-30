package org.zj.atm.user.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oracle.truffle.api.dsl.NodeChild;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zj.atm.framework.starter.database.base.BaseDO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_user")
public class UserDO extends BaseDO {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 真实姓名
     */
    @TableField(value = "real_name")
    private String realName;

    /**
     * 身份证号
     */
    @TableField(value = "identity_id")
    private String identityId;

    /**
     * 用户性别
     */
    @TableField(value = "gender")
    private String gender;

    /**
     * 联系电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 常住地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 冻结标识 0：未冻结 1：已冻结
     */
    @TableField(value = "freeze_flag")
    private Integer freezeFlag;

    /**
     * 上次冻结时间
     */
    @TableField(value = "freeze_time")
    private Long freezeTime;

    /**
     * 注销时间戳
     */
    @TableField(value = "deletion_time")
    private Long deletionTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
