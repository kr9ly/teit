package net.kr9ly.teit.builder;

import net.kr9ly.teit.TemplateEnvironment;
import net.kr9ly.teit.model.LineBreakModel;
import net.kr9ly.teit.model.NodeModel;
import net.kr9ly.teit.model.TemplateModel;
import net.kr9ly.teit.model.TextModel;
import net.kr9ly.teit.model.expression.ExpressionModel;
import net.kr9ly.teit.model.expression.IdentifierModel;
import net.kr9ly.teit.model.tag.IfModel;
import net.kr9ly.teit.model.tag.VariableModel;
import net.kr9ly.teit.reader.TemplateReader;
import net.kr9ly.teit.util.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
public class ModelBuilderTest {

    private TemplateModel parse(String template) {
        TemplateEnvironment env = new TemplateEnvironment();
        ModelBuilder builder = new ModelBuilder();
        return builder.parse(new TemplateReader(new StringReader(template), env), env);
    }

    @Test
    public void testTextModel() {
        TemplateModel model = parse("abcdef");
        TestUtils utils = new TestUtils(model.toString());
        List<NodeModel> children = model.getChildren();
        utils.expect(1, children.size());
        utils.expect(children.get(0), TextModel.class, text -> {
            utils.expect("abcdef", text.getText());
        });
    }

    @Test
    public void testMultiLineTextModel() {
        TemplateModel model = parse("abcdef\nhijklmn");
        TestUtils utils = new TestUtils(model.toString());
        List<NodeModel> children = model.getChildren();
        utils.expect(3, children.size());
        utils.expect(children.get(0), TextModel.class, text -> {
            utils.expect("abcdef", text.getText());
        });
        utils.expect(children.get(1), LineBreakModel.class);
        utils.expect(children.get(2), TextModel.class, text -> {
            utils.expect("hijklmn", text.getText());
        });
    }

    @Test
    public void testExpression() {
        TemplateModel model = parse("abc{(ab+\"c\"*2+('aa'/k))}ef");
        TestUtils utils = new TestUtils(model.toString());
        List<NodeModel> children = model.getChildren();
        utils.expect(3, children.size());
        utils.expect(children.get(0), TextModel.class, text -> {
            utils.expect("abc", text.getText());
        });
        utils.expect(children.get(1), VariableModel.class, variable -> {
            List<NodeModel> list = variable.getVariable().getChildren();
            utils.expect(13, list.size());
        });
        utils.expect(children.get(2), TextModel.class, text -> {
            utils.expect("ef", text.getText());
        });
    }

    @Test
    public void testIfModel() {
        TemplateModel model = parse("{if a}test{/if}abc");
        TestUtils utils = new TestUtils(model.toString());
        List<NodeModel> children = model.getChildren();
        utils.expect(2, children.size());
        utils.expect(children.get(0), IfModel.class, ifModel -> {
            utils.expect(1, ifModel.getCondition().getChildren().size());
            assertEquals(model.toString(), 1, ifModel.getCondition().getChildren().size());
            utils.expect(ifModel.getCondition().getChildren().get(0), IdentifierModel.class, id -> {
                utils.expect("a", id.getIdentifier());
            });
            utils.expect(1, ifModel.getThenNodes().size());
            utils.expect(ifModel.getThenNodes().get(0), TextModel.class, then -> {
                utils.expect("test", then.getText());
            });
        });
        utils.expect(children.get(1), TextModel.class, text -> {
            utils.expect("abc", text.getText());
        });
    }

    @Test
    public void testIfModelThen() {
        TemplateModel model = parse("{if a then c}abc");
        TestUtils utils = new TestUtils(model.toString());
        List<NodeModel> children = model.getChildren();
        utils.expect(2, children.size());
        utils.expect(children.get(0), IfModel.class, ifModel -> {
            utils.expect(1, ifModel.getCondition().getChildren().size());
            assertEquals(model.toString(), 1, ifModel.getCondition().getChildren().size());
            utils.expect(ifModel.getCondition().getChildren().get(0), IdentifierModel.class, id -> {
                utils.expect("a", id.getIdentifier());
            });
            utils.expect(1, ifModel.getThenNodes().size());
            utils.expect(ifModel.getThenNodes().get(0), ExpressionModel.class, expr -> {
                utils.expect(expr.getChildren().get(0), IdentifierModel.class, id -> {
                    utils.expect("c", id.getIdentifier());
                });
            });
        });
        utils.expect(children.get(1), TextModel.class, text -> {
            utils.expect("abc", text.getText());
        });
    }
}
