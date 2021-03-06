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

package fr.imt.boomeuuuuh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import fr.imt.boomeuuuuh.entities.Bomb;
import fr.imt.boomeuuuuh.entities.Entity;
import fr.imt.boomeuuuuh.entities.Player;
import fr.imt.boomeuuuuh.network.packets.client.BombPlacePacket;
import fr.imt.boomeuuuuh.network.packets.client.PlayerChangeBlocPacket;
import fr.imt.boomeuuuuh.utils.Location;

import java.util.ArrayList;
import java.util.List;

public class Game implements InputProcessor {

    private static Game instance;

    private final List<Entity> entities;
    private final List<Entity> toBeRemovedEntities;
    private final SpriteBatch batch;
    private final SpriteBatch hudBatch;
    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    private final BitmapFont font = new BitmapFont();
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private FitViewport viewport;

    private final HUD hud;

    public Player player;

    public List<Bomb> toChangeCollision = new ArrayList<>();

    public int player_bomb = 1;
    public int player_bomb_power = 1;
    public int player_speed = 1;
    public int player_kills = 0;

    public int lastBlocX = 0;
    public int lastBlocY = 0;

    public Game() {
        instance = this;

        loadMap();
        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(), false);

        entities = new ArrayList<>();
        toBeRemovedEntities = new ArrayList<>();

        createWorldBorders();

        hud = new HUD(480, 480);
    }

    /**
     * Creates the world border to avoid players going out
     */
    private void createWorldBorders() {
        BodyDef borders = new BodyDef();
        borders.position.set(new Vector2());

        Body bordersBody = world.createBody(borders);
        EdgeShape edgeShape = new EdgeShape();
        FixtureDef fixtureBorders = new FixtureDef();
        fixtureBorders.filter.categoryBits = Entity.SOLID_CATEGORY;
        edgeShape.set(new Vector2(32 / 100f, 32 / 100f), new Vector2(14 * 32 / 100f, 32 / 100f));
        fixtureBorders.shape = edgeShape;
        bordersBody.createFixture(fixtureBorders);
        edgeShape.set(new Vector2(14 * 32 / 100f, 32 / 100f), new Vector2(14 * 32 / 100f, 14 * 32 / 100f));
        fixtureBorders.shape = edgeShape;
        bordersBody.createFixture(fixtureBorders);
        edgeShape.set(new Vector2(32 / 100f, 32 / 100f), new Vector2(32 / 100f, 14 * 32 / 100f));
        fixtureBorders.shape = edgeShape;
        bordersBody.createFixture(fixtureBorders);
        edgeShape.set(new Vector2(32 / 100f, 14 * 32 / 100f), new Vector2(14 * 32 / 100f, 14 * 32 / 100f));
        fixtureBorders.shape = edgeShape;
        bordersBody.createFixture(fixtureBorders);

        edgeShape.dispose();
    }

    /**
     * Gets the Singleton Instance
     *
     * @return Game
     */
    public static Game getInstance() {
        return instance;
    }

    /**
     * Method called each delta times
     *
     * @param delta time
     */
    public void draw(float delta) {
        handleMovements();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Matrix4 debugMatrix = batch.getProjectionMatrix().cpy().scale(100,
                100, 0);

        world.step(delta, 8, 3);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        hudBatch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);
        renderer.render();

        batch.begin();
        for (Entity entity : entities) {
            entity.draw(batch, delta);
        }
        batch.end();

        hudBatch.begin();
        hud.drawHUD(hudBatch);
        hudBatch.end();

        removeEntities();
    }

    /**
     * Called when the frame is resized
     *
     * @param width  resi
     * @param height resi
     */
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /**
     * Load the map from the file
     */
    public void loadMap() {
        map = new TmxMapLoader().load("map/map1Final.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        camera = new OrthographicCamera(480, 480);
        camera.setToOrtho(false);
        camera.update();
        camera.position.x = 240;
        camera.position.y = 240;
        viewport = new FitViewport(480, 480, camera);
    }

    /**
     * Called when closed
     */
    public void dispose() {
        map.dispose();
        renderer.dispose();
        batch.dispose();
        hudBatch.dispose();
    }

    /**
     * Spawns an entity
     *
     * @param entity to spawn
     */
    public void spawnEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Remove an entity on the next update
     *
     * @param entity to remove
     */
    public void removeEntity(Entity entity) {
        toBeRemovedEntities.add(entity);
    }

    /**
     * Remove an entity by its id
     *
     * @param entityId to remove
     */
    public void removeEntity(int entityId) {
        Entity entity = getEntity(entityId);
        if (entity != null)
            removeEntity(entity);
    }

    /**
     * Clear all the entities to be removed
     */
    private void removeEntities() {
        for (Entity entity : toBeRemovedEntities) {
            if (entity.getBody() != null)
                world.destroyBody(entity.getBody());
            entity.dispose();
            entities.remove(entity);
        }
        toBeRemovedEntities.clear();
    }

    /**
     * Get an entity by its id
     *
     * @param id of the entity
     * @return Entity
     */
    public Entity getEntity(int id) {
        Entity found = null;
        for (Entity entity : new ArrayList<>(entities)) {
            if (entity.getId() == id) {
                found = entity;
                break;
            }
        }
        return found;
    }

    /**
     * Get all the current displayed entities
     *
     * @return List of all entities
     */
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * @return box2D World
     */
    public World getWorld() {
        return world;
    }

    /**
     * Checks the current input of the player
     */
    public void handleMovements() {
        if (player == null)
            return;

        int currentBlocX = player.getBlocX();
        int currentBlocY = player.getBlocY();
        if (lastBlocX != currentBlocX || lastBlocY != currentBlocY) {
            MyGame.getInstance().serverConnection.send(new PlayerChangeBlocPacket(new Location(currentBlocX, currentBlocY)));
            lastBlocX = currentBlocX;
            lastBlocY = currentBlocY;

            if (!toChangeCollision.isEmpty()) {
                List<Bomb> toRemove = new ArrayList<>();

                for (Bomb bomb : toChangeCollision) {
                    if (bomb.getBlocY() != currentBlocY || bomb.getBlocX() != currentBlocX) {
                        bomb.getBody().getFixtureList().get(0).getFilterData().categoryBits = Entity.SOLID_CATEGORY;
                        bomb.getBody().getFixtureList().get(0).getFilterData().maskBits = Entity.PLAYER_CATEGORY;
                        toRemove.add(bomb);
                    }
                }

                toChangeCollision.removeAll(toRemove);
            }
        }

        boolean left = Gdx.input.isKeyPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.Z) || Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);

        if (player != null)
            player.getBody().setLinearVelocity(new Vector2((left ? -1f : 0f) + (right ? 1f : 0f), (up ? 1f : 0) + (down ? -1f : 0f)).nor().scl(1 + (player_speed - 1) / 6f));
    }

    @Override
    public boolean keyDown(int keycode) {
        if (player == null)
            return true;

        if (keycode == Input.Keys.SPACE)
            MyGame.getInstance().serverConnection.send(new BombPlacePacket(new Location(player.getBlocX(), player.getBlocY())));
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}