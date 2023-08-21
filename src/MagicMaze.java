/* Nicholas Aristizabal */

import java.io.*;
import java.util.HashSet;

public class MagicMaze {

    private final char[][] maze;
    private final int row, col;
    private final String mazeNum;

    HashSet<Integer> trapRooms;
    private final int[] rowMoves = {-1, 1, 0, 0};
    private final int[] colMoves = {0, 0, 1, -1};

    public MagicMaze(String mazeNum, int row, int col) throws IOException {
        this.mazeNum = mazeNum;
        this.row = row;
        this.col = col;
        this.maze = new char[row][col];
        readMaze();

        this.trapRooms = new HashSet<>();
    }

    // Translates the characters in the maze file into numbers
    private void readMaze() throws IOException {
        File mazeFile = new File(this.mazeNum);

        BufferedReader mazeInput = new BufferedReader(new FileReader(mazeFile));

        for(int r=0; r<this.row; r++)
            for(int c=0; c<this.col; c++){
                char spot = (char) mazeInput.read();
                if(spot == '\n' || spot == '\r')
                    c--;
                else
                    maze[r][c] = spot;
            }

    }

    public void printMaze(){
        for(int r = 0; r<row; r++){
            for(int c=0; c<col; c++)
                System.out.print(this.maze[r][c]);
            System.out.println();
        }
    }

    // This method translates the character maze into an integer maze. This makes it easier to make comparison
    // especially with the teleports. (personally)
    private int[][] translateMazeToInt(char[][] maze){

        int[][] mazeCopy = new int[this.row][this.col];

        for(int r=0; r<this.row; r++){
            for(int c=0; c<this.col; c++){
                char spot = maze[r][c];
                if(spot == '*')     // If an * replace with -1 (paths)
                    mazeCopy[r][c] = -1;
                else if(spot == '@') // If an @ replace with -11 (walls)
                    mazeCopy[r][c] = -11;
                else if(spot == 'X') // If an X replace with -7 (the goal)
                    mazeCopy[r][c] = -7;
                else if(spot == '\r' || spot == '\n') // If new line or return, skip it
                    c--;
                else
                    mazeCopy[r][c] = spot-48; // 48 is 0 in unicode. This translates the teleports into decimal
            }
        }

        return mazeCopy;
    }

    // Checks to see if the current position we are in is a legal position
    private boolean isValidMove(int r, int c, int[][] maze){

        // Checks to see if we are out of bounds
        if(r>=this.row || r<0 || c<0 || c>=this.col)
            return false;

        // Checks to see if we have already been here
        if(maze[r][c] == -9)
            return false;

        // Check if we are at a wall
        return maze[r][c] != -11;
    }

    // This locates the opposite teleport pad of the one we are standing on and saves them into
    // an array {row, col}
    private int[] findTeleportPos(int teleport, int r, int c, int[][] maze){

        int[] pos = new int[2];
        pos[0] = r;
        pos[1] = c;

        for(int row=0; row<this.row; row++)
            for(int col=0; col<this.col; col++){
                if(maze[row][col] == teleport && !(row == r && col == c)){
                    pos[0] = row;
                    pos[1] = col;
                    return pos;
                }
            }

        return pos;
    }

    // Uses a flood fill algorithm to locates and marks any teleport pads in the room that is not the
    // one we came in from
    private void fillRoom(int r, int c, int teleport, int[][] mazeCopy){

        // We this is an invalid move, return
        if(!isValidMove(r, c, mazeCopy)) return;

        // If we find a teleport pad that is not where we came from, or the goal, set that position
        // to -50 (this makes sense in checkRoom)
        if (mazeCopy[r][c] > -1 && mazeCopy[r][c] != teleport || mazeCopy[r][c] == -7){
            mazeCopy[r][c] = -50;
            return;

        // Set all values that is not -50 to -9 marking that we have been there
        }else if(mazeCopy[r][c] != -50)
            mazeCopy[r][c] = -9;

        // If we do see a -50, skip it as we have already checked it
        if(mazeCopy[r][c] == -50) return;

        // flood fill recursion
        fillRoom(r-1, c, teleport, mazeCopy);
        fillRoom(r+1, c, teleport, mazeCopy);
        fillRoom(r, c+1, teleport, mazeCopy);
        fillRoom(r, c-1, teleport, mazeCopy);
    }

    // Checks the entire board copy to see if we have the value -50
    private boolean checkRoom(int[][] mazeCopy){

        for(int r=0; r<row; r++)
            for(int c=0; c<col; c++)
                if(mazeCopy[r][c] == -50)
                    return true;

        return false;
    }

    // Checks to see if we are in a trapped room. Uses fillRoom and checkRoom.
    // Summary of each:
    // fillRoom will locate any teleport pad in our current room and mark it with the number -50
    //      if no pads are found, then no marks are made.
    // checkRoom will check the entire copy of the maze for the number -50, if it exists, then that
    //      means there is a teleport in our room, and we can proceed. If there is no -50, then there
    //      is no teleport in the room and there is no point in exploring, just go back.
    private boolean trapRoom(int r, int c, int teleport, int[][] maze){

        // Copies the main maze onto a separate array, so we can manipulate it without damaging
        // the main maze
        int[][] mazeCopy = new int[this.row][this.col];

        for(int i=0; i<this.row; i++)
            System.arraycopy(maze[i], 0, mazeCopy[i], 0, this.col);

        fillRoom(r, c, teleport, mazeCopy);
        return !checkRoom(mazeCopy);
    }

    // Wrapper function for backtracking
    public boolean solveMagicMaze(){

        int[][] maze = translateMazeToInt(this.maze);

        return solveMagicMazeR(row-1, 0, maze);
    }

    // Recursive function for backtracking
    public boolean solveMagicMazeR(int r, int c, int[][] maze){

        // Base case. Are we on the goal?
        if(maze[r][c] == -7) {
            return true;
        }

        // Store the current position and what it is (teleport, path, etc..)
        int storedState = maze[r][c];
        int[] storedPos = {r, c};

        // Make changes, set our position to -9, meaning we have been here
        maze[r][c] = -9;

        // Have we already teleported in this iteration (so we don't keep bouncing)
        boolean alreadyTeleported = false;

        // Iterate through the four possible moves
        for(int i=0; i<4; i++){

            // If we are on a teleport pad and have not teleported yet
            if(storedState > -1 && !alreadyTeleported && !this.trapRooms.contains(storedState)) {

                // Find the other teleport and teleport us there
                int[] newPos = findTeleportPos(storedState, r, c, maze);

                r = newPos[0];
                c = newPos[1];

                // Check if the room we teleported to is a trapped room
                // If it is, go back
                // If not, proceed
                if(trapRoom(r, c, storedState, maze)){
                    System.out.println("Trap Room "+ storedState);
                    this.trapRooms.add(storedState);
                    System.out.println(trapRooms);

                    maze[r][c] = storedState;
                    r = storedPos[0];
                    c = storedPos[1];
                }else{
                    maze[r][c] = -9;
                    alreadyTeleported = true;
                }
            }

            // Balance check, is our next move a valid move? If so, move
            if(isValidMove(r + rowMoves[i], c + colMoves[i], maze)) {
                if (solveMagicMazeR(r + rowMoves[i], c + colMoves[i], maze)) {
                    return true;
                }

            }
        }
        // Revert the changes we made (two for the possibility that we teleported, and we need
        // to go back
        maze[r][c] = storedState;
        r = storedPos[0];
        c = storedPos[1];
        maze[r][c] = storedState;

        return false;
    }
}
