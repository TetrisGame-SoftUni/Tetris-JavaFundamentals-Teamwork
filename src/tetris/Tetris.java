package tetris;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JLabel;


public class Tetris extends JFrame {

    JLabel statusbar;

    public Tetris() {

        statusbar = new JLabel("Scores: 0");
        add(statusbar, BorderLayout.PAGE_START);
        Board board = new Board(this);
        add(board);
        board.start();

        setSize(300, 600);
        setFocusable(true);
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    public JLabel getStatusBar() {
        return statusbar;
    }

    public static void main(String[] args) {

        Tetris game = new Tetris();
        game.setLocationRelativeTo(null);
        game.setVisible(true);

    }
}