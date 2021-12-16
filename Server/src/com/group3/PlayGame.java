package com.group3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

//import static com.group3.ServerWorker.*;
public class PlayGame {
    /**
     * The PlayGame Starts The Game Called **** RIDDLARK ****
     * */
    private static ArrayList<Group> groupList = new ArrayList<>();
    static User userName;
    static Group lobbyGroup = new Group();
    static ArrayList<User> isReadyPlayerList = new ArrayList<>();
    public synchronized Boolean playGameHandler(OutputStream outputStream, InputStream inputStream, BufferedReader reader, User user) throws IOException {


        lobbyGroup.addPlayer(user);
//        groupList.add(group);
/*        for (ServerWorker worker : workerList) {
            if (worker.getIsReadyState() != null)

        }*/
        System.out.println("Lobby group created : " + lobbyGroup.getGroupID());
//        user.setGroupID(group.getGroupID());
        String serverReply ="Server Reply>> Hello " + user.getName() + "! Welcome to Riddlark.";
        outputStream.write((serverReply + "\n").getBytes());
        String line = reader.readLine();

        if(line.equalsIgnoreCase("y")) {
            user.setUserReply(line);
            user.setIsReadyState(true);
            System.out.println(
                    "Client User : " + user.getName() +
                            "User Ready to Play : " + user.getIsReadyState() +

                            "Reply >> " + user.getUserReply()
            );
            String waitMsg ="Server Reply>>  No. of ready players in lobby are "+ lobbyGroup.getTotalReadyPlayer().size() +
                    "/" + lobbyGroup.getTotalPlayers() + ". Please wait for other players to join.";

            lobbyGroup.runGroupMsgThread(outputStream,waitMsg);
        }
        try {
            splitLobbyPlayersInGroup(outputStream);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        readyPlayerListener(user, group, reader, outputStream);









//        if (groupList.isEmpty()) {
//            Group group = new Group();
//            group.addPlayer(user);
//            groupList.add(group);
//            System.out.println("Empty GroupList, New Group Created : " + group.getGroupID());
//            user.setGroupID(group.getGroupID());
//            String serverReply ="Server Reply>> "+ user.getName() + " added to Group with ID: " + user.getGroupID();
//            outputStream.write((serverReply + "\n").getBytes());
//            readyPlayerListener(user, group, reader, outputStream);
//            return true;
//        } else {
//            Iterator<Group> iter = groupList.iterator();
//            try {
//                while (iter.hasNext()) {
//                    Group prevGroup = iter.next();
//                    if (!prevGroup.isPlayer(user) && prevGroup.getTotalPlayers() < 6) {
//                        prevGroup.addPlayer(user);
//                        System.out.println("Previous Group : " + prevGroup.toString());
//                        user.setGroupID(prevGroup.getGroupID());
//                        String serverReply ="Server Reply>> "+ user.getName() + " added to Group with ID: " + user.getGroupID();
//                        outputStream.write((serverReply + "\n").getBytes());
//                        readyPlayerListener(user, prevGroup, reader, outputStream);
//                        return true;
//                    } else if (!iter.hasNext()) {
//                        Group newGroup = new Group();
//                        newGroup.addPlayer(user);
//                        groupList.add(newGroup);
//                        System.out.println("New Group Created : " + newGroup.toString());
//                        user.setGroupID(newGroup.getGroupID());
//                        String serverReply ="Server Reply>> "+ user.getName() + " added to Group with ID: " + user.getGroupID();
//                        outputStream.write((serverReply + "\n").getBytes());
//                        readyPlayerListener(user, newGroup, reader, outputStream);
//                        return true;
//                    }
//                }
//                return false;
//            } catch (Exception e) {
//                System.out.println("Error in PlayGame class. \nMessage: " + e.getMessage() + "\n Stacktrace: " + e.getLocalizedMessage());
//                e.printStackTrace();
//            }
//        }
        return false;
    }

    private static void splitLobbyPlayersInGroup(OutputStream outputStream) throws InterruptedException {
        /**
         * if total ready players = 2 , 3 and 4 : Create 1 group
         * if total ready players = 5 : Create 1 groups for 4 players and
         *                              tell last one ready player to join game again
         * if total ready players = 6 : Create 2 groups of 3 players each
         * if total ready players = 7 : Create 2 groups of 3 and 2 players
         * if total ready players = 8 : Create 2 groups of 4 players each
         * */

            Thread.sleep(30000);
            isReadyPlayerList = lobbyGroup.getTotalReadyPlayer();
            if (isReadyPlayerList.size() == 2 || isReadyPlayerList.size() == 3 || isReadyPlayerList.size() == 4){
                Group group1 = new Group();
                for (User readyPlayer: isReadyPlayerList){
                    group1.addPlayer(readyPlayer);
                    lobbyGroup.removePlayer(readyPlayer);
                }
                String waitMsg ="Server Reply>> You belong to group with id" + group1.getGroupID() +
                        ". No of players in group are " + group1.getTotalPlayers();

                group1.runGroupMsgThread(outputStream,waitMsg);
            } else if (isReadyPlayerList.size() == 5) {
//            Group Group1 = new Group(); // of 4 players
            } else if (isReadyPlayerList.size() > 5 ) {
//            Group Group1 = new Group();
//            Group Group2 = new Group();
            }

    }


    private static void readyPlayerListener(User user ,Group group, BufferedReader reader, OutputStream outputStream) throws IOException {
        String line = reader.readLine();
        if(line.equalsIgnoreCase("y")){
            user.setUserReply(line);
            user.setIsReadyState(true);
            System.out.println(
                    "\rClient User : " + user.getName() +
                            "\rGroup ID : " + user.getGroupID() +

                            "\rReply >> " + user.getUserReply()
            );
            runMgsThread(group, user, outputStream);


        }
    }
    private static void runMgsThread(Group group, User user, OutputStream outputStream){
        new Thread(() -> {
            try {
                int i = 1;
                while (i <= 5) {
                    if(group.getIsPlayingState()){break;}
                    String waitMsg ="Server Reply>> Your Group ID: "+ group.getGroupID() +
                            ". No. of ready players in group are "+ group.getTotalReadyPlayer() +
                            "/" + group.getTotalPlayers() + ". Game starts in T - " + i;
                    outputStream.write((waitMsg + "\n").getBytes());

                    Thread.sleep(5000);
                    i++;
                }
                if(group.getTotalPlayers() < 1 && group.getTotalReadyPlayer().size() > 1){
                    group.setIsPlayingState(true);
                    System.out.println("Starting game for current group "+ group.getGroupID() +"!");

                } else {
                    group.removePlayer(user);
                    /*
                     *  'E' is for termination
                     * */
                    String serverReply ="E Server Reply>>" + user.getName() + " remove from group with ID: " + user.getGroupID();
                    outputStream.write((serverReply + "\n").getBytes());
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private static Group getGroupById(String groupID){
        for (Group group : groupList){
            if (group.getGroupID().equals(groupID)) { return group; }
        }
        return null;
    }

}