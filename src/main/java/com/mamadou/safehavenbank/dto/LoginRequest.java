package com.mamadou.safehavenbank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    @Email
    private String email;
    @NotBlank
    @Length(min = 8)
    private String password;
}
