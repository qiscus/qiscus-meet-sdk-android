package com.qiscus.meet;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.List;
import java.util.Map;

/**
 * Created on : 17/03/20
 * Author     : arioki
 * Name       : Yoga Setiawan
 * GitHub     : https://github.com/arioki
 */
class Utils {
    public WritableMap convertHashMapToMap(Map<String, Object> hashMap) {
        WritableMap writableMap = Arguments.createMap();

        for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                writableMap.putNull(key);
            } else if (value instanceof Boolean) {
                writableMap.putBoolean(key, (Boolean) value);
            } else if (value instanceof Double) {
                writableMap.putDouble(key, (Double) value);
            } else if (value instanceof Integer) {
                writableMap.putInt(key, (Integer) value);
            } else if (value instanceof Long) {
                writableMap.putDouble(key, (Long) value);
            } else if (value instanceof String) {
                writableMap.putString(key, (String) value);
            } else if (value instanceof Map) {
                writableMap.putMap(key, convertHashMapToMap((Map) value));
            } else if (value instanceof List) {
                writableMap.putArray(key, convertArrayToArrayList((List) value));
            }
        }

        return writableMap;
    }

    private WritableArray convertArrayToArrayList(List list) {
        WritableArray writableArray = Arguments.createArray();

        if (list.size() < 1) {
            return writableArray;
        }

        Object firstObject = list.get(0);
        if (firstObject == null) {
            for (int i = 0; i < list.size(); i++) {
                writableArray.pushNull();
            }
        } else if (firstObject instanceof Boolean) {
            for (Object object : list) {
                writableArray.pushBoolean((boolean) object);
            }
        } else if (firstObject instanceof Double) {
            for (Object object : list) {
                writableArray.pushDouble((double) object);
            }
        } else if (firstObject instanceof Integer) {
            for (Object object : list) {
                writableArray.pushInt((int) object);
            }
        } else if (firstObject instanceof Long) {
            for (Object object : list) {
                writableArray.pushDouble((long) object);
            }
        } else if (firstObject instanceof String) {
            for (Object object : list) {
                writableArray.pushString((String) object);
            }
        } else if (firstObject instanceof Map) {
            for (Object object : list) {
                writableArray.pushMap(convertHashMapToMap((Map) object));
            }
        } else if (firstObject instanceof List) {
            for (Object object : list) {
                writableArray.pushArray(convertArrayToArrayList((List) object));
            }
        }

        return writableArray;
    }
}
