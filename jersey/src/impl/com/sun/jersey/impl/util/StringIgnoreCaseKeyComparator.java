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

package com.sun.jersey.impl.util;

/**
 * Case insensitive String key comparator.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class StringIgnoreCaseKeyComparator implements KeyComparator<String> {
    public static final StringIgnoreCaseKeyComparator SINGLETON = 
            new StringIgnoreCaseKeyComparator();
    
    public int hash(String k) {
        return k.toLowerCase().hashCode();
    }

    public boolean equals(String x, String y) {
        return x.equalsIgnoreCase(y);
    }
    
    public int compare(String o1, String o2) {
        return o1.compareToIgnoreCase(o2);
    }
        
}