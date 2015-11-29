package net.kr9ly.teit.builder;

import net.kr9ly.teit.TemplateEnvironment;
import net.kr9ly.teit.model.TemplateModel;
import net.kr9ly.teit.reader.TemplateReader;
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
public class ModelBuilder {

    public TemplateModel parse(TemplateReader reader, TemplateEnvironment env) {
        BuilderState state = new BuilderState();
        state.pushParserState(ParserState.DEFAULT);
        state.pushNode(new TemplateModel());
        Token token;
        while ((token = reader.nextToken()).getTokenType() != TokenType.EOF) {
            state.currentParserState().process(token, state, env);
            while (state.getPendingToken() != null) {
                state.currentParserState().process(state.pullPendingToken(), state, env);
            }
        }
        return (TemplateModel) state.rootNode();
    }
}
