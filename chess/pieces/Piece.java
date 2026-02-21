package pieces;

import core.Board;
import core.Square;

public abstract class Piece {
    private PieceColor color;
    //every piece starts alive.
    private boolean isKilled = false;
    private boolean hasMoved = false;

    //constructor
    public Piece(PieceColor color) {
        this.color = color;
    }

    public boolean isKilled(){
        return isKilled;
    }

public void setKilled(boolean killed) {
        isKilled = killed;
    }
    public PieceColor getColor() {
        return color;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    // Default implementation: most pieces will override this.
    // Returns false by default (no move allowed) to encourage overrides in subclasses.
    public boolean isValidMove(Board board, Square start, Square end) {
        return false;
    }

    // Every piece must provide a symbol (like "W-N" for White Knight)
    public abstract String getSymbol();

}
