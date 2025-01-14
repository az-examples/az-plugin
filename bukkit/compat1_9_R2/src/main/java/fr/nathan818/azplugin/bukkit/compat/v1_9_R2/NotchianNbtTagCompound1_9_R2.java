package fr.nathan818.azplugin.bukkit.compat.v1_9_R2;

import static fr.nathan818.azplugin.common.network.AZPacketBuffer.asDataOutput;

import java.io.IOException;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.server.v1_9_R2.NBTCompressedStreamTools;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import pactify.client.api.mcprotocol.NotchianPacketBuffer;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

@RequiredArgsConstructor
public class NotchianNbtTagCompound1_9_R2 implements NotchianNbtTagCompound {

    private final @NonNull NBTTagCompound tag;

    @Override
    @SneakyThrows(IOException.class)
    public void write(NotchianPacketBuffer buf) {
        NBTCompressedStreamTools.a(tag, asDataOutput(buf));
    }

    @Override
    public NotchianNbtTagCompound1_9_R2 shallowClone() {
        return new NotchianNbtTagCompound1_9_R2(tag);
    }

    @Override
    public NotchianNbtTagCompound1_9_R2 deepClone() {
        return new NotchianNbtTagCompound1_9_R2((NBTTagCompound) tag.clone());
    }

    @Override
    public String toString() {
        return Objects.toString(tag);
    }
}
