package com.sq.sjcproject.auth.dto;

import lombok.Data;

@Data
public class SjcRegisterDto {
    private String username;
    private String password;
    private String securePassword;
    private String email;
    private String captchaUuid;
    private String captchaInput;
    private String captchaType;
    private Long systemRoleId;
    private Long projectId;
    private String sjcRoleCode;
}
