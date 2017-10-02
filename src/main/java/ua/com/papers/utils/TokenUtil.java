package ua.com.papers.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Oleh on 11.06.2017.
 */
@Component
public class TokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    public SecureToken parseSecure(String token) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            SecureToken secureToken = new SecureToken();
            secureToken.setData((Map<String, Object>) body.get(SecureToken.DATA));
            return secureToken;
        } catch (JwtException e) {
            return null;
        } catch (ClassCastException e){
            return null;
        }
    }

    public String generateSecure(SecureToken token) {
        Claims claims = Jwts.claims().setSubject(SecureToken.SUBJECT);
        claims.put(SecureToken.DATA, token.getData());

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

}
