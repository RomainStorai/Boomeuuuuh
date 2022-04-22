package fr.imt.boomeuuuuh.network.packets.server;

import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;

import java.nio.charset.StandardCharsets;

public class ReceiveChatPacket extends Packet {

    private final String message;

    /**
     * Chat communication from the server to the client
     * @param message message sent from the server, contains the senders name if it originates from a client
     */
    public ReceiveChatPacket(String message) {
        super(PacketType.RECEIVE_CHAT);

        this.message = message;
    }

    @Override
    protected byte[] encode() {
        return message.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void handle() {
        // Shouldn't be handled server side
    }
}
