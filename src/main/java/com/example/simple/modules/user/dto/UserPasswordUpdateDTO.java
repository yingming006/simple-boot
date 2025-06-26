package com.example.simple.modules.user.dto;

import lombok.Data;

@Data
public class UserPasswordUpdateDTO {

    private String oldPassword;

    private String newPassword;
}