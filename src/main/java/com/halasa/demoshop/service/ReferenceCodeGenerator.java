package com.halasa.demoshop.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReferenceCodeGenerator {

    public String generate() {
        return UUID.randomUUID().toString();
    }
}
