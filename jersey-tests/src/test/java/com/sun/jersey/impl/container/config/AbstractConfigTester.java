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

package com.sun.jersey.impl.container.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import junit.framework.*;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public abstract class AbstractConfigTester extends TestCase {
    public enum Suffix {
        jar, zip
    }
    
    public AbstractConfigTester(String testName) {
        super(testName);
    }
    
    public final File createJarFile(String base, String... entries) throws IOException {
        return createJarFile(Suffix.jar, base, entries);
    }
    
    public final File createJarFile(Suffix s, String base, String... entries) throws IOException {
        Map<String, String> entriesMap = new HashMap<String, String>();
        for (String entry : entries) {
            entriesMap.put(entry, entry);
        }
        return createJarFile(s, base, entriesMap);
    }

    public final File createJarFile(String name, Suffix s, String base, String... entries) throws IOException {
        Map<String, String> entriesMap = new HashMap<String, String>();
        for (String entry : entries) {
            entriesMap.put(entry, entry);
        }
        return createJarFile(name, s, base, entriesMap);
    }

    public final File createJarFile(Suffix s, String base, Map<String, String> entries) throws IOException {
        return createJarFile("test", s, base, entries);
    }

    public final File createJarFile(String name, Suffix s, String base, Map<String, String> entries) throws IOException {
        File tempJar = File.createTempFile(name, "." + s);
        tempJar.deleteOnExit();
        JarOutputStream jos = new JarOutputStream(
                new BufferedOutputStream(
                new FileOutputStream(tempJar)), new Manifest());
        
        Set<String> usedSegments = new HashSet<String>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            for (String path : getPaths(entry.getValue())) {
                if (usedSegments.contains(path))
                    continue;
                
                usedSegments.add(path);
                JarEntry e = new JarEntry(path);
                jos.putNextEntry(e);
                jos.closeEntry();                
            }
            
            JarEntry e = new JarEntry(entry.getValue());
            jos.putNextEntry(e);

            InputStream f = new BufferedInputStream(
                    new FileInputStream(base + entry.getKey()));
            byte[] buf = new byte[1024];
            int read = 1024;
            while ((read = f.read(buf, 0, read)) != -1 ) {
                jos.write(buf, 0, read);
            }
            jos.closeEntry();
        }
        
        jos.close();
        return tempJar;
    }
    
    private String[] getPaths(String entry) {
        String[] segments = entry.split("/");
        String[] paths = new String[segments.length - 1];
        
        if (paths.length == 0)
            return paths;
        
        paths[0] = segments[0] + "/";
        for (int i = 1; i < paths.length; i++) {
            paths[i] = "";
            for (int j = 0; j <= i; j++) {
                paths[i] += segments[j] + "/";
            }
        }
            
        return paths;
    }
}
