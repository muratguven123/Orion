package org.murat.orion.AuthDomain;

import org.murat.orion.AuthDomain.Dto.Request.LoginRequest;
import org.murat.orion.AuthDomain.Dto.Response.LoginResponse;

public interface Loginİnterface {
    LoginResponse login(LoginRequest loginRequest);

    String getLoginType();
}
