package io.feling.mongo.codec;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import io.feling.mongo.codec.serializer.BigDecimalDeserializer;
import io.feling.mongo.codec.serializer.JodaTimeDeserializer;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class MyDeserializers extends SimpleDeserializers {
    private static final long serialVersionUID = 963123360913726980L;

    public MyDeserializers() {
        addDeserializer(DateTime.class, new JodaTimeDeserializer());
        addDeserializer(BigDecimal.class, new BigDecimalDeserializer());
    }

}
