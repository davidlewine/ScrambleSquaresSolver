
package scrambles;
import java.util.ArrayList;

public class Tile {
    int rotation = 0;
    String[] sides = new String[4];
    int squareId;
    
    public Tile(ArrayList<String> wordHalves){
        for(int i = 0; i < 4; i++){
            sides[i] = wordHalves.get((int)(8 * Math.random()));
        }
    }
    public Tile(String[] sidesArg, int id){
        //input is an array of the values of the consecutive sides of the tile
        sides = sidesArg;
        squareId = id;
    }
    
    public void rotate(int n){
        rotation = (rotation + n) % 4;
    }
    
    public String side(int n){
        return sides[(n + rotation) % 4];
    } 
}

