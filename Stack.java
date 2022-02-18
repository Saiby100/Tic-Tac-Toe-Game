import java.awt.Color;

public class Stack {
	private Circle[] circles;
	private int size; 
	public final int x, y; 
	public boolean red; 
	
	public Stack(int x, int y) {
		this.circles = new Circle[5]; 
		this.size = 0; 
		this.x = x; 
		this.y = y; 
		this.red = false;
	}
	
	public void add(Circle circle) {
		if (size == 4)return; 
		circles[size] = circle;
		size++; 
	}
	
	public Circle get(int i) {
		return circles[i]; 
	}
	
	public Circle peek() {
		return circles[size-1]; 
	}
	
	public Circle pop() {
		Circle c = circles[size-1];
		size--; 
		return c; 
	}
	
	public int size() {
		return size;
	}
	
	public void add(int index, Circle circle) {
		if (index >= size || size == 4) {
			add(circle);
			return;
		}
		for (int i = size-1; i >= index; i--) {
			circles[i+1] = circles[i]; 
		}
		circles[index] = circle; 
		size++; 
	}
	
	public Circle remove(int index) {
		
		Circle c = circles[index]; 
		for (int i = index; i < size; i++) {
			circles[i] = circles[i+1]; 
		}
		size--;
		return c;
	}
	
	public boolean isEmpty() {
		return size == 0; 
	}
	
	public void drawSquare(Color colour) {
		StdDraw.setPenColor(colour); 
		StdDraw.setPenRadius(.007);
		StdDraw.square(this.x, this.y, 50);
	}
	
	public void draw() {
		if (size != 0) {
			peek().draw(this.x, this.y);
		}
		else if (this.red) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.filledSquare(this.x, this.y, 47);
		}
		else {
			StdDraw.setPenColor(StdDraw.WHITE); 
			StdDraw.filledSquare(this.x, this.y, 47);
		}
		
	}
	
}
