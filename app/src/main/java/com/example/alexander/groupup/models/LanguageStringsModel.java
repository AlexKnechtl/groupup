package com.example.alexander.groupup.models;

import java.util.Locale;

public class LanguageStringsModel {
    public String Deutsch;
    public String English;
    public String Image;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalLanguageString() {
        switch (Locale.getDefault().getDisplayLanguage()) {
            case "Deutsch":
                return Deutsch;
            default:
                return English;
        }
    }

    public LanguageStringsModel() {
    }

    public LanguageStringsModel(String Deutsch, String English, String Image) {
        this.Deutsch = Deutsch;
        this.English = English;
        this.Image = Image;
    }
}