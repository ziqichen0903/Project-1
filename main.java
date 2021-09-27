/*
    For main idea: Read TXT file, and store the string into a 2D matrix, delete "*-----------*". In this way,
    the "-" indicates the obstacle, and the letters and Spaces indicate that it can be passed.
    For the BFS, using queue to  implemented, From the destination to find the start point.
    For the DFS, using stack to implemented, recursive to traverse.
    In the end, Print path coordinates and write the solution for txt file.
 */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Test {

    public static void main(String[] args) throws IOException {

        // Read txt file, using stream
        File file = new File("D:\\CS350\\maze_3.txt");
        BufferedReader bis = new BufferedReader(new FileReader(file));
        String str = null;
        StringBuffer sb = new StringBuffer();

        // One line String length
        int arrLenth = 0;
        int maxStrLength = 0;

        //Read line by line
        while((str = bis.readLine()) != null) {
            sb.append(str + "\n");
            arrLenth++;
            if(str.length() > maxStrLength) {
                maxStrLength = str.length();
            }
        }
        //Created a 2D matrix
        String[][] arrs = new String[arrLenth][maxStrLength];
        str = sb.toString();

        // split
        String[] tempArr = str.split("\n");
        for (int i = 0; i < tempArr.length; i++) {
            String tempStr = tempArr[i];
            for (int j = 0; j < tempStr.length(); j++) {
                arrs[i][j] = String.valueOf(tempStr.charAt(j));
            }
        }

        // Read done
        bis.close();

        // delete "*-----------*"
        String[][] newArray = remove(arrs);

        //Replace the Spaces in the first and last columns with "-"
        for (int m =0; m < newArray.length; m++) {
            if (newArray[m][0].equals(" ")) {
                newArray[m][0] = "-";
            }
            if (newArray[m][newArray[0].length - 1].equals(" ")) {
                newArray[m][newArray[0].length - 1] = "-";
            }
        }

        // find the start point and end point
        String target = ">";

        int sourceX = 0; // initialize the source x coordinate
        int sourceY = 0; // The source point must be in the first column

        int endX = 0; // initialize the end x coordinate
        int endY = newArray[0].length -1; // The end point must be in the last column

        // for loop, find the sourceX and endX values
        for (int i = 0; i < newArray.length; i++) {

            if (target.equals(newArray[i][0])) {
                sourceX = i;
            }
            if (target.equals(newArray[i][newArray[0].length -1])) {
                endX = i;
            }
        }

        //The source point at the back of the "|" replace Spaces, connected to the letter
        //The end point in front of the "|" replace Spaces, connected to the letter
        newArray[sourceX][sourceY + 1] = " ";
        newArray[endX][endY -1] = " ";

        // The rest of the "|" replace "-"
        String point = "|";
        for (int i = 0; i <newArray.length; i++) {
            if (point.equals(newArray[i][1])) {
                newArray[i][1] = "-";
            }
            if (point.equals(newArray[i][newArray[0].length - 2])) {
                newArray[i][newArray[0].length - 2] = "-";
            }
        }

        StringBuilder sb1 = new StringBuilder(); // String Builder to create the table structure before writing it to the file.

        for (String[] int1 : newArray) {
            for (int j = 0; j < int1.length; j++) {
                sb1.append(int1[j]).append("\t"); // Add tab to delimite the elements
            }
            sb1.append("\r\n"); // Add new line character
        }
        Path path = Paths.get("D:\\CS350\\maze_3_matrix.txt"); // The path to your file
        Files.write(path, sb1.toString().getBytes()); // Writes to that path the bytes in the string from the stringBuilder object.

        // DFS, created stack
        Stack<String> stack = new Stack<>();
        ArrayList<String> list = new ArrayList<>();

        // call func
        DFS(newArray, sourceX, sourceY, endX, endY,stack, list);

        // Store stack.pop() to the list
        List result = new ArrayList<>();
        while(!stack.empty()) {
            result.add(stack.pop());
        }

        // write the solution_dfs for txt file.
        BufferedWriter out = new BufferedWriter(new FileWriter("D:\\CS350\\maze_3_solution_dfs.txt"));
        out.write("The path is: ");
        out.write(String.valueOf(result));
        out.close();


        // BFS, created queue
        Queue<Node> queue = new LinkedList<>();
        ArrayList<String>result1 = new ArrayList<>();

        // call BFS func
        BFS(newArray, sourceX, sourceY, endX, endY, queue, result1);

        //write the result to solution_bfs file
        BufferedWriter output = new BufferedWriter(new FileWriter("D:\\CS350\\maze_3_solution_bfs.txt"));
        output.write("The path is: ");
        output.write(String.valueOf(result1));
        output.close();

    }

    // remove func
    public static String[][] remove(String[][] arrs) {

        // Deletes the specific row
        String[][] arr2 = Arrays.stream(arrs)
                .filter(row -> Arrays.stream(row).noneMatch(i -> i.contains("*")))
                .toArray(String[][]::new);

        // return the new matrix
        return arr2;
    }

    // DFS func
    private static boolean DFS(String[][] newArray, int sourceX, int sourceY, int endX, int endY, Stack<String> stack, ArrayList<String> list) {

        // corner case
        if (sourceX < 0 || sourceY < 0 || sourceX >= newArray.length || sourceY >= newArray[0].length) {
            return false;
        }
        if (sourceX == endX && sourceY == endY) {
            return true;
        }
        String s = String.format("(%d,%d)", sourceX, sourceY);

        //if contained return false, else add to list
        if (list.contains(s)) {
            return false;
        } else {
            list.add(s);
        }

        // Encountering obstacles, return
        if ("-".equals(newArray[sourceX][sourceY])) {
            return false;
        }
        if (sourceX == endX && sourceY == endY) {
            stack.push(s);
            return true;
        }

        // recursive,  traverse the matrix
        boolean ret1 = DFS(newArray, sourceX + 1, sourceY, endX, endY, stack, list);
        boolean ret2 = DFS(newArray, sourceX, sourceY + 1, endX, endY, stack, list);
        boolean ret4 = DFS(newArray, sourceX - 1, sourceY, endX, endY, stack, list);
        boolean ret3 = DFS(newArray, sourceX, sourceY - 1, endX, endY, stack, list);

        if (ret1 || ret2 || ret3 || ret4) {
            stack.push(s);
        }

        //return result
        return ret1 || ret2 || ret3 || ret4;
    }

    // BFS func
    private static boolean BFS(String[][] newArray, int sourceX, int sourceY, int endX, int endY, Queue<Node> queue, ArrayList<String> result1) {

        //created visited point to recording
        boolean visited[][] = new boolean[40][40];

        // created stack , the type is Node
        Stack<Node> path = new Stack<>();

        // source point
        Node start = new Node(sourceX, sourceY);

        //flag the source point
        visited[0][0] = true;

        // end point
        Node end = new Node(endX, endY);

        //current , move
        Node cur = new Node(0, 0, 0, 0);
        Node move = new Node(0, 0, 0, 0);

        // // Four directions(left, right, down, up)
        int dir[][] = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

        queue.offer(start);

        //Backtracking
        while (!queue.isEmpty()) {
            cur = queue.poll();
            path.push(cur);

            // traverse four directions
            for (int i = 0; i < 4; i++) {
                move.x = cur.x + dir[i][0];
                move.y = cur.y + dir[i][1];
                move.prev_x = cur.x;
                move.prev_y = cur.y;

                if (move.x == end.x && move.y == end.y) {
                    while (!path.isEmpty()) {
                        Node show_path = path.pop();

                        if (move.prev_x == show_path.x && move.prev_y == show_path.y) {
                            result1.add("(" + show_path.x + ", " + show_path.y + ")");

                            move = show_path;

                        }
                    }

                    return true;
                }

                // Judgment condition, "-" is obstacle
                if (move.x >= 0 && move.x < newArray.length && move.y >= 0 && move.y < newArray[0].length && (!"-".equals(newArray[move.x][move.y]))
                        && (!visited[move.x][move.y])) {
                    Node new_node = new Node(move.x, move.y, move.prev_x, move.prev_y);
                    queue.offer(new_node);
                    visited[move.x][move.y] = true;
                }
            }
        }
        return false;
    }

    // Node class
    public static class Node {
        public int x;
        public int y;
        public int prev_x;
        public int prev_y;
        public Node(int X, int Y, int PREV_X, int PREV_Y) {
            this.x = X;
            this.y = Y;
            this.prev_x = PREV_X;
            this.prev_y = PREV_Y;
        }
        public Node(int X, int Y) {
            this.x = X;
            this.y = Y;
        }

    }


}
