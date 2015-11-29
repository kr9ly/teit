package net.kr9ly.teit.builder;

import net.kr9ly.teit.TemplateEnvironment;
import net.kr9ly.teit.model.*;
import net.kr9ly.teit.model.error.SyntaxErrorModel;
import net.kr9ly.teit.model.error.UnexpectedCloseTagErrorModel;
import net.kr9ly.teit.model.error.UnmatchedCloseTagErrorModel;
import net.kr9ly.teit.model.expression.*;
import net.kr9ly.teit.model.tag.VariableModel;
import net.kr9ly.teit.model.tag.*;
import net.kr9ly.teit.model.tag.include.IncludePathModel;
import net.kr9ly.teit.token.Token;

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
public enum ParserState {
    DEFAULT {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case TEXT:
                    if (!token.getTokenString().isEmpty()) {
                        state.currentNode().appendChild(new TextModel(token));
                    }
                    return;
                case LINE_BREAK:
                    state.currentNode().appendChild(new LineBreakModel());
                    return;
                case IDENTIFIER:
                    switch (token.getTokenString()) {
                        case "include":
                            state.pushParserState(INCLUDE_TAG);
                            state.pushNode(new IncludeModel(token));
                            return;
                        case "if":
                            state.pushParserState(IF_TAG);
                            state.pushNode(new IfModel(token));
                            return;
                        case "with":
                            state.pushParserState(WITH_TAG);
                            state.pushNode(new WithModel(token));
                            return;
                        case "for":
                            state.pushParserState(FOR_TAG);
                            state.pushNode(new ForModel(token));
                            return;
                        default:
                            state.pushParserState(VARIABLE_TAG);
                            state.pushNode(new VariableModel(token));
                            state.setPendingToken(token);
                            return;
                    }
                case SLASH:
                    state.pushParserState(CLOSE_TAG_START);
                    return;
                case STRING_LITERAL:
                case INTEGER_LITERAL:
                case DOUBLE_LITERAL:
                case PARENTHESIS_OPEN:
                case PLUS:
                case HYPHEN:
                    state.pushParserState(VARIABLE_TAG);
                    state.pushNode(new VariableModel(token));
                    state.setPendingToken(token);
                    return;
                default:
                    state.currentNode().appendChild(new SyntaxErrorModel(token));
            }
        }
    },
    IF_TAG {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case TEXT:
                case LINE_BREAK:
                    startSyntaxErrorMode(token, state);
                    return;
                case IDENTIFIER:
                case STRING_LITERAL:
                case INTEGER_LITERAL:
                case DOUBLE_LITERAL:
                case PARENTHESIS_OPEN:
                case PLUS:
                case HYPHEN:
                    state.popParserState();
                    state.pushParserState(IF_TAG_THEN);
                    startExpressionMode(token, state);
                    return;
                default:
                    startSyntaxErrorMode(token, state);
            }
        }
    },
    IF_TAG_THEN {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case TEXT:
                case LINE_BREAK:
                    state.popParserState();
                    state.setPendingToken(token);
                    return;
                case IDENTIFIER:
                    if (token.getTokenString().equals("then")) {
                        state.popParserState();
                        state.pushParserState(IF_TAG_ELSE_INLINE);
                        state.pushNode(new ExpressionModel());
                        state.pushParserState(EXPRESSION_OPEN);
                        return;
                    }
                    startSyntaxErrorMode(token, state);
                    return;
                default:
                    startSyntaxErrorMode(token, state);
            }
        }
    },
    IF_TAG_ELSE_INLINE {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case TEXT:
                case LINE_BREAK:
                    state.popParserState();
                    state.popNode();
                    state.setPendingToken(token);
                    return;
                case IDENTIFIER:
                    if (token.getTokenString().equals("else")) {
                        state.popParserState();
                        state.pushParserState(IF_TAG_INLINE_CLOSE);
                        state.popNode();
                        state.pushNode(new ElseModel(token));
                        state.pushNode(new ExpressionModel());
                        state.pushParserState(EXPRESSION_OPEN);
                        return;
                    }
                    startSyntaxErrorMode(token, state);
                    return;
                default:
                    startSyntaxErrorMode(token, state);
            }
        }
    },
    IF_TAG_INLINE_CLOSE {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case TEXT:
                case LINE_BREAK:
                    state.popParserState();
                    state.popNode();
                    state.setPendingToken(token);
                    return;
                default:
                    startSyntaxErrorMode(token, state);
            }
        }
    },
    EXPRESSION_OPEN {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case PARENTHESIS_OPEN:
                    state.currentNode().appendChild(new ParenthesisModel(token));
                    return;
                case STRING_LITERAL:
                    state.currentNode().appendChild(new StringLiteralModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                case INTEGER_LITERAL:
                    state.currentNode().appendChild(new IntegerLiteralModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                case DOUBLE_LITERAL:
                    state.currentNode().appendChild(new DoubleLiteralModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                case IDENTIFIER:
                    state.currentNode().appendChild(new IdentifierModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                default:
                    state.popParserState();
                    state.setPendingToken(token);
            }
        }
    },
    EXPRESSION_NEXT {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case PARENTHESIS_CLOSE:
                    state.currentNode().appendChild(new ParenthesisModel(token));
                    return;
                case PARENTHESIS_OPEN:
                    state.currentNode().appendChild(new ParenthesisModel(token));
                    return;
                case STRING_LITERAL:
                    state.currentNode().appendChild(new StringLiteralModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                case INTEGER_LITERAL:
                    state.currentNode().appendChild(new IntegerLiteralModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                case DOUBLE_LITERAL:
                    state.currentNode().appendChild(new DoubleLiteralModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                case IDENTIFIER:
                    state.currentNode().appendChild(new IdentifierModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                default:
                    state.popNode();
                    state.popParserState();
                    state.setPendingToken(token);
            }
        }
    },
    EXPRESSION_VALUE {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case PARENTHESIS_CLOSE:
                    state.currentNode().appendChild(new ParenthesisModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_NEXT);
                    return;
                case PLUS:
                case HYPHEN:
                case SLASH:
                case MOD:
                case MULTIPLIER:
                    state.currentNode().appendChild(new OperatorModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_OPERATOR);
                    return;
                default:
                    state.popParserState();
                    state.popNode();
                    state.setPendingToken(token);
            }
        }
    },
    EXPRESSION_OPERATOR {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case PARENTHESIS_OPEN:
                    state.currentNode().appendChild(new ParenthesisModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_OPEN);
                    return;
                case STRING_LITERAL:
                    state.currentNode().appendChild(new StringLiteralModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                case INTEGER_LITERAL:
                    state.currentNode().appendChild(new IntegerLiteralModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                case DOUBLE_LITERAL:
                    state.currentNode().appendChild(new DoubleLiteralModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                case IDENTIFIER:
                    state.currentNode().appendChild(new IdentifierModel(token));
                    state.popParserState();
                    state.pushParserState(EXPRESSION_VALUE);
                    return;
                default:
                    state.popParserState();
                    state.popNode();
                    state.setPendingToken(token);
            }
        }
    },
    WITH_TAG {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {

        }
    },
    FOR_TAG {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {

        }
    },
    INCLUDE_TAG {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case IDENTIFIER:
                case STRING_LITERAL:
                    state.currentNode().appendChild(new IncludePathModel(token));
                    state.pushParserState(ARGUMENTS);
                    state.pushNode(new ArgumentsModel());
                    return;
                default:
                    startSyntaxErrorMode(token, state);
            }
        }
    },
    VARIABLE_TAG {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case IDENTIFIER:
                case STRING_LITERAL:
                case INTEGER_LITERAL:
                case DOUBLE_LITERAL:
                case PARENTHESIS_OPEN:
                case PLUS:
                case HYPHEN:
                    state.popParserState();
                    state.pushParserState(VARIABLE_CLOSE);
                    startExpressionMode(token, state);
                    return;
                default:
                    startSyntaxErrorMode(token, state);
            }
        }
    },
    VARIABLE_CLOSE {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            state.setPendingToken(token);
            state.popNode();
            state.popParserState();
        }
    },
    CLOSE_TAG_START {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case IDENTIFIER:
                    if (!(state.currentNode() instanceof TagModel)) {
                        state.currentNode().appendChild(new UnexpectedCloseTagErrorModel(token));
                        return;
                    }

                    TagModel tag = (TagModel) state.currentNode();
                    if (!tag.isMatchCloseTag(token.getTokenString())) {
                        state.currentNode().appendChild(new UnmatchedCloseTagErrorModel(token));
                        return;
                    }

                    state.popNode();
                    state.popParserState();
                    return;
                default:
                    startSyntaxErrorMode(token, state);
            }
        }
    },
    SYNTAX_ERROR_IN_TAG {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case TEXT:
                case LINE_BREAK:
                    state.setPendingToken(token);
                    state.popParserState();
                    return;
                default:
                    state.currentNode().appendChild(new SyntaxErrorModel(token));
            }
        }
    },
    ARGUMENTS {
        @Override
        public void process(Token token, BuilderState state, TemplateEnvironment environment) {
            switch (token.getTokenType()) {
                case TEXT:
                case LINE_BREAK:
                    breakTag(token, state);
                    return;
                case IDENTIFIER:
                    return;
                default:
                    state.currentNode().appendChild(new SyntaxErrorModel(token));
            }
        }
    };

    public abstract void process(Token token, BuilderState state, TemplateEnvironment environment);

    protected void breakTag(Token token, BuilderState state) {
        state.setPendingToken(token);
        state.popParserState();
    }

    protected void startSyntaxErrorMode(Token token, BuilderState state) {
        state.setPendingToken(token);
        state.popParserState();
        state.pushParserState(SYNTAX_ERROR_IN_TAG);
    }

    protected void startExpressionMode(Token token, BuilderState state) {
        state.setPendingToken(token);
        state.pushNode(new ExpressionModel());
        state.pushParserState(EXPRESSION_OPEN);
    }
}
