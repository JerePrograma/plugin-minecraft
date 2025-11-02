package dev.jere.servercore.model;

import java.util.UUID;

public class Profile {
    public final UUID uuid;
    public boolean lobbyHidePlayers = false;
    public boolean allowPM = true;
    public boolean canFly = false;
    public long coins = 0L;
    public String localRank = "DEFAULT"; // fallback si no hay LuckPerms

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }
}
