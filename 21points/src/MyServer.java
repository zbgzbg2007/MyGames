import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.*;

public class MyServer implements Runnable {
	static int port=4123;
	static LinkedList<Card> cur;// all the cards
	static int players;//number of players
	static int maxlimit=10;//max number of players
	static int limitpoint=21;//the largest points
	static String res;//the result of game
	
	private Socket client;
	private boolean state;//to show game finish or not
	private int point;//the points a player got
	private String name;//player's name

	public MyServer(Socket c) {
		this.client=c;
		this.state=false;
		this.point=0;
	}

	public String getPlayerName() {
		return this.name;
	}

	public boolean finished() {
		return this.state;
	}

	public int getPoint() {
		return this.point;
	}

	static void InitializeCards() {
		List<Card> beginning=new ArrayList<Card>();
		for (int i=0;i<14;i++)
			for (Shape sh: EnumSet.range(Shape.Heart,Shape.Spade))
				beginning.add(new Card(i,sh));
		if (cur.isEmpty()==false) 
			cur.clear();
		
		Random rand=new Random();
		for (int i=51;i>=0;i--) {
			int x=rand.nextInt(i+1);
			cur.add(beginning.get(x));
			Collections.swap(beginning,x,i);
		}
		
	}
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		ServerSocket main_server=new ServerSocket(port);
		players=0;
		Scanner in=new Scanner(System.in);

		while (players<1) {
		//Record the number of players
			System.out.println("How many players?");
			if (in.hasNextInt()) {
				int a=in.nextInt();
				if (a<=maxlimit&&a>0)
					players=a;
			}
		}
		
		LinkedList<MyServer> waiting=new LinkedList<MyServer>();//waiting list of servers 
		MyServer subserver[]=new MyServer[players];
		for (int i=0;i<subserver.length;i++) {
			subserver[i]=new MyServer(main_server.accept());
			subserver[i].start();
			waiting.add(subserver[i]);
		}
	
		while (true) {	
		//Game begins!
			InitializeCards();
			while (waiting.isEmpty()==false) {
			//Keep sending cards

				try {
				//Notify the next server, waiting for its notifying 

					MyServer curserver=waiting.poll();
					curserver.notify();
					cur.wait();
					if (curserver.finished()==false) 
						waiting.add(curserver); 
				} catch (Exception ex) {
					System.out.println(ex);
				} finally {
				//Stop all the servers
				}
			}	
			
			//Find the winner
			int winner=0;
			while (winner<players&&subserver[winner].getPoint()>limitpoint)
				winner++;
			if (winner>=players)
				res="Nobody wins!!";
			else {
				for (int i=winner+1;i<players;i++) {
					int nextp=subserver[i].getPoint();
					if (nextp<limitpoint&&nextp>subserver[winner].getPoint())
						winner=i;
				}
				res=subserver[winner].getPlayerName()+" wins!!";
			}

			//Notify all the servers and Ready to start again
			for (int i=0;i<players;i++) 
				subserver[i].notify();
			String x="";
			while (x.equals("Y")==false&&x.equals("N")==false) {
				System.out.println("Continue: Y/N");
				String str=in.next();
			}
			if (x.equals("N"))
				break; 
		}
		
		
	}
	

	public synchronized void run() {
		try {
			BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out=new PrintWriter(client.getOutputStream());
			OutputStream outstream=client.getOutputStream();
			ObjectOutputStream objout=new ObjectOutputStream(outstream);
			Card mycard;
			String str=in.readLine();
			this.name=str;
			while (true) {
				str=in.readLine();
				System.out.println(str);
				out.flush();
				if (str.equals("End"))
					break;
				if (str.equals("Ready")==false)
					continue;
				mycard=cur.poll();
				objout.writeObject(mycard);	
				this.state=false;
				this.point+=mycard.getVal();
				while (true) {
					while (str.equals("Y")==false&&str.equals("N")==false)
						str=in.readLine();
					if (str.equals("Y")) {
						mycard=cur.poll();
						objout.writeObject(mycard);
						this.point+=mycard.getVal();
					}
					else 
						break;
					cur.notify();
					this.wait();
				}
				this.state=true;
				cur.notify();
				this.wait();
				out.println(res);
				
			}
			in.close();
			out.close();
			objout.close();
			outstream.close();
			client.close();
		} catch(IOException ex) {
			System.out.println(ex);//something need to do?
		} finally {
			//something need to do?

		}

	}
}
