package ru.hse.mmstr_project.se.storage.fast_storage.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.FriendMetaDto;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationHandler;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KryoRedisSerializer implements RedisSerializer<Object> {
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();

        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

        kryo.register(IncidentMetadataDto.class);
        kryo.register(FriendMetaDto.class);

        kryo.register(ArrayList.class);
        kryo.register(LinkedList.class);
        kryo.register(HashMap.class);
        kryo.register(HashSet.class);
        kryo.register(List.class);
        kryo.register(Map.class);
        kryo.register(Set.class);
        kryo.register(String[].class);
        kryo.register(Object[].class);
        kryo.register(Integer[].class);
        kryo.register(Long[].class);

        kryo.register(String.class);
        kryo.register(Integer.class);
        kryo.register(Long.class);
        kryo.register(Double.class);
        kryo.register(Boolean.class);

        kryo.register(Instant.class, new JodaDateTimeSerializer());

        kryo.register(Arrays.asList("").getClass(), new JavaSerializer());
        kryo.register(Collections.EMPTY_LIST.getClass(), new JavaSerializer());
        kryo.register(Collections.EMPTY_MAP.getClass(), new JavaSerializer());
        kryo.register(Collections.EMPTY_SET.getClass(), new JavaSerializer());
        kryo.register(Collections.singletonList("").getClass(), new JavaSerializer());
        kryo.register(Collections.singleton("").getClass(), new JavaSerializer());
        kryo.register(Collections.singletonMap("", "").getClass(), new JavaSerializer());
        kryo.register(InvocationHandler.class, new JdkProxySerializer());

        kryo.setRegistrationRequired(false);
        kryo.setReferences(true);
        return kryo;
    });

    @Override
    public byte[] serialize(Object t) throws SerializationException {

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             Output output = new Output(stream)) {

            Kryo kryo = kryoThreadLocal.get();
            kryo.writeClassAndObject(output, t);
            output.flush();

            return stream.toByteArray();
        } catch (Exception e) {
            throw new SerializationException("Error serializing object using Kryo", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try (Input input = new Input(bytes)) {
            Kryo kryo = kryoThreadLocal.get();
            return kryo.readClassAndObject(input);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing object using Kryo", e);
        }
    }
}