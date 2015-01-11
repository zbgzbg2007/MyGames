

public class Player {
	int point;
	String name;
	public String getPlayerName() {
		return this.name;
	}
	public void drawCard(Card next) {
		System.out.println("You got a "+next.getShape()+" "+next.getVal().toString());
		this.point+=next.getVal(); 
	}
	Player(int x, String n) {
		this.point=x;
		this.name=n;
	}
	public Integer getPoint() {
		return this.point;
	}

	public void seeAll() {

	}
}
