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

package com.sun.jersey.impl.provider.header;

import com.sun.jersey.impl.http.header.HttpDateFormat;
import com.sun.jersey.impl.http.header.HttpHeaderFactory;
import com.sun.jersey.spi.HeaderDelegateProvider;
import java.text.ParseException;
import java.util.Date;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class DateProvider implements HeaderDelegateProvider<Date> {
    
    public boolean supports(Class<?> type) {
        return Date.class.isAssignableFrom(type);
    }

    public String toString(Date header) {
        return HttpDateFormat.getPreferedDateFormat().format(header);
    }

    public Date fromString(String header) {
        try {
            return HttpHeaderFactory.createDate(header);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
}