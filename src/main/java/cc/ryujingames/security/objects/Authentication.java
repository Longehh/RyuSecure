package cc.ryujingames.security.objects;

import java.util.UUID;

public class Authentication {
    private UUID uuid;
    private String hashedPassword;
    private String hashedIPAddress;

    public Authentication(UUID uuid, String hashedPassword, String hashedIPAddress) {
        this.uuid = uuid;
        this.hashedPassword = hashedPassword;
        this.hashedIPAddress = hashedIPAddress;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getHashedIPAddress() {
        return hashedIPAddress;
    }

    public void setHashedIPAddress(String newHashedIPAddress) {
        this.hashedIPAddress = newHashedIPAddress;
    }

    public void setHashedPassword(String newHashedPassword) {
        this.hashedPassword = newHashedPassword;
    }
}
