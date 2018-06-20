package io.feling.mongo.codec;

import com.fasterxml.jackson.databind.module.SimpleSerializers;
import io.feling.mongo.codec.serializer.BigDecimalSerializer;
import io.feling.mongo.codec.serializer.JodaTimeSerializer;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class MySerializers extends SimpleSerializers {
    private static final long serialVersionUID = -8813058646811549826L;

    public MySerializers() {
        addSerializer(DateTime.class, new JodaTimeSerializer());
        addSerializer(BigDecimal.class, new BigDecimalSerializer());
    }
}
