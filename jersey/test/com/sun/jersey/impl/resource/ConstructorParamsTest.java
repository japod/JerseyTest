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

package com.sun.jersey.impl.resource;

import com.sun.jersey.impl.AbstractResourceTester;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Marc Hadley
 */
public class ConstructorParamsTest extends AbstractResourceTester {
    
    public ConstructorParamsTest(String testName) {
        super(testName);
    }
    
    @Path("/{id}")
    public static class TestOneWebResourceBean {
        
        private String id;
        private UriInfo info;
        
        public TestOneWebResourceBean(@PathParam("id") String id, @Context UriInfo info) {
            this.id = id;
            this.info = info;
        }
        
        @GET
        public String doGet() {
            assertEquals(id, "foo");            
            assertEquals("foo", info.getPath());            
            return "foo";
        }
        
    }
    
    public void testOneWebResource() {
        initiateWebApplication(TestOneWebResourceBean.class);
        resource("/foo").get(String.class);
    }    
}