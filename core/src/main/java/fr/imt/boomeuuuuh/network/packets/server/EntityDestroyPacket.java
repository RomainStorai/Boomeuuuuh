package fr.imt.boomeuuuuh.network.packets.server;

import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;

public class EntityDestroyPacket extends Packet {

    private final int entityId;

    public EntityDestroyPacket(int entityId) {
        super(PacketType.ENTITY_DESTROY);
        this.entityId = entityId;
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