import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.SwingWorker.StateValue;

public class Chess extends JPanel implements MouseListener, PropertyChangeListener {
	/* PRIVATE */
	private Engine engine;
	private Piece selectedPiece;
	private ArrayList<ArrayList<Piece>> board;
	private ArrayList<Piece> allPieces;
	private int wPawnCap = 0;
	private int bPawnCap = 0;
	private int wPieceCap = 0;
	private int bPieceCap = 0;
	private int tileSize;

	/* FLAGS */
	private boolean turn = true;
	private boolean whiteIsComputer;
	private boolean gameEnded = false;
	private boolean gameStarted = false;
	private boolean thinking = false;

	/* INIT */
	Chess() {
		setLayout (null);
		addMouseListener (this);
	}
	public void loaded(boolean playWhite, final int tileSize) {
		this.tileSize = tileSize;
		for (int x = 0; x < Data.printX.length; x++) {
			if (x == 0)
				Data.printX[x] = 0;
			else
				Data.printX[x] = tileSize * (x - 1);
		}
		for (int x = 0; x < Data.printY.length; x++) {
			if (x == 0)
				Data.printY[x] = 0;
			else
				Data.printY[x] = tileSize * (x - 1);
		}

		whiteIsComputer = !playWhite;
		initBoard ();
		gameStarted = true;
		repaint ();

		if (whiteIsComputer)
			goTurn (true);
	}
	void initBoard() {
		board = new ArrayList<ArrayList<Piece>> ();
		allPieces = new ArrayList<Piece> ();

		for (int x = 0; x <= 8; x++) {
			board.add (new ArrayList<Piece> ());
			for (int y = 0; y <= 8; y++)
				board.get (x).add (null);
		}

		for (int i = 0; i < 2; i++) {
			boolean isWhite = (i == 0);
			boolean isComputer = isWhite == whiteIsComputer;
			for (int x = 0; x < 8; x++)
				allPieces.add (new Piece (Data.PAWN, x + 1, (isComputer ? 2 : 7), isWhite, isComputer, tileSize == 100));
			allPieces.add (new Piece (Data.ROOK, 1, (isComputer ? 1 : 8), isWhite, isComputer, tileSize == 100));
			allPieces.add (new Piece (Data.ROOK, 8, (isComputer ? 1 : 8), isWhite, isComputer, tileSize == 100));
			allPieces.add (new Piece (Data.KNIGHT, 2, (isComputer ? 1 : 8), isWhite, isComputer, tileSize == 100));
			allPieces.add (new Piece (Data.KNIGHT, 7, (isComputer ? 1 : 8), isWhite, isComputer, tileSize == 100));
			allPieces.add (new Piece (Data.BISHOP, 3, (isComputer ? 1 : 8), isWhite, isComputer, tileSize == 100));
			allPieces.add (new Piece (Data.BISHOP, 6, (isComputer ? 1 : 8), isWhite, isComputer, tileSize == 100));
			allPieces.add (new Piece (Data.QUEEN, (whiteIsComputer ? 5 : 4), (isComputer ? 1 : 8), isWhite, isComputer, tileSize == 100));
			allPieces.add (new Piece (Data.KING, (whiteIsComputer ? 4 : 5), (isComputer ? 1 : 8), isWhite, isComputer, tileSize == 100));
		}

		updateBoard ();
	}

	/* METHODS */
	public void updateBoard() {
		int i, j;
		for (i = 0; i <= 8; i++) {
			for (j = 0; j <= 8; j++)
				board.get (i).set (j, null);
		}
		for (Piece p : allPieces) {
			if (p.isPlaying ())
				board.get (p.getX ()).set (p.getY (), p);
		}
	}
	private void nextTurn() {
		if (gameEnded)
			return;
		
		turn = !turn;

		if (turn == whiteIsComputer)
			goTurn (turn);
		selectedPiece = null;

		repaint ();
		checkMate ();
	}
	private void goTurn(boolean isWhite) {
		if (!thinking) {
			engine = new Engine (this, whiteIsComputer == isWhite);
			engine.addPropertyChangeListener (this);
			engine.execute ();
			thinking = true;
			Info.setStatus ("thinking.");
		}
	}
	private void checkMate() {
		/*Piece kingW = getPiece (true, Piece.KING);
		Piece kingB = getPiece (false, Piece.KING);*/

		/*ArrayList<Piece> piecesW = new ArrayList<Piece> ();
		ArrayList<Piece> piecesB = new ArrayList<Piece> ();
		for (Piece p : getAllPieces ()) {
			if (p.isWhite () && p.isPlaying ())
				piecesW.add (p);
			else if (!p.isWhite () && p.isPlaying ())
				piecesB.add (p);
		}
		boolean kingAttackedW = false;
		boolean kingAttackedB = false;
		for (Piece p : piecesW) {
			if (isAttacking (p, kingB)) {
				kingAttackedB = true;
				break;
			}
		}
		for (Piece p : piecesB) {
			if (isAttacking (p, kingW)) {
				kingAttackedW = true;
				break;
			}
		}
		if (kingW == null || kingB == null)
			new Error (this, 1);
		if (kingW.getValidMoves (board).size () == 0 && kingAttackedW) {
			gameEnded = true;
			JOptionPane.showMessageDialog (null, "Black won.");
		} else if (kingB.getValidMoves (board).size () == 0 && kingAttackedB) {
			gameEnded = true;
			JOptionPane.showMessageDialog (null, "White won.");
		}*/
	}
	private void handleMouse(Point point) {
		if (gameStarted && !thinking) {
			if (turn != whiteIsComputer) {
				int x = getGridX (point.x);
				int y = getGridY (point.y);
				if (x < 1 || x > 8 || y < 1 || y > 8)
					return;

				Piece p = getPieceLocation (x, y);
				if (p != null && p.isWhite () == turn) {
					if (selectedPiece == null) {
						selectedPiece = p;
						selectedPiece.selected (true);
					} else {
						if (selectedPiece.equals (p)) {
							selectedPiece.selected (false);
							selectedPiece = null;
						} else {
							selectedPiece.selected (false);
							selectedPiece = p;
							selectedPiece.selected (true);
						}
					}
				} else {
					if (selectedPiece != null) {
						Move m = new Move (selectedPiece, x, y);
						if (Move.isValid (board, m)) {
							boolean capture = false;
							boolean castle = false;
							if (isPieceLocation (x, y)) {
								capture (m);
								capture = true;
							}
							if (selectedPiece.getType () == Data.KING) {
								if (m.getX () == selectedPiece.getX () + 2) {
									getPieceLocation (8, m.getY ()).to (selectedPiece.getX () + 1, selectedPiece.getY (), this);
									castle = true;
								}
								if (m.getX () == selectedPiece.getX () - 2) {
									getPieceLocation (1, m.getY ()).to (selectedPiece.getX () - 1, selectedPiece.getY (), this);
									castle = true;
								}
							}
							selectedPiece.to (x, y, this);

							Info.addMove (Data.getMove (whiteIsComputer, m, capture, castle));
							nextTurn ();
						}
					}
				}
				repaint ();
			}
		}
	}
	public void capture(Move m) {
		Piece capPiece = getPieceLocation (m.getX (), m.getY ());
		boolean isPawn = capPiece.getType () == Data.PAWN;
		if (capPiece.isWhite ()) {
			if (isPawn) {
				capPiece.captured (wPawnCap % 4, 1 + wPawnCap / 4);
				wPawnCap++;
			} else {
				capPiece.captured (wPieceCap % 4, 3 + wPieceCap / 4);
				wPieceCap++;
			}
		} else {
			if (isPawn) {
				capPiece.captured (bPawnCap % 4, 1 + bPawnCap / 4);
				bPawnCap++;
			} else {
				capPiece.captured (bPieceCap % 4, 3 + bPieceCap / 4);
				bPieceCap++;
			}
		}
	}
	private int getGridX(int x) {
		for (int a = 0; a < Data.printX.length; a++) {
			if (x <= Data.printX[a] && x >= Data.printX[1])
				return (a - 1);
		}
		return -1;
	}
	private int getGridY(int y) {
		for (int a = 0; a < Data.printY.length; a++) {
			if (y <= Data.printY[a] && y >= Data.printY[1])
				return (a - 1);
		}
		return -1;
	}
	public boolean isPieceLocation(int x, int y) {
		return Move.isPieceLocation (board, x, y);
	}
	public Piece getPieceLocation(int x, int y) {
		return Move.getPieceLocation (board, x, y);
	}
	public Piece getPiece(boolean isWhite, int type) {
		for (Piece p : allPieces) {
			if (p != null) {
				if (p.isPlaying ()) {
					if (p.getType () == type && p.isWhite ())
						return p;
				}
			}
		}
		return null;
	}
	public boolean isAttacking(Piece p, Piece q) {
		if (p.getX () == q.getX () && p.getY () == q.getY ())
			return false;
		if (p.isWhite () == q.isWhite ())
			return false;

		if (p.getType () == Data.PAWN) {
			if ((p.getX () == q.getX () - 1 || p.getX () == q.getX () + 1) && (p.isComputer ()) ? p.getY () == q.getY () - 1
					: p.getY () == q.getY () + 1)
				return true;
		}
		if (p.getType () == Data.ROOK || p.getType () == Data.QUEEN) {
			if (p.getX () == q.getX () || p.getY () == q.getY ()) {
				for (int x = 1; x < (p.getX () == q.getX () ? Math.abs (p.getY () - q.getY ()) : Math.abs (p.getX () - q.getX ())); x++) {
					if (isPieceLocation ((p.getX () == q.getX () ? p.getX () : (p.getY () < q.getY () ? p.getY () + x : q.getY () + x)),
							(p.getY () == q.getY () ? p.getY () : (p.getX () < q.getX () ? p.getX () + x : q.getX () + x))))
						return false;
				}
				if (p.getType () == Data.ROOK)
					return true;
			}
		}
		if (p.getType () == Data.KNIGHT) {
			if (Math.abs (p.getX () - q.getX ()) + Math.abs (p.getY () - q.getY ()) == 3 && Math.abs (p.getX () - q.getX ()) != 0
					&& Math.abs (p.getY () - q.getY ()) != 0)
				return true;
		}
		if (p.getType () == Data.BISHOP || p.getType () == Data.QUEEN) {
			if (Math.abs (p.getX () - q.getX ()) == Math.abs (p.getY () - q.getY ())) {
				for (int x = 1; x < Math.abs (p.getX () - q.getX ()); x++) {
					if (isPieceLocation (q.getX () + (p.getX () > q.getX () ? 1 : -1) * x, q.getY () + (p.getY () > q.getY () ? 1 : -1) * x))
						return false;
				}
				return true;
			}

		}
		if (p.getType () == Data.KING) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					if (p.getX () == q.getX () + x && p.getY () == q.getY () + y)
						return true;
				}
			}
		}
		return false;
	}
	public void printBoard() {
		int rowCount = 0;

		for (ArrayList<Piece> a : board) {
			if (rowCount != 0) {
				System.out.print (rowCount + "  ");
				boolean firstPrint = true;
				for (Piece p : a) {
					if (p != null) {
						if (p.isPlaying ())
							System.out.print (p.getSName () + "| ");
						else
							System.out.print (" | ");
					} else if (!firstPrint)
						System.out.print (" | ");
					firstPrint = false;
				}
				System.out.println ();
			} else
				System.out.println ("   1  2  3  4  5  6  7  8");
			rowCount++;
		}
		System.out.println ();
	}

	/* GETTER */
	public boolean whiteIsComputer() {
		return (whiteIsComputer);
	}
	public ArrayList<ArrayList<Piece>> getBoard() {
		return board;
	}

	/* IMPLEMENTED */
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		super.paintComponent (g);

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (!((x + y) % 2 == 0)) {
					g2.setColor (new Color (120, 120, 120));
					g2.fillRect (tileSize * x, tileSize * y, tileSize, tileSize);
				} else {
					g2.setColor (Color.WHITE);
					g2.fillRect (tileSize * x, tileSize * y, tileSize, tileSize);
				}
			}
		}

		g2.setColor (Color.BLACK);
		for (int x = 0; x <= 8; x++) {
			g2.drawLine (tileSize * x, 0, tileSize * x, tileSize * 8);
			g2.drawLine (0, tileSize * x, tileSize * 8, tileSize * x);
		}

		g2.setFont (new Font ("Verdana", Font.ITALIC, tileSize / 10 + 10));
		for (int x = 0; x < 8; x++) {
			g2.drawString (String.format ("%d", (x + 1)), tileSize * 8 + 15,
					(int) (tileSize * 7.5 - ((!whiteIsComputer ? x : 7 - x) * tileSize)));
			char c = (char) (x + 97);
			g2.drawString (String.format ("%c", c), tileSize / 2 + ((!whiteIsComputer ? x : 7 - x) * tileSize), tileSize * 8 + 25);
		}
		if (gameStarted) {
			for (Piece p : allPieces) {
				if (p.isPlaying ())
					g2.drawImage (
							p.getImage (),
							Data.printX[p.getX ()] + tileSize / 2 - p.getImage ().getWidth (null) / 2,
							Data.printY[p.getY ()] + tileSize / 2 - p.getImage ().getHeight (null) / 2, null);
			}
		}

		if (selectedPiece != null) {
			g2.setColor (Color.red);
			g2.setStroke (new BasicStroke ((float) (tileSize / 20), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
					(float) (tileSize / 20)));
			if (selectedPiece != null)
				g2.drawRect (Data.printX[selectedPiece.getX ()], Data.printY[selectedPiece.getY ()], tileSize, tileSize);

			int[][] validMoves = selectedPiece.getValidMoves (board);
			if (validMoves != null) {
				g2.setStroke (new BasicStroke (4));
				g2.setColor (Color.orange);

				for (int i = 0; i < validMoves.length; i++) {
					g2.drawOval (Data.printX[validMoves[i][0]] +
							selectedPiece.getImage ().getWidth (null) / 2, Data.printY[validMoves[i][1]] +
							selectedPiece.getImage ().getWidth (null) / 2, tileSize / 10, tileSize / 10);
				}
			}
		}
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		
		handleMouse (new Point (e.getX (), e.getY ()));
	}
	public void mouseReleased(MouseEvent e) {}
	public void propertyChange(PropertyChangeEvent e) {
		if (StateValue.DONE == engine.getState () && thinking) {
			try {
				Move bestMove = engine.get ();
				Info.setStatus ("done thinking.");
				Piece bestMovePiece;
				boolean capture = false, castle = false;

				if (bestMove != null) {
					bestMovePiece = bestMove.getPiece ();

					if (bestMovePiece != null) {
						if (!isPieceLocation (bestMovePiece.getX (), bestMovePiece.getY ()))
							new Error (this, 2);
					} else
						new Error (this, 8);

					bestMove = new Move (getPieceLocation (bestMovePiece.getX (), bestMovePiece.getY ()),
								bestMove.getX (), bestMove.getY ());

					if (!Move.isValid (board, bestMove))
						new Error (this, 6);
				} else
					new Error (this, 1);

				bestMovePiece = bestMove.getPiece ();
				if (isPieceLocation (bestMove.getX (), bestMove.getY ())) {
					capture (bestMove);
					capture = true;
				}
				if (bestMovePiece.getType () == Data.KING) {
					if (bestMove.getX () == bestMovePiece.getX () + 2) {
						getPieceLocation (8, bestMove.getY ()).to (bestMovePiece.getX () + 1, bestMovePiece.getY (), this);
						castle = true;
					}
					if (bestMove.getX () == bestMovePiece.getX () - 2) {
						getPieceLocation (1, bestMove.getY ()).to (bestMovePiece.getX () - 1, bestMovePiece.getY (), this);
						castle = true;
					}
				}
				bestMove.getPiece ().to (bestMove.getX (), bestMove.getY (), this);

				Info.addMove (Data.getMove (whiteIsComputer, bestMove, capture, castle));
				Info.setStatus ("waiting.");

				thinking = false;
				nextTurn ();
			} catch (InterruptedException ex) {
				ex.printStackTrace ();
			} catch (ExecutionException ex) {
				ex.printStackTrace ();
			}
		}
	}
}
