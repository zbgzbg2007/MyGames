

public class Player {
	int point;
	String name;
	public String getPlayerName() {
		return this.name;
	}
	public void takeCard(Card next) {
		System.out.println("You got a "+next.getShape()+" "+next.getVal().toString());
		if (next.getVal()<10)
			this.point+=next.getVal(); 
		else
			this.point+=10;
	}
	Player(int x, String n) {
		this.point=x;
		this.name=n;
	}
	public void resetPoint() {
		this.point=0;
	}

	public Integer getPoint() {
		return this.point;
	}

	public void seeAll() {

	}
}
