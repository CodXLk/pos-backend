package com.codX.pos.config;
import com.codX.pos.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret-key}")
    private String secretKey;

    public String generateToken(UserEntity userEntity){
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", userEntity.getId());
        extraClaims.put("role", userEntity.getRole());
        extraClaims.put("email", userEntity.getEmail());
        return generateToken(extraClaims,userEntity);
    }
    public String generateToken(
            Map<String, Object> extraClaims,
            UserEntity userEntity
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userEntity.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Key getSignInKey() {
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
