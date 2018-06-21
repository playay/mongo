package io.feling.mongo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.feling.mongo.codec.JodaTimeCodec;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class MongoClients {

    private static Cache<String, MongoCollection> mongoCollectionCache = CacheBuilder
            .newBuilder()
            .maximumSize(512)
            .build();

    private static class MongoClientHolder {

        private static Properties properties = new Properties();

        static {
            try (InputStream mongoProperties = MongoClients.class.getResourceAsStream("/application.properties")) {
                if (mongoProperties != null) {
                    properties.load(mongoProperties);
                } else {
                    log.error("MongoClients 加载 application.properties 发生异常");
                    throw new RuntimeException("MongoClients 加载 application.properties 发生异常");
                }
            } catch (IOException e) {
                log.error("MongoClients 加载 application.properties 发生异常", e);
                throw new RuntimeException("MongoClients 加载 application.properties 发生异常");
            }
        }

        private static CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(new JodaTimeCodec()),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        private static MongoClientURI mongoClientURI = new MongoClientURI(
                properties.getProperty("spring.data.mongodb.uri"),
                MongoClientOptions.builder().codecRegistry(codecRegistry)
        );

        private static MongoClient mongoClient = new MongoClient(mongoClientURI);

        private static MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());

    }

    public MongoClients() {
    }

    public static <TDocument> MongoCollection<TDocument> getCollection(String collectionName, Class<TDocument> clazz) {
        try {
            return mongoCollectionCache.get(collectionName + clazz.toString(),
                    () -> MongoClientHolder.mongoDatabase.getCollection(collectionName, clazz)
            );
        } catch (ExecutionException e) {
            log.warn("从本地缓存获取 MongoCollection 发生异常", e);
            return MongoClientHolder.mongoDatabase.getCollection(collectionName, clazz);
        }
    }


}
