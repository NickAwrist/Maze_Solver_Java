import java.io.IOException;

public class MazeRunner {

    public static void main(String[] args) throws IOException {

        MagicMaze maze1 = new MagicMaze("maze1.txt", 11, 15);
        MagicMaze maze2 = new MagicMaze("maze2.txt", 11, 15);
        MagicMaze maze3 = new MagicMaze("maze3.txt", 11, 15);
        MagicMaze maze4 = new MagicMaze("maze4.txt", 15, 20);
        MagicMaze maze5 = new MagicMaze("maze5.txt", 15, 20);

        if(maze1.solveMagicMaze())
            System.out.println("Maze 1 complete!");
        if(maze2.solveMagicMaze())
            System.out.println("Maze 2 complete!");
        if(maze3.solveMagicMaze())
            System.out.println("Maze 3 complete!");
        if(maze4.solveMagicMaze())
            System.out.println("Maze 4 complete!");
        if(maze5.solveMagicMaze())
            System.out.println("Maze 5 complete!");

        maze1.printMaze();


    }

    // Storage for later maybe


//    public void printMaze(){
//
//        for(int r=0; r<this.row; r++){
//            for(int c=0; c<this.col; c++)
//                System.out.print(this.maze[r][c]+"\t");
//            System.out.println();
//        }
//
//        System.out.println();
//        System.out.println();
//        System.out.println();
//
//    }




}
