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

package fr.imt.boomeuuuuh.network.packets.server;

import com.google.common.primitives.Ints;
import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;

public class LobbyCredentialsPacket extends Packet {

    private final int port;

    /**
     * Information regarding what UDP port the client can contact the current lobby on
     * @param port port the lobby communicates on
     */
    public LobbyCredentialsPacket(int port) {
        super(PacketType.LOBBY_CREDENTIALS);
        this.port = port;
    }

    @Override
    protected byte[] encode() {
        return Ints.toByteArray(port);
    }

    @Override
    public void handle() {
        // Shouldn't be handled server side
    }
}
