package fr.nathan818.azplugin.common;

import lombok.experimental.UtilityClass;
import pactify.client.api.plsp.packet.client.PLSPPacketConfFlag;
import pactify.client.api.plsp.packet.client.PLSPPacketConfInt;

@UtilityClass
public class AZConstants {

    public static final int CHAT_COMPONENT_MAX_LENGTH = 65535;

    public static void assertConfFlagExists(String key) {
        if (!PLSPPacketConfFlag.KNOWN_FLAGS.contains(key)) {
            throw new IllegalArgumentException("Unknown conf flag: " + key);
        }
    }

    public static void assertConfIntExists(String key) {
        if (!PLSPPacketConfInt.KNOWN_PARAMS.contains(key)) {
            throw new IllegalArgumentException("Unknown conf int: " + key);
        }
    }

    public static boolean isConfFlagSupported(String key, int protocolVersion) {
        return protocolVersion >= 0 && PLSPPacketConfFlag.KNOWN_FLAGS.contains(key);
    }

    public static boolean isConfIntSupported(String key, int protocolVersion) {
        return protocolVersion >= 0 && PLSPPacketConfInt.KNOWN_PARAMS.contains(key);
    }

    public static boolean getDefaultConfFlag(String key, int protocolVersion) {
        switch (key) {
            case "attack_cooldown":
            case "hit_indicator":
            case "pistons_retract_entities":
            case "player_push":
            case "sidebar_scores":
                return true;
            default:
                return false;
        }
    }

    public static int getDefaultConfInt(String key, int protocolVersion) {
        switch (key) {
            case "chat_message_max_size":
                return 100;
            case "max_build_height":
                return Integer.MAX_VALUE;
            default:
                return 0;
        }
    }
}
