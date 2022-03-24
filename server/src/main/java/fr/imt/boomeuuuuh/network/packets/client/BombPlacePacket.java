package fr.imt.boomeuuuuh.network.packets.client;

import fr.imt.boomeuuuuh.lobbies.LobbyState;
import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;
import fr.imt.boomeuuuuh.network.packets.both.DeclinePacket;
import fr.imt.boomeuuuuh.players.Player;

public class BombPlacePacket extends Packet {

    private final Player player;

    public BombPlacePacket(Player player) {
        super(PacketType.BOMB_PLACE);
        this.player = player;
    }


    @Override
    protected byte[] encode() {
        // Shouldn't be handled server side
        return null;
    }

    @Override
    public void handle() {
        if (!player.isInLobby() || player.getLobby().getState() == LobbyState.PLAYING) {
            // decline
            player.serverConnection.send(new DeclinePacket("You are not currently playing"));
        }

        // TODO Check if player can place a bomb and place a bomb
    }
}