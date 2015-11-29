package net.kr9ly.teit.util;

import net.kr9ly.teit.model.expression.IdentifierModel;

import org.junit.Assert;

/**
 * Copyright 2015 kr9ly
 * <br />
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <br />
 * http://www.apache.org/licenses/LICENSE-2.0
 * <br />
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class TestUtils {

    protected String message;

    public TestUtils(String message) {
        this.message = message;
    }

    public void expect(Object expected, Object actual) {
        Assert.assertEquals(message, expected, actual);
    }

    public void expect(boolean condition) {
        Assert.assertTrue(message, condition);
    }

    public <T> void expect(Object object, Class<T> expectClass) {
        Assert.assertTrue(message, expectClass.isInstance(object));
    }

    public <T> void expect(Object object, Class<T> expectClass, ExpectBlock<T> block) {
        expect(object, expectClass);
        block.assertion(expectClass.cast(object));
    }
}
