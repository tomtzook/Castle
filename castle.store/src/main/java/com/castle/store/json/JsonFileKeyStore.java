package com.castle.store.json;

import com.castle.store.KeyStore;
import com.castle.store.exceptions.KeyNotFoundException;
import com.castle.store.exceptions.StoreException;
import com.castle.util.throwables.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class JsonFileKeyStore<V> implements KeyStore<String, V> {

    private final Path mPath;
    private final Class<V> mValueType;
    private final Gson mGson;
    private final JsonParser mParser;
    private final AtomicReference<JsonObject> mStoreRoot;

    public JsonFileKeyStore(Path path, Class<V> valueType, Gson gson, JsonParser parser) {
        mPath = path;
        mValueType = valueType;
        mGson = gson;
        mParser = parser;
        mStoreRoot = new AtomicReference<>();
    }

    @Override
    public boolean exists(String key) throws StoreException {
        try {
            JsonObject root = getRoot();
            return root.has(key);
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public boolean existsAll(Collection<? extends String> keys) throws StoreException {
        try {
            JsonObject root = getRoot();
            for (String key : keys) {
                if (!root.has(key)) {
                    return false;
                }
            }

            return true;
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public V retrieve(String key) throws StoreException {
        try {
            JsonObject root = getRoot();
            JsonElement element = root.get(key);
            if (element == null) {
                throw new KeyNotFoundException(String.valueOf(key));
            }

            return mGson.fromJson(element, mValueType);
        } catch (KeyNotFoundException e) {
            throw e;
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public Optional<V> tryRetrieve(String key) throws StoreException {
        try {
            JsonObject root = getRoot();
            JsonElement element = root.get(key);
            if (element == null) {
                return Optional.empty();
            }

            return Optional.of(mGson.fromJson(element, mValueType));
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public <T extends V> T retrieve(String key, Class<T> type) throws StoreException {
        try {
            JsonObject root = getRoot();
            JsonElement element = root.get(key);
            if (element == null) {
                throw new KeyNotFoundException(String.valueOf(key));
            }

            return mGson.fromJson(element, type);
        } catch (KeyNotFoundException e) {
            throw e;
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public <T extends V> Optional<T> tryRetrieve(String key, Class<T> type) throws StoreException {
        try {
            JsonObject root = getRoot();
            JsonElement element = root.get(key);
            if (element == null) {
                return Optional.empty();
            }

            return Optional.of(mGson.fromJson(element, type));
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public Map<String, V> retrieve(Collection<? extends String> keys) throws StoreException {
        try {
            Map<String, V> result = new HashMap<>();
            JsonObject root = getRoot();

            for (String key : keys) {
                JsonElement element = root.get(key);
                V value = mGson.fromJson(element, mValueType);
                result.put(key, value);
            }

            return result;
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public <T extends V> Map<String, T> retrieve(Collection<? extends String> keys, Class<T> type) throws StoreException {
        try {
            Map<String, T> result = new HashMap<>();
            JsonObject root = getRoot();

            for (String key : keys) {
                JsonElement element = root.get(key);
                T value = mGson.fromJson(element, type);
                result.put(key, value);
            }

            return result;
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public Optional<V> retrieveFirst(BiPredicate<? super String, ? super V> filter) throws StoreException {
        try {
            JsonObject root = getRoot();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                V value = mGson.fromJson(entry.getValue(), mValueType);
                if (filter.test(entry.getKey(), value)) {
                    return Optional.of(value);
                }
            }

            return Optional.empty();
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public Map<String, V> retrieveAll(BiPredicate<? super String, ? super V> filter) throws StoreException {
        try {
            Map<String, V> values = new HashMap<>();

            JsonObject root = getRoot();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                V value = mGson.fromJson(entry.getValue(), mValueType);
                if (filter.test(entry.getKey(), value)) {
                    values.put(entry.getKey(), value);
                }
            }

            return values;
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public Map<String, V> retrieveAll() throws StoreException {
        try {
            Map<String, V> values = new HashMap<>();

            JsonObject root = getRoot();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                V value = mGson.fromJson(entry.getValue(), mValueType);
                values.put(entry.getKey(), value);
            }

            return values;
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super V> consumer) throws StoreException {
        try {
            JsonObject root = getRoot();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                V value = mGson.fromJson(entry.getValue(), mValueType);
                consumer.accept(entry.getKey(), value);
            }
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public Optional<V> store(String key, V value) throws StoreException {
        try {
            JsonElement element = mGson.toJsonTree(value);
            JsonElement old = addToRoot(key, element);
            if (old == null) {
                return Optional.empty();
            }

            return Optional.of(mGson.fromJson(element, mValueType));
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public void storeAll(Map<? extends String, ? extends V> values) throws StoreException {
        try {
            Map<String, JsonElement> json = new HashMap<>();
            for (Map.Entry<? extends String, ? extends V> entry : values.entrySet()) {
                JsonElement element = mGson.toJsonTree(entry.getValue());
                json.put(entry.getKey(), element);
            }

            addToRoot(json);
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public boolean delete(String key) throws StoreException {
        try {
            JsonObject root = getRoot();
            if (root.has(key)) {
                root.remove(key);
                return true;
            }

            return false;
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    @Override
    public boolean deleteAll(Collection<? extends String> keys) throws StoreException {
        try {
            JsonObject root = getRoot();
            boolean removedOnce = false;
            for (String key : keys) {
                if (root.has(key)) {
                    root.remove(key);
                    removedOnce = true;
                }
            }
            storeRoot(root);

            return removedOnce;
        } catch (Throwable t) {
            throw Throwables.getAsType(t, StoreException.class, StoreException::new);
        }
    }

    private JsonElement addToRoot(String key, JsonElement element) throws IOException {
        JsonObject root = getRoot();
        JsonElement old = root.get(key);
        root.add(key, element);
        storeRoot(root);

        return old;
    }

    private void addToRoot(Map<String, JsonElement> elements) throws IOException {
        JsonObject root = getRoot();
        for (Map.Entry<String, JsonElement> entry : elements.entrySet()) {
            root.add(entry.getKey(), entry.getValue());
        }

        storeRoot(root);
    }

    private JsonObject getRoot() throws IOException {
        JsonObject root = mStoreRoot.get();
        if (root != null) {
            return root;
        }

        try(InputStream inputStream = Files.newInputStream(mPath)) {
            JsonElement element = mParser.parse(new InputStreamReader(inputStream));

            if (!element.isJsonObject()) {
                throw new IOException("Expected root of file to be object");
            }

            JsonObject object = element.getAsJsonObject();
            mStoreRoot.set(object);

            return object;
        }
    }

    private void storeRoot(JsonObject root) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(mPath)) {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setLenient(false);
            jsonWriter.setIndent("\t");
            Streams.write(root, jsonWriter);

            outputStream.write(stringWriter.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
}
