package fr.nathan818.azplugin.bukkit.compat.material;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class RegisterItemResult {

    private final @NonNull ItemHandler handler;
}
