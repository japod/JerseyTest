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

package com.sun.jersey.impl.json;

import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author japod
 */
@XmlRootElement
public class ComplexBeanWithAttributes2 {

    @XmlAttribute public String a1;
    @XmlAttribute public int a2;
    @XmlElement public String filler1;
    @XmlElement public List<SimpleBeanWithJustOneAttribute> list;
    @XmlElement public String filler2;
    @XmlElement SimpleBeanWithJustOneAttribute b;

    public static Object createTestInstance() {
        ComplexBeanWithAttributes2 instance = new ComplexBeanWithAttributes2();
        instance.a1 = "hello dolly";
        instance.a2 = 31415926;
        instance.filler1 = "111";
        instance.filler2 = "222";
        instance.b = (SimpleBeanWithJustOneAttribute)SimpleBeanWithJustOneAttribute.createTestInstance();
        instance.list = new LinkedList<SimpleBeanWithJustOneAttribute>();
        instance.list.add((SimpleBeanWithJustOneAttribute)SimpleBeanWithJustOneAttribute.createTestInstance());
        instance.list.add((SimpleBeanWithJustOneAttribute)SimpleBeanWithJustOneAttribute.createTestInstance());
        return instance;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ComplexBeanWithAttributes2)) {
            return false;
        }
        final ComplexBeanWithAttributes2 other = (ComplexBeanWithAttributes2) obj;
        if (this.a1 != other.a1 && (this.a1 == null || !this.a1.equals(other.a1))) {
            System.out.println("a1 differs");
            return false;
        }
        if (this.a2 != other.a2) {
            System.out.println("a2 differs");
            return false;
        }
        if (this.b != other.b && (this.b == null || !this.b.equals(other.b))) {
            System.out.println("b differs");
            return false;
        }
        if (this.filler1 != other.filler1 && (this.filler1 == null || !this.filler1.equals(other.filler1))) {
            System.out.println("f1 differs");
            return false;
        }
        if (this.filler2 != other.filler2 && (this.filler2 == null || !this.filler2.equals(other.filler2))) {
            System.out.println("f2 differs");
            return false;
        }
        if (this.list != other.list && (this.list == null || !this.list.equals(other.list))) {
            System.out.println("list differs");
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.a1 != null ? this.a1.hashCode() : 0);
        hash = 19 * hash + this.a2;
        hash = 19 * hash + (this.b != null ? this.b.hashCode() : 0);
        hash = 19 * hash + (this.filler1 != null ? this.filler1.hashCode() : 0);
        hash = 19 * hash + (this.filler2 != null ? this.filler2.hashCode() : 0);
        hash = 19 * hash + (this.list != null ? this.list.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("CBWA2(%s,%d,%s,%s)", a1, a2, b, listAsString());
    }
    
    private String listAsString() {
        return (list != null) ? list.toString() : null;
    }
}