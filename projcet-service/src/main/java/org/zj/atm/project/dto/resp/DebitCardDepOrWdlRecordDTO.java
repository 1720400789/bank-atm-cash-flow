package org.zj.atm.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 存取款的返回数据实体
 * 凭条记录实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardDepOrWdlRecordDTO {

    /**
     * 单号，其实就是主键
     */
    private Long id;

    /**
     * 操作卡号
     */
    private String DebitCardId;

    /**
     * 操作人身份证号
     */
    private String identityId;

    /**
     * 操作人姓名
     */
    private String realName;

    /**
     * 操作金额
     */
    private BigDecimal operationAmount;

    /**
     * 操作时间
     */
    private Date operationTime;
}
