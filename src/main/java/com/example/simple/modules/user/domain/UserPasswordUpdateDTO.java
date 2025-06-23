package com.example.simple.modules.user.domain;

import lombok.Data;

@Data
public class UserPasswordUpdateDTO {

    private String oldPassword;

    private String newPassword;
}