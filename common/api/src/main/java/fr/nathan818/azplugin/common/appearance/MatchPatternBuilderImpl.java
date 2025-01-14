package fr.nathan818.azplugin.common.appearance;

import fr.nathan818.azplugin.common.appearance.AZCosmeticEquipment.MatchFlag;
import fr.nathan818.azplugin.common.appearance.AZCosmeticEquipment.MatchPattern;
import fr.nathan818.azplugin.common.appearance.AZCosmeticEquipment.MatchPatternBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pactify.client.api.plprotocol.model.cosmetic.PactifyCosmeticEquipment;

final class MatchPatternBuilderImpl implements MatchPatternBuilder {

    public static final Set<Short> ANY_DATA = Collections.emptySet();

    private final Set<MatchFlag> flags = EnumSet.noneOf(MatchFlag.class);
    private final Map<Integer, Set<Short>> items = new HashMap<>();

    @Override
    public MatchPatternBuilder add(MatchFlag flag) {
        flags.add(flag);
        return this;
    }

    @Override
    public MatchPatternBuilder add(MatchFlag... flags) {
        Collections.addAll(this.flags, flags);
        return this;
    }

    @Override
    public MatchPatternBuilder add(Collection<MatchFlag> flags) {
        this.flags.addAll(flags);
        return this;
    }

    @Override
    public MatchPatternBuilder add(int itemId, short data) {
        if (data == (short) -1) {
            items.put(itemId, ANY_DATA);
        } else {
            items.compute(itemId, (key, set) -> {
                if (set == ANY_DATA) {
                    return set;
                }
                if (set == null) {
                    set = new HashSet<>();
                }
                set.add(data);
                return set;
            });
        }
        return this;
    }

    @Override
    public MatchPattern build() {
        if (this.flags.contains(MatchFlag.ANY)) {
            return new MatchPattern(null);
        }

        List<PactifyCosmeticEquipment.ItemPattern> patterns = new ArrayList<>();
        if (!this.flags.isEmpty()) {
            int id0Flags = 0;
            for (MatchFlag flag : this.flags) {
                switch (flag) {
                    case EMPTY:
                        id0Flags |= PactifyCosmeticEquipment.ItemPattern.ID0_EMPTY;
                        break;
                    case NOT_EMPTY:
                        id0Flags |= PactifyCosmeticEquipment.ItemPattern.ID0_NOT_EMPTY;
                        break;
                    case SHOVEL:
                        id0Flags |= PactifyCosmeticEquipment.ItemPattern.ID0_SHOVEL;
                        break;
                    case PICKAXE:
                        id0Flags |= PactifyCosmeticEquipment.ItemPattern.ID0_PICKAXE;
                        break;
                    case AXE:
                        id0Flags |= PactifyCosmeticEquipment.ItemPattern.ID0_AXE;
                        break;
                    case SWORD:
                        id0Flags |= PactifyCosmeticEquipment.ItemPattern.ID0_SWORD;
                        break;
                    case HOE:
                        id0Flags |= PactifyCosmeticEquipment.ItemPattern.ID0_HOE;
                        break;
                    case HELMET:
                        id0Flags |= PactifyCosmeticEquipment.ItemPattern.ID0_HELMET;
                        break;
                    case CHESTPLATE:
                        id0Flags |= PactifyCosmeticEquipment.ItemPattern.ID0_CHESTPLATE;
                        break;
                    case LEGGINGS:
                        id0Flags |= PactifyCosmeticEquipment.ItemPattern.ID0_LEGGINGS;
                        break;
                    case BOOTS:
                        id0Flags |= PactifyCosmeticEquipment.ItemPattern.ID0_BOOTS;
                        break;
                }
            }
            patterns.add(new PactifyCosmeticEquipment.ItemPattern(0, id0Flags));
        }

        for (Map.Entry<Integer, Set<Short>> entry : this.items.entrySet()) {
            int id = entry.getKey();
            Set<Short> data = entry.getValue();
            if (data == ANY_DATA) {
                patterns.add(new PactifyCosmeticEquipment.ItemPattern(id));
            } else {
                for (short d : data) {
                    patterns.add(new PactifyCosmeticEquipment.ItemPattern(id, d));
                }
            }
        }

        return new MatchPattern(Collections.unmodifiableList(patterns));
    }
}
