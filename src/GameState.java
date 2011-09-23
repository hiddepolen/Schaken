public class GameState {
	private boolean turn; // White to move
	/*private boolean wck; // white can castle kingside
	private boolean wcq; // white can castle queenside
	private boolean bck; // black can castle kingside
	private boolean bcq; // black can castle queenside
	private int epSquare; // en-passant target square*/
	private int moveNum; // Move number;
	private int wkX; // White king
	private int wkY;
	private int bkX; // Black king
	private int bkY;

	/* INIT */
	GameState(GameState g) {
		this.turn = g.turn;
		/*this.wck = g.wck;
		this.wcq = g.wcq;
		this.bck = g.bck;
		this.bcq = g.bcq;
		this.epSquare = g.epSquare;*/
		this.moveNum = g.moveNum;
		this.wkX = g.wkX;
		this.wkY = g.wkY;
		this.bkX = g.bkX;
		this.bkY = g.bkY;
	}
	GameState(boolean turn, /*boolean wck, boolean wcq, boolean bck, boolean bcq, int epSquare, */int moveNum, int wkX, int wkY, int bkX, int bkY) {
		this.turn = turn;
		/*this.wck = wck;
		this.wcq = wcq;
		this.bck = bck;
		this.bcq = bcq;
		this.epSquare = epSquare;*/
		this.moveNum = moveNum;
		this.wkX = wkX;
		this.wkY = wkY;
		this.bkX = bkX;
		this.bkY = bkY;
	}

	/* SETTER */
	/*public void setEpSquare (int epSquare) {
		this.epSquare = epSquare;
	}*/
	public void nextTurn() {
		turn = !turn;
	}

	/* GETTER */
	public boolean getTurn() {
		return turn;
	}
	public int getMoveNum() {
		return moveNum;
	}
	/*public int epSquare() {
		return epSquare;
	}
	public boolean wck() {
		return wck;
	}
	public boolean wcq() {
		return wcq;
	}
	public boolean bck() {
		return bck;
	}
	public boolean bcq() {
		return bcq;
	}*/
	public int wkX() {
		return wkX;
	}
	public int wkY() {
		return wkY;
	}
	public int bkX() {
		return wkX;
	}
	public int bkY() {
		return wkY;
	}
	public GameState copy() {
		return new GameState (this);
	}
}
