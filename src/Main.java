import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class Main extends JFrame implements MouseListener, ActionListener {

	/* PRIVATE (JComponent) */
	private Chess chessPanel = new Chess ();
	private JFileChooser chooser = new JFileChooser ();
	private JFrame preGame = new JFrame ("Options");
	
	private JButton bNewGame = new JButton ("New Game");
	private JButton bLoadGame = new JButton ("Load Game");
	private JButton bSaveGame = new JButton ("Save Game");
	private JButton exitButton = new JButton ("Exit");
	private JButton bOK = new JButton ("Play");
	private JButton bExit = new JButton ("Exit");
	private JRadioButton whiteRB = new JRadioButton ("White");
	private JRadioButton blackRB = new JRadioButton ("Black");
	private JRadioButton bigRB = new JRadioButton ("Big");
	private JRadioButton smallRB = new JRadioButton ("Small");
	private JSlider slider = new JSlider (0, 100);

	/* PRIVATE */
	private Dimension size;
	private int tileSize;
	
	/* PUBLIC */
	public Info infoPanel;

	/* MAIN */
	public static void main(String[] args) {
		new Main ();
	}

	/* INIT */
	Main() {
		super ("ProSCo (ProfielWerkstuk SchaakComputer)");

		Dimension dim = Toolkit.getDefaultToolkit ().getScreenSize ();
		preGame.setBounds ((int) (dim.getWidth () / 2 - 225), (int) (dim.getHeight () / 2 - 150), 450, 300);
		preGame.setLayout (new BorderLayout ());

		// BUTTON PANEL //
		JPanel buttonPanel = new JPanel (new FlowLayout ());
		bOK.addActionListener (this);
		bExit.addActionListener (this);
		buttonPanel.add (bOK);
		buttonPanel.add (bExit);
		bOK.setToolTipText ("Start the game.");
		bExit.setToolTipText ("Exit the game.");

		// DIFPANEL /
		JPanel difPanel = new JPanel (new FlowLayout ());
		JLabel label1 = new JLabel ("Computer difficulty");
		slider.setMinimum (600);
		slider.setMaximum (2800);
		slider.setValue (1800);
		slider.setMajorTickSpacing (550);
		slider.setPaintTicks (true);
		slider.setSnapToTicks (true);
		slider.setPaintLabels (true);
		difPanel.add (label1);
		difPanel.add (slider);
		label1.setToolTipText ("Select the computer difficulty, shown in Elo Points.");
		slider.setToolTipText ("Select the computer difficulty, shown in Elo Points.");
		
		// COLOURPANEL //
		JPanel colourPanel = new JPanel (new FlowLayout ());
		JLabel label2 = new JLabel ("Play with:");
		ButtonGroup buttonGroup = new ButtonGroup ();
		buttonGroup.add (whiteRB);
		buttonGroup.add (blackRB);
		colourPanel.add (label2);
		colourPanel.add (whiteRB);
		colourPanel.add (blackRB);
		whiteRB.setToolTipText ("Select white as your colour to play with.");
		blackRB.setToolTipText ("Select black as your colour to play with.");
		
		// SIZEPANEL //
		JPanel sizePanel = new JPanel (new FlowLayout ());
		JLabel label3 = new JLabel ("Board size:");
		ButtonGroup buttonGroup2 = new ButtonGroup ();
		buttonGroup2.add (bigRB);
		buttonGroup2.add (smallRB);
		sizePanel.add (label3);
		sizePanel.add (bigRB);
		sizePanel.add (smallRB);
		bigRB.setToolTipText ("Select the size of the board you will play with. (Big: width = 100px, Small: width = 50px)");
		smallRB.setToolTipText ("Select the size of the board you will play with. (Big: width = 100px, Small: width = 50px)");
		
		// CONTENTPANEL //
		JPanel contentPanel = new JPanel (new FlowLayout (FlowLayout.CENTER, 50, 10));
		contentPanel.add (difPanel);
		contentPanel.add (colourPanel);
		contentPanel.add (sizePanel);

		preGame.add (contentPanel, BorderLayout.CENTER);
		preGame.add (buttonPanel, BorderLayout.SOUTH);

		preGame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		preGame.setVisible (true);
	}
	private void init(boolean playWhite, boolean big) {
		if (big) {
			size = new Dimension (1300,870);
			tileSize = 100;
		}
		else {
			size = new Dimension (900, 470);
			tileSize = 50;
		}
		setSize (size);
		setResizable (false);
		
		Dimension dim = Toolkit.getDefaultToolkit ().getScreenSize ();
		setLocation ((dim.width-getWidth())/2, (dim.height-getHeight())/2);

		chessPanel.addMouseListener (this);
		add (chessPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel (new FlowLayout ());
		infoPanel = new Info (tileSize == 100);

		bNewGame.addActionListener (this);
		bSaveGame.addActionListener (this);
		bLoadGame.addActionListener (this);
		exitButton.addActionListener (this);
		
		bNewGame.setToolTipText ("Start a new game, changes are not saved.");
		bSaveGame.setToolTipText ("Save a game to a game file.");
		bLoadGame.setToolTipText ("Load a game from a game file.");
		exitButton.setToolTipText ("Exit the game.");

		buttonPanel.add (bNewGame);
		buttonPanel.add (bSaveGame);
		buttonPanel.add (bLoadGame);
		buttonPanel.add (exitButton);

		JPanel buttonInfoPanel = new JPanel (new BorderLayout());
		buttonInfoPanel.setPreferredSize (new Dimension(450, (int) size.getHeight ()));
		
		buttonInfoPanel.add (infoPanel, BorderLayout.CENTER);
		JPanel parentButtonPanel = new JPanel(new FlowLayout());
		parentButtonPanel.add (buttonPanel);
		buttonInfoPanel.add (parentButtonPanel, BorderLayout.SOUTH);
		
		add(buttonInfoPanel, BorderLayout.EAST);

		setVisible (true);

		chessPanel.loaded (playWhite, tileSize);
	}

	/* INPLEMENTED */
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource () == exitButton)
			System.exit (EXIT_ON_CLOSE);
		else if (e.getSource () == bOK) {
			if (!(whiteRB.isSelected () || blackRB.isSelected ()))
				JOptionPane.showMessageDialog (preGame, "Pick a colour to play with.");
			else if (!(whiteRB.isSelected () || blackRB.isSelected ()))
				JOptionPane.showMessageDialog (preGame, "Pick a size for your board.");
			else {
				preGame.dispose ();
				init (whiteRB.isSelected (), bigRB.isSelected ());
			}
		} else if (e.getSource () == bExit)
			System.exit (EXIT_ON_CLOSE);
		else if (e.getSource () == bNewGame) {
			System.out.println ("New Game chosen");
		} else if (e.getSource () == bSaveGame) {
			chooser.setDialogTitle ("Save Game");
			chooser.setFileSelectionMode (JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed (true);
			chooser.setFileFilter (new ExtensionFileFilter ("Text", new String[] {"txt"}));

			if (chooser.showSaveDialog (this) == JFileChooser.APPROVE_OPTION) {
				System.out.println ("getCurrentDirectory(): " + chooser.getCurrentDirectory ());
				System.out.println ("getSelectedFile() : " + chooser.getSelectedFile ());
				// Save(chooser.getSelectedFile ());
			} else {
				System.out.println ("No Selection ");
			}
		} else if (e.getSource () == bLoadGame) {
			chooser.setDialogTitle ("Save Game");
			chooser.setFileSelectionMode (JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed (true);
			chooser.setFileFilter (new ExtensionFileFilter ("Text", new String[] {"txt"}));

			if (chooser.showSaveDialog (this) == JFileChooser.APPROVE_OPTION) {
				System.out.println ("getCurrentDirectory(): " + chooser.getCurrentDirectory ());
				System.out.println ("getSelectedFile() : " + chooser.getSelectedFile ());
				// Load(chooser.getSelectedFile ());
			} else {
				System.out.println ("No Selection ");
			}
		}
	}

	/* PRIVATE CLASS */
	class ExtensionFileFilter extends FileFilter {
		/* PRIVATE */
		private String description;
		private String extensions[];

		/* INIT */
		public ExtensionFileFilter(String description, String extension) {
			this (description, new String[] {extension });
		}
		public ExtensionFileFilter(String description, String extensions[]) {
			if (description == null)
				this.description = extensions[0];
			else
				this.description = description;

			this.extensions = (String[]) extensions.clone ();
			toLowerCase (this.extensions);
		}

		/* METHODS */
		private void toLowerCase(String[] toLower) {
			for (String s : toLower)
				s.toLowerCase ();
		}
		public boolean accept(File file) {
			if (file.isDirectory ())
				return true;
			else {
				String path = file.getAbsolutePath ().toLowerCase ();
				for (int i = 0, n = extensions.length; i < n; i++) {
					String extension = extensions[i];
					if ((path.endsWith (extension) && (path.charAt (path.length () - extension.length () - 1)) == '.')) {
						return true;
					}
				}
			}
			return false;
		}
		
		/* GETTER */
		public String getDescription() {
			return description;
		}
	}
}
