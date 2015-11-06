package tetris;

import tetris.Shape.Tetrominoes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel implements ActionListener {

    final int BoardWidth = 10;
    final int BoardHeight = 22;

    Timer timer;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    boolean isPaused = false;
    int numLinesRemoved = 0;
    int curX = 0;
    int curY = 0;
    JLabel statusbar;
    Shape curPiece;
    Tetrominoes[] board;

    public Board(Tetris parent) {

        setFocusable(true);
        this.curPiece = new Shape();
        this.timer = new Timer(400, this);
        this.timer.start();
        this.setBackground(Color.GRAY);
        this.statusbar =  parent.getStatusBar();
        this.board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();
    }

    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    int squareWidth() { return (int) getSize().getWidth() / BoardWidth; }
    int squareHeight() { return (int) getSize().getHeight() / BoardHeight; }
    Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }

    public void start() {
        if (this.isPaused)
            return;

        this.isStarted = true;
        this.isFallingFinished = false;
        this.numLinesRemoved = 0;
        clearBoard();
        newPiece();
        this.timer.start();
    }
    private void pause() {
        if (!isStarted)
            return;

        this.isPaused = !this.isPaused;
        if (this.isPaused) {
            this.timer.stop();
            this.statusbar.setText("paused");
        } else {
            this.timer.start();
            this.statusbar.setText("Score: "+String.valueOf(numLinesRemoved));
        }
        repaint();
    }
    public void paint(Graphics g) {
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - this.BoardHeight * squareHeight();


        for (int i = 0; i < this.BoardHeight; ++i) {
            for (int j = 0; j < this.BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, this.BoardHeight - i - 1);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
            }
        }

        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = this.curX + this.curPiece.x(i);
                int y = this.curY - this.curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                        boardTop + (this.BoardHeight - y - 1) * squareHeight(),
                        this.curPiece.getShape());
            }
        }
    }
    private void dropDown() {
        int newY = this.curY;
        while (newY > 0) {
            if (!tryMove(this.curPiece, this.curX, newY - 1))
                break;
            --newY;
        }
        pieceDropped();
    }
    private void clearBoard() {
        for (int i = 0; i < this.BoardHeight * this.BoardWidth; ++i)
            this.board[i] = Tetrominoes.NoShape;
    }
    private void oneLineDown()
    {
        if (!tryMove(this.curPiece, this.curX, this.curY - 1))
            pieceDropped();
    }
    private void pieceDropped()
    {
        for (int i = 0; i < 4; ++i) {
            int x = this.curX + this.curPiece.x(i);
            int y = this.curY - this.curPiece.y(i);
            this.board[(y * this.BoardWidth) + x] = this.curPiece.getShape();
        }

        removeFullLines();

        if (!this.isFallingFinished)
            newPiece();
    }
    private void newPiece() {
        this.curPiece.setRandomShape();
        this.curX = BoardWidth / 2 + 1;
        this.curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(this.curPiece, this.curX, this.curY)) {
            this.curPiece.setShape(Tetrominoes.NoShape);
            this.timer.stop();
            this.isStarted = false;
            this.statusbar.setText("game over");
        }
    }
    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= this.BoardWidth || y < 0 || y >= this.BoardHeight)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        this.curPiece = newPiece;
        this.curX = newX;
        this.curY = newY;
        repaint();
        return true;
    }
    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = this.BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < this.BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < this.BoardHeight - 1; ++k) {
                    for (int j = 0; j < this.BoardWidth; ++j)
                        this.board[(k * this.BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            this.numLinesRemoved += numFullLines;
            this.statusbar.setText("Score: "+String.valueOf(numLinesRemoved));
            this.isFallingFinished = true;
            this.curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color colors[] = { new Color(40, 100, 255), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };


        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }
    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {

            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (isPaused)
                return;

            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateShape(), curX, curY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case KeyEvent.VK_DOWN:
                    oneLineDown();
                    break;
                case KeyEvent.VK_R:
                    start();
                    break;
            }

        }
    }
}