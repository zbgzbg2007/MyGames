import java.util.*;
import java.io.*;
import java.net.*;

public class MyServer extends Thread{
	private Socket client;
	static int port=4123;
	public MyServer(Socket c) {
		this.client=c;
	}
	List<Card> cur;
	int players;
	static int maxlimit=10;
	public static void main(String[] args) throws IOException, CLassNotFoundException {

		ServerSocket main_server=new ServerSocket(port);
		players=0;
		while (players<1) {
			System.out.println("How many players?");
			Scanner in=new Scanner(Systme.in);
			if (in.hasNextInt()) {
				int a=in.nextInt();
				if (a<=maxlimit&&a>0)
					players=a;
			}
		}
		
		MyServer subserver[]=new MyServer[players]
		for (int i=0;i<subserver.length;i++) {
			subserver[i]=new MyServer(main_server.accept());
			subserver[i].start();
		}
		
	}

	public void run() {
		try {
			BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out=new PrintWriter(client.getOutputStream());
			while (true) {
				String str=in.readLine();
				System.out.println(str);
				out.println("Received");
				out.flush();
				if (str.equals("End"))
					break;
			}
			client.close();
		} catch(IOException ex) {
			System.out.println(ex);//something need to do?
		} finally {
			//something need to do?

		}

	}
}
