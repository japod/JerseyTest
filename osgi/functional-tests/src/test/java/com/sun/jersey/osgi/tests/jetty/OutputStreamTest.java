/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.jersey.osgi.tests.jetty;

import javax.ws.rs.Path;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class OutputStreamTest extends AbstractJettyWebContainerTester {
    @Path("/output")
    public static class TestResource { // implements WebResource {

        @Produces("text/plain")
        @GET
        public void get(HttpRequestContext requestContext, 
                HttpResponseContext responseContext) throws IOException {
            assertEquals("GET", requestContext.getMethod());
            
            responseContext.getOutputStream().
                    write("RESOURCE".getBytes());
        }
        
        @Produces("text/plain")
        @POST
        public void post(HttpRequestContext requestContext, 
                HttpResponseContext responseContext) throws IOException {
            assertEquals("POST", requestContext.getMethod());
            
            String s = requestContext.getEntity(String.class);
            assertEquals("RESOURCE", s);
     
            responseContext.getOutputStream().
                    write("RESOURCE".getBytes());
        }
    }
    
    @Test
    public void testGet() {
        startServer(TestResource.class);
                
        WebResource r = Client.create().resource(getUri().path("output").build());
        assertEquals("RESOURCE", r.get(String.class));        
    }

    @Test
    public void testPost() {        
        startServer(TestResource.class);
                
        WebResource r = Client.create().resource(getUri().path("output").build());
        assertEquals("RESOURCE", r.post(String.class, "RESOURCE"));
    }

    @Test
    public void testAll() {
        startServer(TestResource.class);
                
        WebResource r = Client.create().resource(getUri().path("output").build());
        assertEquals("RESOURCE", r.get(String.class));        
        
        r = Client.create().resource(getUri().path("output").build());
        assertEquals("RESOURCE", r.post(String.class, "RESOURCE"));
    }
}