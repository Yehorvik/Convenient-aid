package ua.edu.sumdu.volonteerProject.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
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

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(SecurityConstraints.SECRET).parseClaimsJws(token);
            return true;
        }catch (SignatureException exception)
        {
            log.error("signature exception in validation ", exception);
        }catch (MalformedJwtException exception){
            log.error("MalformedJwtException in validation ", exception);
        }catch (ExpiredJwtException exception){
            log.error("ExpiredJwtException in validation ", exception);
        }catch (UnsupportedJwtException exception){
            log.error("UnsupportedJwtException in validation ", exception);
        }catch (IllegalArgumentException exception){
            log.error("IllegalARGEX in validation ", exception);
        }
        return false;
    }
    //getuserid

    public UUID getUserIdFromJwt(String token){
        Claims claims = Jwts.parser().setSigningKey(SecurityConstraints.SECRET).parseClaimsJws(token).getBody();
        String id = (String)claims.get("id");
        return UUID.fromString(id);
    }
}
