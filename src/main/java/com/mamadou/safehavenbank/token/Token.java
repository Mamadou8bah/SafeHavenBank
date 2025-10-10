package com.mamadou.safehavenbank.token;

import com.mamadou.safehavenbank.entity.User;
import com.mamadou.safehavenbank.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType type=TokenType.BEARER;
    private boolean revoked;

    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
