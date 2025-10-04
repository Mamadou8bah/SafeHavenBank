package com.mamadou.safehavenbank.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mamadou.safehavenbank.enums.Permission.*;

@RequiredArgsConstructor
public enum Role {
    USER(Collections.emptySet()),
    CUSTOMER(
            Set.of(
                    ACCOUNT_VIEW,
                    TRANSACTION_VIEW,
                    TRANSACTION_CREATE,
                    LOAN_APPLY,
                    LOAN_VIEW
            )
    ),
    TELLER(
            Set.of(
                    ACCOUNT_CREATE,
                    ACCOUNT_VIEW,
                    ACCOUNT_UPDATE,
                    ACCOUNT_DELETE,
                    TRANSACTION_APPROVE,
                    TRANSACTION_VIEW,
                    CARD_MANAGE
            )
    ),
    LOAN_OFFICER(
            Set.of(
                    ACCOUNT_VIEW,
                    TRANSACTION_VIEW,
                    LOAN_VIEW,
                    LOAN_APPROVE
            )
    ),
    BANK_MANAGER(
            Set.of(
                    ACCOUNT_DELETE,
                    ACCOUNT_VIEW,
                    ACCOUNT_CREATE,
                    LOAN_VIEW,
                    LOAN_APPROVE,
                    AUDIT_VIEW,
                    TRANSACTION_VIEW,
                    TRANSACTION_APPROVE,
                    ROLE_MANAGE,
                    CARD_MANAGE
            )
    ),
    AUDITOR(
            Set.of(
                    ACCOUNT_SUSPEND,
                    ACCOUNT_VIEW,
                    TRANSACTION_VIEW,
                    LOAN_VIEW
            )
    ),
    ADMIN(
            Set.of(
                    ACCOUNT_SUSPEND,
                    ACCOUNT_VIEW,
                    ACCOUNT_CREATE,
                    ACCOUNT_DELETE,
                    ACCOUNT_UPDATE,
                    ACCOUNT_SUSPEND,


                    TRANSACTION_VIEW,
                    TRANSACTION_CREATE,
                    TRANSACTION_APPROVE,

                    LOAN_APPLY,
                    LOAN_APPROVE,
                    LOAN_VIEW,

                    CARD_MANAGE,
                    USER_MANAGE,
                    ROLE_MANAGE,
                    AUDIT_VIEW,
                    SYSTEM_CONFIGURE


            )
    );


    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities= getPermissions().stream().map(
                permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
