package gui;

import core.Board;
import core.Square;
import pieces.Piece;
import pieces.PieceColor;

import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;

public class ChessGUI {
    private JFrame frame;
    private Board board;
    private PieceButton[][] squareButtons = new PieceButton[8][8];
    private JLabel statusLabel;
    private int selectedRow = -1;
    private int selectedCol = -1;

    public ChessGUI(Board board) {
        this.board = board;
        
        frame = new JFrame("My Java Chess Game");
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        statusLabel = new JLabel(board.getStatusMessage() + ". Select a piece.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        frame.add(statusLabel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        frame.add(boardPanel, BorderLayout.CENTER);
        buildBoard(boardPanel);
        renderBoard();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void buildBoard(JPanel boardPanel) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final int clickedRow = row;
                final int clickedCol = col;

                PieceButton squareButton = new PieceButton();
                squareButton.setFont(new Font("Serif", Font.PLAIN, 42));
                squareButton.setFocusPainted(false);
                squareButton.addActionListener(e -> onSquareClick(clickedRow, clickedCol));

                squareButtons[row][col] = squareButton;
                boardPanel.add(squareButton);
            }
        }
    }

    private void onSquareClick(int row, int col) {
        Square clickedSquare = board.getBox(row, col);

        if (selectedRow == -1) {
            if (clickedSquare.getPiece() == null) {
                statusLabel.setText("Select a " + board.getCurrentTurn() + " piece first.");
                return;
            }

            if (clickedSquare.getPiece().getColor() != board.getCurrentTurn()) {
                statusLabel.setText("It's " + board.getCurrentTurn() + "'s turn.");
                return;
            }

            selectedRow = row;
            selectedCol = col;
            statusLabel.setText("Selected " + clickedSquare.getPiece().getSymbol() + " at (" + row + ", " + col + "). Choose destination.");
            renderBoard();
            return;
        }

        if (selectedRow == row && selectedCol == col) {
            clearSelection("Selection cleared.");
            return;
        }

        Square startSquare = board.getBox(selectedRow, selectedCol);
        Piece movingPiece = startSquare.getPiece();
        if (movingPiece == null) {
            clearSelection("Selected piece no longer exists. Select again.");
            return;
        }

        if (board.movePiece(selectedRow, selectedCol, row, col)) {
            if (board.hasPendingPromotion()) {
                showPromotionDialog();
            }
            clearSelection(board.getStatusMessage());
            return;
        }

        clearSelection(board.getStatusMessage());
    }

    private void clearSelection(String message) {
        selectedRow = -1;
        selectedCol = -1;
        statusLabel.setText(message);
        renderBoard();
    }

    private void showPromotionDialog() {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(
                frame,
                "Choose a piece for pawn promotion:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        String picked = choice >= 0 ? options[choice] : "Queen";
        board.promotePendingPawn(picked);
    }

    private void renderBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                PieceButton button = squareButtons[row][col];
                boolean isSelected = (row == selectedRow && col == selectedCol);
                Square square = board.getBox(row, col);
                Piece piece = square.getPiece();

                button.setBackground(getSquareColor(row, col, isSelected));
                button.setOpaque(true);
                button.setContentAreaFilled(true);
                button.setBorderPainted(false);
                button.setPiece(piece);
            }
        }
    }

    private Color getSquareColor(int row, int col, boolean isSelected) {
        if (isSelected) {
            return new Color(246, 232, 143);
        }
        if ((row + col) % 2 == 0) {
            return Color.WHITE;
        }
        return Color.BLACK;
    }

    private static class PieceButton extends JButton {
        private Piece piece;

        void setPiece(Piece piece) {
            this.piece = piece;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (piece == null) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());

            String symbol = piece.getSymbol();
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(symbol);
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            GlyphVector glyph = getFont().createGlyphVector(g2.getFontRenderContext(), symbol);
            Shape shape = glyph.getOutline(x, y);

            if (piece.getColor() == PieceColor.BLACK) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.0f));
                g2.draw(shape);
                g2.setColor(Color.BLACK);
                g2.fill(shape);
            } else {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2.0f));
                g2.draw(shape);
                g2.setColor(Color.WHITE);
                g2.fill(shape);
            }

            g2.dispose();
        }
    }
}
