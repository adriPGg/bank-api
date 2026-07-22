package com.adrian.bankapi.specification;

import com.adrian.bankapi.entity.BankAccount;
import com.adrian.bankapi.entity.Transaction;
import com.adrian.bankapi.entity.TransactionType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> filter(
            List<BankAccount> accounts,
            BankAccount account,
            TransactionType type,
            LocalDateTime from,
            LocalDateTime to)
    {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (account != null) {

                predicates.add(
                        cb.or(
                                cb.equal(root.get("fromAccount"), account),
                                cb.equal(root.get("toAccount"), account)
                        )
                );

            } else {

                predicates.add(
                        cb.or(
                                root.get("fromAccount").in(accounts),
                                root.get("toAccount").in(accounts)
                        )
                );
            }

            if (type != null) {
                predicates.add(
                        cb.equal(root.get("transactionType"), type)
                );
            }

            if (from != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                from
                        )
                );
            }

            if (to != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"),
                                to
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private TransactionSpecification() {
    }
}