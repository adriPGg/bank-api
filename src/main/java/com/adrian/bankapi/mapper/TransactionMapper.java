package com.adrian.bankapi.mapper;

import com.adrian.bankapi.dto.TransactionResponse;
import com.adrian.bankapi.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "fromAccount.id", target = "fromAccountId")
    @Mapping(source = "toAccount.id", target = "toAccountId")
    TransactionResponse toResponse(Transaction transaction);
}