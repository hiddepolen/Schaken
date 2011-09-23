import java.util.ArrayList;

public class Move {
	/*
	 * - Move is checking opponents king (+++++) 
	 * 		- (Castle +++) 
	 * - Move captures opponents piece (+++) 
	 * 		- (Opponents piece is being covered ---) 
	 * - Move is attacking opponents piece (++) 
	 * - Move is being covered by own piece (++) 
	 * - Possible move reach (+) 
	 * - Move is covering own piece (+) 
	 * - Piece value (+) 
	 * - Distance from center (-) 
	 * - Move is being attacked by opponent (-) 
	 * - Move is checking own king (-----)
	 * 
	 * - Value pieces: 
	 * 		- attacking piece 
	 * 		- being covered
	 */

	/* PRIVATE */
	private Piece piece;
	private Move parent = null;
	private int x;
	private int y;
	private int value;
	private boolean hasValue = false;

	/* INIT */
	Move(Piece p, int x, int y) {
		piece = p;
		this.x = x;
		this.y = y;
	}

	/* SETTER */
	public void setValue(int value) {
		this.value = value;
		hasValue = true;
	}
	public void setParent(Move m) {
		this.parent = m;
	}

	/* GETTER */
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Piece getPiece() {
		return piece;
	}
	public int getValue() {
		return value;
	}
	public String toString() {
		return piece + " to (" + x + "," + y + ")";
	}
	public boolean hasValue() {
		return hasValue;
	}
	public Move getParent() {
		return parent;
	}
	public boolean hasParent() {
		if (parent != null)
			return true;
		else
			return false;
	}

	/* STATIC */
	public static boolean isValid(ArrayList<ArrayList<Piece>> board, Move m) {
		Piece p = m.getPiece ();
		int x = m.getX ();
		int y = m.getY ();
		boolean isMoveValid = isMoveValid (board, m);

		if (x < 1 || y < 1 || x > 8 || y > 8)
			return false;

		if (isMoveValid && p.getType () != Data.PAWN) {
			if (isPieceLocation (board, x, y)) {
				Piece capPiece = getPieceLocation (board, x, y);
				if (capPiece.getType () == Data.KING)
					return false;
				else if (capPiece.isWhite () != p.isWhite ())
					return true;
				else
					return false;
			} else
				return true;
		} else if (p.getType () == Data.PAWN) {
			if (isPieceLocation (board, x, y)) {
				if (getPieceLocation (board, x, y).isWhite () != p.isWhite ()) {
					if ((p.getX () == x + 1 || p.getX () == x - 1) && p.getY () == y + (p.isComputer () ? -1 : 1))
						return true;
					else
						return false;
				} else
					return false;
			} else if (isMoveValid)
				return true;
			else
				return false;
		} else
			return false;
	}
	private static boolean isMoveValid(ArrayList<ArrayList<Piece>> board, Move m) {
		Piece p = m.getPiece ();
		int x = m.getX ();
		int y = m.getY ();

		if (p.getType () == Data.PAWN) {
			if (x == p.getX () && y == p.getY () + (!p.isComputer () ? -1 : 1) * 1)
				return true;
			else if ((x == p.getX () && y == p.getY () + (!p.isComputer () ? -1 : 1) * 2 && !p.hasMoved ())
					&& (!isPieceLocation (board, p.getX (), p.getY () + (!p.isComputer () ? -1 : 1)))) // First move pawn //
				return true;
		} else if (p.getType () == Data.ROOK) {
			if (x == p.getX () || y == p.getY ()) {
				if (x == p.getX ()) { // Vertical move //
					for (int a = 1; (p.getY () < y && a < y - p.getY ()) || (p.getY () > y && a < p.getY () - y); a++) {
						if (isPieceLocation (board, x, (p.getY () < y ? p.getY () : y) + a))
							return false;
					}
				} else { // Horizontal move //
					for (int a = 1; (p.getX () < x && a < x - p.getX ()) || (p.getX () > x && a < p.getX () - x); a++) {
						if (isPieceLocation (board, (p.getX () < x ? p.getX () : x) + a, y))
							return false;
					}
				}
				return true;
			}
		} else if (p.getType () == Data.KNIGHT) {
			if (Math.abs (p.getY () - y) + Math.abs (p.getX () - x) == 3 && y != p.getY () && x != p.getX ())
				return true;
		} else if (p.getType () == Data.BISHOP) {
			if (Math.abs (p.getY () - y) == Math.abs (p.getX () - x)) {
				for (int a = 1; a < Math.abs (p.getY () - y); a++) {
					if (isPieceLocation (board, p.getX () + (x < p.getX () ? -1 : 1) * a, 
							p.getY () + (y < p.getY () ? -1 : 1) * a))
						return false;
				}
				return true;
			}
		} else if (p.getType () == Data.QUEEN) {
			if (x == p.getX () || y == p.getY ()) { // Rook //
				if (x == p.getX ()) {
					for (int a = 1; (p.getY () < y && a < y - p.getY ()) || (p.getY () > y && a < p.getY () - y); a++) {
						if (isPieceLocation (board, x, (p.getY () < y ? p.getY () : y) + a))
							return false;
					}
				} else {
					for (int a = 1; (p.getX () < x && a < x - p.getX ()) || (p.getX () > x && a < p.getX () - x); a++) {
						if (isPieceLocation (board, (p.getX () < x ? p.getX () : x) + a, y))
							return false;
					}
				}
				return true;
			} else if (Math.abs (p.getY () - y) == Math.abs (p.getX () - x)) { // Bishop //
				if (Math.abs (p.getY () - y) == Math.abs (p.getX () - x)) {
					for (int a = 1; a < Math.abs (p.getY () - y); a++) {
						if (isPieceLocation (board, p.getX () + (x < p.getX () ? -1 : 1) * a, p.getY () + (y < p.getY () ? -1 : 1) * a))
							return false;
					}
					return true;
				}
				return true;
			}
		} else if (p.getType () == Data.KING) { // Both horizontal and vertical movements may be 1 //
			if (Math.abs (p.getY () - y) <= 1 && Math.abs (p.getX () - x) <= 1)
				return true;
			if (!p.hasMoved () && (x == p.getX () + 2 || x == p.getX () - 2)) {
				Piece rook;
				if (x == p.getX () + 2) {
					rook = getPieceLocation (board, 8, p.getY ());
					if (rook != null) {
						if (rook.getType () == Data.ROOK && !rook.hasMoved ()) {
							int i = p.getX ();
							boolean canCastle = true;
							while (++i < 8) {
								if (isPieceLocation (board, i, p.getY ()))
									canCastle = false;
							}
							if (canCastle)
								return true;
						}
					}
				} else {
					rook = getPieceLocation (board, 1, p.getY ());
					if (rook != null) {
						if (rook.getType () == Data.ROOK && !rook.hasMoved ()) {
							int i = p.getX ();
							boolean canCastle = true;
							while (--i > 1) {
								if (isPieceLocation (board, i, p.getY ()))
									canCastle = false;
							}
							if (canCastle)
								return true;
						}
					}
				}
			}
		}
		return false;
	}
	public static boolean isPieceLocation(ArrayList<ArrayList<Piece>> board, int x, int y) {
		if (x < 1 || x > 8 || y < 1 || y > 8)
			return false;

		if (board.get (x).get (y) == null)
			return false;
		else
			return true;
	}
	public static Piece getPieceLocation(ArrayList<ArrayList<Piece>> board, int x, int y) {
		if (x < 1 || x > 8 || y < 1 || y > 8) {
			new Error (Move.class, 3);
			new Check (Move.class, x + ", " + y);
			return null;
		}
		return board.get (x).get (y);
	}
}
