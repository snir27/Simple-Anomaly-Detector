package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class MainTrain {
	
	public static void check(String outputFile,String expectedOutputFile){
		try {
			int chk[]={31,58,59,70,71,82,83,94,95,106,107};			
			BufferedReader st=new BufferedReader(new FileReader(outputFile));
			BufferedReader ex=new BufferedReader(new FileReader(expectedOutputFile));
			int i=1,j=0;
			String lst,lex;
			while((lst=st.readLine())!=null && (lex=ex.readLine())!=null){
				if(i<13 && lst.compareTo(lex)!=0){ // 12
					System.out.println("line "+i+" expected: "+lex+" you got "+lst);
					System.out.println("wrong output (-1)");
				}else
				if(j<11 && i==chk[j]){
					if(lst.compareTo(lex)!=0){ // 88
						System.out.println("line "+i+" expected: "+lex+" you got "+lst);
						System.out.println("wrong output (-8)");
					}
					j++;
				}
				i++;
			}
			if(j<11)
				System.out.println("wrong output size (-"+((11-j)*8)+")");
			st.close();
			ex.close();
		}catch(IOException e) {
			System.out.println("an exception has occured (-100)");
		}
	}
	
	public static void testClient(int port){
		try {
			Socket theServer=new Socket("localhost", port);
			PrintWriter outToserver=new PrintWriter(theServer.getOutputStream());
			BufferedReader inFromServer=new BufferedReader(new InputStreamReader(theServer.getInputStream()));
			
			Thread reader=new Thread(()->{
				try {
					PrintWriter out=new PrintWriter(new FileWriter("output.txt"),true);
					String line;
					while(!(line=inFromServer.readLine()).equals("bye")) {
						out.println(line);
					}
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			
			Thread writer=new Thread(()-> {
				try {
					BufferedReader in=new BufferedReader(new FileReader("input.txt"));
					String line;
					while(!(line=in.readLine()).equals("6")) {
						outToserver.println(line);
						outToserver.flush();
					}					
					outToserver.println(line);
					outToserver.flush();
					in.close();					
				}catch(IOException e) {
					e.printStackTrace();
				}
			});
			
			reader.start();
			writer.start();
			try {
				reader.join();
				writer.join();
			} catch (InterruptedException e) {}
			outToserver.close();
			inFromServer.close();
			theServer.close();
		} catch (IOException e) {
			System.out.println("an IO exception has happend (-100)");
		}
	}

	public static void main(String[] args) {
		Random r=new Random();
		int port=6000+r.nextInt(1000);
		Server server=new Server();
		server.start(port, new AnomalyDetectionHandler());
		testClient(port);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}		
		server.stop();
		check("output.txt", "expectedOutput.txt");
		System.out.println("done");
	}

}
