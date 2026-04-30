package com.tableorder.customer.auth;

import com.tableorder.core.exception.InvalidCredentialsException;
import com.tableorder.core.exception.NotFoundException;
import com.tableorder.core.security.JwtTokenProvider;
import com.tableorder.customer.auth.dto.TableLoginRequest;
import com.tableorder.customer.auth.dto.TableLoginResponse;
import com.tableorder.domain.store.Store;
import com.tableorder.domain.store.StoreRepository;
import com.tableorder.domain.table.TableEntity;
import com.tableorder.domain.table.TableRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TableAuthService {

    private final StoreRepository storeRepository;
    private final TableRepository tableRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public TableAuthService(StoreRepository storeRepository,
                            TableRepository tableRepository,
                            JwtTokenProvider jwtTokenProvider,
                            PasswordEncoder passwordEncoder) {
        this.storeRepository = storeRepository;
        this.tableRepository = tableRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public TableLoginResponse login(TableLoginRequest request) {
        Store store = storeRepository.findByCode(request.storeCode())
                .orElseThrow(() -> new InvalidCredentialsException());

        TableEntity table = tableRepository.findByStoreIdAndTableNumber(store.getId(), request.tableNumber())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(request.password(), table.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtTokenProvider.createTableToken(table.getId(), store.getId());

        return new TableLoginResponse(token, store.getId(), table.getId(), table.getTableNumber());
    }
}
