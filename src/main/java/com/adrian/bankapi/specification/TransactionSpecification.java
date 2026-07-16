package com.adrian.bankapi.specification;

import com.adrian.bankapi.entity.BankAccount;
import com.adrian.bankapi.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;
import com.adrian.bankapi.entity.TransactionType;

import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> belongsToAccount(BankAccount account) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("fromAccount"), account),
                        criteriaBuilder.equal(root.get("toAccount"), account)
                );
    }

    public static Specification<Transaction> hasType(TransactionType type) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("transactionType"), type);
    }

    public static Specification<Transaction> fromDate(LocalDateTime fromDate) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        fromDate
                );
    }

    public static Specification<Transaction> toDate(LocalDateTime toDate) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        toDate
                );
    }
}