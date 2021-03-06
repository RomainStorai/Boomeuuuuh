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

package fr.imt.boomeuuuuh.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import fr.imt.boomeuuuuh.MyGame;
import fr.imt.boomeuuuuh.PlayerData;
import fr.imt.boomeuuuuh.network.packets.client.SelectSkinPacket;
import fr.imt.boomeuuuuh.network.packets.client.UnlockSkinPacket;
import fr.imt.boomeuuuuh.utils.AssetsManager;

public class SkinsScreen implements Screen {

    private final MyGame game;
    private final Stage stage;
    private Label skinLabel;
    private ScrollPane skinScroll;
    public Table skinTable;
    public Label gold;
    public boolean act = true;

    private ImageButton lobbyButton;

    private static final Texture background = new Texture("Backgrounds/cow-1575964.jpg");

    public SkinsScreen(MyGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }


    public void show_old() {
        Skin skin = AssetsManager.getUISkin();

        Table table = new Table();
        table.setFillParent(true);

        gold = new Label(MyGame.getInstance().playerData.gold + " gold", skin);
        table.add(gold);

        skinLabel = new Label("", skin);
        skinLabel.setWrap(true);
        skinLabel.setAlignment(Align.center);

        skinScroll = new ScrollPane(skinLabel, skin);
        skinScroll.setFadeScrollBars(false);

        table.add(skinScroll).fill();
        table.row();

        skinTable = new Table();
        table.add(skinTable).fill();
        table.row();

        PlayerData playerData = MyGame.getInstance().playerData;

        for (final fr.imt.boomeuuuuh.utils.Skin value : fr.imt.boomeuuuuh.utils.Skin.values()) {
            skinTable.row().pad(10, 0, 10, 0);
            boolean hasSkin = playerData.hasSkin(value);
            TextButton button;
            if (hasSkin) {
                if (playerData.getCurrentSkin() == value) {
                    button = new TextButton("Current Skin", skin, "maroon");
                } else {
                    button = new TextButton("Select Skin", skin);
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            if (!act) return;
                            MyGame.getInstance().serverConnection.send(new SelectSkinPacket(value.getDataName()));
                            act = false;
                        }
                    });
                }
            } else {
                if (playerData.gold >= 100) {
                    button = new TextButton("100 gold", skin);
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            if (!act) return;
                            MyGame.getInstance().serverConnection.send(new UnlockSkinPacket(value.getDataName()));
                            act = false;
                        }
                    });
                } else {
                    button = new TextButton("Not enough gold", skin);
                }
            }

            skinTable.add(value.getIcon()).width(32).height(32);
            skinTable.add(button).fillX().uniformX();
        }

        stage.addActor(table);

        // return to main screen button
        ImageButton backButton = new ImageButton(MyGame.getDrawable("text_sample/back.png"));
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(ScreenType.STATS);
            }
        });
        stage.addActor(backButton);


        lobbyButton = new ImageButton(MyGame.getDrawable((MyGame.getInstance().hasLobby())? "text_sample/lobby.png" : "text_sample/lobby_selection.png"));
        lobbyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen((MyGame.getInstance().hasLobby())? ScreenType.LOBBY : ScreenType.LOBBY_SELECTION);
            }
        });
        lobbyButton.right();
        lobbyButton.setPosition(stage.getWidth() - lobbyButton.getWidth(), 0);
        stage.addActor(lobbyButton);
    }

    @Override
    public void show() {
        Skin skin = AssetsManager.getUISkin();

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Label goldLabel = new Label(MyGame.getInstance().playerData.gold + " gold", skin, "title-white");

        Table skinListTable = new Table();
        skinListTable.setFillParent(true);
        ScrollPane listScroll = new ScrollPane(skinListTable);

        PlayerData data = MyGame.getInstance().playerData;

        //Making the list of skins
        int count = 0;
        Table listSubTable = new Table();
        for (final fr.imt.boomeuuuuh.utils.Skin value : fr.imt.boomeuuuuh.utils.Skin.values()){

            //---------Make the table that holds the skin---------
            Table skinTable = new Table();
            skinTable.background(skin.getDrawable("button-orange"));
            skinTable.add(value.getIcon());

            if (data.getCurrentSkin() == value) {
                Label subLabel = new Label("Current Skin", skin, "white");
                skinTable.add(subLabel);
            } else {

                if(data.hasSkin(value)){
                    Label subLabel = new Label("Select Skin", skin, "white");
                    skinTable.add(subLabel);
                    skinTable.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (!act) return;
                            MyGame.getInstance().serverConnection.send(new SelectSkinPacket(value.getDataName()));
                            act = false;
                        }
                    });

                } else{

                    if (data.gold >= 100){
                        Label subLabel = new Label("Buy (100)", skin, "white");
                        skinTable.add(subLabel);
                        skinTable.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                if (!act) return;
                                MyGame.getInstance().serverConnection.send(new UnlockSkinPacket(value.getDataName()));
                                act = false;
                            }
                        });
                    } else{
                        Label subLabel = new Label("Buy (100)", skin, "white");
                        skinTable.add(subLabel);
                        skinTable.background(skin.getDrawable("button-maroon"));
                    }

                }
            }
            //----------------------------------------------------

            if(count == 0){
                listSubTable = new Table();
                listSubTable.add(skinTable);
            }else{
                listSubTable.row().pad(1,0,1,0);
                listSubTable.add(skinTable);
                if(count == 2)
                    skinListTable.add(listSubTable);
            }
            count = (count + 1) % 3;
        }
        if(count != 2)
            skinListTable.add(listSubTable);

        mainTable.add(goldLabel);
        mainTable.row().pad(5,0,5,0);
        mainTable.add(listScroll);

        stage.addActor(mainTable);

        // return to main screen button
        ImageButton backButton = new ImageButton(MyGame.getDrawable("text_sample/back.png"));
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(ScreenType.STATS);
            }
        });
        stage.addActor(backButton);


        lobbyButton = new ImageButton(MyGame.getDrawable((MyGame.getInstance().hasLobby())? "text_sample/lobby.png" : "text_sample/lobby_selection.png"));
        lobbyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen((MyGame.getInstance().hasLobby())? ScreenType.LOBBY : ScreenType.LOBBY_SELECTION);
            }
        });
        lobbyButton.right();
        lobbyButton.setPosition(stage.getWidth() - lobbyButton.getWidth(), 0);
        stage.addActor(lobbyButton);
    }

    public void reShow(){
        act = true;
        stage.clear();
        show();
    }


    @Override
    public void render(float delta) {
        AssetsManager.playMusic("menu");
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Perfect background
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, stage.getWidth(), stage.getHeight());
        stage.getBatch().end();

        //Put the correct position for the button
        lobbyButton.setPosition(stage.getWidth() - lobbyButton.getWidth(), 0);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // change the stage's viewport when teh screen size is changed
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // dispose of assets when not needed anymore
        stage.dispose();
    }
}