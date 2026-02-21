package pieces;

import core.Board;
import core.Square;

public class Rook extends Piece {

    public Rook(PieceColor color) {
        super(color);
    }

    // --- UPDATED METHOD ---
    // Now returning actual chess piece icons using Unicode!
    @Override
    public String getSymbol() {
        if (this.getColor() == PieceColor.WHITE) {
            return "♖"; // White Rook Unicode
        } else {
            return "♜"; // Black Rook Unicode
        }
    }

    @Override
    public boolean isValidMove(Board board, Square start, Square end) {
        // Rule 1: We cannot move to a square that has a piece of our OWN color.
        if (end.getPiece() != null && end.getPiece().getColor() == this.getColor()) {
            return false;
        }

        // Rule 2: The math for a straight line.
        if (start.getX() == end.getX() || start.getY() == end.getY()) {
            return board.isPathClearStraight(start, end);
        }

        // If it didn't move in a straight line, it's illegal.
        return false;
    }
}
