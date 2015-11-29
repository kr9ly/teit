package net.kr9ly.teit.model.tag;

import net.kr9ly.teit.model.NodeModel;
import net.kr9ly.teit.model.expression.ExpressionModel;
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
public class VariableModel extends TagModel {

    private ExpressionModel variable;

    public VariableModel(Token tagNameToken) {
        super(tagNameToken);
    }

    public ExpressionModel getVariable() {
        return variable;
    }

    @Override
    public void appendChild(NodeModel child) {
        if (child instanceof ExpressionModel) {
            variable = (ExpressionModel) child;
            return;
        }
        super.appendChild(child);
    }

    @Override
    public String toString() {
        return "VariableModel{" +
                "variable=" + variable +
                '}';
    }
}
