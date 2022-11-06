package src;

import java.util.ArrayList;

public class Game {
    private int numOfRounds;
    private Results results;
    private Player[] players;
    private double[] payoffs;

    /*
     ** Situation:
     * ___________________________________________
     * | _one \ two_ | cooperation | competition |
     * |_____________|_____________|_____________|
     * | cooperation | ___a \ a___ | ___b \ c___ |
     * |_____________|_____________|_____________|
     * | competition | ___c \ b___ | ___d \ d___ |
     * |_____________|_____________|_____________|
     * 
     ** Payoffs (double[]{a, b, c, d} == payoffs):
     * a is the payoff of both players cooperating (event 0)
     * b is the payoff of cooperating while the opponent competes (event 1)
     * c is the payoff of competing while the opponent cooperates (event 2)
     * d is the payoff of both players competing (event 3)
     * 
     ** Conditions:
     * 1) c > a and b > d --> to have a nash equilibrium at the both competing stage
     * 2) d > a for the both competing stage to be collectively worse than the both
     * cooperating
     * 
     ** Other useful info:
     * 1) Cooperation is equivalent to false and 0
     * Competition is equivalent to true and 1
     * 
     * 2) Event numbers:
     * 0 --> (playerOne == false && playerTwo == false)
     * 1 --> (playerOne == false && playerTwo == true)
     * 2 --> (playerOne == true && playerTwo == false)
     * 3 --> (playerOne == true && playerTwo == true)
     * 
     */

    public Game(Game game) {
        this(game.getNumOfRounds(), game.getPlayers()[0].copy(), game.getPlayers()[1].copy(),
                new double[] { game.getPayoffs()[0], game.getPayoffs()[1], game.getPayoffs()[2], game.getPayoffs()[3] },
                game.getResults());
    }

    public Game(int rounds, Player playerOne, Player playerTwo) {
        this(rounds, playerOne, playerTwo, null);
    }

    public Game(int rounds, Player playerOne, Player playerTwo, double[] payoffs) {
        this(rounds, playerOne, playerTwo, payoffs, new Results());
    }

    public Game(int rounds, Player playerOne, Player playerTwo, double[] payoffs, Results results) {
        if (playerOne == playerTwo)
            System.exit(4); // a player can't play against themself
        this.players = new Player[] { playerOne, playerTwo };
        this.results = new Results();
        this.payoffs = payoffs;
        this.numOfRounds = rounds;
        this.results = results;
        this.playRounds();
    }

    public double[] getScores() {
        if (this.payoffsAreSet())
            throw new NullPointerException("The payoffs are not set");

        int[] list = results.getResultsPerEvent();
        double commonScore = list[0] * payoffs[0] + list[3] * payoffs[3];
        // commonScore is the payoffs of situations 0 and 3 (Same for both players)
        return new double[] { commonScore + list[1] * payoffs[1] + list[2] * payoffs[2],
                commonScore + list[1] * payoffs[2] + list[2] * payoffs[1] };
    }

    public double getScore(int playerNumber) {
        if (this.payoffsAreSet())
            throw new NullPointerException("The payoffs are not set");
        // 0 if its the 1st player and 1 if the second
        int[] list = results.getResultsPerEvent();
        return list[0] * payoffs[0] + list[1] * payoffs[playerNumber + 1] + list[2] * payoffs[2 - playerNumber]
                + list[3] * payoffs[3];
    }

    public int getNumOfRounds() {
        return numOfRounds;
    }

    public Results getResults() {
        return this.results;
    }

    public ArrayList<Boolean> getResultsPerRounds(int playerNumber) {
        return this.results.getResultsPerRounds(playerNumber);
    }

    public Player[] getPlayers() {
        return players;
    }

    public double[] getPayoffs() {
        return this.payoffs;
    }

    public void setPayoffs(double[] payoffs) {
        this.payoffs = payoffs;
    }

    private boolean playersHaveSameName() {
        return players[0].getName().equals(players[1].getName());
    }

    public boolean payoffsAreSet() {
        return payoffs == null;
    }

    public boolean isDone() {
        return numOfRounds == results.getSizeOfResults();
    }

    public void playOneRound(boolean playerOne, boolean playerTwo) {
        this.results.addResult(playerOne, playerTwo);
    }

    public void playRounds() {
        for (int i = 0; i < this.numOfRounds; i++) {
            playOneRound(players[0].decision(this), players[1].decision(this));
        }
    }

    public void printTableOfEvents() {
        int[] array = results.getResultsPerEvent();
        boolean playersHaveSameName = this.playersHaveSameName();
        System.out.println("-------------------------------------------");
        System.out.printf("| %18s / %-18s |%n", players[0].getName() + (playersHaveSameName ? " (1)" : ""),
                players[1].getName() + (playersHaveSameName ? " (2)" : ""));
        System.out.println("-------------------------------------------");
        System.out.printf("| %-11s | %-11s | %-11s |%n", "one \\ two", "cooperation", "competition");
        System.out.println("-------------------------------------------");
        System.out.printf("| %-11s | %-11s | %-11s |%n", "cooperation", array[0], array[1]);
        System.out.println("-------------------------------------------");
        System.out.printf("| %-11s | %-11s | %-11s |%n", "competition", array[2], array[3]);
        System.out.println("-------------------------------------------");
    }

    public void printResultsPerRound() {
        ArrayList<Boolean> temp1 = this.results.getResultsPerRounds(0);
        ArrayList<Boolean> temp2 = this.results.getResultsPerRounds(1);
        boolean playersHaveSameName = playersHaveSameName();
        int maxNameLength = Math.max(players[0].getName().length(), players[1].getName().length())
                + (playersHaveSameName ? 4 : 0);
        System.out.println("Results (0 is cooperation, 1 is competition):");

        System.out.printf("%-" + maxNameLength + "s: %s", players[0].getName() + (playersHaveSameName ? " (1)" : ""),
                (temp1.get(0) ? "1" : "0"));
        for (int i = 1; i < temp1.size(); i++) {
            System.out.print((temp1.get(i) ? ", 1" : ", 0"));
        }
        System.out.printf("%n%-" + maxNameLength + "s: %s", players[1].getName() + (playersHaveSameName ? " (2)" : ""),
                (temp2.get(0) ? "1" : "0"));
        for (int i = 1; i < temp2.size(); i++) {
            System.out.print((temp2.get(i) ? ", 1" : ", 0"));
        }
        System.out.println();
    }

    public void printGameTable() {
        boolean playersHaveSameName = playersHaveSameName();
        System.out.println("-------------------------------------------");
        System.out.printf("| %18s / %-18s |%n", players[0].getName() + (playersHaveSameName ? " (1)" : ""),
                players[1].getName() + (playersHaveSameName ? " (2)" : ""));
        System.out.println("-------------------------------------------");
        System.out.printf("| %-11s | %-11s | %-11s |%n", "one \\ two", "cooperation", "competition");
        System.out.println("-------------------------------------------");
        System.out.printf("| %-11s | %4s \\ %-4s | %4s \\ %-4s |%n", "cooperation", Utils.doubleTostring(payoffs[0]),
                Utils.doubleTostring(payoffs[0]), Utils.doubleTostring(payoffs[1]), Utils.doubleTostring(payoffs[2]));
        System.out.println("-------------------------------------------");
        System.out.printf("| %-11s | %4s \\ %-4s | %4s \\ %-4s |%n", "competition", Utils.doubleTostring(payoffs[2]),
                Utils.doubleTostring(payoffs[1]), Utils.doubleTostring(payoffs[3]), Utils.doubleTostring(payoffs[3]));
        System.out.println("-------------------------------------------");
    }

    public void printScores() {
        boolean playersHaveSameName = this.playersHaveSameName();
        double[] scores = this.getScores();
        int maxNameLength = Math.max(players[0].getName().length(), players[1].getName().length())
                + (playersHaveSameName ? 4 : 0);
        System.out.println("Scores:");
        System.out.printf("%-" + maxNameLength + "s: %s%n", players[0].getName() + (playersHaveSameName ? " (1)" : ""),
                Utils.doubleTostring(scores[0]));
        System.out.printf("%-" + maxNameLength + "s: %s%n", players[1].getName() + (playersHaveSameName ? " (2)" : ""),
                Utils.doubleTostring(scores[1]));
    }

}