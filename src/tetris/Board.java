package tetris;

import tetris.Shape.Tetrominoes;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board extends JPanel implements ActionListener {

    private final int BOARD_CELL_WIDTH = 10;
    private final int BOARD_CELL_HEIGHT = 22;
    private Image imageBackground;
    private boolean isFalled = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int currX = 0;
    private int currY = 0;

    Timer mainTimer;
    JLabel statusBar;
    Shape curPiece;
    Tetrominoes[] board;

    public Board(Tetris parent) {
        initBoard();
        setFocusable(true);
        this.curPiece = new Shape();
        this.mainTimer = new Timer(400, this);
        this.mainTimer.start();
        this.setBackground(Color.GRAY);
        this.statusBar =  parent.getStatusBar();
        this.board = new Tetrominoes[BOARD_CELL_WIDTH * BOARD_CELL_HEIGHT];
        addKeyListener(new TAdapter());
        clearBoard();
    }
    int squareWidth() {
        return (int) getSize().getWidth() / BOARD_CELL_WIDTH; }
    int squareHeight() {
        return (int) getSize().getHeight() / BOARD_CELL_HEIGHT; }
    Tetrominoes shapeAt(int x, int y) {
        return board[(y * BOARD_CELL_WIDTH) + x]; }

    public void start() {
        if (this.isPaused)
            return;
        this.isStarted = true;
        this.isFalled = false;
        this.numLinesRemoved = 0;
        clearBoard();
        newPiece();
        this.mainTimer.start();
    }

    private void initBoard() {
        loadImage();
        int width = imageBackground.getWidth(this);
        int height = imageBackground.getHeight(this);
        setPreferredSize(new Dimension(width, height));
    }
    //Load image
    private void loadImage() {
        ImageIcon ii = new ImageIcon("res/Tetris.jpg");
        imageBackground = ii.getImage();
    }
    @Override //Slice image
    public void paintComponent(Graphics g) {
        g.drawImage(imageBackground, 0, 0, null);
    }
    public void actionPerformed(ActionEvent e) {
        if (isFalled) {
            isFalled = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }
    private void pause() {
        if (!isStarted)
            return;

        this.isPaused = !this.isPaused;
        if (this.isPaused) {
            this.mainTimer.stop();
            this.statusBar.setText("paused");
        } else {
            this.mainTimer.start();
            this.statusBar.setText("Score: "+String.valueOf(numLinesRemoved));
        }
        repaint();
    }
    public void paint(Graphics g) {
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - this.BOARD_CELL_HEIGHT * squareHeight();


        for (int i = 0; i < this.BOARD_CELL_HEIGHT; ++i) {
            for (int j = 0; j < this.BOARD_CELL_WIDTH; ++j) {
                Tetrominoes shape = shapeAt(j, this.BOARD_CELL_HEIGHT - i - 1);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
            }
        }

        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = this.currX + this.curPiece.x(i);
                int y = this.currY - this.curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                        boardTop + (this.BOARD_CELL_HEIGHT - y - 1) * squareHeight(),
                        this.curPiece.getShape());
            }
        }
    }
    private void dropDown() {
        int newY = this.currY;
        while (newY > 0) {
            if (!tryMove(this.curPiece, this.currX, newY - 1))
                break;
            --newY;
        }
        pieceDropped();
    }
    private void clearBoard() {
        for (int i = 0; i < this.BOARD_CELL_HEIGHT * this.BOARD_CELL_WIDTH; ++i)
            this.board[i] = Tetrominoes.NoShape;
    }
    private void oneLineDown() {
        if (!tryMove(this.curPiece, this.currX, this.currY - 1))
            pieceDropped();
    }
    private void pieceDropped() {
        for (int i = 0; i < 4; ++i) {
            int x = this.currX + this.curPiece.x(i);
            int y = this.currY - this.curPiece.y(i);
            this.board[(y * this.BOARD_CELL_WIDTH) + x] = this.curPiece.getShape();
        }

        removeFullLines();

        if (!this.isFalled)
            newPiece();
    }
    private void newPiece() {
        this.curPiece.setRandomShape();
        this.currX = BOARD_CELL_WIDTH / 2 + 1;
        this.currY = BOARD_CELL_HEIGHT - 1 + curPiece.minY();

        if (!tryMove(this.curPiece, this.currX, this.currY)) {
            this.curPiece.setShape(Tetrominoes.NoShape);
            this.mainTimer.stop();
            this.isStarted = false;
            this.statusBar.setText("Game Over");
        }
    }
    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= this.BOARD_CELL_WIDTH || y < 0 || y >= this.BOARD_CELL_HEIGHT)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        this.curPiece = newPiece;
        this.currX = newX;
        this.currY = newY;
        repaint();
        return true;
    }
    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = this.BOARD_CELL_HEIGHT - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < this.BOARD_CELL_WIDTH; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < this.BOARD_CELL_HEIGHT - 1; ++k) {
                    for (int j = 0; j < this.BOARD_CELL_WIDTH; ++j)
                        this.board[(k * this.BOARD_CELL_WIDTH) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            this.numLinesRemoved += numFullLines;
            this.statusBar.setText("Score: "+String.valueOf(numLinesRemoved));
            this.isFalled = true;
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
                    tryMove(curPiece, currX - 1, currY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, currX + 1, currY);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateShape(), currX, currY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case KeyEvent.VK_DOWN:
                    oneLineDown();
                    break;
                case KeyEvent.VK_R:
                    start();
                    statusBar.setText("Score: 0");
                    break;
            }

        }
    }
}
