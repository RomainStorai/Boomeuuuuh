package fr.imt.boomeuuuuh.network.packets.client;

import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;
import fr.imt.boomeuuuuh.network.packets.both.DeclinePacket;
import fr.imt.boomeuuuuh.network.packets.server.PlayerDataPacket;
import fr.imt.boomeuuuuh.players.Player;
import fr.imt.boomeuuuuh.players.PlayerData;

public class UnlockSkinPacket extends Packet {

    private final Player player;
    private final String name;

    public UnlockSkinPacket(String name, Player player) {
        super(PacketType.UNLOCK_SKIN);

        this.name = name;
        this.player = player;
    }

    @Override
    protected byte[] encode() {
        // Shouldn't be used server side
        return null;
    }

    @Override
    public void handle() {
        PlayerData playerData = player.getPlayerData();

        if (playerData.hasSkin(name)) {
            player.serverConnection.send(new DeclinePacket("You already have this skin!"));
            return;
        }

        if (playerData.getGold() < 100) {
            player.serverConnection.send(new DeclinePacket("You do not have enough gold!"));
            return;
        }

        playerData.addGold(-100);
        playerData.unlockSkin(name);

        player.serverConnection.send(new PlayerDataPacket(player));
    }
}
