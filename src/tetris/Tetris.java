package tetris;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;

public class Tetris extends JFrame {

    JLabel statusbar;

    public Tetris() {
        statusbar = new JLabel("Score: 0");
        add(this.statusbar, BorderLayout.PAGE_START);
        statusbar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(statusbar, BorderLayout.SOUTH);
        statusbar.setPreferredSize(new Dimension(getWidth(), 16));
        Board board = new Board(this);
        add(board);
        board.start();

        setSize(306, 664);
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }
    public JLabel getStatusBar() {
        return this.statusbar;
    }

}