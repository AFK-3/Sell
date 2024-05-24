package id.ac.ui.cs.advprog.afk3.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtValidator {

    @Value("${app.secret}")
    private String secret;

    public String getUsernameFromJWT(String token){
        if(token.startsWith("Bearer ")) {
            token= token.substring(7, token.length());
        }
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
