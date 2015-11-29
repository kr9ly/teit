package net.kr9ly.teit.model.expression;

import net.kr9ly.teit.model.AbstractModel;
import net.kr9ly.teit.model.NodeModel;

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
public class ExpressionModel extends AbstractModel {

    private List<NodeModel> children = new ArrayList<>();

    @Override
    public void appendChild(NodeModel child) {
        if (child instanceof StringLiteralModel) {
            children.add(child);
            return;
        } else if (child instanceof IntegerLiteralModel) {
            children.add(child);
            return;
        } else if (child instanceof DoubleLiteralModel) {
            children.add(child);
            return;
        } else if (child instanceof IdentifierModel) {
            children.add(child);
            return;
        } else if (child instanceof OperatorModel) {
            children.add(child);
            return;
        } else if (child instanceof ParenthesisModel) {
            children.add(child);
            return;
        }
        super.appendChild(child);
    }

    public List<NodeModel> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "ExpressionModel{" +
                "children=" + children +
                '}';
    }
}
