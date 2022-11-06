package src;

import java.util.ArrayList;
import java.util.InputMismatchException;

public class Results {
    private ArrayList<Boolean> array1;
    private ArrayList<Boolean> array2;

    public Results() {
        this.array1 = new ArrayList<Boolean>();
        this.array2 = new ArrayList<Boolean>();
    }

    public Results(Results results) {
        this.array1 = new ArrayList<Boolean>();
        this.array2 = new ArrayList<Boolean>();
        for (int i = 0; i < results.getResultsPerRounds(0).size(); i++) {
            this.array1.add(array1.get(i));
            this.array2.add(array2.get(i));
        }
    }

    public ArrayList<Boolean> getResultsPerRounds(int playerNumber) {
        // returns what happens chronologically
        if (playerNumber == 0)
            return this.array1;
        else if (playerNumber == 1) {
            return this.array2;
        }
        throw new InputMismatchException("Invalid player number");
    }

    public int getSizeOfResults() {
        return array1.size();
    }

    /**
     * @return what happens per event, instead of chronologically (either both
     *         cooperate, only playerOne cooperates, only playerTwo cooperates, or
     *         both compete)
     */
    public int[] getResultsPerEvent() {
        int[] resultsPerEvent = new int[4];
        for (int i = 0; i < this.array1.size(); i++) {
            resultsPerEvent[(this.array1.get(i) ? 2 : 0) + (this.array2.get(i) ? 1 : 0)]++;
        }
        return resultsPerEvent;
    }

    public void addResult(boolean playerOne, boolean playerTwo) {
        this.array1.add(playerOne);
        this.array2.add(playerTwo);
    }

}
