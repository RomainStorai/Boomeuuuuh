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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import fr.imt.boomeuuuuh.Game;
import fr.imt.boomeuuuuh.MyGame;
import fr.imt.boomeuuuuh.entities.*;
import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;
import fr.imt.boomeuuuuh.utils.Location;

public class EntityCreatePacket extends Packet {

    private final int entityId, entityType;
    private final Location location;

    public EntityCreatePacket(int entityId, int entityType, Location location) {
        super(PacketType.ENTITY_CREATE);
        this.entityId = entityId;
        this.entityType = entityType;
        this.location = location;
    }

    @Override
    protected byte[] encode() {
        // Shouldn't be used client-side
        return null;
    }

    @Override
    public void handle() {
        if (!MyGame.getInstance().logged || MyGame.getInstance().lobby == null)
            return;

        Game game = Game.getInstance();

        while ((game = Game.getInstance()) == null) {
        }

        final Game finalGame = game;

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                World world = finalGame.getWorld();

                Entity e = null;

                switch (entityType) {
                    case 60:
                        e = new Player(entityId, location, world);
                        break;
                    case 50:
                        e = new SoftBlock(entityId, location, world);
                        break;
                    case 40:
                        e = new HardBlock(entityId, location, world);
                        break;
                    case 20:
                        e = new PowerUP(entityId, location, world);
                }

                if (e == null)
                    return;

                finalGame.spawnEntity(e);
            }
        });
    }
}
