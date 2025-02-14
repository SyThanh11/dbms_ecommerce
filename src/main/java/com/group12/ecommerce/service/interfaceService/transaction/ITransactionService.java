package com.group12.ecommerce.service.interfaceService.transaction;

import java.util.function.Supplier;

public interface ITransactionService {
    <T> T executeInTransaction(Supplier<T> action);
}
