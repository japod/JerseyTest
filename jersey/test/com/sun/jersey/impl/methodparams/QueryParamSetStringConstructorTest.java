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

package com.sun.jersey.impl.methodparams;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Path;
import com.sun.jersey.impl.AbstractResourceTester;
import java.math.BigDecimal;
import java.util.Set;
import javax.ws.rs.GET;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class QueryParamSetStringConstructorTest extends AbstractResourceTester {

    public QueryParamSetStringConstructorTest(String testName) {
        super(testName);
    }

    @Path("/")
    public static class ResourceStringSet {
        @GET
        public String doGetString(@QueryParam("args") Set<BigDecimal> args) {
            assertTrue(args.contains(new BigDecimal("3.145")));
            assertTrue(args.contains(new BigDecimal("2.718")));
            assertTrue(args.contains(new BigDecimal("1.618")));
            return "content";
        }
    }
    
    @Path("/")
    public static class ResourceStringSetAbsent {
        @GET
        public String doGetString(@QueryParam("args") Set<BigDecimal> args) {
            assertEquals(null, args);
            return "content";
        }
    }
    
    
    @Path("/")
    public static class ResourceStringSetNullDefault {
        @GET
        public String doGetString(@QueryParam("args") Set<BigDecimal> args) {
            assertEquals(null, args);
            return "content";
        }
    }
    
    @Path("/")
    public static class ResourceStringSetDefault {
        @GET
        public String doGetString(
                @QueryParam("args") @DefaultValue("3.145") Set<BigDecimal> args) {
            assertTrue(args.contains(new BigDecimal("3.145")));
            return "content";
        }
    }
    
    @Path("/")
    public static class ResourceStringSetDefaultOverride {
        @GET
        public String doGetString(
                @QueryParam("args") @DefaultValue("3.145") Set<BigDecimal> args) {
            assertTrue(args.contains(new BigDecimal("2.718")));
            return "content";
        }
    }
    
    public void testStringConstructorSetGet() {
        initiateWebApplication(ResourceStringSet.class);
        
        resource("/?args=3.145&args=2.718&args=1.618").
                accept("application/stringSet").
                get(String.class);
    }
    
    public void testStringConstructorSetAbsentGet() {
        initiateWebApplication(ResourceStringSetAbsent.class);
        
        resource("/").
            accept("application/stringSet").
            get(String.class);
    }
    
    
    public void testStringConstructorSetNullDefault() {
        initiateWebApplication(ResourceStringSetNullDefault.class);
        
        resource("/").get(String.class);
    }
    
    public void testStringConstructorSetDefault() {
        initiateWebApplication(ResourceStringSetDefault.class);
        
        resource("/").get(String.class);
    }
    
    public void testStringConstructorSetDefaultOverride() {
        initiateWebApplication(ResourceStringSetDefaultOverride.class);
        
        resource("/?args=2.718").
                get(String.class);
    }    
}