import java.io.*;
import java.net.*;
import java.util.Objects;
// import ClientHandler;

public class HttpServer {

	public static int serverPort;

	// public int HEAD() {

	// }

	// public int GET() {

	// }

	// method for reading the request and verifying that it has all the necessary info
	public static void parseRequest() {
		ServerSocket server = null;
		Socket client;
		boolean receiving = true;

		// Attempt to open the server on serverPort
		try {
			server = new ServerSocket(serverPort);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + serverPort);
			System.exit(1);
		}

		while(receiving) {
			// attempt to connect to the client to receive data
			try {
				System.out.println("waiting...");
				client=server.accept();
				System.out.println("Server accepted connection from "+client.getInetAddress());

				// create and run a thread to handle the incoming request
				ClientHandler ch = new ClientHandler(client);
				new Thread(ch).start();
			} catch (IOException e) {
				System.out.println("Server unable to listen on specified port");
				receiving = false;
				System.exit(1);
	        }
		}
	}


	public static void main(String[] args) {
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

		parseRequest();

		// Create a while loop and continuously do the following...

		// instantiate socket and socketserver objects, attempt to open server socket on specified port number
	}
}