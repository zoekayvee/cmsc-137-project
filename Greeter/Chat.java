import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Chat extends JFrame {

	JPanel jp = new JPanel();
	JLabel input = new JLabel(); // input from the user
	JLabel prompt = new JLabel("Enter your name: "); // prompt for entering name
	JButton enterButton = new JButton("ENTER"); // button for 'ENTER'
	JTextField nameField = new JTextField(30); // length of text field
	JTextField chatField = new JTextField(30); // length of text field
	String wholeMessages = ""; // for whole messages
	String br = "<br>";
	String openHtml = "<html>";
	String closeHtml = "</html>";

	public Chat() {
	
		// build
		jp.add(prompt); 
		jp.add(nameField);
		jp.add(enterButton);
		add(jp); 

		// set up
		setTitle("Test Chat");
		setVisible(true);
		setSize(400, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		/*
		*	KEY LISTENERS
		*/

		// entering name
		enterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText(); // scans name
				prompt.setText("Typing as " + name);
			
				// changes after clicking Enter button
				jp.remove(enterButton); // removes Enter button after clicking it 
				jp.remove(nameField);
				jp.repaint();

				jp.add(chatField); // adding a new field for messages
				JLabel msg = new JLabel();
				msg.setText("------------------------------------------------Messages------------------------------------------------");
				jp.add(msg);
				jp.add(input);
			}
		});

		// getting message from user and displaying it
		chatField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String message = chatField.getText(); // asks a message
				
				wholeMessages = wholeMessages + message; // prev message + new message

				chatField.setText(""); // clearing after entering a message
				
				wholeMessages = wholeMessages + br; // adds <br> for output

				String temp = openHtml + wholeMessages; // adds the htmls for output

				input.setText(temp); // displays the message in the UI

			}
		});
	}

	/*public static void main(String args[]) {
		Chat c = new Chat();
	}*/
}

// References: https://www.youtube.com/watch?v=8W5nioLg-Yk