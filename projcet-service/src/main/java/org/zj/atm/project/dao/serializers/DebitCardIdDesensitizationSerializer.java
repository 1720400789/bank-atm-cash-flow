package org.zj.atm.project.dao.serializers;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 银行卡号脱敏反序列化
 */
public class DebitCardIdDesensitizationSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String debitCardId, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String desensitization = DesensitizedUtil.bankCard(debitCardId);
        jsonGenerator.writeString(desensitization);
    }
}
