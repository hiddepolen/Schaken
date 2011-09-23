public class Data {
	/* PRIVATE STATIC */
	private final static String[] X = {"", "a", "b", "c", "d", "e", "f", "g", "h" };
	private final static String[] Y = {"", "1", "2", "3", "4", "5", "6", "7", "8" };
	private final static String[] P = {"", "", "R", "N", "B", "Q", "K" };

	/* STATIC CONSTANTS */
	public static final int PAWN = 1;
	public static final int ROOK = 2;
	public static final int KNIGHT = 3;
	public static final int BISHOP = 4;
	public static final int QUEEN = 5;
	public static final int KING = 6;
	public static final int printX[] = new int[10];
	public static final int printY[] = new int[10];
	public static final int capHumanX[] = new int[10];
	public static final int capComputerX[] = new int[10];
	public static final int capY[] = new int[10];
	public static final int SEARCHDEPTH = 2;

	public static int[][] p(int x, int y, boolean isComputer) {
		int[][] ret = null;

		for (int a = -1; a <= 1; a++) {
			if (isValid (x + a, y + (isComputer ? 1 : -1) * 1)) {
				int[] add = {x + a, y + (isComputer ? 1 : -1) * 1 };
				ret = add (ret, add);
			}
		}
		if ((isComputer && y == 2) || (!isComputer && y == 7)) {
			if (isValid (x, y + (isComputer ? 1 : -1) * 2)) {
				int[] add = {x, y + (isComputer ? 1 : -1) * 2 };
				ret = add (ret, add);
			}
		}
		return ret;
	}
	public static int[][] r(int x, int y, boolean isComputer) {
		if (isComputer)
			y = reverse (y);

		int[][] ret = null;
		int x0, y0;

		x0 = x;
		y0 = y;
		while (++x0 <= 8) {
			int[] add = {x0, y0 };
			ret = add (ret, add);
		}
		x0 = x;
		y0 = y;
		while (--x0 >= 1) {
			int[] add = {x0, y0 };
			ret = add (ret, add);
		}
		x0 = x;
		y0 = y;
		while (++y0 <= 8) {
			int[] add = {x0, y0 };
			ret = add (ret, add);
		}
		x0 = x;
		y0 = y;
		while (--y0 >= 1) {
			int[] add = {x0, y0 };
			ret = add (ret, add);
		}

		if (isComputer)
			return reverse (ret);
		return ret;
	}
	public static int[][] n(int x, int y, boolean isComputer) {
		if (isComputer)
			y = reverse (y);

		int[][] ret = null;
		for (int a = -2; a <= 2; a++) {
			if (a == 0)
				continue;
			for (int b = -2; b <= 2; b++) {
				if (b == 0 || Math.abs (a) == Math.abs (b))
					continue;

				if (isValid (x + a, y + b)) {
					int[] add = {x + a, y + b };
					ret = add (ret, add);
				}
			}
		}
		if (isComputer)
			return reverse (ret);
		return ret;
	}
	public static int[][] b(int x, int y, boolean isComputer) {
		if (isComputer)
			y = reverse (y);

		int[][] ret = null;
		int x0, y0;

		x0 = x;
		y0 = y;
		while (++x0 <= 8 && ++y0 <= 8) {
			int[] add = {x0, y0 };
			ret = add (ret, add);
		}
		x0 = x;
		y0 = y;
		while (--x0 >= 1 && ++y0 <= 8) {
			int[] add = {x0, y0 };
			ret = add (ret, add);
		}
		x0 = x;
		y0 = y;
		while (++x0 <= 8 && --y0 >= 1) {
			int[] add = {x0, y0 };
			ret = add (ret, add);
		}
		x0 = x;
		y0 = y;
		while (--x0 >= 1 && --y0 >= 1) {
			int[] add = {x0, y0 };
			ret = add (ret, add);
		}

		if (isComputer)
			return reverse (ret);
		return ret;
	}
	public static int[][] q(int x, int y, boolean isComputer) {
		int[][] ret = null;
		for (int[] i : r (x, y, isComputer))
			ret = add (ret, i);
		for (int[] i : b (x, y, isComputer))
			ret = add (ret, i);
		return ret;
	}
	public static int[][] k(int x, int y, boolean isComputer) {
		if (isComputer)
			y = reverse (y);

		int[][] ret = null;
		for (int a = -1; a <= 1; a++) {
			for (int b = -1; b <= 1; b++) {
				if (a == 0 && b == 0)
					continue;
				if (isValid (x + a, y + b)) {
					int[] add = {x + a, y + b };
					ret = add (ret, add);
				}
			}
		}
		{
			int[] add = {x + 2, y };
			ret = add (ret, add);
		}
		{
			int[] add = {x - 2, y };
			ret = add (ret, add);
		}

		if (isComputer)
			return reverse (ret);
		return ret;
	}

	private static int[][] add(int[][] in, int[] add) {
		int len = 0;
		if (in != null)
			len = in.length;
		int[][] out = new int[len + 1][2];
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				out[i] = in[i];
			}
		}
		out[len] = add;
		return out;
	}
	static int[][] reverse(int[][] in) {
		if (in == null)
			return in;
		/*if (in.length == 0) 
			return in;*/

		for (int[] i : in) {
			i[1] = 9 - i[1];
		}
		return in;
	}
	static int reverse(int in) {
		return 9 - in;
	}
	public static boolean isValid(int x, int y) {
		if (x > 8 || x < 1 || y > 8 || y < 1)
			return false;
		return true;
	}
	public static int getTableValue(int piece, int x, int y) {
		int ret = -10000;
		if (piece == PAWN) {
			int[][] table = { {0, 0, 0, 0, 0, 0, 0, 0 }, {50, 50, 50, 50, 50, 50, 50, 50 }, {10, 10, 20, 30, 30, 20, 10, 10 },
					{5, 5, 10, 25, 25, 10, 5, 5 }, {0, 0, 0, 20, 20, 0, 0, 0 }, {5, -5, -10, 0, 0, -10, -5, 5 }, {5, 10, 10, -20, -20, 10, 10, 5 },
					{0, 0, 0, 0, 0, 0, 0, 0 } };
			ret = table[8 - y][x - 1];
		} else if (piece == ROOK) {
			int[][] table = { {0, 0, 0, 0, 0, 0, 0, 0 }, {5, 10, 10, 10, 10, 10, 10, 5 }, {-5, 0, 0, 0, 0, 0, 0, -5 }, {-5, 0, 0, 0, 0, 0, 0, -5 },
					{-5, 0, 0, 0, 0, 0, 0, -5 }, {-5, 0, 0, 0, 0, 0, 0, -5 }, {-5, 0, 0, 0, 0, 0, 0, -5 }, {0, 0, 0, 5, 5, 0, 0, 0 } };
			ret = table[8 - y][x - 1];
		} else if (piece == KNIGHT) {
			int[][] table = { {-50, -40, -30, -30, -30, -30, -40, -50 }, {-40, -20, 0, 0, 0, 0, -20, -40 }, {-30, 0, 10, 15, 15, 10, 0, -30 },
					{-30, 5, 15, 20, 20, 15, 5, -30 }, {-30, 0, 15, 20, 20, 15, 0, -30 }, {-30, 5, 10, 15, 15, 10, 5, -30 },
					{-40, -20, 0, 5, 5, 0, -20, -40 }, {-50, -40, -30, -30, -30, -30, -40, -50 } };
			ret = table[8 - y][x - 1];
		} else if (piece == BISHOP) {
			int[][] table = { {-20, -10, -10, -10, -10, -10, -10, -20 }, {-10, 0, 0, 0, 0, 0, 0, -10 }, {-10, 0, 5, 10, 10, 5, 0, -10 },
					{-10, 5, 5, 10, 10, 5, 5, -10 }, {-10, 0, 10, 10, 10, 10, 0, -10 }, {-10, 10, 10, 10, 10, 10, 10, -10 },
					{-10, 5, 0, 0, 0, 0, 5, -10 }, {-20, -10, -10, -10, -10, -10, -10, -20 } };
			ret = table[8 - y][x - 1];
		} else if (piece == QUEEN) {
			int[][] table = { {-20, -10, -10, -5, -5, -10, -10, -20 }, {-10, 0, 0, 0, 0, 0, 0, -10 }, {-10, 0, 5, 5, 5, 5, 0, -10 },
					{-5, 0, 5, 5, 5, 5, 0, -5 }, {0, 0, 5, 5, 5, 5, 0, -5 }, {-10, 5, 5, 5, 5, 5, 0, -10 }, {-10, 0, 5, 0, 0, 0, 0, -10 },
					{-20, -10, -10, -5, -5, -10, -10, -20 } };
			ret = table[8 - y][x - 1];
		} else if (piece == KING) {
			// middle game
			int[][] midGameTable = { {-30, -40, -40, -50, -50, -40, -40, -30 }, {-30, -40, -40, -50, -50, -40, -40, -30 },
					{-30, -40, -40, -50, -50, -40, -40, -30 }, {-30, -40, -40, -50, -50, -40, -40, -30 }, {-20, -30, -30, -40, -40, -30, -30, -20 },
					{-10, -20, -20, -20, -20, -20, -20, -10 }, {20, 20, 0, 0, 0, 0, 20, 20 }, {20, 30, 10, 0, 0, 10, 30, 20 } };

			// end game
			/*int [][] endGameTable = {
					{-50,-40,-30,-20,-20,-30,-40,-50},
					{-30,-20,-10,  0,  0,-10,-20,-30},
					{-30,-10, 20, 30, 30, 20,-10,-30},
					{-30,-10, 30, 40, 40, 30,-10,-30},
					{-30,-10, 30, 40, 40, 30,-10,-30},
					{-30,-10, 20, 30, 30, 20,-10,-30},
					{-30,-30,  0,  0,  0,  0,-30,-30},
					{-50,-30,-30,-30,-30,-30,-30,-50}};*/
			if (true) { // if (isMidGame) {
				ret = midGameTable[8 - y][x - 1];
			}
		} else
			new Error (Data.class, 1);
		/*if (!isWhite)
			ret = -ret;*/
		return ret;
	}
	public static String getMove(boolean whiteisComputer, Move m, boolean capture, boolean castle) {
		String ret = "";
		if (!castle) {
			ret += P[m.getPiece ().getType ()];
			ret += (capture ? "x" : "");
			ret += X[whiteisComputer ? 9-m.getX () : m.getX ()];
			ret += Y[whiteisComputer ? m.getY () : 9-m.getY ()];
		}
		else {
			if (m.getX () == 7 || m.getX () == 2)
				ret += "0-0";
			else if (m.getX () == 6 || m.getX () == 3)
				ret += "0-0-0";
		}
		return ret;
	}
}
