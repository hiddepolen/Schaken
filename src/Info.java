import java.awt.*;

import javax.swing.*;

public class Info extends JPanel {
	private static JTextArea textField;
	private static JTextArea moveField;
	private static JTextField statusField;
	private static boolean toMove = true;
	private static int moveCount = 1;

	Info(boolean big) {
		super (new FlowLayout (FlowLayout.LEADING, 0, 15));

		statusField = new JTextField (33);
		if (big) {
			textField = new JTextArea (20,33);
			moveField = new JTextArea (15,33);
		}
		else {
			textField = new JTextArea (8,33);
			moveField = new JTextArea (10,33);
		}
		textField.setLineWrap (true);
		textField.setBorder (BorderFactory.createLineBorder (new Color (200, 200, 200)));
		textField.setEditable (false);
		textField.setAutoscrolls (true);
		textField.setDragEnabled (true);
		textField.setFont (new Font ("Arial", Font.PLAIN, 16));

		moveField.setBorder (BorderFactory.createLineBorder (new Color (200, 200, 200)));
		moveField.setEditable (false);
		moveField.setAutoscrolls (true);
		moveField.setDragEnabled (true);
		moveField.setFont (new Font ("Arial", Font.PLAIN, 16));
		
		statusField.setBorder (BorderFactory.createLineBorder (new Color (200, 200, 200)));
		statusField.setEditable (false);
		statusField.setFont (new Font ("Arial", Font.PLAIN, 16));
		statusField.setBackground (Color.white);

		JScrollPane movePane = new JScrollPane(moveField);
		JScrollPane textPane = new JScrollPane(textField);
		movePane.setHorizontalScrollBarPolicy (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		textPane.setHorizontalScrollBarPolicy (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		add (movePane);
		add (statusField);
		add (textPane);
	}

	public static void addText(String add) {
		textField.setText (textField.getText () + "\n" + add);
	}
	public static void addMove (String add) {
		if (toMove) {
			moveField.setText (moveField.getText () + "\n"+ moveCount + ". " + add);
			toMove = !toMove;
		}
		else {
			moveField.setText (moveField.getText () + "\t" + add);
			moveCount++;
			toMove = !toMove;
		}
	}
	public static void setStatus (String s) {
		statusField.setText ("Computer is " + s);
	}
}
