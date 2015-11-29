package net.kr9ly.teit.reader;

import net.kr9ly.teit.character.CharacterType;
import net.kr9ly.teit.TemplateEnvironment;
import net.kr9ly.teit.token.Token;
import net.kr9ly.teit.token.TokenType;

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
public enum LexerState {
    DEFAULT {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            switch (cType) {
                case WHITE_SPACE:
                    buffer.append(ch);
                    return;
                case BACK_SLASH:
                    state.push(ESCAPING);
                    return;
                case CURLY_BRACE_OPEN:
                    state.token = new Token(TokenType.TEXT, buffer.toString(), state.column, state.row);
                    state.push(TAG);
                    return;
                default:
                    buffer.append(ch);
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.TEXT;
        }
    },
    TAG {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            switch (cType) {
                case WHITE_SPACE:
                    state.skipColumnPosition = true;
                    return;
                case SLASH:
                    state.token = new Token(TokenType.SLASH, buffer.append(ch).toString(), state.column, state.row);
                    return;
                case PLUS:
                    state.token = new Token(TokenType.PLUS, buffer.append(ch).toString(), state.column, state.row);
                    return;
                case MOD:
                    state.token = new Token(TokenType.MOD, buffer.append(ch).toString(), state.column, state.row);
                    return;
                case MULTIPLIER:
                    state.token = new Token(TokenType.MULTIPLIER, buffer.append(ch).toString(), state.column, state.row);
                    return;
                case PARENTHESIS_OPEN:
                    state.token = new Token(TokenType.PARENTHESIS_OPEN, buffer.append(ch).toString(), state.column, state.row);
                    return;
                case PARENTHESIS_CLOSE:
                    state.token = new Token(TokenType.PARENTHESIS_CLOSE, buffer.append(ch).toString(), state.column, state.row);
                    return;
                case BACK_SLASH:
                    state.push(ESCAPING);
                    return;
                case SINGLE_QUOTE:
                    buffer.append(ch);
                    state.push(SINGLE_STRING_LITERAL);
                    return;
                case DOUBLE_QUOTE:
                    buffer.append(ch);
                    state.push(DOUBLE_STRING_LITERAL);
                    return;
                case HYPHEN:
                case DIGIT:
                    buffer.append(ch);
                    state.push(NUMERIC);
                    return;
                case ALPHABET:
                case UNDER_BAR:
                    switch (ch) {
                        case 't':
                            buffer.append(ch);
                            state.push(BOOLEAN_T);
                            return;
                        case 'f':
                            buffer.append(ch);
                            state.push(BOOLEAN_F);
                            return;
                        default:
                            buffer.append(ch);
                            state.push(IDENTIFIER);
                            return;
                    }
                case PERIOD:
                    state.token = new Token(TokenType.PERIOD, buffer.append(ch).toString(), state.column, state.row);
                    return;
                case COMMA:
                    state.token = new Token(TokenType.COMMA, buffer.append(ch).toString(), state.column, state.row);
                    return;
                case COLON:
                    state.token = new Token(TokenType.COLON, buffer.append(ch).toString(), state.column, state.row);
                    return;
                case CURLY_BRACE_CLOSE:
                    state.skipColumnPosition = true;
                    state.pop();
                    return;
                default:
                    state.token = new Token(TokenType.UNKNOWN, buffer.append(ch).toString(), state.column, state.row);
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    },
    IDENTIFIER {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            switch (cType) {
                case ALPHABET:
                case DIGIT:
                case UNDER_BAR:
                    buffer.append(ch);
                    return;
                default:
                    state.pendingChar = ch;
                    state.token = new Token(TokenType.IDENTIFIER, buffer.toString(), state.column, state.row);
                    state.pop();
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.IDENTIFIER;
        }
    },
    BOOLEAN_T {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            if (ch == 'r') {
                buffer.append(ch);
                state.pop();
                state.push(BOOLEAN_R);
            } else {
                booleanReturn(state, ch, cType, buffer);
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    },
    BOOLEAN_R {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            if (ch == 'u') {
                buffer.append(ch);
                state.pop();
                state.push(BOOLEAN_U);
            } else {
                booleanReturn(state, ch, cType, buffer);
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    },
    BOOLEAN_U {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            toBooleanLastLetter(state, ch, cType, buffer, BOOLEAN_TRUE_E);
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    },
    BOOLEAN_TRUE_E {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            switch (cType) {
                case DIGIT:
                case ALPHABET:
                case HYPHEN:
                    buffer.append(ch);
                    state.pop();
                    state.push(IDENTIFIER);
                    return;
                default:
                    state.pendingChar = ch;
                    state.token = new Token(TokenType.BOOLEAN_LITERAL_TRUE, buffer.toString(), state.column, state.row);
                    state.pop();
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    },
    BOOLEAN_F {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            if (ch == 'a') {
                buffer.append(ch);
                state.pop();
                state.push(BOOLEAN_A);
            } else {
                booleanReturn(state, ch, cType, buffer);
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    },
    BOOLEAN_A {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            if (ch == 'l') {
                buffer.append(ch);
                state.pop();
                state.push(BOOLEAN_L);
            } else {
                booleanReturn(state, ch, cType, buffer);
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    },
    BOOLEAN_L {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            if (ch == 's') {
                buffer.append(ch);
                state.pop();
                state.push(BOOLEAN_S);
            } else {
                booleanReturn(state, ch, cType, buffer);
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    },
    BOOLEAN_S {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            toBooleanLastLetter(state, ch, cType, buffer, BOOLEAN_FALSE_E);
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    },
    BOOLEAN_FALSE_E {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            switch (cType) {
                case DIGIT:
                case ALPHABET:
                case HYPHEN:
                    buffer.append(ch);
                    state.pop();
                    state.push(IDENTIFIER);
                    return;
                default:
                    state.pendingChar = ch;
                    state.token = new Token(TokenType.BOOLEAN_LITERAL_FALSE, buffer.toString(), state.column, state.row);
                    state.pop();
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    },
    NUMERIC {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            switch (cType) {
                case DIGIT:
                    buffer.append(ch);
                    return;
                case PERIOD:
                    buffer.append(ch);
                    state.push(NUMERIC_PERIOD);
                    return;
                default:
                    String str = buffer.toString();
                    state.pendingChar = ch;
                    if (str.equals("-")) {
                        state.token = new Token(TokenType.HYPHEN, buffer.toString(), state.column, state.row);
                    } else if (str.contains(".")) {
                        state.token = new Token(TokenType.DOUBLE_LITERAL, buffer.toString(), state.column, state.row);
                    } else {
                        state.token = new Token(TokenType.INTEGER_LITERAL, buffer.toString(), state.column, state.row);
                    }
                    state.pop();
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.INTEGER_LITERAL;
        }
    },
    NUMERIC_PERIOD {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            switch (cType) {
                case DIGIT:
                    buffer.append(ch);
                    state.pop();
                    return;
                default:
                    state.pendingChar = ch;
                    state.token = new Token(TokenType.UNKNOWN, buffer.toString(), state.column, state.row);
                    state.pop();
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    },
    SINGLE_STRING_LITERAL {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            switch (cType) {
                case BACK_SLASH:
                    state.push(ESCAPING);
                    return;
                case SINGLE_QUOTE:
                    state.token = new Token(TokenType.STRING_LITERAL, buffer.substring(1), state.column, state.row);
                    state.pop();
                    return;
                default:
                    buffer.append(ch);
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.STRING_LITERAL;
        }
    },
    DOUBLE_STRING_LITERAL {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            switch (cType) {
                case BACK_SLASH:
                    state.push(ESCAPING);
                    return;
                case DOUBLE_QUOTE:
                    state.token = new Token(TokenType.STRING_LITERAL, buffer.substring(1), state.column, state.row);
                    state.pop();
                    return;
                default:
                    buffer.append(ch);
            }
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.STRING_LITERAL;
        }
    },
    ESCAPING {
        @Override
        public void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment) {
            buffer.append(ch);
            state.pop();
        }

        @Override
        public TokenType getTokenType() {
            return TokenType.UNKNOWN;
        }
    };

    public abstract void process(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, TemplateEnvironment environment);

    public abstract TokenType getTokenType();

    protected void booleanReturn(ReaderState state, char ch, CharacterType cType, StringBuilder buffer) {
        switch (cType) {
            case ALPHABET:
            case DIGIT:
            case UNDER_BAR:
                buffer.append(ch);
                state.pop();
                state.push(IDENTIFIER);
                break;
            default:
                state.pendingChar = ch;
                state.token = new Token(TokenType.IDENTIFIER, buffer.toString(), state.column, state.row);
                state.pop();
        }
    }

    protected void toBooleanLastLetter(ReaderState state, char ch, CharacterType cType, StringBuilder buffer, LexerState toGo) {
        if (ch == 'e') {
            buffer.append(ch);
            state.pop();
            state.push(toGo);
        } else {
            booleanReturn(state, ch, cType, buffer);
        }
    }
}
