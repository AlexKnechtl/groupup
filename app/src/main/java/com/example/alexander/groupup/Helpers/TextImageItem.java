package com.example.alexander.groupup.Helpers;

public class TextImageItem
{
    private String mText;
    private String mImage;

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public  TextImageItem(String Text, String Image)
    {
        mText = Text;
        mImage = Image;
    }
}
