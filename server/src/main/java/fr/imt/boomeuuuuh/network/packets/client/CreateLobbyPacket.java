package fr.imt.boomeuuuuh.network.packets.client;

import fr.imt.boomeuuuuh.lobbies.Lobby;
import fr.imt.boomeuuuuh.lobbies.LobbyManager;
import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;
import fr.imt.boomeuuuuh.network.packets.both.DeclinePacket;
import fr.imt.boomeuuuuh.players.Player;

public class CreateLobbyPacket extends Packet {

    private final Player player;
    private final String name;

    public CreateLobbyPacket(Player player, String name) {
        super(PacketType.CREATE_LOBBY);

        this.player = player;
        this.name = name;
    }

    @Override
    protected byte[] encode() {
        // Shouldn't be encoded server side
        return null;
    }

    @Override
    public void handle() {
        if (!player.isAuthentified()) {
            DeclinePacket declinePacket = new DeclinePacket("You're not authenticated.");
            player.serverConnection.send(declinePacket);
            return;
        }

        Lobby lobby = LobbyManager.startLobby(player);
        if (lobby == null) {
            DeclinePacket declinePacket = new DeclinePacket("There was an error while creating the lobby");
            player.serverConnection.send(declinePacket);
            return;
        }
        lobby.setName(name);
        lobby.addPlayer(player);
    }
}