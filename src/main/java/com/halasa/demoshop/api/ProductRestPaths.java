package com.halasa.demoshop.api;

public abstract class ProductRestPaths {

    private static final String BASE = RestApiPaths.BASE + "/products";
    private static final String WITH_ID = BASE + "/{id}";

    public static final String GET = WITH_ID;
    public static final String SEARCH = BASE;
    public static final String CREATE = BASE;
    public static final String UPDATE = WITH_ID;
    public static final String DELETE = WITH_ID;

    public static final String GET_PICTURE = WITH_ID + "/picture";
}
