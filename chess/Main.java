import core.Board;
import gui.ChessGUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting up the Chess Engine...");
        
        Board board = new Board();
        board.setupStartingPosition();
        System.out.println("Starting pieces placed.");

        System.out.println("Launching Chess GUI...");
        new ChessGUI(board);
    }
}
