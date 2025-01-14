package fr.nathan818.azplugin.bukkit.compat.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public final class BlockState {

    private final int id;
    private final int data;
}
