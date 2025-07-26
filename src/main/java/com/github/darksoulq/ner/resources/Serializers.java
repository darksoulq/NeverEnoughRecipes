package com.github.darksoulq.ner.resources;

import com.github.darksoulq.abyssallib.server.config.serializer.ConfigSerializer;
import com.github.darksoulq.abyssallib.server.config.serializer.SerializerRegistry;
import com.github.darksoulq.ner.data.InputType;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;

public class Serializers {

    public static void setup() {
        SerializerRegistry.register(InputType.class, new InputTypeSerializer());
    }

    public static class InputTypeSerializer implements ConfigSerializer<InputType> {

        @Override
        public Object serialize(InputType value) {
            return value.name();
        }

        @Override
        public InputType deserialize(Object in, Field field) {
            return InputType.valueOf((String) in);
        }
    }
}
