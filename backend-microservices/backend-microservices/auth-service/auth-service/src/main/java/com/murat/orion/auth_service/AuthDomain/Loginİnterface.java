package com.murat.orion.auth_service.AuthDomain;

import com.murat.orion.auth_service.AuthDomain.Dto.Response.LoginResponse;

public interface Loginİnterface<T> {
    LoginResponse login(T loginRequest);

    String getLoginType();
}
