package cc.ryujingames.security.utils;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GsonFactory {

    private static final String CLASS_KEY = "0d13d874-7213-4401-9e5c-345d2c0f5aa6";

    /*
    - I want to not use Bukkit parsing for most objects... it's kind of clunky
    - Instead... I want to start using any of Mojang's tags
    - They're really well documented + built into MC, and handled by them.
    - Rather than kill your old code, I'm going to write TypeAdapaters using Mojang's stuff.
     */
    private Gson g = new Gson();
    private Gson prettyGson;
    private Gson compactGson;

    /**
     * Returns a Gson instance for use anywhere with new line pretty printing
     * <p>
     * Use @GsonIgnore in order to skip serialization and deserialization
     * </p>
     *
     * @return a Gson instance
     */
    public Gson getPrettyGson() {
        if (prettyGson == null)
            prettyGson = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create();
        return prettyGson;
    }

    /**
     * Returns a Gson instance for use anywhere with one line strings
     * <p>
     * Use @GsonIgnore in order to skip serialization and deserialization
     * </p>
     *
     * @return a Gson instance
     */
    public Gson getCompactGson() {
        if (compactGson == null)
            compactGson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .create();
        return compactGson;
    }

    private Map<String, Object> recursiveSerialization(ConfigurationSerializable o) {
        Map<String, Object> originalMap = o.serialize();
        Map<String, Object> map = new HashMap<>();
        originalMap.forEach((key, o2) -> {
            if (o2 instanceof ConfigurationSerializable) {
                ConfigurationSerializable serializable = (ConfigurationSerializable) o2;
                Map<String, Object> newMap = recursiveSerialization(serializable);
                newMap.put(CLASS_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
                map.put(key, newMap);
            }
        });
        map.put(CLASS_KEY, ConfigurationSerialization.getAlias(o.getClass()));
        return map;
    }

    private Map<String, Object> recursiveDoubleToInteger(Map<String, Object> originalMap) {
        Map<String, Object> map = new HashMap<String, Object>();
        originalMap.forEach((key, o) -> {
            if (o instanceof Double) {
                Double d = (Double) o;
                Integer i = d.intValue();
                map.put(key, i);
            } else if (o instanceof Map) {
                Map<String, Object> subMap = (Map<String, Object>) o;
                map.put(key, recursiveDoubleToInteger(subMap));
            } else {
                map.put(key, o);
            }
        });
        return map;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public static @interface Ignore {
    }

    private class ExposeExlusion implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            final Ignore ignore = fieldAttributes.getAnnotation(Ignore.class);
            if (ignore != null)
                return true;
            final Expose expose = fieldAttributes.getAnnotation(Expose.class);
            return expose != null && (!expose.serialize() || !expose.deserialize());
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    }

    private class LocationGsonAdapter extends TypeAdapter<Location> {

        private Type seriType = new TypeToken<Map<String, Object>>() {
        }.getType();

        private String UUID = "uuid";
        private String X = "x";
        private String Y = "y";
        private String Z = "z";

        @Override
        public void write(JsonWriter jsonWriter, Location location) throws IOException {
            if (location == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(getRaw(location));
        }

        @Override
        public Location read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            return fromRaw(jsonReader.nextString());
        }

        private String getRaw(Location location) {
            Map<String, Object> serial = new HashMap<>();
            serial.put(UUID, location.getWorld().getUID().toString());
            serial.put(X, Double.toString(location.getX()));
            serial.put(Y, Double.toString(location.getY()));
            serial.put(Z, Double.toString(location.getZ()));
            return g.toJson(serial);
        }

        private Location fromRaw(String raw) {
            Map<String, Object> keys = g.fromJson(raw, seriType);
            World w = Bukkit.getWorld(java.util.UUID.fromString((String) keys.get(UUID)));
            return new Location(w, Double.parseDouble((String) keys.get(X)), Double.parseDouble((String) keys.get(Y)), Double.parseDouble((String) keys.get(Z)));
        }
    }
}
