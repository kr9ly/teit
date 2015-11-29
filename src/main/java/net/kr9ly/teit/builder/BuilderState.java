package net.kr9ly.teit.builder;

import net.kr9ly.teit.model.NodeModel;
import net.kr9ly.teit.token.Token;

import java.util.Stack;

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
public class BuilderState {

    private Stack<ParserState> parserStateStack = new Stack<>();

    private Stack<NodeModel> nodeModelStack = new Stack<>();

    private Token pendingToken;

    public void pushParserState(ParserState state) {
        parserStateStack.push(state);
    }

    public ParserState currentParserState() {
        return parserStateStack.peek();
    }

    public ParserState popParserState() {
        return parserStateStack.pop();
    }

    public void pushNode(NodeModel state) {
        if (!nodeModelStack.isEmpty()) {
            currentNode().appendChild(state);
        }
        nodeModelStack.push(state);
    }

    public NodeModel currentNode() {
        return nodeModelStack.peek();
    }

    public NodeModel popNode() {
        return nodeModelStack.pop();
    }

    public NodeModel rootNode() {
        return nodeModelStack.get(0);
    }

    public Token getPendingToken() {
        return pendingToken;
    }

    public Token pullPendingToken() {
        try {
            return pendingToken;
        } finally {
            pendingToken = null;
        }
    }

    public void setPendingToken(Token pendingToken) {
        this.pendingToken = pendingToken;
    }
}
