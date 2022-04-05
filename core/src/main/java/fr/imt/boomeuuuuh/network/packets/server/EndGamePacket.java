package fr.imt.boomeuuuuh.network.packets.server;

import com.badlogic.gdx.Gdx;
import fr.imt.boomeuuuuh.MyGame;
import fr.imt.boomeuuuuh.lobbies.Lobby;
import fr.imt.boomeuuuuh.lobbies.LobbyState;
import fr.imt.boomeuuuuh.network.packets.Packet;
import fr.imt.boomeuuuuh.network.packets.PacketType;
import fr.imt.boomeuuuuh.screens.ScreenType;

public class EndGamePacket extends Packet {

    public EndGamePacket() {
        super(PacketType.END_GAME);
    }

    @Override
    protected byte[] encode() {
        // Shouldn't be used client-side
        return null;
    }

    @Override
    public void handle() {
        final Lobby lobby = MyGame.getInstance().lobby;
        if (!MyGame.getInstance().logged || lobby == null)
            return;

        lobby.game = null;
        lobby.state = LobbyState.WAITING;

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                MyGame.getInstance().changeScreen(ScreenType.LOBBY);
            }
        });
    }
}
