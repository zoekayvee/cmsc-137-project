import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ChatServer {
    private ServerSocket serverSocket;
    private ArrayList<Socket> clients;

    public ChatServer(int port) throws IOException{
        //binding a socket to a port
        clients = new ArrayList<Socket>();
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000000);
        
        // Constantly listens for clients
        Thread acceptClient = new Thread() {
          public void run() {
            boolean connected = true;
            System.out.println("Accepting clients");
            while (connected) {
              try{
                Socket client = serverSocket.accept();
                clients.add(client);
                System.out.println(client.getRemoteSocketAddress() + " has just connected");
                
                // Listens if client sends a message
                Thread clientListener = new Thread() {
                  public void run() {
                    System.out.println("Receiving messages from" + client.getRemoteSocketAddress());
                    while (connected) {
                      try {
                        DataInputStream in = new DataInputStream(client.getInputStream());
                        String message = in.readUTF();
                        System.out.println(message); //readUTF waits for input}
                        messageClients(message);
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    }
                  }
                };

                clientListener.start();
              } catch (IOException e) {
                  e.printStackTrace();
              }
            }
          }
        };

        acceptClient.start();
    }

    public void messageClients(String message) {
      for(Socket receiver: clients) {
        try {
          DataOutputStream out = new DataOutputStream(receiver.getOutputStream());
          /* Send data to the ClientSocket */
          out.writeUTF(message);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    public static void main(String [] args){
      System.out.println("Starting server");
      try{
        int port = Integer.parseInt(args[0]);
        new ChatServer(port);
          
        } catch(IOException e){
            //e.printStackTrace();
            System.out.println("Usage: java GreetingServer <port no.>\n"+
                    "Make sure to use valid ports (greater than 1023)");
        }catch(ArrayIndexOutOfBoundsException e){
            //e.printStackTrace();
            System.out.println("Usage: java GreetingServer <port no.>\n"+
                    "Insufficient arguments given.");
        }
    }
}

// public class GreetingServer extends Thread{
//     private ServerSocket serverSocket;

//     public GreetingServer(int port) throws IOException{
//         //binding a socket to a port
//         serverSocket = new ServerSocket(port);
//         serverSocket.setSoTimeout(1000000);
//     }

//     public void run(){
//         boolean connected = true;
//         while(connected){
//             try{
//                 System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");

//                 /* Start accepting data from the ServerSocket */
//                 //waits or accepts connection from client
//                 Socket client = serverSocket.accept();

//                 System.out.println("Just connected to " + client.getRemoteSocketAddress());

//                 while (true) {
//                     /* Read data from the ClientSocket */
//                     DataInputStream in = new DataInputStream(client.getInputStream());
//                     String message = in.readUTF();
//                     System.out.println(message); //readUTF waits for input

//                     DataOutputStream out = new DataOutputStream(client.getOutputStream());
//                     /* Send data to the ClientSocket */
//                     out.writeUTF("From server: " + message);
//                 }
//             }catch(SocketTimeoutException s){
//                 System.out.println("Socket timed out!");
//                 break;
//             }catch(IOException e){
//                 e.printStackTrace();
//                 System.out.println("Input/Output Error!");
//                 //possible cause: client was disconnected while waiting for input
//                 break;
//             }
//         }
//     }