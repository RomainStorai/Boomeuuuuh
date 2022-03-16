package fr.imt.boomeuuuuh.network.packets;

import fr.imt.boomeuuuuh.Player;
import fr.imt.boomeuuuuh.network.packets.both.DeclinePacket;
import fr.imt.boomeuuuuh.network.packets.both.TestPacket;
import fr.imt.boomeuuuuh.network.packets.client.*;

import java.math.BigInteger;

public enum PacketType {

    TEST {
        @Override
        public Packet make(byte[] data) {
            return new TestPacket(new String(data));
        }
    },
    DECLINE {
        @Override
        public Packet make(byte[] data, Player player) {
            return new DeclinePacket(new String(data), player);
        }
    },
    LOBBY_LIST,
    REQUEST_LOBBY_LIST {
        @Override
        public Packet make(byte[] data, Player player) {
            return new RequestLobbyListPacket(player);
        }
    },
    JOIN_LOBBY {
        @Override
        public Packet make(byte[] data, Player player) {
            return new JoinLobbyPacket(player);
        }
    },
    LOBBY_INFO,
    SEND_CHAT {
        @Override
        public Packet make(byte[] data, Player player) {
            return new SendChatPacket(new String(data), player);
        }
    },
    RECEIVE_CHAT,
    CREATE_LOBBY {
        @Override
        public Packet make(byte[] data, Player player) {
            return new CreateLobbyPacket(player, new String(data));
        }
    },
    KICK,
    LEAVE {
        @Override
        public Packet make(byte[] data, Player player) {
            return new LeavePacket(player);
        }
    },
    DISCONNECT {
        @Override
        public Packet make(byte[] data, Player player) {
            return new DisconnectPacket(player);
        }
    },
    INITIALIZE_LOBBY_CONNECTION {
        @Override
        public Packet make(byte[] data, Player player) {
            return new InitializeLobbyConnectionPacket(new BigInteger(data).intValueExact(), player);
        }
    },
    SUCCESSFULLY_JOINED;

    public Packet make(byte[] data) {

        /*
        This method won't be used : Server can't receive server packets
         */

        return new Packet(TEST) {
            @Override
            protected byte[] encode() {
                return null;
            }

            @Override
            public void handle() {
                // Nothing
            }
        };
    }

    public Packet make(byte[] data, Player player) {
        /*
        This method won't be used : Server can't receive server packets
         */

        return new Packet(TEST) {
            @Override
            protected byte[] encode() {
                return null;
            }

            @Override
            public void handle() {
                // Nothing
            }
        };
    }
}
