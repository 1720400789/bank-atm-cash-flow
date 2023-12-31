package org.zj.atm.project.dao.enrity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zj.atm.framework.starter.database.base.BaseDO;

/**
 * 
 * @TableName t_debit_card_goto_0
 */
@TableName(value ="t_debit_card_goto")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DebitCardGotoDO extends BaseDO implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 银行卡号
     */
    @TableField(value = "debit_card_id")
    private String debitCardId;

    /**
     * 用户身份证号
     */
    @TableField(value = "identity_id")
    private String identityId;

    @TableField(value = "deletion_time")
    private String deletionTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}