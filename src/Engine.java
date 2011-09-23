import java.util.*;

import javax.swing.SwingWorker;

public class Engine extends SwingWorker<Move, Void> {
	/* PIVATE */
	private Chess chess;
	private int[] captured;
	private int evalCount;
	private int[][] board = new int[9][9]; // filled with 0, or pieceID
	private final int[] piece = new int[33]; // piece[id]
	private final int[] value = new int[33]; // value[id]
	private int[][][] mList; // MoveList
	private int[] nm;
	private int[][][] cList; // CaptureList
	private int[] nc;
	private int[][][] capList; // CaptureList in support of Quinscence Search()
	private int[] nCap;
	private int[] finalMove;

	/* FLAGS */
	private boolean whiteCanCastle = false;
	private boolean blackCanCastle = false;
	private final boolean whiteIsComputer;
	private boolean isComputer;

	/* CONSTANTS */
	private final boolean[] white = new boolean[33]; // isWhite[id]
	private final boolean[] computer = new boolean[33]; // isComputer[id]
	private final boolean[] moved = new boolean[33]; // hasMoved[id]
	private final boolean[] playing = new boolean[33]; // playing[id]
	private final int[] kingID = new int[2];
	private final int[] knightID = new int[4];
	private final int[] bishopID = new int[4];

	/* INIT */
	Engine(Chess chess, boolean isComputer) {
		this.chess = chess;
		int ply = Data.SEARCHDEPTH;
		init (ply);

		this.isComputer = isComputer;
		whiteIsComputer = chess.whiteIsComputer ();

		int id = 1;
		int[] bishop = {0, 0 };
		int[] knight = {0, 0 };
		ArrayList<ArrayList<Piece>> board = chess.getBoard ();
		for (int a = 1; a <= 8; a++) {
			for (int b = 1; b <= 8; b++) {
				Piece p = board.get (a).get (b);
				if (p == null) {
					this.board[a][b] = 0;
				} else {
					this.board[a][b] = id;
					piece[id] = p.getType ();
					value[id] = (p.isWhite () ? p.getValue () : -p.getValue ());

					white[id] = p.isWhite ();
					moved[id] = p.hasMoved ();
					computer[id] = p.isComputer ();

					if (p.getType () == Data.KING) {
						if (p.isWhite ()) {
							whiteCanCastle = p.canCastle ();
							kingID[0] = id;
						} else {
							blackCanCastle = p.canCastle ();
							kingID[1] = id;
						}
					}
					if (p.getType () == Data.BISHOP) {
						if (p.isWhite ())
							bishopID[bishop[0]++] = id;
						else
							bishopID[2 + bishop[1]++] = id;
					}
					if (p.getType () == Data.KNIGHT) {
						if (p.isWhite ())
							knightID[knight[0]++] = id;
						else
							knightID[2 + knight[1]++] = id;
					}

					if (p.isPlaying ())
						playing[id] = true;
					else
						playing[id] = false;

					id++;
				}
			}
		}
		value[0] = 0;
		piece[0] = 0;
		white[0] = false;
		moved[0] = false;
		computer[0] = false;
		playing[0] = false;
	}
	private void init(int ply) {
		captured = new int[ply + 1];

		mList = new int[9][100][5];
		nm = new int[9];

		cList = new int[9][25][7];
		nc = new int[9];

		//cmList = new int[9][25][6];
		//ncm = new int[9];

		capList = new int[10][35][7];
		nCap = new int[10];

		finalMove = new int[5];
	}

	/* METHODS*/
	public Move doInBackground() {
		int ply = Data.SEARCHDEPTH;
		init (ply);

		long start = Calendar.getInstance ().getTimeInMillis ();
		evalCount = 0;

		// Get game stage //
		alphaBeta (ply, -0x8000, 0x8000, isComputer);

		Piece retPiece = chess.getPieceLocation (finalMove[0], finalMove[1]);
		Move ret = new Move (retPiece, finalMove[2], finalMove[3]);

		long end = Calendar.getInstance ().getTimeInMillis ();
		long time = end - start;

		Info.addText ("T: " + time + " ms,\t EC: " + evalCount + ",\t v: " + 1000 * time / evalCount + "\t" + ret.getPiece ().getSName () + "("
				+ ret.getX () + ", " + ret.getY () + ").");
		return ret;
	}
	private int alphaBeta(int ply, int alpha, int beta, boolean isComputer) {
		int fromX, fromY, toX, toY;
		int eval, count = 0;
		int[] i;
		boolean legal;

		this.isComputer = isComputer;

		if (ply == 0)
			return qSearch (ply, alpha, beta, isComputer);

		//if (!inCheck (ply, this.isComputer == whiteIsComputer)) {
		moveGenerator (ply);

		// process captures //
		for (int a = 0; a < nc[ply]; a++) {
			i = cList[ply][a];
			fromX = i[0];
			fromY = i[1];
			toX = i[2];
			toY = i[3];

			legal = doMove (ply, fromX, fromY, toX, toY);

			if (legal) {
				count++;

				eval = -alphaBeta (ply - 1, -beta, -alpha, !isComputer);

				undoMove (ply, toX, toY, fromX, fromY);

				if (eval >= beta) {
					if (ply == Data.SEARCHDEPTH)
						finalMove = Arrays.copyOf (i, 5);
					return beta;
				}
				if (eval > alpha) {
					if (ply == Data.SEARCHDEPTH)
						finalMove = Arrays.copyOf (i, 5);
					alpha = eval;
				}
			} else
				undoMove (ply, toX, toY, fromX, fromY);
		}

		// process moves //
		for (int a = 0; a < nm[ply]; a++) {
			i = mList[ply][a];
			fromX = i[0];
			fromY = i[1];
			toX = i[2];
			toY = i[3];
			legal = doMove (ply, fromX, fromY, toX, toY);

			if (legal) {
				count++;

				eval = -alphaBeta (ply - 1, -beta, -alpha, !isComputer);

				undoMove (ply, toX, toY, fromX, fromY);

				if (eval >= beta) {
					if (ply == Data.SEARCHDEPTH)
						finalMove = Arrays.copyOf (i, 5);
					return beta;
				}
				if (eval > alpha) {
					if (ply == Data.SEARCHDEPTH)
						finalMove = Arrays.copyOf (i, 5);
					alpha = eval;
				}
			} else
				undoMove (ply, toX, toY, fromX, fromY);
		}
		/*} else {
			checkMoveGenerator (ply);

			for (int a = 0; a < ncm[ply]; a++) {
				i = cmList[ply][a];
				fromX = i[0];
				fromY = i[1];
				toX = i[2];
				toY = i[3];
				legal = doMove (ply, fromX, fromY, toX, toY);

				if (legal) {
					count++;

					eval = -alphaBeta (ply - 1, -beta, -alpha, !isComputer);

					undoMove (ply, toX, toY, fromX, fromY);

					if (eval >= beta) {
						if (ply == Data.SEARCHDEPTH)
							finalMove = Arrays.copyOf (i, 5);
						return beta;
					}
					if (eval > alpha) {
						if (ply == Data.SEARCHDEPTH)
							finalMove = Arrays.copyOf (i, 5);
						alpha = eval;
					}
				} else
					undoMove (ply, toX, toY, fromX, fromY);
			}
		}*/

		if (count == 0) {
			if (inCheck (isComputer == whiteIsComputer))
				return -(100000 - (Data.SEARCHDEPTH - ply) + 1);
			else
				return 0;
		}
		return alpha;
	}
	private int qSearch(int ply, int alpha, int beta, boolean isComputer) {
		int fromX, fromY, toX, toY;
		boolean legal;

		this.isComputer = isComputer;

		int eval = evaluate ();
		if (ply > 9)
			return eval;
		if (eval >= beta)
			return beta;
		if (eval > alpha)
			alpha = eval;

		captureGenerator (ply);

		int[] i;
		for (int j = 0; j < nCap[ply]; j++) {
			i = capList[ply][j];
			fromX = i[0];
			fromY = i[1];
			toX = i[2];
			toY = i[3];

			legal = doMove (ply, fromX, fromY, toX, toY);
			if (legal) {
				eval = -qSearch (ply + 1, -beta, -alpha, !isComputer);

				undoMove (ply, toX, toY, fromX, fromY);

				if (eval >= beta)
					return beta;
				if (eval > alpha)
					alpha = eval;
			} else
				undoMove (ply, toX, toY, fromX, fromY);
		}
		return alpha;
	}
	synchronized private int evaluate() {
		int boardValue = 0, coveredValue = 0, posValue = 0, checkValue = 0, pairValue = 0;
		int evalValue, id;
		boolean isWhite = isComputer == whiteIsComputer;

		int x, y;
		for (x = 1; x <= 8; x++) {
			for (y = 1; y <= 8; y++) {
				id = board[x][y];

				if (value[id] > 0 && isComputer == whiteIsComputer)
					posValue += Data.getTableValue (piece[id], x, y);
				else if (value[id] < 0 && isComputer != whiteIsComputer)
					posValue -= Data.getTableValue (piece[id], x, y);

				if (whiteIsComputer == isComputer)
					boardValue += value[id];
				else
					boardValue += value[id];
				if (bishopPair (isWhite))
					pairValue += isWhite ? 100 : -100;
				if (knightPair (isWhite))
					pairValue += isWhite ? 100 : -100;
				checkValue = inCheck (!isWhite) ? (isWhite ? 20000 : -20000) : 0;
			}
		}

		evalCount++;
		evalValue = boardValue + posValue + coveredValue + checkValue;

		/*System.out.println (evalValue);
		*///printBoard ();
		return (isComputer == whiteIsComputer ? evalValue : -evalValue);
	}
	private void moveGenerator(int ply) {
		nm[ply] = 0;
		nc[ply] = 0;

		int x, y;

		for (x = 1; x <= 8; x++) {
			for (y = 1; y <= 8; y++) {
				if (whiteIsComputer == isComputer ? value[board[x][y]] > 0 : value[board[x][y]] < 0)
					generateMoves (ply, x, y);
			}
		}

		// MVV-LVA move ordering for captures //
		int[] temp;
		int i, j;
		for (i = 0; i < (nc[ply] - 1); i++) {
			for (j = i + 1; j < nc[ply]; j++) {

				if (cList[ply][i][6] > cList[ply][j][6])
					continue;
				if ((cList[ply][i][6] == cList[ply][j][6]) && (cList[ply][i][5] < cList[ply][j][5]))
					continue;
				temp = cList[ply][i];
				cList[ply][i] = cList[ply][j];
				cList[ply][j] = temp;
			}
		}
	}
	private void generateMoves(int ply, int x, int y) {
		int id = board[x][y];
		int p = piece[id];

		int x0, y0;
		int toID;

		switch (p) {
			case Data.PAWN:
				if (y < 8 && y > 1) {
					int[][] j = Data.p (x, y, computer[id]);
					for (int[] i : j) {
						toID = board[i[0]][i[1]];

						if (x == i[0]) { // eliminate non-captures where the square is blocked //
							if (board[x][y + (computer[id] ? 1 : -1) * 1] != 0)
								continue;
							if (toID != 0)
								continue;
						} else { // eliminate diagonal moves where there is no capture //
							if (value[id] * value[toID] >= 0)
								continue;
						}

						addMove (ply, x, y, i[0], i[1], 0);
					}
				}
				break;

			case Data.KING:
				for (int[] i : Data.k (x, y, computer[id])) {
					if (Math.abs (x - i[0]) <= 1 && Math.abs (y - i[1]) <= 1) {
						toID = board[i[0]][i[1]];
						if (value[id] * value[toID] > 0)
							continue;

						addMove (ply, x, y, i[0], i[1], 0);
					} else if (Math.abs (x - i[0]) == 2 && y == i[1] && (white[id] ? whiteCanCastle : blackCanCastle)) {
						if (!moved[id] && x == (whiteIsComputer ? 4 : 5) && y == (computer[id] ? 1 : 8)) {
							if (piece[board[1][y]] == Data.ROOK && moved[board[1][y]] == false && i[0] < x) {
								boolean kingCanCastle = true;
								int k = x;
								while (--k > 1) {
									if (board[k][y] != 0)
										kingCanCastle = false;
								}
								if (kingCanCastle)
									addMove (ply, x, y, i[0], i[1], 0);
							}
							if (piece[board[8][y]] == Data.ROOK && moved[board[8][y]] == false && i[0] > x) {
								boolean kingCanCastle = true;
								int k = x;
								while (++k < 8) {
									if (board[k][y] != 0)
										kingCanCastle = false;
								}
								if (kingCanCastle)
									addMove (ply, x, y, i[0], i[1], 0);
							}
						}
					}
				}
				break;

			case Data.KNIGHT:
				for (int[] i : Data.n (x, y, computer[id])) {
					if (value[id] * value[board[i[0]][i[1]]] > 0)
						continue;
					addMove (ply, x, y, i[0], i[1], 0);
				}
				break;

			case Data.QUEEN:
			case Data.ROOK:

				y0 = y;
				x0 = x;
				while (++y0 <= 8) {
					if (addMove (ply, x, y, x0, y0, 0))
						break;
				}

				y0 = y;
				x0 = x;
				while (--y0 >= 1) {
					if (addMove (ply, x, y, x0, y0, 0))
						break;
				}

				y0 = y;
				x0 = x;
				while (++x0 <= 8) {
					if (addMove (ply, x, y, x0, y0, 0))
						break;
				}

				y0 = y;
				x0 = x;
				while (--x0 >= 1) {
					if (addMove (ply, x, y, x0, y0, 0))
						break;
				}

				if (p == Data.ROOK)
					break;

			case Data.BISHOP:

				y0 = y;
				x0 = x;
				while (++y0 <= 8 && ++x0 <= 8) {
					if (addMove (ply, x, y, x0, y0, 0))
						break;
				}

				y0 = y;
				x0 = x;
				while (--y0 >= 1 && ++x0 <= 8) {
					if (addMove (ply, x, y, x0, y0, 0))
						break;
				}

				y0 = y;
				x0 = x;
				while (++y0 <= 8 && --x0 >= 1) {
					if (addMove (ply, x, y, x0, y0, 0))
						break;
				}

				y0 = y;
				x0 = x;
				while (--y0 >= 1 && --x0 >= 1) {
					if (addMove (ply, x, y, x0, y0, 0))
						break;
				}
				break;
		}
	}
	boolean addMove(int ply, int fromX, int fromY, int toX, int toY, int promo) {
		if (fromX < 1 || fromX > 8 || fromY < 1 || fromY > 8 || toX < 1 || toX > 8 || toY < 1 || toY > 8) {
			new Error (this, 8);
			return true;
		}

		if (inCheckAfterMove (ply, fromX, fromY, toX, toY, white[board[fromX][toX]]))
			return false;

		int fromVal = value[board[fromX][fromY]];
		int toVal = value[board[toX][toY]];

		if (fromVal * toVal > 0)
			return true;
		if (piece[board[toX][toY]] == Data.KING)
			return true;

		if (fromVal * toVal < 0) {
			cList[ply][nc[ply]][0] = fromX;
			cList[ply][nc[ply]][1] = fromY;
			cList[ply][nc[ply]][2] = toX;
			cList[ply][nc[ply]][3] = toY;
			cList[ply][nc[ply]][4] = promo;
			cList[ply][nc[ply]][5] = isComputer == whiteIsComputer ? fromVal : -fromVal;
			cList[ply][nc[ply]++][6] = (promo == 0) ? (isComputer ? -fromVal : fromVal) : (isComputer ? promo : -promo);
		} else {
			mList[ply][nm[ply]][0] = fromX;
			mList[ply][nm[ply]][1] = fromY;
			mList[ply][nm[ply]][2] = toX;
			mList[ply][nm[ply]][3] = toY;
			mList[ply][nm[ply]++][4] = promo;
		}

		return !(toVal == 0);
	}
	private boolean inCheckAfterMove(int ply, int fromX, int fromY, int toX, int toY, boolean isWhite) {
		doMove (ply, fromX, fromY, toX, toY);
		if (inCheck (isWhite)) {
			//
			//printBoard (true);
			undoMove (ply, toX, toY, fromX, fromY);
			return true;
		}
		undoMove (ply, toX, toY, fromX, fromY);
		return false;
	}
	private void captureGenerator(int ply) {
		nCap[ply] = 0;

		int x, y;
		for (x = 1; x <= 8; x++) {
			for (y = 1; y <= 8; y++) {
				if (isComputer == whiteIsComputer ? piece[board[x][y]] > 0 : piece[board[x][y]] < 0)
					generateCaptures (ply, x, y);
			}
		}

		// MVV-LVA move ordering for captures //

		int[] temp;
		int i, j;
		for (i = 0; i < (nCap[ply] - 1); i++) {
			for (j = i + 1; j < nCap[ply]; j++) {

				if (capList[ply][i][6] > capList[ply][j][6])
					continue;
				if ((capList[ply][i][6] == capList[ply][j][6]) && (capList[ply][i][5] < capList[ply][j][5]))
					continue;
				temp = capList[ply][i];
				capList[ply][i] = capList[ply][j];
				capList[ply][j] = temp;
			}
		}
	}
	private void generateCaptures(int ply, int x, int y) {
		int id = board[x][y];
		int p = piece[id];

		int x0, y0;

		switch (p) {
			case Data.PAWN:
				if (y < 8 && y > 1) {
					for (int[] i : Data.p (x, y, computer[id])) {
						int toID = board[i[0]][i[1]];

						if (x != i[0]) {
							if (i[1] == y + (computer[id] ? 1 : -1) * 1) {
								if (value[id] * value[toID] < 0)
									addCapture (ply, x, y, i[0], i[1], 0);
							}
						}
					}
				}
				break;

			case Data.KING:
				for (int[] i : Data.k (x, y, computer[id])) {
					if (!(y == i[1] && (x == i[0] + 2 || x == i[0] - 2))) {
						int toID = board[i[0]][i[1]];

						if (value[id] * value[toID] < 0)
							addCapture (ply, x, y, i[0], i[1], 0);
					}
				}
				break;

			case Data.KNIGHT:
				for (int[] i : Data.n (x, y, computer[id])) {
					if (value[id] * value[board[i[0]][i[1]]] < 0)
						addCapture (ply, x, y, i[0], i[1], 0);
				}
				break;

			case Data.QUEEN:
			case Data.ROOK:

				y0 = y;
				x0 = x;
				while (++y0 <= 8) { // South //
					if (board[x0][y0] == 0)
						continue;
					if (value[board[x][y]] * value[board[x0][y0]] < 0)
						addCapture (ply, x, y, x0, y0, 0);
					break;
				}

				y0 = y;
				x0 = x;
				while (--y0 >= 1) { // North //
					if (board[x0][y0] == 0)
						continue;
					if (value[board[x][y]] * value[board[x0][y0]] < 0)
						addCapture (ply, x, y, x0, y0, 0);
					break;
				}

				y0 = y;
				x0 = x;
				while (++x0 <= 8) { // East //
					if (board[x0][y0] == 0)
						continue;
					if (value[board[x][y]] * value[board[x0][y0]] < 0)
						addCapture (ply, x, y, x0, y0, 0);
					break;
				}

				y0 = y;
				x0 = x;
				while (--x0 >= 1) { // West //
					if (board[x0][y0] == 0)
						continue;
					if (value[board[x][y]] * value[board[x0][y0]] < 0)
						addCapture (ply, x, y, x0, y0, 0);
					break;
				}
				if (p == Data.ROOK)
					break;

			case Data.BISHOP:

				y0 = y;
				x0 = x;
				while (++y0 <= 8 && ++x0 <= 8) { // Southeast //
					if (board[x0][y0] == 0)
						continue;
					if (value[board[x][y]] * value[board[x0][y0]] < 0)
						addCapture (ply, x, y, x0, y0, 0);
					break;
				}

				y0 = y;
				x0 = x;
				while (--y0 >= 1 && ++x0 <= 8) { // Northeast //
					if (board[x0][y0] == 0)
						continue;
					if (value[board[x][y]] * value[board[x0][y0]] < 0)
						addCapture (ply, x, y, x0, y0, 0);
					break;
				}

				y0 = y;
				x0 = x;
				while (++y0 <= 8 && --x0 >= 1) { // Southwest //
					if (board[x0][y0] == 0)
						continue;
					if (value[board[x][y]] * value[board[x0][y0]] < 0)
						addCapture (ply, x, y, x0, y0, 0);
					break;
				}

				y0 = y;
				x0 = x;
				while (--y0 >= 1 && --x0 >= 1) { // Northwest //
					if (board[x0][y0] == 0)
						continue;
					if (value[board[x][y]] * value[board[x0][y0]] < 0)
						addCapture (ply, x, y, x0, y0, 0);
					break;
				}
				break;
			default:
				new Error (this, 12);
				break;
		}

	}
	private void addCapture(int ply, int fromX, int fromY, int toX, int toY, int promo) {
		if (fromX < 1 || fromX > 8 || fromY < 1 || fromY > 8 || toX < 1 || toX > 8 || toY < 1 || toY > 8) {
			new Error (this, 5);
			return;
		}
		if (inCheckAfterMove (ply, fromX, fromY, toX, toY, white[board[fromX][fromY]]))
			return;

		if (piece[board[toX][toY]] == Data.KING)
			return;

		int fromID = board[fromX][fromY];
		int toID = board[toX][toY];

		capList[ply][nCap[ply]][0] = fromY;
		capList[ply][nCap[ply]][1] = fromY;
		capList[ply][nCap[ply]][2] = toX;
		capList[ply][nCap[ply]][3] = toY;
		capList[ply][nCap[ply]][4] = promo;
		capList[ply][nCap[ply]][5] = white[fromID] ? value[toID] : -value[toID];
		capList[ply][nCap[ply]++][6] = (promo == 0) ? (white[fromID] ? -value[toID] : value[toID]) : (white[fromID] ? promo : -promo);
	}
	private boolean doMove(int ply, int fromX, int fromY, int toX, int toY) {
		if (fromX < 1 || fromX > 8 || fromY < 1 || fromY > 8 || toX < 1 || toX > 8 || toY < 1 || toY > 8)
			return false;

		captured[ply] = board[toX][toY];
		board[toX][toY] = board[fromX][fromY];
		board[fromX][fromY] = 0;

		if (piece[board[toX][toY]] == Data.KING) {
			if (toX == fromX + 2 && toY == fromY) {
				board[fromX + 1][fromY] = board[8][fromY];
				board[8][fromY] = 0;
			}
			if (toX == fromX - 2 && toY == fromY) {
				board[fromX - 1][fromY] = board[1][fromY];
				board[1][fromY] = 0;
			}
		}

		return true;
	}
	private boolean undoMove(int ply, int fromX, int fromY, int toX, int toY) {
		if (fromX < 1 || fromX > 8 || fromY < 1 || fromY > 8 || toX < 1 || toX > 8 || toY < 1 || toY > 8)
			return false;

		if (piece[board[fromX][fromY]] == Data.KING) {
			if (toX == fromX + 2 && toY == fromY) {
				board[1][fromY] = board[fromX + 1][fromY];
				board[fromX + 1][fromY] = 0;
			}
			if (toX == fromX - 2 && toY == fromY) {
				board[8][fromY] = board[fromX - 1][fromY];
				board[fromX - 1][fromY] = 0;
			}
		}

		board[toX][toY] = board[fromX][fromY];
		board[fromX][fromY] = captured[ply];

		return true;
	}
	private boolean inCheck(boolean isWhite) {

		int x = 0, y = 0;
		boolean found = false;
		if (isWhite) {
			int i, j;
			for (i = 1; i <= 8; i++) {
				for (j = 1; j <= 8; j++) {
					if (board[i][j] == kingID[0]) {
						x = i;
						y = j;
						found = true;
						break;
					}
				}
				if (found)
					break;
			}
		} else {
			int i, j;
			for (i = 1; i <= 8; i++) {
				for (j = 1; j <= 8; j++) {
					if (board[i][j] == kingID[1]) {
						x = i;
						y = j;
						found = true;
						break;
					}
				}
				if (found)
					break;
			}

		}
		return isAttacked (x, y);
	}
	private boolean isAttacked(int x, int y) {
		for (int[] i : Data.n (x, y, computer[board[x][y]])) {
			if (piece[board[i[0]][i[1]]] == Data.KNIGHT && value[board[x][y]] * value[board[i[0]][i[1]]] < 0) {
				//printBoard(true);
				return true;
			}
		}
		if (x <= 7 && y <= 7 && y >= 2) {
			if (piece[board[x + 1][y + (isComputer ? 1 : -1) * 1]] == Data.PAWN
					&& value[board[x][y]] * value[board[x + 1][y + (isComputer ? 1 : -1) * 1]] < 0)
				return true;
		} else if (x >= 2 && y <= 7 && y >= 2) {
			if (piece[board[x - 1][y + (isComputer ? 1 : -1) * 1]] == Data.PAWN
					&& value[board[x][y]] * value[board[x - 1][y + (isComputer ? 1 : -1) * 1]] < 0)
				return true;
		}
		int x0, y0;
		x0 = x;
		y0 = y;
		while (--y0 >= 1 && --x0 >= 1) {
			if (value[board[x0][y0]] * value[board[x][y]] > 0)
				break;
			else if (value[board[x0][y0]] * value[board[x][y]] < 0 &&
					(piece[board[x0][y0]] == Data.QUEEN || piece[board[x0][y0]] == Data.BISHOP))
				return true;
		}
		x0 = x;
		y0 = y;
		while (++y0 <= 8 && --x0 >= 1) {
			if (value[board[x0][y0]] * value[board[x][y]] > 0)
				break;
			else if (value[board[x0][y0]] * value[board[x][y]] < 0 &&
					(piece[board[x0][y0]] == Data.QUEEN || piece[board[x0][y0]] == Data.BISHOP)) {
				//printBoard(true);
				return true;
			}
		}
		x0 = x;
		y0 = y;
		while (--y0 >= 1 && ++x0 <= 8) {
			if (value[board[x0][y0]] * value[board[x][y]] > 0)
				break;
			else if (value[board[x0][y0]] * value[board[x][y]] < 0 &&
					(piece[board[x0][y0]] == Data.QUEEN || piece[board[x0][y0]] == Data.BISHOP))
				return true;
		}
		x0 = x;
		y0 = y;
		while (++y0 <= 8 && ++x0 <= 8) {
			if (value[board[x0][y0]] * value[board[x][y]] > 0)
				break;
			else if (value[board[x0][y0]] * value[board[x][y]] < 0 &&
					(piece[board[x0][y0]] == Data.QUEEN || piece[board[x0][y0]] == Data.BISHOP))
				return true;
		}

		x0 = x;
		y0 = y;
		while (++y0 <= 8) {
			if (value[board[x][y0]] * value[board[x][y]] > 0)
				break;
			else if (value[board[x][y0]] * value[board[x][y]] < 0 &&
					(piece[board[x][y0]] == Data.QUEEN || piece[board[x][y0]] == Data.ROOK))
				return true;
		}
		x0 = x;
		y0 = y;
		while (--y0 >= 1) {
			if (value[board[x][y0]] * value[board[x][y]] > 0)
				break;
			else if (value[board[x][y0]] * value[board[x][y]] < 0 &&
					(piece[board[x][y0]] == Data.QUEEN || piece[board[x][y0]] == Data.ROOK))
				return true;
		}
		x0 = x;
		y0 = y;
		while (++x0 <= 8) {
			if (value[board[x0][y]] * value[board[x][y]] > 0)
				break;
			else if (value[board[x0][y]] * value[board[x][y]] < 0 &&
					(piece[board[x0][y]] == Data.QUEEN || piece[board[x0][y]] == Data.ROOK))
				return true;
		}
		x0 = x;
		y0 = y;
		while (--x0 >= 1) {
			if (value[board[x0][y]] * value[board[x][y]] > 0)
				break;
			else if (value[board[x0][y]] * value[board[x][y]] < 0 &&
					(piece[board[x0][y]] == Data.QUEEN || piece[board[x0][y]] == Data.ROOK))
				return true;
		}
		return false;
	}
	private boolean bishopPair(boolean isWhite) {
		int b1 = bishopID[isWhite ? 0 : 2];
		int b2 = bishopID[isWhite ? 1 : 3];
		int a, b;
		int xb1 = 0, yb1 = 0, xb2 = 100, yb2 = 100;
		for (a = 1; a <= 8; a++) {
			for (b = 1; b <= 8; b++) {
				if (board[a][b] == b1) {
					xb1 = a;
					yb1 = b;
				} else if (board[a][b] == b2) {
					xb2 = a;
					yb2 = b;
				}
			}
		}
		if (xb1 == 0 || yb1 == 0 || xb2 == 100 || yb2 == 100)
			return false;
		if (Math.abs (xb1 - xb2) <= 1 && Math.abs (yb1 - yb2) <= 1) {
			printBoard (true);
			return true;
		}
		return false;
	}
	private boolean knightPair(boolean isWhite) {
		int b1 = knightID[isWhite ? 0 : 2];
		int b2 = knightID[isWhite ? 1 : 3];
		int a, b;
		int xb1 = 0, yb1 = 0, xb2 = 100, yb2 = 100;
		for (a = 1; a <= 8; a++) {
			for (b = 1; b <= 8; b++) {
				if (board[a][b] == b1) {
					xb1 = a;
					yb1 = b;
				} else if (board[a][b] == b2) {
					xb2 = a;
					yb2 = b;
				}
			}
		}
		if (xb1 == 0 || yb1 == 0 || xb2 == 100 || yb2 == 100)
			return false;
		if (Math.abs (xb1 - xb2) <= 1 && Math.abs (yb1 - yb2) <= 1) {
			printBoard (true);
			return true;
		}
		return false;
	}
	private void printBoard() {
		/*try {
			Thread.sleep (0);
		} catch (InterruptedException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}*/
		/*for (int y = 8; y >= 1; y--) {
			boolean firstPrint = true;
			for (int x = 0; x <= 8; x++) {
				if (board[x][9 - y] != 0) {
					if (playing[board[x][9 - y]])
						System.out.print (piece[board[x][9 - y]] + "| ");
					else
						System.out.print (" | ");
				} else if (!firstPrint)
					System.out.print (" | ");
				firstPrint = false;
			}
			System.out.println ();
		}
		System.out.println ();*/
	}
	private void printBoard(boolean b) {
		for (int y = 8; y >= 1; y--) {
			boolean firstPrint = true;
			for (int x = 0; x <= 8; x++) {
				if (board[x][9 - y] != 0) {
					if (playing[board[x][9 - y]])
						System.out.print (piece[board[x][9 - y]] + "|");
					else
						System.out.print (" |");
				} else if (!firstPrint)
					System.out.print (" |");
				firstPrint = false;
			}
			System.out.println ();
		}
		System.out.println ();
	}
}
