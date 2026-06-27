package com.eduhab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.eduhab.rest.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String url(String path) {
        return "http://localhost:" + port + "/api" + path;
    }

    @Test
    void fullUserCrud_ShouldWork() throws Exception {
        ObjectNode userJson = objectMapper.createObjectNode();
        userJson.put("email", "integration@test.com");
        userJson.put("passwordHash", "testpass");
        userJson.put("firstName", "Integration");
        userJson.put("lastName", "Test");
        userJson.put("role", "STUDENT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(userJson.toString(), headers);

        ResponseEntity<UserDto> createResponse = restTemplate.exchange(
                url("/user"), HttpMethod.POST, requestEntity, UserDto.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertTrue(createResponse.getBody().getId() > 0);
        assertEquals("integration@test.com", createResponse.getBody().getEmail());

        int userId = createResponse.getBody().getId();

        ResponseEntity<UserDto> getResponse = restTemplate.getForEntity(
                url("/user/" + userId), UserDto.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("integration@test.com", getResponse.getBody().getEmail());

        ResponseEntity<UserDto[]> getAllResponse = restTemplate.getForEntity(
                url("/user"), UserDto[].class);

        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        List<UserDto> allUsers = Arrays.asList(getAllResponse.getBody());
        assertTrue(allUsers.size() >= 1);

        UserDto updateDto = createResponse.getBody();
        updateDto.setFirstName("Updated");
        HttpEntity<UserDto> updateEntity = new HttpEntity<>(updateDto);

        ResponseEntity<UserDto> updateResponse = restTemplate.exchange(
                url("/user/" + userId), HttpMethod.PUT, updateEntity, UserDto.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Updated", updateResponse.getBody().getFirstName());

        restTemplate.delete(url("/user/" + userId));

        ResponseEntity<UserDto> getAfterDelete = restTemplate.getForEntity(
                url("/user/" + userId), UserDto.class);

        assertEquals(HttpStatus.NOT_FOUND, getAfterDelete.getStatusCode());
    }
}
