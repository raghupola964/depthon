package com.depthon.dto;

import lombok.Data;
import com.depthon.domain.Division;
import com.depthon.domain.Subdivision;

@Data
public class RegisterRequest {

    private String email;
    private String username;
    private String fullName;
    private String password;

    private Division division;
    private Subdivision subdivision;
}