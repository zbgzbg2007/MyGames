import java.net.*;
import java.io.*;
public class MyClient {

static Socket server;
static int points;
static int port=4123;
static int total=21;
	public static void main(String[] args) throws UnknownHostException, IOException {

		server=new Socket(InetAddress.getLocalHost(),port);
//		server=new Socket();
//		server.connect(new InetSocketAddress(args[0],4123);
//		BufferedReader in=new BufferedReader(new InputStreamReader(server.getInputStream()));
		PrintWriter out=new PrintWriter(server.getOutputStream());
		BufferedReader wt=new BufferedReader(new InputStreamReader(System.in));
		InputStream in=server.getInputStream();
		ObjectInputStream objin=new ObjectInputStream(in);
		while (true) {
			points=0;
			System.out.println("Ready/End?");
			String str=wt.readLine();
			if (str.equals("End"))
				break;
			if (str.equals("Ready")!=true)
				continue;
			try {
				Card mycard=(Card)objin.readObject();
				points+=mycard.getVal();
				System.out.println("You got a "+mycard.getShape()+" "+mycard.getVal().toString());
				out.println("Y");
				mycard=(Card)objin.readObject();
				points+=mycard.getVal();
				System.out.println("You got a "+mycard.getShape()+" "+mycard.getVal().toString());
			} catch (Exception ex) {
				System.out.println(ex);
				break;				
			}
			while (true) { 
				try {
					Card mycard=(Card)objin.readObject();
					points+=mycard.getVal();
					System.out.println("You got a "+mycard.getShape()+" "+mycard.getVal().toString());
				} catch (Exception ex) {
					System.out.println(ex);
					break;
				}
	
				if (points>total) {
					System.out.println("You Lose");
					out.println("Lose");
					out.flush();
					break;
				}
				else {
					while (true) {
						System.out.println("Want another card: Y/N?");
						str=wt.readLine();
						if (str.equals("Y")||str.equals("N"))
							break;
					}
					out.println(str);
					out.flush();
				}
			}	
		}
		objin.close();
		in.close();
		server.close();
	}

}
