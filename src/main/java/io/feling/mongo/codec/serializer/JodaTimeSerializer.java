package io.feling.mongo.codec.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.undercouch.bson4jackson.BsonGenerator;
import org.joda.time.DateTime;

import java.io.IOException;

public class JodaTimeSerializer extends JsonSerializer<DateTime> {

    @Override
    public void serialize(DateTime value,
                          JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        if (value == null) {
            serializers.defaultSerializeNull(gen);
        } else if (gen instanceof BsonGenerator) {
            BsonGenerator bgen = (BsonGenerator)gen;
            bgen.writeDateTime(value.toDate());
        } else {
            gen.writeNumber(value.getMillis());
        }
    }
}
