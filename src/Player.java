package src;

import java.util.ArrayList;

public abstract class Player {

    private String name;
    private boolean includesRandomness;
    private boolean dependsOnPastResult;

    protected Player(String name, boolean includesRandomness, boolean dependsOnPastResult) {
        if (name.length() > 18) {
            System.out.println("Name is too long (more than 18 characters)");
            System.exit(0);
        }
        this.name = name;
        this.includesRandomness = includesRandomness;
        this.dependsOnPastResult = dependsOnPastResult;
    }

    public abstract Player copy();

    public String getName() {
        return name;
    }

    public abstract boolean decision(Game game);

    public boolean includesRandomness() {
        return this.includesRandomness;
    }

    public boolean dependsOnPastResult() {
        return this.dependsOnPastResult;
    }

    public int getSelfIndex(Game game) {
        return (game.getPlayers()[0] == this) ? 0 : 1;
    }

}

class AlwaysCooperate extends Player {

    public AlwaysCooperate(String name) {
        super(name, false, false);
    }

    @Override
    public Player copy() {
        return new AlwaysCooperate(this.getName());
    }

    @Override
    public boolean decision(Game game) {
        return false;
    }

}

class AlwaysCompete extends Player {

    public AlwaysCompete(String name) {
        super(name, false, false);
    }

    @Override
    public Player copy() {
        return new AlwaysCompete(this.getName());
    }

    @Override
    public boolean decision(Game game) {
        return true;
    }

}

class Random extends Player {
    private double cooperationProbability;

    public Random(String name, double cooperationProbability) {
        super(name, true, false);
        this.cooperationProbability = cooperationProbability;
    }

    public Random(String name) {
        this(name, 0.5);
    }

    public double getCooperationProbability() {
        return cooperationProbability;
    }

    @Override
    public Player copy() {
        return new Random(this.getName(), this.getCooperationProbability());
    }

    @Override
    public boolean decision(Game game) {
        return (Math.random() < cooperationProbability) ? false : true;
    }

}

class TitForTat extends Player {
    private int numOfTats;
    private int numOfTits;

    /**
     * @param name      Player's name
     * @param numOfTats Number of competitions done by the opponent for tits to
     *                  happen
     * @param numOfTits Number of tits that happen after the tats
     */
    public TitForTat(String name, int numOfTats, int numOfTits) {
        super(name, false, true);
        if (numOfTats <= 0 || numOfTits <= 0) {
            System.exit(1); // num of tits or tats not valid
        }
        this.numOfTats = numOfTats;
        this.numOfTits = numOfTits;
    }

    public TitForTat(String name) {
        this(name, 1, 1);
    }

    public int getNumOfTits() {
        return this.numOfTits;
    }

    public int getNumOfTats() {
        return this.numOfTats;
    }

    @Override
    public Player copy() {
        return new TitForTat(this.getName(), this.getNumOfTits(), this.getNumOfTats());
    }

    @Override
    /** @return whether there are enough tats to have a tits reaction */
    public boolean decision(Game game) {
        ArrayList<Boolean> opponentResults = game.getResults()
                .getResultsPerRounds(this.getSelfIndex(game) == 0 ? 1 : 0);
        int counter = 0;
        for (int i = Math.max(opponentResults.size() - this.numOfTats - this.numOfTits + 1, 0); i < opponentResults
                .size(); i++) {
            if (opponentResults.get(i) == true) {
                counter++;
                if (counter == this.numOfTats)
                    return true;
            } else
                counter = 0;
        }
        return false;
    }
}

/**
 * Always cooperates unless the other competes once, then he always competes
 * equivalent of tit for tat with tat = 1 and tit = infinity
 */
class Grudger extends Player {
    private boolean grudge;

    public Grudger(String name) {
        super(name, false, true);
        grudge = false;
    }

    private Grudger(String name, boolean grudge) {
        super(name, false, true);
        this.grudge = grudge;
    }

    public boolean getGrudge() {
        return grudge;
    }

    @Override
    public Player copy() {
        return new Grudger(this.getName(), this.grudge);
    }

    @Override
    public boolean decision(Game game) {
        if (grudge == true)
            return true;
        int size = game.getResults().getSizeOfResults();
        System.out.println(size + " " + grudge);
        if (size == 0 || game.getResultsPerRounds((this.getSelfIndex(game) == 0) ? 1 : 0).get(size - 1) == false)
            return false;
        else {
            grudge = true;
            return true;
        }
    }
}

/**
 * Behaves like tit-for-tat but has a fixed probability of competing each time
 */
class Joss extends Player {

    private double competingProbability;

    public Joss(String name, double competingProbability) {
        super(name, true, false);
        this.competingProbability = competingProbability;
    }

    public Joss(String name) {
        this(name, 0.2);
    }

    public double getCompetingProbability() {
        return this.competingProbability;
    }

    @Override
    public Player copy() {
        return new Joss(this.getName(), this.competingProbability);
    }

    @Override
    public boolean decision(Game game) {
        if (Math.random() < competingProbability) {
            return true;
        }
        ArrayList<Boolean> opponentResults = game.getResults()
                .getResultsPerRounds(this.getSelfIndex(game) == 0 ? 1 : 0);
        return opponentResults.size() != 0 && opponentResults.get(opponentResults.size() - 1);
    }
}

class MiniMax extends Player {
    private Scenario tree;

    public MiniMax(String name) {
        super(name, false, true);
    }

    private MiniMax(String name, Scenario tree) {
        this(name);
        this.tree = tree;
    }

    @Override
    public Player copy() {
        return new MiniMax(this.getName(), new Scenario(this.tree));
    }

    @Override
    public boolean decision(Game game) {
        // To complete
        return true;
    }

    private class Scenario {
        private Game game;
        private double[] score;
        private boolean isDone;
        private Scenario[] subtrees; // at index 0 is if it plays false and 1 if true

        public Scenario(Game game) {
            if (game.isDone()) {
                isDone = true;
                score = game.getScores();
            } else {
                // Game.playOneRound();
                // subtrees[0] = new Scenario();
                // Game game2 = new Game(game);
            }
            /*
             * required stuff:
             * functions:
             * game.getScores()
             * game.isDone()
             *
             * #possibilites
             * l=self.possibilites() // assigns sub trees
             * for i in range(len(l)):
             * l[i]=l[i].minimax() // for each sub tree, make it the value of the results
             *
             * #selecionner_branche
             * return max(l) if self.tour==True else min(l) // returns the min/max else
             * false
             */
        }

        public Scenario(Scenario scenario) {
            this.game = scenario.game;
            this.isDone = scenario.game.isDone();
        }

    }

}