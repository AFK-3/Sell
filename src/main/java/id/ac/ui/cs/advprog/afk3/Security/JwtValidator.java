package id.ac.ui.cs.advprog.afk3.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtValidator {
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey("secret")
                    .parseClaimsJws(token);
            System.out.println("validate token "+token);
            return true;
        } catch (Exception ex) {
            throw new RuntimeException("JWT was exprired or incorrect",ex.fillInStackTrace());
        }
    }
    public String getUsernameFromJWT(String token){
        if(token.startsWith("Bearer ")) {
            token= token.substring(7, token.length());
        }
        Claims claims = Jwts.parser()
                .setSigningKey("secret")
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
