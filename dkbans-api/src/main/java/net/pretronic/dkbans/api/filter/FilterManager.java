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

package net.pretronic.dkbans.api.filter;

import net.pretronic.dkbans.api.filter.operation.FilterOperation;
import net.pretronic.dkbans.api.filter.operation.FilterOperationFactory;

import java.util.Collection;

public interface FilterManager {

    Collection<Filter> getFilters();

    Collection<Filter> getFilters(String area);

    Filter getFilter(int id);

    Filter createFilter(String area, String operation, String value);

    Filter createFilter(String area, FilterOperation operation, String value);

    void deleteFilter(int id);

    void deleteFilter(Filter filter);

    boolean checkFilter(String area,String input);

    Collection<String> getAffiliationAreas();

    default boolean hasAffiliationArea(String area){
        for (String affiliationArea : getAffiliationAreas()) {
            if(affiliationArea.equalsIgnoreCase(area)) return true;
        }
        return false;
    }

    void registerAffiliationArea(String area);

    void unregisterAffiliationArea(String area);


    FilterOperationFactory getOperationFactory(String name);

    default boolean hasOperationFactory(String name){
        return getOperationFactory(name) != null;
    }

    Collection<FilterOperationFactory> getOperationFactories();

    void registerOperationFactory(FilterOperationFactory operation);

    void unregisterOperationFactory(FilterOperationFactory operation);

    void reload();

}
