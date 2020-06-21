/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 14.06.20, 09:27
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

package net.pretronic.dkbans.common.filter;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.filter.Filter;
import net.pretronic.dkbans.api.filter.operation.FilterOperation;
import net.pretronic.dkbans.api.filter.operation.FilterOperationFactory;

public class DefaultFilter implements Filter {

    private final int id;
    private final String affiliationArea;
    private final String operationName;
    private final FilterOperation operation;
    private final String value;

    public DefaultFilter(int id,String affiliationArea, String operationName, String value) {
        this.id = id;
        this.affiliationArea = affiliationArea;
        this.operationName = operationName;
        this.value = value;

        FilterOperationFactory factory = DKBans.getInstance().getFilterManager().getOperationFactory(operationName);
        if(factory != null) operation = factory.build(getValue());
        else operation = null;
    }

    public DefaultFilter(int id, String affiliationArea, FilterOperation operation, String value) {
        this.id = id;
        this.affiliationArea = affiliationArea;
        this.operation = operation;
        this.operationName = operation.getName();
        this.value = value;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getAffiliationArea() {
        return affiliationArea;
    }

    @Override
    public String getOperationName() {
        return operationName;
    }

    @Override
    public FilterOperation getOperation() {
        return operation;
    }


    @Override
    public String getValue() {
        return value;
    }


    @Override
    public boolean check(String input) {
        return operation != null && operation.matches(input);
    }
}
