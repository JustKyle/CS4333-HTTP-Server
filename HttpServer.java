import java.io.*;
import java.net.*;
import java.util.Objects;

public class HttpServer {

	public static void main(String[] args) {
		int serverPort = 0;

		// Exit program if more than one argument given
		if (args.length > 1) {
			System.out.println("Too many arguments given");
			System.exit(1);
		}  else if (args.length == 0) { // use defualt port if none provided
			System.out.println("No port provided, proceeding with default port 32251");
			serverPort = 32251;
		} else if (!args[0].matches("^[0-9]+$")) { // exit program if port number is invalid
			System.out.println("Invalid port number. The port number cannot contain letters or spaces");
			System.exit(1);
		} else { // if everything passes, set the port number to the argument
			serverPort = Integer.parseInt(args[0]);
		}

		// instantiate socket and socketserver objects, attempt to open server socket on specified port number
		ServerSocket server = null;
		Socket client = null;
		try {
			server = new ServerSocket(serverPort);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + serverPort);
			System.exit(1);
		} 


		try {
			client=server.accept();
			System.out.println("Server accepted connection from "+client.getInetAddress());
		} catch (IOException e) {
			System.out.println("Server unable to listen on specified port");
			System.exit(1);
        } 

        boolean running = true;
        BufferedReader in = null;
        while (running) {
        	String message = "";
	        try {
	        	in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	        } catch (IOException e) {
				System.out.println("Couldn't get an inputStream from the client");
				running = false;
			} try {
				while(running) {
					message = in.readLine();
					System.out.println("[remote] " + message);
				}
			} catch  (IOException e) {
				System.out.println("No I/O");
				running = false;
			}
		}
	}
}