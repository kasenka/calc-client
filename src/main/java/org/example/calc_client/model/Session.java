package org.example.calc_client.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Base64;
import java.util.Map;

public class Session {
    private static String jwt;
    private static String userRole;

    public static void setJwt(String token) {
        jwt = token;
    }

    public static String getJwt() {
        return jwt;
    }

    public static HttpHeaders getJwtHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);

        return headers;
    }

    public static void clear() {
        jwt = null;
    }

    public static Map<String, Object> decodeJwtPayload(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(payloadJson, Map.class);
        return decodeJwtPayload(jwt);
    }

    public static void setRole(String role) {
        userRole = role;
    }

    public static String getRole() {
        return userRole;
    }
}

