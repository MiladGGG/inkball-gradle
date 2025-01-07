package inkball;

import processing.core.PImage;
@SuppressWarnings("unused")
public class Colours {
    public static enum c{
        Grey,
        Orange,
        Blue,
        Green,
        Yellow;


    }


    public static int getSpriteIndex(c Colour){
        int i = 0;
        switch (Colour) {
            case Grey:
                i =0;
                break;
            case Orange:
                i =1;
                break;
            case Blue:
                i =2;
                break;
            case Green:
                i =3;
                break;
            case Yellow:
                i = 4;
                break;
        
            default:
                break;
        }
        return i;
    }

    public static c getColour(String s){
        if(s.equals("grey")){
            return c.Grey;
        }
        if(s.equals("orange")){
            return c.Orange;
        }
        if(s.equals("blue")){
            return c.Blue;
        }
        if(s.equals("green")){
            return c.Green;
        }
        if(s.equals("yellow")){
            return c.Yellow;
        }
        return null;
    }
}
