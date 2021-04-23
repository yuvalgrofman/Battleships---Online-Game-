
import java.io.*;
import java.net.*;

public class Server {

    private ServerSocket serverSocket;
    private int playersConnected = 0;
    private int portNumber;
    ServerSideConnection playerOneSSC;
    ServerSideConnection playerTwoSSC;

    int rowsAndCols;
    int[][] playerOneBoard;
    int[][] playerTwoBoard;

    private boolean receivedPlayer1Board = false;
    private boolean receivedPlayer2Board = false;

    private boolean sentPlayer1Board = false;
    private boolean sentPlayer2Board = false;

    private boolean sentBoards = false;

    private int[] player1Coords;
    private int[] player2Coords;

    private boolean player1CoordsValid = false;
    private boolean player2CoordsValid = false;

    private boolean isPlayerOneTurn = true;

    private boolean playerOnePlaying = true;
    private boolean playerTwoPlaying = true;  

    private boolean aPlayerWon = false;
    private boolean playerOneWon = false;
    private boolean playerTwoWon = false;

    private boolean playerOnePlayingValid = false;
    private boolean playerTwoPlayingValid = false;

    /**
     * 
     * @param portNumber the portNumber of the serverSocket
     * Initializes the ServerSocket and gets it ready to accept connection 
     */
    public Server(int portNumber) {
        this.portNumber = portNumber;
        System.out.println("-----Server-------");
        try {
            serverSocket = new ServerSocket(portNumber, 50, InetAddress.getByName("0.0.0.0"));
            // serverSocket = new ServerSocket(portNumber);
            // serverSocket.bind(new InetSocketAddress("0.0.0.0",portNumber));
            System.out.println(InetAddress.getByName("0.0.0.0"));
            System.out.println("Server Socket Created at: " + serverSocket.getInetAddress().getLocalHost());
        } catch (IOException ex) {
            System.out.println("IOException from Server Constructor");
            System.out.println(ex.getLocalizedMessage());
            System.out.println(ex.getStackTrace());
            closeServer();
        }
        acceptConnections();

    }

    /**
     * Closes the server
     */
    public void closeServer() {
        System.exit(0);
    }

    /**
     * Makes the serverSocket accept up to two sockets and then stops accepting connections 
     */
    public void acceptConnections() {
        try {
            System.out.println("Accepting connections");

            while (playersConnected < 2) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress().getHostAddress());
                playersConnected++;
                System.out.println("Player #" + playersConnected + " has connected.");
                ServerSideConnection ssc = new ServerSideConnection(socket, playersConnected);

                if (playersConnected == 1) {
                    playerOneSSC = ssc;
                } else {
                    playerTwoSSC = ssc;
                }

                Thread runThread = new Thread(ssc);
                runThread.start();//run() is called HERE

            }

            System.out.println("All players have connected no longer accepting connections.");
        } catch (IOException ex) {
            System.out.println("IOException from acceptConnections");
            System.out.println(ex.getLocalizedMessage());
            closeServer();
        }
    }
    
    //Not used in code so I commented out 
    //----------------------------------------------------------------
    // public boolean isValidBoard(int[][] board) {
    //     for (int i = 0; i < board.length; i++) {
    //         for (int j = 0; j < board[0].length; j++) {
    //             if (board[i][j] != 0) {
    //                 return true;
    //             }
    //         }
    //     }
    //     return false;
    // }
    //----------------------------------------------------------------

    /**
     * Class responsible for the connections to each client 
     */
    private class ServerSideConnection implements Runnable {

        private Socket socket;
        private DataOutputStream dataOutputStream;
        private DataInputStream dataInputStream;
        private int playerId;
        private boolean receivedBoard;
        private boolean clientPlaying = true;

        /**
         * 
         * @param socket a socket which is used to communicate with the client 
         * @param playerId The Id of the player the instance of this class is connected to
         */
        public ServerSideConnection(Socket socket, int playerId) {
            this.socket = socket;
            this.playerId = playerId;
            receivedBoard = false;

            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                System.out.println("IOException from SSC Constructor");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                closeServer();
            }
        }

        /**
         * A method which creates a new thread which is used to run code 
         * triggered on line 95 
         */
        public void run() {
            try {

                dataOutputStream.writeInt(playerId);

                rowsAndCols = dataInputStream.readInt();
                while (clientPlaying) {

                    if (playerId == 1) {

                        playerOneBoard = read2DArray();
                        receivedPlayer1Board = true;

                        while (!receivedPlayer2Board) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        write2DArray(playerTwoBoard);

                    } else {

                        playerTwoBoard = read2DArray();
                        receivedPlayer2Board = true;

                        while (!receivedPlayer1Board) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        write2DArray(playerOneBoard);
                    }

                    isPlayerOneTurn = true;
                    playerOnePlayingValid = false;
                    playerTwoPlayingValid = false;
                    player1CoordsValid = false;
                    player2CoordsValid = false;

                    dataOutputStream.flush();

                    if (playerId == 2) {
                        while (!player1CoordsValid) {
                            Thread.sleep(10);
                        }

                        writeCoords(player1Coords);
                    }

                    boolean sentCoords = false;

                    while (!aPlayerWon) {   //While no player won their are four states 
                                            //Case 1. you are p1 and its your turn -> wait for client p1 for input and store it  
                                            //Case 2. you are p1 and its not your turn -> wait for p2 to end turn and then send to p1 coords of where p2 clicked 
                                            //Case 3. you are p2 and its your turn -> wait for client p2 for input and store it 
                                            //Case 4. you are p2 and its not your turn -> wait for p1 to end turn and then send to p1 coords of where p1 clicked
                                            
                        //Case 1
                        if (playerId == 1 && isPlayerOneTurn) {
                            player1Coords = receiveCoords();

                            if (player1Coords[0] == -1 && player1Coords[1] == -1) {
                                throw new IOException();
                            }

                            if (didClientWin()) {
                                System.out.println("Player # " + playerId + " Won");
                                playerOneWon = true;
                                aPlayerWon = true;
                            }

                            player1CoordsValid = true;
                            player2CoordsValid = false;
                            isPlayerOneTurn = false;
                            sentCoords = false;

                        //Case 2
                        } else if (playerId == 1 && !isPlayerOneTurn && !sentCoords) {// playerOne in his opponents turn

                            while (!player2CoordsValid) {
                                Thread.sleep(10);
                            }

                            boolean successfullyWroteCoords = writeCoords(player2Coords);

                            if (!successfullyWroteCoords) {
                                throw new IOException();
                            }

                            sentCoords = true;

                        //Case 3
                        } else if (playerId == 2 && !isPlayerOneTurn) {// playerTwo in his turn

                            player2Coords = receiveCoords();

                            if (player2Coords[0] == -1 && player2Coords[1] == -1) {
                                throw new IOException();
                            }

                            if (didClientWin()) {
                                System.out.println("Player # " + playerId + " Won");
                                playerTwoWon = true;
                                aPlayerWon = true;
                            }

                            player1CoordsValid = false;
                            player2CoordsValid = true;
                            isPlayerOneTurn = true;
                            sentCoords = false;

                        //Case 4
                        } else if (playerId == 2 && isPlayerOneTurn && !sentCoords) {// playerTwo in his opponents turn

                            while (!player1CoordsValid) {
                                Thread.sleep(10);
                            }

                            boolean successfullyWroteCoords = writeCoords(player1Coords);

                            if (!successfullyWroteCoords) {
                                throw new IOException();
                            }

                            sentCoords = true;

                        }
                    }
                    clientPlaying = dataInputStream.readBoolean();

                    if (playerId == 1) {
                        playerOnePlaying = clientPlaying;
                        playerOnePlayingValid = true;

                        while (!playerTwoPlayingValid) {
                            Thread.sleep(10);
                        }

                    } else {
                        playerTwoPlaying = clientPlaying;
                        playerTwoPlayingValid = true;

                        while (!playerOnePlayingValid) {
                            Thread.sleep(10);
                        }

                    }

                    clientPlaying = playerOnePlaying && playerTwoPlaying;
                    dataOutputStream.writeBoolean(clientPlaying);

                    receivedPlayer1Board = false;
                    receivedPlayer2Board = false;
                    aPlayerWon = false;
                }
            } catch (IOException ex) {
                System.out.println("IOException from run in SSC");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                closeServer();
            } catch (InterruptedException ex) {
                System.out.println("InterruptedException from run in SSC");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
            }
        }
        
        /**
         * 
         * @param array a 2d array 
         * this function sends a 2d array to the client 
         */
        public void write2DArray(int[][] array) {

            try {
                for (int x = 0; x < array.length; x++) {
                    for (int y = 0; y < array[0].length; y++) {
                        dataOutputStream.writeInt(array[x][y]);
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOException from write2dArray");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                closeServer();
            }
        }

        /**
         * reads a 2d array from the client 
         * @return and returns it 
         */
        public int[][] read2DArray() {
            try {

                int xLength = dataInputStream.readInt();
                int yLength = dataInputStream.readInt();

                int[][] array = new int[yLength][xLength];
                for (int y = 0; y < yLength; y++) {
                    for (int x = 0; x < xLength; x++) {
                        array[x][y] = dataInputStream.readInt();
                    }
                }

                return array;

            } catch (IOException ex) {
                System.out.println("IOException from read2DArray");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                closeServer();
            }

            int[][] array = { { -1 } };
            return array;

        }

        /**
         * receives an array of length 2 which represents coords
         * @return and returns it
         */
        public int[] receiveCoords() {

            int[] coords = { -1, -1 };

            try {

                coords[0] = dataInputStream.readInt();
                coords[1] = dataInputStream.readInt();

            } catch (IOException ex) {
                System.out.println("IOException from receiveCoords");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                closeServer();
            }

            return coords;
        }

        /**
         * 
         * @param coords an int of length two which represents coords
         * this function sends those coords to the client  
         * @return if the coords were successfully sent
         */
        public boolean writeCoords(int[] coords) {

            try {
                dataOutputStream.writeInt(coords[0]);
                dataOutputStream.writeInt(coords[1]);
            } catch (IOException ex) {
                System.out.println("IOException from writeCoords");
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());

                return false;
            }

            return true;
        }

        /**
         * receives 
         * @return and returns a boolean which represents if the client won
         */
        public boolean didClientWin() {

            try {
                 return dataInputStream.readBoolean();
            } catch (IOException ex) {
                System.out.println("IOException from didYouWin"); 
                System.out.println(ex.getLocalizedMessage());
                System.out.println(ex.getStackTrace());
                closeServer();
            }

            return false; 
        }
    }
}