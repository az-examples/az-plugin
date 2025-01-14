package fr.nathan818.azplugin.common.gui;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
public final class AZVignette {

    private final int red;
    private final int green;
    private final int blue;

    public float getRedFloat() {
        return red / 255.0F;
    }

    public float getGreenFloat() {
        return green / 255.0F;
    }

    public float getBlueFloat() {
        return blue / 255.0F;
    }

    public int getColorRGB() {
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    public static AZVignette buildRGB(int red, int green, int blue) {
        return new AZVignette(red & 0xFF, green & 0xFF, blue & 0xFF);
    }

    public static AZVignette buildRGB(int rgbIntColor) {
        return buildRGB(rgbIntColor >> 16, rgbIntColor >> 8, rgbIntColor);
    }

    public static class Builder {

        public Builder red(int red) {
            this.red = red & 0xFF;
            return this;
        }

        public Builder green(int green) {
            this.green = green & 0xFF;
            return this;
        }

        public Builder blue(int blue) {
            this.blue = blue & 0xFF;
            return this;
        }

        public Builder colorRGB(int red, int green, int blue) {
            red(red);
            green(green);
            blue(blue);
            return this;
        }

        public Builder colorRGB(int rgbIntColor) {
            red(rgbIntColor >> 16);
            green(rgbIntColor >> 8);
            blue(rgbIntColor);
            return this;
        }
    }
}
