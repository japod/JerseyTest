/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved. 
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License. 
 * 
 * You can obtain a copy of the License at:
 *     https://jersey.dev.java.net/license.txt
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each
 * file and include the License file at:
 *     https://jersey.dev.java.net/license.txt
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 *     "Portions Copyrighted [year] [name of copyright owner]"
 */

package com.sun.jersey.impl;

import com.sun.jersey.impl.application.WebApplicationContext;
import com.sun.jersey.spi.container.ContainerRequest;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.*;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class QueryParametersHttpRequestTest extends TestCase {
    
    public QueryParametersHttpRequestTest(String testName) {
        super(testName);
    }
    
    public void testGeneral() throws Exception {
        ContainerRequest r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?verbose=true&item=1&item=2", "/context");
        MultivaluedMap<String, String> p = new WebApplicationContext(null, r, null).
                getQueryParameters();
        assertEquals(p.get("verbose").size(), 1);
        assertEquals(p.getFirst("verbose"),"true");
        assertEquals(p.get("item").size(), 2);
        assertEquals(p.getFirst("item"),"1");
        assertEquals(p.get("foo"), null);
        assertEquals(p.getFirst("foo"), null);
    }
    
    public void testEmpty() throws Exception {
        ContainerRequest r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10", "/context");
        MultivaluedMap<String, String> p = new WebApplicationContext(null, r, null).
                getQueryParameters();
        assertEquals(p.size(),0);
    }
    
    public void testSingleAmpersand() throws Exception {
        ContainerRequest r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?&", "/context");
        MultivaluedMap<String, String> p = new WebApplicationContext(null, r, null).
                getQueryParameters();
        assertEquals(p.size(),0);
    }
    
    public void testMultipleAmpersand() throws Exception {
        ContainerRequest r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?&&%20=%20&&&", "/context");
        MultivaluedMap<String, String> p = new WebApplicationContext(null, r, null).
                getQueryParameters();
        assertEquals(p.size(),1);
    }
    
    public void testInterspersedAmpersand() throws Exception {
        ContainerRequest r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?a=1&&b=2", "/context");
        MultivaluedMap<String, String> p = new WebApplicationContext(null, r, null).
                getQueryParameters();
        assertEquals(p.size(),2);
    }
    
    public void testEmptyValues() throws Exception {
        ContainerRequest r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?one&two&three", "/context");
        MultivaluedMap<String, String> p = new WebApplicationContext(null, r, null).
                getQueryParameters();
        assertEquals(p.getFirst("one"),"");
        assertEquals(p.getFirst("two"),"");
        assertEquals(p.getFirst("three"),"");
    }
    
    public void testMultipleEmptyValues() throws Exception {
        ContainerRequest r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?one&one&one", "/context");
        MultivaluedMap<String, String> p = new WebApplicationContext(null, r, null).
                getQueryParameters();
        assertEquals(p.get("one").size(), 3);
        assertEquals(p.get("one").get(0),"");
        assertEquals(p.get("one").get(1),"");
        assertEquals(p.get("one").get(2),"");
    }
    
    public void testWhiteSpace() throws Exception {
        ContainerRequest r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?x+=+1%20&%20y+=+2", "/context");
        MultivaluedMap<String, String> p = new WebApplicationContext(null, r, null).
                getQueryParameters();
        assertEquals(p.getFirst("x "), " 1 ");
        assertEquals(p.getFirst(" y "), " 2");
    }
    
    public void testDecoded() throws Exception {
        ContainerRequest r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?x+=+1%20&%20y+=+2", "/context");
        MultivaluedMap<String, String> p = new WebApplicationContext(null, r, null).
                getQueryParameters();
        assertEquals(" 1 ", p.getFirst("x "));
        assertEquals(" 2", p.getFirst(" y "));
        
        r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?x=1&y=1+%2B+2", "/context");
        p = new WebApplicationContext(null, r, null)
                .getQueryParameters(true);
        assertEquals("1", p.getFirst("x"));
        assertEquals("1 + 2", p.getFirst("y"));
        
        r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?x=1&y=1+%26+2", "/context");
        p = new WebApplicationContext(null, r, null)
                .getQueryParameters(true);
        assertEquals("1", p.getFirst("x"));
        assertEquals("1 & 2", p.getFirst("y"));
        
        r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?x=1&y=1+%7C%7C+2", "/context");
        p = new WebApplicationContext(null, r, null)
                .getQueryParameters(true);
        assertEquals("1", p.getFirst("x"));
        assertEquals("1 || 2", p.getFirst("y"));
    }
        
    public void testEncoded() throws Exception {
        ContainerRequest r = new TestHttpRequestContext(null, "GET", null,
                "/context/widgets/10?x+=+1%20&%20y+=+2", "/context");
        MultivaluedMap<String, String> p = new WebApplicationContext(null, r, null).
                getQueryParameters(false);
        assertEquals("+1%20", p.getFirst("x+"));
        assertEquals("+2", p.getFirst("%20y+"));
    }
}