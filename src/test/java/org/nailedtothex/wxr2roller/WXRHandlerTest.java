package org.nailedtothex.wxr2roller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WXRHandlerTest {

    @org.junit.Test
    public void testGetAnchorFromLink() throws Exception {
        String input = "http://jlueck.wordpress.com/2008/05/16/a-test-of-newlines-and-labelscategories/";
        String expected = "a-test-of-newlines-and-labelscategories";
        String actual = WXRHandler.getAnchorFromLink(input);
        assertThat(actual, is(expected));
    }

    @org.junit.Test
    public void testGetAnchorFromLink2() throws Exception {
        String input = "http://jlueck.wordpress.com/2008/05/16/a-test-of-newlines-and-labelscategories";
        String expected = "a-test-of-newlines-and-labelscategories";
        String actual = WXRHandler.getAnchorFromLink(input);
        assertThat(actual, is(expected));
    }
}