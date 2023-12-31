package org.zj.atm.user.dao.serializers;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 姓名脱敏反序列化
 */
public class NameDesensitizationSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String realName, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String desensitization = DesensitizedUtil.chineseName(realName);
        jsonGenerator.writeString(desensitization);
    }
}
