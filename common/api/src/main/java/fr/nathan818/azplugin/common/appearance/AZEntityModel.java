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

    private final int modelId; // Minecraft 1.9.4 entity ID
    private final @Nullable NotchianNbtTagCompound metadata;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final float eyeHeightStand;
    private final float eyeHeightSneak;
    private final float eyeHeightSleep;
    private final float eyeHeightElytra;

    public static class Builder {

        public Builder offset(float offsetX, float offsetY, float offsetZ) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            return this;
        }

        public Builder eyeHeight(float eyeHeightStand) {
            return eyeHeight(eyeHeightStand, eyeHeightStand, eyeHeightStand, eyeHeightStand);
        }

        public Builder eyeHeight(float eyeHeightStand, float eyeHeightSneak) {
            return eyeHeight(eyeHeightStand, eyeHeightSneak, eyeHeightStand, eyeHeightStand);
        }

        public Builder eyeHeight(
            float eyeHeightStand,
            float eyeHeightSneak,
            float eyeHeightSleep,
            float eyeHeightElytra
        ) {
            this.eyeHeightStand = eyeHeightStand;
            this.eyeHeightSneak = eyeHeightSneak;
            this.eyeHeightSleep = eyeHeightSleep;
            this.eyeHeightElytra = eyeHeightElytra;
            return this;
        }
    }
}
