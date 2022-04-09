package fr.imt.boomeuuuuh.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.common.base.CharMatcher;
import fr.imt.boomeuuuuh.MyGame;
import fr.imt.boomeuuuuh.network.ServerConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPAddressScreen implements Screen {

    private static final Pattern illegalChars = Pattern.compile("[~#@*+%{}<>\\[\\]|\"\\_^ ]");
    private static final Pattern notNumbers = Pattern.compile("[a-zA-Z]");

    private final MyGame game;
    private final Stage stage;
    public Label label;

    public IPAddressScreen(MyGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        // temporary until we have asset manager in
        Skin skin = new Skin(Gdx.files.internal("skin/neon-ui.json"));

        //create elements
        label = new Label("Choose your server", skin);
        final TextField ip = new TextField("193.26.14.30", skin);
        final TextField port = new TextField("25566", skin);
        TextButton connect = new TextButton("Connect", skin);
        final TextButton backButton = new TextButton("Back", skin); // the extra argument here "small" is used to set the button to the smaller version instead of the big default version


        //add buttons to table
        table.add(label).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(ip).fillX().uniformX();
        table.row();
        table.add(port).fillX().uniformX();
        table.row();
        table.add(connect).fillX().uniformX();
        table.row();
        table.add(backButton).fillX().uniformX();

        // create button listeners
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(ScreenType.MAIN_MENU);
            }
        });

        connect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String address = ip.getText();
                String strPort = port.getText();
                if (address == null || strPort == null)
                    return;
                if (containsIllegalChars(address) || containsIllegalCharsAndNumbers(strPort)) {
                    label.setText("Please type a real IP address and/or port");
                    return;
                }

                if (game.connected) {
                    MyGame.getInstance().changeScreen(ScreenType.LOG_IN);
                    return;
                }

                try {
                    MyGame.SERVER_ADDRESS = InetAddress.getByName(address);
                    MyGame.SERVER_PORT_TCP = Integer.parseInt(strPort);
                } catch (UnknownHostException e) {
                    label.setText("Unknown host.");
                    return;
                } catch (NumberFormatException e) {
                    label.setText("Port isn't a number.");
                    return;
                }

                try {
                    game.serverConnection = new ServerConnection(MyGame.SERVER_ADDRESS, MyGame.SERVER_PORT_TCP);
                    game.connected = true;
                    game.changeScreen(ScreenType.LOG_IN);
                } catch (IOException e) {
                    game.connected = false;
                    label.setText("No connection. Try another server");
                }
            }
        });
    }


    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // dispose of assets when not needed anymore
        stage.dispose();
    }

    private boolean containsIllegalChars(String str) {
        Matcher matcher = illegalChars.matcher(str);
        return matcher.find() || !CharMatcher.ascii().matchesAllOf(str);
    }

    private boolean containsIllegalCharsAndNumbers(String str) {
        Matcher matcher = illegalChars.matcher(str);
        Matcher matcher2 = notNumbers.matcher(str);
        return matcher.find() || matcher2.find() || !CharMatcher.ascii().matchesAllOf(str);
    }

}