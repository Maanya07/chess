package core;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.PieceColor;
import pieces.Queen;
import pieces.Rook;

public class Board {
    private final Square[][] boxes = new Square[8][8];
    private PieceColor currentTurn = PieceColor.WHITE;
    private boolean gameOver = false;
    private PieceColor winner = null;
    private String statusMessage = "Turn: WHITE";

    // Tracks pawn eligible to be captured en passant on the next move.
    private int enPassantPawnX = -1;
    private int enPassantPawnY = -1;
    private int pendingPromotionX = -1;
    private int pendingPromotionY = -1;
    private PieceColor pendingPromotionColor = null;

    public Board() {
        this(true);
    }

    private Board(boolean initializeSquares) {
        if (initializeSquares) {
            resetBoard();
        }
    }

    public Square getBox(int x, int y) {
        if (!isInsideBoard(x, y)) {
            return null;
        }
        return boxes[x][y];
    }

    public boolean isInsideBoard(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public PieceColor getCurrentTurn() {
        return currentTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public PieceColor getWinner() {
        return winner;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public boolean hasPendingPromotion() {
        return pendingPromotionColor != null;
    }

    public boolean movePiece(int startX, int startY, int endX, int endY) {
        if (gameOver) {
            statusMessage = "Game over.";
            return false;
        }

        if (hasPendingPromotion()) {
            statusMessage = "Choose a promotion piece first.";
            return false;
        }

        if (!isInsideBoard(startX, startY) || !isInsideBoard(endX, endY)) {
            statusMessage = "Move is outside the board.";
            return false;
        }

        Square start = getBox(startX, startY);
        Square end = getBox(endX, endY);
        Piece piece = start.getPiece();

        if (piece == null) {
            statusMessage = "No piece selected.";
            return false;
        }

        if (piece.getColor() != currentTurn) {
            statusMessage = "It's " + currentTurn + "'s turn.";
            return false;
        }

        if (!isPseudoLegalMove(startX, startY, endX, endY, piece)) {
            statusMessage = "Illegal move for " + piece.getSymbol() + ".";
            return false;
        }

        Board simulation = deepCopy();
        simulation.applyMoveUnchecked(startX, startY, endX, endY);
        if (simulation.isKingInCheck(currentTurn)) {
            statusMessage = "Illegal move: your king would be in check.";
            return false;
        }

        applyMoveUnchecked(startX, startY, endX, endY);
        currentTurn = opposite(currentTurn);

        if (hasPendingPromotion()) {
            statusMessage = "Promote " + pendingPromotionColor + " pawn.";
            return true;
        }

        updateGameStateAfterTurn();
        return true;
    }

    public boolean promotePendingPawn(String pieceName) {
        if (!hasPendingPromotion()) {
            return false;
        }

        Square promotionSquare = getBox(pendingPromotionX, pendingPromotionY);
        Piece promotedPiece = createPromotionPiece(pieceName, pendingPromotionColor);
        if (promotedPiece == null) {
            return false;
        }

        promotedPiece.setHasMoved(true);
        promotionSquare.setPiece(promotedPiece);

        pendingPromotionX = -1;
        pendingPromotionY = -1;
        pendingPromotionColor = null;

        updateGameStateAfterTurn();
        return true;
    }

    private Piece createPromotionPiece(String pieceName, PieceColor color) {
        if (pieceName == null) {
            return new Queen(color);
        }

        String normalized = pieceName.trim().toUpperCase();
        if ("ROOK".equals(normalized)) {
            return new Rook(color);
        }
        if ("BISHOP".equals(normalized)) {
            return new Bishop(color);
        }
        if ("KNIGHT".equals(normalized)) {
            return new Knight(color);
        }
        return new Queen(color);
    }

    private void updateGameStateAfterTurn() {
        boolean opponentInCheck = isKingInCheck(currentTurn);
        boolean opponentHasMove = hasAnyLegalMoves(currentTurn);

        if (!opponentHasMove && opponentInCheck) {
            gameOver = true;
            winner = opposite(currentTurn);
            statusMessage = "Checkmate. " + winner + " wins.";
        } else if (!opponentHasMove) {
            gameOver = true;
            winner = null;
            statusMessage = "Stalemate.";
        } else if (opponentInCheck) {
            statusMessage = "Check on " + currentTurn + ".";
        } else {
            statusMessage = "Turn: " + currentTurn;
        }
    }

    public boolean isPathClearStraight(Square start, Square end) {
        if (start.getX() == end.getX()) {
            int yStep = end.getY() > start.getY() ? 1 : -1;
            for (int y = start.getY() + yStep; y != end.getY(); y += yStep) {
                if (getBox(start.getX(), y).getPiece() != null) {
                    return false;
                }
            }
            return true;
        }

        if (start.getY() == end.getY()) {
            int xStep = end.getX() > start.getX() ? 1 : -1;
            for (int x = start.getX() + xStep; x != end.getX(); x += xStep) {
                if (getBox(x, start.getY()).getPiece() != null) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public boolean isPathClearDiagonal(Square start, Square end) {
        int xDiff = end.getX() - start.getX();
        int yDiff = end.getY() - start.getY();

        if (Math.abs(xDiff) != Math.abs(yDiff) || xDiff == 0) {
            return false;
        }

        int xStep = xDiff > 0 ? 1 : -1;
        int yStep = yDiff > 0 ? 1 : -1;

        int x = start.getX() + xStep;
        int y = start.getY() + yStep;

        while (x != end.getX() && y != end.getY()) {
            if (getBox(x, y).getPiece() != null) {
                return false;
            }
            x += xStep;
            y += yStep;
        }

        return true;
    }

    public void resetBoard() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                boxes[x][y] = new Square(x, y, null);
            }
        }

        currentTurn = PieceColor.WHITE;
        gameOver = false;
        winner = null;
        statusMessage = "Turn: WHITE";
        enPassantPawnX = -1;
        enPassantPawnY = -1;
        pendingPromotionX = -1;
        pendingPromotionY = -1;
        pendingPromotionColor = null;
    }

    public void setupStartingPosition() {
        resetBoard();

        for (int y = 0; y < 8; y++) {
            getBox(6, y).setPiece(new Pawn(PieceColor.WHITE));
            getBox(1, y).setPiece(new Pawn(PieceColor.BLACK));
        }

        // White back rank (row 7): two rooks at both corners.
        getBox(7, 0).setPiece(new Rook(PieceColor.WHITE));
        getBox(7, 1).setPiece(new Knight(PieceColor.WHITE));
        getBox(7, 2).setPiece(new Bishop(PieceColor.WHITE));
        getBox(7, 3).setPiece(new Queen(PieceColor.WHITE));
        getBox(7, 4).setPiece(new King(PieceColor.WHITE));
        getBox(7, 5).setPiece(new Bishop(PieceColor.WHITE));
        getBox(7, 6).setPiece(new Knight(PieceColor.WHITE));
        getBox(7, 7).setPiece(new Rook(PieceColor.WHITE));

        // Black back rank (row 0): two rooks at both corners.
        getBox(0, 0).setPiece(new Rook(PieceColor.BLACK));
        getBox(0, 1).setPiece(new Knight(PieceColor.BLACK));
        getBox(0, 2).setPiece(new Bishop(PieceColor.BLACK));
        getBox(0, 3).setPiece(new Queen(PieceColor.BLACK));
        getBox(0, 4).setPiece(new King(PieceColor.BLACK));
        getBox(0, 5).setPiece(new Bishop(PieceColor.BLACK));
        getBox(0, 6).setPiece(new Knight(PieceColor.BLACK));
        getBox(0, 7).setPiece(new Rook(PieceColor.BLACK));
    }

    public void setupDemoPieces() {
        setupStartingPosition();
    }

    private boolean isPseudoLegalMove(int startX, int startY, int endX, int endY, Piece piece) {
        if (startX == endX && startY == endY) {
            return false;
        }

        Square start = getBox(startX, startY);
        Square end = getBox(endX, endY);
        Piece endPiece = end.getPiece();
        if (endPiece != null && endPiece.getColor() == piece.getColor()) {
            return false;
        }
        if (endPiece instanceof King) {
            return false;
        }

        if (piece instanceof Pawn) {
            return isPseudoLegalPawnMove(start, end, piece);
        }

        if (piece instanceof King && Math.abs(endY - startY) == 2 && startX == endX) {
            return canCastle(startX, startY, endY, piece);
        }

        return piece.isValidMove(this, start, end);
    }

    private boolean isPseudoLegalPawnMove(Square start, Square end, Piece pawn) {
        int direction = pawn.getColor() == PieceColor.WHITE ? -1 : 1;
        int startRow = pawn.getColor() == PieceColor.WHITE ? 6 : 1;

        int xDiff = end.getX() - start.getX();
        int yDiff = end.getY() - start.getY();

        if (yDiff == 0) {
            if (xDiff == direction && end.getPiece() == null) {
                return true;
            }
            if (start.getX() == startRow && xDiff == 2 * direction && end.getPiece() == null) {
                Square middle = getBox(start.getX() + direction, start.getY());
                return middle.getPiece() == null;
            }
            return false;
        }

        if (Math.abs(yDiff) == 1 && xDiff == direction) {
            if (end.getPiece() != null && end.getPiece().getColor() != pawn.getColor()) {
                return true;
            }

            if (end.getPiece() == null && enPassantPawnX == start.getX() && enPassantPawnY == end.getY()) {
                Piece adjacent = getBox(enPassantPawnX, enPassantPawnY).getPiece();
                return adjacent instanceof Pawn && adjacent.getColor() != pawn.getColor();
            }
        }

        return false;
    }

    private boolean canCastle(int row, int kingY, int endY, Piece kingPiece) {
        if (!(kingPiece instanceof King) || kingPiece.hasMoved()) {
            return false;
        }

        if (isKingInCheck(kingPiece.getColor())) {
            return false;
        }

        int rookY;
        int step;
        if (endY == 6) {
            rookY = 7;
            step = 1;
        } else if (endY == 2) {
            rookY = 0;
            step = -1;
        } else {
            return false;
        }

        Square rookSquare = getBox(row, rookY);
        if (rookSquare == null || !(rookSquare.getPiece() instanceof Rook)) {
            return false;
        }

        Piece rookPiece = rookSquare.getPiece();
        if (rookPiece.getColor() != kingPiece.getColor() || rookPiece.hasMoved()) {
            return false;
        }

        for (int y = kingY + step; y != rookY; y += step) {
            if (getBox(row, y).getPiece() != null) {
                return false;
            }
        }

        PieceColor enemy = opposite(kingPiece.getColor());
        for (int y = kingY + step; y != endY + step; y += step) {
            if (isSquareUnderAttack(row, y, enemy)) {
                return false;
            }
        }

        return true;
    }

    private void applyMoveUnchecked(int startX, int startY, int endX, int endY) {
        Square start = getBox(startX, startY);
        Square end = getBox(endX, endY);
        Piece piece = start.getPiece();

        int previousEnPassantX = enPassantPawnX;
        int previousEnPassantY = enPassantPawnY;

        enPassantPawnX = -1;
        enPassantPawnY = -1;

        if (piece instanceof King && Math.abs(endY - startY) == 2 && startX == endX) {
            int rookStartY = endY == 6 ? 7 : 0;
            int rookEndY = endY == 6 ? 5 : 3;

            Square rookStart = getBox(startX, rookStartY);
            Square rookEnd = getBox(startX, rookEndY);
            Piece rookPiece = rookStart.getPiece();

            rookEnd.setPiece(rookPiece);
            rookStart.setPiece(null);
            if (rookPiece != null) {
                rookPiece.setHasMoved(true);
            }
        }

        if (piece instanceof Pawn && end.getPiece() == null && Math.abs(endY - startY) == 1) {
            if (previousEnPassantX == startX && previousEnPassantY == endY) {
                Square capturedPawnSquare = getBox(previousEnPassantX, previousEnPassantY);
                Piece capturedPawn = capturedPawnSquare.getPiece();
                if (capturedPawn != null) {
                    capturedPawn.setKilled(true);
                }
                capturedPawnSquare.setPiece(null);
            }
        }

        if (end.getPiece() != null) {
            end.getPiece().setKilled(true);
        }

        end.setPiece(piece);
        start.setPiece(null);
        piece.setHasMoved(true);

        if (piece instanceof Pawn && Math.abs(endX - startX) == 2) {
            enPassantPawnX = endX;
            enPassantPawnY = endY;
        }

        if (piece instanceof Pawn) {
            if ((piece.getColor() == PieceColor.WHITE && endX == 0)
                    || (piece.getColor() == PieceColor.BLACK && endX == 7)) {
                pendingPromotionX = endX;
                pendingPromotionY = endY;
                pendingPromotionColor = piece.getColor();
            }
        }
    }

    private PieceColor opposite(PieceColor color) {
        return color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
    }

    private boolean isKingInCheck(PieceColor kingColor) {
        Square kingSquare = findKing(kingColor);
        if (kingSquare == null) {
            return true;
        }

        return isSquareUnderAttack(kingSquare.getX(), kingSquare.getY(), opposite(kingColor));
    }

    private Square findKing(PieceColor color) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = getBox(x, y).getPiece();
                if (piece instanceof King && piece.getColor() == color) {
                    return getBox(x, y);
                }
            }
        }
        return null;
    }

    private boolean isSquareUnderAttack(int targetX, int targetY, PieceColor attackerColor) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = getBox(x, y).getPiece();
                if (piece == null || piece.getColor() != attackerColor) {
                    continue;
                }
                if (canPieceAttackSquare(x, y, targetX, targetY, piece)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canPieceAttackSquare(int startX, int startY, int targetX, int targetY, Piece piece) {
        Square start = getBox(startX, startY);
        Square target = getBox(targetX, targetY);
        int xDiff = targetX - startX;
        int yDiff = targetY - startY;

        if (piece instanceof Pawn) {
            int direction = piece.getColor() == PieceColor.WHITE ? -1 : 1;
            return xDiff == direction && Math.abs(yDiff) == 1;
        }

        if (piece instanceof Knight) {
            return Math.abs(xDiff) * Math.abs(yDiff) == 2;
        }

        if (piece instanceof Bishop) {
            return isDiagonalAttackWithClearPath(start, target);
        }

        if (piece instanceof Rook) {
            return isStraightAttackWithClearPath(start, target);
        }

        if (piece instanceof Queen) {
            return isStraightAttackWithClearPath(start, target)
                    || isDiagonalAttackWithClearPath(start, target);
        }

        if (piece instanceof King) {
            return Math.abs(xDiff) <= 1 && Math.abs(yDiff) <= 1 && !(xDiff == 0 && yDiff == 0);
        }

        return false;
    }

    private boolean isStraightAttackWithClearPath(Square start, Square target) {
        if (start.getX() != target.getX() && start.getY() != target.getY()) {
            return false;
        }
        return isPathClearStraight(start, target);
    }

    private boolean isDiagonalAttackWithClearPath(Square start, Square target) {
        int xDiff = Math.abs(target.getX() - start.getX());
        int yDiff = Math.abs(target.getY() - start.getY());
        if (xDiff != yDiff || xDiff == 0) {
            return false;
        }
        return isPathClearDiagonal(start, target);
    }

    private boolean hasAnyLegalMoves(PieceColor color) {
        for (int startX = 0; startX < 8; startX++) {
            for (int startY = 0; startY < 8; startY++) {
                Piece piece = getBox(startX, startY).getPiece();
                if (piece == null || piece.getColor() != color) {
                    continue;
                }

                for (int endX = 0; endX < 8; endX++) {
                    for (int endY = 0; endY < 8; endY++) {
                        if (!isPseudoLegalMove(startX, startY, endX, endY, piece)) {
                            continue;
                        }

                        Board simulation = deepCopy();
                        simulation.applyMoveUnchecked(startX, startY, endX, endY);
                        if (!simulation.isKingInCheck(color)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Board deepCopy() {
        Board copy = new Board(false);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece original = getBox(x, y).getPiece();
                Piece cloned = clonePiece(original);
                copy.boxes[x][y] = new Square(x, y, cloned);
            }
        }

        copy.currentTurn = currentTurn;
        copy.gameOver = gameOver;
        copy.winner = winner;
        copy.statusMessage = statusMessage;
        copy.enPassantPawnX = enPassantPawnX;
        copy.enPassantPawnY = enPassantPawnY;
        copy.pendingPromotionX = pendingPromotionX;
        copy.pendingPromotionY = pendingPromotionY;
        copy.pendingPromotionColor = pendingPromotionColor;

        return copy;
    }

    private Piece clonePiece(Piece piece) {
        if (piece == null) {
            return null;
        }

        Piece clone;
        if (piece instanceof Pawn) {
            clone = new Pawn(piece.getColor());
        } else if (piece instanceof Knight) {
            clone = new Knight(piece.getColor());
        } else if (piece instanceof Bishop) {
            clone = new Bishop(piece.getColor());
        } else if (piece instanceof Rook) {
            clone = new Rook(piece.getColor());
        } else if (piece instanceof Queen) {
            clone = new Queen(piece.getColor());
        } else if (piece instanceof King) {
            clone = new King(piece.getColor());
        } else {
            return null;
        }

        clone.setKilled(piece.isKilled());
        clone.setHasMoved(piece.hasMoved());
        return clone;
    }
}
