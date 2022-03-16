package fr.imt.boomeuuuuh.Game;

import fr.imt.boomeuuuuh.Boomeuuuuh;
import fr.imt.boomeuuuuh.entities.Entity;
import fr.imt.boomeuuuuh.lobbies.Lobby;
import fr.imt.boomeuuuuh.players.Player;
import fr.imt.boomeuuuuh.entities.bombs.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.TimeUnit;

public class GameManager {

    //Global references
    private final Lobby lobby;
    private final List<Entity> entityList;

    //Time references
    private final long minimumTimePerUpdate = 50; //ms
    private long currentUpdateStartTime;

    //Map references
    String mapID;
    int mapHeight;
    int mapWidth;
    private final int[][] baseMap;

    //Id references
    private int lastID;

    public GameManager(Lobby lobby, String mapID){
        //Set vars
        this.lobby = lobby;
        entityList = new ArrayList<>();

        //Load Map
        this.mapID = mapID;
        //TODO : Load Map from mapID
        mapWidth = 100;
        mapHeight = 100;
        baseMap = new int[mapWidth][mapHeight];
    }

    //-----------------------------------------------------
    private void Update(){
        //Set time
        currentUpdateStartTime = System.currentTimeMillis();

        //---Bombs---
        for (Entity e : entityList) {
            if(e instanceof Bomb)
                ((Bomb) e).checkExplosion(entityList, baseMap);//Faille possible

        }
        //-----------

        try{ TimeUnit.MILLISECONDS.sleep(System.currentTimeMillis() + minimumTimePerUpdate - currentUpdateStartTime);}
        catch (Exception e) { Boomeuuuuh.logger.severe(e.getMessage()); }
    }
    //-----------------------------------------------------
    //------------------------BOMBS------------------------
    public void placeBomb(Player origin, Bomb bombType){}
    //-----------------------------------------------------
}
