package tetris;

import tetris.Shape.Tetrominoes;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class Board extends JPanel implements ActionListener {


    final int BoardWidth = 10;
    final int BoardHeight = 22;

    Timer timer;
    boolean isFalled = false;
    boolean isStarted = false;
    boolean isPaused = false;
    int linesRemoved = 0;
    int currX = 0;
    int currY = 0;
    JLabel statusbar;
    Shape curPiece;
    Tetrominoes[] board;



    public Board(Tetris parent) {

        setFocusable(true);
        curPiece = new Shape();
        timer = new Timer(400, this);
        timer.start();
<<<<<<< HEAD
        //TODO
        statusbar =  parent.getStatusBar();
        board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        //TODO
        clearBoard();
    }


    private void clearBoard()
    {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Tetrominoes.NoShape;
    }

    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
=======
        statusbar =  parent.getStatusBar();
        board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();
    }

    public void actionPerformed(ActionEvent e) {
        if (isFalled) {
            isFalled = false;
            //newPiece();
>>>>>>> origin/master
        } else {
            oneLineDown();
        }
    }
    private void newPiece()
    {
        curPiece.setRandomShape();
        curX = BoardWidth / 2 + 1;
        curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusbar.setText("Game over!");
        }
    }

    private void oneLineDown()
    {
        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
    }

    private void pieceDropped()
    {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished)
            newPiece();
    }



    int squareWidth() { return (int) getSize().getWidth() / BoardWidth; }
    int squareHeight() { return (int) getSize().getHeight() / BoardHeight; }
    Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }


    public void start()
    {
        if (isPaused)
            return;

        isStarted = true;
        isFalled = false;
        linesRemoved = 0;
        //TODO
        clearBoard();
        //TODO
        newPiece();
        timer.start();
    }
    private void clearBoard()
    {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Tetrominoes.NoShape;
    }

    private boolean tryMove(Shape newPiece, int newX, int newY)
    {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        currX = newX;
        currY = newY;
        repaint();
        return true;
    }

    private void removeFullLines()
    {
        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j)
                        board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
<<<<<<< HEAD
            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf("Scores: " + numLinesRemoved));
            isFallingFinished = true;
=======
            linesRemoved += numFullLines;
            statusbar.setText(String.valueOf(linesRemoved));
            isFalled = true;
>>>>>>> origin/master
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
    }

    private void pause()
    {
        if (!isStarted)
            return;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusbar.setText("Paused");
        } else {
            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }
        repaint();
    }

    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {

            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                //TODO
                pause();
                return;
            }

            if (isPaused)
                return;

            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, currX - 1, currY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, currX + 1, currY);
                    break;
                case KeyEvent.VK_DOWN:
                    //tryMove(curPiece.rotateRight(), currX, currY);
                    break;
                case KeyEvent.VK_UP:
                    //tryMove(curPiece.rotateLeft(), currX, currY);
                    break;
                case KeyEvent.VK_SPACE:
                   // dropDown();
                    break;
                case 'd':
                    //oneLineDown();
                    break;
                case 'D':
                   // oneLineDown();
                    break;
            }

        }
    }
}