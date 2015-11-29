package net.kr9ly.teit.reader;

import net.kr9ly.teit.TemplateEnvironment;
import net.kr9ly.teit.token.Token;
import net.kr9ly.teit.token.TokenType;
import net.kr9ly.teit.util.ReaderTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
@SuppressWarnings("Duplicates")
@RunWith(JUnit4.class)
public class TemplateReaderTest {

    private TemplateReader newReader(String template) {
        return new TemplateReader(new StringReader(template), new TemplateEnvironment());
    }

    private List<Token> readTokens(String template) {
        TemplateReader reader = newReader(template);
        List<Token> list = new ArrayList<>();
        Token token;
        while ((token = reader.nextToken()).getTokenType() != TokenType.EOF) {
            list.add(token);
        }
        return list;
    }

    @Test
    public void testStringOnly() {
        List<Token> tokens = readTokens("The quick brown fox jumped over the lazy dog");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(1, tokens.size());
        utils.expect(tokens.get(0), TokenType.TEXT, "The quick brown fox jumped over the lazy dog", 0, 0);
    }

    @Test
    public void testLineFeed() {
        List<Token> tokens = readTokens("The quick brown\n fox jumped over the lazy dog");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(3, tokens.size());
        utils.expect(tokens.get(0), TokenType.TEXT, "The quick brown", 0, 0);
        utils.expect(tokens.get(1), TokenType.LINE_BREAK, 15, 0);
        utils.expect(tokens.get(2), TokenType.TEXT, " fox jumped over the lazy dog", 0, 1);

        tokens = readTokens("The quick brown\n\n fox jumped over the lazy dog");
        utils = new ReaderTestUtils(tokens.toString());
        utils.expect(5, tokens.size());
        utils.expect(tokens.get(1), TokenType.LINE_BREAK, 15, 0);
        utils.expect(tokens.get(2), TokenType.TEXT, "", 0, 1);
        utils.expect(tokens.get(3), TokenType.LINE_BREAK, 0, 1);
        utils.expect(tokens.get(4), TokenType.TEXT, " fox jumped over the lazy dog", 0, 2);
    }

    @Test
    public void testCarriageReturn() {
        List<Token> tokens = readTokens("The quick brown\r fox jumped over the lazy dog");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(3, tokens.size());

        utils.expect(tokens.get(0), TokenType.TEXT, "The quick brown", 0, 0);
        utils.expect(tokens.get(1), TokenType.LINE_BREAK, 15, 0);
        utils.expect(tokens.get(2), TokenType.TEXT, " fox jumped over the lazy dog", 0, 1);

        tokens = readTokens("The quick brown\r\r fox jumped over the lazy dog");
        utils = new ReaderTestUtils(tokens.toString());
        utils.expect(5, tokens.size());
        utils.expect(tokens.get(1), TokenType.LINE_BREAK, 15, 0);
        utils.expect(tokens.get(2), TokenType.TEXT, "", 0, 1);
        utils.expect(tokens.get(3), TokenType.LINE_BREAK, 0, 1);
        utils.expect(tokens.get(4), TokenType.TEXT, " fox jumped over the lazy dog", 0, 2);
    }

    @Test
    public void testCarriageReturnAndLineFeed() {
        List<Token> tokens = readTokens("The quick brown\r\n fox jumped over the lazy dog");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(3, tokens.size());
        utils.expect(tokens.get(0), TokenType.TEXT, "The quick brown", 0, 0);
        utils.expect(tokens.get(1), TokenType.LINE_BREAK, 15, 0);
        utils.expect(tokens.get(2), TokenType.TEXT, " fox jumped over the lazy dog", 0, 1);

        tokens = readTokens("The quick brown\r\n\r\n fox jumped over the lazy dog");
        utils = new ReaderTestUtils(tokens.toString());
        utils.expect(5, tokens.size());
        utils.expect(tokens.get(1), TokenType.LINE_BREAK, 15, 0);
        utils.expect(tokens.get(2), TokenType.TEXT, "", 0, 1);
        utils.expect(tokens.get(3), TokenType.LINE_BREAK, 0, 1);
        utils.expect(tokens.get(4), TokenType.TEXT, " fox jumped over the lazy dog", 0, 2);
    }

    @Test
    public void testIdentifier() {
        List<Token> tokens = readTokens("{abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789}");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(3, tokens.size());
        utils.expect(tokens.get(0), TokenType.TEXT, "", 0, 0);
        utils.expect(tokens.get(1), TokenType.IDENTIFIER, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789", 1, 0);
        utils.expect(tokens.get(2), TokenType.TEXT, "", 65, 0);
    }

    @Test
    public void testEscape() {
        List<Token> tokens = readTokens("The quick brown\\{\\} fox \\\\jumped over the lazy dog");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(1, tokens.size());
        utils.expect(tokens.get(0), TokenType.TEXT, "The quick brown{} fox \\jumped over the lazy dog", 0, 0);
    }

    @Test
    public void testBooleans() {
        List<Token> tokens = readTokens("{true false}");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(4, tokens.size());
        utils.expect(tokens.get(1), TokenType.BOOLEAN_LITERAL_TRUE, 1, 0);
        utils.expect(tokens.get(2), TokenType.BOOLEAN_LITERAL_FALSE, 6, 0);
    }

    @Test
    public void testBrokenBooleans() {
        List<Token> tokens = readTokens("{t tr tru true1 truee f fa fal fals false1 falsee}");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(13, tokens.size());
        utils.expect(tokens.get(1), TokenType.IDENTIFIER, "t", 1, 0);
        utils.expect(tokens.get(2), TokenType.IDENTIFIER, "tr", 3, 0);
        utils.expect(tokens.get(3), TokenType.IDENTIFIER, "tru", 6, 0);
        utils.expect(tokens.get(4), TokenType.IDENTIFIER, "true1", 10, 0);
        utils.expect(tokens.get(5), TokenType.IDENTIFIER, "truee", 16, 0);
        utils.expect(tokens.get(6), TokenType.IDENTIFIER, "f", 22, 0);
        utils.expect(tokens.get(7), TokenType.IDENTIFIER, "fa", 24, 0);
        utils.expect(tokens.get(8), TokenType.IDENTIFIER, "fal", 27, 0);
        utils.expect(tokens.get(9), TokenType.IDENTIFIER, "fals", 31, 0);
        utils.expect(tokens.get(10), TokenType.IDENTIFIER, "false1", 36, 0);
        utils.expect(tokens.get(11), TokenType.IDENTIFIER, "falsee", 43, 0);
    }

    @Test
    public void testNumeric() {
        List<Token> tokens = readTokens("{ - 1 123 -123 1.5 -0.23 }");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(8, tokens.size());
        utils.expect(tokens.get(1), TokenType.HYPHEN, 2, 0);
        utils.expect(tokens.get(2), TokenType.INTEGER_LITERAL, "1", 4, 0);
        utils.expect(tokens.get(3), TokenType.INTEGER_LITERAL, "123", 6, 0);
        utils.expect(tokens.get(4), TokenType.INTEGER_LITERAL, "-123", 10, 0);
        utils.expect(tokens.get(5), TokenType.DOUBLE_LITERAL, "1.5", 15, 0);
        utils.expect(tokens.get(6), TokenType.DOUBLE_LITERAL, "-0.23", 19, 0);
    }

    @Test
    public void testSingleQuoteStringLiterals() {
        List<Token> tokens = readTokens("{ 'The quick' 'brown\\' fox' 'over\" the \\\\' }");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(5, tokens.size());
        utils.expect(tokens.get(1), TokenType.STRING_LITERAL, "The quick", 2, 0);
        utils.expect(tokens.get(2), TokenType.STRING_LITERAL, "brown' fox", 14, 0);
        utils.expect(tokens.get(3), TokenType.STRING_LITERAL, "over\" the \\", 28, 0);
    }

    @Test
    public void testDoubleQuoteStringLiterals() {
        List<Token> tokens = readTokens("{ \"The quick\" \"brown\\\" fox\" \"over' the \\\\\" }");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(5, tokens.size());
        utils.expect(tokens.get(1), TokenType.STRING_LITERAL, "The quick", 2, 0);
        utils.expect(tokens.get(2), TokenType.STRING_LITERAL, "brown\" fox", 14, 0);
        utils.expect(tokens.get(3), TokenType.STRING_LITERAL, "over' the \\", 28, 0);
    }

    @Test
    public void testOperators() {
        List<Token> tokens = readTokens("{ /+%().,:- }");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(11, tokens.size());
        utils.expect(tokens.get(1), TokenType.SLASH, 2, 0);
        utils.expect(tokens.get(2), TokenType.PLUS, 3, 0);
        utils.expect(tokens.get(3), TokenType.MOD, 4, 0);
        utils.expect(tokens.get(4), TokenType.PARENTHESIS_OPEN, 5, 0);
        utils.expect(tokens.get(5), TokenType.PARENTHESIS_CLOSE, 6, 0);
        utils.expect(tokens.get(6), TokenType.PERIOD, 7, 0);
        utils.expect(tokens.get(7), TokenType.COMMA, 8, 0);
        utils.expect(tokens.get(8), TokenType.COLON, 9, 0);
        utils.expect(tokens.get(9), TokenType.HYPHEN, 10, 0);
    }

    @Test
    public void testMethodCall() {
        List<Token> tokens = readTokens("{ hoge._bar }");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(5, tokens.size());
        utils.expect(tokens.get(1), TokenType.IDENTIFIER, "hoge", 2, 0);
        utils.expect(tokens.get(2), TokenType.PERIOD, 6, 0);
        utils.expect(tokens.get(3), TokenType.IDENTIFIER, "_bar", 7, 0);
    }

    @Test
    public void testTagSet() {
        List<Token> tokens = readTokens("{if a}abcde{/if}");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(7, tokens.size());
        utils.expect(tokens.get(1), TokenType.IDENTIFIER, "if", 1, 0);
        utils.expect(tokens.get(2), TokenType.IDENTIFIER, "a", 4, 0);
        utils.expect(tokens.get(3), TokenType.TEXT, "abcde", 6, 0);
        utils.expect(tokens.get(4), TokenType.SLASH, 12, 0);
        utils.expect(tokens.get(5), TokenType.IDENTIFIER, "if", 13, 0);
    }

    @Test
    public void testMultilineTemplate() {
        List<Token> tokens = readTokens("abcde{ if foo is a }\naaaaa\n{ else }ccccc\nbbbbb\n{ /if }");
        ReaderTestUtils utils = new ReaderTestUtils(tokens.toString());
        utils.expect(19, tokens.size());
        utils.expect(tokens.get(0), TokenType.TEXT, "abcde", 0, 0);
        utils.expect(tokens.get(1), TokenType.IDENTIFIER, "if", 7, 0);
        utils.expect(tokens.get(2), TokenType.IDENTIFIER, "foo", 10, 0);
        utils.expect(tokens.get(3), TokenType.IDENTIFIER, "is", 14, 0);
        utils.expect(tokens.get(4), TokenType.IDENTIFIER, "a", 17, 0);
        utils.expect(tokens.get(5), TokenType.TEXT, 20, 0);
        utils.expect(tokens.get(6), TokenType.LINE_BREAK, 20, 0);
        utils.expect(tokens.get(7), TokenType.TEXT, "aaaaa", 0, 1);
        utils.expect(tokens.get(8), TokenType.LINE_BREAK, 5, 1);
        utils.expect(tokens.get(9), TokenType.TEXT, 0, 2);
        utils.expect(tokens.get(10), TokenType.IDENTIFIER, "else", 2, 2);
        utils.expect(tokens.get(11), TokenType.TEXT, "ccccc", 8, 2);
        utils.expect(tokens.get(12), TokenType.LINE_BREAK, 13, 2);
        utils.expect(tokens.get(13), TokenType.TEXT, "bbbbb", 0, 3);
        utils.expect(tokens.get(14), TokenType.LINE_BREAK, 5, 3);
        utils.expect(tokens.get(15), TokenType.TEXT, "", 0, 4);
        utils.expect(tokens.get(16), TokenType.SLASH, 2, 4);
        utils.expect(tokens.get(17), TokenType.IDENTIFIER, "if", 3, 4);
        utils.expect(tokens.get(18), TokenType.TEXT, 7, 4);
    }
}
