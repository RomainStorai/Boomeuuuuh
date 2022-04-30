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

import fr.imt.boomeuuuuh.Game;
import fr.imt.boomeuuuuh.MyGame;
import fr.imt.boomeuuuuh.entities.Entity;
import fr.imt.boomeuuuuh.entities.MovableEntity;
import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;
import fr.imt.boomeuuuuh.utils.Location;

public class EntityMovePacket extends Packet {

    private final int entityId;
    private final Location location;
    private final int speed;

    public EntityMovePacket(int entityId, Location location, int speed) {
        super(PacketType.ENTITY_MOVE);
        this.entityId = entityId;
        this.location = location;
        this.speed = speed;
    }

    @Override
    protected byte[] encode() {
        // Shouldn't be used client-side
        return null;
    }

    @Override
    public void handle() {
        Game game = Game.getInstance();

        if (!MyGame.getInstance().logged || MyGame.getInstance().lobby == null || game == null)
            return;

        if (game.player != null && entityId == game.player.getId())
            return;

        Entity entity = game.getEntity(entityId);

        if (!(entity instanceof MovableEntity))
            return;

        ((MovableEntity) entity).setToBloc(location, speed);
    }
}
