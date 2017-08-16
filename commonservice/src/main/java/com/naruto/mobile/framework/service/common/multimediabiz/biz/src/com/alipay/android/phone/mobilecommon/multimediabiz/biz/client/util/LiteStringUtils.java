/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 由org.apache.commons.langs.StringUtils改造而来的的精简版LiteStringUtils
 * <p>Operations on {@link String} that are
 * <code>null</code> safe.</p>
 *
 * <ul>
 *  <li><b>IsEmpty/IsBlank</b>
 *      - checks if a String contains text</li>
 *  <li><b>Trim/Strip</b>
 *      - removes leading and trailing whitespace</li>
 *  <li><b>Equals</b>
 *      - compares two strings null-safe</li>
 *  <li><b>startsWith</b>
 *      - check if a String starts with a prefix null-safe</li>
 *  <li><b>endsWith</b>
 *      - check if a String ends with a suffix null-safe</li>
 *  <li><b>IndexOf/LastIndexOf/Contains</b>
 *      - null-safe index-of checks
 *  <li><b>IndexOfAny/LastIndexOfAny/IndexOfAnyBut/LastIndexOfAnyBut</b>
 *      - index-of any of a set of Strings</li>
 *  <li><b>Substring/Left/Right/Mid</b>
 *      - null-safe substring extractions</li>
 *  <li><b>SubstringBefore/SubstringAfter/SubstringBetween</b>
 *      - substring extraction relative to other strings</li>
 *  <li><b>Split/Join</b>
 *      - splits a String into an array of substrings and vice versa</li>
 *  <li><b>Remove/Delete</b>
 *      - removes part of a String</li>
 *  <li><b>Replace/Overlay</b>
 *      - Searches a String and replaces one String with another</li>
 *  <li><b>Chomp/Chop</b>
 *      - removes the last part of a String</li>
 *  <li><b>LeftPad/RightPad/Center/Repeat</b>
 *      - pads a String</li>
 *  <li><b>UpperCase/LowerCase/SwapCase/Capitalize/Uncapitalize</b>
 *      - changes the case of a String</li>
 *  <li><b>CountMatches</b>
 *      - counts the number of occurrences of one String in another</li>
 *  <li><b>IsAlpha/IsNumeric/IsWhitespace/IsAsciiPrintable</b>
 *      - checks the characters in a String</li>
 *  <li><b>DefaultString</b>
 *      - protects against a null input String</li>
 *  <li><b>Reverse/ReverseDelimited</b>
 *      - reverses a String</li>
 *  <li><b>Abbreviate</b>
 *      - abbreviates a string using ellipsis</li>
 *  <li><b>Difference</b>
 *      - compares Strings and reports on their differences</li>
 *  <li><b>LevensteinDistance</b>
 *      - the number of changes needed to change one String into another</li>
 * </ul>
 *
 * <p>The <code>LiteStringUtils</code> class defines certain words related to
 * String handling.</p>
 *
 * <ul>
 *  <li>null - <code>null</code></li>
 *  <li>empty - a zero-length string (<code>""</code>)</li>
 *  <li>space - the space character (<code>' '</code>, char 32)</li>
 *  <li>whitespace - the characters defined by {@link Character#isWhitespace(char)}</li>
 *  <li>trim - the characters &lt;= 32 as in {@link String#trim()}</li>
 * </ul>
 *
 * <p><code>LiteStringUtils</code> handles <code>null</code> input Strings quietly.
 * That is to say that a <code>null</code> input will return <code>null</code>.
 * Where a <code>boolean</code> or <code>int</code> is being returned
 * details vary by method.</p>
 *
 * <p>A side effect of the <code>null</code> handling is that a
 * <code>NullPointerException</code> should be considered a bug in
 * <code>LiteStringUtils</code> (except for deprecated methods).</p>
 *
 * <p>Methods in this class give sample code to explain their operation.
 * The symbol <code>*</code> is used to indicate any input including <code>null</code>.</p>
 *
 * @see String
 * @author <a href="http://jakarta.apache.org/turbine/">Apache Jakarta Turbine</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author Daniel L. Rall
 * @author <a href="mailto:gcoladonato@yahoo.com">Greg Coladonato</a>
 * @author <a href="mailto:ed@apache.org">Ed Korthof</a>
 * @author <a href="mailto:rand_mcneely@yahoo.com">Rand McNeely</a>
 * @author Stephen Colebourne
 * @author <a href="mailto:fredrik@westermarck.com">Fredrik Westermarck</a>
 * @author Holger Krauth
 * @author <a href="mailto:alex@purpletech.com">Alexander Day Chaffee</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author Arun Mammen Thomas
 * @author Gary Gregory
 * @author Phil Steitz
 * @author Al Chou
 * @author Michael Davey
 * @author Reuben Sivan
 * @author Chris Hyzer
 * @author Scott Johnson
 * @author jinzhaoyu
 * @since 1.0
 * @version $Id: LiteStringUtils.java 635447 2008-03-10 06:27:09Z bayard $
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class LiteStringUtils {
    // Performance testing notes (JDK 1.4, Jul03, scolebourne)
    // Whitespace:
    // Character.isWhitespace() is faster than WHITESPACE.indexOf()
    // where WHITESPACE is a string of all whitespace characters
    //
    // Character access:
    // String.charAt(n) versus toCharArray(), then array[n]
    // String.charAt(n) is about 15% worse for a 10K string
    // They are about equal for a length 50 string
    // String.charAt(n) is about 4 times better for a length 3 string
    // String.charAt(n) is best bet overall
    //
    // Append:
    // String.concat about twice as fast as StringBuffer.append
    // (not sure who tested this)

    /**
     * The empty String <code>""</code>.
     * @since 2.0
     */
    public static final String EMPTY = "";

    /**
     * Represents a failed index search.
     * @since 2.1
     */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * <p>The maximum size to which the padding constant(s) can expand.</p>
     */
    private static final int PAD_LIMIT = 8192;
    
    /**
     * An empty immutable <code>String</code> array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * <p><code>LiteStringUtils</code> instances should NOT be constructed in
     * standard programming. Instead, the class should be used as
     * <code>LiteStringUtils.trim(" foo ");</code>.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public LiteStringUtils() {
        super();
    }

    // Empty checks
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * LiteStringUtils.isEmpty(null)      = true
     * LiteStringUtils.isEmpty("")        = true
     * LiteStringUtils.isEmpty(" ")       = false
     * LiteStringUtils.isEmpty("bob")     = false
     * LiteStringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>Checks if a String is not empty ("") and not null.</p>
     *
     * <pre>
     * LiteStringUtils.isNotEmpty(null)      = false
     * LiteStringUtils.isNotEmpty("")        = false
     * LiteStringUtils.isNotEmpty(" ")       = true
     * LiteStringUtils.isNotEmpty("bob")     = true
     * LiteStringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null
     */
    public static boolean isNotEmpty(String str) {
        return !LiteStringUtils.isEmpty(str);
    }

    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * LiteStringUtils.isBlank(null)      = true
     * LiteStringUtils.isBlank("")        = true
     * LiteStringUtils.isBlank(" ")       = true
     * LiteStringUtils.isBlank("bob")     = false
     * LiteStringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * LiteStringUtils.isNotBlank(null)      = false
     * LiteStringUtils.isNotBlank("")        = false
     * LiteStringUtils.isNotBlank(" ")       = false
     * LiteStringUtils.isNotBlank("bob")     = true
     * LiteStringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is
     *  not empty and not null and not whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
        return !LiteStringUtils.isBlank(str);
    }

    // Trim
    //-----------------------------------------------------------------------
    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String, handling <code>null</code> by returning
     * an empty String ("").</p>
     *
     * <pre>
     * LiteStringUtils.clean(null)          = ""
     * LiteStringUtils.clean("")            = ""
     * LiteStringUtils.clean("abc")         = "abc"
     * LiteStringUtils.clean("    abc    ") = "abc"
     * LiteStringUtils.clean("     ")       = ""
     * </pre>
     *
     * @see String#trim()
     * @param str  the String to clean, may be null
     * @return the trimmed text, never <code>null</code>
     * @deprecated Use the clearer named {@link #trimToEmpty(String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String clean(String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String, handling <code>null</code> by returning
     * <code>null</code>.</p>
     *
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #strip(String)}.</p>
     *
     * <p>To trim your choice of characters, use the
     * {@link #strip(String, String)} methods.</p>
     *
     * <pre>
     * LiteStringUtils.trim(null)          = null
     * LiteStringUtils.trim("")            = ""
     * LiteStringUtils.trim("     ")       = ""
     * LiteStringUtils.trim("abc")         = "abc"
     * LiteStringUtils.trim("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed string, <code>null</code> if null String input
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String returning <code>null</code> if the String is
     * empty ("") after the trim or if it is <code>null</code>.
     *
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #stripToNull(String)}.</p>
     *
     * <pre>
     * LiteStringUtils.trimToNull(null)          = null
     * LiteStringUtils.trimToNull("")            = null
     * LiteStringUtils.trimToNull("     ")       = null
     * LiteStringUtils.trimToNull("abc")         = "abc"
     * LiteStringUtils.trimToNull("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed String,
     *  <code>null</code> if only chars &lt;= 32, empty or null String input
     * @since 2.0
     */
    public static String trimToNull(String str) {
        String ts = trim(str);
        return isEmpty(ts) ? null : ts;
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String returning an empty String ("") if the String
     * is empty ("") after the trim or if it is <code>null</code>.
     *
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #stripToEmpty(String)}.</p>
     *
     * <pre>
     * LiteStringUtils.trimToEmpty(null)          = ""
     * LiteStringUtils.trimToEmpty("")            = ""
     * LiteStringUtils.trimToEmpty("     ")       = ""
     * LiteStringUtils.trimToEmpty("abc")         = "abc"
     * LiteStringUtils.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed String, or an empty String if <code>null</code> input
     * @since 2.0
     */
    public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }

    // Stripping
    //-----------------------------------------------------------------------
    /**
     * <p>Strips whitespace from the start and end of a String.</p>
     *
     * <p>This is similar to {@link #trim(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.strip(null)     = null
     * LiteStringUtils.strip("")       = ""
     * LiteStringUtils.strip("   ")    = ""
     * LiteStringUtils.strip("abc")    = "abc"
     * LiteStringUtils.strip("  abc")  = "abc"
     * LiteStringUtils.strip("abc  ")  = "abc"
     * LiteStringUtils.strip(" abc ")  = "abc"
     * LiteStringUtils.strip(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  the String to remove whitespace from, may be null
     * @return the stripped String, <code>null</code> if null String input
     */
    public static String strip(String str) {
        return strip(str, null);
    }

    /**
     * <p>Strips whitespace from the start and end of a String  returning
     * <code>null</code> if the String is empty ("") after the strip.</p>
     *
     * <p>This is similar to {@link #trimToNull(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * LiteStringUtils.stripToNull(null)     = null
     * LiteStringUtils.stripToNull("")       = null
     * LiteStringUtils.stripToNull("   ")    = null
     * LiteStringUtils.stripToNull("abc")    = "abc"
     * LiteStringUtils.stripToNull("  abc")  = "abc"
     * LiteStringUtils.stripToNull("abc  ")  = "abc"
     * LiteStringUtils.stripToNull(" abc ")  = "abc"
     * LiteStringUtils.stripToNull(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  the String to be stripped, may be null
     * @return the stripped String,
     *  <code>null</code> if whitespace, empty or null String input
     * @since 2.0
     */
    public static String stripToNull(String str) {
        if (str == null) {
            return null;
        }
        str = strip(str, null);
        return str.length() == 0 ? null : str;
    }

    /**
     * <p>Strips whitespace from the start and end of a String  returning
     * an empty String if <code>null</code> input.</p>
     *
     * <p>This is similar to {@link #trimToEmpty(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * LiteStringUtils.stripToEmpty(null)     = ""
     * LiteStringUtils.stripToEmpty("")       = ""
     * LiteStringUtils.stripToEmpty("   ")    = ""
     * LiteStringUtils.stripToEmpty("abc")    = "abc"
     * LiteStringUtils.stripToEmpty("  abc")  = "abc"
     * LiteStringUtils.stripToEmpty("abc  ")  = "abc"
     * LiteStringUtils.stripToEmpty(" abc ")  = "abc"
     * LiteStringUtils.stripToEmpty(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  the String to be stripped, may be null
     * @return the trimmed String, or an empty String if <code>null</code> input
     * @since 2.0
     */
    public static String stripToEmpty(String str) {
        return str == null ? EMPTY : strip(str, null);
    }

    /**
     * <p>Strips any of a set of characters from the start and end of a String.
     * This is similar to {@link String#trim()} but allows the characters
     * to be stripped to be controlled.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * An empty string ("") input returns the empty string.</p>
     *
     * <p>If the stripChars String is <code>null</code>, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.
     * Alternatively use {@link #strip(String)}.</p>
     *
     * <pre>
     * LiteStringUtils.strip(null, *)          = null
     * LiteStringUtils.strip("", *)            = ""
     * LiteStringUtils.strip("abc", null)      = "abc"
     * LiteStringUtils.strip("  abc", null)    = "abc"
     * LiteStringUtils.strip("abc  ", null)    = "abc"
     * LiteStringUtils.strip(" abc ", null)    = "abc"
     * LiteStringUtils.strip("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped String, <code>null</code> if null String input
     */
    public static String strip(String str, String stripChars) {
        if (isEmpty(str)) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    /**
     * <p>Strips any of a set of characters from the start of a String.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * An empty string ("") input returns the empty string.</p>
     *
     * <p>If the stripChars String is <code>null</code>, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * LiteStringUtils.stripStart(null, *)          = null
     * LiteStringUtils.stripStart("", *)            = ""
     * LiteStringUtils.stripStart("abc", "")        = "abc"
     * LiteStringUtils.stripStart("abc", null)      = "abc"
     * LiteStringUtils.stripStart("  abc", null)    = "abc"
     * LiteStringUtils.stripStart("abc  ", null)    = "abc  "
     * LiteStringUtils.stripStart(" abc ", null)    = "abc "
     * LiteStringUtils.stripStart("yxabc  ", "xyz") = "abc  "
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped String, <code>null</code> if null String input
     */
    public static String stripStart(String str, String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while ((start != strLen) && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != -1)) {
                start++;
            }
        }
        return str.substring(start);
    }

    /**
     * <p>Strips any of a set of characters from the end of a String.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * An empty string ("") input returns the empty string.</p>
     *
     * <p>If the stripChars String is <code>null</code>, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * LiteStringUtils.stripEnd(null, *)          = null
     * LiteStringUtils.stripEnd("", *)            = ""
     * LiteStringUtils.stripEnd("abc", "")        = "abc"
     * LiteStringUtils.stripEnd("abc", null)      = "abc"
     * LiteStringUtils.stripEnd("  abc", null)    = "  abc"
     * LiteStringUtils.stripEnd("abc  ", null)    = "abc"
     * LiteStringUtils.stripEnd(" abc ", null)    = " abc"
     * LiteStringUtils.stripEnd("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped String, <code>null</code> if null String input
     */
    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        if (stripChars == null) {
            while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    // StripAll
    //-----------------------------------------------------------------------
    /**
     * <p>Strips whitespace from the start and end of every String in an array.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>A new array is returned each time, except for length zero.
     * A <code>null</code> array will return <code>null</code>.
     * An empty array will return itself.
     * A <code>null</code> array entry will be ignored.</p>
     *
     * <pre>
     * LiteStringUtils.stripAll(null)             = null
     * LiteStringUtils.stripAll([])               = []
     * LiteStringUtils.stripAll(["abc", "  abc"]) = ["abc", "abc"]
     * LiteStringUtils.stripAll(["abc  ", null])  = ["abc", null]
     * </pre>
     *
     * @param strs  the array to remove whitespace from, may be null
     * @return the stripped Strings, <code>null</code> if null array input
     */
    public static String[] stripAll(String[] strs) {
        return stripAll(strs, null);
    }

    /**
     * <p>Strips any of a set of characters from the start and end of every
     * String in an array.</p>
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>A new array is returned each time, except for length zero.
     * A <code>null</code> array will return <code>null</code>.
     * An empty array will return itself.
     * A <code>null</code> array entry will be ignored.
     * A <code>null</code> stripChars will strip whitespace as defined by
     * {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * LiteStringUtils.stripAll(null, *)                = null
     * LiteStringUtils.stripAll([], *)                  = []
     * LiteStringUtils.stripAll(["abc", "  abc"], null) = ["abc", "abc"]
     * LiteStringUtils.stripAll(["abc  ", null], null)  = ["abc", null]
     * LiteStringUtils.stripAll(["abc  ", null], "yz")  = ["abc  ", null]
     * LiteStringUtils.stripAll(["yabcz", null], "yz")  = ["abc", null]
     * </pre>
     *
     * @param strs  the array to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped Strings, <code>null</code> if null array input
     */
    public static String[] stripAll(String[] strs, String stripChars) {
        int strsLen;
        if (strs == null || (strsLen = strs.length) == 0) {
            return strs;
        }
        String[] newArr = new String[strsLen];
        for (int i = 0; i < strsLen; i++) {
            newArr[i] = strip(strs[i], stripChars);
        }
        return newArr;
    }

    // Equals
    //-----------------------------------------------------------------------
    /**
     * <p>Compares two Strings, returning <code>true</code> if they are equal.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * LiteStringUtils.equals(null, null)   = true
     * LiteStringUtils.equals(null, "abc")  = false
     * LiteStringUtils.equals("abc", null)  = false
     * LiteStringUtils.equals("abc", "abc") = true
     * LiteStringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @see String#equals(Object)
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return <code>true</code> if the Strings are equal, case sensitive, or
     *  both <code>null</code>
     */
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    /**
     * <p>Compares two Strings, returning <code>true</code> if they are equal ignoring
     * the case.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered equal. Comparison is case insensitive.</p>
     *
     * <pre>
     * LiteStringUtils.equalsIgnoreCase(null, null)   = true
     * LiteStringUtils.equalsIgnoreCase(null, "abc")  = false
     * LiteStringUtils.equalsIgnoreCase("abc", null)  = false
     * LiteStringUtils.equalsIgnoreCase("abc", "abc") = true
     * LiteStringUtils.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     *
     * @see String#equalsIgnoreCase(String)
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return <code>true</code> if the Strings are equal, case insensitive, or
     *  both <code>null</code>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }

    // IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(int)}.</p>
     *
     * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.</p>
     *
     * <pre>
     * LiteStringUtils.indexOf(null, *)         = -1
     * LiteStringUtils.indexOf("", *)           = -1
     * LiteStringUtils.indexOf("aabaabaa", 'a') = 0
     * LiteStringUtils.indexOf("aabaabaa", 'b') = 2
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @return the first index of the search character,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, char searchChar) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar);
    }

    /**
     * <p>Finds the first index within a String from a start position,
     * handling <code>null</code>.
     * This method uses {@link String#indexOf(int, int)}.</p>
     *
     * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.
     * A negative start position is treated as zero.
     * A start position greater than the string length returns <code>-1</code>.</p>
     *
     * <pre>
     * LiteStringUtils.indexOf(null, *, *)          = -1
     * LiteStringUtils.indexOf("", *, *)            = -1
     * LiteStringUtils.indexOf("aabaabaa", 'b', 0)  = 2
     * LiteStringUtils.indexOf("aabaabaa", 'b', 3)  = 5
     * LiteStringUtils.indexOf("aabaabaa", 'b', 9)  = -1
     * LiteStringUtils.indexOf("aabaabaa", 'b', -1) = 2
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search character,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar, startPos);
    }

    /**
     * <p>Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String)}.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.</p>
     *
     * <pre>
     * LiteStringUtils.indexOf(null, *)          = -1
     * LiteStringUtils.indexOf(*, null)          = -1
     * LiteStringUtils.indexOf("", "")           = 0
     * LiteStringUtils.indexOf("aabaabaa", "a")  = 0
     * LiteStringUtils.indexOf("aabaabaa", "b")  = 2
     * LiteStringUtils.indexOf("aabaabaa", "ab") = 1
     * LiteStringUtils.indexOf("aabaabaa", "")   = 0
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.indexOf(searchStr);
    }

    /**
     * <p>Finds the n-th index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String)}.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.</p>
     *
     * <pre>
     * LiteStringUtils.ordinalIndexOf(null, *, *)          = -1
     * LiteStringUtils.ordinalIndexOf(*, null, *)          = -1
     * LiteStringUtils.ordinalIndexOf("", "", *)           = 0
     * LiteStringUtils.ordinalIndexOf("aabaabaa", "a", 1)  = 0
     * LiteStringUtils.ordinalIndexOf("aabaabaa", "a", 2)  = 1
     * LiteStringUtils.ordinalIndexOf("aabaabaa", "b", 1)  = 2
     * LiteStringUtils.ordinalIndexOf("aabaabaa", "b", 2)  = 5
     * LiteStringUtils.ordinalIndexOf("aabaabaa", "ab", 1) = 1
     * LiteStringUtils.ordinalIndexOf("aabaabaa", "ab", 2) = 4
     * LiteStringUtils.ordinalIndexOf("aabaabaa", "", 1)   = 0
     * LiteStringUtils.ordinalIndexOf("aabaabaa", "", 2)   = 0
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param ordinal  the n-th <code>searchStr</code> to find
     * @return the n-th index of the search String,
     *  <code>-1</code> (<code>INDEX_NOT_FOUND</code>) if no match or <code>null</code> string input
     * @since 2.1
     */
    public static int ordinalIndexOf(String str, String searchStr, int ordinal) {
        if (str == null || searchStr == null || ordinal <= 0) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return 0;
        }
        int found = 0;
        int index = INDEX_NOT_FOUND;
        do {
            index = str.indexOf(searchStr, index + 1);
            if (index < 0) {
                return index;
            }
            found++;
        } while (found < ordinal);
        return index;
    }

    /**
     * <p>Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String, int)}.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.
     * A negative start position is treated as zero.
     * An empty ("") search String always matches.
     * A start position greater than the string length only matches
     * an empty search String.</p>
     *
     * <pre>
     * LiteStringUtils.indexOf(null, *, *)          = -1
     * LiteStringUtils.indexOf(*, null, *)          = -1
     * LiteStringUtils.indexOf("", "", 0)           = 0
     * LiteStringUtils.indexOf("aabaabaa", "a", 0)  = 0
     * LiteStringUtils.indexOf("aabaabaa", "b", 0)  = 2
     * LiteStringUtils.indexOf("aabaabaa", "ab", 0) = 1
     * LiteStringUtils.indexOf("aabaabaa", "b", 3)  = 5
     * LiteStringUtils.indexOf("aabaabaa", "b", 9)  = -1
     * LiteStringUtils.indexOf("aabaabaa", "b", -1) = 2
     * LiteStringUtils.indexOf("aabaabaa", "", 2)   = 2
     * LiteStringUtils.indexOf("abc", "", 9)        = 3
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        // JDK1.2/JDK1.3 have a bug, when startPos > str.length for "", hence
        if (searchStr.length() == 0 && startPos >= str.length()) {
            return str.length();
        }
        return str.indexOf(searchStr, startPos);
    }

    // LastIndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the last index within a String, handling <code>null</code>.
     * This method uses {@link String#lastIndexOf(int)}.</p>
     *
     * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.</p>
     *
     * <pre>
     * LiteStringUtils.lastIndexOf(null, *)         = -1
     * LiteStringUtils.lastIndexOf("", *)           = -1
     * LiteStringUtils.lastIndexOf("aabaabaa", 'a') = 7
     * LiteStringUtils.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @return the last index of the search character,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, char searchChar) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.lastIndexOf(searchChar);
    }

    /**
     * <p>Finds the last index within a String from a start position,
     * handling <code>null</code>.
     * This method uses {@link String#lastIndexOf(int, int)}.</p>
     *
     * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.
     * A negative start position returns <code>-1</code>.
     * A start position greater than the string length searches the whole string.</p>
     *
     * <pre>
     * LiteStringUtils.lastIndexOf(null, *, *)          = -1
     * LiteStringUtils.lastIndexOf("", *,  *)           = -1
     * LiteStringUtils.lastIndexOf("aabaabaa", 'b', 8)  = 5
     * LiteStringUtils.lastIndexOf("aabaabaa", 'b', 4)  = 2
     * LiteStringUtils.lastIndexOf("aabaabaa", 'b', 0)  = -1
     * LiteStringUtils.lastIndexOf("aabaabaa", 'b', 9)  = 5
     * LiteStringUtils.lastIndexOf("aabaabaa", 'b', -1) = -1
     * LiteStringUtils.lastIndexOf("aabaabaa", 'a', 0)  = 0
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @param startPos  the start position
     * @return the last index of the search character,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.lastIndexOf(searchChar, startPos);
    }

    /**
     * <p>Finds the last index within a String, handling <code>null</code>.
     * This method uses {@link String#lastIndexOf(String)}.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.</p>
     *
     * <pre>
     * LiteStringUtils.lastIndexOf(null, *)          = -1
     * LiteStringUtils.lastIndexOf(*, null)          = -1
     * LiteStringUtils.lastIndexOf("", "")           = 0
     * LiteStringUtils.lastIndexOf("aabaabaa", "a")  = 0
     * LiteStringUtils.lastIndexOf("aabaabaa", "b")  = 2
     * LiteStringUtils.lastIndexOf("aabaabaa", "ab") = 1
     * LiteStringUtils.lastIndexOf("aabaabaa", "")   = 8
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return the last index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr);
    }

    /**
     * <p>Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#lastIndexOf(String, int)}.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.
     * A negative start position returns <code>-1</code>.
     * An empty ("") search String always matches unless the start position is negative.
     * A start position greater than the string length searches the whole string.</p>
     *
     * <pre>
     * LiteStringUtils.lastIndexOf(null, *, *)          = -1
     * LiteStringUtils.lastIndexOf(*, null, *)          = -1
     * LiteStringUtils.lastIndexOf("aabaabaa", "a", 8)  = 7
     * LiteStringUtils.lastIndexOf("aabaabaa", "b", 8)  = 5
     * LiteStringUtils.lastIndexOf("aabaabaa", "ab", 8) = 4
     * LiteStringUtils.lastIndexOf("aabaabaa", "b", 9)  = 5
     * LiteStringUtils.lastIndexOf("aabaabaa", "b", -1) = -1
     * LiteStringUtils.lastIndexOf("aabaabaa", "a", 0)  = 0
     * LiteStringUtils.lastIndexOf("aabaabaa", "b", 0)  = -1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr, startPos);
    }

    // Contains
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if String contains a search character, handling <code>null</code>.
     * This method uses {@link String#indexOf(int)}.</p>
     *
     * <p>A <code>null</code> or empty ("") String will return <code>false</code>.</p>
     *
     * <pre>
     * LiteStringUtils.contains(null, *)    = false
     * LiteStringUtils.contains("", *)      = false
     * LiteStringUtils.contains("abc", 'a') = true
     * LiteStringUtils.contains("abc", 'z') = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @return true if the String contains the search character,
     *  false if not or <code>null</code> string input
     * @since 2.0
     */
    public static boolean contains(String str, char searchChar) {
        if (isEmpty(str)) {
            return false;
        }
        return str.indexOf(searchChar) >= 0;
    }

    /**
     * <p>Checks if String contains a search String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String)}.</p>
     *
     * <p>A <code>null</code> String will return <code>false</code>.</p>
     *
     * <pre>
     * LiteStringUtils.contains(null, *)     = false
     * LiteStringUtils.contains(*, null)     = false
     * LiteStringUtils.contains("", "")      = true
     * LiteStringUtils.contains("abc", "")   = true
     * LiteStringUtils.contains("abc", "a")  = true
     * LiteStringUtils.contains("abc", "z")  = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return true if the String contains the search String,
     *  false if not or <code>null</code> string input
     * @since 2.0
     */
    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.indexOf(searchStr) >= 0;
    }

    /**
     * <p>Checks if String contains a search String irrespective of case,
     * handling <code>null</code>. This method uses
     * {@link #contains(String, String)}.</p>
     *
     * <p>A <code>null</code> String will return <code>false</code>.</p>
     *
     * <pre>
     * LiteStringUtils.contains(null, *) = false
     * LiteStringUtils.contains(*, null) = false
     * LiteStringUtils.contains("", "") = true
     * LiteStringUtils.contains("abc", "") = true
     * LiteStringUtils.contains("abc", "a") = true
     * LiteStringUtils.contains("abc", "z") = false
     * LiteStringUtils.contains("abc", "A") = true
     * LiteStringUtils.contains("abc", "Z") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return true if the String contains the search String irrespective of
     * case or false if not or <code>null</code> string input
     */
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return contains(str.toUpperCase(Locale.getDefault()), searchStr.toUpperCase());
    }

    // ContainsAny
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if the String contains any character in the given
     * set of characters.</p>
     *
     * <p>A <code>null</code> String will return <code>false</code>.
     * A <code>null</code> or zero length search array will return <code>false</code>.</p>
     *
     * <pre>
     * LiteStringUtils.containsAny(null, *)                = false
     * LiteStringUtils.containsAny("", *)                  = false
     * LiteStringUtils.containsAny(*, null)                = false
     * LiteStringUtils.containsAny(*, [])                  = false
     * LiteStringUtils.containsAny("zzabyycdxx",['z','a']) = true
     * LiteStringUtils.containsAny("zzabyycdxx",['b','y']) = true
     * LiteStringUtils.containsAny("aba", ['z'])           = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the <code>true</code> if any of the chars are found,
     * <code>false</code> if no match or null input
     * @since 2.4
     */
    public static boolean containsAny(String str, char[] searchChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * Checks if the String contains any character in the given set of characters.
     * </p>
     * 
     * <p>
     * A <code>null</code> String will return <code>false</code>. A <code>null</code> search string will return
     * <code>false</code>.
     * </p>
     * 
     * <pre>
     * LiteStringUtils.containsAny(null, *)            = false
     * LiteStringUtils.containsAny("", *)              = false
     * LiteStringUtils.containsAny(*, null)            = false
     * LiteStringUtils.containsAny(*, "")              = false
     * LiteStringUtils.containsAny("zzabyycdxx", "za") = true
     * LiteStringUtils.containsAny("zzabyycdxx", "by") = true
     * LiteStringUtils.containsAny("aba","z")          = false
     * </pre>
     * 
     * @param str
     *            the String to check, may be null
     * @param searchChars
     *            the chars to search for, may be null
     * @return the <code>true</code> if any of the chars are found, <code>false</code> if no match or null input
     * @since 2.4
     */
    public static boolean containsAny(String str, String searchChars) {
        if (searchChars == null) {
            return false;
        }
        return containsAny(str, searchChars.toCharArray());
    }

    /**
     * <p>Search a String to find the first index of any
     * character not in the given set of characters.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.
     * A <code>null</code> search string will return <code>-1</code>.</p>
     *
     * <pre>
     * LiteStringUtils.indexOfAnyBut(null, *)            = -1
     * LiteStringUtils.indexOfAnyBut("", *)              = -1
     * LiteStringUtils.indexOfAnyBut(*, null)            = -1
     * LiteStringUtils.indexOfAnyBut(*, "")              = -1
     * LiteStringUtils.indexOfAnyBut("zzabyycdxx", "za") = 3
     * LiteStringUtils.indexOfAnyBut("zzabyycdxx", "")   = 0
     * LiteStringUtils.indexOfAnyBut("aba","ab")         = -1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * @since 2.0
     */
    public static int indexOfAnyBut(String str, String searchChars) {
        if (isEmpty(str) || isEmpty(searchChars)) {
            return -1;
        }
        for (int i = 0; i < str.length(); i++) {
            if (searchChars.indexOf(str.charAt(i)) < 0) {
                return i;
            }
        }
        return -1;
    }

    // ContainsNone
    //-----------------------------------------------------------------------
    /**
     * <p>Checks that the String does not contain certain characters.</p>
     *
     * <p>A <code>null</code> String will return <code>true</code>.
     * A <code>null</code> invalid character array will return <code>true</code>.
     * An empty String ("") always returns true.</p>
     *
     * <pre>
     * LiteStringUtils.containsNone(null, *)       = true
     * LiteStringUtils.containsNone(*, null)       = true
     * LiteStringUtils.containsNone("", *)         = true
     * LiteStringUtils.containsNone("ab", '')      = true
     * LiteStringUtils.containsNone("abab", 'xyz') = true
     * LiteStringUtils.containsNone("ab1", 'xyz')  = true
     * LiteStringUtils.containsNone("abz", 'xyz')  = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param invalidChars  an array of invalid chars, may be null
     * @return true if it contains none of the invalid chars, or is null
     * @since 2.0
     */
    public static boolean containsNone(String str, char[] invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }
        int strSize = str.length();
        int validSize = invalidChars.length;
        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < validSize; j++) {
                if (invalidChars[j] == ch) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * <p>Checks that the String does not contain certain characters.</p>
     *
     * <p>A <code>null</code> String will return <code>true</code>.
     * A <code>null</code> invalid character array will return <code>true</code>.
     * An empty String ("") always returns true.</p>
     *
     * <pre>
     * LiteStringUtils.containsNone(null, *)       = true
     * LiteStringUtils.containsNone(*, null)       = true
     * LiteStringUtils.containsNone("", *)         = true
     * LiteStringUtils.containsNone("ab", "")      = true
     * LiteStringUtils.containsNone("abab", "xyz") = true
     * LiteStringUtils.containsNone("ab1", "xyz")  = true
     * LiteStringUtils.containsNone("abz", "xyz")  = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param invalidChars  a String of invalid chars, may be null
     * @return true if it contains none of the invalid chars, or is null
     * @since 2.0
     */
    public static boolean containsNone(String str, String invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }
        return containsNone(str, invalidChars.toCharArray());
    }

    // IndexOfAny strings
    //-----------------------------------------------------------------------
    /**
     * <p>Find the first index of any of a set of potential substrings.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.
     * A <code>null</code> or zero length search array will return <code>-1</code>.
     * A <code>null</code> search array entry will be ignored, but a search
     * array containing "" will return <code>0</code> if <code>str</code> is not
     * null. This method uses {@link String#indexOf(String)}.</p>
     *
     * <pre>
     * LiteStringUtils.indexOfAny(null, *)                     = -1
     * LiteStringUtils.indexOfAny(*, null)                     = -1
     * LiteStringUtils.indexOfAny(*, [])                       = -1
     * LiteStringUtils.indexOfAny("zzabyycdxx", ["ab","cd"])   = 2
     * LiteStringUtils.indexOfAny("zzabyycdxx", ["cd","ab"])   = 2
     * LiteStringUtils.indexOfAny("zzabyycdxx", ["mn","op"])   = -1
     * LiteStringUtils.indexOfAny("zzabyycdxx", ["zab","aby"]) = 1
     * LiteStringUtils.indexOfAny("zzabyycdxx", [""])          = 0
     * LiteStringUtils.indexOfAny("", [""])                    = 0
     * LiteStringUtils.indexOfAny("", ["a"])                   = -1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStrs  the Strings to search for, may be null
     * @return the first index of any of the searchStrs in str, -1 if no match
     */
    public static int indexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }
        int sz = searchStrs.length;

        // String's can't have a MAX_VALUEth index.
        int ret = Integer.MAX_VALUE;

        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];
            if (search == null) {
                continue;
            }
            tmp = str.indexOf(search);
            if (tmp == -1) {
                continue;
            }

            if (tmp < ret) {
                ret = tmp;
            }
        }

        return (ret == Integer.MAX_VALUE) ? -1 : ret;
    }

    /**
     * <p>Find the latest index of any of a set of potential substrings.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.
     * A <code>null</code> search array will return <code>-1</code>.
     * A <code>null</code> or zero length search array entry will be ignored,
     * but a search array containing "" will return the length of <code>str</code>
     * if <code>str</code> is not null. This method uses {@link String#indexOf(String)}</p>
     *
     * <pre>
     * LiteStringUtils.lastIndexOfAny(null, *)                   = -1
     * LiteStringUtils.lastIndexOfAny(*, null)                   = -1
     * LiteStringUtils.lastIndexOfAny(*, [])                     = -1
     * LiteStringUtils.lastIndexOfAny(*, [null])                 = -1
     * LiteStringUtils.lastIndexOfAny("zzabyycdxx", ["ab","cd"]) = 6
     * LiteStringUtils.lastIndexOfAny("zzabyycdxx", ["cd","ab"]) = 6
     * LiteStringUtils.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * LiteStringUtils.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * LiteStringUtils.lastIndexOfAny("zzabyycdxx", ["mn",""])   = 10
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStrs  the Strings to search for, may be null
     * @return the last index of any of the Strings, -1 if no match
     */
    public static int lastIndexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }
        int sz = searchStrs.length;
        int ret = -1;
        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];
            if (search == null) {
                continue;
            }
            tmp = str.lastIndexOf(search);
            if (tmp > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    // Substring
    //-----------------------------------------------------------------------
    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     *
     * <p>A negative start position can be used to start <code>n</code>
     * characters from the end of the String.</p>
     *
     * <p>A <code>null</code> String will return <code>null</code>.
     * An empty ("") String will return "".</p>
     *
     * <pre>
     * LiteStringUtils.substring(null, *)   = null
     * LiteStringUtils.substring("", *)     = ""
     * LiteStringUtils.substring("abc", 0)  = "abc"
     * LiteStringUtils.substring("abc", 2)  = "c"
     * LiteStringUtils.substring("abc", 4)  = ""
     * LiteStringUtils.substring("abc", -2) = "bc"
     * LiteStringUtils.substring("abc", -4) = "abc"
     * </pre>
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     *  count back from the end of the String by this many characters
     * @return substring from start position, <code>null</code> if null String input
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }

        // handle negatives, which means last n characters
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY;
        }

        return str.substring(start);
    }

    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     *
     * <p>A negative start position can be used to start/end <code>n</code>
     * characters from the end of the String.</p>
     *
     * <p>The returned substring starts with the character in the <code>start</code>
     * position and ends before the <code>end</code> position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * <code>start = 0</code>. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.</p>
     *
     * <p>If <code>start</code> is not strictly to the left of <code>end</code>, ""
     * is returned.</p>
     *
     * <pre>
     * LiteStringUtils.substring(null, *, *)    = null
     * LiteStringUtils.substring("", * ,  *)    = "";
     * LiteStringUtils.substring("abc", 0, 2)   = "ab"
     * LiteStringUtils.substring("abc", 2, 0)   = ""
     * LiteStringUtils.substring("abc", 2, 4)   = "c"
     * LiteStringUtils.substring("abc", 4, 6)   = ""
     * LiteStringUtils.substring("abc", 2, 2)   = ""
     * LiteStringUtils.substring("abc", -2, -1) = "b"
     * LiteStringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     *  count back from the end of the String by this many characters
     * @param end  the position to end at (exclusive), negative means
     *  count back from the end of the String by this many characters
     * @return substring from start position to end positon,
     *  <code>null</code> if null String input
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return EMPTY;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    // Left/Right/Mid
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the leftmost <code>len</code> characters of a String.</p>
     *
     * <p>If <code>len</code> characters are not available, or the
     * String is <code>null</code>, the String will be returned without
     * an exception. An exception is thrown if len is negative.</p>
     *
     * <pre>
     * LiteStringUtils.left(null, *)    = null
     * LiteStringUtils.left(*, -ve)     = ""
     * LiteStringUtils.left("", *)      = ""
     * LiteStringUtils.left("abc", 0)   = ""
     * LiteStringUtils.left("abc", 2)   = "ab"
     * LiteStringUtils.left("abc", 4)   = "abc"
     * </pre>
     *
     * @param str  the String to get the leftmost characters from, may be null
     * @param len  the length of the required String, must be zero or positive
     * @return the leftmost characters, <code>null</code> if null String input
     */
    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * <p>Gets the rightmost <code>len</code> characters of a String.</p>
     *
     * <p>If <code>len</code> characters are not available, or the String
     * is <code>null</code>, the String will be returned without an
     * an exception. An exception is thrown if len is negative.</p>
     *
     * <pre>
     * LiteStringUtils.right(null, *)    = null
     * LiteStringUtils.right(*, -ve)     = ""
     * LiteStringUtils.right("", *)      = ""
     * LiteStringUtils.right("abc", 0)   = ""
     * LiteStringUtils.right("abc", 2)   = "bc"
     * LiteStringUtils.right("abc", 4)   = "abc"
     * </pre>
     *
     * @param str  the String to get the rightmost characters from, may be null
     * @param len  the length of the required String, must be zero or positive
     * @return the rightmost characters, <code>null</code> if null String input
     */
    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    /**
     * <p>Gets <code>len</code> characters from the middle of a String.</p>
     *
     * <p>If <code>len</code> characters are not available, the remainder
     * of the String will be returned without an exception. If the
     * String is <code>null</code>, <code>null</code> will be returned.
     * An exception is thrown if len is negative.</p>
     *
     * <pre>
     * LiteStringUtils.mid(null, *, *)    = null
     * LiteStringUtils.mid(*, *, -ve)     = ""
     * LiteStringUtils.mid("", 0, *)      = ""
     * LiteStringUtils.mid("abc", 0, 2)   = "ab"
     * LiteStringUtils.mid("abc", 0, 4)   = "abc"
     * LiteStringUtils.mid("abc", 2, 4)   = "c"
     * LiteStringUtils.mid("abc", 4, 2)   = ""
     * LiteStringUtils.mid("abc", -2, 2)  = "ab"
     * </pre>
     *
     * @param str  the String to get the characters from, may be null
     * @param pos  the position to start from, negative treated as zero
     * @param len  the length of the required String, must be zero or positive
     * @return the middle characters, <code>null</code> if null String input
     */
    public static String mid(String str, int pos, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0 || pos > str.length()) {
            return EMPTY;
        }
        if (pos < 0) {
            pos = 0;
        }
        if (str.length() <= (pos + len)) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }

    // SubStringAfter/SubStringBefore
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the substring before the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * A <code>null</code> separator will return the input string.</p>
     *
     * <pre>
     * LiteStringUtils.substringBefore(null, *)      = null
     * LiteStringUtils.substringBefore("", *)        = ""
     * LiteStringUtils.substringBefore("abc", "a")   = ""
     * LiteStringUtils.substringBefore("abcba", "b") = "a"
     * LiteStringUtils.substringBefore("abc", "c")   = "ab"
     * LiteStringUtils.substringBefore("abc", "d")   = "abc"
     * LiteStringUtils.substringBefore("abc", "")    = ""
     * LiteStringUtils.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring before the first occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringBefore(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.length() == 0) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>Gets the substring after the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * A <code>null</code> separator will return the empty string if the
     * input string is not <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.substringAfter(null, *)      = null
     * LiteStringUtils.substringAfter("", *)        = ""
     * LiteStringUtils.substringAfter(*, null)      = ""
     * LiteStringUtils.substringAfter("abc", "a")   = "bc"
     * LiteStringUtils.substringAfter("abcba", "b") = "cba"
     * LiteStringUtils.substringAfter("abc", "c")   = ""
     * LiteStringUtils.substringAfter("abc", "d")   = ""
     * LiteStringUtils.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring after the first occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringAfter(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * <p>Gets the substring before the last occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * An empty or <code>null</code> separator will return the input string.</p>
     *
     * <pre>
     * LiteStringUtils.substringBeforeLast(null, *)      = null
     * LiteStringUtils.substringBeforeLast("", *)        = ""
     * LiteStringUtils.substringBeforeLast("abcba", "b") = "abc"
     * LiteStringUtils.substringBeforeLast("abc", "c")   = "ab"
     * LiteStringUtils.substringBeforeLast("a", "a")     = ""
     * LiteStringUtils.substringBeforeLast("a", "z")     = "a"
     * LiteStringUtils.substringBeforeLast("a", null)    = "a"
     * LiteStringUtils.substringBeforeLast("a", "")      = "a"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring before the last occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringBeforeLast(String str, String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return str;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>Gets the substring after the last occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * An empty or <code>null</code> separator will return the empty string if
     * the input string is not <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.substringAfterLast(null, *)      = null
     * LiteStringUtils.substringAfterLast("", *)        = ""
     * LiteStringUtils.substringAfterLast(*, "")        = ""
     * LiteStringUtils.substringAfterLast(*, null)      = ""
     * LiteStringUtils.substringAfterLast("abc", "a")   = "bc"
     * LiteStringUtils.substringAfterLast("abcba", "b") = "a"
     * LiteStringUtils.substringAfterLast("abc", "c")   = ""
     * LiteStringUtils.substringAfterLast("a", "a")     = ""
     * LiteStringUtils.substringAfterLast("a", "z")     = ""
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring after the last occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringAfterLast(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return EMPTY;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == (str.length() - separator.length())) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    // Substring between
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the String that is nested in between two instances of the
     * same String.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> tag returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.substringBetween(null, *)            = null
     * LiteStringUtils.substringBetween("", "")             = ""
     * LiteStringUtils.substringBetween("", "tag")          = null
     * LiteStringUtils.substringBetween("tagabctag", null)  = null
     * LiteStringUtils.substringBetween("tagabctag", "")    = ""
     * LiteStringUtils.substringBetween("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str  the String containing the substring, may be null
     * @param tag  the String before and after the substring, may be null
     * @return the substring, <code>null</code> if no match
     * @since 2.0
     */
    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * <p>Gets the String that is nested in between two Strings.
     * Only the first match is returned.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> open/close returns <code>null</code> (no match).
     * An empty ("") open and close returns an empty string.</p>
     *
     * <pre>
     * LiteStringUtils.substringBetween("wx[b]yz", "[", "]") = "b"
     * LiteStringUtils.substringBetween(null, *, *)          = null
     * LiteStringUtils.substringBetween(*, null, *)          = null
     * LiteStringUtils.substringBetween(*, *, null)          = null
     * LiteStringUtils.substringBetween("", "", "")          = ""
     * LiteStringUtils.substringBetween("", "", "]")         = null
     * LiteStringUtils.substringBetween("", "[", "]")        = null
     * LiteStringUtils.substringBetween("yabcz", "", "")     = ""
     * LiteStringUtils.substringBetween("yabcz", "y", "z")   = "abc"
     * LiteStringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str  the String containing the substring, may be null
     * @param open  the String before the substring, may be null
     * @param close  the String after the substring, may be null
     * @return the substring, <code>null</code> if no match
     * @since 2.0
     */
    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    /**
     * <p>Searches a String for substrings delimited by a start and end tag,
     * returning all matching substrings in an array.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> open/close returns <code>null</code> (no match).
     * An empty ("") open/close returns <code>null</code> (no match).</p>
     *
     * <pre>
     * LiteStringUtils.substringsBetween("[a][b][c]", "[", "]") = ["a","b","c"]
     * LiteStringUtils.substringsBetween(null, *, *)            = null
     * LiteStringUtils.substringsBetween(*, null, *)            = null
     * LiteStringUtils.substringsBetween(*, *, null)            = null
     * LiteStringUtils.substringsBetween("", "[", "]")          = []
     * </pre>
     *
     * @param str  the String containing the substrings, null returns null, empty returns empty
     * @param open  the String identifying the start of the substring, empty returns null
     * @param close  the String identifying the end of the substring, empty returns null
     * @return a String Array of substrings, or <code>null</code> if no match
     * @since 2.3
     */
    public static String[] substringsBetween(String str, String open, String close) {
        if (str == null || isEmpty(open) || isEmpty(close)) {
            return null;
        }
        int strLen = str.length();
        if (strLen == 0) {
            return EMPTY_STRING_ARRAY;
        }
        int closeLen = close.length();
        int openLen = open.length();
        List list = new ArrayList();
        int pos = 0;
        while (pos < (strLen - closeLen)) {
            int start = str.indexOf(open, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            int end = str.indexOf(close, start);
            if (end < 0) {
                break;
            }
            list.add(str.substring(start, end));
            pos = end + closeLen;
        }
        if (list.isEmpty()) {
            return null;
        } 
        return (String[]) list.toArray(new String [list.size()]);
    }

    // Nested extraction
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the String that is nested in between two instances of the
     * same String.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> tag returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.getNestedString(null, *)            = null
     * LiteStringUtils.getNestedString("", "")             = ""
     * LiteStringUtils.getNestedString("", "tag")          = null
     * LiteStringUtils.getNestedString("tagabctag", null)  = null
     * LiteStringUtils.getNestedString("tagabctag", "")    = ""
     * LiteStringUtils.getNestedString("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str  the String containing nested-string, may be null
     * @param tag  the String before and after nested-string, may be null
     * @return the nested String, <code>null</code> if no match
     * @deprecated Use the better named {@link #substringBetween(String, String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String getNestedString(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * <p>Gets the String that is nested in between two Strings.
     * Only the first match is returned.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> open/close returns <code>null</code> (no match).
     * An empty ("") open/close returns an empty string.</p>
     *
     * <pre>
     * LiteStringUtils.getNestedString(null, *, *)          = null
     * LiteStringUtils.getNestedString("", "", "")          = ""
     * LiteStringUtils.getNestedString("", "", "tag")       = null
     * LiteStringUtils.getNestedString("", "tag", "tag")    = null
     * LiteStringUtils.getNestedString("yabcz", null, null) = null
     * LiteStringUtils.getNestedString("yabcz", "", "")     = ""
     * LiteStringUtils.getNestedString("yabcz", "y", "z")   = "abc"
     * LiteStringUtils.getNestedString("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str  the String containing nested-string, may be null
     * @param open  the String before nested-string, may be null
     * @param close  the String after nested-string, may be null
     * @return the nested String, <code>null</code> if no match
     * @deprecated Use the better named {@link #substringBetween(String, String, String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String getNestedString(String str, String open, String close) {
        return substringBetween(str, open, close);
    }

    // Splitting
    //-----------------------------------------------------------------------
    /**
     * <p>Splits the provided text into an array, using whitespace as the
     * separator.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.split(null)       = null
     * LiteStringUtils.split("")         = []
     * LiteStringUtils.split("abc def")  = ["abc", "def"]
     * LiteStringUtils.split("abc  def") = ["abc", "def"]
     * LiteStringUtils.split(" abc ")    = ["abc"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(String str) {
        return split(str, null, -1);
    }

    /**
     * <p>Splits the provided text into an array, separator specified.
     * This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.split(null, *)         = null
     * LiteStringUtils.split("", *)           = []
     * LiteStringUtils.split("a.b.c", '.')    = ["a", "b", "c"]
     * LiteStringUtils.split("a..b.c", '.')   = ["a", "b", "c"]
     * LiteStringUtils.split("a:b:c", '.')    = ["a:b:c"]
     * LiteStringUtils.split("a b c", ' ')    = ["a", "b", "c"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChar  the character used as the delimiter
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.0
     */
    public static String[] split(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    /**
     * <p>Splits the provided text into an array, separators specified.
     * This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.</p>
     *
     * <pre>
     * LiteStringUtils.split(null, *)         = null
     * LiteStringUtils.split("", *)           = []
     * LiteStringUtils.split("abc def", null) = ["abc", "def"]
     * LiteStringUtils.split("abc def", " ")  = ["abc", "def"]
     * LiteStringUtils.split("abc  def", " ") = ["abc", "def"]
     * LiteStringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChars  the characters used as the delimiters,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    /**
     * <p>Splits the provided text into an array with a maximum length,
     * separators specified.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.</p>
     *
     * <p>If more than <code>max</code> delimited substrings are found, the last
     * returned string includes all characters after the first <code>max - 1</code>
     * returned strings (including separator characters).</p>
     *
     * <pre>
     * LiteStringUtils.split(null, *, *)            = null
     * LiteStringUtils.split("", *, *)              = []
     * LiteStringUtils.split("ab de fg", null, 0)   = ["ab", "cd", "ef"]
     * LiteStringUtils.split("ab   de fg", null, 0) = ["ab", "cd", "ef"]
     * LiteStringUtils.split("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * LiteStringUtils.split("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChars  the characters used as the delimiters,
     *  <code>null</code> splits on whitespace
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    /**
     * <p>Splits the provided text into an array, separator string specified.</p>
     *
     * <p>The separator(s) will not be included in the returned String array.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separator splits on whitespace.</p>
     *
     * <pre>
     * LiteStringUtils.splitByWholeSeparator(null, *)               = null
     * LiteStringUtils.splitByWholeSeparator("", *)                 = []
     * LiteStringUtils.splitByWholeSeparator("ab de fg", null)      = ["ab", "de", "fg"]
     * LiteStringUtils.splitByWholeSeparator("ab   de fg", null)    = ["ab", "de", "fg"]
     * LiteStringUtils.splitByWholeSeparator("ab:cd:ef", ":")       = ["ab", "cd", "ef"]
     * LiteStringUtils.splitByWholeSeparator("ab-!-cd-!-ef", "-!-") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separator  String containing the String to be used as a delimiter,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String was input
     */
    public static String[] splitByWholeSeparator(String str, String separator) {
        return splitByWholeSeparatorWorker( str, separator, -1, false ) ;
    }

    /**
     * <p>Splits the provided text into an array, separator string specified.
     * Returns a maximum of <code>max</code> substrings.</p>
     *
     * <p>The separator(s) will not be included in the returned String array.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separator splits on whitespace.</p>
     *
     * <pre>
     * LiteStringUtils.splitByWholeSeparator(null, *, *)               = null
     * LiteStringUtils.splitByWholeSeparator("", *, *)                 = []
     * LiteStringUtils.splitByWholeSeparator("ab de fg", null, 0)      = ["ab", "de", "fg"]
     * LiteStringUtils.splitByWholeSeparator("ab   de fg", null, 0)    = ["ab", "de", "fg"]
     * LiteStringUtils.splitByWholeSeparator("ab:cd:ef", ":", 2)       = ["ab", "cd:ef"]
     * LiteStringUtils.splitByWholeSeparator("ab-!-cd-!-ef", "-!-", 5) = ["ab", "cd", "ef"]
     * LiteStringUtils.splitByWholeSeparator("ab-!-cd-!-ef", "-!-", 2) = ["ab", "cd-!-ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separator  String containing the String to be used as a delimiter,
     *  <code>null</code> splits on whitespace
     * @param max  the maximum number of elements to include in the returned
     *  array. A zero or negative value implies no limit.
     * @return an array of parsed Strings, <code>null</code> if null String was input
     */
    public static String[] splitByWholeSeparator( String str, String separator, int max ) {
        return splitByWholeSeparatorWorker(str, separator, max, false);
    }

    /**
     * <p>Splits the provided text into an array, separator string specified. </p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separator splits on whitespace.</p>
     *
     * <pre>
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens(null, *)               = null
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens("", *)                 = []
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens("ab de fg", null)      = ["ab", "de", "fg"]
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens("ab   de fg", null)    = ["ab", "", "", "de", "fg"]
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens("ab:cd:ef", ":")       = ["ab", "cd", "ef"]
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separator  String containing the String to be used as a delimiter,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String was input
     * @since 2.4
     */
    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, true);
    }

    /**
     * <p>Splits the provided text into an array, separator string specified.
     * Returns a maximum of <code>max</code> substrings.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separator splits on whitespace.</p>
     *
     * <pre>
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens(null, *, *)               = null
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens("", *, *)                 = []
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens("ab de fg", null, 0)      = ["ab", "de", "fg"]
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens("ab   de fg", null, 0)    = ["ab", "", "", "de", "fg"]
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens("ab:cd:ef", ":", 2)       = ["ab", "cd:ef"]
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-", 5) = ["ab", "cd", "ef"]
     * LiteStringUtils.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-", 2) = ["ab", "cd-!-ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separator  String containing the String to be used as a delimiter,
     *  <code>null</code> splits on whitespace
     * @param max  the maximum number of elements to include in the returned
     *  array. A zero or negative value implies no limit.
     * @return an array of parsed Strings, <code>null</code> if null String was input
     * @since 2.4
     */
    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator, int max) {
        return splitByWholeSeparatorWorker(str, separator, max, true);
    }

    /**
     * Performs the logic for the <code>splitByWholeSeparatorPreserveAllTokens</code> methods.
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separator  String containing the String to be used as a delimiter,
     *  <code>null</code> splits on whitespace
     * @param max  the maximum number of elements to include in the returned
     *  array. A zero or negative value implies no limit.
     * @param preserveAllTokens if <code>true</code>, adjacent separators are
     * treated as empty token separators; if <code>false</code>, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.4
     */
    private static String[] splitByWholeSeparatorWorker(String str, String separator, int max, 
                                                        boolean preserveAllTokens) 
    {
        if (str == null) {
            return null;
        }

        int len = str.length();

        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }

        if ((separator == null) || (EMPTY.equals(separator))) {
            // Split on whitespace.
            return splitWorker(str, null, max, preserveAllTokens);
        }

        int separatorLength = separator.length();

        ArrayList substrings = new ArrayList();
        int numberOfSubstrings = 0;
        int beg = 0;
        int end = 0;
        while (end < len) {
            end = str.indexOf(separator, beg);

            if (end > -1) {
                if (end > beg) {
                    numberOfSubstrings += 1;

                    if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                    } else {
                        // The following is OK, because String.substring( beg, end ) excludes
                        // the character at the position 'end'.
                        substrings.add(str.substring(beg, end));

                        // Set the starting point for the next search.
                        // The following is equivalent to beg = end + (separatorLength - 1) + 1,
                        // which is the right calculation:
                        beg = end + separatorLength;
                    }
                } else {
                    // We found a consecutive occurrence of the separator, so skip it.
                    if (preserveAllTokens) {
                        numberOfSubstrings += 1;
                        if (numberOfSubstrings == max) {
                            end = len;
                            substrings.add(str.substring(beg));
                        } else {
                            substrings.add(EMPTY);
                        }
                    }
                    beg = end + separatorLength;
                }
            } else {
                // String.substring( beg ) goes from 'beg' to the end of the String.
                substrings.add(str.substring(beg));
                end = len;
            }
        }

        return (String[]) substrings.toArray(new String[substrings.size()]);
    }

    // -----------------------------------------------------------------------
    /**
     * <p>Splits the provided text into an array, using whitespace as the
     * separator, preserving all tokens, including empty tokens created by 
     * adjacent separators. This is an alternative to using StringTokenizer.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.splitPreserveAllTokens(null)       = null
     * LiteStringUtils.splitPreserveAllTokens("")         = []
     * LiteStringUtils.splitPreserveAllTokens("abc def")  = ["abc", "def"]
     * LiteStringUtils.splitPreserveAllTokens("abc  def") = ["abc", "", "def"]
     * LiteStringUtils.splitPreserveAllTokens(" abc ")    = ["", "abc", ""]
     * </pre>
     *
     * @param str  the String to parse, may be <code>null</code>
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(String str) {
        return splitWorker(str, null, -1, true);
    }

    /**
     * <p>Splits the provided text into an array, separator specified,
     * preserving all tokens, including empty tokens created by adjacent
     * separators. This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.splitPreserveAllTokens(null, *)         = null
     * LiteStringUtils.splitPreserveAllTokens("", *)           = []
     * LiteStringUtils.splitPreserveAllTokens("a.b.c", '.')    = ["a", "b", "c"]
     * LiteStringUtils.splitPreserveAllTokens("a..b.c", '.')   = ["a", "", "b", "c"]
     * LiteStringUtils.splitPreserveAllTokens("a:b:c", '.')    = ["a:b:c"]
     * LiteStringUtils.splitPreserveAllTokens("a\tb\nc", null) = ["a", "b", "c"]
     * LiteStringUtils.splitPreserveAllTokens("a b c", ' ')    = ["a", "b", "c"]
     * LiteStringUtils.splitPreserveAllTokens("a b c ", ' ')   = ["a", "b", "c", ""]
     * LiteStringUtils.splitPreserveAllTokens("a b c  ", ' ')   = ["a", "b", "c", "", ""]
     * LiteStringUtils.splitPreserveAllTokens(" a b c", ' ')   = ["", a", "b", "c"]
     * LiteStringUtils.splitPreserveAllTokens("  a b c", ' ')  = ["", "", a", "b", "c"]
     * LiteStringUtils.splitPreserveAllTokens(" a b c ", ' ')  = ["", a", "b", "c", ""]
     * </pre>
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChar  the character used as the delimiter,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(String str, char separatorChar) {
        return splitWorker(str, separatorChar, true);
    }

    /**
     * Performs the logic for the <code>split</code> and 
     * <code>splitPreserveAllTokens</code> methods that do not return a
     * maximum array length.
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChar the separate character
     * @param preserveAllTokens if <code>true</code>, adjacent separators are
     * treated as empty token separators; if <code>false</code>, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        List list = new ArrayList();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * <p>Splits the provided text into an array, separators specified, 
     * preserving all tokens, including empty tokens created by adjacent
     * separators. This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.</p>
     *
     * <pre>
     * LiteStringUtils.splitPreserveAllTokens(null, *)           = null
     * LiteStringUtils.splitPreserveAllTokens("", *)             = []
     * LiteStringUtils.splitPreserveAllTokens("abc def", null)   = ["abc", "def"]
     * LiteStringUtils.splitPreserveAllTokens("abc def", " ")    = ["abc", "def"]
     * LiteStringUtils.splitPreserveAllTokens("abc  def", " ")   = ["abc", "", def"]
     * LiteStringUtils.splitPreserveAllTokens("ab:cd:ef", ":")   = ["ab", "cd", "ef"]
     * LiteStringUtils.splitPreserveAllTokens("ab:cd:ef:", ":")  = ["ab", "cd", "ef", ""]
     * LiteStringUtils.splitPreserveAllTokens("ab:cd:ef::", ":") = ["ab", "cd", "ef", "", ""]
     * LiteStringUtils.splitPreserveAllTokens("ab::cd:ef", ":")  = ["ab", "", cd", "ef"]
     * LiteStringUtils.splitPreserveAllTokens(":cd:ef", ":")     = ["", cd", "ef"]
     * LiteStringUtils.splitPreserveAllTokens("::cd:ef", ":")    = ["", "", cd", "ef"]
     * LiteStringUtils.splitPreserveAllTokens(":cd:ef:", ":")    = ["", cd", "ef", ""]
     * </pre>
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChars  the characters used as the delimiters,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    /**
     * <p>Splits the provided text into an array with a maximum length,
     * separators specified, preserving all tokens, including empty tokens 
     * created by adjacent separators.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.</p>
     *
     * <p>If more than <code>max</code> delimited substrings are found, the last
     * returned string includes all characters after the first <code>max - 1</code>
     * returned strings (including separator characters).</p>
     *
     * <pre>
     * LiteStringUtils.splitPreserveAllTokens(null, *, *)            = null
     * LiteStringUtils.splitPreserveAllTokens("", *, *)              = []
     * LiteStringUtils.splitPreserveAllTokens("ab de fg", null, 0)   = ["ab", "cd", "ef"]
     * LiteStringUtils.splitPreserveAllTokens("ab   de fg", null, 0) = ["ab", "cd", "ef"]
     * LiteStringUtils.splitPreserveAllTokens("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * LiteStringUtils.splitPreserveAllTokens("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * LiteStringUtils.splitPreserveAllTokens("ab   de fg", null, 2) = ["ab", "  de fg"]
     * LiteStringUtils.splitPreserveAllTokens("ab   de fg", null, 3) = ["ab", "", " de fg"]
     * LiteStringUtils.splitPreserveAllTokens("ab   de fg", null, 4) = ["ab", "", "", "de fg"]
     * </pre>
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChars  the characters used as the delimiters,
     *  <code>null</code> splits on whitespace
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, true);
    }

    /**
     * Performs the logic for the <code>split</code> and 
     * <code>splitPreserveAllTokens</code> methods that return a maximum array 
     * length.
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChars the separate character
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit.
     * @param preserveAllTokens if <code>true</code>, adjacent separators are
     * treated as empty token separators; if <code>false</code>, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * <p>Splits a String by Character type as returned by
     * <code>java.lang.Character.getType(char)</code>. Groups of contiguous
     * characters of the same type are returned as complete tokens. 
     * <pre>
     * LiteStringUtils.splitByCharacterType(null)         = null
     * LiteStringUtils.splitByCharacterType("")           = []
     * LiteStringUtils.splitByCharacterType("ab de fg")   = ["ab", " ", "de", " ", "fg"]
     * LiteStringUtils.splitByCharacterType("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
     * LiteStringUtils.splitByCharacterType("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
     * LiteStringUtils.splitByCharacterType("number5")    = ["number", "5"]
     * LiteStringUtils.splitByCharacterType("fooBar")     = ["foo", "B", "ar"]
     * LiteStringUtils.splitByCharacterType("foo200Bar")  = ["foo", "200", "B", "ar"]
     * LiteStringUtils.splitByCharacterType("ASFRules")   = ["ASFR", "ules"]
     * </pre>
     * @param str the String to split, may be <code>null</code>
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.4
     */
    public static String[] splitByCharacterType(String str) {
        return splitByCharacterType(str, false);
    }

    /**
     * <p>Splits a String by Character type as returned by
     * <code>java.lang.Character.getType(char)</code>. Groups of contiguous
     * characters of the same type are returned as complete tokens, with the
     * following exception: the character of type
     * <code>Character.UPPERCASE_LETTER</code>, if any, immediately
     * preceding a token of type <code>Character.LOWERCASE_LETTER</code>
     * will belong to the following token rather than to the preceding, if any,
     * <code>Character.UPPERCASE_LETTER</code> token. 
     * <pre>
     * LiteStringUtils.splitByCharacterTypeCamelCase(null)         = null
     * LiteStringUtils.splitByCharacterTypeCamelCase("")           = []
     * LiteStringUtils.splitByCharacterTypeCamelCase("ab de fg")   = ["ab", " ", "de", " ", "fg"]
     * LiteStringUtils.splitByCharacterTypeCamelCase("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
     * LiteStringUtils.splitByCharacterTypeCamelCase("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
     * LiteStringUtils.splitByCharacterTypeCamelCase("number5")    = ["number", "5"]
     * LiteStringUtils.splitByCharacterTypeCamelCase("fooBar")     = ["foo", "Bar"]
     * LiteStringUtils.splitByCharacterTypeCamelCase("foo200Bar")  = ["foo", "200", "Bar"]
     * LiteStringUtils.splitByCharacterTypeCamelCase("ASFRules")   = ["ASF", "Rules"]
     * </pre>
     * @param str the String to split, may be <code>null</code>
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.4
     */
    public static String[] splitByCharacterTypeCamelCase(String str) {
        return splitByCharacterType(str, true);
    }

    /**
     * <p>Splits a String by Character type as returned by
     * <code>java.lang.Character.getType(char)</code>. Groups of contiguous
     * characters of the same type are returned as complete tokens, with the
     * following exception: if <code>camelCase</code> is <code>true</code>,
     * the character of type <code>Character.UPPERCASE_LETTER</code>, if any,
     * immediately preceding a token of type <code>Character.LOWERCASE_LETTER</code>
     * will belong to the following token rather than to the preceding, if any,
     * <code>Character.UPPERCASE_LETTER</code> token. 
     * @param str the String to split, may be <code>null</code>
     * @param camelCase whether to use so-called "camel-case" for letter types
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.4
     */
    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return EMPTY_STRING_ARRAY;
        }
        char[] c = str.toCharArray();
        List list = new ArrayList();
        int tokenStart = 0;
        int currentType = Character.getType(c[tokenStart]);
        for (int pos = tokenStart + 1; pos < c.length; pos++) {
            int type = Character.getType(c[pos]);
            if (type == currentType) {
                continue;
            }
            if (camelCase && type == Character.LOWERCASE_LETTER && currentType == Character.UPPERCASE_LETTER) {
                int newTokenStart = pos - 1;
                if (newTokenStart != tokenStart) {
                    list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                    tokenStart = newTokenStart;
                }
            } else {
                list.add(new String(c, tokenStart, pos - tokenStart));
                tokenStart = pos;
            }
            currentType = type;
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return (String[]) list.toArray(new String[list.size()]);
    }

    // Joining
    //-----------------------------------------------------------------------
    /**
     * <p>Concatenates elements of an array into a single String.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * LiteStringUtils.concatenate(null)            = null
     * LiteStringUtils.concatenate([])              = ""
     * LiteStringUtils.concatenate([null])          = ""
     * LiteStringUtils.concatenate(["a", "b", "c"]) = "abc"
     * LiteStringUtils.concatenate([null, "", "a"]) = "a"
     * </pre>
     *
     * @param array  the array of values to concatenate, may be null
     * @return the concatenated String, <code>null</code> if null array input
     * @deprecated Use the better named {@link #join(Object[])} instead.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String concatenate(Object[] array) {
        return join(array, null);
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No separator is added to the joined String.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * LiteStringUtils.join(null)            = null
     * LiteStringUtils.join([])              = ""
     * LiteStringUtils.join([null])          = ""
     * LiteStringUtils.join(["a", "b", "c"]) = "abc"
     * LiteStringUtils.join([null, "", "a"]) = "a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @return the joined String, <code>null</code> if null array input
     * @since 2.0
     */
    public static String join(Object[] array) {
        return join(array, null);
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * LiteStringUtils.join(null, *)               = null
     * LiteStringUtils.join([], *)                 = ""
     * LiteStringUtils.join([null], *)             = ""
     * LiteStringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
     * LiteStringUtils.join(["a", "b", "c"], null) = "abc"
     * LiteStringUtils.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use
     * @return the joined String, <code>null</code> if null array input
     * @since 2.0
     */
    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }

        return join(array, separator, 0, array.length);
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * LiteStringUtils.join(null, *)               = null
     * LiteStringUtils.join([], *)                 = ""
     * LiteStringUtils.join([null], *)             = ""
     * LiteStringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
     * LiteStringUtils.join(["a", "b", "c"], null) = "abc"
     * LiteStringUtils.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from.  It is
     * an error to pass in an end index past the end of the array
     * @param endIndex the index to stop joining from (exclusive). It is
     * an error to pass in an end index past the end of the array
     * @return the joined String, <code>null</code> if null array input
     * @since 2.0
     */
    public static String join(Object[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }

        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + 1);
        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }


    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A <code>null</code> separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * LiteStringUtils.join(null, *)                = null
     * LiteStringUtils.join([], *)                  = ""
     * LiteStringUtils.join([null], *)              = ""
     * LiteStringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * LiteStringUtils.join(["a", "b", "c"], null)  = "abc"
     * LiteStringUtils.join(["a", "b", "c"], "")    = "abc"
     * LiteStringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @return the joined String, <code>null</code> if null array input
     */
    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A <code>null</code> separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * LiteStringUtils.join(null, *)                = null
     * LiteStringUtils.join([], *)                  = ""
     * LiteStringUtils.join([null], *)              = ""
     * LiteStringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * LiteStringUtils.join(["a", "b", "c"], null)  = "abc"
     * LiteStringUtils.join(["a", "b", "c"], "")    = "abc"
     * LiteStringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @param startIndex the first index to start joining from.  It is
     * an error to pass in an end index past the end of the array
     * @param endIndex the index to stop joining from (exclusive). It is
     * an error to pass in an end index past the end of the array
     * @return the joined String, <code>null</code> if null array input
     */
    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }

        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length())
                        + separator.length());

        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * <p>Deletes all whitespaces from a String as defined by
     * {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * LiteStringUtils.deleteWhitespace(null)         = null
     * LiteStringUtils.deleteWhitespace("")           = ""
     * LiteStringUtils.deleteWhitespace("abc")        = "abc"
     * LiteStringUtils.deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     *
     * @param str  the String to delete whitespace from, may be null
     * @return the String without whitespaces, <code>null</code> if null String input
     */
    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }

    // Remove
    //-----------------------------------------------------------------------
    /**
     * <p>Removes a substring only if it is at the begining of a source string,
     * otherwise returns the source string.</p>
     *
     * <p>A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> search string will return the source string.</p>
     *
     * <pre>
     * LiteStringUtils.removeStart(null, *)      = null
     * LiteStringUtils.removeStart("", *)        = ""
     * LiteStringUtils.removeStart(*, null)      = *
     * LiteStringUtils.removeStart("www.domain.com", "www.")   = "domain.com"
     * LiteStringUtils.removeStart("domain.com", "www.")       = "domain.com"
     * LiteStringUtils.removeStart("www.domain.com", "domain") = "www.domain.com"
     * LiteStringUtils.removeStart("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     *  <code>null</code> if null String input
     * @since 2.1
     */
    public static String removeStart(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.startsWith(remove)){
            return str.substring(remove.length());
        }
        return str;
    }

    /**
     * <p>Case insensitive removal of a substring if it is at the begining of a source string,
     * otherwise returns the source string.</p>
     *
     * <p>A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> search string will return the source string.</p>
     *
     * <pre>
     * LiteStringUtils.removeStartIgnoreCase(null, *)      = null
     * LiteStringUtils.removeStartIgnoreCase("", *)        = ""
     * LiteStringUtils.removeStartIgnoreCase(*, null)      = *
     * LiteStringUtils.removeStartIgnoreCase("www.domain.com", "www.")   = "domain.com"
     * LiteStringUtils.removeStartIgnoreCase("www.domain.com", "WWW.")   = "domain.com"
     * LiteStringUtils.removeStartIgnoreCase("domain.com", "www.")       = "domain.com"
     * LiteStringUtils.removeStartIgnoreCase("www.domain.com", "domain") = "www.domain.com"
     * LiteStringUtils.removeStartIgnoreCase("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for (case insensitive) and remove, may be null
     * @return the substring with the string removed if found,
     *  <code>null</code> if null String input
     * @since 2.4
     */
    public static String removeStartIgnoreCase(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (startsWithIgnoreCase(str, remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    /**
     * <p>Removes a substring only if it is at the end of a source string,
     * otherwise returns the source string.</p>
     *
     * <p>A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> search string will return the source string.</p>
     *
     * <pre>
     * LiteStringUtils.removeEnd(null, *)      = null
     * LiteStringUtils.removeEnd("", *)        = ""
     * LiteStringUtils.removeEnd(*, null)      = *
     * LiteStringUtils.removeEnd("www.domain.com", ".com.")  = "www.domain.com"
     * LiteStringUtils.removeEnd("www.domain.com", ".com")   = "www.domain"
     * LiteStringUtils.removeEnd("www.domain.com", "domain") = "www.domain.com"
     * LiteStringUtils.removeEnd("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     *  <code>null</code> if null String input
     * @since 2.1
     */
    public static String removeEnd(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    /**
     * <p>Case insensitive removal of a substring if it is at the end of a source string,
     * otherwise returns the source string.</p>
     *
     * <p>A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> search string will return the source string.</p>
     *
     * <pre>
     * LiteStringUtils.removeEnd(null, *)      = null
     * LiteStringUtils.removeEnd("", *)        = ""
     * LiteStringUtils.removeEnd(*, null)      = *
     * LiteStringUtils.removeEnd("www.domain.com", ".com.")  = "www.domain.com."
     * LiteStringUtils.removeEnd("www.domain.com", ".com")   = "www.domain"
     * LiteStringUtils.removeEnd("www.domain.com", "domain") = "www.domain.com"
     * LiteStringUtils.removeEnd("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for (case insensitive) and remove, may be null
     * @return the substring with the string removed if found,
     *  <code>null</code> if null String input
     * @since 2.4
     */
    public static String removeEndIgnoreCase(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (endsWithIgnoreCase(str, remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    /**
     * <p>Removes all occurrences of a substring from within the source string.</p>
     *
     * <p>A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> remove string will return the source string.
     * An empty ("") remove string will return the source string.</p>
     *
     * <pre>
     * LiteStringUtils.remove(null, *)        = null
     * LiteStringUtils.remove("", *)          = ""
     * LiteStringUtils.remove(*, null)        = *
     * LiteStringUtils.remove(*, "")          = *
     * LiteStringUtils.remove("queued", "ue") = "qd"
     * LiteStringUtils.remove("queued", "zz") = "queued"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     *  <code>null</code> if null String input
     * @since 2.1
     */
    public static String remove(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        return replace(str, remove, EMPTY, -1);
    }

    /**
     * <p>Removes all occurrences of a character from within the source string.</p>
     *
     * <p>A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.</p>
     *
     * <pre>
     * LiteStringUtils.remove(null, *)       = null
     * LiteStringUtils.remove("", *)         = ""
     * LiteStringUtils.remove("queued", 'u') = "qeed"
     * LiteStringUtils.remove("queued", 'z') = "queued"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the char to search for and remove, may be null
     * @return the substring with the char removed if found,
     *  <code>null</code> if null String input
     * @since 2.1
     */
    public static String remove(String str, char remove) {
        if (isEmpty(str) || str.indexOf(remove) == -1) {
            return str;
        }
        char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove) {
                chars[pos++] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }

    // Replacing
    //-----------------------------------------------------------------------
    /**
     * <p>Replaces a String with another String inside a larger String, once.</p>
     *
     * <p>A <code>null</code> reference passed to this method is a no-op.</p>
     *
     * <pre>
     * LiteStringUtils.replaceOnce(null, *, *)        = null
     * LiteStringUtils.replaceOnce("", *, *)          = ""
     * LiteStringUtils.replaceOnce("any", null, *)    = "any"
     * LiteStringUtils.replaceOnce("any", *, null)    = "any"
     * LiteStringUtils.replaceOnce("any", "", *)      = "any"
     * LiteStringUtils.replaceOnce("aba", "a", null)  = "aba"
     * LiteStringUtils.replaceOnce("aba", "a", "")    = "ba"
     * LiteStringUtils.replaceOnce("aba", "a", "z")   = "zba"
     * </pre>
     *
     * @see #replace(String text, String searchString, String replacement, int max)
     * @param text  text to search and replace in, may be null
     * @param searchString  the String to search for, may be null
     * @param replacement  the String to replace with, may be null
     * @return the text with any replacements processed,
     *  <code>null</code> if null String input
     */
    public static String replaceOnce(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, 1);
    }

    /**
     * <p>Replaces all occurrences of a String within another String.</p>
     *
     * <p>A <code>null</code> reference passed to this method is a no-op.</p>
     *
     * <pre>
     * LiteStringUtils.replace(null, *, *)        = null
     * LiteStringUtils.replace("", *, *)          = ""
     * LiteStringUtils.replace("any", null, *)    = "any"
     * LiteStringUtils.replace("any", *, null)    = "any"
     * LiteStringUtils.replace("any", "", *)      = "any"
     * LiteStringUtils.replace("aba", "a", null)  = "aba"
     * LiteStringUtils.replace("aba", "a", "")    = "b"
     * LiteStringUtils.replace("aba", "a", "z")   = "zbz"
     * </pre>
     *
     * @see #replace(String text, String searchString, String replacement, int max)
     * @param text  text to search and replace in, may be null
     * @param searchString  the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @return the text with any replacements processed,
     *  <code>null</code> if null String input
     */
    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    /**
     * <p>Replaces a String with another String inside a larger String,
     * for the first <code>max</code> values of the search String.</p>
     *
     * <p>A <code>null</code> reference passed to this method is a no-op.</p>
     *
     * <pre>
     * LiteStringUtils.replace(null, *, *, *)         = null
     * LiteStringUtils.replace("", *, *, *)           = ""
     * LiteStringUtils.replace("any", null, *, *)     = "any"
     * LiteStringUtils.replace("any", *, null, *)     = "any"
     * LiteStringUtils.replace("any", "", *, *)       = "any"
     * LiteStringUtils.replace("any", *, *, 0)        = "any"
     * LiteStringUtils.replace("abaa", "a", null, -1) = "abaa"
     * LiteStringUtils.replace("abaa", "a", "", -1)   = "b"
     * LiteStringUtils.replace("abaa", "a", "z", 0)   = "abaa"
     * LiteStringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
     * LiteStringUtils.replace("abaa", "a", "z", 2)   = "zbza"
     * LiteStringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text  text to search and replace in, may be null
     * @param searchString  the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @param max  maximum number of values to replace, or <code>-1</code> if no maximum
     * @return the text with any replacements processed,
     *  <code>null</code> if null String input
     */
    public static String replace(String text, String searchString, String replacement, int max) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = (increase < 0 ? 0 : increase);
        increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
        StringBuffer buf = new StringBuffer(text.length() + increase);
        while (end != -1) {
            buf.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * <p>
     * Replaces all occurrences of Strings within another String.
     * </p>
     * 
     * <p>
     * A <code>null</code> reference passed to this method is a no-op, or if
     * any "search string" or "string to replace" is null, that replace will be
     * ignored. This will not repeat. For repeating replaces, call the
     * overloaded method.
     * </p>
     * 
     * <pre>
     *  LiteStringUtils.replaceEach(null, *, *)        = null
     *  LiteStringUtils.replaceEach("", *, *)          = ""
     *  LiteStringUtils.replaceEach("aba", null, null) = "aba"
     *  LiteStringUtils.replaceEach("aba", new String[0], null) = "aba"
     *  LiteStringUtils.replaceEach("aba", null, new String[0]) = "aba"
     *  LiteStringUtils.replaceEach("aba", new String[]{"a"}, null)  = "aba"
     *  LiteStringUtils.replaceEach("aba", new String[]{"a"}, new String[]{""})  = "b"
     *  LiteStringUtils.replaceEach("aba", new String[]{null}, new String[]{"a"})  = "aba"
     *  LiteStringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"})  = "wcte"
     *  (example of how it does not repeat)
     *  LiteStringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"})  = "dcte"
     * </pre>
     * 
     * @param text
     *            text to search and replace in, no-op if null
     * @param searchList
     *            the Strings to search for, no-op if null
     * @param replacementList
     *            the Strings to replace them with, no-op if null
     * @return the text with any replacements processed, <code>null</code> if
     *         null String input
     * @throws IndexOutOfBoundsException
     *             if the lengths of the arrays are not the same (null is ok,
     *             and/or size 0)
     * @since 2.4
     */
    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    /**
     * <p>
     * Replaces all occurrences of Strings within another String.
     * </p>
     * 
     * <p>
     * A <code>null</code> reference passed to this method is a no-op, or if
     * any "search string" or "string to replace" is null, that replace will be
     * ignored. This will not repeat. For repeating replaces, call the
     * overloaded method.
     * </p>
     * 
     * <pre>
     *  LiteStringUtils.replaceEach(null, *, *, *) = null
     *  LiteStringUtils.replaceEach("", *, *, *) = ""
     *  LiteStringUtils.replaceEach("aba", null, null, *) = "aba"
     *  LiteStringUtils.replaceEach("aba", new String[0], null, *) = "aba"
     *  LiteStringUtils.replaceEach("aba", null, new String[0], *) = "aba"
     *  LiteStringUtils.replaceEach("aba", new String[]{"a"}, null, *) = "aba"
     *  LiteStringUtils.replaceEach("aba", new String[]{"a"}, new String[]{""}, *) = "b"
     *  LiteStringUtils.replaceEach("aba", new String[]{null}, new String[]{"a"}, *) = "aba"
     *  LiteStringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"}, *) = "wcte"
     *  (example of how it repeats)
     *  LiteStringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, false) = "dcte"
     *  LiteStringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, true) = "tcte"
     *  LiteStringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, true) = IllegalArgumentException
     *  LiteStringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, false) = "dcabe"
     * </pre>
     * 
     * @param text
     *            text to search and replace in, no-op if null
     * @param searchList
     *            the Strings to search for, no-op if null
     * @param replacementList
     *            the Strings to replace them with, no-op if null
     * @return the text with any replacements processed, <code>null</code> if
     *         null String input
     * @throws IllegalArgumentException
     *             if the search is repeating and there is an endless loop due
     *             to outputs of one being inputs to another
     * @throws IndexOutOfBoundsException
     *             if the lengths of the arrays are not the same (null is ok,
     *             and/or size 0)
     * @since 2.4
     */
    public static String replaceEachRepeatedly(String text, String[] searchList, String[] replacementList) {
        // timeToLive should be 0 if not used or nothing to replace, else it's
        // the length of the replace array
        int timeToLive = searchList == null ? 0 : searchList.length;
        return replaceEach(text, searchList, replacementList, true, timeToLive);
    }

    /**
     * <p>
     * Replaces all occurrences of Strings within another String.
     * </p>
     * 
     * <p>
     * A <code>null</code> reference passed to this method is a no-op, or if
     * any "search string" or "string to replace" is null, that replace will be
     * ignored. 
     * </p>
     * 
     * <pre>
     *  LiteStringUtils.replaceEach(null, *, *, *) = null
     *  LiteStringUtils.replaceEach("", *, *, *) = ""
     *  LiteStringUtils.replaceEach("aba", null, null, *) = "aba"
     *  LiteStringUtils.replaceEach("aba", new String[0], null, *) = "aba"
     *  LiteStringUtils.replaceEach("aba", null, new String[0], *) = "aba"
     *  LiteStringUtils.replaceEach("aba", new String[]{"a"}, null, *) = "aba"
     *  LiteStringUtils.replaceEach("aba", new String[]{"a"}, new String[]{""}, *) = "b"
     *  LiteStringUtils.replaceEach("aba", new String[]{null}, new String[]{"a"}, *) = "aba"
     *  LiteStringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"}, *) = "wcte"
     *  (example of how it repeats)
     *  LiteStringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, false) = "dcte"
     *  LiteStringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, true) = "tcte"
     *  LiteStringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, *) = IllegalArgumentException
     * </pre>
     * 
     * @param text
     *            text to search and replace in, no-op if null
     * @param searchList
     *            the Strings to search for, no-op if null
     * @param replacementList
     *            the Strings to replace them with, no-op if null
     * @param repeat if true, then replace repeatedly 
     *       until there are no more possible replacements or timeToLive < 0
     * @param timeToLive
     *            if less than 0 then there is a circular reference and endless
     *            loop
     * @return the text with any replacements processed, <code>null</code> if
     *         null String input
     * @throws IllegalArgumentException
     *             if the search is repeating and there is an endless loop due
     *             to outputs of one being inputs to another
     * @throws IndexOutOfBoundsException
     *             if the lengths of the arrays are not the same (null is ok,
     *             and/or size 0)
     * @since 2.4
     */
    private static String replaceEach(String text, String[] searchList, String[] replacementList, 
                                      boolean repeat, int timeToLive) 
    {

        // mchyzer Performance note: This creates very few new objects (one major goal)
        // let me know if there are performance requests, we can create a harness to measure

        if (text == null || text.length() == 0 || searchList == null || 
            searchList.length == 0 || replacementList == null || replacementList.length == 0) 
        {
            return text;
        }

        // if recursing, this shouldnt be less than 0
        if (timeToLive < 0) {
            throw new IllegalStateException("TimeToLive of " + timeToLive + " is less than 0: " + text);
        }

        int searchLength = searchList.length;
        int replacementLength = replacementList.length;

        // make sure lengths are ok, these need to be equal
        if (searchLength != replacementLength) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: "
                + searchLength
                + " vs "
                + replacementLength);
        }

        // keep track of which still have matches
        boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex = -1;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++) {
            if (noMoreMatchesForReplIndex[i] || searchList[i] == null || 
                searchList[i].length() == 0 || replacementList[i] == null) 
            {
                continue;
            }
            tempIndex = text.indexOf(searchList[i]);

            // see if we need to keep searching for this
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
            } else {
                if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1) {
            return text;
        }

        int start = 0;

        // get a good guess on the size of the result buffer so it doesnt have to double if it goes over a bit
        int increase = 0;

        // count the replacement text elements that are larger than their corresponding text being replaced
        for (int i = 0; i < searchList.length; i++) {
            int greater = replacementList[i].length() - searchList[i].length();
            if (greater > 0) {
                increase += 3 * greater; // assume 3 matches
            }
        }
        // have upper-bound at 20% increase, then let Java take over
        increase = Math.min(increase, text.length() / 5);

        StringBuffer buf = new StringBuffer(text.length() + increase);

        while (textIndex != -1) {

            for (int i = start; i < textIndex; i++) {
                buf.append(text.charAt(i));
            }
            buf.append(replacementList[replaceIndex]);

            start = textIndex + searchList[replaceIndex].length();

            textIndex = -1;
            replaceIndex = -1;
            tempIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i] || searchList[i] == null || 
                    searchList[i].length() == 0 || replacementList[i] == null) 
                {
                    continue;
                }
                tempIndex = text.indexOf(searchList[i], start);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else {
                    if (textIndex == -1 || tempIndex < textIndex) {
                        textIndex = tempIndex;
                        replaceIndex = i;
                    }
                }
            }
            // NOTE: logic duplicated above END

        }
        int textLength = text.length();
        for (int i = start; i < textLength; i++) {
            buf.append(text.charAt(i));
        }
        String result = buf.toString();
        if (!repeat) {
            return result;
        }

        return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
    }

    // Replace, character based
    //-----------------------------------------------------------------------
    /**
     * <p>Replaces all occurrences of a character in a String with another.
     * This is a null-safe version of {@link String#replace(char, char)}.</p>
     *
     * <p>A <code>null</code> string input returns <code>null</code>.
     * An empty ("") string input returns an empty string.</p>
     *
     * <pre>
     * LiteStringUtils.replaceChars(null, *, *)        = null
     * LiteStringUtils.replaceChars("", *, *)          = ""
     * LiteStringUtils.replaceChars("abcba", 'b', 'y') = "aycya"
     * LiteStringUtils.replaceChars("abcba", 'z', 'y') = "abcba"
     * </pre>
     *
     * @param str  String to replace characters in, may be null
     * @param searchChar  the character to search for, may be null
     * @param replaceChar  the character to replace, may be null
     * @return modified String, <code>null</code> if null string input
     * @since 2.0
     */
    public static String replaceChars(String str, char searchChar, char replaceChar) {
        if (str == null) {
            return null;
        }
        return str.replace(searchChar, replaceChar);
    }

    /**
     * <p>Replaces multiple characters in a String in one go.
     * This method can also be used to delete characters.</p>
     *
     * <p>For example:<br />
     * <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code>.</p>
     *
     * <p>A <code>null</code> string input returns <code>null</code>.
     * An empty ("") string input returns an empty string.
     * A null or empty set of search characters returns the input string.</p>
     *
     * <p>The length of the search characters should normally equal the length
     * of the replace characters.
     * If the search characters is longer, then the extra search characters
     * are deleted.
     * If the search characters is shorter, then the extra replace characters
     * are ignored.</p>
     *
     * <pre>
     * LiteStringUtils.replaceChars(null, *, *)           = null
     * LiteStringUtils.replaceChars("", *, *)             = ""
     * LiteStringUtils.replaceChars("abc", null, *)       = "abc"
     * LiteStringUtils.replaceChars("abc", "", *)         = "abc"
     * LiteStringUtils.replaceChars("abc", "b", null)     = "ac"
     * LiteStringUtils.replaceChars("abc", "b", "")       = "ac"
     * LiteStringUtils.replaceChars("abcba", "bc", "yz")  = "ayzya"
     * LiteStringUtils.replaceChars("abcba", "bc", "y")   = "ayya"
     * LiteStringUtils.replaceChars("abcba", "bc", "yzx") = "ayzya"
     * </pre>
     *
     * @param str  String to replace characters in, may be null
     * @param searchChars  a set of characters to search for, may be null
     * @param replaceChars  a set of characters to replace, may be null
     * @return modified String, <code>null</code> if null string input
     * @since 2.0
     */
    public static String replaceChars(String str, String searchChars, String replaceChars) {
        if (isEmpty(str) || isEmpty(searchChars)) {
            return str;
        }
        if (replaceChars == null) {
            replaceChars = EMPTY;
        }
        boolean modified = false;
        int replaceCharsLength = replaceChars.length();
        int strLength = str.length();
        StringBuffer buf = new StringBuffer(strLength);
        for (int i = 0; i < strLength; i++) {
            char ch = str.charAt(i);
            int index = searchChars.indexOf(ch);
            if (index >= 0) {
                modified = true;
                if (index < replaceCharsLength) {
                    buf.append(replaceChars.charAt(index));
                }
            } else {
                buf.append(ch);
            }
        }
        if (modified) {
            return buf.toString();
        }
        return str;
    }

    // Overlay
    //-----------------------------------------------------------------------
    /**
     * <p>Overlays part of a String with another String.</p>
     *
     * <pre>
     * LiteStringUtils.overlayString(null, *, *, *)           = NullPointerException
     * LiteStringUtils.overlayString(*, null, *, *)           = NullPointerException
     * LiteStringUtils.overlayString("", "abc", 0, 0)         = "abc"
     * LiteStringUtils.overlayString("abcdef", null, 2, 4)    = "abef"
     * LiteStringUtils.overlayString("abcdef", "", 2, 4)      = "abef"
     * LiteStringUtils.overlayString("abcdef", "zzzz", 2, 4)  = "abzzzzef"
     * LiteStringUtils.overlayString("abcdef", "zzzz", 4, 2)  = "abcdzzzzcdef"
     * LiteStringUtils.overlayString("abcdef", "zzzz", -1, 4) = IndexOutOfBoundsException
     * LiteStringUtils.overlayString("abcdef", "zzzz", 2, 8)  = IndexOutOfBoundsException
     * </pre>
     *
     * @param text  the String to do overlaying in, may be null
     * @param overlay  the String to overlay, may be null
     * @param start  the position to start overlaying at, must be valid
     * @param end  the position to stop overlaying before, must be valid
     * @return overlayed String, <code>null</code> if null String input
     * @throws NullPointerException if text or overlay is null
     * @throws IndexOutOfBoundsException if either position is invalid
     * @deprecated Use better named {@link #overlay(String, String, int, int)} instead.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String overlayString(String text, String overlay, int start, int end) {
        return new StringBuffer(start + overlay.length() + text.length() - end + 1)
            .append(text.substring(0, start))
            .append(overlay)
            .append(text.substring(end))
            .toString();
    }

    /**
     * <p>Overlays part of a String with another String.</p>
     *
     * <p>A <code>null</code> string input returns <code>null</code>.
     * A negative index is treated as zero.
     * An index greater than the string length is treated as the string length.
     * The start index is always the smaller of the two indices.</p>
     *
     * <pre>
     * LiteStringUtils.overlay(null, *, *, *)            = null
     * LiteStringUtils.overlay("", "abc", 0, 0)          = "abc"
     * LiteStringUtils.overlay("abcdef", null, 2, 4)     = "abef"
     * LiteStringUtils.overlay("abcdef", "", 2, 4)       = "abef"
     * LiteStringUtils.overlay("abcdef", "", 4, 2)       = "abef"
     * LiteStringUtils.overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
     * LiteStringUtils.overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
     * LiteStringUtils.overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
     * LiteStringUtils.overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
     * LiteStringUtils.overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
     * LiteStringUtils.overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
     * </pre>
     *
     * @param str  the String to do overlaying in, may be null
     * @param overlay  the String to overlay, may be null
     * @param start  the position to start overlaying at
     * @param end  the position to stop overlaying before
     * @return overlayed String, <code>null</code> if null String input
     * @since 2.0
     */
    public static String overlay(String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = EMPTY;
        }
        int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        return new StringBuffer(len + start - end + overlay.length() + 1)
            .append(str.substring(0, start))
            .append(overlay)
            .append(str.substring(end))
            .toString();
    }

    // Padding
    //-----------------------------------------------------------------------
    /**
     * <p>Repeat a String <code>repeat</code> times to form a
     * new String.</p>
     *
     * <pre>
     * LiteStringUtils.repeat(null, 2) = null
     * LiteStringUtils.repeat("", 0)   = ""
     * LiteStringUtils.repeat("", 2)   = ""
     * LiteStringUtils.repeat("a", 3)  = "aaa"
     * LiteStringUtils.repeat("ab", 2) = "abab"
     * LiteStringUtils.repeat("a", -2) = ""
     * </pre>
     *
     * @param str  the String to repeat, may be null
     * @param repeat  number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated,
     *  <code>null</code> if null String input
     */
    public static String repeat(String str, int repeat) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return EMPTY;
        }
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return padding(repeat, str.charAt(0));
        }

        int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1 :
                char ch = str.charAt(0);
                char[] output1 = new char[outputLength];
                for (int i = repeat - 1; i >= 0; i--) {
                    output1[i] = ch;
                }
                return new String(output1);
            case 2 :
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default :
                StringBuffer buf = new StringBuffer(outputLength);
                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }
                return buf.toString();
        }
    }

    /**
     * <p>Returns padding using the specified delimiter repeated
     * to a given length.</p>
     *
     * <pre>
     * LiteStringUtils.padding(0, 'e')  = ""
     * LiteStringUtils.padding(3, 'e')  = "eee"
     * LiteStringUtils.padding(-2, 'e') = IndexOutOfBoundsException
     * </pre>
     *
     * <p>Note: this method doesn't not support padding with
     * <a href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary Characters</a>
     * as they require a pair of <code>char</code>s to be represented.
     * If you are needing to support full I18N of your applications
     * consider using {@link #repeat(String, int)} instead. 
     * </p>
     *
     * @param repeat  number of times to repeat delim
     * @param padChar  character to repeat
     * @return String with repeated character
     * @throws IndexOutOfBoundsException if <code>repeat &lt; 0</code>
     * @see #repeat(String, int)
     */
    private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        }
        final char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = padChar;
        }
        return new String(buf);
    }

    /**
     * <p>Right pad a String with spaces (' ').</p>
     *
     * <p>The String is padded to the size of <code>size</code>.</p>
     *
     * <pre>
     * LiteStringUtils.rightPad(null, *)   = null
     * LiteStringUtils.rightPad("", 3)     = "   "
     * LiteStringUtils.rightPad("bat", 3)  = "bat"
     * LiteStringUtils.rightPad("bat", 5)  = "bat  "
     * LiteStringUtils.rightPad("bat", 1)  = "bat"
     * LiteStringUtils.rightPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @return right padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     */
    public static String rightPad(String str, int size) {
        return rightPad(str, size, ' ');
    }

    /**
     * <p>Right pad a String with a specified character.</p>
     *
     * <p>The String is padded to the size of <code>size</code>.</p>
     *
     * <pre>
     * LiteStringUtils.rightPad(null, *, *)     = null
     * LiteStringUtils.rightPad("", 3, 'z')     = "zzz"
     * LiteStringUtils.rightPad("bat", 3, 'z')  = "bat"
     * LiteStringUtils.rightPad("bat", 5, 'z')  = "batzz"
     * LiteStringUtils.rightPad("bat", 1, 'z')  = "bat"
     * LiteStringUtils.rightPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padChar  the character to pad with
     * @return right padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(padding(pads, padChar));
    }

    /**
     * <p>Right pad a String with a specified String.</p>
     *
     * <p>The String is padded to the size of <code>size</code>.</p>
     *
     * <pre>
     * LiteStringUtils.rightPad(null, *, *)      = null
     * LiteStringUtils.rightPad("", 3, "z")      = "zzz"
     * LiteStringUtils.rightPad("bat", 3, "yz")  = "bat"
     * LiteStringUtils.rightPad("bat", 5, "yz")  = "batyz"
     * LiteStringUtils.rightPad("bat", 8, "yz")  = "batyzyzy"
     * LiteStringUtils.rightPad("bat", 1, "yz")  = "bat"
     * LiteStringUtils.rightPad("bat", -1, "yz") = "bat"
     * LiteStringUtils.rightPad("bat", 5, null)  = "bat  "
     * LiteStringUtils.rightPad("bat", 5, "")    = "bat  "
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padStr  the String to pad with, null or empty treated as single space
     * @return right padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     */
    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return rightPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * <p>Left pad a String with spaces (' ').</p>
     *
     * <p>The String is padded to the size of <code>size<code>.</p>
     *
     * <pre>
     * LiteStringUtils.leftPad(null, *)   = null
     * LiteStringUtils.leftPad("", 3)     = "   "
     * LiteStringUtils.leftPad("bat", 3)  = "bat"
     * LiteStringUtils.leftPad("bat", 5)  = "  bat"
     * LiteStringUtils.leftPad("bat", 1)  = "bat"
     * LiteStringUtils.leftPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @return left padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     */
    public static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    /**
     * <p>Left pad a String with a specified character.</p>
     *
     * <p>Pad to a size of <code>size</code>.</p>
     *
     * <pre>
     * LiteStringUtils.leftPad(null, *, *)     = null
     * LiteStringUtils.leftPad("", 3, 'z')     = "zzz"
     * LiteStringUtils.leftPad("bat", 3, 'z')  = "bat"
     * LiteStringUtils.leftPad("bat", 5, 'z')  = "zzbat"
     * LiteStringUtils.leftPad("bat", 1, 'z')  = "bat"
     * LiteStringUtils.leftPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padChar  the character to pad with
     * @return left padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return padding(pads, padChar).concat(str);
    }

    /**
     * <p>Left pad a String with a specified String.</p>
     *
     * <p>Pad to a size of <code>size</code>.</p>
     *
     * <pre>
     * LiteStringUtils.leftPad(null, *, *)      = null
     * LiteStringUtils.leftPad("", 3, "z")      = "zzz"
     * LiteStringUtils.leftPad("bat", 3, "yz")  = "bat"
     * LiteStringUtils.leftPad("bat", 5, "yz")  = "yzbat"
     * LiteStringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
     * LiteStringUtils.leftPad("bat", 1, "yz")  = "bat"
     * LiteStringUtils.leftPad("bat", -1, "yz") = "bat"
     * LiteStringUtils.leftPad("bat", 5, null)  = "  bat"
     * LiteStringUtils.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padStr  the String to pad with, null or empty treated as single space
     * @return left padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     */
    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    /**
     * Gets a String's length or <code>0</code> if the String is <code>null</code>.
     * 
     * @param str
     *            a String or <code>null</code>
     * @return String length or <code>0</code> if the String is <code>null</code>.
     * @since 2.4
     */
    public static int length(String str) {
        return str == null ? 0 : str.length();
    }
    
    // Centering
    //-----------------------------------------------------------------------
    /**
     * <p>Centers a String in a larger String of size <code>size</code>
     * using the space character (' ').<p>
     *
     * <p>If the size is less than the String length, the String is returned.
     * A <code>null</code> String returns <code>null</code>.
     * A negative size is treated as zero.</p>
     *
     * <p>Equivalent to <code>center(str, size, " ")</code>.</p>
     *
     * <pre>
     * LiteStringUtils.center(null, *)   = null
     * LiteStringUtils.center("", 4)     = "    "
     * LiteStringUtils.center("ab", -1)  = "ab"
     * LiteStringUtils.center("ab", 4)   = " ab "
     * LiteStringUtils.center("abcd", 2) = "abcd"
     * LiteStringUtils.center("a", 4)    = " a  "
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size  the int size of new String, negative treated as zero
     * @return centered String, <code>null</code> if null String input
     */
    public static String center(String str, int size) {
        return center(str, size, ' ');
    }

    /**
     * <p>Centers a String in a larger String of size <code>size</code>.
     * Uses a supplied character as the value to pad the String with.</p>
     *
     * <p>If the size is less than the String length, the String is returned.
     * A <code>null</code> String returns <code>null</code>.
     * A negative size is treated as zero.</p>
     *
     * <pre>
     * LiteStringUtils.center(null, *, *)     = null
     * LiteStringUtils.center("", 4, ' ')     = "    "
     * LiteStringUtils.center("ab", -1, ' ')  = "ab"
     * LiteStringUtils.center("ab", 4, ' ')   = " ab"
     * LiteStringUtils.center("abcd", 2, ' ') = "abcd"
     * LiteStringUtils.center("a", 4, ' ')    = " a  "
     * LiteStringUtils.center("a", 4, 'y')    = "yayy"
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size  the int size of new String, negative treated as zero
     * @param padChar  the character to pad the new String with
     * @return centered String, <code>null</code> if null String input
     * @since 2.0
     */
    public static String center(String str, int size, char padChar) {
        if (str == null || size <= 0) {
            return str;
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padChar);
        str = rightPad(str, size, padChar);
        return str;
    }

    /**
     * <p>Centers a String in a larger String of size <code>size</code>.
     * Uses a supplied String as the value to pad the String with.</p>
     *
     * <p>If the size is less than the String length, the String is returned.
     * A <code>null</code> String returns <code>null</code>.
     * A negative size is treated as zero.</p>
     *
     * <pre>
     * LiteStringUtils.center(null, *, *)     = null
     * LiteStringUtils.center("", 4, " ")     = "    "
     * LiteStringUtils.center("ab", -1, " ")  = "ab"
     * LiteStringUtils.center("ab", 4, " ")   = " ab"
     * LiteStringUtils.center("abcd", 2, " ") = "abcd"
     * LiteStringUtils.center("a", 4, " ")    = " a  "
     * LiteStringUtils.center("a", 4, "yz")   = "yayz"
     * LiteStringUtils.center("abc", 7, null) = "  abc  "
     * LiteStringUtils.center("abc", 7, "")   = "  abc  "
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size  the int size of new String, negative treated as zero
     * @param padStr  the String to pad the new String with, must not be null or empty
     * @return centered String, <code>null</code> if null String input
     * @throws IllegalArgumentException if padStr is <code>null</code> or empty
     */
    public static String center(String str, int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padStr);
        str = rightPad(str, size, padStr);
        return str;
    }

    // Case conversion
    //-----------------------------------------------------------------------
    /**
     * <p>Converts a String to upper case as per {@link String#toUpperCase()}.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.upperCase(null)  = null
     * LiteStringUtils.upperCase("")    = ""
     * LiteStringUtils.upperCase("aBc") = "ABC"
     * </pre>
     *
     * @param str  the String to upper case, may be null
     * @return the upper cased String, <code>null</code> if null String input
     */
    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    /**
     * <p>Converts a String to lower case as per {@link String#toLowerCase()}.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.lowerCase(null)  = null
     * LiteStringUtils.lowerCase("")    = ""
     * LiteStringUtils.lowerCase("aBc") = "abc"
     * </pre>
     *
     * @param str  the String to lower case, may be null
     * @return the lower cased String, <code>null</code> if null String input
     */
    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase(Locale.getDefault());
    }

    /**
     * <p>Capitalizes a String changing the first letter to title case as
     * per {@link Character#toTitleCase(char)}. No other letters are changed.</p>
     *
     * <p>For a word based algorithm, see {@link WordUtils#capitalize(String)}.
     * A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.capitalize(null)  = null
     * LiteStringUtils.capitalize("")    = ""
     * LiteStringUtils.capitalize("cat") = "Cat"
     * LiteStringUtils.capitalize("cAt") = "CAt"
     * </pre>
     *
     * @param str  the String to capitalize, may be null
     * @return the capitalized String, <code>null</code> if null String input
     * @see WordUtils#capitalize(String)
     * @see #uncapitalize(String)
     * @since 2.0
     */
    public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen)
            .append(Character.toTitleCase(str.charAt(0)))
            .append(str.substring(1))
            .toString();
    }

    /**
     * <p>Capitalizes a String changing the first letter to title case as
     * per {@link Character#toTitleCase(char)}. No other letters are changed.</p>
     *
     * @param str  the String to capitalize, may be null
     * @return the capitalized String, <code>null</code> if null String input
     * @deprecated Use the standardly named {@link #capitalize(String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String capitalise(String str) {
        return capitalize(str);
    }

    /**
     * <p>Uncapitalizes a String changing the first letter to title case as
     * per {@link Character#toLowerCase(char)}. No other letters are changed.</p>
     *
     * <p>For a word based algorithm, see {@link WordUtils#uncapitalize(String)}.
     * A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.uncapitalize(null)  = null
     * LiteStringUtils.uncapitalize("")    = ""
     * LiteStringUtils.uncapitalize("Cat") = "cat"
     * LiteStringUtils.uncapitalize("CAT") = "cAT"
     * </pre>
     *
     * @param str  the String to uncapitalize, may be null
     * @return the uncapitalized String, <code>null</code> if null String input
     * @see WordUtils#uncapitalize(String)
     * @see #capitalize(String)
     * @since 2.0
     */
    public static String uncapitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen)
            .append(Character.toLowerCase(str.charAt(0)))
            .append(str.substring(1))
            .toString();
    }

    /**
     * <p>Uncapitalizes a String changing the first letter to title case as
     * per {@link Character#toLowerCase(char)}. No other letters are changed.</p>
     *
     * @param str  the String to uncapitalize, may be null
     * @return the uncapitalized String, <code>null</code> if null String input
     * @deprecated Use the standardly named {@link #uncapitalize(String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String uncapitalise(String str) {
        return uncapitalize(str);
    }

    /**
     * <p>Swaps the case of a String changing upper and title case to
     * lower case, and lower case to upper case.</p>
     *
     * <ul>
     *  <li>Upper case character converts to Lower case</li>
     *  <li>Title case character converts to Lower case</li>
     *  <li>Lower case character converts to Upper case</li>
     * </ul>
     *
     * <p>For a word based algorithm, see {@link WordUtils#swapCase(String)}.
     * A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * LiteStringUtils.swapCase(null)                 = null
     * LiteStringUtils.swapCase("")                   = ""
     * LiteStringUtils.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer performs a word based algorithm.
     * If you only use ASCII, you will notice no change.
     * That functionality is available in WordUtils.</p>
     *
     * @param str  the String to swap case, may be null
     * @return the changed String, <code>null</code> if null String input
     */
    public static String swapCase(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        StringBuffer buffer = new StringBuffer(strLen);

        char ch = 0;
        for (int i = 0; i < strLen; i++) {
            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                ch = Character.toUpperCase(ch);
            }
            buffer.append(ch);
        }
        return buffer.toString();
    }

    // Count matches
    //-----------------------------------------------------------------------
    /**
     * <p>Counts how many times the substring appears in the larger String.</p>
     *
     * <p>A <code>null</code> or empty ("") String input returns <code>0</code>.</p>
     *
     * <pre>
     * LiteStringUtils.countMatches(null, *)       = 0
     * LiteStringUtils.countMatches("", *)         = 0
     * LiteStringUtils.countMatches("abba", null)  = 0
     * LiteStringUtils.countMatches("abba", "")    = 0
     * LiteStringUtils.countMatches("abba", "a")   = 2
     * LiteStringUtils.countMatches("abba", "ab")  = 1
     * LiteStringUtils.countMatches("abba", "xxx") = 0
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param sub  the substring to count, may be null
     * @return the number of occurrences, 0 if either String is <code>null</code>
     */
    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    // Character Tests
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if the String contains only unicode letters.</p>
     *
     * <p><code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.</p>
     *
     * <pre>
     * LiteStringUtils.isAlpha(null)   = false
     * LiteStringUtils.isAlpha("")     = true
     * LiteStringUtils.isAlpha("  ")   = false
     * LiteStringUtils.isAlpha("abc")  = true
     * LiteStringUtils.isAlpha("ab2c") = false
     * LiteStringUtils.isAlpha("ab-c") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains letters, and is non-null
     */
    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetter(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the String contains only unicode letters and
     * space (' ').</p>
     *
     * <p><code>null</code> will return <code>false</code>
     * An empty String ("") will return <code>true</code>.</p>
     *
     * <pre>
     * LiteStringUtils.isAlphaSpace(null)   = false
     * LiteStringUtils.isAlphaSpace("")     = true
     * LiteStringUtils.isAlphaSpace("  ")   = true
     * LiteStringUtils.isAlphaSpace("abc")  = true
     * LiteStringUtils.isAlphaSpace("ab c") = true
     * LiteStringUtils.isAlphaSpace("ab2c") = false
     * LiteStringUtils.isAlphaSpace("ab-c") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains letters and space,
     *  and is non-null
     */
    public static boolean isAlphaSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isLetter(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the String contains only unicode letters or digits.</p>
     *
     * <p><code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.</p>
     *
     * <pre>
     * LiteStringUtils.isAlphanumeric(null)   = false
     * LiteStringUtils.isAlphanumeric("")     = true
     * LiteStringUtils.isAlphanumeric("  ")   = false
     * LiteStringUtils.isAlphanumeric("abc")  = true
     * LiteStringUtils.isAlphanumeric("ab c") = false
     * LiteStringUtils.isAlphanumeric("ab2c") = true
     * LiteStringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains letters or digits,
     *  and is non-null
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetterOrDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the String contains only unicode letters, digits
     * or space (<code>' '</code>).</p>
     *
     * <p><code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.</p>
     *
     * <pre>
     * LiteStringUtils.isAlphanumeric(null)   = false
     * LiteStringUtils.isAlphanumeric("")     = true
     * LiteStringUtils.isAlphanumeric("  ")   = true
     * LiteStringUtils.isAlphanumeric("abc")  = true
     * LiteStringUtils.isAlphanumeric("ab c") = true
     * LiteStringUtils.isAlphanumeric("ab2c") = true
     * LiteStringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains letters, digits or space,
     *  and is non-null
     */
    public static boolean isAlphanumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isLetterOrDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the String contains only unicode digits.
     * A decimal point is not a unicode digit and returns false.</p>
     *
     * <p><code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.</p>
     *
     * <pre>
     * LiteStringUtils.isNumeric(null)   = false
     * LiteStringUtils.isNumeric("")     = true
     * LiteStringUtils.isNumeric("  ")   = false
     * LiteStringUtils.isNumeric("123")  = true
     * LiteStringUtils.isNumeric("12 3") = false
     * LiteStringUtils.isNumeric("ab2c") = false
     * LiteStringUtils.isNumeric("12-3") = false
     * LiteStringUtils.isNumeric("12.3") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains digits, and is non-null
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the String contains only unicode digits or space
     * (<code>' '</code>).
     * A decimal point is not a unicode digit and returns false.</p>
     *
     * <p><code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.</p>
     *
     * <pre>
     * LiteStringUtils.isNumeric(null)   = false
     * LiteStringUtils.isNumeric("")     = true
     * LiteStringUtils.isNumeric("  ")   = true
     * LiteStringUtils.isNumeric("123")  = true
     * LiteStringUtils.isNumeric("12 3") = true
     * LiteStringUtils.isNumeric("ab2c") = false
     * LiteStringUtils.isNumeric("12-3") = false
     * LiteStringUtils.isNumeric("12.3") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains digits or space,
     *  and is non-null
     */
    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the String contains only whitespace.</p>
     *
     * <p><code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.</p>
     *
     * <pre>
     * LiteStringUtils.isWhitespace(null)   = false
     * LiteStringUtils.isWhitespace("")     = true
     * LiteStringUtils.isWhitespace("  ")   = true
     * LiteStringUtils.isWhitespace("abc")  = false
     * LiteStringUtils.isWhitespace("ab2c") = false
     * LiteStringUtils.isWhitespace("ab-c") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains whitespace, and is non-null
     * @since 2.0
     */
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    // Defaults
    //-----------------------------------------------------------------------
    /**
     * <p>Returns either the passed in String,
     * or if the String is <code>null</code>, an empty String ("").</p>
     *
     * <pre>
     * LiteStringUtils.defaultString(null)  = ""
     * LiteStringUtils.defaultString("")    = ""
     * LiteStringUtils.defaultString("bat") = "bat"
     * </pre>
     *
     * @see ObjectUtils#toString(Object)
     * @see String#valueOf(Object)
     * @param str  the String to check, may be null
     * @return the passed in String, or the empty String if it
     *  was <code>null</code>
     */
    public static String defaultString(String str) {
        return str == null ? EMPTY : str;
    }

    /**
     * <p>Returns either the passed in String, or if the String is
     * <code>null</code>, the value of <code>defaultStr</code>.</p>
     *
     * <pre>
     * LiteStringUtils.defaultString(null, "NULL")  = "NULL"
     * LiteStringUtils.defaultString("", "NULL")    = ""
     * LiteStringUtils.defaultString("bat", "NULL") = "bat"
     * </pre>
     *
     * @see ObjectUtils#toString(Object,String)
     * @see String#valueOf(Object)
     * @param str  the String to check, may be null
     * @param defaultStr  the default String to return
     *  if the input is <code>null</code>, may be null
     * @return the passed in String, or the default if it was <code>null</code>
     */
    public static String defaultString(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * <p>Returns either the passed in String, or if the String is
     * empty or <code>null</code>, the value of <code>defaultStr</code>.</p>
     *
     * <pre>
     * LiteStringUtils.defaultIfEmpty(null, "NULL")  = "NULL"
     * LiteStringUtils.defaultIfEmpty("", "NULL")    = "NULL"
     * LiteStringUtils.defaultIfEmpty("bat", "NULL") = "bat"
     * </pre>
     *
     * @see LiteStringUtils#defaultString(String, String)
     * @param str  the String to check, may be null
     * @param defaultStr  the default String to return
     *  if the input is empty ("") or <code>null</code>, may be null
     * @return the passed in String, or the default
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return LiteStringUtils.isEmpty(str) ? defaultStr : str;
    }

    // Abbreviating
    //-----------------------------------------------------------------------
    /**
     * <p>Abbreviates a String using ellipses. This will turn
     * "Now is the time for all good men" into "Now is the time for..."</p>
     *
     * <p>Specifically:
     * <ul>
     *   <li>If <code>str</code> is less than <code>maxWidth</code> characters
     *       long, return it.</li>
     *   <li>Else abbreviate it to <code>(substring(str, 0, max-3) + "...")</code>.</li>
     *   <li>If <code>maxWidth</code> is less than <code>4</code>, throw an
     *       <code>IllegalArgumentException</code>.</li>
     *   <li>In no case will it return a String of length greater than
     *       <code>maxWidth</code>.</li>
     * </ul>
     * </p>
     *
     * <pre>
     * LiteStringUtils.abbreviate(null, *)      = null
     * LiteStringUtils.abbreviate("", 4)        = ""
     * LiteStringUtils.abbreviate("abcdefg", 6) = "abc..."
     * LiteStringUtils.abbreviate("abcdefg", 7) = "abcdefg"
     * LiteStringUtils.abbreviate("abcdefg", 8) = "abcdefg"
     * LiteStringUtils.abbreviate("abcdefg", 4) = "a..."
     * LiteStringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param maxWidth  maximum length of result String, must be at least 4
     * @return abbreviated String, <code>null</code> if null String input
     * @throws IllegalArgumentException if the width is too small
     * @since 2.0
     */
    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    /**
     * <p>Abbreviates a String using ellipses. This will turn
     * "Now is the time for all good men" into "...is the time for..."</p>
     *
     * <p>Works like <code>abbreviate(String, int)</code>, but allows you to specify
     * a "left edge" offset.  Note that this left edge is not necessarily going to
     * be the leftmost character in the result, or the first character following the
     * ellipses, but it will appear somewhere in the result.
     *
     * <p>In no case will it return a String of length greater than
     * <code>maxWidth</code>.</p>
     *
     * <pre>
     * LiteStringUtils.abbreviate(null, *, *)                = null
     * LiteStringUtils.abbreviate("", 0, 4)                  = ""
     * LiteStringUtils.abbreviate("abcdefghijklmno", -1, 10) = "abcdefg..."
     * LiteStringUtils.abbreviate("abcdefghijklmno", 0, 10)  = "abcdefg..."
     * LiteStringUtils.abbreviate("abcdefghijklmno", 1, 10)  = "abcdefg..."
     * LiteStringUtils.abbreviate("abcdefghijklmno", 4, 10)  = "abcdefg..."
     * LiteStringUtils.abbreviate("abcdefghijklmno", 5, 10)  = "...fghi..."
     * LiteStringUtils.abbreviate("abcdefghijklmno", 6, 10)  = "...ghij..."
     * LiteStringUtils.abbreviate("abcdefghijklmno", 8, 10)  = "...ijklmno"
     * LiteStringUtils.abbreviate("abcdefghijklmno", 10, 10) = "...ijklmno"
     * LiteStringUtils.abbreviate("abcdefghijklmno", 12, 10) = "...ijklmno"
     * LiteStringUtils.abbreviate("abcdefghij", 0, 3)        = IllegalArgumentException
     * LiteStringUtils.abbreviate("abcdefghij", 5, 6)        = IllegalArgumentException
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param offset  left edge of source String
     * @param maxWidth  maximum length of result String, must be at least 4
     * @return abbreviated String, <code>null</code> if null String input
     * @throws IllegalArgumentException if the width is too small
     * @since 2.0
     */
    public static String abbreviate(String str, int offset, int maxWidth) {
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if ((str.length() - offset) < (maxWidth - 3)) {
            offset = str.length() - (maxWidth - 3);
        }
        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + "...";
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if ((offset + (maxWidth - 3)) < str.length()) {
            return "..." + abbreviate(str.substring(offset), maxWidth - 3);
        }
        return "..." + str.substring(str.length() - (maxWidth - 3));
    }

    // Difference
    //-----------------------------------------------------------------------
    /**
     * <p>Compares two Strings, and returns the portion where they differ.
     * (More precisely, return the remainder of the second String,
     * starting from where it's different from the first.)</p>
     *
     * <p>For example,
     * <code>difference("i am a machine", "i am a robot") -> "robot"</code>.</p>
     *
     * <pre>
     * LiteStringUtils.difference(null, null) = null
     * LiteStringUtils.difference("", "") = ""
     * LiteStringUtils.difference("", "abc") = "abc"
     * LiteStringUtils.difference("abc", "") = ""
     * LiteStringUtils.difference("abc", "abc") = ""
     * LiteStringUtils.difference("ab", "abxyz") = "xyz"
     * LiteStringUtils.difference("abcde", "abxyz") = "xyz"
     * LiteStringUtils.difference("abcde", "xyz") = "xyz"
     * </pre>
     *
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return the portion of str2 where it differs from str1; returns the
     * empty String if they are equal
     * @since 2.0
     */
    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        int at = indexOfDifference(str1, str2);
        if (at == -1) {
            return EMPTY;
        }
        return str2.substring(at);
    }

    /**
     * <p>Compares two Strings, and returns the index at which the
     * Strings begin to differ.</p>
     *
     * <p>For example,
     * <code>indexOfDifference("i am a machine", "i am a robot") -> 7</code></p>
     *
     * <pre>
     * LiteStringUtils.indexOfDifference(null, null) = -1
     * LiteStringUtils.indexOfDifference("", "") = -1
     * LiteStringUtils.indexOfDifference("", "abc") = 0
     * LiteStringUtils.indexOfDifference("abc", "") = 0
     * LiteStringUtils.indexOfDifference("abc", "abc") = -1
     * LiteStringUtils.indexOfDifference("ab", "abxyz") = 2
     * LiteStringUtils.indexOfDifference("abcde", "abxyz") = 2
     * LiteStringUtils.indexOfDifference("abcde", "xyz") = 0
     * </pre>
     *
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return the index where str2 and str1 begin to differ; -1 if they are equal
     * @since 2.0
     */
    public static int indexOfDifference(String str1, String str2) {
        if (str1 == str2) {
            return -1;
        }
        if (str1 == null || str2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < str1.length() && i < str2.length(); ++i) {
            if (str1.charAt(i) != str2.charAt(i)) {
                break;
            }
        }
        if (i < str2.length() || i < str1.length()) {
            return i;
        }
        return -1;
    }

    /**
     * <p>Compares all Strings in an array and returns the index at which the
     * Strings begin to differ.</p>
     *
     * <p>For example,
     * <code>indexOfDifference(new String[] {"i am a machine", "i am a robot"}) -> 7</code></p>
     *
     * <pre>
     * LiteStringUtils.indexOfDifference(null) = -1
     * LiteStringUtils.indexOfDifference(new String[] {}) = -1
     * LiteStringUtils.indexOfDifference(new String[] {"abc"}) = -1
     * LiteStringUtils.indexOfDifference(new String[] {null, null}) = -1
     * LiteStringUtils.indexOfDifference(new String[] {"", ""}) = -1
     * LiteStringUtils.indexOfDifference(new String[] {"", null}) = 0
     * LiteStringUtils.indexOfDifference(new String[] {"abc", null, null}) = 0
     * LiteStringUtils.indexOfDifference(new String[] {null, null, "abc"}) = 0
     * LiteStringUtils.indexOfDifference(new String[] {"", "abc"}) = 0
     * LiteStringUtils.indexOfDifference(new String[] {"abc", ""}) = 0
     * LiteStringUtils.indexOfDifference(new String[] {"abc", "abc"}) = -1
     * LiteStringUtils.indexOfDifference(new String[] {"abc", "a"}) = 1
     * LiteStringUtils.indexOfDifference(new String[] {"ab", "abxyz"}) = 2
     * LiteStringUtils.indexOfDifference(new String[] {"abcde", "abxyz"}) = 2
     * LiteStringUtils.indexOfDifference(new String[] {"abcde", "xyz"}) = 0
     * LiteStringUtils.indexOfDifference(new String[] {"xyz", "abcde"}) = 0
     * LiteStringUtils.indexOfDifference(new String[] {"i am a machine", "i am a robot"}) = 7
     * </pre>
     *
     * @param strs  array of strings, entries may be null
     * @return the index where the strings begin to differ; -1 if they are all equal
     * @since 2.4
     */
    public static int indexOfDifference(String[] strs) {
        if (strs == null || strs.length <= 1) {
            return -1;
        }
        boolean anyStringNull = false;
        boolean allStringsNull = true;
        int arrayLen = strs.length;
        int shortestStrLen = Integer.MAX_VALUE;
        int longestStrLen = 0;

        // find the min and max string lengths; this avoids checking to make
        // sure we are not exceeding the length of the string each time through
        // the bottom loop.
        for (int i = 0; i < arrayLen; i++) {
            if (strs[i] == null) {
                anyStringNull = true;
                shortestStrLen = 0;
            } else {
                allStringsNull = false;
                shortestStrLen = Math.min(strs[i].length(), shortestStrLen);
                longestStrLen = Math.max(strs[i].length(), longestStrLen);
            }
        }

        // handle lists containing all nulls or all empty strings
        if (allStringsNull || (longestStrLen == 0 && !anyStringNull)) {
            return -1;
        }

        // handle lists containing some nulls or some empty strings
        if (shortestStrLen == 0) {
            return 0;
        }

        // find the position with the first difference across all strings
        int firstDiff = -1;
        for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
            char comparisonChar = strs[0].charAt(stringPos);
            for (int arrayPos = 1; arrayPos < arrayLen; arrayPos++) {
                if (strs[arrayPos].charAt(stringPos) != comparisonChar) {
                    firstDiff = stringPos;
                    break;
                }
            }
            if (firstDiff != -1) {
                break;
            }
        }

        if (firstDiff == -1 && shortestStrLen != longestStrLen) {
            // we compared all of the characters up to the length of the
            // shortest string and didn't find a match, but the string lengths
            // vary, so return the length of the shortest string.
            return shortestStrLen;
        }
        return firstDiff;
    }
    
    /**
     * <p>Compares all Strings in an array and returns the initial sequence of 
     * characters that is common to all of them.</p>
     *
     * <p>For example,
     * <code>getCommonPrefix(new String[] {"i am a machine", "i am a robot"}) -> "i am a "</code></p>
     *
     * <pre>
     * LiteStringUtils.getCommonPrefix(null) = ""
     * LiteStringUtils.getCommonPrefix(new String[] {}) = ""
     * LiteStringUtils.getCommonPrefix(new String[] {"abc"}) = "abc"
     * LiteStringUtils.getCommonPrefix(new String[] {null, null}) = ""
     * LiteStringUtils.getCommonPrefix(new String[] {"", ""}) = ""
     * LiteStringUtils.getCommonPrefix(new String[] {"", null}) = ""
     * LiteStringUtils.getCommonPrefix(new String[] {"abc", null, null}) = ""
     * LiteStringUtils.getCommonPrefix(new String[] {null, null, "abc"}) = ""
     * LiteStringUtils.getCommonPrefix(new String[] {"", "abc"}) = ""
     * LiteStringUtils.getCommonPrefix(new String[] {"abc", ""}) = ""
     * LiteStringUtils.getCommonPrefix(new String[] {"abc", "abc"}) = "abc"
     * LiteStringUtils.getCommonPrefix(new String[] {"abc", "a"}) = "a"
     * LiteStringUtils.getCommonPrefix(new String[] {"ab", "abxyz"}) = "ab"
     * LiteStringUtils.getCommonPrefix(new String[] {"abcde", "abxyz"}) = "ab"
     * LiteStringUtils.getCommonPrefix(new String[] {"abcde", "xyz"}) = ""
     * LiteStringUtils.getCommonPrefix(new String[] {"xyz", "abcde"}) = ""
     * LiteStringUtils.getCommonPrefix(new String[] {"i am a machine", "i am a robot"}) = "i am a "
     * </pre>
     *
     * @param strs  array of String objects, entries may be null
     * @return the initial sequence of characters that are common to all Strings
     * in the array; empty String if the array is null, the elements are all null 
     * or if there is no common prefix. 
     * @since 2.4
     */
    public static String getCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return EMPTY;
        }
        int smallestIndexOfDiff = indexOfDifference(strs);
        if (smallestIndexOfDiff == -1) {
            // all strings were identical
            if (strs[0] == null) {
                return EMPTY;
            }
            return strs[0];
        } else if (smallestIndexOfDiff == 0) {
            // there were no common initial characters
            return EMPTY;
        } else {
            // we found a common initial character sequence
            return strs[0].substring(0, smallestIndexOfDiff);
        }
    }  
    
    // Misc
    //-----------------------------------------------------------------------
    /**
     * <p>Find the Levenshtein distance between two Strings.</p>
     *
     * <p>This is the number of changes needed to change one String into
     * another, where each change is a single character modification (deletion,
     * insertion or substitution).</p>
     *
     * <p>The previous implementation of the Levenshtein distance algorithm
     * was from <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a></p>
     *
     * <p>Chas Emerick has written an implementation in Java, which avoids an OutOfMemoryError
     * which can occur when my Java implementation is used with very large strings.<br>
     * This implementation of the Levenshtein distance algorithm
     * is from <a href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.com/ldjava.htm</a></p>
     *
     * <pre>
     * LiteStringUtils.getLevenshteinDistance(null, *)             = IllegalArgumentException
     * LiteStringUtils.getLevenshteinDistance(*, null)             = IllegalArgumentException
     * LiteStringUtils.getLevenshteinDistance("","")               = 0
     * LiteStringUtils.getLevenshteinDistance("","a")              = 1
     * LiteStringUtils.getLevenshteinDistance("aaapppp", "")       = 7
     * LiteStringUtils.getLevenshteinDistance("frog", "fog")       = 1
     * LiteStringUtils.getLevenshteinDistance("fly", "ant")        = 3
     * LiteStringUtils.getLevenshteinDistance("elephant", "hippo") = 7
     * LiteStringUtils.getLevenshteinDistance("hippo", "elephant") = 7
     * LiteStringUtils.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
     * LiteStringUtils.getLevenshteinDistance("hello", "hallo")    = 1
     * </pre>
     *
     * @param s  the first String, must not be null
     * @param t  the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input <code>null</code>
     */
    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        /*
           The difference between this impl. and the previous is that, rather 
           than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
           we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
           is the 'current working' distance array that maintains the newest distance cost
           counts as we iterate through the characters of String s.  Each time we increment
           the index of String t we are comparing, d is copied to p, the second int[].  Doing so
           allows us to retain the previous cost counts as required by the algorithm (taking 
           the minimum of the cost count to the left, up one, and diagonally up and to the left
           of the current cost count being calculated).  (Note that the arrays aren't really 
           copied anymore, just switched...this is clearly much better than cloning an array 
           or doing a System.arraycopy() each time  through the outer loop.)

           Effectively, the difference between the two implementations is this one does not 
           cause an out of memory condition when calculating the LD over two very large strings.
         */

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            String tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int p[] = new int[n+1]; //'previous' cost array, horizontally
        int d[] = new int[n+1]; // cost array, horizontally
        int _d[]; //placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i<=n; i++) {
            p[i] = i;
        }

        for (j = 1; j<=m; j++) {
            t_j = t.charAt(j-1);
            d[0] = j;

            for (i=1; i<=n; i++) {
                cost = s.charAt(i-1)==t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now 
        // actually has the most recent cost counts
        return p[n];
    }

    /**
     * <p>Gets the minimum of three <code>int</code> values.</p>
     *
     * @param a  value 1
     * @param b  value 2
     * @param c  value 3
     * @return  the smallest of the values
     */
/*
    private static int min(int a, int b, int c) {
        // Method copied from NumberUtils to avoid dependency on subpackage
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }
*/

    // startsWith
    //-----------------------------------------------------------------------

    /**
     * <p>Check if a String starts with a specified prefix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * LiteStringUtils.startsWith(null, null)      = true
     * LiteStringUtils.startsWith(null, "abcdef")  = false
     * LiteStringUtils.startsWith("abc", null)     = false
     * LiteStringUtils.startsWith("abc", "abcdef") = true
     * LiteStringUtils.startsWith("abc", "ABCDEF") = false
     * </pre>
     *
     * @see String#startsWith(String)
     * @param str  the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @return <code>true</code> if the String starts with the prefix, case sensitive, or
     *  both <code>null</code>
     * @since 2.4
     */
    public static boolean startsWith(String str, String prefix) {
        return startsWith(str, prefix, false);
    }

    /**
     * <p>Case insensitive check if a String starts with a specified prefix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case insensitive.</p>
     *
     * <pre>
     * LiteStringUtils.startsWithIgnoreCase(null, null)      = true
     * LiteStringUtils.startsWithIgnoreCase(null, "abcdef")  = false
     * LiteStringUtils.startsWithIgnoreCase("abc", null)     = false
     * LiteStringUtils.startsWithIgnoreCase("abc", "abcdef") = true
     * LiteStringUtils.startsWithIgnoreCase("abc", "ABCDEF") = true
     * </pre>
     *
     * @see String#startsWith(String)
     * @param str  the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @return <code>true</code> if the String starts with the prefix, case insensitive, or
     *  both <code>null</code>
     * @since 2.4
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return startsWith(str, prefix, true);
    }

    /**
     * <p>Check if a String starts with a specified prefix (optionally case insensitive).</p>
     *
     * @see String#startsWith(String)
     * @param str  the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case
     *  (case insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or
     *  both <code>null</code>
     */
    private static boolean startsWith(String str, String prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            return (str == null && prefix == null);
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
    }

    // endsWith
    //-----------------------------------------------------------------------

    /**
     * <p>Check if a String ends with a specified suffix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * LiteStringUtils.endsWith(null, null)      = true
     * LiteStringUtils.endsWith(null, "abcdef")  = false
     * LiteStringUtils.endsWith("def", null)     = false
     * LiteStringUtils.endsWith("def", "abcdef") = true
     * LiteStringUtils.endsWith("def", "ABCDEF") = false
     * </pre>
     *
     * @see String#endsWith(String)
     * @param str  the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case sensitive, or
     *  both <code>null</code>
     * @since 2.4
     */
    public static boolean endsWith(String str, String suffix) {
        return endsWith(str, suffix, false);
    }

    /**
     * <p>Case insensitive check if a String ends with a specified suffix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case insensitive.</p>
     *
     * <pre>
     * LiteStringUtils.endsWithIgnoreCase(null, null)      = true
     * LiteStringUtils.endsWithIgnoreCase(null, "abcdef")  = false
     * LiteStringUtils.endsWithIgnoreCase("def", null)     = false
     * LiteStringUtils.endsWithIgnoreCase("def", "abcdef") = true
     * LiteStringUtils.endsWithIgnoreCase("def", "ABCDEF") = false
     * </pre>
     *
     * @see String#endsWith(String)
     * @param str  the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case insensitive, or
     *  both <code>null</code>
     * @since 2.4
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return endsWith(str, suffix, true);
    }

    /**
     * <p>Check if a String ends with a specified suffix (optionally case insensitive).</p>
     *
     * @see String#endsWith(String)
     * @param str  the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case
     *  (case insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or
     *  both <code>null</code>
     */
    private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return (str == null && suffix == null);
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
    }

}
