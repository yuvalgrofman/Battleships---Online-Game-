package game;

import javax.swing.*;
import java.awt.*;

public class View {

    //creating the image icons for the view class 
    private ImageIcon shipIcon = new ImageIcon("Resources/images/ship.png");
    private ImageIcon oceanIcon = new ImageIcon("Resources/images/ocean.png");
    private ImageIcon hitShipIcon = new ImageIcon("Resources/images/hitShip.png");
    private ImageIcon hitOceanIcon = new ImageIcon("Resources/images/hitOcean.png");

    private JFrame frame = new JFrame();


    private JPanel titlePanel = new JPanel();
    private JTabbedPane buttons = new JTabbedPane();

    private JPanel myTabPanel = new JPanel();
    private JPanel enemyTabPanel = new JPanel();

    private JPanel myButtonPanel = new JPanel();
    private JPanel enemyButtonPanel = new JPanel();
    private JPanel tabSideButtons = new JPanel();
    private JPanel statusPanel = new JPanel();
    private JPanel endGamePanel = new JPanel();
    private JPanel endGameButtonsPanel = new JPanel();

    private JLabel endGameLabel = new JLabel();
    private JLabel playerIdLabel = new JLabel();
    private JLabel scoreLabel = new JLabel();
    private JLabel titleText = new JLabel();
    private JTextArea messagePlayerLabel = new JTextArea();
    private JMenuBar musicMenuBar = new JMenuBar();

    private JMenu musicMenu = new JMenu("music menu");
    private JMenu songs = new JMenu("songs");
    private JMenuItem songZero = new JMenuItem("Beautiful Dream");  
    private JMenuItem songOne = new JMenuItem("Driving Ambition");  
    private JMenuItem songTwo = new JMenuItem("Just Chill");  
    private JCheckBoxMenuItem pauseButton = new JCheckBoxMenuItem("pause");

    private JButton yesButton = new JButton();
    private JButton noButton = new JButton();

    private JButton[][] myButtons;
    private JButton[][] enemyButtons;

    private JButton finishSetup = new JButton();
    private JButton randomizeGrid = new JButton();

    private int[][] myShipsLocation;
    private int[][] enemyShipsLocation;

    private int rowsAndCols;
    private int playerId;

    public View(int rowAndCols, int[][] myShipsLocation, int playerId) {

        this.playerId = playerId;
        this.myShipsLocation = myShipsLocation;
        this.rowsAndCols = rowAndCols;

        myButtons = new JButton[rowAndCols][rowAndCols];
        enemyButtons = new JButton[rowAndCols][rowAndCols];

        // setting the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(1000, 1000);
        frame.setResizable(true);
        frame.setVisible(true);

        // setting the title text
        titleText.setBackground(new Color(25, 25, 25));
        titleText.setForeground(new Color(230, 69, 0));
        titleText.setText("BATTLESHIPS - THE GAME");
        titleText.setFont(new Font("Century Gothic" ,Font.BOLD, 60));
        titleText.setHorizontalAlignment(JLabel.CENTER);
        titleText.setBackground(new Color(50, 50, 50));
        titleText.setOpaque(true);

        // statusBar panel setup
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.PAGE_AXIS));

        // setting the labels for the status bar
        playerIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerIdLabel.setText("              Player #" + playerId);
        playerIdLabel.setHorizontalAlignment(JLabel.CENTER);
        playerIdLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));

        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setText("            You : 0, Enemy : 0");
        scoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        messagePlayerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        //setting up the endGamePanel 
        endGamePanel.setLayout(new BoxLayout(endGamePanel, BoxLayout.Y_AXIS)); 

        endGameLabel.setText("Do you want a rematch?");
        endGameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        endGameButtonsPanel.setLayout(new BoxLayout(endGameButtonsPanel, BoxLayout.X_AXIS));

        yesButton.setText("Yes");
        noButton.setText("No");

        endGameButtonsPanel.add(yesButton);
        endGameButtonsPanel.add(noButton);

        endGamePanel.add(endGameLabel);
        endGamePanel.add(endGameButtonsPanel);

        // setting the layout for a bunch of panels
        titlePanel.setLayout(new BorderLayout());
        myTabPanel.setLayout(new BorderLayout());
        enemyTabPanel.setLayout(new BorderLayout());
        myButtonPanel.setLayout(new GridLayout(rowAndCols, rowAndCols));
        enemyButtonPanel.setLayout(new GridLayout(rowAndCols, rowAndCols));
        tabSideButtons.setLayout(new BoxLayout(tabSideButtons, BoxLayout.PAGE_AXIS));

        //setting up the song menu in the sideButtonPanel
        songs.add(songZero);
        songs.add(songOne);
        songs.add(songTwo);

        musicMenu.add(songs);
        musicMenu.add(pauseButton);

        musicMenuBar.add(musicMenu);

        pauseButton.setEnabled(false);

        for (int i = 0; i < rowAndCols; i++) {
            for (int j = 0; j < rowAndCols; j++) {
                myButtons[i][j] = new JButton();
                enemyButtons[i][j] = new JButton();
                myButtonPanel.add(myButtons[i][j]);
                enemyButtonPanel.add(enemyButtons[i][j]);
                enemyButtons[i][j].setIcon(oceanIcon);

                if (myShipsLocation[j][i] == 1) {
                    myButtons[i][j].setIcon(shipIcon);
                } else {
                    myButtons[i][j].setIcon(oceanIcon);
                }
            }
        }

        // setting the extra buttons for the myTabSideButtons
        finishSetup.setText("Finish Setup");
        finishSetup.setHorizontalAlignment(SwingConstants.CENTER);
        finishSetup.setFont(new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 15));
        finishSetup.setMinimumSize(new Dimension(0, (int) frame.getSize().getHeight() / 10));
        finishSetup.setPreferredSize(new Dimension((int) (frame.getSize().getWidth() / 5), (int) frame.getSize().getHeight() / 13));
        finishSetup.setMaximumSize(new Dimension(1000, (int) frame.getSize().getHeight() / 10));

        randomizeGrid.setText("Randomize Grid");
        randomizeGrid.setHorizontalAlignment(SwingConstants.CENTER);
        randomizeGrid.setFont(new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 15));
        randomizeGrid.setMinimumSize(new Dimension(0, (int) frame.getSize().getHeight() / 10));
        randomizeGrid.setPreferredSize(new Dimension((int) (frame.getSize().getWidth() / 5), (int) frame.getSize().getHeight() / 13));
        randomizeGrid.setMaximumSize(new Dimension(1000, (int) frame.getSize().getHeight() / 10));

        messagePlayerLabel.setMinimumSize(new Dimension(0, 0));
        messagePlayerLabel.setPreferredSize(new Dimension(0,0));
        messagePlayerLabel.setMaximumSize(new Dimension(1000, (int) frame.getSize().getHeight() / 15));


        musicMenuBar.setMinimumSize(new Dimension(0, (int) frame.getSize().getHeight() / 10));
        musicMenuBar.setPreferredSize(new Dimension((int) (frame.getSize().getWidth() / 5), (int) frame.getSize().getHeight() / 13));
        musicMenuBar.setMaximumSize(new Dimension(1000, (int) frame.getSize().getHeight() / 10));
        musicMenu.setHorizontalAlignment(SwingConstants.CENTER);
        musicMenu.setFont(new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 20));
        musicMenu.setMinimumSize(new Dimension(0, (int) frame.getSize().getHeight() / 10));
        musicMenu.setPreferredSize(new Dimension((int) (frame.getSize().getWidth() / 5), (int) frame.getSize().getHeight() / 13));
        musicMenu.setMaximumSize(new Dimension(1000, (int) frame.getSize().getHeight() / 10));

        // setting up status panel
        statusPanel.add(playerIdLabel);
        statusPanel.add(scoreLabel);
        statusPanel.add(messagePlayerLabel);
        statusPanel.add(endGamePanel);

        endGamePanel.setVisible(false);
        messagePlayerLabel.setVisible(false);

        // adding components to myTabSideButtons
        tabSideButtons.add(finishSetup);
        tabSideButtons.add(randomizeGrid);
        tabSideButtons.add(musicMenuBar);
        tabSideButtons.add(statusPanel);

        //adding components to enemy
        enemyTabPanel.add(enemyButtonPanel, BorderLayout.CENTER);

        // adding components to myTabPanel
        myTabPanel.add(myButtonPanel, BorderLayout.CENTER);
        myTabPanel.add(tabSideButtons, BorderLayout.EAST);

        buttons.addTab("your Board", myTabPanel);
        buttons.addTab("enemy Board", enemyTabPanel);
        titlePanel.add(titleText);

        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(buttons, BorderLayout.CENTER);
    }

    public static void addActionListenerButtons(JButton[][] buttons, Object ActionListener) {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                buttons[i][j].addActionListener((java.awt.event.ActionListener) ActionListener);
            }
        }
    }

    public static void addActionListenerSongMenuItems (JMenuItem [] menuItems, Object ActionListener) {
        for (int i = 0; i < menuItems.length; i++) {
            menuItems[i].addActionListener((java.awt.event.ActionListener) ActionListener);
        }
    }

    public JMenuItem[] getSongMenuItems() {
        JMenuItem[] menuItemArray = { songZero, songOne, songTwo };
        return menuItemArray;
    }

    public JCheckBoxMenuItem getPauseButton() {
        return this.pauseButton;
    }

    public JButton getMyButton(int x, int y) {
        return this.myButtons[x][y];
    }

    public JButton getEnemyButton(int x, int y) {
        return this.enemyButtons[x][y];
    }

    public JButton[][] getMyButtons() {
        return this.myButtons;
    }

    public JButton[][] getEnemyButtons() {
        return this.enemyButtons;
    }

    public JButton getFinishSetup() {
        return this.finishSetup;
    }

    public JButton getRandomizeGrid() {
        return this.randomizeGrid;
    }

    public JPanel getMyTabPanel() {
        return this.myTabPanel;
    }

    public JPanel getMyTabSideButtons() {
        return this.tabSideButtons;
    }

    public JFrame getFrame() {
        return this.frame;
    }


    public JMenuBar getMusicMenuBar() {
        return this.musicMenuBar;
    }

    public JMenu getMusicMenu() {
        return this.musicMenu;
    }

    public JMenu getSongs() {
        return this.songs;
    }

    public JMenuItem getSongOne() {
        return this.songZero;
    }

    public JMenuItem getSongTwo() {
        return this.songOne;
    }

    public JMenuItem getSongThree() {
        return this.songTwo;
    }


    public void refreshMyBoard(int[][] myShipsLocation) {
        this.myShipsLocation = myShipsLocation;

        for (int i = 0; i < rowsAndCols; i++) {
            for (int j = 0; j < rowsAndCols; j++) {

                myButtons[j][i].setIcon(null);

                if (myShipsLocation[i][j] == 1) {
                    myButtons[j][i].setIcon(shipIcon);

                } else if (myShipsLocation[i][j] == 0){
                    myButtons[j][i].setIcon(oceanIcon);

                }else if(myShipsLocation[i][j] == -1){
                    myButtons[j][i].setIcon(hitShipIcon);

                } else {
                    myButtons[j][i].setIcon(hitOceanIcon);
                }
            }
        }
    }

    public void refreshEnemyBoard(int[][] enemyShipsLocation) {
        this.enemyShipsLocation = enemyShipsLocation;

        for (int i = 0; i < rowsAndCols; i++) {
            for (int j = 0; j < rowsAndCols; j++) {

                enemyButtons[i][j].setIcon(oceanIcon);

                if (enemyShipsLocation[i][j] == -1) {
                    enemyButtons[i][j].setIcon(hitShipIcon);

                } else if (enemyShipsLocation[i][j] == -2) {
                    enemyButtons[i][j].setIcon(hitOceanIcon);
                }
            }
        }
    }

    public ImageIcon getShipIcon() {
        return this.shipIcon;
    }

    public ImageIcon getOceanIcon() {
        return this.oceanIcon;
    }

    public ImageIcon getHitShipIcon() {
        return this.hitShipIcon;
    }

    public ImageIcon getHitOceanIcon() {
        return this.hitOceanIcon;
    }

    public JPanel getStatusPanel() {
        return this.statusPanel;
    }

    public JTextArea getMessagePlayerLabel() {
        return this.messagePlayerLabel;
    }

    public JLabel getScoreLabel() {
        return this.scoreLabel;
    }

    public JPanel getEndGamePanel() {
        return this.endGamePanel;
    }


    public JButton getYesButton() {
        return this.yesButton;
    }

    public JButton getNoButton() {
        return this.noButton;
    }

    public void resetMessagePlayer() {
        messagePlayerLabel.setText("");
        messagePlayerLabel.setVisible(false);
    }

    public void sendPlayerMessage(Font font, String text) {
        messagePlayerLabel.setText(text);
        messagePlayerLabel.setFont(font);
        messagePlayerLabel.setVisible(true);
    }
}
