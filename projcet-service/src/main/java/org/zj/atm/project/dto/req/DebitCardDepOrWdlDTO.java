package org.zj.atm.project.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardDepOrWdlDTO {

    /**
     * 银行卡号
     * 考虑了一下决定不传入银行卡号了，从 UserContext 中拿吧
     */
//    private String debitCardId;

    /**
     * 操作金额
     */
    private BigDecimal operationAmount;

    /**
     * 操作类型
     * 1 表示存款
     * 2 表示取款
     */
    private Integer operationType;

}
