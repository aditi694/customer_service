package com.bank.customer_service.security;

import com.bank.customer_service.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        customerId = UUID.randomUUID();
    }

    @Test
    void generateToken_success() {

        String token = jwtUtil.generate(customerId, "ADMIN");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_rolePrefixRemoved() {
        String token = jwtUtil.generate(customerId, "ROLE_ADMIN");
        Claims claims = jwtUtil.parse(token);
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    void generateToken_roleUppercase() {
        String token = jwtUtil.generate(customerId, "admin");
        Claims claims = jwtUtil.parse(token);
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    void generateToken_customerIdIsdCorrect() {
        String token = jwtUtil.generate(customerId, "ADMIN");
        Claims claims = jwtUtil.parse(token);
        assertEquals(customerId.toString(),
                claims.get("customerId"));
    }
    @Test
    void generateToken_expirationIsSet(){
        String token = jwtUtil.generate(customerId, "ADMIN");
        Claims claims = jwtUtil.parse(token);
        Date issue = claims.getIssuedAt();
        Date expired = claims.getExpiration();

        assertNotNull(issue);
        assertNotNull(expired);
        assertTrue(expired.after(issue));
    }
    @Test
    void parseToken_success() {
        String token = jwtUtil.generate(customerId, "ADMIN");
        Claims claims = jwtUtil.parse(token);

        assertNotNull(claims);
        assertNotNull(claims.get("customerId"));
        assertNotNull(claims.get("role"));
    }
    @Test
    void parseToken_invalidTokenException() {
        String invalidToken = "this.is.not.a.jwt";
        assertThrows(JwtException.class,
                () -> jwtUtil.parse(invalidToken));
    }
}



