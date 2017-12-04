import java.net.*;
import java.io.*;
import java.util.Scanner;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChatClient {

    public static String ign = ""; // will be 'in game name'

    public static void main(String [] args){
        boolean connected = true;
            try{
                String serverName = args[0];            //get IP address of server from first param
                int port = Integer.parseInt(args[1]);   //get port from second param

                /* Open a ClientSocket and connect to ServerSocket */
                System.out.println("Connecting to " + serverName + " on port " + port);

    			//creating a new socket for client and binding it to a port
                Socket server = new Socket(serverName, port);

                System.out.println("Just connected to " + server.getRemoteSocketAddress() + "\n\n");

                Thread sender = new Thread() {
                    public void run() {
                        Scanner s = new Scanner(System.in);
                        Boolean initial = true; // flag for asking name
                        try {
                            while(connected) {
                                
                                if (initial) {
                                    /* Send data to the ServerSocket */
                                    OutputStream outToServer = server.getOutputStream();
                                    DataOutputStream out = new DataOutputStream(outToServer);

                                    // scans user's name
                                    System.out.println("Enter your name: ");
                                    String name;
                                    name = s.nextLine();

                                    /* Receive data from the ServerSocket */
                                    InputStream inFromServer = server.getInputStream();
                                    DataInputStream in = new DataInputStream(inFromServer);

                                    ign = name; // sets given name to ign
                                    initial = false; // change the flag
                                }
                                else {
                                    /* Send data to the ServerSocket */
                                    OutputStream outToServer = server.getOutputStream();
                                    DataOutputStream out = new DataOutputStream(outToServer);

                                    // scans user's message
                                    String msg;
                                    msg = s.nextLine();

                                    out.writeUTF(ign + ": " + msg); // sends msg to server

                                    /* Receive data from the ServerSocket */
                                    InputStream inFromServer = server.getInputStream();
                                    DataInputStream in = new DataInputStream(inFromServer);
                                }
                            }
                        s.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                Thread receiver = new Thread() { // client receiving all the messages from the server
                    public void run() {
                        try {
                            System.out.println("Will be receiving messages from server");
                            while (connected) {
                                InputStream inFromServer = server.getInputStream();
                                DataInputStream in = new DataInputStream(inFromServer);
                                System.out.println(in.readUTF()); // server sends msgs to client
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // GUI here
                JFrame frame = new JFrame("Chat");
                
                final JPanel upper = new JPanel();
                final JPanel lower = new JPanel();
                final JPanel whole = new JPanel();

                // for upper ui
                JLabel prompt1 = new JLabel("Enter your name: ");
                JTextField nameField = new JTextField(30);
                JButton enterButton = new JButton("ENTER");

                // for lower ui
                JLabel allChat = new JLabel(); // for all chat

                // sizes
                upper.setPreferredSize(new Dimension(600, 200));
                lower.setPreferredSize(new Dimension(600, 400));
                whole.setPreferredSize(new Dimension(600, 600));

                // adding all together
                upper.add(prompt1);
                upper.add(nameField);
                upper.add(enterButton);

                lower.add(allChat);

                whole.add(upper);
                whole.add(lower);

                frame.setContentPane(whole);
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                // start sending and receiving
                sender.start();
                receiver.start();
            }catch(IOException e){
                e.printStackTrace();
                System.out.println("Cannot find (or disconnected from) Server");
            }catch(ArrayIndexOutOfBoundsException e){
                System.out.println("Usage: java ChatClient <server ip> <port no.>");
            }
    }
}
