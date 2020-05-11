package net.pretronic.dkbans.api.filter;

public interface Filter {

    int getId();

    String getName();

    void setName(String name);


    FilterAffiliationArea getAffiliationArea();

    void setAffiliationArea(FilterAffiliationArea affiliationArea);


    FilterOperation getFilterOperation();

    void setFilterOperation(FilterOperation operation);


    String getValue();


    void delete();

}
