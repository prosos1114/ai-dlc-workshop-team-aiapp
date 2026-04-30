package com.tableorder.admin.auth;

import com.tableorder.admin.auth.dto.AdminResponse;
import com.tableorder.admin.auth.dto.TokenResponse;
import com.tableorder.core.exception.AccountLockedException;
import com.tableorder.core.exception.DuplicateResourceException;
import com.tableorder.core.exception.InvalidCredentialsException;
import com.tableorder.core.exception.NotFoundException;
import com.tableorder.core.security.JwtTokenProvider;
import com.tableorder.domain.admin.Admin;
import com.tableorder.domain.admin.AdminRepository;
import com.tableorder.domain.store.Store;
import com.tableorder.domain.store.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdminAuthService {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthService.class);
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    private final StoreRepository storeRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final long adminExpiration;

    public AdminAuthService(StoreRepository storeRepository,
                            AdminRepository adminRepository,
                            PasswordEncoder passwordEncoder,
                            JwtTokenProvider jwtTokenProvider,
                            @Value("${jwt.admin-expiration}") long adminExpiration) {
        this.storeRepository = storeRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.adminExpiration = adminExpiration;
    }

    @Transactional
    public TokenResponse login(String storeCode, String username, String password) {
        Store store = storeRepository.findByCode(storeCode)
                .orElseThrow(InvalidCredentialsException::new);

        Admin admin = adminRepository.findByStoreIdAndUsername(store.getId(), username)
                .orElseThrow(InvalidCredentialsException::new);

        if (admin.isLocked()) {
            log.warn("Login attempt on locked account: store={}, username={}", storeCode, username);
            throw new AccountLockedException();
        }

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            admin.incrementLoginAttempts();
            if (admin.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                admin.lock(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
                log.warn("Account locked after {} failed attempts: store={}, username={}",
                        MAX_LOGIN_ATTEMPTS, storeCode, username);
            }
            adminRepository.save(admin);
            throw new InvalidCredentialsException();
        }

        admin.resetLoginAttempts();
        adminRepository.save(admin);

        String token = jwtTokenProvider.createAdminToken(admin.getId(), store.getId());
        log.info("Admin login successful: store={}, username={}", storeCode, username);
        return new TokenResponse(token, adminExpiration, store.getId());
    }

    @Transactional
    public AdminResponse register(String storeCode, String username, String password) {
        Store store = storeRepository.findByCode(storeCode)
                .orElseThrow(() -> new NotFoundException("Store", storeCode));

        if (adminRepository.existsByStoreIdAndUsername(store.getId(), username)) {
            throw new DuplicateResourceException("Admin", username);
        }

        Admin admin = Admin.builder()
                .storeId(store.getId())
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();

        adminRepository.save(admin);
        log.info("Admin registered: store={}, username={}", storeCode, username);
        return AdminResponse.from(admin);
    }
}
