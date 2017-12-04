/*
  UI Server
*/

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class GameServer implements Constants {
  int playerCount = 0;
  DatagramSocket serverSocket = null;
  GameState game;
  int gameStage = WAITING_FOR_PLAYERS;
  int numPlayers;
  String playerData;
  long startTime;
  long estimatedTime;
  boolean endGame = false;
//  ChatServer chatServer;

  public GameServer(int numPlayers) {
    this.numPlayers = numPlayers;
    try {
      serverSocket = new DatagramSocket(PORT);
      serverSocket.setSoTimeout(100);
    } catch (IOException e) {
      System.err.println("Could not listen on port: " + PORT);
      System.exit(-1);
    } catch (Exception e){}

      try{
        new ChatServer(3000);

        } catch(IOException e){
            //e.printStackTrace();
            System.out.println("Usage: java GreetingServer <port no.>\n"+
                    "Make sure to use valid ports (greater than 1023)");
        }catch(ArrayIndexOutOfBoundsException e){
            //e.printStackTrace();
            System.out.println("Usage: java GreetingServer <port no.>\n"+
                    "Insufficient arguments given.");
        }
    game = new GameState();

    System.out.println("Game created... ");

    // Generates good and bad food in the game state
    Thread food = new Thread() {
      public void run(){
        while(!endGame) {
          try {
            Thread.sleep(3000);
          } catch(Exception ioe){}

          NetFood goodFood = new NetFood(GOOD);
          NetFood badFood = new NetFood(BAD);

          game.generateFood(goodFood.getId(), goodFood);
          game.generateFood(badFood.getId(), badFood);

          broadcast(goodFood.toString());
          broadcast(badFood.toString());
        }
      }
    };

    Thread collisionDetection = new Thread(){
      public void run(){
        while(!endGame) {
          try{
            Thread.sleep(1000);
          } catch(Exception ioe){}

          Iterator itPlayer = game.getPlayers().entrySet().iterator();
          while (itPlayer.hasNext()) {
            Map.Entry pairPlayer = (Map.Entry)itPlayer.next();
            NetPlayer player = (NetPlayer) pairPlayer.getValue();
            Iterator itFood = game.getFood().entrySet().iterator();
            while(itFood.hasNext()) {
              Map.Entry pairFood = (Map.Entry)itFood.next();
              NetFood orb = (NetFood) pairFood.getValue();
              System.out.println(player.getX() + player.getY() + orb.getX() + orb.getY());
              if (checkCollision(player.getX(), player.getY(), orb.getX(), orb.getY())){
                broadcast("FOODEATEN " + orb.getId());
                System.out.println("FOOD HAS BEEN EATEN");
              }
            }
          }
        }
      }
    };

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
//             playerData information read depending on state of game
            switch(gameStage){
              case WAITING_FOR_PLAYERS:
                if (playerData.startsWith("CONNECT")){
                  String[] tokens = playerData.split(" ");
                  // Constructs player details
                  NetPlayer player = new NetPlayer(tokens[1], tokens[2], packet.getAddress(),packet.getPort());
                  System.out.println("Player connected: "+ player.getID());
                  game.addPlayer(player.getID(), player);
                  broadcast("CONNECTED "+tokens[1]);
                  playerCount++;
                  if (playerCount==numPlayers){
                    System.out.println(playerCount);
                    gameStage=GAME_START;
                  }
                }
                break;

              case GAME_START:
                // Sends player information to clients
                broadcast(game.playerData());
                System.out.println("Game State: START");
                broadcast("START");
                startTime = System.currentTimeMillis();
                food.start();
                collisionDetection.start();
                gameStage=IN_PROGRESS;
                break;

              case IN_PROGRESS:
                // Stops game after 3 minutes
                estimatedTime = System.currentTimeMillis() - startTime;
                if (estimatedTime > 180000) {
                  broadcast("ENDGAME");
                  endGame = true;
                  break;
                }
                if (playerData.startsWith("PLAYER")){
                  //Tokenize: PLAYER <player name> <x> <y>
                  String[] playerInfo = playerData.split(" ");
                  String id = playerInfo[1];
                  int x = Integer.parseInt(playerInfo[2].trim());
                  int y = Integer.parseInt(playerInfo[3].trim());
                  //Get the player from the game state
                  NetPlayer player=(NetPlayer) game.getPlayers().get(id);
                  player.setX(x);
                  player.setY(y);

                  //Send to all the updated game state
                  broadcast(player.toString());
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
			String id=(String)ite.next();
			NetPlayer player=(NetPlayer)game.getPlayers().get(id);
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

	// Checks for collision
	public boolean checkCollision(int px, int py, int fx, int fy) {
      if (px == fx && py == fy){
          return true;
      }
      else return false;
    }


  public static void main(String args[]) {
    if (args.length != 1) {
      System.out.println("Usage: java GameServer <number of players>");
      System.exit(1);
    }

    new GameServer(Integer.parseInt(args[0]));
  }
}
