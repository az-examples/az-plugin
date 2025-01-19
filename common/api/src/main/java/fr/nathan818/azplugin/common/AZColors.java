package fr.nathan818.azplugin.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AZColors {

    public static int ofRGB(int colorRGB) {
        return 0xFF000000 | colorRGB;
    }

    public static int ofRGB(int red, int green, int blue) {
        return ofRGB(red, green, blue, 255);
    }

    public static int ofRGB(int red, int green, int blue, int alpha) {
        return (alpha << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    public static int getAlpha(int colorARGB) {
        return (colorARGB >> 24) & 0xFF;
    }

    public static int getRed(int colorARGB) {
        return (colorARGB >> 16) & 0xFF;
    }

    public static int getGreen(int colorARGB) {
        return (colorARGB >> 8) & 0xFF;
    }

    public static int getBlue(int colorARGB) {
        return colorARGB & 0xFF;
    }
}
