package com.group3;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
//import static com.group3.ServerWorker.*;
public class PlayGame {
    /**
     * The PlayGame Starts The Game Called **** RIDDLARK ****
     * */
    private static ArrayList<Group> groupList = new ArrayList<>();
    static User userName;

    public Boolean playGameHandler(OutputStream outputStream, InputStream inputStream, BufferedReader reader, User user) throws IOException {


        if (groupList.isEmpty()) {
            Group group = new Group();
            group.addPlayer(user);
            groupList.add(group);
            System.out.println("Empty GroupList, New Group Created : " + group.getGroupID());
            user.setGroupID(group.getGroupID());
            String serverReply ="Server Reply>> "+ user.getName() + " added to Group with ID: " + user.getGroupID();
            outputStream.write((serverReply + "\n").getBytes());
            readyPlayerListener(user, group, reader, outputStream);
            return true;
        } else {
            Iterator<Group> iter = groupList.iterator();
            try {
                while (iter.hasNext()) {
                    Group prevGroup = iter.next();
                    if (!prevGroup.isPlayer(user) && prevGroup.getTotalPlayers() < 4) {
                        prevGroup.addPlayer(user);
                        System.out.println("Previous Group : " + prevGroup.toString());
                        user.setGroupID(prevGroup.getGroupID());
                        String serverReply ="Server Reply>> "+ user.getName() + " added to Group with ID: " + user.getGroupID();
                        outputStream.write((serverReply + "\n").getBytes());
                        readyPlayerListener(user, prevGroup, reader, outputStream);
                        return true;
                    } else if (!iter.hasNext()) {
                        Group newGroup = new Group();
                        newGroup.addPlayer(user);
                        groupList.add(newGroup);
                        System.out.println("New Group Created : " + newGroup.toString());
                        user.setGroupID(newGroup.getGroupID());
                        String serverReply ="Server Reply>> "+ user.getName() + " added to Group with ID: " + user.getGroupID();
                        outputStream.write((serverReply + "\n").getBytes());
                        readyPlayerListener(user, newGroup, reader, outputStream);
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                System.out.println("Error in PlayGame class. \nMessage: " + e.getMessage() + "\n Stacktrace: " + e.getLocalizedMessage());
                e.printStackTrace();
            }


        }
        return false;
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
                if(group.getTotalPlayers() > 1 && group.getTotalReadyPlayer() > 1){
                    group.setIsPlayingState(true);
                    System.out.println("Starting game for current group "+group.getGroupID()+"!");
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