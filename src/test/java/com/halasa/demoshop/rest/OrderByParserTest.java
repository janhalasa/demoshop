package com.halasa.demoshop.rest;

import com.halasa.demoshop.service.OrderBy;
import com.halasa.demoshop.service.OrderByDirection;
import com.halasa.demoshop.service.validation.ValidationException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class OrderByParserTest {

    private OrderByParser parser = new OrderByParser();

    @Test
    public void testParseOne() {
        Assert.assertEquals(
                new OrderBy("field", OrderByDirection.ASC),
                parser.parseOne("field:asc"));

        Assert.assertEquals(
                new OrderBy("field", OrderByDirection.ASC),
                parser.parseOne("field:ASC"));

        Assert.assertEquals(
                new OrderBy("field", OrderByDirection.DESC),
                parser.parseOne("field:desc"));

        Assert.assertEquals(
                new OrderBy("field", OrderByDirection.DESC),
                parser.parseOne("field:DESC"));

        Assert.assertEquals(
                new OrderBy("field", OrderByDirection.ASC),
                parser.parseOne("field"));
    }

    @Test
    public void testParse() {
        final List<OrderBy> parsed = parser.parse(" id: asc, name2 :desc,size, some.asoc.id:desc ");
        Assert.assertEquals(new OrderBy("id", OrderByDirection.ASC), parsed.get(0));
        Assert.assertEquals(new OrderBy("name2", OrderByDirection.DESC), parsed.get(1));
        Assert.assertEquals(new OrderBy("size", OrderByDirection.ASC), parsed.get(2));
        Assert.assertEquals(new OrderBy("some.asoc.id", OrderByDirection.DESC), parsed.get(3));
    }

    @Test(expected = ValidationException.class)
    public void testParseWrongFormat() {
        final List<OrderBy> parsed = parser.parse("id-asc");
    }
}
