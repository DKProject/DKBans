package net.pretronic.dkbans.api.filter;

import java.util.Collection;

public interface FilterManager {

    Collection<Filter> getFilters();

    Filter getFilter(int id);

    Filter getName(String name);

    Filter createFilter(String name,FilterAffiliationArea area,FilterOperation operation, String value);

    void deleteFilter(int id);

}
