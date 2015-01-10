import java.net.*;
import java.io.*;
public class MyClient {

static Socket server;
static Player me;
static int port=4123;
static int max=21;
	public static void main(String[] args) throws UnknownHostException, IOException {

		server=new Socket(InetAddress.getLocalHost(),port);
//		server=new Socket();
//		server.connect(new InetSocketAddress(args[0],4123);
//		BufferedReader in=new BufferedReader(new InputStreamReader(server.getInputStream()));
		PrintWriter out=new PrintWriter(server.getOutputStream());
		BufferedReader wt=new BufferedReader(new InputStreamReader(System.in));
		InputStream in=server.getInputStream();
		ObjectInputStream objin=new ObjectInputStream(in);
		me=new Player(0);
		Card mycard;
		while (true) {
			String str="";
			while (str.equals("Ready")==false&&str.equals("End")==false) {
				System.out.println("Ready/End?");
				str=wt.readLine();
			}
			out.println(str);
			out.flush();
			if (str.equals("End"))
				break;
			try {
				mycard=(Card)objin.readObject();
				me.drawCard(mycard);
				out.println("Y");
				out.flush();
				mycard=(Card)objin.readObject();
				me.drawCard(mycard);
			} catch (Exception ex) {
				System.out.println(ex);
				break;				
			}
			while (true) { 
	
				if (me.getPoint()>max) {
					System.out.println("You Lose");
					out.println("N");
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
					if (str.equals("N")) 
						break;
					out.println(str);
					out.flush();
				}
				try {
					mycard=(Card)objin.readObject();
					me.drawCard(mycard);
				} catch (Exception ex) {
					System.out.println(ex);
					break;
				}
			}
			str=in.readLine();
			System.out.println(str);	
		}
		objin.close();
		in.close();
		server.close();
	}

}
