import java.net.*;
import java.io.*;
import java.util.Scanner;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChatClient {

    public static String ign = ""; // will be 'in game name'
    public static JTextField chatField = new JTextField(10); // length of text field
    public static JLabel input = new JLabel(); // input from the user
    public static String wholeMessages = ""; // for whole messages
    public static String br = "<br>";
    public static String openHtml = "<html>";
    public static String closeHtml = "</html>";
    public static String messageFromClient = "";
    public static String messageFromServer = "";
    public static String messageForClient; // from server
    public static String temp = "";
    public static JButton instructionsButton = new JButton("Instructions");
    public static JLabel newLine = new JLabel("<html>---<br></html>");

    public static boolean chatStart;
    public static boolean chatReceive = false;

    public ChatClient(String serverName, int port, String name){
        boolean connected = true;
        try{
//            String serverName = args[0];            //get IP address of server from first param
//            int port = Integer.parseInt(args[1]);   //get port from second param
//
//            /* Open a ClientSocket and connect to ServerSocket */
            System.out.println("Connecting to " + serverName + " on port " + port);

            //creating a new socket for client and binding it to a port
            Socket server = new Socket(serverName, port);

            System.out.println("Just connected to " + server.getRemoteSocketAddress() + "\n\n");

            // GUI here
            JFrame frame = new JFrame("Chat");
            JPanel whole = new JPanel();
            JLabel prompt1 = new JLabel("Enter your name: ");
            JTextField nameField = new JTextField(10);
            JButton enterButton = new JButton("ENTER");

            // size
            whole.setPreferredSize(new Dimension(300, 300));

            // adding all together
            whole.add(instructionsButton);
            whole.add(newLine);
            whole.add(prompt1);
            whole.add(nameField);
            whole.add(enterButton);

            frame.setContentPane(whole);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            // THREADS

            Thread sender = new Thread() {
                public void run() {
                    try {
                        while(connected) {

                            /* Send data to the ServerSocket */
                            OutputStream outToServer = server.getOutputStream();
                            DataOutputStream out = new DataOutputStream(outToServer);

                            String msg1;
                            msg1 = messageFromClient;

                            if (chatStart == true) { // will only send to server when client enters a chat
                                out.writeUTF(ign + ": " + msg1); // sends msg to server
                                chatStart = false;
                            }

                            /* Receive data from the ServerSocket */
                            InputStream inFromServer = server.getInputStream();
                            DataInputStream in = new DataInputStream(inFromServer);
                        }

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


                            try {
                                Thread.sleep(300); // for waiting the message from the server

                                messageFromServer = in.readUTF();
                                System.out.println(messageFromServer);
                                wholeMessages = wholeMessages + messageFromServer; // prev message + new message
                                wholeMessages = wholeMessages + br; // adds <br> for output
                                temp = openHtml + wholeMessages + closeHtml; // adds the htmls for output

                                input.setText(temp); // displays the message in the UI
                            }
                            catch (Exception f){
                                f.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            enterButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String name = nameField.getText(); // scans name
                    prompt1.setText("Typing as " + name);
                    nameField.setText(""); // clearing the text after typing
                    ign = name; // sets given name to ign after asking name

                    // removing some elements after
                    whole.remove(enterButton);
                    whole.remove(nameField);
                    whole.repaint();

                    // adds new elements
                    whole.add(chatField);
                    JLabel msg2 = new JLabel();
                    whole.add(msg2);
                    whole.add(input); // jlabel for displaying all chat
                }
            });

            // getting message from user and displaying it
            chatField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    String message = chatField.getText(); // asks a message
                    messageFromClient = message;
                    chatField.setText(""); // clears input field after entering a message
                    chatStart = true; // will allow the sender to send to the server


                }
            });

            instructionsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                 
                // GUI here
                JFrame frame2 = new JFrame("Game Instructions");
                JPanel whole2 = new JPanel();
                JLabel header = new JLabel("Game mechanics of TAKAW");

               
                String labelContent = "<html>RULES<BR>The game ends after 3 minutes.<BR>The player with the biggest size wins.<BR><BR><BR>HOW TO PLAY<BR>1.Use your mouse to move your<BR>circle avatar around the map<BR>2.Increase your size by 'eating'<BR>the food (smaller circles) by moving close<BR>and pass it<BR>3.If you have a bigger size compared<BR>to another player/s, try to eat them by<BR>treating them as food<BR>4.If you have a smaller size compared<BR>to another player/s, prevent being eaten<BR>by moving away from them</html>";

                JLabel actualInstructions = new JLabel(labelContent);


                // size
                whole2.setPreferredSize(new Dimension(500, 600));

                whole2.add(actualInstructions);

                frame2.setContentPane(whole2);
                frame2.pack();
                frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame2.setVisible(true);

                }
            });


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
