package org.zj.atm.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zj.atm.framework.starter.database.base.BaseDO;

/**
 * 
 * @TableName t_dep_wdl_record_0
 */
@TableName(value ="t_dep_wdl_record_0")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepWdlRecordDO extends BaseDO implements Serializable {
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

    /**
     * 操作金额
     */
    @TableField(value = "operation_amount")
    private BigDecimal operationAmount;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private Date operationTime;

    /**
     * 操作类型 1：存款 2：取款
     */
    @TableField(value = "operation_type")
    private Integer operationType;

    /**
     * 删除时间戳
     */
    @TableField(value = "deletion_time")
    private Long deletionTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}