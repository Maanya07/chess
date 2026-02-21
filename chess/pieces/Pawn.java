package pieces;

import core.Board;
import core.Square;

public class Pawn extends Piece {

    public Pawn(PieceColor color) {
        super(color);
    }

    @Override
    public String getSymbol() {
        if (this.getColor() == PieceColor.WHITE) {
            return "♙";
        } else {
            return "♟";
        }
    }

    @Override
    public boolean isValidMove(Board board, Square start, Square end) {
        if (start.getX() == end.getX() && start.getY() == end.getY()) {
            return false;
        }

        int direction = this.getColor() == PieceColor.WHITE ? -1 : 1;
        int startRow = this.getColor() == PieceColor.WHITE ? 6 : 1;
        int xDiff = end.getX() - start.getX();
        int yDiff = Math.abs(end.getY() - start.getY());

        if (yDiff == 0) {
            if (end.getPiece() != null) {
                return false;
            }

            if (xDiff == direction) {
                return true;
            }

            if (start.getX() == startRow && xDiff == 2 * direction) {
                Square stepSquare = board.getBox(start.getX() + direction, start.getY());
                return stepSquare != null && stepSquare.getPiece() == null;
            }
            return false;
        }

        if (yDiff == 1 && xDiff == direction) {
            return end.getPiece() != null && end.getPiece().getColor() != this.getColor();
        }

        return false;
    }
}
