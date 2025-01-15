package fr.nathan818.azplugin.common.appearance;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
public final class AZEntityModel {

    @lombok.Builder.Default
    private final int modelId = 3072; // Minecraft 1.9.4 entity ID, or 3072 for "self"

    private final @Nullable NotchianNbtTagCompound metadata;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;

    @lombok.Builder.Default
    private final float eyeHeightStand = Float.NaN;

    @lombok.Builder.Default
    private final float eyeHeightSneak = Float.NaN;

    @lombok.Builder.Default
    private final float eyeHeightSleep = Float.NaN;

    @lombok.Builder.Default
    private final float eyeHeightElytra = Float.NaN;

    public static class Builder {

        public Builder offset(float offsetX, float offsetY, float offsetZ) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            return this;
        }

        public Builder eyeHeight(float eyeHeightStand) {
            return eyeHeight(eyeHeightStand, eyeHeightStand, Float.NaN, Float.NaN);
        }

        public Builder eyeHeight(float eyeHeightStand, float eyeHeightSneak) {
            return eyeHeight(eyeHeightStand, eyeHeightSneak, Float.NaN, Float.NaN);
        }

        public Builder eyeHeight(
            float eyeHeightStand,
            float eyeHeightSneak,
            float eyeHeightSleep,
            float eyeHeightElytra
        ) {
            this.eyeHeightStand$value = eyeHeightStand;
            this.eyeHeightStand$set = true;
            this.eyeHeightSneak$value = eyeHeightSneak;
            this.eyeHeightSneak$set = true;
            this.eyeHeightSleep$value = eyeHeightSleep;
            this.eyeHeightSleep$set = true;
            this.eyeHeightElytra$value = eyeHeightElytra;
            this.eyeHeightElytra$set = true;
            return this;
        }
    }
}
