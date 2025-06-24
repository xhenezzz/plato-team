import aidyn.kelbetov.UserServiceApplication;
import aidyn.kelbetov.dto.RegisterDto;
import aidyn.kelbetov.entity.User;
import aidyn.kelbetov.repo.UserRepository;
import aidyn.kelbetov.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = UserServiceApplication.class
)
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=1025",
        "app.frontend.url=http://localhost:3000"
})
@Transactional
class UserRegistrationIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setup() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessagePreparator.class));
    }

    @Test
    void fullRegistrationFlow_Success() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("integration@test.com");
        registerDto.setPassword("password123");
        registerDto.setName("Integration");
        registerDto.setSurname("Test");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.emailConfirmed").value(false));

        User savedUser = userRepository.findByEmail("integration@test.com").orElse(null);
        assertNotNull(savedUser);
        assertEquals("Integration", savedUser.getName());
        assertEquals("Test", savedUser.getSurname());
        assertFalse(savedUser.isEmailConfirmed());
        assertNotNull(savedUser.getConfirmationToken());
        assertNotNull(savedUser.getTokenExpiry());
        assertTrue(savedUser.getTokenExpiry().isAfter(LocalDateTime.now()));

        verify(mailSender).send(any(MimeMessage.class)); // ✅ вот тут исправлено
    }


    @Test
    void confirmEmail_ValidToken_Success() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("confirm@test.com");
        registerDto.setPassword("password123");
        registerDto.setName("Confirm");
        registerDto.setSurname("Test");

        userService.registerUser(registerDto);
        User user = userRepository.findByEmail("confirm@test.com").orElseThrow();
        String token = user.getConfirmationToken();

        mockMvc.perform(get("/api/users/confirm-email") // ✅ заменили POST на GET
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Email успешно подтвержден!"));

        User confirmedUser = userRepository.findByEmail("confirm@test.com").orElseThrow();
        assertTrue(confirmedUser.isEmailConfirmed());
        assertNull(confirmedUser.getConfirmationToken());
        assertNull(confirmedUser.getTokenExpiry());
    }

    @Test
    void resendConfirmationEmail_Success() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("resend@test.com");
        registerDto.setPassword("password123");
        registerDto.setName("Resend");
        registerDto.setSurname("Test");

        userService.registerUser(registerDto);

        mockMvc.perform(post("/api/users/resend-confirmation")
                        .param("email", "resend@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Письмо с подтверждением отправлено повторно")); // ✅ исправлен текст

        User user = userRepository.findByEmail("resend@test.com").orElseThrow();
        assertNotNull(user.getConfirmationToken());
        assertNotNull(user.getTokenExpiry());
    }
}
