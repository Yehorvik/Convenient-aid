package ua.edu.sumdu.volonteerProject.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    //generate token

    public String generateToken(Authentication authentication){
        JwtUserDetails user = (JwtUserDetails) authentication.getPrincipal();
        Date date = new Date(System.currentTimeMillis());
        Date expireDate = new Date(date.getTime()+ SecurityConstraints.TOKEN_EXPIRATION_TIME);
        String userId = user.getId().toString();
        Map<String, Object> claims = new HashMap<>();
        claims.put("id",  userId);
        claims.put("username", user.getUsername());
        claims.put("firstname", user.getFirstName());
        claims.put("secondname", user.getSecondName());
        claims.put("authorities", user.getAuthorities());
        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims).setIssuedAt(date)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstraints.SECRET)
                .compact();
    }

    //validate

    //getuserid
}
