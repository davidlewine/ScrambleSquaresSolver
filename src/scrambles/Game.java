package scrambles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.util.ArrayList;
import java.util.Arrays;

public class Game {

    int dim = 3;//gameboard dimension
    boolean solved = false;
    ArrayList<Tile> solution = new ArrayList<>();
    ArrayList<Tile> puzzle;
    ArrayList<String> wordHalves = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "A", "B", "C", "D"));
    BufferedImage letNumImg;
   

    public Game() {
        puzzle = new ArrayList<>();
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
        letNumImg = printPuzzle(puzzle);
    }
    
    public Game(Square[][] squares) {
        puzzle = new ArrayList<>();
        for (Square[] row: squares){
            for(Square square: row){
                String[] edgeValues = new String[4];
                for(int i = 0; i < 4; i++){
                    edgeValues[i] = square.edges[i].value;
                }
                puzzle.add(new Tile(edgeValues, square.idNum));
            }
        }
        
        System.out.println("puzzle generated");
        letNumImg = printPuzzle(puzzle);
       
    }
    public BufferedImage getLetNumImg(){
        return letNumImg;
    }

    public BufferedImage printPuzzle(ArrayList<Tile> p) {
        int tileDim = 50, tileBuffer = 30;
        BufferedImage img = new BufferedImage(3*tileDim + 4*tileBuffer, 3*tileDim + 4*tileBuffer, TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0, img.getWidth(), img.getHeight());
        g.setColor(Color.gray);
        int startX = tileBuffer;
        int startY = tileBuffer;
        int x = startX - tileBuffer/4;
        int y = startY - tileBuffer/2;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                g.fillRect(x, y, tileDim + (3*tileBuffer)/4, tileDim + (3*tileBuffer)/4);
                x+= tileDim + tileBuffer;
            }
            x = startX-tileBuffer/5; 
            y+= tileDim + tileBuffer;
        }
        g.setColor(Color.white);   
        x = startX + tileDim/2;
        y = startY;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if(i*dim+j<p.size()){
                System.out.print("  " + p.get(i * dim + j).side(0) + "     ");
                g.drawString("" + p.get(i * dim + j).side(0), x, y);
                x+= tileDim + tileBuffer;
                }
            }
            System.out.println();
            y+= tileDim/2;
            x = startX;
            for (int j = 0; j < dim; j++) {
                if(i*dim+j<p.size()){
                System.out.print(p.get(i * dim + j).side(3) + "   " + p.get(i * dim + j).side(1) + "   ");
                g.drawString("" + p.get(i * dim + j).side(3), x, y);
                x+= tileDim;
                g.drawString("" + p.get(i * dim + j).side(1), x, y);
                x+= tileBuffer;
                }
            }
            System.out.println();
            y+= tileDim/2;
            x = startX + tileDim/2;
            for (int j = 0; j < dim; j++) {
                if(i*dim+j<p.size()){
                System.out.print("  " + p.get(i * dim + j).side(2) + "     ");
                g.drawString("" + p.get(i * dim + j).side(2), x, y);
                x+= tileDim + tileBuffer;
                }
            }
            System.out.println();
            System.out.println();
            y+= tileBuffer;
            x = startX + tileDim/2;
        }
        return img;
    }

    public ArrayList<Tile> solve() {
        checkRemaining();
        if(solved){
            System.out.println("puzzle solved");
            printPuzzle(solution);
        }
        else{
            System.out.println("not solved");
            printPuzzle(solution);
        }
        return solution;
    }

    public void checkTile(int index){  //check if a puzzle tile fits in the next slot of the solution
        if (tileFits(solution, puzzle.get(index))) {
            solution.add(puzzle.get(index));
            //printPuzzle(solution);
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
