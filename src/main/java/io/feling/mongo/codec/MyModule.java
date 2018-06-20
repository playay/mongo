package io.feling.mongo.codec;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class MyModule extends Module {
    @Override
    public String getModuleName() {
        return "MyModule";
    }

    @Override
    public Version version() {
        return new Version(0, 0, 0, "", "net.novaplanet", "mongo");
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new MySerializers());
        context.addDeserializers(new MyDeserializers());
    }
}
