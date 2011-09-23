
public class Unmove {
	private Piece piece;
	private Piece capturedPiece;
	private int fromX;
	private int fromY;
	private int toX;
	private int toY;
	
	/* INIT */
	Unmove(Piece p, int fromX, int fromY, int toX, int toY, Piece c) {
		piece = p;
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
		capturedPiece = c;
	}
	
	/* GETTER */
	public int getFromX() {
		return fromX;
	}
	public int getFromY() {
		return fromY;
	}
	public int getToX() {
		return toX;
	}
	public int getToY() {
		return toY;
	}
	public Piece getPiece() {
		return piece;
	}
	public Piece getCapturedPiece() {
		return capturedPiece;
	}
	public String toString () {
		return piece + " to (" + toX + "," + toY + "), from (" + fromX + "," + fromY + "), " + capturedPiece;
	}
}
