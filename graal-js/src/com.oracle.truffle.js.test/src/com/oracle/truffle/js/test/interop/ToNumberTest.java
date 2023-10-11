/*
 * Copyright (c) 2019, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.js.test.interop;

import static org.junit.Assert.assertTrue;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Test;

import com.oracle.truffle.js.lang.JavaScriptLanguage;
import com.oracle.truffle.js.test.JSTest;

public class ToNumberTest {

    @Test
    public void testForeignArrayLength() {
        try (Context context = JSTest.newContextBuilder().allowAllAccess(true).build()) {
            String jscode = "var array = [42,211];\n" +
                            "array.length = new java.lang.StringBuilder('1');\n" +
                            "(array.length == 1 && array[0] == 42) || array.toString()";
            Value value = context.eval(JavaScriptLanguage.ID, jscode);
            assertTrue(value.toString(), value.isBoolean());
            assertTrue(value.toString(), value.asBoolean());
        }
    }

    @Test
    public void testForeignCompareFnResult() {
        try (Context context = JSTest.newContextBuilder().allowAllAccess(true).build()) {
            String jscode = "var array = [211,42];\n" +
                            "array.sort(function(x,y) { return java.math.BigInteger.valueOf(x - y); });\n" +
                            "(array.length == 2 && array[0] == 42 && array[1] == 211) || array.toString()";
            Value value = context.eval(JavaScriptLanguage.ID, jscode);
            assertTrue(value.toString(), value.isBoolean());
            assertTrue(value.toString(), value.asBoolean());
        }
    }

    @Test
    public void testLongIsFinite() {
        try (Context context = JSTest.newContextBuilder().allowAllAccess(true).build()) {
            String jscode = "var longValue = java.lang.Long.valueOf(1699603200000);\n" +
                            "Number.isFinite(longValue)";
            Value value = context.eval(JavaScriptLanguage.ID, jscode);
            assertTrue(value.toString(), value.isBoolean());
            assertTrue(value.toString(), value.asBoolean());
        }
    }

    @Test
    public void testLongIsInteger() {
        try (Context context = JSTest.newContextBuilder().allowAllAccess(true).build()) {
            String jscode = "var longValue = java.lang.Long.valueOf(1699603200000);\n" +
                            "Number.isInteger(longValue)";
            Value value = context.eval(JavaScriptLanguage.ID, jscode);
            assertTrue(value.toString(), value.isBoolean());
            assertTrue(value.toString(), value.asBoolean());
        }
    }

}
