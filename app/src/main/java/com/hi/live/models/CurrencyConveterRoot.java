package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

public class CurrencyConveterRoot {

    @SerializedName("date")
    private String date;

    @SerializedName("result")
    private double result;

    @SerializedName("motd")
    private Motd motd;

    @SerializedName("success")
    private boolean success;

    @SerializedName("query")
    private Query query;

    @SerializedName("historical")
    private boolean historical;

    @SerializedName("info")
    private Info info;

    public String getDate() {
        return date;
    }

    public double getResult() {
        return result;
    }

    public Motd getMotd() {
        return motd;
    }

    public boolean isSuccess() {
        return success;
    }

    public Query getQuery() {
        return query;
    }

    public boolean isHistorical() {
        return historical;
    }

    public Info getInfo() {
        return info;
    }

    public static class Query {

        @SerializedName("amount")
        private int amount;

        @SerializedName("from")
        private String from;

        @SerializedName("to")
        private String to;

        public int getAmount() {
            return amount;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }

    public static class Motd {

        @SerializedName("msg")
        private String msg;

        @SerializedName("url")
        private String url;

        public String getMsg() {
            return msg;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class Info {

        @SerializedName("rate")
        private double rate;

        public double getRate() {
            return rate;
        }
    }
}