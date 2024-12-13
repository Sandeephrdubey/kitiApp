package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LevelRoot {

    @SerializedName("level")
    private String level;

    @SerializedName("levels")
    private List<LevelsItem> levels;

    @SerializedName("status")
    private boolean status;

    public String getLevel() {
        return level;
    }

    public List<LevelsItem> getLevels() {
        return levels;
    }

    public boolean isStatus() {
        return status;
    }

    public static class LevelsItem {

        @SerializedName("rupee")
        private int rupee;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("__v")
        private int V;

        @SerializedName("name")
        private String name;

        @SerializedName("_id")
        private String id;

        @SerializedName("type")
        private String type;

        @SerializedName("updatedAt")
        private String updatedAt;

        public int getRupee() {
            return rupee;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public int getV() {
            return V;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}