package com.gitee.starblues.utils;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * map 值获取者工具类
 *
 * @author starBlues
 * @version 3.0.1
 */
public class MapValueGetter {

    private final Map<String, Object> map;

    public MapValueGetter(Map<String, Object> map) {
        if(map == null){
            this.map = Collections.emptyMap();
        } else {
            this.map = map;
        }
    }

    /**
     * 获取object
     * @param key map key
     * @return value
     */
    public Object getObject(String key) {
        return map.get(key);
    }


    /**
     * 获取 String 类型值
     * @param key map key
     * @return String value
     */
    public String getString(String key) {
        return getValue(key, String::valueOf);
    }

    /**
     * 获取 Integer 类型值
     * @param key map key
     * @return Integer value
     */
    public Integer getInteger(String key) {
        return getValue(key, value -> {
            if(value instanceof Integer){
                return (Integer) value;
            }
            return Integer.parseInt(String.valueOf(value));
        });
    }

    /**
     * 获取 Long 类型值
     * @param key map key
     * @return Long value
     */
    public Long getLong(String key) {
        return getValue(key, value -> {
            if(value instanceof Long){
                return (Long) value;
            }
            return Long.parseLong(String.valueOf(value));
        });
    }

    /**
     * 获取 Double 类型值
     * @param key map key
     * @return Double value
     */
    public Double getDouble(String key) {
        return getValue(key, value -> {
            if(value instanceof Double){
                return (Double) value;
            }
            return Double.parseDouble(String.valueOf(value));
        });
    }

    /**
     * 获取 Float 类型值
     * @param key map key
     * @return Float value
     */
    public Float getFloat(String key) {
        return getValue(key, value -> {
            if(value instanceof Float){
                return (Float) value;
            }
            return Float.parseFloat(String.valueOf(value));
        });
    }

    /**
     * 获取 Boolean 类型值
     * @param key map key
     * @return Boolean value
     */
    public Boolean getBoolean(String key) {
        return getValue(key, value -> {
            if(value instanceof Boolean){
                return (Boolean) value;
            }
            return Boolean.parseBoolean(String.valueOf(value));
        });
    }

    /**
     * 获取值并根据自定义实现转换类型
     * @param key map key
     * @param function 自定义类型转换
     * @param <T> 转换后的类型泛型
     * @return 转换后的类型值
     */
    private <T> T getValue(String key, Function<Object, T> function){
        Object value = getObject(key);
        if(value == null){
            return null;
        }
        return function.apply(value);
    }


}
