package org.example.calc_client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class Session {
    private static String jwt;

    public static void setJwt(String token) {
        jwt = token;
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
}

