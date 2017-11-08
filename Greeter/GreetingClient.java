import java.net.*;
import java.io.*;
import java.util.Scanner;

public class GreetingClient{
    public static void main(String [] args){
        boolean connected = true;
            try{
                String serverName = args[0]; //get IP address of server from first param
                int port = Integer.parseInt(args[1]); //get port from second param

                /* Open a ClientSocket and connect to ServerSocket */
                System.out.println("Connecting to " + serverName + " on port " + port);
                
    			//creating a new socket for client and binding it to a port
                Socket server = new Socket(serverName, port);

                System.out.println("Just connected to " + server.getRemoteSocketAddress());
                
                Thread sender = new Thread() {
                    public void run() {
                        Scanner scanIn = new Scanner(System.in);
                        try{
                            while(connected) {            
                                /* Send data to the ServerSocket */
                                OutputStream outToServer = server.getOutputStream();
                                DataOutputStream out = new DataOutputStream(outToServer);

                                String msg;

                                msg = scanIn.next();

                                out.writeUTF("From client " + server.getLocalSocketAddress()+" says: " + msg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        scanIn.close();
                    }
                };

                Thread receiver = new Thread() {
                    public void run() {
                        try {
                            System.out.println("Will be receiving messages from server");
                            while (connected) {
                                InputStream inFromServer = server.getInputStream();
                                DataInputStream in = new DataInputStream(inFromServer);
                                System.out.println(in.readUTF());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                sender.start();
                receiver.start();

    			//closing the socket of the client
                //server.close();
            }catch(IOException e){
                e.printStackTrace();
                System.out.println("Cannot find (or disconnected from) Server");
            }catch(ArrayIndexOutOfBoundsException e){
                System.out.println("Usage: java GreetingClient <server ip> <port no.>");
            }
    }
}
