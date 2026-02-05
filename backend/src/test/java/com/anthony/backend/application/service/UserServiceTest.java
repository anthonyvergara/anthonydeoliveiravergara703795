package com.anthony.backend.application.service;

import com.anthony.backend.domain.model.User;
import com.anthony.backend.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserService - Testes Unitários")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;
    private User adminUser;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .username("user1")
                .password("encodedPassword1")
                .role(User.Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user2 = User.builder()
                .id(2L)
                .username("user2")
                .password("encodedPassword2")
                .role(User.Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        adminUser = User.builder()
                .id(3L)
                .username("admin")
                .password("encodedPasswordAdmin")
                .role(User.Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve buscar todos os usuários com sucesso")
    void shouldGetAllUsersSuccessfully() {
        List<User> users = Arrays.asList(user1, user2, adminUser);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(user1, user2, adminUser);

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void shouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsers();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void shouldGetUserByIdSuccessfully() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        User result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("user1");
        assertThat(result.getRole()).isEqualTo(User.Role.USER);

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente por ID")
    void shouldThrowExceptionWhenGettingNonExistentUserById() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Deve buscar usuário admin por ID com sucesso")
    void shouldGetAdminUserByIdSuccessfully() {
        Long adminId = 3L;

        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));

        User result = userService.getUserById(adminId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(adminId);
        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getRole()).isEqualTo(User.Role.ADMIN);

        verify(userRepository).findById(adminId);
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Deve retornar usuários com diferentes roles")
    void shouldReturnUsersWithDifferentRoles() {
        List<User> users = Arrays.asList(user1, adminUser);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRole()).isEqualTo(User.Role.USER);
        assertThat(result.get(1).getRole()).isEqualTo(User.Role.ADMIN);
    }

    @Test
    @DisplayName("Deve buscar usuário mantendo todos os atributos")
    void shouldGetUserKeepingAllAttributes() {
        Long userId = 1L;
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30);

        user1.setCreatedAt(createdAt);
        user1.setUpdatedAt(updatedAt);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        User result = userService.getUserById(userId);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("user1");
        assertThat(result.getPassword()).isEqualTo("encodedPassword1");
        assertThat(result.getRole()).isEqualTo(User.Role.USER);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("Deve buscar todos os usuários preservando a ordem")
    void shouldGetAllUsersPreservingOrder() {
        List<User> users = Arrays.asList(adminUser, user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).containsExactly(adminUser, user1, user2);
    }

    @Test
    @DisplayName("Deve deletar múltiplos usuários sequencialmente")
    void shouldDeleteMultipleUsersSequentially() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        doNothing().when(userRepository).deleteById(userId1);
        doNothing().when(userRepository).deleteById(userId2);

        userService.deleteUser(userId1);
        userService.deleteUser(userId2);

        verify(userRepository).deleteById(userId1);
        verify(userRepository).deleteById(userId2);
        verify(userRepository, times(2)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve retornar apenas um usuário quando existe apenas um")
    void shouldReturnOnlyOneUserWhenOnlyOneExists() {
        List<User> users = Collections.singletonList(user1);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(user1);
    }

    @Test
    @DisplayName("Deve buscar usuário com ID válido diferentes")
    void shouldGetUsersWithDifferentValidIds() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        User result1 = userService.getUserById(1L);
        User result2 = userService.getUserById(2L);

        assertThat(result1.getId()).isEqualTo(1L);
        assertThat(result2.getId()).isEqualTo(2L);
        assertThat(result1).isNotEqualTo(result2);

        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
    }

}

