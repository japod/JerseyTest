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

package com.sun.jersey.api.uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A URI template.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class UriTemplate {
    /**
     * Order the templates according to the string comparison of the
     * template regular expressions.
     * <p>
     * The JSR-311 specification states:
     *     "Sort the set of matching resource classes using the number of 
     *      characters in the regular expression not resulting from template 
     *      variables as the primary key and the number of matching groups 
     *      as a secondary key"
     */
    static public final Comparator<UriTemplate> COMPARATOR = new Comparator<UriTemplate>() {
        public int compare(UriTemplate o1, UriTemplate o2) {
            if (o1 == null && o2 == null)
                return 0;
            if (o1 == null)
                return 1;
            if (o2 == null)
                return -1;
            
            if (o1 == EMPTY && o2 == EMPTY)
                return 0;
            if (o1 == EMPTY)
                return 1;
            if (o2 == EMPTY)
                return -1;

            // Compare the number of explicit characters
            // Note that it is important that o2 is compared against o1
            // so that a regular expression with say 10 explicit characters
            // is less than a regular expression with say 5 explicit characters.
            int i = o2.getNumberOfExplicitCharacters() - o1.getNumberOfExplicitCharacters();
            if (i != 0) return i;

            // If the number of explicit characters is equal
            // compare the number of template variables
            // Note that it is important that o2 is compared against o1
            // so that a regular expression with say 10 template variables
            // is less than a regular expression with say 5 template variables.
            i = o2.getNumberOfTemplateVariables() - o1.getNumberOfTemplateVariables();
            if (i != 0) return i;

            // If the number of explicit characters and template variables
            // are equal then comapre the regexes
            // The order does not matter as long as templates with different
            // explicit characters are distinguishable
            return o2.pattern.getRegex().compareTo(o1.pattern.getRegex());
        }
    };
    
    /**
     * The regular expression for matching URI templates and names.
     */
    private static final Pattern TEMPLATE_NAMES_PATTERN = Pattern.compile("\\{([\\w-\\._~]+?)\\}");
    
    /**
     * This uses a reluctant (non-greedy qualifier) to ensure that
     * expresions to the right of an expression will be matched.
     */
    private static final String TEMPLATE_VALUE_REGEX = "(.*?)";
    
    
    /**
     * The empty URI template that matches the null or empty URI path
     */
    public static final UriTemplate EMPTY = new UriTemplate();
    
    /**
     * The URI template.
     */
    private final String template;
        
    /**
     * The pattern generated from the template
     */
    private final UriPattern pattern;
    
    /**
     * True if the URI template ends in a '/' character.
     */
    private final boolean endsWithSlash;
    
    /**
     * The template variables in the URI template.
     */
    private final List<String> templateVariables;
    
    /**
     * The number of characters in the regular expression not resulting
     * from conversion of template variables.
     */
    private final int numOfCharacters;

    /**
     * Constructor for NULL template
     */
    private UriTemplate() {
        this.template = "";
        this.pattern = UriPattern.EMPTY;
        this.endsWithSlash = false;
        this.templateVariables = Collections.emptyList();
        this.numOfCharacters = 0;
    }
    
    /**
     * Construct a new URI template.
     * <p>
     * The template will be parsed to extract template variables.
     * <p>
     * A specific regular expression will be generated from the template
     * to match URIs according to the template and map template variables to
     * template values.
     * <p>
     * @param template the template.
     * @throws {@link java.util.regex.PatternSyntaxException} if the specific
     *         regular expression could not be generated
     * @throws {@link IllegalArgumentException} if the template is null or
     *         an empty string.
     */
    public UriTemplate(String template) {
        if (template == null || template.length() == 0)
            throw new IllegalArgumentException();
        
        // TODO correctly validate template
        
        StringBuilder b = new StringBuilder();
        List<String> names = new ArrayList<String>();
        
        // Find all template variables
        // Create regular expression for template matching
        Matcher m = TEMPLATE_NAMES_PATTERN.matcher(template);
        int i = 0;
        int c = 0;
        while(m.find()) {
            c += m.start() - i;
            copyURITemplateCharacters(template, i, m.start(), b);
            b.append(TEMPLATE_VALUE_REGEX);
            names.add(m.group(1));
            i = m.end();
        }
        copyURITemplateCharacters(template, i, template.length(), b);
        c += template.length() - i;
        
        this.template = template;
        
        this.pattern = new UriPattern(b.toString());
        
        this.numOfCharacters = c;

        this.endsWithSlash = template.charAt(template.length() - 1) == '/';
        
        this.templateVariables = Collections.unmodifiableList(names);
    }
    
    /**
     * Copy characters from the URI template to a string builder.
     * Characters that correspond to regular expression
     * characters will be escaped.
     * <p>
     * TODO need to escape all regex characters present
     *
     * @param template The URI template.
     * @param start The start index of characters in template to copy
     * @param end The end index of characters in template to copy
     * @param b The string builder to copy the characters.
     */
    private static void copyURITemplateCharacters(String template, int start, int end, 
            StringBuilder b) {
        for (int i = start; i < end; i++) {
            char c = template.charAt(i);
            if (RESERVED_REGEX_CHARACTERS.contains(c))
                b.append("\\");
            b.append(c);
        }
    }
    
    private static Set<Character> RESERVED_REGEX_CHARACTERS = createReserved();
    
    private static Set<Character> createReserved() {
        // TODO need to escape all regex characters present
        char[] reserved = {
            '.',
            '?',
            '(', 
            ')'};
        
        Set<Character> s = new HashSet<Character>();
        for (char c : reserved) s.add(c);
        return s;
    }        
            

    /**
     * Get the URI template as a String.
     * @return the URI template.
     */
    public final String getTemplate() {
        return template;
    }

    /**
     * Get the URI pattern.
     * 
     * @return the URI pattern.
     */
    public final UriPattern getPattern() {
        return pattern;
    }
    
    /**
     * Returns true if the template end in a '/'
     */
    public final boolean endsWithSlash() {
        return endsWithSlash;
    }
    
    /**
     * Get the list of template variables for the template.
     * @return the list of template variables.
     */
    public final List<String> getTemplateVariables() {
        return templateVariables;
    }
    
    /**
     * Ascertain if a template variable is a member of this
     * template.
     * @param name name The template variable.
     * @return true if the template variable is a member of the template, otherwise
     * false.
     */
    public final boolean isTemplateVariablePresent(String name) {
        for (String s : templateVariables) {
            if (s.equals(name))
                return true;
        }
        
        return false;
    }
    
    /** 
     * Get the number of characters in the regular expression not resulting
     * from conversion of template variables.
     * @return the number of explicit characters
     */
    public final int getNumberOfExplicitCharacters() {
        return numOfCharacters;
    }

    /**
     * Get the number of template variables.
     * @return the number of template variables.
     */
    public final int getNumberOfTemplateVariables() {
        return templateVariables.size();
    }

    private int numberOfPathSegments;
    
    private Map<String, Integer> pathSegmentIndex;
    
    /**
     * Get the number of path segments in the template.
     * 
     * @return the number of path segments
     */
    public final int getNumberOfPathSegments() {
        initPathSegments();
        
        return numberOfPathSegments;
    }
    
    /**
     * Get the path segment index of a path segment that contains a
     * template variable.
     * 
     * @param variable the template variable
     * @return the path segment index, otherise -1 if the template variable is
     *         not present
     */
    public final int getPathSegmentIndex(String variable) {
        initPathSegments();
        
        Integer i = pathSegmentIndex.get(variable);
        return (i != null) ? i : -1;
    }
    
    private final void initPathSegments() {
        if (pathSegmentIndex == null) {
            pathSegmentIndex = new HashMap<String, Integer>();
            
            numberOfPathSegments = 0;
            for (String subPath : getTemplate().split("/")) {
                if (subPath.length() == 0)
                    continue;
                
                for (String v : getTemplateVariables())
                    if (subPath.contains('{' + v + '}'))
                        pathSegmentIndex.put(v, numberOfPathSegments);
                numberOfPathSegments++;
            }
        }        
    }
    
    /**
     * Match a URI against the template.
     * <p>
     * If the URI matches against the pattern then the template variable to value 
     * map will be filled with template variables as keys and template values as 
     * values.
     * <p>
     * 
     * @param uri the uri to match against the template.
     * @param templateVariableToValue the map where to put template variables (as keys)
     *        and template values (as values). The map is cleared before any
     *        entries are put.
     * @return true if the URI matches the template, otherwise false.
     * @throws {@link IllegalArgumentException} if the uri or 
     *         templateVariableToValue is null.
     */
    public final boolean match(CharSequence uri, Map<String, String> templateVariableToValue) {
        if (templateVariableToValue == null) 
            throw new IllegalArgumentException();

        return pattern.match(uri, templateVariables, templateVariableToValue);
    }
    
    /**
     * Match a URI against the template.
     * <p>
     * If the URI matches against the pattern then the template variable to value 
     * map will be filled with template variables as keys and template values as 
     * values.
     * <p>
     * 
     * @param uri the uri to match against the template.
     * @param groupValues the list to store the values of a pattern's 
     *        capturing groups is matching is successful. The values are stored 
     *        in the same order as the pattern's capturing groups.
     * @return true if the URI matches the template, otherwise false.
     * @throws {@link IllegalArgumentException} if the uri or 
     *         templateVariableToValue is null.
     */
    public final boolean match(CharSequence uri, List<String> groupValues) {
        if (groupValues == null) 
            throw new IllegalArgumentException();

        return pattern.match(uri, groupValues);
    }
        
    /**
     * Create a URI by substituting any template variables
     * for corresponding template values.
     * <p>
     * A URI template variable without a value will be substituted by the 
     * empty string.
     *
     * @param values the map of template variables to template values.
     * @return the URI.
     */
    public final String createURI(Map<String, String> values) {
        StringBuilder b = new StringBuilder();
        // Find all template variables
        Matcher m = TEMPLATE_NAMES_PATTERN.matcher(template);
        int i = 0;
        while(m.find()) {
            b.append(template, i, m.start());
            String tValue = values.get(m.group(1));
            if (tValue != null) b.append(tValue);
            i = m.end();
        }
        b.append(template, i, template.length());
        return b.toString();
    }
    
    /**
     * Create a URI by substituting any template variables
     * for corresponding template values.
     * <p>
     * A URI template varibale without a value will be substituted by the 
     * empty string.
     *
     * @param values the array of template values. The values will be 
     *        substituted in order of occurence of unique template variables.
     * @return the URI.
     */
    public final String createURI(String... values) {
        return createURI(values, 0, values.length);
    }
    
    /**
     * Create a URI by substituting any template variables
     * for corresponding template values.
     * <p>
     * A URI template variable without a value will be substituted by the 
     * empty string.
     *
     * @param values the array of template values. The values will be 
     *        substituted in order of occurence of unique template variables.
     * @param offset the offset into the array
     * @param length the length of the array
     * @return the URI.
     */
    public final String createURI(String[] values, int offset, int length) {
        Map<String, String> mapValues = new HashMap<String, String>();
        StringBuilder b = new StringBuilder();
        // Find all template variables
        Matcher m = TEMPLATE_NAMES_PATTERN.matcher(template);
        int v = offset;
        length += offset;
        int i = 0;
        while(m.find()) {
            b.append(template, i, m.start());
            String tVariable = m.group(1);
            // Check if a template variable has already occurred
            // If so use the value to ensure that two or more declarations of 
            // a template variable have the same value
            String tValue = mapValues.get(tVariable);
            if (tValue != null) {
                b.append(tValue);
            } else {
                if (v < length) {
                    tValue = values[v++];
                    if (tValue != null) {
                        mapValues.put(tVariable, tValue);
                        b.append(tValue);
                    }
                }
            }
            i = m.end();
        }
        b.append(template, i, template.length());
        return b.toString();
    }
    
    @Override
    public final String toString() {
        return pattern.toString();
    }
    
    /**
     * Hashcode is calculated from String of the regular expression 
     * generated from the template.
     */
    @Override
    public final int hashCode() {
        return pattern.hashCode();
    }

    /**
     * Equality is calculated from the String of the regular expression 
     * generated from the templates.
     */
    @Override
    public final boolean equals(Object o) {
        if (o instanceof UriTemplate) {
            UriTemplate that = (UriTemplate)o;
            return this.pattern.equals(that.pattern);
        } else {
            return false;
        }
    }
    
    /**
     * Construct a URI from the component parts each of which may contain 
     * template variables.
     * <p>
     * A template values is an Object instance MUST support the toString() 
     * method to convert the template value to a String instance.
     *
     * @param scheme the URI scheme component
     * @param userInfo the URI user info component
     * @param host the URI host component
     * @param port the URI port component
     * @param path the URI path component
     * @param query the URI query componnet
     * @param fragment the URI fragment component
     * @param values the template variable to value map
     * @param encode if true encode a template value according to the correspond
     *        component type of the associated template variable, otherwise
     *        validate the template value
     * @return a URI
     */
    public final static String createURI(final String scheme, 
            final String userInfo, final String host, final String port, 
            final String path, final String query, final String fragment,
            final Map<String, Object> values, final boolean encode) {
        Map<String, String> stringValues = new HashMap<String, String>();
        for (Map.Entry<String, Object> e : values.entrySet()) {
            if (e.getValue() != null)
                stringValues.put(e.getKey(), e.getValue().toString());
        }
        
        return createURIWithStringValues(scheme, userInfo, host, port, path, query, fragment, 
                stringValues, encode);
    }
    
    /**
     * Construct a URI from the component parts each of which may contain 
     * template variables.
     *
     * @param scheme the URI scheme component
     * @param userInfo the URI user info component
     * @param host the URI host component
     * @param port the URI port component
     * @param path the URI path component
     * @param query the URI query componnet
     * @param fragment the URI fragment component
     * @param values the template variable to value map
     * @param encode if true encode a template value according to the correspond
     *        component type of the associated template variable, otherwise
     *        validate the template value
     * @return a URI
     */
    public final static String createURIWithStringValues(final String scheme, 
            final String userInfo, final String host, final String port, 
            final String path, final String query, final String fragment,
            final Map<String, String> values, final boolean encode) {
        
        StringBuilder sb = new StringBuilder();
        
        if (scheme != null)
            createURIComponent(UriComponent.Type.SCHEME, scheme, values, false, sb).
                    append(':');
        
        if (userInfo != null || host != null || port != null) {
            sb.append("//");
            
            if (userInfo != null && userInfo.length() > 0) 
                createURIComponent(UriComponent.Type.USER_INFO, userInfo, values, encode, sb).
                    append('@');
            
            if (host != null) {
                // TODO check IPv6 address
                createURIComponent(UriComponent.Type.HOST, host, values, encode, sb);
            }

            if (port != null && port.length() > 0) {
                sb.append(':');
                createURIComponent(UriComponent.Type.PORT, port, values, false, sb);                
            }
        }

        if (path != null)
            createURIComponent(UriComponent.Type.PATH, path, values, encode, sb);                
        
        if (query != null && query.length() > 0) {
            sb.append('?');
            createURIComponent(UriComponent.Type.QUERY, query, values, encode, sb);
        }
         
        if (fragment != null && fragment.length() > 0) {
            sb.append('#');
            createURIComponent(UriComponent.Type.FRAGMENT, path, values, encode, sb);                
        }
        return sb.toString();
    }
    
    private static StringBuilder createURIComponent(final UriComponent.Type t, 
            final String template,
            final Map<String, String> values, 
            final boolean encode, 
            final StringBuilder b) {
        if (template.indexOf('{') == -1) {
            b.append(template);
            return b;
        }
        
        // Find all template variables
        final Matcher m = TEMPLATE_NAMES_PATTERN.matcher(template);
        int i = 0;
        while(m.find()) {
            b.append(template, i, m.start());
            String tValue = values.get(m.group(1));
            if (tValue != null) {
                if (encode)
                    tValue = UriComponent.encode(tValue, t);
                else
                    UriComponent.validate(tValue, t);
                b.append(tValue);
            } else {
                throw new IllegalArgumentException("The template variable, " + 
                        m.group(1) + ", has no value");
            }
            i = m.end();
        }
        b.append(template, i, template.length());
        return b;
    }
    
    /**
     * Construct a URI from the component parts each of which may contain 
     * template variables.
     * <p>
     * The template values are an array of Object and each Object instance
     * MUST support the toString() method to convert the template value to
     * a String instance.
     * 
     * @param scheme the URI scheme component
     * @param userInfo the URI user info component
     * @param host the URI host component
     * @param port the URI port component
     * @param path the URI path component
     * @param query the URI query componnet
     * @param fragment the URI fragment component
     * @param values the array of template values
     * @param encode if true encode a template value according to the correspond
     *        component type of the associated template variable, otherwise
     *        validate the template value
     * @return a URI
     */
    public final static String createURI(final String scheme, 
            final String userInfo, final String host, final String port, 
            final String path, final String query, final String fragment,
            final Object[] values, final boolean encode) {
        
        String[] stringValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null)
                stringValues[i] = values[i].toString();
        }
        
        return createURIWithStringValues(scheme, userInfo, host, port, path, query, fragment,
                stringValues, encode);
    }
    
    /**
     * Construct a URI from the component parts each of which may contain 
     * template variables.
     *
     * @param scheme the URI scheme component
     * @param userInfo the URI user info component
     * @param host the URI host component
     * @param port the URI port component
     * @param path the URI path component
     * @param query the URI query componnet
     * @param fragment the URI fragment component
     * @param values the array of template values
     * @param encode if true encode a template value according to the correspond
     *        component type of the associated template variable, otherwise
     *        validate the template value
     * @return a URI
     */
    public final static String createURIWithStringValues(final String scheme, 
            final String userInfo, final String host, final String port, 
            final String path, final String query, final String fragment,
            final String[] values, final boolean encode) {
        
        final Map<String, String> mapValues = new HashMap<String, String>();
        final StringBuilder sb = new StringBuilder();
        int offset = 0;
        
        if (scheme != null) {
            offset = createURIComponent(UriComponent.Type.SCHEME, scheme, values, 
                    offset, false, mapValues, sb);
            sb.append(':');
        }
        
        if (userInfo != null || host != null || port != null) {
            sb.append("//");
            
            if (userInfo != null && userInfo.length() > 0) {
                offset = createURIComponent(UriComponent.Type.USER_INFO, userInfo, values, 
                        offset, encode, mapValues, sb);
                sb.append('@');
            }
            
            if (host != null) {
                // TODO check IPv6 address
                offset = createURIComponent(UriComponent.Type.HOST, host, values, 
                        offset, encode, mapValues, sb);
            }

            if (port != null && port.length() > 0) {
                sb.append(':');
                offset = createURIComponent(UriComponent.Type.PORT, port, values, 
                        offset, false, mapValues, sb);
            }
        }

        if (path != null)
            offset = createURIComponent(UriComponent.Type.PATH, path, values, 
                    offset, encode, mapValues, sb);
        
        if (query != null && query.length() > 0) {
            sb.append('?');
            offset = createURIComponent(UriComponent.Type.QUERY, query, values, 
                    offset, encode, mapValues, sb);
        }
         
        if (fragment != null && fragment.length() > 0) {
            sb.append('#');
            offset = createURIComponent(UriComponent.Type.FRAGMENT, path, values, 
                    offset, encode, mapValues, sb);
        }
        return sb.toString();
    }
    
    private static int createURIComponent(final UriComponent.Type t, 
            final String template,
            final String[] values, final int offset,
            final boolean encode, 
            final Map<String, String> mapValues,
            final StringBuilder b) {
        // Find all template variables
        final Matcher m = TEMPLATE_NAMES_PATTERN.matcher(template);
        int v = offset;
        int i = 0;
        while(m.find()) {
            b.append(template, i, m.start());
            final String tVariable = m.group(1);
            // Check if a template variable has already occurred
            // If so use the value to ensure that two or more declarations of 
            // a template variable have the same value
            String tValue = mapValues.get(tVariable);
            if (tValue != null) {
                b.append(tValue);
            } else {
                if (v < values.length) {
                    tValue = values[v++];
                    if (tValue != null) {
                        mapValues.put(tVariable, tValue);
                        if (encode)
                            tValue = UriComponent.encode(tValue, t);
                        else
                            UriComponent.validate(tValue, t);
                        b.append(tValue);
                    } else {
                        throw new IllegalArgumentException("The template variable, " + 
                                tVariable + ", has no value");                        
                    }
                }
            }
            i = m.end();
        }
        b.append(template, i, template.length());
        return v;
    }
}