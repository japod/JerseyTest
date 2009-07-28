/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://jersey.dev.java.net/CDDL+GPL.html
 * or jersey/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at jersey/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
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
package com.sun.jersey.spi.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A cached entity in-bound HTTP request that caches the entity instance
 * obtained from the adapted container request.
 * <p>
 * A filter may utilize this class if it requires an entity of a specific type
 * and that same type will also be utilized by a resource method.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class CachedEntityContainerRequest extends AdaptingContainerRequest {

    Object entity;

    public CachedEntityContainerRequest(ContainerRequest acr) {
        super(acr);
    }

    /**
     * Get the entity or a cached instance.
     *
     * @return If called for the first time obtain the entity from the adapting
     *         container request and cache the instance. If called for second or
     *         subsequent time return the cached instance.
     * @throws ClassCastException if the cached entity cannot be cast to the
     *         type requested.
     */
    @Override
    public <T> T getEntity(Class<T> type) throws ClassCastException {
        if (entity == null) {
            T t =  acr.getEntity(type);
            entity = t;
            return t;
        } else {
            return type.cast(entity);
        }
    }

    /**
     * Get the entity or a cached instance.
     *
     * @return If called for the first time obtain the entity from the adapting
     *         container request and cache the instance. If called for second or
     *         subsequent time return the cached instance.
     * @throws ClassCastException if the cached entity cannot be cast to the
     *         type requested.
     */
    @Override
    public <T> T getEntity(Class<T> type, Type genericType, Annotation[] as) throws ClassCastException {
        if (entity == null) {
            T t = acr.getEntity(type, genericType, as);
            entity = t;
            return t;
        } else {
            return type.cast(entity);
        }
    }
}