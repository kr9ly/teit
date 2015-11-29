package net.kr9ly.teit.model.tag;

import net.kr9ly.teit.model.NodeModel;
import net.kr9ly.teit.model.TextModel;
import net.kr9ly.teit.model.expression.ExpressionModel;
import net.kr9ly.teit.token.Token;

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
public class IfModel extends TagModel {

    private ExpressionModel condition;

    private List<NodeModel> thenNodes = new ArrayList<>();

    public IfModel(Token tagNameToken) {
        super(tagNameToken);
    }

    @Override
    public void appendChild(NodeModel child) {
        if (child instanceof ExpressionModel) {
            if (condition == null) {
                condition = (ExpressionModel) child;
            } else {
                thenNodes.add(child);
            }
            return;
        } else if (child instanceof TextModel) {
            thenNodes.add(child);
            return;
        } else if (child instanceof IfModel) {
            thenNodes.add(child);
            return;
        }
        super.appendChild(child);
    }

    public ExpressionModel getCondition() {
        return condition;
    }

    public List<NodeModel> getThenNodes() {
        return thenNodes;
    }

    @Override
    public boolean isMatchCloseTag(String closeTagName) {
        if (closeTagName.equals("else")) {
            return true;
        }
        return super.isMatchCloseTag(closeTagName);
    }

    @Override
    public String toString() {
        return "IfModel{" +
                "condition=" + condition +
                ", thenNodes=" + thenNodes +
                '}';
    }
}
