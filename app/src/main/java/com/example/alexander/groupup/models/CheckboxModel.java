package com.example.alexander.groupup.Models;

public class CheckboxModel {

    boolean isSelected;
    String checkboxItem;

    public CheckboxModel(boolean isSelected, String checkboxItem) {
        this.isSelected = isSelected;
        this.checkboxItem = checkboxItem;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCheckboxItem() {
        return checkboxItem;
    }

    public void setCheckboxItem(String checkboxItem) {
        this.checkboxItem = checkboxItem;
    }
}