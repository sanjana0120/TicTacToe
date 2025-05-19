import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TicTacToeWithScore extends JFrame implements ActionListener {
    private JButton[][] buttons = new JButton[3][3];
    private JButton restartButton, modeButton;
    private JLabel scoreLabel;
    private char currentPlayer = 'X';
    private boolean gameEnded = false;
    private boolean vsAI = false;

    private int xWins = 0;
    private int oWins = 0;
    private int draws = 0;

    public TicTacToeWithScore() {
        setTitle("Tic-Tac-Toe Game (With Scoreboard)");
        setSize(430, 570);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel(new GridLayout(3, 3)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 30, 60),
                        getWidth(), getHeight(), new Color(70, 70, 100));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        initializeButtons(gamePanel);
        add(gamePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(3, 1));
        bottomPanel.setBackground(new Color(40, 40, 70));

        scoreLabel = new JLabel("", JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setForeground(Color.WHITE);
        updateScoreLabel();

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 18));
        restartButton.setBackground(new Color(50, 120, 180));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(BorderFactory.createRaisedBevelBorder());
        restartButton.addActionListener(e -> resetGame());

        modeButton = new JButton("Mode: 2 Players");
        modeButton.setFont(new Font("Arial", Font.BOLD, 18));
        modeButton.setBackground(new Color(100, 80, 180));
        modeButton.setForeground(Color.WHITE);
        modeButton.setFocusPainted(false);
        modeButton.setBorder(BorderFactory.createRaisedBevelBorder());
        modeButton.addActionListener(e -> toggleMode());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(40, 40, 70));
        buttonPanel.add(restartButton);
        buttonPanel.add(modeButton);

        bottomPanel.add(scoreLabel);
        bottomPanel.add(buttonPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void initializeButtons(JPanel panel) {
        Font font = new Font("Verdana", Font.BOLD, 50);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton("");
                btn.setFont(font);
                btn.setFocusPainted(false);
                btn.setBackground(new Color(220, 220, 255));
                btn.setBorder(BorderFactory.createRaisedBevelBorder());
                btn.setForeground(Color.DARK_GRAY);
                btn.addActionListener(this);

                buttons[i][j] = btn;
                panel.add(btn);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameEnded) return;

        JButton clicked = (JButton) e.getSource();
        if (!clicked.getText().equals("")) return;

        clicked.setText(String.valueOf(currentPlayer));
        clicked.setForeground(currentPlayer == 'X' ? Color.RED : Color.BLUE);

        if (checkWin()) {
            JOptionPane.showMessageDialog(this, "Player " + currentPlayer + " wins!");
            if (currentPlayer == 'X') xWins++;
            else oWins++;
            gameEnded = true;
            updateScoreLabel();
            return;
        } else if (isDraw()) {
            JOptionPane.showMessageDialog(this, "It's a draw!");
            draws++;
            gameEnded = true;
            updateScoreLabel();
            return;
        }

        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';

        if (vsAI && currentPlayer == 'O') {
            aiMove();
        }
    }

    private void aiMove() {
        Timer timer = new Timer(400, evt -> {
            Point move = bestMove();
            if (move != null) {
                buttons[move.x][move.y].doClick();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private Point bestMove() {
        // Try to win
        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) {
            if (buttons[i][j].getText().equals("")) {
                buttons[i][j].setText("O");
                if (checkWin()) {
                    buttons[i][j].setText("");
                    return new Point(i, j);
                }
                buttons[i][j].setText("");
            }
        }
        // Try to block player
        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) {
            if (buttons[i][j].getText().equals("")) {
                buttons[i][j].setText("X");
                if (checkWin()) {
                    buttons[i][j].setText("");
                    return new Point(i, j);
                }
                buttons[i][j].setText("");
            }
        }
        // Otherwise first available
        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) {
            if (buttons[i][j].getText().equals("")) {
                return new Point(i, j);
            }
        }
        return null;
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (checkEqual(buttons[i][0], buttons[i][1], buttons[i][2])) return true;
            if (checkEqual(buttons[0][i], buttons[1][i], buttons[2][i])) return true;
        }
        return checkEqual(buttons[0][0], buttons[1][1], buttons[2][2]) ||
               checkEqual(buttons[0][2], buttons[1][1], buttons[2][0]);
    }

    private boolean isDraw() {
        for (JButton[] row : buttons)
            for (JButton btn : row)
                if (btn.getText().equals("")) return false;
        return true;
    }

    private boolean checkEqual(JButton b1, JButton b2, JButton b3) {
        return !b1.getText().equals("") &&
               b1.getText().equals(b2.getText()) &&
               b2.getText().equals(b3.getText());
    }

    private void resetGame() {
        gameEnded = false;
        currentPlayer = 'X';
        for (JButton[] row : buttons)
            for (JButton btn : row) {
                btn.setText("");
                btn.setBackground(new Color(220, 220, 255));
                btn.setForeground(Color.DARK_GRAY);
            }
    }

    private void toggleMode() {
        vsAI = !vsAI;
        modeButton.setText(vsAI ? "Mode: Vs Computer" : "Mode: 2 Players");
        resetGame();
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score - X: " + xWins + " | O: " + oWins + " | Draws: " + draws);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeWithScore::new);
    }
}