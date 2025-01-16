package fr.nathan818.azplugin.bukkit.compat.v1_8_R3;

import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import org.jetbrains.annotations.NotNull;

public class ChunkCodec1_8_R3 {

    public static void writeChunkData(
        @NotNull PacketDataSerializer buf,
        byte[] data,
        int sectionsMask,
        boolean complete,
        boolean prefixLen,
        int[] rewritePalette
    ) {
        if (prefixLen) {
            buf.b(data.length);
        }
        int dataIndex = 0;
        int sectionCount = Integer.bitCount(sectionsMask & 0xFFFF);
        for (int i = 0, len = sectionCount * 4096; i < len; ++i) {
            int blockStateId = ((data[dataIndex] & 0xFF) | (data[dataIndex + 1] << 8));
            if (blockStateId >= 0 && blockStateId < rewritePalette.length) {
                blockStateId = rewritePalette[blockStateId];
            }
            buf.writeByte(blockStateId);
            buf.writeByte(blockStateId >>> 8);
            dataIndex += 2;
        }
        buf.writeBytes(data, dataIndex, data.length - dataIndex);
    }
}
