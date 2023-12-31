package org.zj.atm.user.dao.serializers;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 身份证号脱敏反序列化
 */
public class IdCardDesensitizationSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String identityId, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String desensitization = DesensitizedUtil.idCardNum(identityId, 4, 4);
        jsonGenerator.writeString(desensitization);
    }
}
