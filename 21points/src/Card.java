import java.io.*;

public class Card implements Serializable{
	
	int val;
	Shape shape;
	public int getVal() {
		return this.val;
	}
	public Shape getShape() {
		return this.shape;
	}
	public Card(int x, Shape c) {
		this.val=x;
		this.shape=c;
	}
}
