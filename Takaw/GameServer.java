/*
  UI Server
*/

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameServer implements Constants {
  int playerCount = 0;
  DatagramSocket serverSocket = null;
  GameState game;
  int gameStage = WAITING_FOR_PLAYERS;
  int numPlayers;
  String playerData;

  public GameServer(int numPlayers) {
    this.numPlayers = numPlayers;
    try {
      serverSocket = new DatagramSocket(PORT);
      serverSocket.setSoTimeout(100);
    } catch (IOException e) {
      System.err.println("Could not listen on port: " + PORT);
      System.exit(-1);
    } catch (Exception e){}

    game = new GameState();

    System.out.println("Game created... ");

    // Thread that listens for packets and handles game information
    Thread t = new Thread() {
      public void run() {
        while(true) {
          byte[] buf = new byte[256];
    			DatagramPacket packet = new DatagramPacket(buf, buf.length);

          // Constantly receive packets
          try{
         			serverSocket.receive(packet);
    			} catch(Exception ioe){}

          playerData = new String(buf);
          playerData = playerData.trim();

          // Checks if player has sent data
          if(!playerData.equals("")){
            System.out.println(playerData);
            // playerData information read depending on state of game
            switch(gameStage){
              case WAITING_FOR_PLAYERS:
                if (playerData.startsWith("CONNECT")){
                  String[] tokens = playerData.split(" ");
                  NetPlayer player = new NetPlayer(tokens[1],packet.getAddress(),packet.getPort());
                  System.out.println("Player connected: "+tokens[1]);
                  game.addPlayer(tokens[1].trim(),player);
                  System.out.println("MAY MAKIKICONNECT");
                  broadcast("CONNECTED "+tokens[1]);
                  playerCount++;
                  if (playerCount==numPlayers){
                    System.out.println(playerCount);
                    gameStage=GAME_START;
                  }
                }
                break;

              case GAME_START:
                System.out.println("Game State: START");
                broadcast("START");
                gameStage=IN_PROGRESS;
                break;

              case IN_PROGRESS:
                if (playerData.startsWith("PLAYER")){
                  //Tokenize: PLAYER <player name> <x> <y>
                  String[] playerInfo = playerData.split(" ");
                  String pname =playerInfo[1];
                  int x = Integer.parseInt(playerInfo[2].trim());
                  int y = Integer.parseInt(playerInfo[3].trim());
                  //Get the player from the game state
                  NetPlayer player=(NetPlayer)game.getPlayers().get(pname);
                  player.setX(x);
                  player.setY(y);
                  //Update the game state
                  game.addPlayer(pname, player);
                  //Send to all the updated game state
                  broadcast(game.toString());
                }
                break;
            }
          }
        }
      }
    };
    t.start();
  }

  public void broadcast(String msg){
		for(Iterator ite=game.getPlayers().keySet().iterator();ite.hasNext();){
			String name=(String)ite.next();
			NetPlayer player=(NetPlayer)game.getPlayers().get(name);
			send(player,msg);
		}
	}

  public void send(NetPlayer player, String msg){
		DatagramPacket packet;
		byte buf[] = msg.getBytes();
		packet = new DatagramPacket(buf, buf.length, player.getAddress(),player.getPort());
		try{
			serverSocket.send(packet);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

  public static void main(String args[]) {
    if (args.length != 1) {
      System.out.println("Usage: java GameServer <number of players>");
      System.exit(1);
    }

    new GameServer(Integer.parseInt(args[0]));
  }
}
