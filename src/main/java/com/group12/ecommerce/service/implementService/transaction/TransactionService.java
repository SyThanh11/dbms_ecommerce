package com.group12.ecommerce.service.implementService.transaction;

import com.group12.ecommerce.service.interfaceService.transaction.ITransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionService implements ITransactionService {
    private final PlatformTransactionManager transactionManager;

    @Override
    public <T> T executeInTransaction(Supplier<T> action) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            T result = action.get();
            transactionManager.commit(status);
            return result;
        } catch (Exception e) {
            transactionManager.rollback(status);
            log.error("Transaction failed: {}", e.getMessage());
            throw new RuntimeException("Transaction failed", e);
        }
    }
}
