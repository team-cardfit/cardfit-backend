package CardRecommendService;

import CardRecommendService.loginUtils.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JwtTest {

    @Autowired
    JwtProvider jwtProvider;

    @Test
    void test1() {
        Boolean validToken = jwtProvider.isValidToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIzZDc2NDkyYy03NjFhLTQyMTctODhkMy1jNDU2Y2VkMjU5MjkiLCJlbWFpbCI6IjEyM0BnbWFpbC5jb20iLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.w7vhFRV2zUoXasBsO6If87dcDSO__saLPy2I6LeNTuY");
        assertThat(validToken).isTrue();
    }
}
