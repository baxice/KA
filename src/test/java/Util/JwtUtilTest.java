package Util;

import com.example.knowledgeapplication.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private static final String SECRET = "your-32-char-long-secret-key-1234567890";
    private static final String TEST_EMAIL = "test@example.com";

    @Test
    void testGenerateAndValidate() {
        // 生成Token
        String token = JwtUtil.generateToken(TEST_EMAIL, SECRET, 3600000);
        assertNotNull(token);

        // 验证Token
        Claims claims = JwtUtil.validateToken(token, SECRET);
        assertNotNull(claims);
        assertEquals(TEST_EMAIL, claims.getSubject());
    }

    @Test
    void testExpiredToken() throws InterruptedException {
        // 生成100毫秒后过期的Token
        String token = JwtUtil.generateToken(TEST_EMAIL, SECRET, 100);
        Thread.sleep(200); // 确保过期
        assertNull(JwtUtil.validateToken(token, SECRET));
    }

    @Test
    void testInvalidSignature() {
        String token = JwtUtil.generateToken(TEST_EMAIL, SECRET, 3600000);
        assertFalse(JwtUtil.verifySignature(token, "wrong-secret-key"));
    }
}
