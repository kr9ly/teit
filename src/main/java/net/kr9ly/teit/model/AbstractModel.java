package net.kr9ly.teit.model;

import net.kr9ly.teit.model.error.ErrorModel;

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
public abstract class AbstractModel implements NodeModel {

    private List<ErrorModel> errors = new ArrayList<>();

    @Override
    public void appendChild(NodeModel child) {
        if (child instanceof ErrorModel) {
            errors.add((ErrorModel) child);
            return;
        }
        throw new IllegalStateException(getClass().getCanonicalName() + " cannot contain " + child.getClass().getCanonicalName());
    }

    @Override
    public boolean hasError() {
        return !errors.isEmpty();
    }
}
