package com.example.eduhub.network.sync;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SyncEngineTest {

    @Test
    public void toJson_returnsJsonString() {
        Map<String, Object> body = new HashMap<>();
        body.put("key", "value");
        body.put("number", 123);
        String json = SyncEngine.toJson(body);
        assertNotNull(json);
        assertTrue(json.contains("\"key\""));
        assertTrue(json.contains("\"value\""));
        assertTrue(json.contains("\"number\""));
        assertTrue(json.contains("123"));
    }

    @Test
    public void toJson_returnsEmptyObjectForNull() {
        String json = SyncEngine.toJson(null);
        assertEquals("{}", json);
    }

    @Test
    public void toJson_returnsEmptyObjectForEmptyMap() {
        String json = SyncEngine.toJson(new HashMap<>());
        assertEquals("{}", json);
    }

    @Test
    public void constants_areNotEmpty() {
        assertNotNull(SyncEngine.E_GRADE);
        assertNotNull(SyncEngine.OP_CREATE);
        assertFalse(SyncEngine.E_GRADE.isEmpty());
        assertFalse(SyncEngine.OP_CREATE.isEmpty());
    }
}
