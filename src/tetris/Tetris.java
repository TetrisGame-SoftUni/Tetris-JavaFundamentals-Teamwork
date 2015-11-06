package tetris;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;


public class Tetris extends JFrame {

    JLabel statusbar;

    public Tetris() {

        this.statusbar = new JLabel("Score: 0");
        add(this.statusbar, BorderLayout.PAGE_START);
        this.statusbar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(this.statusbar, BorderLayout.SOUTH);
        this.statusbar.setPreferredSize(new Dimension(getWidth(), 16));
        Board board = new Board(this);
        add(board);
        board.start();

        setSize(300, 600);
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    public JLabel getStatusBar() {
        return this.statusbar;
    }

    public static void main(String[] args) {
        Tetris game = new Tetris();
        game.setLocationRelativeTo(null);
        game.setVisible(true);
        game.setResizable(false);
    }
}