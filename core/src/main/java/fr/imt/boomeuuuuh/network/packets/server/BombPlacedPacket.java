package fr.imt.boomeuuuuh.network.packets.server;

import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;
import fr.imt.boomeuuuuh.utils.Location;

public class BombPlacedPacket extends Packet {

    private final int entityId, power;
    private final Location location;

    public BombPlacedPacket(int entityId, int power, Location location) {
        super(PacketType.BOMB_PLACED);
        this.entityId = entityId;
        this.power = power;
        this.location = location;
    }

    @Override
    protected byte[] encode() {
        // Shouldn't be used client-side
        return null;
    }

    @Override
    public void handle() {
        // TODO
    }
}
