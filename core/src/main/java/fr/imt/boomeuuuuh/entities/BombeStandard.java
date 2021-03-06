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

package fr.imt.boomeuuuuh.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import fr.imt.boomeuuuuh.Game;
import fr.imt.boomeuuuuh.utils.AssetsManager;
import fr.imt.boomeuuuuh.utils.Location;

public class BombeStandard extends Bomb {

    private static final Texture sheetTexture = new Texture("Bomb/Clignx16.png");
    private static final Texture explosion_verti = new Texture("Bomb/fire_verti.png");
    private static final Texture explosion_horiz = new Texture("Bomb/fire_horiz.png");
    private static final Animation<TextureRegion> animation;
    public boolean calculateExplosion = false;
    private boolean explode = false;
    private long time;

    static {
        TextureRegion[] tabRegion = TextureRegion.split(sheetTexture, 32, sheetTexture.getHeight())[0];
        animation = new Animation<TextureRegion>(0.25F, tabRegion);
    }

    private int right;
    private int left;
    private int up;
    private int down;

    private float animationTime = 0f;

    public BombeStandard(int id, Location location, World world, int power) {
        super(id, location, world, power);

        time = System.nanoTime();
    }

    public void draw(SpriteBatch batch, float delta) {
        if (explode) {
            drawExplosion(batch);

            if (System.nanoTime() - time > 6e8) {
                explode = false;
                //Game.getInstance().removeEntity(this);
            }

            return;
        }

        animationTime += delta;

        TextureRegion currentRegion = animation.getKeyFrame(animationTime, true);
        currentRegion.setRegion(currentRegion, 0, 0, 32, 32);
        batch.draw(currentRegion, this.getPixelX(), this.getPixelY());
        if (System.nanoTime() - time > 28e8) {
            AssetsManager.playSound("bomb");
            calculateExplosion();
        }
    }

    public void calculateExplosion() {
        if (calculateExplosion) return;

        up = Math.min(14, this.getBlocY() + power);
        down = Math.max(1, this.getBlocY() - power);
        left = Math.max(1, this.getBlocX() - power);
        right = Math.min(14, this.getBlocX() + power);

        calculateExplosion = true;
        explode = false;
        for (Entity E : Game.getInstance().getEntities()) {
            if (!(E instanceof Bloc)) continue;
            boolean hard = E instanceof HardBlock;
            if (E.getBlocY() == this.getBlocY()) {
                if ((this.getBlocX() - power <= E.getBlocX()) & (E.getBlocX() <= this.getBlocX() - 1)) {
                    left = Math.max(left, E.getBlocX() + (hard ? 1 : 0));
                }
                if ((this.getBlocX() + power >= E.getBlocX()) & (E.getBlocX() >= this.getBlocX() + 1)) {
                    right = Math.min(right, E.getBlocX() + (hard ? -1 : 0));
                }
            }

            if (E.getBlocX() == this.getBlocX()) {
                if ((this.getBlocY() - power <= E.getBlocY()) & (E.getBlocY() <= this.getBlocY() - 1)) {
                    down = Math.max(down, E.getBlocY() + (hard ? 1 : 0));
                }
                if ((this.getBlocY() + power >= E.getBlocY()) & (E.getBlocY() >= this.getBlocY() + 1)) {
                    up = Math.min(up, E.getBlocY() + (hard ? -1 : 0));
                }
            }
        }

        for (Entity B : Game.getInstance().getEntities()) {
            if (B == this)
                continue;
            if (B instanceof BombeStandard) {
                if (((BombeStandard) B).calculateExplosion) continue;
                if (((left <= B.getBlocX()) && (B.getBlocX() <= right) && (B.getBlocY() == this.getBlocY())) || ((down <= B.getBlocY()) && (B.getBlocY() <= up) && (B.getBlocX() == this.getBlocX()))) {
                    ((BombeStandard) B).calculateExplosion();
                }
            }
        }

        explode = true;
        time = System.nanoTime();
    }

    public void drawExplosion(Batch batch) {
        for (int k = 0; k < this.power; k++) {
            if (this.getBlocX() + k <= Math.min(this.getBlocX() + power, right)) {
                batch.draw(explosion_horiz, this.getPixelX() + (32 * k), this.getPixelY());
            }
            if (this.getBlocX() - k >= Math.max(this.getBlocX() - power, left)) {
                batch.draw(explosion_horiz, this.getPixelX() - (32 * k), this.getPixelY());
            }
            if (this.getBlocY() + k <= Math.min(this.getBlocY() + power, up)) {
                batch.draw(explosion_verti, this.getPixelX(), this.getPixelY() + (32 * k));
            }
            if (this.getBlocY() - k >= Math.max(this.getBlocY() - power, down)) {
                batch.draw(explosion_verti, this.getPixelX(), this.getPixelY() - (32 * k));
            }
        }
    }
}







