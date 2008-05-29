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

package com.sun.jersey.impl.model.parameter;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.impl.model.parameter.multivalued.MultivaluedParameterProcessor;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.api.representation.FormParam;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableContext;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.spi.service.ComponentProvider.Scope;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public final class FormParamInjectableProvider implements 
        InjectableProvider<FormParam, Parameter> {
    
    private static final class FormParamInjectable implements Injectable<Object> {
        private final MultivaluedParameterExtractor extractor;
        private final boolean decode;
        
        FormParamInjectable(MultivaluedParameterExtractor extractor, boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }
        
        public Object getValue(HttpContext context) {
            Form form = (Form)
                    context.getProperties().get("com.sun.jersey.api.representation.form");
            if (form == null) {
                throw new ContainerException(
                        "An @FormParam parameter is being used in a context" +
                        "where no form representation is available");
            }
            return extractor.extract(form);
        }
    }
        
    public Scope getScope() {
        return Scope.PerRequest;
    }
    
    public Injectable getInjectable(InjectableContext ic, FormParam a, Parameter c) {
        String parameterName = c.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            // Invalid query parameter name
            return null;
        }
        
        MultivaluedParameterExtractor e = MultivaluedParameterProcessor.
                process(c.getDefaultValue(), c.getParameterClass(), 
                c.getParameterType(), parameterName);    
        if (e == null)
            return null;
        
        return new FormParamInjectable(e, !c.isEncoded());
    }
}