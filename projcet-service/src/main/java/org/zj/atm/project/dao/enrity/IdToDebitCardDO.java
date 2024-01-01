package org.zj.atm.project.dao.enrity;

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
@TableName(value = "t_id_to_debit_card_goto_0")
public class IdToDebitCardDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 银行卡记录主键
     */
    @TableField(value = "card_id")
    private Long cardId;

    /**
     * 银行卡号
     */
    @TableField(value = "debit_card_id")
    private String debitCardId;

    @TableField(value = "deletion_time")
    private String deletionTime;
}
