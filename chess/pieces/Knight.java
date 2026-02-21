package pieces;

import core.Board;
import core.Square;

public class Knight extends Piece {

    public Knight(PieceColor color) {
        super(color);
    }

    @Override
    public String getSymbol() {
        if (this.getColor() == PieceColor.WHITE) {
            return "♘"; // White Knight Unicode
        } else {
            return "♞"; // Black Knight Unicode
        }
    }

    @Override
    public boolean isValidMove(Board board, Square start, Square end) {
        if (end.getPiece() != null && end.getPiece().getColor() == this.getColor()) {
            return false;
        }

        int xMath = Math.abs(start.getX() - end.getX());
        int yMath = Math.abs(start.getY() - end.getY());

        if (xMath * yMath == 2) {
            return true;
        }

        return false;
    }
}