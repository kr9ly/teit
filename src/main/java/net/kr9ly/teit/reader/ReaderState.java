package net.kr9ly.teit.reader;

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
public class ReaderState {

    private Stack<LexerState> stack = new Stack<>();

    public Token token;

    public char pendingChar;

    public int column;

    public int row;

    public boolean skipColumnPosition = false;

    public void push(LexerState state) {
        stack.push(state);
    }

    public LexerState current() {
        return stack.peek();
    }

    public LexerState pop() {
        return stack.pop();
    }
}
