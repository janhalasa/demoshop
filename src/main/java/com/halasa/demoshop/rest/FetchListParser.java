package com.halasa.demoshop.rest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FetchListParser {

    public List<String> parse(String fetchListString) {
        if (fetchListString == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(fetchListString.trim().split("[ ]*,[ ]*"));
    }
}
