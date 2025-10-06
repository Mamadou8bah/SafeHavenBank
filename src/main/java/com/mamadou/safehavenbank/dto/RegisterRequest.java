package com.mamadou.safehavenbank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotNull
    private String fullName;
    @Email
    private String email;

    @Size(min = 8)
    private String password;

    @Size(min = 7)
    private long phoneNumber;

    @NotBlank
    private String address;

    @NotNull
    private LocalDate birthDate;
}
