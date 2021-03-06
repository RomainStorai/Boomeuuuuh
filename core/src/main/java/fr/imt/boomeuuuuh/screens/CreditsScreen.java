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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import fr.imt.boomeuuuuh.MyGame;
import fr.imt.boomeuuuuh.utils.AssetsManager;

public class CreditsScreen implements Screen {

    private final MyGame game;
    private final Stage stage;
    private ImageButton credits;

    private static final Texture background = new Texture("Backgrounds/cow-1575964.jpg");

    public CreditsScreen(MyGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Skin skin = AssetsManager.getUISkin();

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        credits = new ImageButton(MyGame.getDrawable("text_sample/credits.png"));
        credits.setPosition(stage.getWidth()/2, stage.getHeight() - credits.getHeight() - 10, Align.center);
        credits.setColor(1,1,1,0.5f);
        stage.addActor(credits);

        TextButton contributorsTitle = new TextButton("CONTRIBUTORS", skin, "maroon-small");
        TextButton contributorsText = new TextButton(
                "Romain STORAI \n" +
                        "Benoit FAURE \n" +
                        "Matheo BREGIER \n" +
                        "Antoine MATHIEU \n" +
                        "Theo NICOLAU \n" +
                        "Arnaud GARRY \n\nThe project is under Apache licence v2.0", skin);
        TextButton externalTitle = new TextButton("EXTERNAL RESOURCES", skin, "maroon-small");
        TextButton externalText = new TextButton(
                "Cow picture : freeimages.com, by kitenellie, title : Cow \n" +
                        "Music : https://www.fesliyanstudios.com/ David Renda\nUsing LibGDX framework", skin);

        mainTable.add(contributorsTitle);
        mainTable.row().pad(0,0,0,0);
        mainTable.add(contributorsText);
        mainTable.row().pad(20,0,0,0);
        mainTable.add(externalTitle);
        mainTable.row().pad(0,0,0,0);
        mainTable.add(externalText);

        stage.addActor(mainTable);

        // return to main screen button
        ImageButton backButton = new ImageButton(MyGame.getDrawable("text_sample/back.png"));
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(ScreenType.MAIN_MENU);
            }
        });
        stage.addActor(backButton);
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

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // change the stage's viewport when teh screen size is changed
        stage.getViewport().update(width, height, true);
        credits.setPosition(stage.getWidth()/2, stage.getHeight() - credits.getHeight(), Align.center);
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
