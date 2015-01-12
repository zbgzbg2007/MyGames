import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.*;

public class MyServer extends Thread {
	static int port=4123;
	static LinkedList<Card> cur=new LinkedList<Card>();// all the cards
	static int players;//number of players
	static int maxlimit=10;//max number of players
	static int limitpoint=21;//the largest points
	static volatile String res;//the result of game
	
	private Socket client;
	private boolean state;//to show game finish or not
	private int point;//the points a player got
	private String name;//player's name
	
	private volatile boolean isRunning=true;


	public MyServer(Socket c) {
		this.client=c;
		this.state=false;
		this.point=0;
	}

	public void kill() {
		this.isRunning=false;
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
	//Initialize all the cards

		List<Card> beginning=new ArrayList<Card>();
		for (int i=0;i<13;i++)
			for (Shape sh: EnumSet.range(Shape.Heart,Shape.Spade))
				beginning.add(new Card(i+1,sh));
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
		}
	
		while (true) {	
		//Game begins!
			InitializeCards();
			for (int i=0;i<subserver.length;i++)
				waiting.add(subserver[i]);
			while (waiting.isEmpty()==false) {
			//Keep sending cards

				try {
				//Notify the next server, waiting for its notifying 

					MyServer curserver=waiting.poll();
					synchronized (curserver) {
						curserver.notify();
					}
					synchronized (curserver) {
						curserver.wait();
					}
					
					if (curserver.finished()==false) 
						waiting.add(curserver); 
				} catch (Exception ex) {
					System.out.println(ex);
					subserver[0].kill();
					for (int i=0;i<players;i++) 
						synchronized (subserver[i]) {
							subserver[i].notify();
						}
					break;
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
			
			System.out.println(res);
			for (int i=0;i<players;i++)
				synchronized (subserver[i]) {
					subserver[i].notify();
				}

			//Notify all the servers and Ready to start again
			String x="";
			while ((x.equals("Y")==false)&&(x.equals("N")==false)) {
				System.out.println("Continue: Y/N");
				x=in.next();
			}
			if (x.equals("N"))
				break; 
		}
		main_server.close();
		subserver[0].kill();		
		for (int i=0;i<players;i++)
			synchronized (subserver[i]) {
				subserver[i].notify();
			}
		System.out.println("Master out");
		
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


			while (isRunning) {
			//Game begins!
				try {
				//wait for the master to begin
					this.wait();
				} catch (Exception ex) {
					System.out.println(ex);
					isRunning=false;
				}


				point=0;

				if (isRunning==false)
					break;
				str=in.readLine();
				System.out.println(str);
				if (str.equals("End"))
					break;
				if (str.equals("Ready")==false)
					continue;

				mycard=cur.poll();
				this.state=false;
				if (mycard.getVal()<10)
					this.point+=mycard.getVal();
				else
					this.point+=10;
				objout.writeObject(mycard);
				objout.flush();

				try {
					while (isRunning) {
					//Keep taking cards
						while (str.equals("Y")==false&&str.equals("N")==false)
							str=in.readLine();
						if (str.equals("Y")) {
							mycard=cur.poll();
							if (mycard.getVal()<10)
								this.point+=mycard.getVal();
							else
								this.point+=10;
							objout.writeObject(mycard);
							objout.flush();
						}
						else 
							break;

						str="";
						synchronized (this) {
							this.notify();
						}
						synchronized (this) {
							this.wait();
						}
					}

					//Stop taking cards
					this.state=true;
					synchronized (this) {
						this.notify();
					}
					synchronized (this) {
						this.wait();
					}
					
					//Show the final result
					out.println(res);
					out.flush();

				} catch (Exception ex) {
					System.out.println(ex);
					
					this.state=true;
					isRunning=false;
					break;
				} 
			}

			in.close();
			out.close();
			objout.close();
			outstream.close();
			client.close();
		} catch(IOException ex) {
			System.out.println(ex);
		} 

	}
}
