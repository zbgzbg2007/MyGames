

public class Player {
	int point;
	public void drawCard(Card next) {
		System.out.println("You got a "+next.getShape()+" "+next.getVal().toString());
		this.point+=next.getVal(); 
	}
	Player(int x) {
		this.point=x;
	}
	public Integer getPoint() {
		return this.point;
	}

	public void seeAll() {

	}
}
