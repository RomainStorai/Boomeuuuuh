package fr.imt.boomeuuuuh.network.packets.client;

import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;
import fr.imt.boomeuuuuh.network.packets.both.DeclinePacket;
import fr.imt.boomeuuuuh.players.Player;

public class CreateAccountPacket extends Packet {

    private final Player player;
    private final String username, password;

    public CreateAccountPacket(Player player, String username, String password) {
        super(PacketType.CREATE_ACCOUNT);

        this.player = player;
        this.username = username;
        this.password = password;
    }

    @Override
    protected byte[] encode() {
        // Shouldn't be used server-side
        return null;
    }

    @Override
    public void handle() {
        if (player.isAuthentified())
            return;

        // TODO MANAGE ACCOUNT CREATION
        if (true /* TODO check if possible */) {
            DeclinePacket packet = new DeclinePacket("Here goes the reason why it's not possible to create the account");
            player.serverConnection.send(packet);
            return;
        }

        player.authenticate(username, password);
    }
}
