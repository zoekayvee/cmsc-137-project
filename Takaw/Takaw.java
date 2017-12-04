/*
  Client game screen proper
*/

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;

import static java.awt.Color.RED;

public class Takaw extends JPanel implements Constants {
  private HashMap players = new HashMap();
  private HashMap food = new HashMap();

  JFrame frame = new JFrame();

  int x = 10, y = 10, xspeed = 2, yspeed = 2, prevX, prevY;

  String name = "Testing";
  String pname;
  String id;

  String server = "localhost";
  boolean connected = false;
  DatagramSocket socket = new DatagramSocket();
  String serverData;
  BufferedImage offscreen;

  public Takaw(String server, String name) throws Exception {
    this.server = server;
    this.name=name;
    this.id = UUID.randomUUID().toString();


    frame.setTitle("Takaw");
    socket.setSoTimeout(100);

    frame.getContentPane().add(this);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(640, 480);
    frame.setVisible(true);

    offscreen = (BufferedImage) this.createImage(640, 480);

    // Handles Movement
    frame.addKeyListener(new KeyHandler());
    frame.addMouseMotionListener(new MouseMotionHandler());

    Thread t = new Thread() {
      public void run() {
        while(true) {
          byte[] buf = new byte[256];
          DatagramPacket packet = new DatagramPacket(buf, buf.length);
          try{
            socket.receive(packet);
          } catch(Exception ioe) {}
          serverData = new String(buf);
          serverData = serverData.trim();

          if (!connected) {
            send("CONNECT " + id + " " + name);
            connected = true;
          }

          if (!serverData.equals("")) {
            System.out.println("Receiving: " + serverData);
            if (!connected && serverData.startsWith("CONNECTED")) {
              connected = true;
              System.out.println("Connected");
            } else if (connected) {
              // Adds copy of server's player information to client
              if (serverData.startsWith("ADDPLAYER")){
                String[] playersInfo = serverData.split(":");
                for (int i=0;i<playersInfo.length;i++){
                  String[] playerInfo = playersInfo[i].split(" ");
                  String id = playerInfo[1];
                  String pname = playerInfo[2];
                  int x = Integer.parseInt(playerInfo[3]);
                  int y = Integer.parseInt(playerInfo[4]);
                  Matakaw matakawer = new Matakaw(id, pname, x, y);
                  addPlayer(id, matakawer);
                }
              }
              // Updates hashmap values if player moves
              if (serverData.startsWith("PLAYER")){
                String[] playerInfo = serverData.split(" ");
                String id = playerInfo[1];
                int x = Integer.parseInt(playerInfo[2]);
                int y = Integer.parseInt(playerInfo[3]);
                Matakaw player = (Matakaw) players.get(id);
                player.setX(x);
                player.setY(y);
                System.out.println(player.getX() + " " + player.getY());
              }
              if (serverData.startsWith("FOOD")) {
                String[] foodData = serverData.split(" ");
                String fid = foodData[1];
                int ftype = Integer.parseInt(foodData[2]);
                int x = Integer.parseInt(foodData[3]);
                int y = Integer.parseInt(foodData[4]);
                Food orb = new Food(fid, ftype, x, y);
                food.put(orb.getId(), orb);
              }
              if (serverData.startsWith("FOODEATEN")) {
                String[] foodEaten = serverData.split(" ");
                String fid = foodEaten[1];
                food.remove(foodEaten[1]);
              }
              if (serverData.startsWith("ENDGAME")) {
                System.out.println("End of Game");
                break;
              }
            }
          }
        }
      }
    };

    t.start();

    // Paints circles according to location stated in HashMap
    Thread paint = new Thread() {
      public void run() {

        while(true){
          try {
            Thread.sleep(10);
          } catch(Exception ioe){}

          Iterator itPlayer = players.entrySet().iterator();
          offscreen.getGraphics().clearRect(0, 0, 640, 480);
          while (itPlayer.hasNext()) {
            Map.Entry pair = (Map.Entry)itPlayer.next();
            Matakaw player = (Matakaw) pair.getValue();
            offscreen.getGraphics().fillOval(player.getX(), player.getY(), 20, 20);
            offscreen.getGraphics().drawString(player.getName(),player.getX()-10,player.getY()+30);
//            it.remove();
          }

          Iterator itFood = food.entrySet().iterator();
          while (itFood.hasNext()) {
            Map.Entry pair = (Map.Entry)itFood.next();
            Food orb = (Food) pair.getValue();
            if(orb.getType() == GOOD) {
              offscreen.getGraphics().setColor(Color.GREEN);
            } else {
              offscreen.getGraphics().setColor(Color.RED);
            }
            offscreen.getGraphics().fillOval(orb.getX(), orb.getY(), 10, 10);
          }

          frame.repaint();
        }
      }
    };

    paint.start();

  }

  // Sends message to server
  public void send(String msg) {
    try {
      System.out.println("Sending: " + msg);
      byte[] buf = msg.getBytes();
      InetAddress address = InetAddress.getByName(server);
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
      socket.send(packet);
    } catch(Exception e){}
  }

  public void paintComponent(Graphics g) {
    g.drawImage(offscreen, 0, 0, null);
  }

  public void addPlayer(String id, Matakaw player) {
    players.put(id, player);
  }

  // Moves according to mouse motion
  class MouseMotionHandler extends MouseMotionAdapter {
    public void mouseMoved(MouseEvent move) {
      x = move.getX(); y = move.getY();
      if (prevX != x || prevY != y) {
        send("PLAYER " + id + " " + x + " " + y);
      }
    }
  }

  class KeyHandler extends KeyAdapter {
    public void keyPressed(KeyEvent ke){
      prevX=x;prevY=y;
      switch (ke.getKeyCode()){
      case KeyEvent.VK_DOWN:y+=yspeed;break;
      case KeyEvent.VK_UP:y-=yspeed;break;
      case KeyEvent.VK_LEFT:x-=xspeed;break;
      case KeyEvent.VK_RIGHT:x+=xspeed;break;
      }
      if (prevX != x || prevY != y){
        send("PLAYER "+name+" "+x+" "+y);
      }
    }
  }

  public static void main(String args[]) throws Exception{
    if (args.length != 2){
      System.out.println("Usage: java Takaw <server> <player name>");
      System.exit(1);
    }

    new ChatClient(args[0], 3000, args[1]);

    new Takaw(args[0],args[1]);
  }
}
