package model;

public class GameDecomplier {
    private GameData[] games;

    public GameDecomplier(GameData[] games){
        this.games = games;
    }

    public GameData[] getGames(){
        return games;
    }
}
