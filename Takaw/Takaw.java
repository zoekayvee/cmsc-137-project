/*
  Game screen proper
*/

import java.awt.Graphics;
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

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Takaw extends JPanel implements Constants {
  JFrame frame = new JFrame();

  int x = 10, y = 10, xspeed = 2, yspeed = 2, prevX, prevY;

  String name = "Testing";
  String pname;

  String server = "localhost";
  boolean connected = false;
  DatagramSocket socket = new DatagramSocket();
  String serverData;
  BufferedImage offscreen;

  public Takaw(String server, String name) throws Exception {
    this.server = server;
    this.name=name;

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
            System.out.println("Sending bitch");
            send("CONNECT " + name);
          }

          if (!serverData.equals("")) {
            System.out.println(serverData);
            if (!connected && serverData.startsWith("CONNECTED")) {
              connected = true;
              System.out.println("Connected");
            } else if (connected) {

            }
          }
        }
      }
    };

    t.start();

    Thread paint = new Thread() {
      public void run() {
        while(true){
          try {
            Thread.sleep(10);
          } catch(Exception ioe){}
          offscreen.getGraphics().clearRect(0, 0, 640, 480);
          offscreen.getGraphics().fillOval(x, y, 20, 20);
          offscreen.getGraphics().drawString("Zoe",x-10,y+30);
          frame.repaint();
        }
      }
    };

    paint.start();

  }

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

  class MouseMotionHandler extends MouseMotionAdapter {
    public void mouseMoved(MouseEvent move) {
      x = move.getX(); y = move.getY();
      if (prevX != x || prevY != y) {
        send("PLAYER " + name + " " + x + " " + y);
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

    new Takaw(args[0],args[1]);
  }
}
