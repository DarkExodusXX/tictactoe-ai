package tictactoeai;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TicTacToe {
    static Scanner scanner = new Scanner(System.in);
    static Scanner scannerForLine = new Scanner(System.in);
    static Random random = new Random(System.nanoTime());

    public static void startGame() {
        System.out.print("Input command: ");
        String inputCommand = scannerForLine.nextLine();
        String[] inputArgs = inputCommand.split(" ");
        String player1 = null;
        String player2 = null;
        List<String> availablePlayers = new LinkedList<String>();
        availablePlayers.add("user");
        availablePlayers.add("easy");
        availablePlayers.add("medium");
        availablePlayers.add("hard");
        switch (inputArgs[0]) {
            case "start": {
                if (inputArgs.length != 3) {
                    System.out.println("Bad parameters!");
                    startGame();
                }
                if (availablePlayers.contains(inputArgs[1]) && availablePlayers.contains(inputArgs[2])) {
                    player1 = inputArgs[1];
                    player2 = inputArgs[2];
                } else {
                    System.out.println("Bad parameters!");
                    startGame();
                }
                break;
            }
            case "exit": {
                return;
            }
            default: {
                System.out.println("Bad parameters!");
                startGame();
            }
        }

        String newInput = "         ";
        print(newInput);

        String status = "Game not finished";

        int id = 0;
        String player;
        while (status.equals("Game not finished")) {
            if (id % 2 == 0) {
                player = player1;
            } else {
                player = player2;
            }
            newInput = getInput(newInput, id, player);
            if (!player.equals("user")) {
                System.out.printf("Making move level \"%s\"\n", player);
            }
            print(newInput);
            status = checkStatus(newInput);
            id++;
        }

        System.out.println(status);
    }

    public static void print(String input) {
        System.out.println("-".repeat(9));
        for (int i = 0; i < 3; i++) {
            System.out.printf("| %c %c %c |%n", input.charAt(i * 3), input.charAt(i * 3 + 1), input.charAt(i * 3 + 2));
        }
        System.out.println("-".repeat(9));
    }

    public static String getInput(String input, int id, String player) {
        int x = 0;
        int y = 0;
        if (player.equals("user")) {
            System.out.print("Enter the coordinates: ");
            x = getIntegerFromInput();
            y = getIntegerFromInput();

            if ((x > 3 || x < 1 || y > 3 || y < 1)) {
                System.out.println("Coordinates should be from 1 to 3!");
                return getInput(input, id, player);
            }
        } else {
            switch (player) {
                case "easy": {
                    x = random.nextInt(3) + 1;
                    y = random.nextInt(3) + 1;
                    break;
                }
                case "medium": {
                    int[] coordinates = getCoordsFromMedium(id, input);
                    x = coordinates[0];
                    y = coordinates[1];
                    break;
                }
                case "hard": {
                    Move bestMove;
                    if (id % 2 == 0) {
                        bestMove = getCoordsFromHard(input, 'X', 'O', 'X');
                    } else {
                        bestMove = getCoordsFromHard(input, 'O', 'X', 'O');
                    }
                    x = bestMove.index % 3 + 1;
                    y = (8 - bestMove.index) / 3 + 1;
                    break;
                }
            }
        }

        String newInput;

        char symbol;
        if (input.charAt(8 - y * 3 + x) == ' ' || player.equals("hard")) {
            if (id % 2 == 1) {
                symbol = 'O';
            } else {
                symbol = 'X';
            }
            newInput = input.substring(0, 8 - y * 3 + x) + symbol + input.substring(9 - y * 3 + x);
            return newInput;
        } else {
            if (player.equals("user")) {
                System.out.println("This cell is occupied! Choose another one!");
            }
            return getInput(input, id, player);
        }
    }

    private static int[] getCoordsFromMedium(int id, String board) {
        int[] coordinates = new int[2];
        String mySymbol = "";
        String opponentSymbol = "";
        String myWin = "";
        String opponentWin = "";
        if (id % 2 == 0) {
            mySymbol = "X";
            opponentSymbol = "O";
        } else {
            mySymbol = "O";
            opponentSymbol = "X";
        }
        myWin = mySymbol + " wins";
        opponentWin = opponentSymbol + " wins";
        String newInput = board;
        boolean isWin = false;
        for (int i = 0; i < board.length(); i++) {
            if (board.charAt(i) == ' ') {
                newInput = board.substring(0, i) + mySymbol + board.substring(i + 1);
                if (checkStatus(newInput).equals(myWin)) {
                    coordinates[0] = i % 3 + 1;
                    coordinates[1] = (8 - i) / 3 + 1;
                    isWin = true;
                    break;
                }
            }
        }
        if (!isWin) {
            for (int i = 0; i < board.length(); i++) {
                if (board.charAt(i) == ' ') {
                    newInput = board.substring(0, i) + opponentSymbol + board.substring(i + 1);
                    if (checkStatus(newInput).equals(opponentWin)) {
                        coordinates[0] = i % 3 + 1;
                        coordinates[1] = (8 - i) / 3 + 1;
                        isWin = true;
                        break;
                    }
                }
            }
        }
        if (!isWin) {
            coordinates[0] = random.nextInt(3) + 1;
            coordinates[1] = random.nextInt(3) + 1;
        }
        return coordinates;
    }

    private static Move getCoordsFromHard(String board, char symbol, char opponentSymbol, char mySymbol) {
        List<Integer> availableCoords = getAvailableCoords(board);

        if (checkStatus(board).equals(String.format("%s wins", mySymbol))) {
            Move move = new Move();
            move.score = 10;
            return move;
        }

        if (checkStatus(board).equals(String.format("%s wins", opponentSymbol))) {
            Move move = new Move();
            move.score = -10;
            return move;
        }

        if (checkStatus(board).equals("Draw")) {
            Move move = new Move();
            move.score = 0;
            return move;
        }

        List<Move> moves = new LinkedList<>();

        for (int i : availableCoords) {
            String newBoard = "";
            Move move = new Move();
            move.index = i;
            if (i == 8) {
                newBoard = board.substring(0, i) + symbol;
            } else {
                newBoard = board.substring(0, i) + symbol + board.substring(i + 1);
            }
            if (symbol == 'X') {
                symbol = 'O';
            } else {
                symbol = 'X';
            }
            Move result = getCoordsFromHard(newBoard, symbol, opponentSymbol, mySymbol);
            move.score = result.score;
            if (move.score == -10 && symbol == opponentSymbol) {
                return move;
            }

            if (move.score == 10 && symbol == mySymbol) {
                return move;
            }

            moves.add(move);
        }

        int bestMove = 0;
        if (symbol == mySymbol) {
            int bestScore = -10000;
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).score > bestScore) {
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        } else {
            int bestScore = 10000;
            for(int i = 0; i < moves.size(); i++){
                if(moves.get(i).score < bestScore){
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        }

        return moves.get(bestMove);
    }

    private static String checkStatus(String input) {
        for (int i = 0; i < 3; i++) {
            if (input.charAt(i) == input.charAt(i + 3) && input.charAt(i) == input.charAt(i + 6) && input.charAt(i) != ' ') {
                return String.format("%c wins", input.charAt(i));
            }
            if (input.charAt(i * 3) == input.charAt(i * 3 + 1) && input.charAt(i * 3) == input.charAt(i * 3 + 2) && input.charAt(i * 3) != ' ') {
                return String.format("%c wins", input.charAt(i * 3));
            }
        }
        if (input.charAt(0) == input.charAt(4) && input.charAt(0) == input.charAt(8) && input.charAt(0) != ' ') {
            return String.format("%c wins", input.charAt(0));
        }
        if (input.charAt(2) == input.charAt(4) && input.charAt(2) == input.charAt(6) && input.charAt(2) != ' ') {
            return String.format("%c wins", input.charAt(2));
        }
        for (int i = 0; i < 9; i++) {
            if (input.charAt(i) == ' ') {
                return "Game not finished";
            }
        }
        return "Draw";
    }

    private static List<Integer> getAvailableCoords(String board) {
        List <Integer> availableCoords = new LinkedList<Integer>();
        for (int i = 0; i < board.length(); i++) {
            if (board.charAt(i) == ' ') {
                availableCoords.add(i);
            }
        }
        return availableCoords;
    }

    private static int getIntegerFromInput() {
        int input;
        try {
            input = Integer.parseInt(scanner.next());
        } catch (Exception e) {
            System.out.println("You should enter numbers!");
            return getIntegerFromInput();
        }
        return input;
    }
}
