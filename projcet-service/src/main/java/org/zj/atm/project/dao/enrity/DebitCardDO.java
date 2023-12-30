package org.zj.atm.project.dao.enrity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.zj.atm.framework.starter.database.base.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @TableName t_debit_card
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value ="t_debit_card")
public class DebitCardDO extends BaseDO implements Serializable {
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
     * 银行卡号对应密码，本行必须为 6 位数字
     */
    @TableField(value = "pwd")
    private String pwd;

    /**
     * 用户身份证号
     */
    @TableField(value = "identity_id")
    private String identityId;

    /**
     * 联系电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 开户时的账户金额
     */
    @TableField(value = "initial_balance")
    private BigDecimal initialBalance;

    /**
     * 当前的账户余额
     */
    @TableField(value = "account_balance")
    private BigDecimal accountBalance;

    /**
     * 银行卡类型
     */
    @TableField(value = "card_type")
    private Integer cardType;

    /**
     * 存储利率
     */
    @TableField(value = "rate")
    private BigDecimal rate;

    /**
     * 银行卡状态 0：正常 1：冻结
     */
    @TableField(value = "card_status")
    private Integer cardStatus;

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
