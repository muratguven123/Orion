package org.murat.orion.AuthDomain;

import org.murat.orion.AuthDomain.Dto.Response.LoginResponse;

public interface Loginİnterface<T> {
    LoginResponse login(T loginRequest);

    String getLoginType();
}
