import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Piece {
	/* PRIVATE */
	private int x, y;
	private int capX, capY;
	private String sName;
	private String lName;
	private int[][] moves;

	/* FLAGS */
	private boolean playing = true;
	private boolean moved = false;
	private boolean canCastle = false;
	private boolean selected = false;

	/* CONSTANTS */
	private final int TYPE;
	private final int VALUE;
	private final Image image;
	private final boolean ISWHITE;
	private final boolean ISCOMPUTER;

	/* INIT */
	Piece(int type, int x, int y, boolean w, boolean c, boolean big) {
		this.x = x;
		this.y = y;
		ISWHITE = w;
		ISCOMPUTER = c;
		TYPE = type;
		if (TYPE == Data.PAWN) {
			VALUE = 100;
			sName = (ISWHITE ? "P" : "p");
			lName = (ISWHITE ? "wPawn" : "bPawn");
		} else if (TYPE == Data.ROOK) {
			VALUE = 525;
			sName = (ISWHITE ? "R" : "r");
			lName = (ISWHITE ? "wRook" : "bRook");
		} else if (TYPE == Data.KNIGHT) {
			VALUE = 305;
			sName = (ISWHITE ? "N" : "n");
			lName = (ISWHITE ? "wKnight" : "bKnight");
		} else if (TYPE == Data.BISHOP) {
			VALUE = 333;
			sName = (ISWHITE ? "B" : "b");
			lName = (ISWHITE ? "wBishop" : "bBishop");
		} else if (TYPE == Data.QUEEN) {
			VALUE = 850;
			sName = (ISWHITE ? "Q" : "q");
			lName = (ISWHITE ? "wQueen" : "bQueen");
		} else if (TYPE == Data.KING) {
			VALUE = 32000;
			sName = (ISWHITE ? "K" : "k");
			lName = (ISWHITE ? "wKing" : "bKing");
		} else {
			VALUE = 0;
			new Error (this, 5);
		}

		if (TYPE == Data.KING)
			canCastle = true;

		String path = "images/" + String.format ((ISWHITE ? (big ? "w%db" : "w%ds") : (big ? "b%db" : "b%ds")), type) + ".png";
		image = new ImageIcon (getClass ().getResource (path)).getImage ();
	}
	Piece(Piece p) {
		this.x = p.x;
		this.y = p.y;
		this.TYPE = p.TYPE;
		this.VALUE = p.VALUE;
		this.image = p.image;
		this.ISWHITE = p.ISWHITE;
		this.ISCOMPUTER = p.ISCOMPUTER;

		this.sName = p.sName;
		this.lName = p.lName;

		this.playing = p.playing;
		this.moved = p.moved;
		this.canCastle = p.canCastle;
		this.selected = p.selected;
	}

	/* METHODS */
	public int[][] getValidMoves(ArrayList<ArrayList<Piece>> board) {
		final boolean isComputer = ISCOMPUTER;
		moves = new int[0][2];
		int[][] b = new int[9][9];
		int x0, y0;

		if (playing) {
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					Piece p = board.get (i).get (j);
					if (p == null)
						b[i][j] = 0;
					else
						b[i][j] = (p.isWhite () ? p.getValue () : -p.getValue ());
				}
			}

			switch (TYPE) {
				case Data.PAWN:
					for (int[] i : Data.p (x, y, isComputer)) {
						if (x == i[0]) {
							if (b[x][y + (isComputer ? 1 : -1) * 1] != 0)
								continue;

							if (i[1] == y + (isComputer ? 1 : -1) * 2) {
								if (b[i[0]][i[1]] != 0)
									continue;
							}
							addMove (b, i[0], i[1]);
						} else {
							if (b[i[0]][i[1]] * b[x][y] >= 0)
								continue;
							addMove (b, i[0], i[1]);
						}

					}
					break;
				case Data.KNIGHT:
					for (int[] i : Data.n (x, y, isComputer))
						addMove (b, i[0], i[1]);
					break;
				case Data.KING:
					for (int[] i : Data.k (x, y, isComputer)) {
						if (Math.abs (x - i[0]) <= 1 && Math.abs (y - i[1]) <= 1)
							addMove (b, i[0], i[1]);
						else {
							if (!moved)
								addMove (b, i[0], i[1]);
						}
					}
					break;
				case Data.QUEEN:
				case Data.BISHOP:
					y0 = y;
					x0 = x;
					while (++y0 <= 8 && ++x0 <= 8) {
						if (addMove (b, x0, y0))
							break;
					}

					y0 = y;
					x0 = x;
					while (--y0 >= 1 && ++x0 <= 8) {
						if (addMove (b, x0, y0))
							break;
					}

					y0 = y;
					x0 = x;
					while (++y0 <= 8 && --x0 >= 1) {
						if (addMove (b, x0, y0))
							break;
					}

					y0 = y;
					x0 = x;
					while (--y0 >= 1 && --x0 >= 1) {
						if (addMove (b, x0, y0))
							break;
					}

					if (TYPE == Data.BISHOP)
						break;
				case Data.ROOK:
					y0 = y;
					x0 = x;
					while (++y0 <= 8) {
						if (addMove (b, x0, y0))
							break;
					}

					y0 = y;
					x0 = x;
					while (--y0 >= 1) {
						if (addMove (b, x0, y0))
							break;
					}

					y0 = y;
					x0 = x;
					while (++x0 <= 8) {
						if (addMove (b, x0, y0))
							break;
					}

					y0 = y;
					x0 = x;
					while (--x0 >= 1) {
						if (addMove (b, x0, y0))
							break;
					}
					break;
			}
		}
		return moves;
	}
	private boolean addMove(int[][] b, int toX, int toY) {
		if (ISWHITE ? b[toX][toY] > 0 : b[toX][toY] < 0)
			return true;

		int len = 0;
		if (moves != null)
			len = moves.length;
		int[][] ret = new int[len + 1][2];
		if (moves != null) {
			for (int i = 0; i < moves.length; i++)
				ret[i] = moves[i];
		}
		ret[len][0] = toX;
		ret[len][1] = toY;

		moves = ret;

		if (b[toX][toY] != 0)
			return true;
		return false;
	}
	public int getCovering(ArrayList<ArrayList<Piece>> board) {
		boolean isComputer = ISCOMPUTER;
		int covering = 0;

		if (playing) {
			if (TYPE == Data.PAWN) {
				for (int a = -1; a <= 1; a++) {
					if (a == 0)
						continue;

					if (Move.isPieceLocation (board, x + a, y + (isComputer ? 1 : -1))) {
						if (Move.getPieceLocation (board, x + a, y + (isComputer ? 1 : -1)).isWhite () == ISWHITE)
							covering++;
					}
				}
			}
			if (TYPE == Data.ROOK || TYPE == Data.QUEEN) {
				int a = 1;
				boolean done = false;

				for (int b = 0; b < 4; b++, a = 1, done = false) {
					while (a <= 7 && !done) {
						if (Move.isPieceLocation (board, x + (b < 2 ? 0 : 1) * (b % 2 == 0 ? 1 : -1) * a, y + (b > 1 ? 0 : 1) * (b % 2 == 0 ? 1 : -1)
								* a)) {
							if (Move.getPieceLocation (board, x + (b < 2 ? 0 : 1) * (b % 2 == 0 ? 1 : -1) * a, y + (b > 1 ? 0 : 1)
									* (b % 2 == 0 ? 1 : -1) * a).ISWHITE == ISWHITE) {
								done = true;
								covering++;
								break;
							} else
								break;
						}
						a++;
					}
				}
			}
			if (TYPE == Data.KNIGHT) {
				for (int a = -2; a <= 2; a++) {
					if (a == 0)
						continue;

					for (int b = -2; b <= 2; b++) {
						if (b == 0)
							continue;
						if (Math.abs (a + b) != 3)
							continue;

						if (Move.isPieceLocation (board, x + a, y + b)) {
							if (Move.getPieceLocation (board, x + a, y + b).ISWHITE == ISWHITE)
								covering++;
						}
					}
				}
			}
			if (TYPE == Data.BISHOP || TYPE == Data.QUEEN) {
				int a = 1;
				boolean done = false;
				for (int b = 0; b < 4; b++, done = false, a = 1) {
					while (a <= 7 && !done) {
						if (Move.isPieceLocation (board, x + (b < 2 ? -1 : 1) * a, y + (b % 2 == 0 ? -1 : 1) * a)) {
							if (Move.getPieceLocation (board, x + (b < 2 ? -1 : 1) * a, y + (b % 2 == 0 ? -1 : 1) * a).ISWHITE == ISWHITE) {
								done = true;
								covering++;
								break;
							} else
								break;
						}
						a++;
					}
				}
			}
			if (TYPE == Data.KING) {
				for (int a = -1; a <= 1; a++) {
					for (int b = -1; b <= 1; b++) {
						if (a == 0 && b == 0)
							continue;

						if (Move.isPieceLocation (board, x + a, y + b)) {
							if (Move.getPieceLocation (board, x + a, y + b).ISWHITE == ISWHITE)
								covering++;
						}
					}
				}
			}
		}
		return covering;
	}
	public int getCovered(ArrayList<ArrayList<Piece>> board) {
		int covered = 0;
		boolean isComputer = ISCOMPUTER;

		if (playing) {
			// Data.PAWN 
			for (int a = -1; a <= 1; a++) {
				if (a == 0)
					continue;

				if (Move.isPieceLocation (board, x + a, y + (isComputer ? 1 : -1))) {
					if (Move.getPieceLocation (board, x, y).ISWHITE == ISWHITE && Move.getPieceLocation (board, x, y).getType () == Data.PAWN)
						covered++;
				}
			}
			// Data.ROOK & Data.QUEEN
			for (int a = -x - 1; a <= 8 - x + 1; a++) { // horizontal
				if (a == 0)
					continue;
				if (Move.isPieceLocation (board, x + a, y)) {
					if (Move.getPieceLocation (board, x + a, y).getType () == Data.ROOK || Move.getPieceLocation (board, x + a, y).getType () == Data.QUEEN
							&& Move.getPieceLocation (board, x + a, y).ISWHITE == ISWHITE)
						covered++;

					if (a < 0)
						a = 1;
					else
						break;
				}
			}
			for (int a = -x - 1; a <= 8 - x + 1; a++) { // vertical
				if (a == 0)
					continue;
				if (Move.isPieceLocation (board, x, y + a)) {
					if (Move.getPieceLocation (board, x, y + a).getType () == Data.ROOK || Move.getPieceLocation (board, x, y + a).getType () == Data.QUEEN
							&& Move.getPieceLocation (board, x, y + a).ISWHITE == ISWHITE)
						covered++;

					if (a < 0)
						a = 1;
					else
						break;
				}
			}
			// Data.KNIGHT
			for (int a = -2; a <= 2; a++) {
				if (a == 0)
					continue;

				for (int b = -2; b <= 2; b++) {
					if (b == 0 || a + b != 3)
						continue;

					if (Move.isPieceLocation (board, x + a, y + b)) {
						if (Move.getPieceLocation (board, x + a, y + b).getType () == Data.KNIGHT
								&& Move.getPieceLocation (board, x + a, y + b).ISWHITE == ISWHITE)
							covered++;
					}
				}
			}
			// Data.BISHOP & Data.QUEEN
			for (int a = -x - 1; a <= 8 - x + 1; a++) { // x and y both increasing/decreasing
				if (a == 0)
					continue;
				if (Move.isPieceLocation (board, x + a, y + a)) {
					if (Move.getPieceLocation (board, x + a, y + a).getType () == Data.BISHOP
							|| Move.getPieceLocation (board, x + a, y + a).getType () == Data.QUEEN
							&& Move.getPieceLocation (board, x + a, y + a).ISWHITE == ISWHITE)
						covered++;

					if (a < 0)
						a = 1;
					else
						break;
				}
			}
			for (int a = -x - 1; a <= 8 - x + 1; a++) { // x increasing, y decreasing/x decreasing, y increasing
				if (a == 0)
					continue;
				if (Move.isPieceLocation (board, x - a, y + a)) {
					if (Move.getPieceLocation (board, x - a, y + a).getType () == Data.BISHOP
							|| Move.getPieceLocation (board, x - a, y + a).getType () == Data.QUEEN
							&& Move.getPieceLocation (board, x - a, y + a).ISWHITE == ISWHITE)
						covered++;

					if (a < 0)
						a = 1;
					else
						break;
				}
			}
			// Data.KING
			for (int a = -1; a <= 1; a++) {
				for (int b = -1; b <= 1; b++) {

					if (Move.isPieceLocation (board, x + a, y + a)) {
						if (Move.getPieceLocation (board, x + a, y + a).getType () == Data.KING
								&& Move.getPieceLocation (board, x + a, y + a).ISWHITE == ISWHITE)
							covered++;
					}
				}
			}
		}

		return covered;
	}
	public int getAttacking(ArrayList<ArrayList<Piece>> board) {
		int attacking = 0;

		if (playing) {
			if (TYPE == Data.PAWN) {
				for (int a = -1; a <= 1; a++) {
					if (a == 0)
						continue;

					if (Move.isPieceLocation (board, x + a, y - 1)) {
						if (Move.getPieceLocation (board, x + a, y - 1).ISWHITE == !ISWHITE)
							attacking++;
					}
				}
			}
			if (TYPE == Data.ROOK || TYPE == Data.QUEEN) {
				int a = 1;
				boolean done = false;

				for (int b = 0; b < 4; b++, a = 1, done = false) {
					while (a <= 7 && !done) {
						if (Move.isPieceLocation (board, x + (b < 2 ? 0 : 1) * (b % 2 == 0 ? 1 : -1) * a, y + (b > 1 ? 0 : 1) * (b % 2 == 0 ? 1 : -1)
								* a)) {
							if (Move.getPieceLocation (board, x + (b < 2 ? 0 : 1) * (b % 2 == 0 ? 1 : -1) * a, y + (b > 1 ? 0 : 1)
									* (b % 2 == 0 ? 1 : -1) * a).ISWHITE == !ISWHITE) {
								done = true;
								attacking++;
								break;
							} else
								break;
						}
						a++;
					}
				}
			}
			if (TYPE == Data.KNIGHT) {
				for (int a = -2; a <= 2; a++) {
					if (a == 0)
						continue;

					for (int b = -2; b <= 2; b++) {
						if (b == 0)
							continue;
						if (Math.abs (a + b) != 3)
							continue;

						if (Move.isPieceLocation (board, x + a, y + b)) {
							if (Move.getPieceLocation (board, x + a, y + b).ISWHITE == !ISWHITE)
								attacking++;
						}
					}
				}
			}
			if (TYPE == Data.BISHOP || TYPE == Data.QUEEN) {
				int a = 1;
				boolean done = false;
				for (int b = 0; b < 4; b++, done = false, a = 1) {
					while (a <= 7 && !done) {
						if (Move.isPieceLocation (board, x + (b < 2 ? -1 : 1) * a, y + (b % 2 == 0 ? -1 : 1) * a)) {
							if (Move.getPieceLocation (board, x + (b < 2 ? -1 : 1) * a, y + (b % 2 == 0 ? -1 : 1) * a).ISWHITE == !ISWHITE) {
								done = true;
								attacking++;
								break;
							} else
								break;
						}
						a++;
					}
				}
			}
			if (TYPE == Data.KING) {
				for (int a = -1; a <= 1; a++) {
					for (int b = -1; b <= 1; b++) {
						if (a == 0 && b == 0)
							continue;

						if (Move.isPieceLocation (board, x + a, y + b)) {
							if (Move.getPieceLocation (board, x + a, y + b).ISWHITE == !ISWHITE)
								attacking++;
						}
					}
				}
			}
		}
		return attacking;
	}
	public boolean isCheckingKing(Chess chess) {
		return chess.isAttacking (this, chess.getPiece (!ISWHITE, Data.KING));
	}
	public String toString() {
		return lName + ": (" + x + "," + y + ")";
	}

	/* SETTER */
	public void castled() {
		canCastle = false;
	}
	public void moved() {
		moved = true;
	}
	public void to(int x, int y, Chess chess) {
		if (x < 1 || x > 8 || y < 1 || y > 8) {
			new Error (this, 7);
			return;
		}

		this.x = x;
		this.y = y;

		if (TYPE == Data.KING) {
			if (this.x == x + 2 || this.x == x-2)
				canCastle = false;
		}
		chess.updateBoard ();
		moved ();
	}
	public void selected(boolean b) {
		selected = b;
	}
	public void captured() {
		playing = false;
	}
	public void captured(int x, int y) {
		playing = false;
		capX = x;
		capY = y;
	}
	public void freed() {
		playing = true;
	}

	/* GETTER */
	public int getType() {
		return TYPE;
	}
	public int getValue() {
		return VALUE;
	}
	public Image getImage() {
		return image;
	}
	public boolean isPlaying() {
		return playing;
	}
	public boolean canCastle() {
		return canCastle;
	}
	public boolean hasMoved() {
		return moved;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public boolean isSelected() {
		return selected;
	}
	public boolean isWhite() {
		return ISWHITE;
	}
	public boolean isComputer() {
		return ISCOMPUTER;
	}
	public int getCapX() {
		return capX;
	}
	public int getCapY() {
		return capY;
	}
	public String getSName() {
		return sName;
	}
	public String getLName() {
		return lName;
	}
}
