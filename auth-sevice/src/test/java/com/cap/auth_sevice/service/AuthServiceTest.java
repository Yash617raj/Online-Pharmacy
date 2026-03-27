package com.cap.auth_sevice.service;

import com.cap.auth_sevice.dto.*;
import com.cap.auth_sevice.entity.Role;
import com.cap.auth_sevice.entity.User;
import com.cap.auth_sevice.exception.ApiException;
import com.cap.auth_sevice.repository.UserRepository;
import com.cap.auth_sevice.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository repository;
    @Mock private PasswordEncoder encoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private ModelMapper mapper;

    @InjectMocks private AuthService authService;

    private RegisterRequest registerReq() {
        RegisterRequest r = new RegisterRequest();
        r.setEmail("test@gmail.com");
        r.setPassword("1234");
        return r;
    }

    private AuthRequest loginReq() {
        AuthRequest r = new AuthRequest();
        r.setEmail("test@gmail.com");
        r.setPassword("1234");
        return r;
    }

    private User user() {
        User u = new User();
        u.setEmail("test@gmail.com");
        u.setPassword("encoded");
        u.setRole(Role.USER);
        return u;
    }

    @Test
    void register_Success() {
        RegisterRequest req = registerReq();
        User u = new User();

        when(repository.findByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(mapper.map(req, User.class)).thenReturn(u);
        when(encoder.encode(req.getPassword())).thenReturn("encoded");

        authService.register(req);

        assertEquals(Role.USER, u.getRole());
        verify(repository).save(u);
    }

    @Test
    void register_EmailAlreadyExists() {
        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(ApiException.class,
                () -> authService.register(registerReq()));
    }

    @Test
    void register_PasswordEncoded() {
        RegisterRequest req = registerReq();
        User u = new User();

        when(repository.findByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(mapper.map(req, User.class)).thenReturn(u);
        when(encoder.encode("1234")).thenReturn("encoded");

        authService.register(req);

        assertEquals("encoded", u.getPassword());
    }

    @Test
    void register_DefaultRoleAssigned() {
        RegisterRequest req = registerReq();
        User u = new User();

        when(repository.findByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(mapper.map(req, User.class)).thenReturn(u);
        when(encoder.encode(any())).thenReturn("encoded");

        authService.register(req);

        assertEquals(Role.USER, u.getRole());
    }

    @Test
    void register_SaveCalledOnce() {
        RegisterRequest req = registerReq();
        User u = new User();

        when(repository.findByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(mapper.map(req, User.class)).thenReturn(u);
        when(encoder.encode(any())).thenReturn("encoded");

        authService.register(req);

        verify(repository, times(1)).save(u);
    }

    @Test
    void register_MapperCalled() {
        RegisterRequest req = registerReq();

        when(repository.findByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(mapper.map(eq(req), eq(User.class))).thenReturn(new User());
        when(encoder.encode(any())).thenReturn("encoded");

        authService.register(req);

        verify(mapper).map(req, User.class);
    }

    @Test
    void register_EncoderCalled() {
        RegisterRequest req = registerReq();

        when(repository.findByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(mapper.map(req, User.class)).thenReturn(new User());
        when(encoder.encode(any())).thenReturn("encoded");

        authService.register(req);

        verify(encoder).encode("1234");
    }

    @Test
    void register_NoSaveOnDuplicate() {
        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(ApiException.class,
                () -> authService.register(registerReq()));

        verify(repository, never()).save(any());
    }

    @Test
    void login_Success() {
        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user()));
        when(encoder.matches("1234", "encoded")).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");

        AuthResponse res = authService.login(loginReq());

        assertEquals("token", res.getToken());
    }

    @Test
    void login_UserNotFound() {
        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> authService.login(loginReq()));
    }

    @Test
    void login_InvalidPassword() {
        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user()));
        when(encoder.matches(any(), any())).thenReturn(false);

        assertThrows(ApiException.class,
                () -> authService.login(loginReq()));
    }

    @Test
    void login_EncoderCalled() {
        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user()));
        when(encoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");

        authService.login(loginReq());

        verify(encoder).matches("1234", "encoded");
    }

    @Test
    void login_JwtCalled() {
        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user()));
        when(encoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");

        authService.login(loginReq());

        verify(jwtUtil).generateToken("test@gmail.com", Role.USER);
    }

    @Test
    void login_TokenNotGeneratedOnFailure() {
        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user()));
        when(encoder.matches(any(), any())).thenReturn(false);

        assertThrows(ApiException.class,
                () -> authService.login(loginReq()));

        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void login_ResponseNotNull() {
        when(repository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user()));
        when(encoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");

        AuthResponse res = authService.login(loginReq());

        assertNotNull(res);
    }
}