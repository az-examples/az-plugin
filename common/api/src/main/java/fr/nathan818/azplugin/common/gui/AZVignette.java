package fr.nathan818.azplugin.common.gui;

import fr.nathan818.azplugin.common.AZColors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

// TODO: Javadoc: The vignette is the circular shadow around the player's screen, usually black or darkened.
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
public final class AZVignette {

    private final int colorARGB;

    public int getRed() {
        return AZColors.getRed(colorARGB);
    }

    public int getGreen() {
        return AZColors.getGreen(colorARGB);
    }

    public int getBlue() {
        return AZColors.getBlue(colorARGB);
    }

    public float getRedFloat() {
        return getRed() / 255.0F;
    }

    public float getGreenFloat() {
        return getGreen() / 255.0F;
    }

    public float getBlueFloat() {
        return getBlue() / 255.0F;
    }

    public static AZVignette buildRGB(int colorRGB) {
        return new AZVignette(AZColors.ofRGB(colorRGB));
    }

    public static AZVignette buildRGB(int red, int green, int blue) {
        return new AZVignette(AZColors.ofRGB(red, green, blue));
    }

    public static class Builder {

        public Builder colorARGB(int colorARGB) {
            this.colorARGB = colorARGB | 0xFF000000;
            return this;
        }

        public Builder colorRGB(int red, int green, int blue) {
            colorARGB = AZColors.ofRGB(red, green, blue);
            return this;
        }

        public Builder colorRGB(int colorRGB) {
            this.colorARGB = AZColors.ofRGB(colorRGB);
            return this;
        }
    }
}
