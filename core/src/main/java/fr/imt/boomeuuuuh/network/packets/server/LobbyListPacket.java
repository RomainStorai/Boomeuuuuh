package fr.imt.boomeuuuuh.network.packets.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fr.imt.boomeuuuuh.MyGame;
import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;
import fr.imt.boomeuuuuh.network.packets.client.JoinLobbyPacket;
import fr.imt.boomeuuuuh.screens.LobbySelectionScreen;
import fr.imt.boomeuuuuh.screens.ScreenType;
import fr.imt.boomeuuuuh.utils.AssetsManager;
import fr.imt.boomeuuuuh.utils.LobbyInfoList;

import java.util.ArrayList;
import java.util.List;

public class LobbyListPacket extends Packet {

    private final String rawString;

    public LobbyListPacket(String rawString) {
        super(PacketType.LOBBY_LIST);
        this.rawString = rawString;
    }

    @Override
    protected byte[] encode() {
        // Shouldn't be used client-side
        return null;
    }

    @Override
    public void handle() {
        if (rawString == null || rawString.length() <= 2)
            return;

        String[] lobbies = rawString.split("[/]");
        final LobbySelectionScreen lobbySelectionScreen = (LobbySelectionScreen) MyGame.getInstance().getScreen(ScreenType.LOBBY_SELECTION);
        final List<LobbyInfoList> list = new ArrayList<>();
        for (String lobby : lobbies) {
            String[] data = lobby.split("[|]");
            LobbyInfoList lobbyInfoList = new LobbyInfoList();
            lobbyInfoList.name = data[0];
            lobbyInfoList.currentPlayers = Integer.parseInt(data[1]);
            lobbyInfoList.open = data[2].equalsIgnoreCase("1");
            list.add(lobbyInfoList);
        }
        lobbySelectionScreen.lobbies = list;

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Skin skin = AssetsManager.getUISkin();
                Table scrollTable = lobbySelectionScreen.scrollTable;
                scrollTable.clear();
                for (final LobbyInfoList lobbyInfoList : list) {
                    scrollTable.row().pad(10, 0, 10, 0);

                    //Table thisTable = new Table();
                    //thisTable.setBackground(skin.getDrawable("Button"));
                    //scrollTable.add(thisTable);

                    final TextButton button = new TextButton(lobbyInfoList.name + " (" + lobbyInfoList.currentPlayers + "/8)", skin);
                    button.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            MyGame.getInstance().serverConnection.send(new JoinLobbyPacket(lobbyInfoList.name));
                        }
                    });
                    scrollTable.add(button).fillX().uniformX();
                    // add a picture
                    Image img = new Image(new Texture("Lobby/lock.png"));
                    if (lobbyInfoList.open)
                        img = new Image(new Texture("Lobby/open.png"));

                    scrollTable.add(img).width(32).height(40);
                }
            }
        });

    }
}
