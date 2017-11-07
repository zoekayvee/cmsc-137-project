/*
 * GreetingClient.java
 * CMSC137 Sample Code for TCP Socket Programming
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class GreetingClient{
    
    public static String ign = "";

    public static void main(String [] args){
        boolean connected = true;
            try{
                String serverName = args[0]; //get IP address of server from first param
                int port = Integer.parseInt(args[1]); //get port from second param

                /* Open a ClientSocket and connect to ServerSocket */
                System.out.println("Connecting to " + serverName + " on port " + port);
                
    			//creating a new socket for client and binding it to a port
                Socket server = new Socket(serverName, port);

                System.out.println("Just connected to " + server.getRemoteSocketAddress() + "\n\n");
                
                Thread sender = new Thread() {
                    public void run() {
                        Scanner s = new Scanner(System.in);
                        Boolean initial = true;
                        try {
                            while(connected) {            
                                if (initial) {
                                    /* Send data to the ServerSocket */
                                    OutputStream outToServer = server.getOutputStream();
                                    DataOutputStream out = new DataOutputStream(outToServer);

                                    // scans name
                                    System.out.println("Enter your name: ");
                                    String name;
                                    name = s.nextLine();

                                    //out.writeUTF("Your name is: " + name);

                                    /* Receive data from the ServerSocket */
                                    InputStream inFromServer = server.getInputStream();
                                    DataInputStream in = new DataInputStream(inFromServer);
                                    //System.out.println("Server says " + in.readUTF());
                                
                                    ign = name; // sets name to ign
                                    initial = false; // change the flag 
                                }
                                else {
                                    /* Send data to the ServerSocket */
                                    OutputStream outToServer = server.getOutputStream();
                                    DataOutputStream out = new DataOutputStream(outToServer);

                                    // scans message
                                    System.out.print(ign + ": ");
                                    String msg;
                                    msg = s.nextLine();

                                    out.writeUTF(ign + ": " + msg);

                                    /* Receive data from the ServerSocket */
                                    InputStream inFromServer = server.getInputStream();
                                    DataInputStream in = new DataInputStream(inFromServer);
                                    //System.out.println("Server says " + in.readUTF());
                                }
                            }
                        s.close();
                        } 
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                Thread receiver = new Thread() {
                    public void run() {
                        try {
                            System.out.println("Will be receiving messages from server");
                            while (connected) {
                                InputStream inFromServer = server.getInputStream();
                                DataInputStream in = new DataInputStream(inFromServer);
                                System.out.println("Server says " + in.readUTF());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                sender.start();
                receiver.start();
            }catch(IOException e){
                e.printStackTrace();
                System.out.println("Cannot find (or disconnected from) Server");
            }catch(ArrayIndexOutOfBoundsException e){
                System.out.println("Usage: java GreetingClient <server ip> <port no.>");
            }
    }
}
