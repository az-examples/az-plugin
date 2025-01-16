package fr.nathan818.azplugin.bukkit.compat.material;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
@ToString
public class RegisterBlockResult {

    private final @NonNull BlockHandler handler;
    private final @Nullable ItemHandler itemHandler;
}
