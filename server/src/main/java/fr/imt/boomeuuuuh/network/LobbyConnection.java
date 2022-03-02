package fr.imt.boomeuuuuh.network;

import fr.imt.boomeuuuuh.Boomeuuuuh;
import fr.imt.boomeuuuuh.Player;
import fr.imt.boomeuuuuh.Server;
import fr.imt.boomeuuuuh.network.packets.Packet;

import java.io.IOException;
import java.net.*;

public class LobbyConnection extends Thread {

    private final DatagramSocket socket;

    public LobbyConnection(int port) throws SocketException {
        this.socket = new DatagramSocket(port);

        this.start();
    }

    @Override
    public void run() {
        while (true /* TODO make it closable */) {
            DatagramPacket incomingPacket = new DatagramPacket(new byte[28], 28); // TODO change buffer size to optimize
            try {
                socket.receive(incomingPacket);
                // TODO Handle packet and link to a specific player
            } catch (IOException e) {
                Boomeuuuuh.logger.severe("Error while reading incoming UDP packets : " + e.getMessage());
            }
        }
    }

    /**
     * Sends one or more packets to a specific player through UDP
     *
     * @param player  Receiver
     * @param packets Packets to send
     */
    public void send(Player player, Packet... packets) {
        InetAddress address = player.getAddress();
        int updPort = player.getUDPPort();

        for (Packet packet : packets) {
            byte[] packed = packet.getBytes();
            try {
                socket.send(new DatagramPacket(packed, packed.length, address, updPort));
            } catch (IOException e) {
                Boomeuuuuh.logger.severe("Impossible to send packets to " + player.getAddress());
            }
        }
    }
}
