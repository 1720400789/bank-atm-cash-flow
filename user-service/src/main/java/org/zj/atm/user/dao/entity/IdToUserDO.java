package org.zj.atm.user.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zj.atm.framework.starter.database.base.BaseDO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_id_to_user_goto")
public class IdToUserDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户表主键
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 用户身份证
     */
    @TableField(value = "identity_id")
    private String identityId;

    @TableField(value = "deletion_time")
    private String deletionTime;

}
