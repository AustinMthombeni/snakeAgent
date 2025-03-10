import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class MyAgent extends za.ac.wits.snake.DevelopmentAgent {

    public static void main(String args[]) {
        MyAgent agent = new MyAgent();
        MyAgent.start(agent, args);
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String initString = br.readLine();
            String[] temp = initString.split(" ");
            int nSnakes = Integer.parseInt(temp[0]);

            while (true) {
                String line = br.readLine();
                if (line.contains("Game Over")) {
                    break;
                }

                
                String apple1 = line;
                String[] appleCoordinates = apple1.split(" ");
                int appleX = Integer.parseInt(appleCoordinates[0]);
                int appleY = Integer.parseInt(appleCoordinates[1]);

                
                String[][] playArea = new String[50][50];
                for (int i = 0; i < 50; i++) {
                    for (int j = 0; j < 50; j++) {
                        playArea[i][j] = "0"; 
                    }
                }

                
                int nObstacles = 3;
                for (int obstacle = 1; obstacle < nObstacles + 1; obstacle++) {
                    String obs = br.readLine();
                    drawObstacle(obs, obstacle, playArea);  
                }

               
                int nZombies = 3;
                String zom1HeadCoordinates = "";
                String zom2HeadCoordinates = "";
                String zom3HeadCoordinates = "";
                for (int zombie = 1; zombie < nZombies + 1; zombie++) {
                    String zom = br.readLine();
                    if(zombie == 1) {
                    	zom1HeadCoordinates = zom.split(" ")[0];
                    }
                    if(zombie == 2) {
                    	zom2HeadCoordinates = zom.split(" ")[0];
                    }
                    if(zombie == 3) {
                    	zom3HeadCoordinates = zom.split(" ")[0];
                    }
                    
                    
                    drawObstacle(zom, zombie, playArea);
                }
                String[] zom1headCoords = zom1HeadCoordinates.split(",");
                int zom1headX = Integer.parseInt(zom1headCoords[0]);
                int zom1headY = Integer.parseInt(zom1headCoords[1]);
                
                String[] zom2headCoords = zom2HeadCoordinates.split(",");
                int zom2headX = Integer.parseInt(zom2headCoords[0]);
                int zom2headY = Integer.parseInt(zom2headCoords[1]);
                
                String[] zom3headCoords = zom3HeadCoordinates.split(",");
                int zom3headX = Integer.parseInt(zom3headCoords[0]);
                int zom3headY = Integer.parseInt(zom3headCoords[1]);
                

                int mySnakeNum = Integer.parseInt(br.readLine());
                String headCoordinates = "";
                
                String[] otherSnakes = new String[nSnakes - 1];  
                int otherSnakeIndex = 0; 

                for (int i = 0; i < nSnakes; i++) {
                    String snakeLine = br.readLine();
                    if (i == mySnakeNum) {
                        headCoordinates = snakeLine.split(" ")[3]; 
                   } else {
                      otherSnakes[otherSnakeIndex] = snakeLine; 
                       otherSnakeIndex++; 
                    }
                    drawSnake(snakeLine, i + 1, playArea); 
                }

                
                String[] headCoords = headCoordinates.split(",");
                int headX = Integer.parseInt(headCoords[0]);
                int headY = Integer.parseInt(headCoords[1]);

                int aliveSnakes = 0;
               int[][] otherSnakesHeadCoords = new int[otherSnakes.length][2]; 

                for (int j = 0; j < otherSnakes.length; j++) {
                    
                    String[] parts = otherSnakes[j].split(" ");
                    if (parts.length >= 4) { 
                    	if (parts.length >= 4) { 
                            String status = parts[0]; 
                            
                           
                            if (status.equals("alive")) {
                                aliveSnakes++; 
                            }
                        String otherSnakeHeadCoords = parts[3]; 
                        String[] coordsSplit = otherSnakeHeadCoords.split(",");
                        otherSnakesHeadCoords[j][0] = Integer.parseInt(coordsSplit[0]); 
                        otherSnakesHeadCoords[j][1] = Integer.parseInt(coordsSplit[1]); 
                    } 
                    	else {
                        
         
                        otherSnakesHeadCoords[j][0] = -1; 
                        otherSnakesHeadCoords[j][1] = -1; 
                    }
                }
                int[] distancesToApple = new int[otherSnakesHeadCoords.length + 1]; 
                distancesToApple[0] = manhattanDistance(appleX, appleY, headX, headY); 
                for (int i = 0; i < otherSnakesHeadCoords.length; i++) {
                    distancesToApple[i + 1] = manhattanDistance(appleX, appleY, otherSnakesHeadCoords[i][0], otherSnakesHeadCoords[i][1]);
                }               
                int bestMove;
                boolean isClosest = true;
                boolean closest = true;
                for (int k = 1; k < distancesToApple.length; k++) {
                    if (distancesToApple[0] >= distancesToApple[k]) {
                        isClosest = false; 
                        break;
                    }
                }
                for (int k = 1; k < distancesToApple.length; k++) {
                    if (distancesToApple[0] == distancesToApple[k]) {
                        closest = false; 
                        
                        break;
                    }
                    
                    
                }
                if(aliveSnakes<3) {
                	bestMove= aStarMove(playArea, headX, headY, appleX, appleY, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
                }
                else if (isClosest&&closest) {
                    bestMove = aStarMove(playArea, headX, headY, appleX, appleY, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
                } else {
                    bestMove = calculateMove(headX, headY,playArea, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
                }
                System.out.println(bestMove);
            }
        }
            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int aStarMove(String[][] playArea, int HeadX, int HeadY, int appleX, int appleY,int zom1headX,int zom1headY,int zom2headX,int zom2headY,int zom3headX,int zom3headY) {
        Node startNode = new Node(HeadX, HeadY, null);
        Node targetNode = new Node(appleX, appleY, null);

        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        Set<String> closedList = new HashSet<>();
        openList.add(startNode);
        int move = zigzagMove(HeadX, HeadY,playArea, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();
            closedList.add(currentNode.x + "," + currentNode.y);

           
            if (currentNode.x == appleX && currentNode.y == appleY) {
                return reconstructMove(currentNode,playArea,HeadX,HeadY,zom1headX,zom1headY,zom2headX,zom2headY,zom3headX, zom3headY);  // Return the first move towards the apple
            }
            List<Node> neighbors = getNeighbors(currentNode, playArea);

            for (Node neighbor : neighbors) {
                if (closedList.contains(neighbor.x + "," + neighbor.y)) continue;

                int tentativeGCost = currentNode.gCost + 1;
                if (!isInOpenList(openList, neighbor) || tentativeGCost < neighbor.gCost) {
                    neighbor.calculateCosts(targetNode, tentativeGCost);
                    neighbor.parent = currentNode;

                    if (!isInOpenList(openList, neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }
        return move;

        

    }

    
    private static int reconstructMove(Node currentNode, String[][] playArea, int headX, int headY,int zom1headX, int zom1headY, int zom2headX, int zom2headY, int zom3headX, int zom3headY) {
		while (currentNode.parent != null && currentNode.parent.parent != null) {
		currentNode = currentNode.parent;  
		}

		int HeadZombie1Distance = manhattanDistance(headX, headY, zom1headX, zom1headY);
		int HeadZombie2Distance = manhattanDistance(headX, headY, zom2headX, zom2headY);
		int HeadZombie3Distance = manhattanDistance(headX, headY, zom3headX, zom3headY);
		
		int xDirection = currentNode.x - currentNode.parent.x;
		int yDirection = currentNode.y - currentNode.parent.y;
		
		
		
		if (xDirection == 1 && HeadZombie1Distance >= 3 && HeadZombie2Distance >= 3 && HeadZombie3Distance >= 3) {
			if (isSafe(playArea, headX+1, headY)) {
	            return 3;  
			}
	        
	    }

	    
	    if (xDirection == -1 && HeadZombie1Distance >= 3 && HeadZombie2Distance >= 3 && HeadZombie3Distance >= 3) {
	    	if (isSafe(playArea, headX-1, headY)) {
	            return 2;  
	    	}
	    }

	 
	    if (yDirection == 1  && HeadZombie1Distance >= 3 && HeadZombie2Distance >= 3 && HeadZombie3Distance >= 3) {
	    	if (isSafe(playArea, headX, headY+1)) {
	            return 1;  
	    	}
	        
	    }

	   
	    if (yDirection == -1 && HeadZombie1Distance >= 3 && HeadZombie2Distance >= 3 && HeadZombie3Distance >= 3) {
	    	if (isSafe(playArea, headX, headY-1)) {
	            return 0;
	    }
	    }
	        
		


		List<Integer> safeMoves = new ArrayList<>();
		
		if (isSafe(playArea, headX, headY - 1)) { 
			safeMoves.add(0);
		}
		if (isSafe(playArea, headX, headY + 1)) { 
			safeMoves.add(1);
		}
		if (isSafe(playArea, headX - 1, headY)) {
			safeMoves.add(2);
		}
		if (isSafe(playArea, headX + 1, headY)) {
			safeMoves.add(3);
		}
	
	
		for (int move : safeMoves) {
		    int newX;
		    int newY;
		    if (move == 2) {
		        newX = headX - 1;  
		    } 
		    else if (move == 3) {
		        newX = headX + 1;  
		    } 
		    else {
		        newX = headX;      
		    }
		    if (move == 0) {
		        newY = headY - 1;  
		    } 
		    else if (move == 1) {
		        newY = headY + 1;  
		    } 
		    else {
		        newY = headY;      
		    }

			if (manhattanDistance(newX, newY, zom1headX, zom1headY) >= 2 &&manhattanDistance(newX, newY, zom2headX, zom2headY) >= 2 &&manhattanDistance(newX, newY, zom3headX, zom3headY) >=2) {
				return move; 
			}
			
		
		}

		if (safeMoves.isEmpty()) {
		    return new Random().nextInt(4);
		} 
		else {
		   
		    int randomIndex = new Random().nextInt(safeMoves.size()); 
		    return safeMoves.get(randomIndex); 
		}

}

    private static boolean isSafe(String[][] playArea, int x, int y) {
        return x >= 0 && x < 50&& y >= 0 && y < 50&& playArea[x][y].equals("0");  
    }
    public static int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }


    private static List<Node> getNeighbors(Node node, String[][] playArea) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};  

        for (int[] direction : directions) {
            int newX = node.x + direction[0];
            int newY = node.y + direction[1];

            if (newX >= 0 && newX < playArea.length && newY >= 0 && newY < playArea[0].length
                    && playArea[newX][newY].equals("0")) {  
                neighbors.add(new Node(newX, newY, node));
            }
        }

        return neighbors;
    }

    private static boolean isInOpenList(PriorityQueue<Node> openList, Node node) {
        return openList.stream().anyMatch(n -> n.x == node.x && n.y == node.y);
    }

    static class Node {
        int x, y;
        Node parent;
        int gCost, hCost, fCost;

        public Node(int x, int y, Node parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

        public void calculateCosts(Node target, int gCostFromStart) {
            this.gCost = gCostFromStart;
            this.hCost = Math.abs(target.x - this.x) + Math.abs(target.y - this.y);  
            this.fCost = gCost + hCost;
        }
    }

    
    public static void drawSnake(String snakeInfo, int snakeNumber, String[][] playArea) {
        String[] values = snakeInfo.split(" ");
        for (int i = 3; i < values.length - 1; i++) {
            drawLine(playArea, values[i], values[i + 1], snakeNumber);
        }
    }

    public static void drawObstacle(String obstacle, int obsNumber, String[][] playArea) {
        String[] values = obstacle.split(" ");
        for (int i = 0; i < values.length - 1; i++) {
            drawLine(playArea, values[i], values[i + 1], obsNumber);
        }
    }

    public static void drawLine(String[][] playArea, String coOrd1, String coOrd2, int snakeNum) {
        String[] values = coOrd1.split(",");
        String[] values2 = coOrd2.split(",");
        int x1 = Integer.parseInt(values[0]);
        int x2 = Integer.parseInt(values2[0]);
        int y1 = Integer.parseInt(values[1]);
        int y2 = Integer.parseInt(values2[1]);
        int xMin = Math.min(y1, y2);
        int xMax = Math.max(y1, y2);
        int yMin = Math.min(x1, x2);
        int yMax = Math.max(x1, x2);
        for (int i = xMin; i <= xMax; i++) {
            for (int j = yMin; j <= yMax; j++) {
                playArea[j][i] = String.valueOf(snakeNum);
            }
        }
    }
    public static int zigzagMove(int headX, int headY, String playArea[][], int zom1headX, int zom1headY, int zom2headX, int zom2headY, int zom3headX, int zom3headY) {
        
        int zigzagLimit = 50;
        if (headX == zigzagLimit - 1) {
            if (isSafe(playArea, headX, headY + 1) && 
                manhattanDistance(headX, headY + 1, zom1headX, zom1headY) >= 3 &&
                manhattanDistance(headX, headY + 1, zom2headX, zom2headY) >= 3 &&
                manhattanDistance(headX, headY + 1, zom3headX, zom3headY) >= 3) {
                return 1;
            }
            
            if (isSafe(playArea, headX - 1, headY) &&
                manhattanDistance(headX - 1, headY, zom1headX, zom1headY) >= 3 &&
                manhattanDistance(headX - 1, headY, zom2headX, zom2headY) >= 3 &&
                manhattanDistance(headX - 1, headY, zom3headX, zom3headY) >= 3) {
                return 2; 
            }
        } if (headX == 0) {
            
            if (isSafe(playArea, headX, headY + 1) &&
                manhattanDistance(headX, headY + 1, zom1headX, zom1headY) >= 3 &&
                manhattanDistance(headX, headY + 1, zom2headX, zom2headY) >= 3 &&
                manhattanDistance(headX, headY + 1, zom3headX, zom3headY) >= 3) {
                return 1; 
            }
           
            if (isSafe(playArea, headX + 1, headY) &&
                manhattanDistance(headX + 1, headY, zom1headX, zom1headY) >= 3 &&
                manhattanDistance(headX + 1, headY, zom2headX, zom2headY) >= 3 &&
                manhattanDistance(headX + 1, headY, zom3headX, zom3headY) >= 3) {
                return 3; 
            }
        }

        
        if ((headY % 2 == 0) && isSafe(playArea, headX + 1, headY)) {
            
            if (manhattanDistance(headX + 1, headY, zom1headX, zom1headY) >= 3 &&
                manhattanDistance(headX + 1, headY, zom2headX, zom2headY) >= 3 &&
                manhattanDistance(headX + 1, headY, zom3headX, zom3headY) >= 3) {
                return 3; 
            }
        } if (isSafe(playArea, headX - 1, headY)) {
            
            if (manhattanDistance(headX - 1, headY, zom1headX, zom1headY) >= 3 &&
                manhattanDistance(headX - 1, headY, zom2headX, zom2headY) >= 3 &&
                manhattanDistance(headX - 1, headY, zom3headX, zom3headY) >=3) {
                return 2; 
            }
        }

        
List<Integer> safeMoves = new ArrayList<>();
		
		
		if (isSafe(playArea, headX, headY - 1)) { 
			safeMoves.add(0);
		}
		if (isSafe(playArea, headX, headY + 1)) { 
			safeMoves.add(1);
		}
		if (isSafe(playArea, headX - 1, headY)) {
			safeMoves.add(2);
		}
		if (isSafe(playArea, headX + 1, headY)) {
			safeMoves.add(3);
		}
	
	
		for (int move : safeMoves) {
		    int newX;
		    int newY;
		    if (move == 2) {
		        newX = headX - 1;  
		    } 
		    else if (move == 3) {
		        newX = headX + 1;  
		    } 
		    else {
		        newX = headX;      
		    }
		    if (move == 0) {
		        newY = headY - 1;  
		    } 
		    else if (move == 1) {
		        newY = headY + 1;  
		    } 
		    else {
		        newY = headY;      
		    }

			if (manhattanDistance(newX, newY, zom1headX, zom1headY) >= 2 &&manhattanDistance(newX, newY, zom2headX, zom2headY) >= 2 &&manhattanDistance(newX, newY, zom3headX, zom3headY) >=2) {
				return move; 
			}
			
		
		}

		if (safeMoves.isEmpty()) {
		    return new Random().nextInt(4);
		} 
		else {
		   
		    int randomIndex = new Random().nextInt(safeMoves.size()); 
		    return safeMoves.get(randomIndex); 
		}
    }
   
    	public static int calculateMove(int headX, int headY, String playArea[][], int zom1headX, int zom1headY, int zom2headX, int zom2headY, int zom3headX, int zom3headY) {
            
    	    int zigzagLimit = 50; 
    	    int nextY = (headY + 4) % playArea.length; 
    	    
    	 
    	    if (headX == 0 && headY == 49) {
    	        if (isSafe(playArea, headX + 1, headY)) {
    	            return aStarMove(playArea, headX, headY, headX + 1, headY, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        } else if (isSafe(playArea, headX, headY - 1)) { 
    	            return aStarMove(playArea, headX, headY, headX, headY - 1, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        }
    	    }

    	    
    	    if (headX == 49 && headY == 0) {
    	        if (isSafe(playArea, headX - 1, headY)) { 
    	            return aStarMove(playArea, headX, headY, headX - 1, headY, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        } else if (isSafe(playArea, headX, headY + 1)) { 
    	            return aStarMove(playArea, headX, headY, headX, headY + 1, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        }
    	    }

    	   
    	    if (headX == 0 && headY == 0) {
    	        if (isSafe(playArea, headX + 1, headY)) {
    	            return aStarMove(playArea, headX, headY, headX + 1, headY, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        } else if (isSafe(playArea, headX, headY + 1)) { 
    	            return aStarMove(playArea, headX, headY, headX, headY + 1, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        }
    	    }

    	 
    	    if (headX == 49 && headY == 49) {
    	        if (isSafe(playArea, headX - 1, headY)) { 
    	            return aStarMove(playArea, headX, headY, headX - 1, headY, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        } else if (isSafe(playArea, headX, headY - 1)) { 
    	            return aStarMove(playArea, headX, headY, headX, headY - 1, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        }
    	    }
 	    
 
    	    if (headX >= zigzagLimit - 1) {
    	        if (isSafe(playArea, headX - 1, nextY)) { 
    	            return aStarMove(playArea, headX, headY, headX - 1, nextY, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        }
    	        
    	    } 

    	    else if (headX <= 0) {
    	        if (isSafe(playArea, headX + 1, nextY)) {
    	            return aStarMove(playArea, headX, headY, headX + 1, nextY, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        }
    	        
    	    }


    	    
    	    if (headY % 4 == 0) {
    	        
    	        if (isSafe(playArea, headX + 1, headY)) {
    	            return aStarMove(playArea, headX, headY, headX + 1, headY, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        }
    	    } else {
    	        
    	        if (isSafe(playArea, headX - 1, headY)) {
    	            return aStarMove(playArea, headX, headY, headX - 1, headY, zom1headX, zom1headY, zom2headX, zom2headY, zom3headX, zom3headY);
    	        }
    	    }


    	    
    	    List<Integer> safeMoves = new ArrayList<>();
    	  
    	    

    	    
    	    if (isSafe(playArea, headX, headY - 1)) { 
    	        safeMoves.add(0); 
    	    }
    	    if (isSafe(playArea, headX, headY + 1)) { 
    	        safeMoves.add(1);
    	    }
    	    if (isSafe(playArea, headX - 1, headY)) {
    	        safeMoves.add(2);
    	    }
    	    if (isSafe(playArea, headX + 1, headY)) {
    	        safeMoves.add(3);
    	    }

    	    
    	    for (int move : safeMoves) {
    	        int newX = headX;
    	        int newY = headY;

    	        if (move == 2) {
    	            newX = headX - 1;  
    	        } else if (move == 3) {
    	            newX = headX + 1;  
    	        }

    	        if (move == 0) {
    	            newY = headY - 1;  
    	        } else if (move == 1) {
    	            newY = headY + 1;  
    	        }

    	        if (manhattanDistance(newX, newY, zom1headX, zom1headY) >= 2 &&
    	            manhattanDistance(newX, newY, zom2headX, zom2headY) >= 2 &&
    	            manhattanDistance(newX, newY, zom3headX, zom3headY) >= 2) {
    	            return move;
    	        }
    	    }

    	    if (safeMoves.isEmpty()) {
    	        return new Random().nextInt(4); 
    	    } else {
    	        int randomIndex = new Random().nextInt(safeMoves.size()); 
    	        return safeMoves.get(randomIndex); 
    	    }
    	}



}
