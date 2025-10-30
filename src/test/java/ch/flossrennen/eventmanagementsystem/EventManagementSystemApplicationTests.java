package ch.flossrennen.eventmanagementsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
class EventManagementSystemApplicationTests {

    @Test
    void contextLoads() {
    }

}
