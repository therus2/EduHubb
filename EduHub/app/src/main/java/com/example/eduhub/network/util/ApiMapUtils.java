package com.example.eduhub.network.util;

import java.util.Map;

public final class ApiMapUtils {

    private ApiMapUtils() {
    }

    public static String safeStr(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object val = map.get(key);
        return fromObject(val);
    }

    public static String fromObject(Object val) {
        if (val == null) return null;
        if (val instanceof Number) {
            long longValue = ((Number) val).longValue();
            if (((Number) val).doubleValue() == longValue) {
                return String.valueOf(longValue);
            }
        }
        return normalizeId(String.valueOf(val));
    }

    
    public static String normalizeId(String value) {
        if (value == null || value.isEmpty()) return value;
        String trimmed = value.trim();
        if (trimmed.endsWith(".0")) {
            trimmed = trimmed.substring(0, trimmed.length() - 2);
        }
        try {
            long parsed = (long) Double.parseDouble(trimmed);
            return String.valueOf(parsed);
        } catch (NumberFormatException e) {
            return trimmed;
        }
    }

    public static Integer safeInt(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object val = map.get(key);
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).intValue();
        String normalized = normalizeId(String.valueOf(val));
        try {
            return Integer.parseInt(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
