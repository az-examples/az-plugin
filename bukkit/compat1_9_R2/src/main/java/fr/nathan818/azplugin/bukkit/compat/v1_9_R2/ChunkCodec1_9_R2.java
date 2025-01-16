package fr.nathan818.azplugin.bukkit.compat.v1_9_R2;

import net.minecraft.server.v1_9_R2.PacketDataSerializer;

public class ChunkCodec1_9_R2 {

    private static final int MAX_DATA_LEN = 2097152;
    private static final byte[] DATA_LEN_PLACEHOLDER = new byte[getVarIntBytes(MAX_DATA_LEN)];
    private static final int VARINT_FAILED = -1;

    public static void writeChunkData(
        PacketDataSerializer buf,
        PacketDataSerializer data,
        int sectionsMask,
        boolean complete,
        boolean hasSkylight,
        int[] rewritePalette
    ) {
        int dataLenIndex = buf.writerIndex();
        buf.writeBytes(DATA_LEN_PLACEHOLDER);
        for (int i = 0; i < 16; i++) {
            if ((sectionsMask & (1 << i)) == 0) {
                continue;
            }
            writeBlocks(buf, data, rewritePalette);
            buf.writeBytes(data, hasSkylight ? 4096 : 2048); // skyLight+blockLight
        }
        if (complete) {
            buf.writeBytes(data, 256); // biomes
        }
        int dateLen = buf.writerIndex() - dataLenIndex - DATA_LEN_PLACEHOLDER.length;
        setFixedVarInt(buf, dataLenIndex, DATA_LEN_PLACEHOLDER.length, dateLen);
    }

    private static void writeBlocks(PacketDataSerializer buf, PacketDataSerializer data, int[] rewritePalette) {
        int bits = data.readByte();
        if (bits <= 8) {
            writeBlocksArrayPalette(buf, data, bits, rewritePalette);
        } else {
            writeBlocksGlobalPalette(buf, data, rewritePalette);
        }
    }

    private static void writeBlocksArrayPalette(
        PacketDataSerializer buf,
        PacketDataSerializer data,
        int bits,
        int[] rewritePalette
    ) {
        bits = Math.max(4, bits);
        buf.writeByte(bits);

        int paletteLen = Math.min(data.g(), 1 << bits);
        buf.d(paletteLen);
        for (int i = 0; i < paletteLen; i++) {
            int blockStateId = data.g();
            if (blockStateId >= 0 && blockStateId < rewritePalette.length) {
                blockStateId = rewritePalette[blockStateId];
            }
            buf.d(blockStateId);
        }

        int expectedBlocksLen = computeCompactArrayLength(bits);
        int blocksLen = data.g();
        if (blocksLen > expectedBlocksLen) {
            throw new RuntimeException("Invalid blocks length: " + blocksLen + " > " + expectedBlocksLen);
        }
        buf.d(blocksLen);
        buf.writeBytes(data, blocksLen * 8);
    }

    private static void writeBlocksGlobalPalette(
        PacketDataSerializer buf,
        PacketDataSerializer data,
        int[] rewritePalette
    ) {
        int bits = 13;
        buf.writeByte(bits);
        data.g(); // Ignored
        buf.writeByte(0);

        int expectedBlocksLen = computeCompactArrayLength(bits);
        int blocksLen = data.g();
        if (blocksLen > expectedBlocksLen) {
            throw new RuntimeException("Invalid blocks length: " + blocksLen + " > " + expectedBlocksLen);
        }
        buf.d(blocksLen);
        long[] blocks = new long[blocksLen];
        for (int i = 0; i < blocksLen; i++) {
            blocks[i] = data.readLong();
        }

        long maxEntryValue = (1L << bits) - 1;
        for (int i = 0; i < 4096; i++) {
            int bitIndex = i * bits;
            int startIndex = bitIndex / 64;
            int endIndex = ((i + 1) * bits - 1) / 64;
            int bitOffset = bitIndex % 64;
            int blockStateId;
            if (startIndex == endIndex) {
                blockStateId = (int) ((blocks[startIndex] >>> bitOffset) & maxEntryValue);
            } else {
                int endBitOffset = 64 - bitOffset;
                blockStateId = (int) (((blocks[startIndex] >>> bitOffset) | (blocks[endIndex] << endBitOffset)) &
                    maxEntryValue);
            }

            int mappedBlockStateId = blockStateId;
            if (blockStateId >= 0 && blockStateId < rewritePalette.length) {
                mappedBlockStateId = rewritePalette[blockStateId];
            }
            if (mappedBlockStateId != blockStateId) {
                blocks[startIndex] =
                    (blocks[startIndex] & ~(maxEntryValue << bitOffset)) |
                    (((long) mappedBlockStateId & maxEntryValue) << bitOffset);
                if (startIndex != endIndex) {
                    int endBitOffset = 64 - bitOffset;
                    int remainingBits = bits - endBitOffset;
                    blocks[endIndex] =
                        ((blocks[endIndex] >>> remainingBits) << remainingBits) |
                        (((long) mappedBlockStateId & maxEntryValue) >> endBitOffset);
                }
            }
        }

        for (long l : blocks) {
            buf.writeLong(l);
        }
    }

    public static boolean hasSkylight(PacketDataSerializer data, boolean complete, int sectionsMask) {
        // boolean hasSky = tryRead(data, complete, sectionsMask, true);
        // boolean hasNoSky = tryRead(data, complete, sectionsMask, false);
        return !tryRead(data, complete, sectionsMask, false);
    }

    public static boolean tryRead(PacketDataSerializer data, boolean complete, int sectionsMask, boolean hasSky) {
        data.markReaderIndex();
        try {
            for (int i = 0; i < 16; i++) {
                if ((sectionsMask & (1 << i)) == 0) {
                    continue;
                }
                if (!trySkipBlocks(data)) { // blocks
                    return false;
                }
                if (!trySkipBytes(data, hasSky ? 4096 : 2048)) { // skyLight+blockLight
                    return false;
                }
            }
            if (complete && !trySkipBytes(data, 256)) { // biomes
                return false;
            }
            return data.readableBytes() == 0;
        } finally {
            data.resetReaderIndex();
        }
    }

    private static boolean trySkipBlocks(PacketDataSerializer data) {
        if (!data.isReadable()) {
            return false;
        }
        int bits = data.readByte();
        if (bits <= 8) {
            // Array Palette
            bits = Math.max(4, bits);
            int paletteLen = Math.min(tryReadVarInt(data), 1 << bits);
            if (paletteLen == VARINT_FAILED) {
                return false;
            }
            for (int i = 0; i < paletteLen; i++) {
                if (tryReadVarInt(data) == VARINT_FAILED) {
                    return false;
                }
            }
        } else {
            // Global Palette
            bits = 13;
            if (tryReadVarInt(data) == VARINT_FAILED) {
                return false;
            }
        }

        // Blocks
        int expectedBlocksLen = computeCompactArrayLength(bits);
        int blocksLen = tryReadVarInt(data);
        if (blocksLen == VARINT_FAILED || blocksLen != expectedBlocksLen || !trySkipBytes(data, blocksLen * 8)) {
            return false;
        }
        return true;
    }

    private static int tryReadVarInt(PacketDataSerializer data) {
        int out = 0;
        int bytes = 0;
        byte in;
        do {
            if (!data.isReadable()) {
                return VARINT_FAILED;
            }
            in = data.readByte();
            out |= (in & 0x7F) << (bytes++ * 7);
            if (bytes > 5) {
                return VARINT_FAILED;
            }
        } while ((in & 0x80) == 0x80);
        return out;
    }

    public static int getVarIntBytes(int i) {
        for (int j = 1; j < 5; ++j) {
            if ((i & (-1 << (j * 7))) == 0) {
                return j;
            }
        }
        return 5;
    }

    private static void setFixedVarInt(PacketDataSerializer buf, int index, int varIntBytes, int value) {
        int i = value;
        int bytes = 0;
        while (++bytes < varIntBytes || (i & 0xFFFFFF80) != 0) {
            buf.setByte(index++, (i & 0x7F) | 0x80);
            i >>>= 7;
        }
        if (bytes > varIntBytes) {
            throw new IllegalArgumentException("Value to big for " + varIntBytes + " bytes VarInt: " + value);
        }
        buf.setByte(index, i);
    }

    private static boolean trySkipBytes(PacketDataSerializer data, int len) {
        if (data.readableBytes() < len) {
            return false;
        }
        data.skipBytes(len);
        return true;
    }

    private static int computeCompactArrayLength(int bits) {
        return roundUp(4096 * bits, 64) / 64;
    }

    private static int roundUp(int number, int interval) {
        if (interval == 0) {
            return 0;
        }
        if (number == 0) {
            return interval;
        }
        if (number < 0) {
            interval *= -1;
        }
        int remainder = number % interval;
        return (remainder == 0) ? number : (number + interval - remainder);
    }
}
