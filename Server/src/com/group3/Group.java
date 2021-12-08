package com.group3;

import com.group3.models.Response;
import com.group3.models.User;

import java.util.ArrayList;

public class Group {
    /**
     * The Group class creates group of participating players.
     * */
//    private static int idGen = 10000;
    private static int groupID = 10000;
    private int numberOfGroups;
    private ArrayList<User> players = new ArrayList<>();
    Response response = new Response();
    Group(){
        groupID++;
    }
    /**
     * @return the current group id.
     * */
    public int getGroupID(){ return this.groupID; }
    /**
     * @param user The user object to be added in the list of players.
     * */
    public void addPlayer(User user){
        if (!this.players.contains(user)) { this.players.add(user); }
    }
    /**
     * @return total player in the group list.
     * */
    public int getTotalPlayers() { return this.players.size(); }
    /**
     * @param user the user object to be checked if exists in the list of players.
     * @return true or false base on result.
     * */
    public boolean isPlayer(User user) { return this.players.contains(user); }
    /**
     * @param user the user object to be removed from the list of players.
     * */
    public void removePlayer(User user) { this.players.remove(user); }
    /**
     * remove all players from the list of players.
     * */
    public void removeAllPlayers() { this.players.removeAll(this.players); }

    @Override
    public String toString() {
        return "Group{" +
                "ID = " + this.getGroupID() +
                ",No. of players=" + this.getTotalPlayers() +
                '}';
    }
}
