package com.cs371m.theselfiestudio;

import android.graphics.Bitmap;

/**
 * Created by jeremy on 4/22/15.
 */
public class ImageFromFacebook {

    /* Instance Variables */
    public Bitmap img;
    public int numLikes;
    public String name;

    ImageFromFacebook(Bitmap i, int x, String n)
    {
        img = i;
        numLikes = x;
        name = n;
    }

    ImageFromFacebook(Bitmap i, int x)
    {
        img = i;
        numLikes = x;
        name = "no_name";
    }

    public void setImg(Bitmap i)
    {
        this.img = i;
    }

    public Bitmap getImg()
    {
        return this.img;
    }

    public int getNumLikes()
    {
        return this.numLikes;
    }

}
