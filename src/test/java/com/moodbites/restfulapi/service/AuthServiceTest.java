package com.moodbites.restfulapi.service;

import com.moodbites.restfulapi.model.Session;
import com.moodbites.restfulapi.model.User;
import com.moodbites.restfulapi.repository.SessionRepository;
import com.moodbites.restfulapi.repository.UserRepository;
import com.moodbites.restfulapi.util.PasswordHasherMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private PasswordHasherMatcher passwordMaker;

    @InjectMocks
    private AuthService authService;

    // ───────────────────────────────────────────
    // authenticateUser
    // ───────────────────────────────────────────

    @Test
    void authenticateUser_emailAndPasswordCorrect_returnSession() {
        User user = buildVerifiedUser();
        Session session = new Session();

        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(user));
        when(passwordMaker.matchPassword("pass123", "hashed")).thenReturn(true);
        when(sessionRepository.save(any())).thenReturn(session);

        Optional<Session> result = authService.authenticateUser("john@mail.com", "pass123", "fcm-token");

        assertThat(result).isPresent();
    }

    @Test
    void authenticateUser_wrongPassword_returnEmpty() {
        User user = buildVerifiedUser();

        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(user));
        when(passwordMaker.matchPassword("wrong", "hashed")).thenReturn(false);

        Optional<Session> result = authService.authenticateUser("john@mail.com", "wrong", "fcm-token");

        assertThat(result).isEmpty();
    }

    @Test
    void authenticateUser_notVerified_returnEmpty() {
        User user = new User();
        user.setEmail("john@mail.com");
        user.setPassword("hashed");
        user.setVerifiedAt(null);

        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(user));

        Optional<Session> result = authService.authenticateUser("john@mail.com", "pass123", "fcm-token");

        assertThat(result).isEmpty();
        verify(passwordMaker, never()).matchPassword(any(), any());
    }

    @Test
    void authenticateUser_emailNotFound_returnEmpty() {
        when(userRepository.findByEmail("ghost@mail.com")).thenReturn(Optional.empty());

        Optional<Session> result = authService.authenticateUser("ghost@mail.com", "pass123", "fcm-token");

        assertThat(result).isEmpty();
        verify(passwordMaker, never()).matchPassword(any(), any());
    }

    // ───────────────────────────────────────────
    // isEmailAvailable
    // ───────────────────────────────────────────

    @Test
    void isEmailAvailable_emailNotExist_returnTrue() {
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());

        boolean result = authService.isEmailAvailable("new@mail.com");

        assertThat(result).isTrue();
        verify(userRepository, never()).delete(any());
    }

    @Test
    void isEmailAvailable_emailExist_deleteAndReturnTrue() {
        User existing = buildVerifiedUser();
        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(existing));

        boolean result = authService.isEmailAvailable("john@mail.com");

        // NOTE: ini bug di service — email ada malah didelete
        // test ini dokumentasi behavior saat ini, bukan behavior ideal
        assertThat(result).isTrue();
        verify(userRepository).delete(existing);
    }

    // ───────────────────────────────────────────
    // registerToko
    // ───────────────────────────────────────────

    @Test
    void registerToko_validData_returnUser() {
        User savedUser = new User();
        savedUser.setEmail("john@mail.com");
        savedUser.setName("John");

        when(passwordMaker.hashPassword("pass123")).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(sessionRepository.save(any())).thenReturn(new Session());

        User result = authService.registerUser("john@mail.com", "pass123", "fcm-token", "John");

        assertThat(result.getEmail()).isEqualTo("john@mail.com");
        assertThat(result.getName()).isEqualTo("John");
        verify(userRepository).save(any(User.class));
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void registerToko_passwordHashed_notStoredAsPlaintext() {
        User savedUser = new User();
        savedUser.setPassword("hashed");

        when(passwordMaker.hashPassword("pass123")).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(sessionRepository.save(any())).thenReturn(new Session());

        authService.registerUser("john@mail.com", "pass123", "fcm-token", "John");

        verify(passwordMaker).hashPassword("pass123");
        verify(userRepository).save(argThat(user ->
            !"pass123".equals(user.getPassword())
        ));
    }

    @Test
    void registerToko_sessionCreated_afterUserSaved() {
        User savedUser = new User();
        savedUser.setEmail("john@mail.com");

        when(passwordMaker.hashPassword(any())).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(sessionRepository.save(any())).thenReturn(new Session());

        authService.registerUser("john@mail.com", "pass123", "fcm-token", "John");

        // pastikan urutan: save user DULU, baru save session
        var order = inOrder(userRepository, sessionRepository);
        order.verify(userRepository).save(any());
        order.verify(sessionRepository).save(any());
    }

    // ───────────────────────────────────────────
    // verifyUser
    // ───────────────────────────────────────────

    @Test
    void verifyUser_userHasSession_returnSession() {
        Session session = new Session();
        User user = buildVerifiedUser();
        user.setSessions(Set.of(session));

        when(userRepository.save(any())).thenReturn(user);

        Session result = authService.verifyUser(user);

        assertThat(result).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    void verifyUser_userHasNoSession_returnNull() {
        User user = new User();
        user.setSessions(Set.of());

        when(userRepository.save(any())).thenReturn(user);

        Session result = authService.verifyUser(user);

        assertThat(result).isNull();
    }

    @Test
    void verifyUser_setsVerifiedAt_notNull() {
        User user = new User();
        user.setSessions(Set.of());

        when(userRepository.save(any())).thenReturn(user);

        authService.verifyUser(user);

        verify(userRepository).save(argThat(u ->
            u.getVerifiedAt() != null
        ));
    }

    // ───────────────────────────────────────────
    // findSessionBySessionToken
    // ───────────────────────────────────────────

    @Test
    void findSessionBySessionToken_validToken_returnSession() {
        Session session = new Session();
        when(sessionRepository.findById("valid-token")).thenReturn(Optional.of(session));

        Optional<Session> result = authService.findSessionBySessionToken("valid-token");

        assertThat(result).isPresent();
    }

    @Test
    void findSessionBySessionToken_invalidToken_returnEmpty() {
        when(sessionRepository.findById("invalid-token")).thenReturn(Optional.empty());

        Optional<Session> result = authService.findSessionBySessionToken("invalid-token");

        assertThat(result).isEmpty();
    }

    // ───────────────────────────────────────────
    // deleteSession
    // ───────────────────────────────────────────

    @Test
    void deleteSession_validSession_deletedFromRepository() {
        Session session = new Session();

        authService.deleteSession(session);

        verify(sessionRepository).delete(session);
    }

    @Test
    void deleteSession_calledOnce_notCalledTwice() {
        Session session = new Session();

        authService.deleteSession(session);

        verify(sessionRepository, times(1)).delete(session);
    }

    // ───────────────────────────────────────────
    // findLoginByEmail & findLoginById
    // ───────────────────────────────────────────

    @Test
    void findLoginByEmail_emailExist_returnUser() {
        User user = buildVerifiedUser();
        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(user));

        Optional<User> result = authService.findLoginByEmail("john@mail.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john@mail.com");
    }

    @Test
    void findLoginByEmail_emailNotExist_returnEmpty() {
        when(userRepository.findByEmail("ghost@mail.com")).thenReturn(Optional.empty());

        Optional<User> result = authService.findLoginByEmail("ghost@mail.com");

        assertThat(result).isEmpty();
    }

    @Test
    void findLoginById_idExist_returnUser() {
        User user = buildVerifiedUser();
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));

        Optional<User> result = authService.findLoginById("user-123");

        assertThat(result).isPresent();
    }

    @Test
    void findLoginById_idNotExist_returnEmpty() {
        when(userRepository.findById("invalid-id")).thenReturn(Optional.empty());

        Optional<User> result = authService.findLoginById("invalid-id");

        assertThat(result).isEmpty();
    }

    // ───────────────────────────────────────────
    // Helper
    // ───────────────────────────────────────────

    private User buildVerifiedUser() {
        User user = new User();
        user.setEmail("john@mail.com");
        user.setPassword("hashed");
        user.setVerifiedAt(LocalDateTime.now());
        user.setSessions(Set.of());
        return user;
    }
}