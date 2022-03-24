package fr.imt.boomeuuuuh.network.packets;

public abstract class Packet {

    private final PacketType packetType;

    public Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    protected abstract byte[] encode();

    public abstract void handle();

    /**
     * Gets raw data from a packet, step before sending through network
     *
     * @return bytes
     */
    public byte[] getBytes() {
        byte[] data = encode();
        byte[] packet = new byte[data.length + 2];
        System.out.println("S Packet " + packetType.ordinal());
        packet[0] = (byte) (packetType.ordinal() - 126);
        packet[1] = (byte) (data.length - 126);
        System.arraycopy(data, 0, packet, 2, data.length);
        return packet;
    }

    /**
     * Transforms a byte raw data to a Packet
     *
     * @param packet raw data
     * @return Packet built
     */
    public static Packet getFromBytes(byte[] packet) {
        int type = packet[0] + 126;
        if (type < 0 || type >= PacketType.values().length)
            type = 0;
        System.out.println("R Packet " + type);
        int size = packet[1] + 126;
        byte[] data = extractData(packet, size);
        PacketType packetType = PacketType.values()[type];

        return packetType.make(data);
    }

    /**
     * Extracts data from a packet (removing the header)
     *
     * @param packet Packet to extract
     * @param size   of the packet
     * @return extracted data
     */
    private static byte[] extractData(byte[] packet, int size) {
        byte[] data = new byte[size];
        System.arraycopy(packet, 2, data, 0, size);
        return data;
    }
}