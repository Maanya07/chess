package pieces;

import core.Board;
import core.Square;

public class King extends Piece {

    public King(PieceColor color) {
        super(color);
    }

    @Override
    public String getSymbol() {
        if (this.getColor() == PieceColor.WHITE) {
            return "♔";
        } else {
            return "♚";
        }
    }

    @Override
    public boolean isValidMove(Board board, Square start, Square end) {
        if (end.getPiece() != null && end.getPiece().getColor() == this.getColor()) {
            return false;
        }

        int xDiff = Math.abs(start.getX() - end.getX());
        int yDiff = Math.abs(start.getY() - end.getY());

        return (xDiff <= 1 && yDiff <= 1) && !(xDiff == 0 && yDiff == 0);
    }
}
