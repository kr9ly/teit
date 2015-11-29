package net.kr9ly.teit.util;

import net.kr9ly.teit.token.Token;
import net.kr9ly.teit.token.TokenType;

import static org.junit.Assert.assertEquals;

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
public class ReaderTestUtils extends TestUtils {

    public ReaderTestUtils(String message) {
        super(message);
    }

    public void expect(Token token, TokenType tokenType, String tokenString, int column, int row) {
        assertEquals(message, tokenType, token.getTokenType());
        assertEquals(message, tokenString, token.getTokenString());
        assertEquals(message, column, token.getColumn());
        assertEquals(message, row, token.getRow());
    }

    public void expect(Token token, TokenType tokenType,int column, int row) {
        assertEquals(message, tokenType, token.getTokenType());
        assertEquals(message, column, token.getColumn());
        assertEquals(message, row, token.getRow());
    }
}
