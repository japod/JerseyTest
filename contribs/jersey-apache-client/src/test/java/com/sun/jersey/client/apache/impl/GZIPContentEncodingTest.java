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

package com.sun.jersey.client.apache.impl;

import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.Path;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import java.util.Arrays;
import javax.ws.rs.POST;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class GZIPContentEncodingTest extends AbstractGrizzlyServerTester {

    @Path("/")
    public static class Resource {
        @POST
        public byte[] post(byte[] content) { return content; }
    }
    
    public GZIPContentEncodingTest(String testName) {
        super(testName);
    }


    public void testPost() {
        ResourceConfig rc = new DefaultResourceConfig(Resource.class);
        rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS,
                GZIPContentEncodingFilter.class.getName());
        rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS,
                GZIPContentEncodingFilter.class.getName());
        startServer(rc);

        ApacheHttpClient c = ApacheHttpClient.create();
        c.addFilter(new com.sun.jersey.api.client.filter.GZIPContentEncodingFilter());

        WebResource r = c.resource(getUri().path("/").build());
        byte[] content = new byte[1024 * 1024];
        assertTrue(Arrays.equals(content, r.post(byte[].class, content)));

        ClientResponse cr = r.post(ClientResponse.class, content);
        assertTrue(cr.hasEntity());
        cr.close();
    }

    public void testPostChunked() {
        ResourceConfig rc = new DefaultResourceConfig(Resource.class);
        rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS,
                GZIPContentEncodingFilter.class.getName());
        rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS,
                GZIPContentEncodingFilter.class.getName());
        startServer(rc);

        DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
        config.getProperties().put(ApacheHttpClientConfig.PROPERTY_CHUNKED_ENCODING_SIZE, 1024);
        ApacheHttpClient c = ApacheHttpClient.create(config);
        c.addFilter(new com.sun.jersey.api.client.filter.GZIPContentEncodingFilter());

        WebResource r = c.resource(getUri().path("/").build());
        byte[] content = new byte[1024 * 1024];
        assertTrue(Arrays.equals(content, r.post(byte[].class, content)));

        ClientResponse cr = r.post(ClientResponse.class, "POST");
        assertTrue(cr.hasEntity());
        cr.close();
    }

}