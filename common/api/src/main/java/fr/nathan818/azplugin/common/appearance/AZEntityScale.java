package fr.nathan818.azplugin.common.appearance;

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
public final class AZEntityScale {

    @lombok.Builder.Default
    private final float bboxWidth = 1.0F;

    @lombok.Builder.Default
    private final float bboxHeight = 1.0F;

    @lombok.Builder.Default
    private final float renderWidth = 1.0F;

    @lombok.Builder.Default
    private final float renderDepth = 1.0F;

    @lombok.Builder.Default
    private final float renderHeight = 1.0F;

    @lombok.Builder.Default
    private final float itemInHandWidth = 1.0F;

    @lombok.Builder.Default
    private final float itemInHandDepth = 1.0F;

    @lombok.Builder.Default
    private final float itemInHandHeight = 1.0F;

    @lombok.Builder.Default
    private final float nameTags = 1.0F;

    public boolean isOne() {
        return (
            bboxWidth == 1.0F &&
            bboxHeight == 1.0F &&
            renderWidth == 1.0F &&
            renderDepth == 1.0F &&
            renderHeight == 1.0F &&
            itemInHandWidth == 1.0F &&
            itemInHandDepth == 1.0F &&
            itemInHandHeight == 1.0F &&
            nameTags == 1.0F
        );
    }

    public static class Builder {

        public Builder bbox(float bbox) {
            bboxWidth(bbox);
            bboxHeight(bbox);
            return this;
        }

        public Builder render(float render) {
            renderWidth(render);
            renderDepth(render);
            renderHeight(render);
            return this;
        }

        public Builder itemInHand(float itemInHand) {
            itemInHandWidth(itemInHand);
            itemInHandDepth(itemInHand);
            itemInHandHeight(itemInHand);
            return this;
        }

        public AZEntityScale build() {
            float bboxWidth = this.bboxWidth$set ? this.bboxWidth$value : 1.0F;
            float bboxHeight = this.bboxHeight$set ? this.bboxHeight$value : 1.0F;
            float renderWidth = this.renderWidth$set ? this.renderWidth$value : bboxWidth;
            float renderDepth = this.renderDepth$set ? this.renderDepth$value : bboxWidth;
            float renderHeight = this.renderHeight$set ? this.renderHeight$value : bboxHeight;
            float itemInHandWidth = this.itemInHandWidth$set ? this.itemInHandWidth$value : renderWidth;
            float itemInHandDepth = this.itemInHandDepth$set ? this.itemInHandDepth$value : renderDepth;
            float itemInHandHeight = this.itemInHandHeight$set ? this.itemInHandHeight$value : renderHeight;
            float nameTags = this.nameTags$set ? this.nameTags$value : 1.0F;
            return new AZEntityScale(
                bboxWidth,
                bboxHeight,
                renderWidth,
                renderDepth,
                renderHeight,
                itemInHandWidth,
                itemInHandDepth,
                itemInHandHeight,
                nameTags
            );
        }
    }
}
