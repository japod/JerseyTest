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

package com.sun.jersey.impl.provider.entity;


import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.impl.util.ThrowHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author japod
 */
public class JSONObjectProvider  extends AbstractMessageReaderWriterProvider<JSONObject>{
    
    public JSONObjectProvider() {
        Class<?> c = JSONObject.class;
    }
    
    public boolean isReadable(Class<?> type, Type genericType, Annotation annotations[]) {
        return type == JSONObject.class;        
    }
    
    public JSONObject readFrom(
            Class<JSONObject> type, 
            Type genericType, 
            Annotation annotations[],
            MediaType mediaType, 
            MultivaluedMap<String, String> httpHeaders, 
            InputStream entityStream) throws IOException {
        try {
            return new JSONObject(readFromAsString(entityStream, mediaType));
        } catch (JSONException je) {
            throw ThrowHelper.withInitCause(je, new IOException(ImplMessages.ERROR_PARSING_JSON_OBJECT()));
        }
    }
    
    public boolean isWriteable(Class<?> type, Type genericType, Annotation annotations[]) {
        return type == JSONObject.class;        
    }
    
    public void writeTo(
            JSONObject t, 
            Class<?> type, 
            Type genericType, 
            Annotation annotations[], 
            MediaType mediaType, 
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(entityStream, 
                    getCharset(mediaType, UTF8));
            t.write(writer);
            writer.write("\n");
            writer.flush();
        } catch (JSONException je) {
            throw ThrowHelper.withInitCause(je, new IOException(ImplMessages.ERROR_WRITING_JSON_OBJECT()));
        }
    }
}