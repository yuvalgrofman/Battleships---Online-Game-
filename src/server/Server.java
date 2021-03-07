package server;

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

    public Server(int portNumber) {
        this.portNumber = portNumber;

        System.out.println("-----Server-------");
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException ex) {
            System.out.println("IOException from Sever Constructor");
            System.out.println(ex.getLocalizedMessage());
            System.out.println(ex.getStackTrace());
            closeServer();
        }
        acceptConnections();

    }

    public void closeServer() {
        System.exit(0);
    }

    public void acceptConnections() {
        try {
            System.out.println("Accepting connections");

            while (playersConnected < 2) {
                Socket socket = serverSocket.accept();
                playersConnected++;
                System.out.println("Player #" + playersConnected + " has connected.");
                ServerSideConnection ssc = new ServerSideConnection(socket, playersConnected);

                if (playersConnected == 1) {
                    playerOneSSC = ssc;
                } else {
                    playerTwoSSC = ssc;
                }

                Thread runThread = new Thread(ssc);
                runThread.start();

            }

            System.out.println("All players have connected no longer accepting connections.");
        } catch (IOException ex) {
            System.out.println("IOException from acceptConnections");
            System.out.println(ex.getLocalizedMessage());
            closeServer();
        }
    }

    public boolean isValidBoard(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] != 0) {
                    return true;
                }
            }
        }

        return false;
    }

    private class ServerSideConnection implements Runnable {

        private Socket socket;
        private DataOutputStream dataOutputStream;
        private DataInputStream dataInputStream;
        private int playerId;
        private boolean receivedBoard;
        private boolean clientPlaying = true;

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

                    while (!aPlayerWon) {

                        if (playerId == 1 && isPlayerOneTurn) {// playerOne in his turn
                            player1Coords = receiveCoords();

                            if (player1Coords[0] == -1 && player1Coords[1] == -1) {
                                throw new IOException();
                            }

                            if (didClientWin()){
                                System.out.println("Player # " + playerId + " Won");
                                playerOneWon = true;
                                aPlayerWon = true;
                            }

                            player1CoordsValid = true;
                            player2CoordsValid = false;
                            isPlayerOneTurn = false;
                            sentCoords = false;

                        } else if (playerId == 1 && !isPlayerOneTurn && !sentCoords) {// playerOne in his opponents turn

                            while (!player2CoordsValid) {
                                Thread.sleep(10);
                            }

                            boolean successfullyWroteCoords = writeCoords(player2Coords);

                            if (!successfullyWroteCoords) {
                                throw new IOException();
                            }

                            sentCoords = true;

                        } else if (playerId == 2 && !isPlayerOneTurn) {// playerTwo in his turn

                            player2Coords = receiveCoords();

                            if (player2Coords[0] == -1 && player2Coords[1] == -1) {
                                throw new IOException();
                            }

                            if (didClientWin()){
                                System.out.println("Player # " + playerId + " Won");
                                playerTwoWon = true; 
                                aPlayerWon = true;
                            }

                            player1CoordsValid = false;
                            player2CoordsValid = true;
                            isPlayerOneTurn = true;
                            sentCoords = false;

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