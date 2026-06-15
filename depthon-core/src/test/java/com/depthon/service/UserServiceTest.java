package com.depthon.service;

import com.depthon.domain.Division;
import com.depthon.domain.Subdivision;
import com.depthon.dto.RegisterRequest;
import com.depthon.model.User;
import com.depthon.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_derivesDivisionFromSubdivision() {
        // ARRANGE: build a registration request with a subdivision
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@depthon.com");
        request.setUsername("tester");
        request.setFullName("Test Person");
        request.setPassword("password123");
        request.setSubdivision(Subdivision.SOFTWARE_DEVELOPER);

        // Tell the fakes how to behave:
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));

        // ACT: run the real method we're testing
        User result = userService.registerUser(request);

        // ASSERT: the division was derived correctly from the subdivision
        assertEquals(Subdivision.SOFTWARE_DEVELOPER, result.getSubdivision());
        assertEquals(Division.INFORMATION_TECHNOLOGY, result.getDivision());
    }
}