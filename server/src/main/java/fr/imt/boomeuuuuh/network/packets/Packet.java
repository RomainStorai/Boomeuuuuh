/*
 * Copyright (c) 2022.
 * Authors : Storaï R, Faure B, Mathieu A, Garry A, Nicolau T, Bregier M.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package fr.imt.boomeuuuuh.network.packets;

import fr.imt.boomeuuuuh.Server;
import fr.imt.boomeuuuuh.players.Player;

import java.net.InetAddress;

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
    public static Packet getFromBytes(byte[] packet, InetAddress address) {
        int type = packet[0] + 126;
        int size = packet[1] + 126;
        byte[] data = extractData(packet, size);
        PacketType packetType = PacketType.values()[type];

        Packet instance = packetType.make(data);
        if (instance.packetType == PacketType.TEST)
            instance = packetType.make(data, Server.getPlayer(address));
        return instance;
    }

    /**
     * Transforms a byte raw data to a Packet
     *
     * @param packet raw data
     * @param port   port
     * @return Packet built
     */
    public static Packet getFromBytes(byte[] packet, InetAddress address, int port) {
        int type = packet[0] + 126;
        int size = packet[1] + 126;
        byte[] data = extractData(packet, size);
        PacketType packetType = PacketType.values()[type];

        Packet instance = packetType.make(data);
        if (instance.packetType == PacketType.TEST)
            instance = packetType.make(data, Server.getPlayer(address, port));
        return instance;
    }

    /**
     * Transforms a byte raw data to a Packet
     *
     * @param packet raw data
     * @return Packet built
     */
    public static Packet getFromBytes(byte[] packet, Player player) {
        int type = packet[0] + 126;
        int size = packet[1] + 126;
        byte[] data = extractData(packet, size);
        PacketType packetType = PacketType.values()[type];
        Packet instance = packetType.make(data);
        if (instance.packetType == PacketType.TEST)
            instance = packetType.make(data, player);
        return instance;
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
