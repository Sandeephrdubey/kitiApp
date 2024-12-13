package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryDetailRoot {

    @SerializedName("area")
    private int area;

    @SerializedName("nativeName")
    private String nativeName;

    @SerializedName("capital")
    private String capital;

    @SerializedName("demonym")
    private String demonym;

    @SerializedName("alpha2Code")
    private String alpha2Code;


    @SerializedName("subregion")
    private String subregion;


    @SerializedName("relevance")
    private String relevance;

    @SerializedName("population")
    private int population;

    @SerializedName("numericCode")
    private String numericCode;

    @SerializedName("alpha3Code")
    private String alpha3Code;


    @SerializedName("name")
    private String name;

    @SerializedName("region")
    private String region;


    @SerializedName("currencies")
    private List<String> currencies;

    public int getArea() {
        return area;
    }

    public String getNativeName() {
        return nativeName;
    }

    public String getCapital() {
        return capital;
    }

    public String getDemonym() {
        return demonym;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }


    public String getRelevance() {
        return relevance;
    }

    public int getPopulation() {
        return population;
    }

    public String getNumericCode() {
        return numericCode;
    }

    public String getAlpha3Code() {
        return alpha3Code;
    }


    public List<String> getCurrencies() {
        return currencies;
    }

}