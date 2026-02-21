package pieces;

import core.Board;
import core.Square;

public class Queen extends Piece {

    public Queen(PieceColor color) {
        super(color);
    }

    @Override
    public String getSymbol() {
        if (this.getColor() == PieceColor.WHITE) {
            return "♕";
        } else {
            return "♛";
        }
    }

    @Override
    public boolean isValidMove(Board board, Square start, Square end) {
        if (start.getX() == end.getX() && start.getY() == end.getY()) {
            return false;
        }

        if (end.getPiece() != null && end.getPiece().getColor() == this.getColor()) {
            return false;
        }

        boolean straight = (start.getX() == end.getX() || start.getY() == end.getY())
                && board.isPathClearStraight(start, end);
        boolean diagonal = board.isPathClearDiagonal(start, end);

        return straight || diagonal;
    }
}
