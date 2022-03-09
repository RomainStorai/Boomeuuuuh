package fr.imt.test;

import fr.imt.boomeuuuuh.Server;
import fr.imt.boomeuuuuh.network.LobbyConnection;
import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.both.TestPacket;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;

public class NetworkTest {

    @org.junit.jupiter.api.Test
    public void testTestPacket() throws UnknownHostException {
        TestPacket destruct = new TestPacket("Ceci est test");
        byte[] bytes = destruct.getBytes();
        TestPacket reconstruct = (TestPacket) Packet.getFromBytes(bytes, InetAddress.getLocalHost());
        Assertions.assertEquals(destruct.getMessage(), reconstruct.getMessage());
    }

    @org.junit.jupiter.api.Test
    public void testTCPSocket() throws IOException, InterruptedException {
        Server server = new Server(25500);
        server.start();
        Socket socket = new Socket(InetAddress.getLocalHost(), 25500);
        Thread.sleep(100);
        Assertions.assertFalse(Server.getPlayers().isEmpty());
        PrintStream writer = new PrintStream(socket.getOutputStream());
        writer.println(new String(new TestPacket("Test").getBytes()));
        socket.close();
        Thread.sleep(100);
        Assertions.assertTrue(Server.getPlayers().isEmpty());
        server.close();
    }

    @org.junit.jupiter.api.Test
    public void testUDPSocket() throws IOException, InterruptedException {
        LobbyConnection lobbyConnection = new LobbyConnection();
        int port = lobbyConnection.getPort();
        DatagramSocket socket = new DatagramSocket();
        byte[] testPacket = new TestPacket("Test").getBytes();
        byte[] testPacket1 = new TestPacket("Test 1").getBytes();
        DatagramPacket packet = new DatagramPacket(testPacket, testPacket.length, InetAddress.getLocalHost(), port);
        DatagramPacket packet1 = new DatagramPacket(testPacket1, testPacket1.length, InetAddress.getLocalHost(), port);
        socket.send(packet);
        socket.send(packet1);
    }
}
