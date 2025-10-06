package com.mamadou.safehavenbank.wrapper;


import com.mamadou.safehavenbank.dto.RegisterRequest;
import com.mamadou.safehavenbank.entity.User;

public class RegisterRequestWrapper {

    public static User wrapToUser(RegisterRequest registerRequest) {
        User wrappedUser = new User();
        wrappedUser.setAddress(registerRequest.getAddress());
        wrappedUser.setBirthDate(registerRequest.getBirthDate());
        wrappedUser.setEmail(registerRequest.getEmail());
        wrappedUser.setPhoneNumber(registerRequest.getPhoneNumber());
        wrappedUser.setFullName(registerRequest.getFullName());
        return wrappedUser;
    }
}
