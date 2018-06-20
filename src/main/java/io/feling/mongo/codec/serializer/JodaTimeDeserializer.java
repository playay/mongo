package io.feling.mongo.codec.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.undercouch.bson4jackson.BsonConstants;
import de.undercouch.bson4jackson.BsonParser;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Date;

public class JodaTimeDeserializer extends JsonDeserializer<DateTime> {
    @Override
    public DateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p instanceof BsonParser) {
            BsonParser bsonParser = (BsonParser)p;
            if (bsonParser.getCurrentToken() != JsonToken.VALUE_EMBEDDED_OBJECT ||
                    bsonParser.getCurrentBsonType() != BsonConstants.TYPE_DATETIME) {
                ctxt.handleUnexpectedToken(DateTime.class, p);
            }
            return new DateTime((Date)bsonParser.getEmbeddedObject());
        } else {
            return new DateTime(p.getLongValue());
        }
    }
}
