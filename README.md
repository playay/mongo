mongoClient with PojoCodec(official), JodaTimeCodec(customize) etc.


## 引入maven依赖
```xml
<dependency>
  <groupId>io.feling</groupId>
  <artifactId>mongo</artifactId>
  <version>1.0.0</version>
</dependency>
```

## 添加配置文件
| key | value |
| :----- | :----- |
| 位置与文件名 | class.getResourceAsStream("/application.properties") |
| 内容 | spring.data.mongodb.uri= |


## 获取 collection 并使用

```java
MyPojo myPojo = MongoClients.getCollection("test", MyPojo.class).find().first();
```




