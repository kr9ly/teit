package net.kr9ly.teit.reader;

import net.kr9ly.teit.character.CharacterType;
import net.kr9ly.teit.TemplateEnvironment;
import net.kr9ly.teit.token.Token;
import net.kr9ly.teit.token.TokenType;

import java.io.IOException;
import java.io.Reader;

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
public class TemplateReader {

    private Reader reader;

    private TemplateEnvironment environment;

    private int currentColumn = 0;

    private int currentRow = 0;

    private CharacterType lastCharType;

    private Token nextToken;

    private ReaderState state = new ReaderState();

    public TemplateReader(Reader reader, TemplateEnvironment environment) {
        this.reader = reader;
        this.environment = environment;
        state.push(LexerState.DEFAULT);
    }

    public Token nextToken() {
        StringBuilder buffer = new StringBuilder();

        try {
            if (nextToken != null) {
                try {
                    return nextToken;
                } finally {
                    nextToken = null;
                }
            }

            state.row = currentRow;
            if (state.pendingChar > 0) {
                state.column = Math.max(0, currentColumn - 1);
                Token token = processChar(state.pendingChar, state, buffer, true);
                if (token != null) {
                    state.pendingChar = 0;
                    lastCharType = null;
                    return token;
                }
            }
            state.column = currentColumn;

            int i;
            while ((i = reader.read()) != -1) {
                Token token = processChar((char) i, state, buffer, false);
                if (token != null) {
                    return token;
                }
            }
        } catch (IOException e) {
            nextToken = new Token(TokenType.EOF, null, state.column, state.row);
            return new Token(state.current().getTokenType(), buffer.toString(), state.column, state.row);
        }
        nextToken = new Token(TokenType.EOF, null, state.column, state.row);
        return new Token(state.current().getTokenType(), buffer.toString(), state.column, state.row);
    }

    private Token processChar(char ch, ReaderState state, StringBuilder buffer, boolean isLastChar) {
        state.pendingChar = 0;
        CharacterType cType = getCharacterType(ch);
        switch (cType) {
            case CARRIAGE_RETURN:
                if (lastCharType == CharacterType.CARRIAGE_RETURN) {
                    nextLine(currentColumn, currentRow);
                    state.pendingChar = ch;
                    lastCharType = cType;
                    return new Token(state.current().getTokenType(), buffer.toString(), state.column, state.row);
                }
                break;
            case LINE_FEED:
                nextLine(currentColumn, currentRow);
                return new Token(state.current().getTokenType(), buffer.toString(), state.column, state.row);
            default:
                if (!isLastChar) {
                    currentColumn++;
                }
                if (lastCharType == CharacterType.CARRIAGE_RETURN) {
                    nextLine(currentColumn - 1, currentRow);
                    state.pendingChar = ch;
                    lastCharType = cType;
                    return new Token(state.current().getTokenType(), buffer.toString(), state.column, state.row);
                }
                state.current().process(state, ch, cType, buffer, environment);
                if (state.token != null) {
                    try {
                        return state.token;
                    } finally {
                        state.token = null;
                    }
                }
                if (state.skipColumnPosition) {
                    state.column = currentColumn;
                    state.skipColumnPosition = false;
                }
                break;
        }
        lastCharType = cType;
        return null;
    }

    private void nextLine(int column, int row) {
        currentRow++;
        currentColumn = 0;
        state.pendingChar = 0;
        lastCharType = null;
        nextToken = new Token(TokenType.LINE_BREAK, null, column, row);
    }

    private CharacterType getCharacterType(char ch) {
        switch (ch) {
            case ' ':
            case '　':
                return CharacterType.WHITE_SPACE;
            case '\r':
                return CharacterType.CARRIAGE_RETURN;
            case '\n':
                return CharacterType.LINE_FEED;
            case '.':
                return CharacterType.PERIOD;
            case ':':
                return CharacterType.COLON;
            case ',':
                return CharacterType.COMMA;
            case '+':
                return CharacterType.PLUS;
            case '%':
                return CharacterType.MOD;
            case '*':
                return CharacterType.MULTIPLIER;
            case '_':
                return CharacterType.UNDER_BAR;
            case '-':
                return CharacterType.HYPHEN;
            case '\'':
                return CharacterType.SINGLE_QUOTE;
            case '"':
                return CharacterType.DOUBLE_QUOTE;
            case '\\':
                return CharacterType.BACK_SLASH;
            case '/':
                return CharacterType.SLASH;
            case '|':
                return CharacterType.PIPE;
            case '(':
                return CharacterType.PARENTHESIS_OPEN;
            case ')':
                return CharacterType.PARENTHESIS_CLOSE;
            case '{':
                return CharacterType.CURLY_BRACE_OPEN;
            case '}':
                return CharacterType.CURLY_BRACE_CLOSE;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return CharacterType.DIGIT;
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                return CharacterType.ALPHABET;
            default:
                return CharacterType.OTHER;
        }
    }
}
