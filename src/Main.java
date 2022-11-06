package src;

public class Main {
    public static void main(String[] args) {
        Player playerOne = new Joss("Joss", 0.2);
        Player playerTwo = new TitForTat("TitForTat", 1, 1);
        Game game = new Game(100, playerTwo, playerOne, new double[] { 3, 0, 5, 1 });
        game.printGameTable();
        game.printTableOfEvents();
        game.printResultsPerRound();
        game.printScores();
    }
}