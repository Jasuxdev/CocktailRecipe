package com.example.distributingdata;

public class CocktailModel implements java.io.Serializable {
    private String mTitle;
    private String mGlass;
    private String mSmallImage;
    private String mDescription;
    private String mIngredients;

    public String getTitle() {
        return mTitle;
    }

    public String getSmallImage() { return mSmallImage; }

    public String getDescription() { return mDescription; }

    public String getGlass() {return mGlass;}

    public String getIngredients() {return mIngredients;}

    public void putTitle(String title) {mTitle = title;}

    public void putSmallImage(String smallImage) { mSmallImage = smallImage; }

    public void putDescription(String description) { mDescription = description; }

    public void putGlass(String glass) { mGlass = glass; }

    public void putIngredients(String ingredients) { mIngredients = ingredients; }

}
