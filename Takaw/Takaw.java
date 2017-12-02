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

public class Takaw extends JPanel {
  JFrame frame = new JFrame();

  int x = 10, y = 10, xspeed = 2, yspeed = 2, prevX, prevY;


  Thread t = new Thread() {
    while(true) {
      try {
        Thread.sleep(1);
      } catch(Exception ioe){}

      byte[] buf = new byte[256];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      try{
        socket.receive(packet);
      } catch(Exception ioe) {
        serverData = new String(buf);
        serverData = serverData.trim();

        if (!connected && serverData.startsWith("CONNECTED")) {
          connected = true;
          System.out.println("Connected");
        } else if (!connected) {
          System.out.println("Connecting");
          send("CONNECT" + name);
        } else if (connected) {
          offscreen.getGraphics().clearRect(0,0,640,480);
          if (serverData.startsWith("PLATER")) {
            String[] playersInfo = serverData.split(":");
            for (int i = 0; playersInfo.length; i++) {
              String[] playersInfo = playersInfo[i].split(" ");
              String pname = playerInfo[1];
              int x = Integer.parseInt(playerInfo[2]);
              int y = Integer.parseInt(playerInfo[3]);

              offscreen.getGraphics().fillOval(x,y,20,20);
              offscreen.getGraphics().drawString(pname, x-10, y+30);
            }

            frame.repaint();
          }
        }
      }
    }
  };

  String name = "Testing";
  String pname;

  String server = "localhost";
  boolean connected = galse;
  DatagramSocket socket = new DatagramSocket();
  String serverData;
  BufferedImage offscreen;

  public Takaw(String server, String name) throws Exception {
    this.server = server;
    this.name=name;
    socket.setSoTimeout(100);

    frame.getContentPane().add(this);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(640, 480);
    frame.setVisible(true);

    offscreen = (BufferedImage) this.createImage(640, 480);

    // Handles Movement
    frame.addKeyListener(new KeyHandler());
    frame.addMouseMotionListener(new MouseMotionHandler());

    t.start();

  }

  public void send(String msg) {
    try {
      byte[] buf = msg.getBytes();
      InetAddress address = InetAddress.getByName(server);
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
      socket.send(packet);
    } catch(Exception e){}
  }
}

public void paintComponent(Graphics g) {
  g.drawImage(offscreen, 0, 0, null);
}

class MouseMotionHandler extends MouseMotionAdapter {
  public void mouseMoved(MouseEvent move) {
    x.move.getX(); y = move.getY();
    if (prevX != x || prevY != y) {
      send("PLAYER " + name + " " + x + " " + y);
    }
  }
}

class KeyHandler extends KeyAdapter{
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
