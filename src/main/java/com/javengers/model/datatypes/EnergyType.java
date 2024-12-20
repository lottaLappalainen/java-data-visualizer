package com.javengers.model.datatypes;

public enum EnergyType {
    
    // these are the corresponding ids of the datasets in Fingrid's api
    WATER("191"), WIND("181"), SOLAR("248");
    
    private String value;

    EnergyType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
