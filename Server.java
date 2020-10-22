import java.net.*;
import java.io.*;

public class HW1Server {
    public static void main(String[] args) throws IOException {
        
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        
        try{
            ServerSocket serverSocket =
                new ServerSocket(portNumber);

	    	while(true){
				Socket clientSocket = serverSocket.accept();
	        	ClientWorker w=new ClientWorker(clientSocket);
	        	Thread t=new Thread(w);
	      		t.start();
		    }
        
        }
		catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}

class ClientWorker implements Runnable {
  private Socket client;

//Constructor
  ClientWorker(Socket client) {
    this.client = client;
  }

	public void run(){
		String line;
		BufferedReader in = null;
		PrintWriter out = null;
		try{
		  in = new BufferedReader(new 
			InputStreamReader(client.getInputStream()));
		  out = new 
			PrintWriter(client.getOutputStream(), true);
		}
		catch (IOException e) {
		  System.out.println("in or out failed");
		  System.exit(-1);
		}

		while(true){
			try{
				line = in.readLine();
				//parse a get command host

				String delims="[ ]+";
				String[] tokens=line.split(delims);
				String httphost;
				String httpfile;
				String dloadfile;

				if(tokens[0].toUpperCase().equals("GET")){
					httphost=tokens[1].substring(0,tokens[1].indexOf("/"));
					httpfile=tokens[1].substring(tokens[1].indexOf("/"));
					dloadfile="proxy-"+httpfile.substring(httpfile.lastIndexOf("/")+1);
					//http socket hostname port 80 send mssg
					try (

						Socket httpSocket = new Socket(httphost,80);
						PrintWriter httpout =
							new PrintWriter(httpSocket.getOutputStream(), true);
						BufferedReader httpin =
							new BufferedReader(
								new InputStreamReader(httpSocket.getInputStream()));

					) {

						String httprequest;

						httprequest=tokens[0].toUpperCase()+" "+httpfile+" HTTP/1.1\r\nHost: "+httphost+"\r\n\r\n";
						httpout.println(httprequest);
						//parse response text save file

						String z;
						int swtch=0;
						String str = "";
						BufferedWriter writer = new BufferedWriter(new FileWriter(dloadfile));

						writer.close();
						while((z = httpin.readLine()) != null){
							if(swtch==1){
								BufferedWriter writer2 = new BufferedWriter(new FileWriter(dloadfile, true));

								writer2.append("\n");
								writer2.append(z);
								writer2.close();
							}
							if(z.contains("Content-Type: text/html")){
								swtch=1;
							}				
						}
					}
					catch (UnknownHostException e) {
						System.err.println("Don't know about host " + httphost);
						System.exit(1);
					} catch (IOException e) {
						System.err.println("Couldn't get I/O for the connection to " +
							httphost);
						System.exit(1);
					} 
					//get and send bits
		
					File f = new File(dloadfile);
					long fileSize = f.length();
		
					out.println(fileSize);
					//Send file to client

					BufferedReader proxyfile;

					try {
						proxyfile = new BufferedReader(new FileReader(dloadfile));

						String fileline = proxyfile.readLine();

						while (fileline != null) {
							out.println(fileline);
							fileline = proxyfile.readLine();
						}
						proxyfile.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				else{
					System.out.println("client to server message does not begin with 'get[space]'");
				}
			}
			catch (IOException e) {
				System.out.println("Read failed");
				System.exit(-1);
		   }
		}
	}
}
