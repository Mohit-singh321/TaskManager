package com.zosh.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.*;

public class JwtProvider {

    static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECERET_KEY.getBytes());

    public static String generatedToken(Authentication auth){
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        String roles = populateAuthorities(authorities);
        String jwt = Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + 86400000))
                .claim("email" , auth.getName())
                .claim("authorities" , roles)
                .signWith(key)
                .compact();
        return jwt;
    }
    public static String getEmailFromJwtToken(String jwt){
        jwt = jwt.substring(7);

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build().
                parseSignedClaims(jwt)
                .getPayload();

        String email = String.valueOf(claims.get("email"));
        return email;
    }

    public static String populateAuthorities(Collection<? extends GrantedAuthority> collection){
        Set<String> auths = new HashSet<>();

        for(GrantedAuthority authority : collection){
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}
