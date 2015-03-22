package scrambles;

import java.util.ArrayList;
import java.util.Arrays;

public class Game {

    int dim = 3;//gameboard dimension
    boolean solved = false;
    ArrayList<Tile> solution = new ArrayList<>();
    ArrayList<Tile> puzzle = new ArrayList<>();
    ArrayList<String> wordHalves = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "A", "B", "C", "D"));
   

    public Game() {
        ArrayList<Tile> tempPuzzle = new ArrayList<>();
        for (int i = 0; i < dim * dim; i++) {
            tempPuzzle.add(new Tile(wordHalves));
        }
        // make adjacent tiles match.
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim - 1; j++) {
                tempPuzzle.get(i * dim + j + 1).sides[3] = complement(tempPuzzle.get(i * dim + j).sides[1]);
                tempPuzzle.get((j + 1) * dim + i).sides[0] = complement(tempPuzzle.get(j * dim + i).sides[2]);
            }
        }
        
        //scramble puzzle by choosing tiles at random from "tempPuzzle", rotating them, and appending them to a new array "puzzle"
        while (tempPuzzle.size() > 0) {
            int r = (int)(tempPuzzle.size() * Math.random());
            Tile tempTile = tempPuzzle.get(r);
            tempTile.rotate((int) (4 * Math.random()));
            puzzle.add(tempTile);
            tempPuzzle.remove(r);
        }
        System.out.println("puzzle generated");
        printPuzzle(puzzle);
    }

    public void printPuzzle(ArrayList<Tile> p) {
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                System.out.print("  " + p.get(i * dim + j).side(0) + "     ");
            }
            System.out.println();
            for (int j = 0; j < dim; j++) {
                System.out.print(p.get(i * dim + j).side(3) + "   " + p.get(i * dim + j).side(1) + "   ");
            }
            System.out.println();
            for (int j = 0; j < dim; j++) {
                System.out.print("  " + p.get(i * dim + j).side(2) + "     ");
            }
            System.out.println();
            System.out.println();
        }
    }

    public void solve() {
        checkRemaining();
        if(solved) System.out.println("puzzle solved");
        printPuzzle(solution);
    }

    public void checkTile(int index){  //check if a puzzle tile fits in the next slot of the solution
        if (tileFits(solution, puzzle.get(index))) {
            solution.add(puzzle.get(index));
            puzzle.remove(index);
            checkRemaining();//check if remaining tiles can be made to fit
            if (!solved) {//remaining tiles can't be made to fit; put tile back in puzzle
                puzzle.add(index, solution.get(solution.size() - 1));
                solution.remove(solution.size() - 1);
            }
        }
    }

    public void checkRemaining(){ //check to see if remaining tiles can be made to fit in some arrangement.
        int currentTileIndex = 0; //set currentTile to the first of the remaining tiles.
        while (currentTileIndex < puzzle.size() && !solved) { 
            checkTile(currentTileIndex); //see if the current tile as rotated leads to a solution.
            if (!solved) { //curent tile as rotated didn't lead to a solution.
                puzzle.get(currentTileIndex).rotate(1); //rotate current tile.
                if (puzzle.get(currentTileIndex).rotation == 0) { //if current tile has been rotated through all positions.
                    currentTileIndex++; //get next tile.
                }
            }
        }
        if (solution.size() == dim * dim) { // if solution uses all tiles.
            solved = true;
        }
    }

    public boolean tileFits(ArrayList<Tile> solution, Tile t) {
        if (solution.size() % dim > 0) {//if the new tile will have adjacent neighbor to its left, check that it matches
            if (!solution.get(solution.size() - 1).side(1).equals(wordHalves.get((wordHalves.indexOf(t.side(3)) + 4)%8))){
                return false;
            }
        }
        if (solution.size() >= dim) {//if the new tile will have a neighbor above, check that it matches
            if (!solution.get(solution.size() - dim).side(2).equals(wordHalves.get((wordHalves.indexOf(t.side(0)) + 4)%8))){
                return false;
            }
        }
        return true;
    }
    
    public String complement(String s){  //returns word half that matches s.
        return wordHalves.get((wordHalves.indexOf(s) + 4) % 8);
    }
}
