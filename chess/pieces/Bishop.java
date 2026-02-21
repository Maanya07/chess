package pieces;

import core.Board;
import core.Square;

public class Bishop extends Piece {

    public Bishop(PieceColor color) {
        super(color);
    }

    @Override
    public String getSymbol() {
        if (this.getColor() == PieceColor.WHITE) {
            return "♗";
        } else {
            return "♝";
        }
    }

    @Override
    public boolean isValidMove(Board board, Square start, Square end) {
        if (end.getPiece() != null && end.getPiece().getColor() == this.getColor()) {
            return false;
        }

        return board.isPathClearDiagonal(start, end);
    }
}
