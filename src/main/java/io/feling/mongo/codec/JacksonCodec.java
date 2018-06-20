/*
 * Copyright 2015 Yann Le Moigne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.feling.mongo.codec;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonModule;
import de.undercouch.bson4jackson.BsonParser;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.io.IOException;
import java.io.UncheckedIOException;

class JacksonCodec<T> implements Codec<T> {
    private ObjectMapper bsonObjectMapper;
    private final Codec<RawBsonDocument> rawBsonDocumentCodec;
    private final Class<T> type;

    public JacksonCodec(CodecRegistry codecRegistry,
                        Class<T> type) {
        this.rawBsonDocumentCodec = codecRegistry.get(RawBsonDocument.class);
        this.type = type;

        BsonFactory bsonFactory = new BsonFactory();
        bsonFactory.enable(BsonParser.Feature.HONOR_DOCUMENT_LENGTH);
        bsonObjectMapper = new ObjectMapper(bsonFactory);
        bsonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        bsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        bsonObjectMapper.registerModule(new BsonModule());
        bsonObjectMapper.registerModule(new MyModule());
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        try {
            RawBsonDocument document = rawBsonDocumentCodec.decode(reader, decoderContext);
            return bsonObjectMapper.readValue(document.getByteBuffer().array(), type);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void encode(BsonWriter writer, Object value, EncoderContext encoderContext) {
        try {
            byte[] data = bsonObjectMapper.writeValueAsBytes(value);
            rawBsonDocumentCodec.encode(writer, new RawBsonDocument(data), encoderContext);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Class<T> getEncoderClass() {
        return this.type;
    }
}