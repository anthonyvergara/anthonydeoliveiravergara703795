package com.anthony.backend.application.service;

import com.anthony.backend.controller.dto.response.AuthResponse;
import com.anthony.backend.controller.dto.request.LoginRequest;
import com.anthony.backend.controller.dto.request.RegisterRequest;
import com.anthony.backend.domain.model.User;
import com.anthony.backend.domain.repository.UserRepository;
import com.anthony.backend.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuthService - Testes Unitários")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .role(User.Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Deve registrar novo usuário com sucesso")
    void shouldRegisterNewUserSuccessfully() {
        String accessToken = "access.token.here";
        String refreshToken = "refresh.token.here";

        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });
        when(jwtService.generateAccessToken(any(User.class))).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(refreshToken);

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(response.getUsername()).isEqualTo(registerRequest.getUsername());
        assertThat(response.getRole()).isEqualTo("USER");

        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(argThat(u ->
            u.getUsername().equals(registerRequest.getUsername()) &&
            u.getPassword().equals("encodedPassword") &&
            u.getRole() == User.Role.USER
        ));
        verify(jwtService).generateAccessToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar usuário com username existente")
    void shouldThrowExceptionWhenRegisteringUserWithExistingUsername() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username already exists");

        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void shouldLoginSuccessfully() {
        String accessToken = "access.token.here";
        String refreshToken = "refresh.token.here";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getRole()).isEqualTo("USER");

        verify(authenticationManager).authenticate(argThat(auth ->
            auth.getPrincipal().equals(loginRequest.getUsername()) &&
            auth.getCredentials().equals(loginRequest.getPassword())
        ));
        verify(userRepository).findByUsername(loginRequest.getUsername());
        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    @DisplayName("Deve lançar exceção ao fazer login com credenciais inválidas")
    void shouldThrowExceptionWhenLoginWithInvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Bad credentials");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao fazer login quando usuário não é encontrado")
    void shouldThrowExceptionWhenLoginAndUserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername(loginRequest.getUsername());
        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    @DisplayName("Deve renovar access token com refresh token válido")
    void shouldRefreshAccessTokenWithValidRefreshToken() {
        String refreshToken = "valid.refresh.token";
        String newAccessToken = "new.access.token";
        String username = "testuser";

        when(jwtService.getTokenType(refreshToken)).thenReturn("REFRESH");
        when(jwtService.extractUsername(refreshToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn(newAccessToken);

        AuthResponse response = authService.refresh(refreshToken);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getRole()).isEqualTo("USER");

        verify(jwtService).getTokenType(refreshToken);
        verify(jwtService).extractUsername(refreshToken);
        verify(userRepository).findByUsername(username);
        verify(jwtService).isTokenValid(refreshToken, user);
        verify(jwtService).generateAccessToken(user);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar renovar com token que não é refresh token")
    void shouldThrowExceptionWhenRefreshingWithNonRefreshToken() {
        String accessToken = "access.token.here";

        when(jwtService.getTokenType(accessToken)).thenReturn("ACCESS");

        assertThatThrownBy(() -> authService.refresh(accessToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Token inválido: não é um refresh token");

        verify(jwtService).getTokenType(accessToken);
        verify(jwtService, never()).extractUsername(anyString());
        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtService, never()).isTokenValid(anyString(), any());
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao renovar token quando usuário não é encontrado")
    void shouldThrowExceptionWhenRefreshingAndUserNotFound() {
        String refreshToken = "valid.refresh.token";
        String username = "nonexistent";

        when(jwtService.getTokenType(refreshToken)).thenReturn("REFRESH");
        when(jwtService.extractUsername(refreshToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(refreshToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(jwtService).getTokenType(refreshToken);
        verify(jwtService).extractUsername(refreshToken);
        verify(userRepository).findByUsername(username);
        verify(jwtService, never()).isTokenValid(anyString(), any());
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao renovar token quando refresh token é inválido")
    void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
        String refreshToken = "invalid.refresh.token";
        String username = "testuser";

        when(jwtService.getTokenType(refreshToken)).thenReturn("REFRESH");
        when(jwtService.extractUsername(refreshToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh(refreshToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Refresh token inválido ou expirado");

        verify(jwtService).getTokenType(refreshToken);
        verify(jwtService).extractUsername(refreshToken);
        verify(userRepository).findByUsername(username);
        verify(jwtService).isTokenValid(refreshToken, user);
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Deve criar usuário com role USER por padrão no registro")
    void shouldCreateUserWithUserRoleByDefaultOnRegister() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access.token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh.token");

        authService.register(registerRequest);

        verify(userRepository).save(argThat(u -> u.getRole() == User.Role.USER));
    }

    @Test
    @DisplayName("Deve codificar a senha do usuário ao registrar")
    void shouldEncodeUserPasswordWhenRegistering() {
        String rawPassword = "myPassword123";
        String encodedPassword = "encodedPassword123";
        registerRequest.setPassword(rawPassword);

        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access.token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh.token");

        authService.register(registerRequest);

        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(argThat(u -> u.getPassword().equals(encodedPassword)));
    }

    @Test
    @DisplayName("Deve retornar role ADMIN quando usuário é admin")
    void shouldReturnAdminRoleWhenUserIsAdmin() {
        String accessToken = "access.token.here";
        String refreshToken = "refresh.token.here";
        User adminUser = User.builder()
                .id(2L)
                .username("adminuser")
                .password("encodedPassword")
                .role(User.Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        loginRequest.setUsername("adminuser");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("adminuser")).thenReturn(Optional.of(adminUser));
        when(jwtService.generateAccessToken(adminUser)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(adminUser)).thenReturn(refreshToken);

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Deve manter o mesmo refresh token ao renovar access token")
    void shouldKeepSameRefreshTokenWhenRefreshing() {
        String originalRefreshToken = "original.refresh.token";
        String newAccessToken = "new.access.token";

        when(jwtService.getTokenType(originalRefreshToken)).thenReturn("REFRESH");
        when(jwtService.extractUsername(originalRefreshToken)).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(originalRefreshToken, user)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn(newAccessToken);

        AuthResponse response = authService.refresh(originalRefreshToken);

        assertThat(response.getRefreshToken()).isEqualTo(originalRefreshToken);
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(response.getAccessToken()).isNotEqualTo(originalRefreshToken);
    }

    @Test
    @DisplayName("Deve definir timestamps ao criar novo usuário")
    void shouldSetTimestampsWhenCreatingNewUser() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access.token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh.token");

        authService.register(registerRequest);

        verify(userRepository).save(argThat(u ->
            u.getCreatedAt() != null && u.getUpdatedAt() != null
        ));
    }
}

