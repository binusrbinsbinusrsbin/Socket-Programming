import java.io.*;
import java.net.*;

public class HW1Client {
    public static void main(String[] args) throws IOException {
        
        if (args.length != 2) {
            System.err.println(
                "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (

            Socket echoSocket = new Socket(hostName, portNumber);
            PrintWriter out =
                new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(echoSocket.getInputStream()));
            BufferedReader stdIn =
                new BufferedReader(
                    new InputStreamReader(System.in))

        )
		{

            String userInput;

            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);

				String delims="[ ]+";
				String[] tokens=userInput.split(delims);

				if(tokens[0].toUpperCase().equals("GET")){

					String dloadfile;

					dloadfile=userInput.substring(userInput.lastIndexOf("/")+1);
					System.out.println(dloadfile+" file transfer");	

					int size;

					size = Integer.parseInt(in.readLine());

					//show download progress. save file

					String z;
					String str = "";
					BufferedWriter writer = new BufferedWriter(new FileWriter(dloadfile));
					File f = new File(dloadfile);

					writer.close();

					int linecount=0;

					while((z = in.readLine()) != null){

						BufferedWriter writer2 = new BufferedWriter(new FileWriter(dloadfile, true));

						if(linecount > 0){
							writer2.append("\n");
							writer2.append(z);
							writer2.close();
							System.out.println(((float)f.length()/size)*100+"% "+f.length()+" bytes out of "+size+" bytes");
						}
						if(f.length()==size){
							System.out.println("'"+dloadfile+"' file transfer complete");						
						}
						linecount++;
					}
				}
				else{
					System.out.println("client to server message does not begin with 'get[space]'");
				}
			}
        }
		catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        }
		catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +hostName);
            System.exit(1);
        } 
    }
}
