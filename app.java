import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Hashtable; 
public class app{
	
	static Stack[] blue = new Stack[3];
	static Stack[] green = new Stack[3];
	static Stack[][] board = new Stack[4][4]; 
	static Hashtable<Character, Stack> blueHash, greenHash; 
	
	public static void main(String[] args) {
		//SETTING UP
		int width = 900;
		int height = 500;
		StdDraw.setCanvasSize(width, height);
		StdDraw.setXscale(0, width);
		StdDraw.setYscale(0, height); 
		fillGrid(300, 55);
		
		//Allows each stack to be referenced via a Particular letter
		blueHash = new Hashtable<Character, Stack>(); 
		greenHash = new Hashtable<Character, Stack>(); 
		
		blueHash.put('A', blue[2]); 
		blueHash.put('B', blue[1]); 
		blueHash.put('C', blue[0]); 

		greenHash.put('A', green[2]); 
		greenHash.put('B', green[1]); 
		greenHash.put('C', green[0]); 
				
		Font newFont = new Font("Arial", Font.PLAIN, 20);
		StdDraw.setFont(newFont); 
		
		boolean blueTurn = true; 
		int[] cords;
		Stack stack, stack2;
		boolean invalidMove; 
		Color colour = StdDraw.BLUE; 
		
		updateWindow();
		while (true){
			invalidMove = false;
			if (blueTurn) drawText(115, 455, "Blue Player's Turn", colour); 
			else drawText(785, 455, "Green Player's Turn", colour); 
			
			char input = detectKey(); 
			if ((input == 'A' || input == 'B' || input == 'C')) {
				
				if (blueTurn) stack = blueHash.get(input);
				else stack = greenHash.get(input);
				cords = moveBox(0, 0); 

				if (stack.isEmpty() || !board[cords[0]][cords[1]].isEmpty()) {
					 invalidMove = true;
				}
				else if (board[cords[0]][cords[1]].red) {
					board[cords[0]][cords[1]].red = false;
					blueTurn = !blueTurn;
				}
				else {
					board[cords[0]][cords[1]].add(stack.pop()); 
					blueTurn = !blueTurn;
				}
			}

			else if (input == 'a' || input == 's' || input == 'd' || input == 'w') {
				cords = moveBox(0, 0); 
				int[] cords2 = moveBox(cords[0], cords[1]); 
				stack = board[cords[0]][cords[1]]; 
				stack2 = board[cords2[0]][cords2[1]];
				
				if (stack.isEmpty() || (blueTurn && stack.peek().isGreen()) || (!blueTurn && stack.peek().isBlue()) || 
						(!stack2.isEmpty() && stack.peek().isSmaller(stack2.peek()))) invalidMove = true;
				else if (stack2.red) {
					stack2.red = false;
					returnPieces(stack);
					blueTurn = !blueTurn;
				}
				else {
					board[cords2[0]][cords2[1]].add(stack.pop()); 
					blueTurn = !blueTurn; 	
				}		
			}
			else if (input == 'T') {
				drawText(450, 455, "Transporter is chosen", colour);
				cords = moveBox(0, 0); 
				stack = board[cords[0]][cords[1]]; 
				int[] cords2 = new int[2]; 
				
				if (blueTurn && stack.peek().isGreen())invalidMove = true;
				
				else if (!blueTurn && stack.peek().isBlue())invalidMove = true;
				
				else {
					getCords(cords, cords2); 
					stack2 = board[cords2[0]][cords2[1]]; 
					if (stack2.red) {
						stack2.red = false;
					}
					else board[cords2[0]][cords2[1]].add(stack.pop()); 
					
					blueTurn = !blueTurn; 
				}	
			}

			else if (input == 'S') {
				drawText(450, 455, "Shifter is chosen", colour); 
				cords = moveBox(0, 0); 
				stack = board[cords[0]][cords[1]]; 
				if (stack.isEmpty() || blueTurn && stack.peek().isGreen() || !blueTurn && stack.peek().isBlue())invalidMove = true; 
				else {
					int[][] cords2 = getSurroundingCords(cords); 
					int[] stackSizes = getSizes(cords2); 

					for (int i = 0; i < cords2.length; i++) {
						stack = getStack(cords2, i);
						for (int j = 0; j < stackSizes[i]; j++) {
							getStack(cords2, i+1).add(stack.remove(0)); 
						}
					}
					blueTurn = !blueTurn; 
				}
			}
			else if (input == 'X') {
				drawText(450, 455, "Bomb is chosen", colour); 
				cords = moveBox(0, 0); 
				stack = board[cords[0]][cords[1]]; 
				if (stack.isEmpty() || blueTurn && stack.peek().isBlue() || !blueTurn && stack.peek().isGreen())invalidMove = true;
				else {
					while(!stack.isEmpty()) returnPieces(stack); 
					int[][] cords2 = getSurroundingCords(cords); 
					for (int[] vals : cords2) {
						stack = board[vals[0]][vals[1]]; 
						
						if (stack.isEmpty()) {
							stack.red = true;
						}
						else {
							while(!stack.isEmpty())
								returnPieces(stack); 
						}
					}
					blueTurn = !blueTurn;
				}
			}
			
			if (!invalidMove) updateWindow();
			else {
				drawText(450, 455, "Invalid Move", StdDraw.BLACK);
				pause(2); 
			}
			if (winner()) {
				pause(5); 
				break;
			}
			if (blueTurn) colour = StdDraw.BLUE; 
			else colour = StdDraw.GREEN; 
		}
	}
	
	public static void updateWindow() {		
		//Drawing pieces in their default location
		for (int i = 0; i < blue.length; i++) {
			blue[i].draw(); 
			green[i].draw(); 
		}

		//Drawing pieces on the board
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				board[i][j].draw(); 
			}
		}
	}
	
	//Sets up grid and pieces
	public static void fillGrid(int xCord, int yCord) {	
		for (int i = xCord, k = 0; i < xCord+400; i+=100, k++) {
			for (int j = yCord+300, l = 0; j >= yCord; j-=100, l++) {
				board[l][k] = new Stack(i, j); 
				board[l][k].drawSquare(StdDraw.BLACK); 
			}
		}
		setPieces(); 
	}
	
	public static void setPieces() {
		for (int i = 105, j = 0; i <= 305; i+=100, j++) {
			blue[j] = new Stack(90, i); 
			green[j] = new Stack(810, i); 
		}
		
		//Setting coordinates for all circle piece objects
		for (int i = 0; i < 3; i++) {
			for (int j = 1; j < 5; j++) {
				blue[i].add(new Circle(j, StdDraw.BLUE)); 
				green[i].add(new Circle(j, StdDraw.GREEN));
			}
		}
		
		StdDraw.picture(blue[2].x+100, blue[2].y, "bomb.png");
		StdDraw.picture(blue[1].x+100, blue[1].y, "refresh.png");
		StdDraw.picture(blue[0].x+100, blue[0].y, "rubik.png");
		
		StdDraw.picture(green[2].x-100, green[2].y, "bomb.png");
		StdDraw.picture(green[1].x-100, green[1].y, "refresh.png");
		StdDraw.picture(green[0].x-100, green[0].y, "rubik.png");
		
		//draw text 65 pixels away from blue[i].x
		drawText(blue[2].x-65, blue[2].y, "A", StdDraw.BLACK); 
		drawText(blue[1].x-65, blue[1].y, "B", StdDraw.BLACK); 
		drawText(blue[0].x-65, blue[0].y, "C", StdDraw.BLACK); 
		
		drawText(green[2].x+65, green[2].y, "A", StdDraw.BLACK); 
		drawText(green[1].x+65, green[1].y, "B", StdDraw.BLACK); 
		drawText(green[0].x+65, green[0].y, "C", StdDraw.BLACK); 
	}
	
	
	
	//Navigation box for selecting a space
	public static int[] moveBox(int a, int b) {
		int i = a; 
		int j = b;
		int[] cords = new int[2]; 

		do{
			board[i][j].drawSquare(StdDraw.RED); 

			char input = detectKey(); 
			if (input == 'w' && i > 0) {
				board[i][j].drawSquare(StdDraw.BLACK); 
				i--; 
			}
			else if (input == 's' && i < 3) {
				board[i][j].drawSquare(StdDraw.BLACK); 
				i++;
			}
			else if (input == 'a' && j > 0) {
				board[i][j].drawSquare(StdDraw.BLACK); 
				j--;
			}
			else if (input == 'd' && j < 3) {
				board[i][j].drawSquare(StdDraw.BLACK); 
				j++;
			}
		}while (!StdDraw.isKeyPressed(KeyEvent.VK_ENTER));
		
		board[i][j].drawSquare(StdDraw.BLACK); 
		cords[0] = i; 
		cords[1] = j; 
		return cords; 
	}
	
	//Detects the key that is typed
	public static char detectKey() {
		while (!StdDraw.hasNextKeyTyped());
		return StdDraw.nextKeyTyped(); 
	}
	
	//Sets the pen colour
	public static void setColour(Color colour) {
		StdDraw.setPenColor(colour);
	}
	
	//Draws specified text at the specified coordinates and colour 
	public static void drawText(double x, double y, String text, Color colour) {
		setColour(StdDraw.WHITE); 
		StdDraw.filledRectangle(450, 455, 450, 40); 
		
		setColour(colour); 
		StdDraw.text(x, y, text);
	}
	
	//Pauses code for a specified amount of seconds
	public static void pause(int seconds) {
		try {
			Thread.sleep(seconds*1000); 
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//Checks if the piece is blue at a specified grid position
	public static boolean isBlue(int x, int y) {
		if (board[x][y].isEmpty()) return false; 
		return board[x][y].peek().isBlue(); 
	}
	
	//Checks if the piece is green at a specified grid position
	public static boolean isGreen(int x, int y) {
		if (board[x][y].isEmpty()) return false; 
		return board[x][y].peek().isGreen(); 
	}
	
	//Checks for a winner
	public static boolean winner() {
		boolean blue = false; 
		boolean green = false;
		
		if (isBlue(0, 0) && isBlue(1, 1) && isBlue(2, 2) && isBlue(3, 3)) blue = true;
		else if (isBlue(3, 0) && isBlue(2, 1) && isBlue(1, 2) && isBlue(0, 3)) blue = true;
		
		else if (isBlue(0, 0) && isBlue(1, 0) && isBlue(2, 0) && isBlue(3, 0)) blue = true;
		else if (isBlue(0, 1) && isBlue(1, 1) && isBlue(2, 1) && isBlue(3, 1)) blue = true;
		else if (isBlue(0, 2) && isBlue(1, 2) && isBlue(2, 2) && isBlue(3, 2)) blue = true;
		else if (isBlue(0, 3) && isBlue(1, 3) && isBlue(2, 3) && isBlue(3, 3)) blue = true;
		
		else if (isBlue(0, 0) && isBlue(0, 1) && isBlue(0, 2) && isBlue(0, 3)) blue = true;
		else if (isBlue(1, 0) && isBlue(1, 1) && isBlue(1, 2) && isBlue(1, 3)) blue = true;
		else if (isBlue(2, 0) && isBlue(2, 1) && isBlue(2, 2) && isBlue(2, 3)) blue = true;
		else if (isBlue(3, 0) && isBlue(3, 1) && isBlue(3, 2) && isBlue(3, 3)) blue = true;
		
		if (isGreen(0, 0) && isGreen(1, 1) && isGreen(2, 2) && isGreen(3, 3)) green = true;
		else if (isGreen(3, 0) && isGreen(2, 1) && isGreen(1, 2) && isGreen(0, 3)) green = true;
		
		else if (isGreen(0, 0) && isGreen(1, 0) && isGreen(2, 0) && isGreen(3, 0)) green = true;
		else if (isGreen(0, 1) && isGreen(1, 1) && isGreen(2, 1) && isGreen(3, 1)) green = true;
		else if (isGreen(0, 2) && isGreen(1, 2) && isGreen(2, 2) && isGreen(3, 2)) green = true;
		else if (isGreen(0, 3) && isGreen(1, 3) && isGreen(2, 3) && isGreen(3, 3)) green = true;
		
		else if (isGreen(0, 0) && isGreen(0, 1) && isGreen(0, 2) && isGreen(0, 3)) green = true;
		else if (isGreen(1, 0) && isGreen(1, 1) && isGreen(1, 2) && isGreen(1, 3)) green = true;
		else if (isGreen(2, 0) && isGreen(2, 1) && isGreen(2, 2) && isGreen(2, 3)) green = true;
		else if (isGreen(3, 0) && isGreen(3, 1) && isGreen(3, 2) && isGreen(3, 3)) green = true;
		
		if (blue) drawText(450, 455, "Blue Wins!", StdDraw.BLUE); 
		else if (green) drawText(450, 455, "Green Wins!", StdDraw.GREEN); 
		
		return blue || green;
	}
	
	
	//Gets manhattan distance using the formula
	public static int getManhattanDistance(int[] arr, int a, int b) {
		if (!board[a][b].isEmpty()) return 10; 
		int x = arr[0] - a;
		int y = arr[1] - b; 
		if (x < 0)x = -1*x;
		if (y < 0)y = -1*y; 
		
		return x+y; 
	}
	
	//Alters the specified arr array giving the closest grid position according to manhattan distance formula
	public static void getCords(int[] cords, int[] arr) {
		int a = 10; 
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int manDistance = getManhattanDistance(cords, i, j);
				if (manDistance < a && manDistance != 0) {
					a = manDistance; 
					arr[0] = i; 
					arr[1] = j; 
					if (a == 1) return; 
				}
			}
		}
	}
	
	public static int[][] getSurroundingCords(int[] cords) {
		int i = cords[0];
		int j = cords[1]; 
		
		int[][] vals = {{check(i-1), check(j-1)}, {check(i-1), check(j)}, 
				{check(i-1), check(j+1)}, {check(i), check(j+1)}, 
				{check(i+1), check(j+1)}, {check(i+1), check(j)}, 
				{check(i+1), check(j-1)}, {check(i), check(j-1)}
				}; 
		
		return vals; 
	}
	
	public static int check(int a) {
		if (a < 0) return 3;
		else if (a > 3) return 0; 
		else return a; 
	}
	
	public static Stack getStack(int[][] cords, int index){
		if (index == cords.length)index = 0; 
		return board[cords[index][0]][cords[index][1]]; 
	}
	
	public static int[] getSizes(int[][] cords) {
		int[] sizes = new int[cords.length]; 
		for (int i = 0; i < cords.length; i++) {
			sizes[i] = getStack(cords, i).size(); 
		}
		return sizes; 
	}
	
	public static void returnPieces(Stack stack) {
		Circle piece = stack.pop(); 
		Stack blueStack, greenStack;
		int stackIndex = 2; 
		int index = 0; 
		
		if (piece.isBlue()) {
			blueStack = blue[stackIndex];
			index = traverseStack(blueStack, piece); 
			while(index == -1) {
				stackIndex--; 
				blueStack = blue[stackIndex]; 
				index = traverseStack(blueStack, piece); 
			}
			blueStack.add(index, piece); 
		}
		else if (piece.isGreen()) {
			greenStack = green[stackIndex]; 
			index = traverseStack(greenStack, piece); 
			while (index == -1) {
				stackIndex--; 
				greenStack = green[stackIndex]; 
				index = traverseStack(greenStack, piece); 
			} 
			greenStack.add(index, piece);
		}
	}
	//returns the index if a piece can fit in a stack, -1 otherwise
	public static int traverseStack(Stack stack, Circle piece) {
		if (stack.size() == 4)return -1;
		if (piece.isSmaller(stack.get(0)) || stack.size() == 0)return 0;
		if (piece.isBigger(stack.get(stack.size()-1)))return stack.size(); 

		for (int i = 0; i < stack.size(); i++) {
			if (piece.isBigger(stack.get(i)) && piece.isSmaller(stack.get(i+1)))return i+1; 
		}
		return -1; 
	}
}