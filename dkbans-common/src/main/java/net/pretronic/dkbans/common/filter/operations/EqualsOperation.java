/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.06.20, 17:26
 * @web %web%
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.pretronic.dkbans.common.filter.operations;

import net.pretronic.dkbans.api.filter.operation.FilterOperation;
import net.pretronic.dkbans.api.filter.operation.FilterOperationFactory;
import net.pretronic.libraries.utility.Validate;

public class EqualsOperation implements FilterOperation {

    private static final String NAME = "EQUALS";

    private final String value;

    public EqualsOperation(String value) {
        Validate.notNull(value);
        this.value = value;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean matches(String input) {
        return value.equalsIgnoreCase(input);
    }

    public static class Factory implements FilterOperationFactory {

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public FilterOperation build(String value) {
            return new EqualsOperation(value);
        }
    }
}
