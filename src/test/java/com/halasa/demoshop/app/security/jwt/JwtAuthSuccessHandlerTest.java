package com.halasa.demoshop.app.security.jwt;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.util.Pair;

public class JwtAuthSuccessHandlerTest {

    @Test
    public void testSplitNames() {
        JwtAuthSuccessHandler handler = new JwtAuthSuccessHandler(null, null);
        this.assertNames(handler, "Pedro", "Pedro", "Pedro");
        this.assertNames(handler, "Pedro V", "Pedro", "V");
    }

    private void assertNames(JwtAuthSuccessHandler handler, String fullName, String firstName, String lastName) {
        Pair<String, String> names = handler.splitNames(fullName);
        Assert.assertEquals(firstName, names.getFirst());
        Assert.assertEquals(lastName, names.getSecond());
    }
}
