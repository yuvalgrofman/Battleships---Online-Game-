import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Font;
import java.awt.event.ActionEvent;

public class Controller {

    
    private boolean setupFinished = false;
    private boolean gameFinished = false;
    private int rowsAndCols = 6;
    private int playerId;
    
    
    private View view;//the view is the gui 
    private ClientSideConnection clientSideConnection;//the CSC is the connection to the sever

    //the clips are used to make noise 
    //MusicClip is in charge of music 
    //and soundEffectsClip is incharge of different sound effects
    private Clip musicClip;
    private Clip soundEffectsClip;

    //URL which hold the music files data
    private URL beautifulDreamURL = this.getClass().getResource("/resources/music/beautiful-dream.wav");
    private URL drivingAmbitionURL = this.getClass().getResource("/resources/music/driving-ambition.wav");
    private URL justChillURL = this.getClass().getResource("/resources/music/just-chill.wav");
    private URL buttonSoundURL = this.getClass().getResource("/resources/Sound Effects/clickMusic.wav");

    /*
     * 1 - ship, -1 - hit ship 0 - ocean -2 - hit ocean
     */
    private int[][] myShipsLocation;
    private int[][] enemyShipsLocation;

    //setting up the initial position of each ship
    private int[][] ship1 = { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 } };
    private int[][] ship3 = { { 0, 1 }, { 1, 1 }, { 2, 1 } };
    private int[][] ship2 = { { 0, 2 }, { 1, 2 }, { 2, 2 } };
    private int[][] ship4 = { { 0, 3 }, { 1, 3 } };
    private int[][] ship5 = { { 3, 3 }, { 4, 3 } };
    private int[][] ship6 = { { 0, 4 }, { 1, 4 } };
    private int[][] ship7 = { { 3, 4 } };
    private int[][] ship8 = { { 5, 4 } };
    private int[][] ship9 = { { 0, 5 } };
    private int[][] ship10 = { { 2, 5 } };

    //setting up an array which holds all of the ships
    private int[][][] ships = { ship1, ship2, ship3, ship4, ship5, ship6, ship7, ship8, ship9, ship10 };

    private int lastButtonPressX;
    private int lastButtonPressY;
    private boolean buttonPressed = false;
    private int[][] lastShipPressed;
    private boolean yourTurn;
    private boolean randomizeSetupTriggered = false;

    int[] enemyClickedLocation;

    private int myWins = 0;
    private int enemyWins = 0;

    private int portNumber;
    private String ipAddress;

    /**
     * Constructor 
     * @param rowsAndCols represents the amount rows and cols each player should have 
     * @param portNumber the port number which will be used for the connection to the server 
     */
    public Controller(int rowsAndCols, int portNumber, String ipAddress) {
        //checks that rows and cols is greater than 6
        if (rowsAndCols < 6) {
            System.out.println("parameter rowsAndCols must be equal or greater than 6");
            throw new IllegalArgumentException();
        }

        this.ipAddress = ipAddress;
        this.portNumber = portNumber;        
        this.rowsAndCols = rowsAndCols;

        //setting up ships location 
        setUpMyShipsLocation();

        //connecting to server
        connectToServer();

        //creating the view with all the necessary parameters
        this.view = new View(rowsAndCols, myShipsLocation, playerId);

        //adding the appropriate ActionListener to each button  
        View.addActionListenerButtons(view.getMyButtons(), new myBoardButtonClickListener());
        View.addActionListenerButtons(view.getEnemyButtons(), new enemyBoardButtonClickListener());
        View.addActionListenerSongMenuItems(view.getSongMenuItems(), new songButtonsClickListener());

        view.getPauseButton().addActionListener(new songButtonsClickListener());
        view.getFinishSetup().addActionListener(new myBoardButtonClickListener());
        view.getRandomizeGrid().addActionListener(new myBoardButtonClickListener());
        view.getYesButton().addActionListener(new myBoardButtonClickListener());
        view.getNoButton().addActionListener(new myBoardButtonClickListener());

        //adding a changeListener to the volume slider
        view.getVolumeSlider().addChangeListener(new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent e) {
                setVolume((float)(view.getVolumeSlider().getValue())/100);
            }
            
        });
    }

    /**
     * setting up the ships in players board 
     */
    public void setUpMyShipsLocation() {

        this.myShipsLocation = new int[rowsAndCols][rowsAndCols];

        // setting the starting location for your ships
        // ship 1 - size 4
        myShipsLocation[0][0] = 1;
        myShipsLocation[1][0] = 1;
        myShipsLocation[2][0] = 1;
        myShipsLocation[3][0] = 1;

        // ship 2 - size 3
        myShipsLocation[0][1] = 1;
        myShipsLocation[1][1] = 1;
        myShipsLocation[2][1] = 1;

        // ship 3 - size 3
        myShipsLocation[0][2] = 1;
        myShipsLocation[1][2] = 1;
        myShipsLocation[2][2] = 1;

        // ship 4 - size 2
        myShipsLocation[0][3] = 1;
        myShipsLocation[1][3] = 1;

        // ship 5 - size 2
        myShipsLocation[3][3] = 1;
        myShipsLocation[4][3] = 1;

        // ship 5 - size 2
        myShipsLocation[0][4] = 1;
        myShipsLocation[1][4] = 1;

        // ship 8 - size 1
        myShipsLocation[3][4] = 1;

        // ship 8 - size 1
        myShipsLocation[5][4] = 1;

        // ship 9 - size 1
        myShipsLocation[0][5] = 1;

        // ship 10 - size 1
        myShipsLocation[2][5] = 1;
    }

    /**
     * creates a 2d array that represents the enemy board and sets one point to 1 so that the program does not believe that the player won 
     * it has no impact on the logic of the code because later this 2d array will be changed 
     */
    public void setUpEnemyShipsLocation() {
        enemyShipsLocation = new int[rowsAndCols][rowsAndCols];
        enemyShipsLocation[0][0] = 1;// assigning a value to an arbitrary point on the board
        // so the DidIWon function doesn't return true

    }

    /**
     * @return true if the setup phase ended
     */
    public boolean isSetupFinished() {
        return this.setupFinished;
    }

    public void setSetupFinished(boolean setupFinished) {
        this.setupFinished = setupFinished;
    }

    public int getRowsAndCols() {
        return this.rowsAndCols;
    }

    public int[][] getMyShipsLocation() {
        return this.myShipsLocation;
    }

    public void setMyShipsLocation(int[][] myShipsLocation) {
        this.myShipsLocation = myShipsLocation;
    }

    /**
     * 
     * @param x the x coordinate of a certain point
     * @param y the y coordinate of a certain point 
     * @return the ship/array which the point is on if the point is on a ship
     * otherwise returns null         
     */
    public int[][] isOnShip(int x, int y) {

        for (int i = 0; i < ships.length; i++) {
            for (int j = 0; j < ships[i].length; j++) {

                if (ships[i][j][0] == x && ships[i][j][1] == y)
                    return ships[i];
            }
        }

        return null;
    }

    /**
     * 
     * @param ship the ship which is to be moved 
     * @param originalX a x coordinate of a point on the ship
     * @param originalY a y coordinate of the same point on the ship
     * @param newX a x coordinate of a point on the grid 
     * @param newY a y coordinate of the same point on the grid 
     * @return if the ship has successfully been moved 
     * moves a ship from one place to another such that the direction and orientation of the ship are preserved 
     * and such that the point on the ship (originalX, originalY) -> (newX, newY) 
     */
    public boolean moveShip(int[][] ship, int originalX, int originalY, int newX, int newY) {

        int xDiff = newX - originalX;
        int yDiff = newY - originalY;

        for (int i = 0; i < ship.length; i++) {
            int xToMove = ship[i][0] + xDiff;
            int yToMove = ship[i][1] + yDiff;

            if (xToMove < 0 || xToMove >= rowsAndCols || yToMove < 0 || yToMove >= rowsAndCols) {
                return false;
            }

            if ((myShipsLocation[xToMove][yToMove] == 1)) {
                return false;
            }
        }

        for (int i = 0; i < ship.length; i++) {
            int xToMove = ship[i][0] + xDiff;
            int yToMove = ship[i][1] + yDiff;

            myShipsLocation[ship[i][0]][ship[i][1]] = 0;
            myShipsLocation[xToMove][yToMove] = 1;

            ship[i][0] = xToMove;
            ship[i][1] = yToMove;
        }

        view.refreshMyBoard(myShipsLocation);

        return true;
    }

    /**
     * checks if the user won 
     * @return true if the user won (the opponents board has no ships remaining)
     */
    public boolean didIWin() {

        for (int y = 0; y < enemyShipsLocation.length; y++) {
            for (int x = 0; x < enemyShipsLocation[0].length; x++) {

                if (enemyShipsLocation[y][x] == 1) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * checks if the opponent has won 
     * @return true if the opponent won (The player's board has no ships remaining)
     */
    public boolean didEnemyWin() {

        for (int i = 0; i < myShipsLocation.length; i++) {
            for (int j = 0; j < myShipsLocation[0].length; j++) {

                if (myShipsLocation[i][j] == 1) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 
     * @param clickCoords an array with two integer elements which represent where the opponent clicked 
     * changes the value of point is myShipsLocation according to if the opponent hit a ship or not
     * the refreshes the player's board part of the gui  
     */
    private void enemyClickedOnMyBoard(int[] clickCoords) {
        int x = clickCoords[0];
        int y = clickCoords[1];

        if (myShipsLocation[x][y] == 1) {

            myShipsLocation[x][y] = -1;

        } else {

            myShipsLocation[x][y] = -2;

        }

        view.refreshMyBoard(myShipsLocation);
    }

    /**
     * 
     * @param clickCoords an array with two integer elements which represent where the player clicked 
     * changes the value of point is enemyShipsLocation according to if the player hit a ship or not
     * the refreshes the opponent's board part of the gui  
     */
    public void IClickedOnEnemyBoard(int[] clickCoords) {

        int x = clickCoords[0];
        int y = clickCoords[1];

        if (enemyShipsLocation[y][x] == 1) {

            enemyShipsLocation[y][x] = -1;
        } else {

            enemyShipsLocation[y][x] = -2;
        }

        view.refreshEnemyBoard(enemyShipsLocation);
    }

    /**
     * 
     * @param songNumber receives a number which represents which song is to be played
     * 0 - beautiful dream
     * 1 - driving ambition 
     * 2 - just chill
     * plays the song according to songNumber
     */
    public void playSong(int songNumber) {

        if (musicClip != null)
            musicClip.stop();

        URL selectedSong;

        if (songNumber == 0)
            selectedSong = beautifulDreamURL;
        else if (songNumber == 1)
            selectedSong = drivingAmbitionURL;
        else
            selectedSong = justChillURL;

        try {

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(selectedSong);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioInputStream);
            musicClip.start();
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception ex) {
            view.sendPlayerMessage(new Font("Arial", Font.BOLD, 13),"Error with playing sound.");
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @return a float which is equal to 10^(volume/20h)
     */
    public float getVolume() {
        FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
        return (float) Math.pow(10f, gainControl.getValue() / 20f);
    }

    /**
     * 
     * @param volume a parameter which represents the volume of the sound
     * throws an error if the volume is negative, zero or greater/equal to two
     * sets the volume according to volume  
     */
    public void setVolume(float volume) {
        if (volume < 0f || volume > 2f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    /**
     * using soundEffectsClip the function makes a button sound is randomizeTriggeredSetup is false 
     * this is because randomize board triggers makeButtonSound but it shouldn't so if it does trigger it there is no sound  
     */
    public void makeButtonSound() {
        if (!randomizeSetupTriggered) {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(buttonSoundURL);
                soundEffectsClip = AudioSystem.getClip();
                soundEffectsClip.open(audioInputStream);
                soundEffectsClip.start();
            } catch (Exception ex) {
                System.out.println("Error with playing button noise.");
                view.sendPlayerMessage(new Font("Arial", Font.BOLD, 13),"Error with playing button noise.");
                ex.printStackTrace();
            }
        }
    }

    public View getView() {
        return this.view;
    }

    /**
     * calls view.closeGui()
     */
    public void closeClient() {
        view.closeGui();
    }

    /**
     * initializes ClientSideConnection which establishes a connection with the server
     */
    private void connectToServer() {
        clientSideConnection = new ClientSideConnection();
    }

    /**
     * calls view.connectionErrorPane()
     */
    public void connectionError() {
        View.connectionErrorPane();
    }

    class songButtonsClickListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            makeButtonSound();

            JMenuItem[] menuItems = view.getSongMenuItems();
            if (view.getPauseButton() == e.getSource()) {
                if (((JCheckBoxMenuItem) e.getSource()).getState()) {
                    musicClip.stop();
                } else {
                    musicClip.start();
                }
            }

            for (int i = 0; i < menuItems.length; i++) {
                if (menuItems[i] == e.getSource()) {
                    playSong(i);
                    view.getPauseButton().setState(false);
                    view.getPauseButton().setEnabled(true);
                    view.getVolumeSlider().setEnabled(true);
                }
            } 
        }

    }

    class myBoardButtonClickListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            makeButtonSound();

            if (e.getSource() == view.getFinishSetup()) {
                view.changePhase("In-Game");
                setupFinished = true;
                view.getMyTabSideButtons().remove(view.getFinishSetup());
                view.getMyTabSideButtons().remove(view.getRandomizeGrid());

                if (playerId == 1) {
                    yourTurn = true;
                } else {
                    yourTurn = false;
                }

                clientSideConnection.write2DArray(myShipsLocation);
                enemyShipsLocation = clientSideConnection.receiveOpponentBoard(rowsAndCols, rowsAndCols);

                if (playerId == 2) {
                    enemyClickedLocation = clientSideConnection.receiveCoords();
                    enemyClickedOnMyBoard(enemyClickedLocation);
                    yourTurn = true;
                }

            } else if (e.getSource() == view.getRandomizeGrid()) {
                randomizeSetupTriggered = true;
                Random random = new Random();

                for (int i = 0; i < 100; i++) {
                    int x = random.nextInt(rowsAndCols);
                    int y = random.nextInt(rowsAndCols);
                    view.getMyButtons()[x][y].doClick();
                }

                randomizeSetupTriggered = false;

            } else if (!setupFinished) {
                for (int i = 0; i < rowsAndCols; i++) {
                    for (int j = 0; j < rowsAndCols; j++) {
                        if (e.getSource() == view.getMyButton(i, j)) {
                            if (buttonPressed) {
                                moveShip(lastShipPressed, lastButtonPressX, lastButtonPressY, j, i);
                                buttonPressed = false;

                            } else {
                                if (isOnShip(j, i) != null) {

                                    lastButtonPressX = j;
                                    lastButtonPressY = i;
                                    buttonPressed = true;
                                    lastShipPressed = isOnShip(lastButtonPressX, lastButtonPressY);
                                }
                            }
                        }
                    }
                }
            } else if (gameFinished && e.getSource() == view.getYesButton()) {

                view.getEndGamePanel().setVisible(false);

                setUpMyShipsLocation();
                setUpEnemyShipsLocation();

                view.refreshMyBoard(myShipsLocation);
                view.refreshEnemyBoard(enemyShipsLocation);
                view.resetMessagePlayer();

                setupFinished = false;
                gameFinished = false;

                if (playerId == 1) {
                    yourTurn = true;
                } else {
                    yourTurn = false;
                }

                view.getMyTabSideButtons().add(view.getFinishSetup(), 0);
                view.getMyTabSideButtons().add(view.getRandomizeGrid(), 1);

                boolean enemyWantsToPlay = false; 
                try {

                    clientSideConnection.dataOutputStream.writeBoolean(true);
                    enemyWantsToPlay = clientSideConnection.dataInputStream.readBoolean();

                } catch (IOException ex) {
                    System.out.println("IOException from run in SSC");
                    System.out.println(ex.getLocalizedMessage());
                    System.out.println(ex.getStackTrace());
                    connectionError();
                }

                if (!enemyWantsToPlay) {
                    view.sendPlayerMessage(new Font("Arial", Font.BOLD, 13), "Thanks For Playing");

                    view.opponentDeclinedRematch();
                }

                view.changePhase("Setup Phase");

            } else if (gameFinished && e.getSource() == view.getNoButton()) {

                view.getMyTabSideButtons().remove(view.getEndGamePanel());
                view.sendPlayerMessage(new Font("Arial", Font.BOLD, 13), "Thanks For Playing");

                try {
                    clientSideConnection.dataOutputStream.writeBoolean(false);

                } catch (IOException ex) {
                    System.out.println("IOException from run in SSC");
                    System.out.println(ex.getLocalizedMessage());
                    System.out.println(ex.getStackTrace());
                    connectionError();
                }

                closeClient();
            }

        }
    }

    class enemyBoardButtonClickListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            makeButtonSound();

            if (setupFinished && yourTurn && !gameFinished) {
                for (int i = 0; i < rowsAndCols; i++) {
                    for (int j = 0; j < rowsAndCols; j++) {
                        if (e.getSource() == view.getEnemyButton(i, j)) {
                            if (enemyShipsLocation[i][j] < 0) {

                                view.sendPlayerMessage(new Font("Arial" ,Font.BOLD ,15) ,"You cannot click on button\n which was already clicked.");
                            } else {
                                view.resetMessagePlayer();

                                int[] clickCoords = { j, i };
                                IClickedOnEnemyBoard(clickCoords);

                                clientSideConnection.sendCoords(j, i);

                                if (clientSideConnection.sendDidIWin()) {
                                    view.changePhase("Between Games");
                                    myWins++;
                                    view.getScoreLabel().setText("You : " + myWins + " ,Enemy : " + enemyWins);
                                    view.sendPlayerMessage(new Font("Arial", Font.BOLD, 20),"Good Job!!! You Won!");;
                                    view.getEndGamePanel().setVisible(true);;
                                    view.youWonPane(myWins, enemyWins);

                                    gameFinished = true;
                                } else {

                                    yourTurn = false;

                                    enemyClickedLocation = clientSideConnection.receiveCoords();
                                    enemyClickedOnMyBoard(enemyClickedLocation);

                                    if (didEnemyWin()) {
                                        view.changePhase("Between Games");
                                        enemyWins++;
                                        view.getScoreLabel().setText("You : " + myWins + " ,Enemy : " + enemyWins);
                                        view.sendPlayerMessage(new Font("Arial", Font.BOLD, 20),"Nice Try, Maybe next time...");;
                                        view.getEndGamePanel().setVisible(true);
                                        view.youLostPane(myWins, enemyWins);

                                        gameFinished = true;
                                    }

                                    yourTurn = true;
                                }
                            }
                        }
                    }
                }
            }        }
    }

    private class ClientSideConnection {

        private Socket socket = new Socket();
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        public ClientSideConnection() {

            try {
                socket = new Socket(ipAddress, portNumber);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                playerId = dataInputStream.readInt();
                dataOutputStream.writeInt(rowsAndCols);
                System.out.println("Connected to server as player #" + playerId);

            } catch (IOException ex) {
                System.out.println("IOException from ClientSideConnection Constructor");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                connectionError();
            }
        }

        public void write2DArray(int[][] array) {

            try {
                dataOutputStream.writeInt(array.length);
                dataOutputStream.writeInt(array[0].length);

                for (int x = 0; x < array.length; x++) {
                    for (int y = 0; y < array[0].length; y++) {
                        dataOutputStream.writeInt(array[x][y]);
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOException from write2dArray");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                connectionError();
            }
        }

        public int[][] receiveOpponentBoard(int xLength, int yLength) {

            int[][] array = new int[yLength][xLength];

            try {

                for (int y = 0; y < yLength; y++) {
                    for (int x = 0; x < xLength; x++) {
                        array[y][x] = dataInputStream.readInt();
                    }
                }

            } catch (IOException ex) {
                System.out.println("IOException from read2DArray");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                connectionError();
            }

            return array;

        }

        public void sendCoords(int x, int y) {

            try {
                dataOutputStream.writeInt(x);
                dataOutputStream.writeInt(y);

            } catch (IOException ex) {
                System.out.println("IOException from sendCoords");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                connectionError();
            }
        }

        public int[] receiveCoords() {
            int[] coords = { -1, -1 };

            try {
                coords[0] = dataInputStream.readInt();
                coords[1] = dataInputStream.readInt();
            } catch (IOException ex) {
                System.out.println("IOException from receiveCoords");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                connectionError();
            }

            return coords;
        }

        public boolean sendDidIWin() {
            try {
                dataOutputStream.writeBoolean(didIWin());
            } catch (IOException ex) {
                System.out.println("IOException from sendDidIWin"); 
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                connectionError();
            }

            return didIWin();
        }

    }
}