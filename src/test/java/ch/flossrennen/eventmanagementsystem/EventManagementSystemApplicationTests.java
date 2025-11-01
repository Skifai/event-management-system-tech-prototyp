package ch.flossrennen.eventmanagementsystem;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventManagementSystemApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void failingTest() {
        Assertions.fail("This should fail");
    }

    @Test
    void successfulTest() {
        Assertions.assertThat(true).isTrue();
    }

}
