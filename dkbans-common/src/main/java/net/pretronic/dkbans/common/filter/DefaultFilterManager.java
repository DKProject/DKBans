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

package net.pretronic.dkbans.common.filter;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.event.DKBansFiltersUpdateEvent;
import net.pretronic.dkbans.api.filter.Filter;
import net.pretronic.dkbans.api.filter.FilterAffiliationArea;
import net.pretronic.dkbans.api.filter.FilterManager;
import net.pretronic.dkbans.api.filter.operation.FilterOperation;
import net.pretronic.dkbans.api.filter.operation.FilterOperationFactory;
import net.pretronic.dkbans.common.event.DefaultDKBansFiltersUpdateEvent;
import net.pretronic.dkbans.common.filter.operations.*;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultFilterManager implements FilterManager {

    private final Collection<Filter> filters;
    private final Collection<String> affiliationArea;
    private final Collection<FilterOperationFactory> factories;

    public DefaultFilterManager() {
        this.filters = new ArrayList<>();
        this.factories = new ArrayList<>();
        this.affiliationArea = new ArrayList<>();
    }

    @Override
    public Collection<Filter> getFilters() {
        return filters;
    }

    @Override
    public Collection<Filter> getFilters(String area) {
        Validate.notNull(area);
        return Iterators.filter(this.filters, filter -> filter.getAffiliationArea().equalsIgnoreCase(area));
    }

    @Override
    public Filter getFilter(int id) {
        return Iterators.findOne(this.filters, filter -> filter.getId() == id);
    }

    @Override
    public Filter createFilter(String area, String operation0, String value) {
        Validate.notNull((Object) operation0,area,value);
        FilterOperationFactory factory = getOperationFactory(operation0);
        if(factory == null) throw new NullPointerException("Filter operation not available");
        sendUpdate();
        return createFilter(area, factory.build(value), value);
    }

    @Override
    public Filter createFilter(String area, FilterOperation operation, String value) {
        Validate.notNull(operation,area,value);
        int id = DKBans.getInstance().getStorage().createFilter(area,operation.getName(),value);
        Filter filter = new DefaultFilter(id,area,operation,value);
        this.filters.add(filter);
        sendUpdate();
        return filter;
    }

    @Override
    public void deleteFilter(int id) {
        DKBans.getInstance().getStorage().deleteFilter(id);
        Iterators.removeOne(this.filters, filter -> filter.getId() == id);
        sendUpdate();
    }

    @Override
    public void deleteFilter(Filter filter) {
        Validate.notNull(filter);
        deleteFilter(filter.getId());
    }

    @Override
    public boolean checkFilter(String area, String input) {
        Validate.notNull((Object)area,(Object)input);
        for (Filter filter : filters) {
            if(filter.getAffiliationArea().equalsIgnoreCase(area) && filter.check(input)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<String> getAffiliationAreas() {
        return affiliationArea;
    }

    @Override
    public void registerAffiliationArea(String area) {
        Validate.notNull(area);
        affiliationArea.add(area);
    }

    @Override
    public void unregisterAffiliationArea(String area) {
        Validate.notNull(area);
        affiliationArea.remove(area);
    }

    @Override
    public FilterOperationFactory getOperationFactory(String name) {
        Validate.notNull(name);
        return Iterators.findOne(this.factories, factory -> factory.getName().equalsIgnoreCase(name));
    }

    @Override
    public Collection<FilterOperationFactory> getOperationFactories() {
        return factories;
    }

    @Override
    public void registerOperationFactory(FilterOperationFactory operation) {
        Validate.notNull(operation);
        if(getOperationFactory(operation.getName()) != null){
            throw new IllegalArgumentException("An operation with the name "+operation.getName()+" is already registered");
        }
        factories.add(operation);
    }

    @Override
    public void unregisterOperationFactory(FilterOperationFactory operation) {
        Validate.notNull(operation);
        factories.remove(operation);
    }

    @Override
    public void reload() {
        this.filters.clear();
        this.filters.addAll(DKBans.getInstance().getStorage().loadFilters());
    }

    private void sendUpdate(){
        DKBans.getInstance().getEventBus().callEvent(DKBansFiltersUpdateEvent.class,new DefaultDKBansFiltersUpdateEvent());
    }

    public void initialize(){
        registerOperationFactory(new EqualsOperation.Factory());
        registerOperationFactory(new ContainsOperation.Factory());
        registerOperationFactory(new StartsWithOperation.Factory());
        registerOperationFactory(new EndsWithOperation.Factory());
        registerOperationFactory(new RegexOperation.Factory());

        registerAffiliationArea(FilterAffiliationArea.CHAT_ADVERTISING);
        registerAffiliationArea(FilterAffiliationArea.CHAT_INSULT);
        registerAffiliationArea(FilterAffiliationArea.COMMAND);
        registerAffiliationArea(FilterAffiliationArea.COMMAND_MUTE);
        registerAffiliationArea(FilterAffiliationArea.PLAYER_NAME);

        this.filters.addAll(DKBans.getInstance().getStorage().loadFilters());
    }
}
