package com.halasa.demoshop.api;

public class AuthRestPaths {

    private static final String BASE = RestApiPaths.BASE + "/auth";

    public static final String TOKEN = BASE + "/token";
    public static final String TOKEN_RENEW = BASE + "/token/renew";
    public static final String TOKEN_REVOKE = BASE + "/token/revoke";
}
