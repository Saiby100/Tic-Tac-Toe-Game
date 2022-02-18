import java.awt.Color;

public class Circle{
		private int circleSize;
		private Color circleColour; 
		
		public Circle(int size, Color colour) {
			this.circleSize = size*10; 
			this.circleColour = colour;
		}
	
		public int getSize() {
			return this.circleSize; 
		}
		
		public boolean isSmaller(Circle c) {
			return this.circleSize <= c.circleSize; 
		}
		
		public boolean isBigger(Circle c) {
			return this.circleSize >= c.circleSize; 
		}
		
		public boolean sameColor(Circle c) {
			return this.circleColour == c.circleColour; 
		}
		
		public boolean isGreen() {
			return this.circleColour == StdDraw.GREEN; 
		}
		
		public boolean isBlue() {
			return this.circleColour == StdDraw.BLUE; 
		}
		
		public void draw(int cx, int cy) {
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.filledSquare(cx, cy, 47);
			
			StdDraw.setPenRadius(.009);
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.circle(cx, cy, this.circleSize);
			
			StdDraw.setPenRadius(.002);
			StdDraw.setPenColor(this.circleColour);
			StdDraw.filledCircle(cx, cy, this.circleSize);
		}
		
		public static int getManhattanDistance(int[] arr, int a, int b) {
			int x = arr[0] - a;
			int y = arr[1] - b; 
			if (x < 0)x = -1*x;
			if (y < 0)y = -1*y; 
			
			return x+y; 
			
		}
	}
