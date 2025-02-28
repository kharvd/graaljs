/*
 * Copyright (c) 2018, 2023, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.js.builtins.math;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.profiles.InlinedConditionProfile;
import com.oracle.truffle.js.nodes.function.JSBuiltin;
import com.oracle.truffle.js.runtime.JSContext;
import com.oracle.truffle.js.runtime.JSRuntime;
import com.oracle.truffle.js.runtime.SafeInteger;

/**
 * Math.floor(x). Returns the greatest (closest to +Infinity) Number value that is not greater than
 * x and is an integer. If x is already an integer, the result is x.
 *
 * The value of {@code Math.floor(x)} is the same as the value of {@code -Math.ceil(-x)}.
 */
public abstract class FloorNode extends MathOperation {

    public FloorNode(JSContext context, JSBuiltin builtin) {
        super(context, builtin);
    }

    @Specialization
    protected static int floorInt(int a) {
        return a;
    }

    @Specialization
    protected static SafeInteger floorSafeInt(SafeInteger a) {
        return a;
    }

    @Specialization
    protected final Object floorDouble(double d,
                    @Cached @Shared("isZero") InlinedConditionProfile isZero,
                    @Cached @Shared("fitsInt") InlinedConditionProfile fitsInt,
                    @Cached @Shared("fitsSafeLong") InlinedConditionProfile fitsSafeLong,
                    @Cached @Shared("smaller") InlinedConditionProfile smaller) {
        if (isZero.profile(this, d == 0.0)) {
            // floor(-0.0) => -0.0
            // floor(+0.0) => +0.0
            return d;
        } else if (fitsInt.profile(this, d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE)) {
            int i = (int) d;
            return smaller.profile(this, d < i) ? i - 1 : i;
        } else if (fitsSafeLong.profile(this, JSRuntime.isSafeInteger(d))) {
            long i = (long) d;
            long result = smaller.profile(this, d < i) ? i - 1 : i;
            return SafeInteger.valueOf(result);
        } else {
            return Math.floor(d);
        }
    }

    @Specialization(replaces = "floorDouble")
    protected final Object floorToDouble(Object a,
                    @Cached @Shared("isZero") InlinedConditionProfile isZero,
                    @Cached @Shared("fitsInt") InlinedConditionProfile fitsInt,
                    @Cached @Shared("fitsSafeLong") InlinedConditionProfile fitsSafeLong,
                    @Cached @Shared("smaller") InlinedConditionProfile smaller) {
        double d = toDouble(a);
        return floorDouble(d, isZero, fitsInt, fitsSafeLong, smaller);
    }
}
