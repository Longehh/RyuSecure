package cc.ryujingames.security.utils;

import cc.ryujingames.security.RyuSecure;
import cc.ryujingames.security.objects.Authentication;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AuthenticationUtils {

    public static boolean isCorrectPassword(UUID uuid, String rawPassword) {
        Authentication authentication = RyuSecure.getInstance().getAuthenticationHashMap().get(uuid);
        return RyuSecure.getInstance().getArgon2().verify(authentication.getHashedPassword(), rawPassword.toCharArray());
    }

    public static boolean isCorrectIP(UUID uuid, String rawIPAddress) {
        Authentication authentication = RyuSecure.getInstance().getAuthenticationHashMap().get(uuid);
        return RyuSecure.getInstance().getArgon2().verify(authentication.getHashedIPAddress(), rawIPAddress.toCharArray());
    }

    public static String createHashedPassword(String rawPassword) {
        String hashedPassword;
        try{
            hashedPassword = RyuSecure.getInstance().getArgon2().hash(10, 65536, 1, rawPassword.toCharArray());
        } finally {
            RyuSecure.getInstance().getArgon2().wipeArray(rawPassword.toCharArray());
        }
        return hashedPassword;
    }

    public static String createHashedIPAddress(String rawIPAddress) {
        String hashedIPAddress;
        try {
            hashedIPAddress = RyuSecure.getInstance().getArgon2().hash(10, 65536, 1, rawIPAddress.toCharArray());
        } finally {
            RyuSecure.getInstance().getArgon2().wipeArray(rawIPAddress.toCharArray());
        }
        return hashedIPAddress;
    }

    public static boolean isStaffMember(Player player) {
        return player.hasPermission(RyuSecure.getInstance().getConfig().getString("securityPermission"));
    }
}
