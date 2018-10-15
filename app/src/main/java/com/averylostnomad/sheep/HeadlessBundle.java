package com.averylostnomad.sheep;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class HeadlessBundle {

    private static final String CLASS_NAME = "__className";

    private static HashMap<String, String> aliases = new HashMap<String, String>();

    private JsonObject data;

    public HeadlessBundle() {
        this(new JsonObject());
    }

    public String toString() {
        return data.toString();
    }

    private HeadlessBundle(JsonObject data) {
        this.data = data;
    }

    public boolean isNull() {
        return data == null;
    }

    public ArrayList<String> fields() {
        ArrayList<String> result = new ArrayList<String>();

        @SuppressWarnings("unchecked")
        Iterator<String> iterator = data.names().iterator();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        return result;
    }

    public boolean contains(String key) {
        return data.get(key) != null;
    }

    public boolean getBoolean(String key) {
        return data.get(key).asBoolean();
    }

    public int getInt(String key) {
        return data.get(key).asInt();
    }

    public float getFloat(String key) {
        return (float) data.get(key).asFloat();
    }

    public String getString(String key) {
        return data.get(key).asString();
    }

    public HeadlessBundle getBundle(String key) {
        return new HeadlessBundle(data.get(key).asObject());
    }

    private HeadlessBundlable get() {
        try {
            String clName = getString(CLASS_NAME);
            if (aliases.containsKey(clName)) {
                clName = aliases.get(clName);
            }

            Class<?> cl = Class.forName(clName);
            if (cl != null) {
                HeadlessBundlable object = (HeadlessBundlable) cl.newInstance();
                object.restoreFromBundle(this);
                return object;
            } else {
                return null;
            }
        } catch (Exception e) {
            e = null;
            return null;
        }
    }

    public HeadlessBundlable get(String key) {
        return getBundle(key).get();
    }

    public <E extends Enum<E>> E getEnum(String key, Class<E> enumClass) {
        return Enum.valueOf(enumClass, data.get(key).asString());
    }

    public int[] getIntArray(String key) {
        JsonArray array = data.get(key).asArray();
        int length = array.size();
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = array.get(i).asInt();
        }
        return result;
    }

    public boolean[] getBooleanArray(String key) {
        JsonArray array = data.get(key).asArray();
        int length = array.size();
        boolean[] result = new boolean[length];
        for (int i = 0; i < length; i++) {
            result[i] = array.get(i).asBoolean();
        }
        return result;
    }

    public String[] getStringArray(String key) {
        JsonArray array = data.get(key).asArray();
        int length = array.size();
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            result[i] = array.get(i).asString();
        }
        return result;
    }

    public Collection<HeadlessBundlable> getCollection(String key) {

        ArrayList<HeadlessBundlable> list = new ArrayList<HeadlessBundlable>();

        JsonArray array = data.get(key).asArray();
        for (int i = 0; i < array.size(); i++) {
            list.add(new HeadlessBundle(array.get(i).asObject()).get());
        }

        return list;
    }

    public void put(String key, boolean value) {
        data.set(key, value);
    }

    public void put(String key, int value) {
        data.set(key, value);
    }

    public void put(String key, float value) {
        data.set(key, value);
    }

    public void put(String key, String value) {
        data.set(key, value);
    }

    public void put(String key, HeadlessBundle bundle) {
        data.set(key, bundle.data);
    }

    public void put(String key, HeadlessBundlable object) {
        if (object != null) {
            HeadlessBundle bundle = new HeadlessBundle();
            bundle.put(CLASS_NAME, object.getClass().getName());
            object.storeInBundle(bundle);
            data.set(key, bundle.data);
        }
    }

    public void put(String key, Enum<?> value) {
        if (value != null) {
            data.set(key, value.name());
        }
    }

    public void put(String key, int[] array) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < array.length; i++) {
            jsonArray.add(array[i]);
        }
        data.set(key, jsonArray);
    }

    public void put(String key, boolean[] array) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < array.length; i++) {
            jsonArray.add(array[i]);
        }
        data.set(key, jsonArray);
    }

    public void put(String key, String[] array) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < array.length; i++) {
            jsonArray.add(array[i]);
        }
        data.set(key, jsonArray);
    }

    public void put(String key, Collection<? extends HeadlessBundlable> collection) {
        JsonArray array = new JsonArray();
        for (HeadlessBundlable object : collection) {
            HeadlessBundle bundle = new HeadlessBundle();
            bundle.put(CLASS_NAME, object.getClass().getName());
            object.storeInBundle(bundle);
            array.add(bundle.data);
        }
        data.set(key, array);
    }

    public static HeadlessBundle read(InputStream stream) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder all = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                all.append(line);
                line = reader.readLine();
            }

            JsonObject json = JsonObject.readFrom(all.toString());
            reader.close();

            return new HeadlessBundle(json);
        } catch (Exception e) {
            return null;
        }
    }

    public static HeadlessBundle read(byte[] bytes) {
        JsonObject json = JsonObject.readFrom(new String(bytes));
        return new HeadlessBundle(json);
    }

    public static boolean write(HeadlessBundle bundle, OutputStream stream) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
            writer.write(bundle.data.toString());
            writer.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void addAlias(Class<?> cl, String alias) {
        aliases.put(alias, cl.getName());
    }
}
