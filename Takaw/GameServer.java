/*
UI Server
*/

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameServer implements Runnable, Constants{
  int playerCount = 0;
  DatagramSocket serverSocket = null;
  GameState game;
  int gameStage = WAITING_FOR_PLAYERS;
  int numPlayers;

  Thread t = new Thread(this);

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
      t.start();
  }

  public static void main(String args[]) {
    if (args.length != 1) {
      System.out.println("Usage: java GameServer <number of players>");
      System.exit(1);
    }

    new GameServer(Integer.parseInt(args[0]));
  }
}
