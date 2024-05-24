package id.ac.ui.cs.advprog.afk3.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class JwtValidatorTest {
    private String secret;

    @InjectMocks
    private JwtValidator validator;

    private String generateToken(String username) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + 1000);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt( new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, "secret")
                .compact();
        System.out.println("New token :");
        System.out.println(token);
        return token;
    }

    @Test
    public void getUsernameTest(){
        String username ="budi";

        String token = generateToken(username);
        ReflectionTestUtils.setField(validator, "secret", "secret");
        String usernameResult = validator.getUsernameFromJWT(token);
        assertEquals(username, usernameResult);

        token = "Bearer "+generateToken(username);
        ReflectionTestUtils.setField(validator, "secret", "secret");
        usernameResult = validator.getUsernameFromJWT(token);

        assertEquals(username, usernameResult);
    }
}
