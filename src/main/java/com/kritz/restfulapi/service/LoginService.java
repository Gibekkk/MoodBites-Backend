package com.kritz.restfulapi.service;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kritz.restfulapi.model.Login;
import com.kritz.restfulapi.model.Toko;
import com.kritz.restfulapi.model.enums.Level;
import com.kritz.restfulapi.model.Session;
import com.kritz.restfulapi.repository.LoginRepository;
import com.kritz.restfulapi.repository.SessionRepository;
import com.kritz.restfulapi.repository.TokoRepository;
import com.kritz.restfulapi.util.PasswordHasherMatcher;

import jakarta.transaction.Transactional;

@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TokoRepository tokoRepository;

    @Autowired
    private PasswordHasherMatcher passwordMaker;

    @Transactional
    public void deleteSession(Session session) {
        sessionRepository.delete(session);
    }

    public Optional<Session> authenticateUser(String email, String password, String fcmToken) {
        Optional<Login> loginOpt = loginRepository.findByEmail(email);
        if (loginOpt.isPresent()) {
            Login login = loginOpt.get();
            if (login.getVerifiedAt() != null) {
                if (passwordMaker.matchPassword(password, login.getPassword())) {
                    Session session = regenerateSessionToken(login, fcmToken);
                    return Optional.of(session);
                }
            }
        }
        return Optional.empty();
    }

    public boolean isEmailAvailable(String email) {
        Optional<Login> loginOpt = loginRepository.findByEmail(email);
        // return loginOpt.isEmpty();

        // Negate the result to indicate availability (for debugging purposes)
        if(loginOpt.isEmpty())
            return true;
        
        loginRepository.delete(loginOpt.get());
        return true;
    }

    public Optional<Login> findLoginByEmail(String email) {
        return loginRepository.findByEmail(email);
    }

    public Optional<Login> findLoginById(String loginId) {
        return loginRepository.findById(loginId);
    }

    public Session regenerateSessionToken(Login login, String fcmToken) {
        // Optional<Session> conflictSessionOpt = sessionRepository.findByFcmToken(fcmToken);
        // if(conflictSessionOpt.isPresent()) {
        //     Session conflictSession = conflictSessionOpt.get();
        //     deleteSession(conflictSession);
        // }
        Session session = new Session();
        session.setIdLogin(login);
        session.setToken(UUID.randomUUID().toString());
        session.setFcmToken(fcmToken);
        session.setCreatedAt(LocalDateTime.now());
        session.setEditedAt(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    public Toko RegisterToko(String email, String password, String fcmToken, String namaToko, String deskripsi) {
        Login newLogin = new Login();
        newLogin.setEmail(email);
        newLogin.setPassword(passwordMaker.hashPassword(password));
        newLogin.setCreatedAt(LocalDateTime.now());
        newLogin.setEditedAt(LocalDateTime.now());
        newLogin.setLevel(Level.TOKO);
        newLogin = loginRepository.save(newLogin);

        Toko newToko = new Toko();
        newToko.setIdLogin(newLogin);
        newToko.setNama(namaToko);
        newToko.setDeskripsi(deskripsi);
        newToko.setCreatedAt(LocalDateTime.now());
        newToko.setEditedAt(LocalDateTime.now());
        newLogin.setIdToko(newToko);

        newToko = tokoRepository.save(newToko);
        regenerateSessionToken(newLogin, fcmToken);
        return newToko;
    }

    public Session verifyUser(Login login) {
        login.setVerifiedAt(LocalDateTime.now());
        loginRepository.save(login);
        return login.getListSession().stream().findFirst().orElse(null);
    }

    public Toko editProfilToko(Toko toko, String nama, String deskripsi) {
        toko.setNama(nama);
        toko.setDeskripsi(deskripsi);
        toko.setEditedAt(LocalDateTime.now());
        return tokoRepository.save(toko);
    }

    public Optional<Session> findSessionBySessionToken(String sessionToken) {
        Optional<Session> sessionOpt = sessionRepository.findByToken(sessionToken);
        return sessionOpt;
    }

}
