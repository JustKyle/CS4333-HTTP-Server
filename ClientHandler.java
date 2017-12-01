import java.io.*;
import java.net.*;
import java.util.Objects;

public class ClientHandler implements Runnable
{
    private Socket s;

	public ClientHandler(Socket _Socket)
	{
	    this.s = _Socket;
	}

	public void run() {
		BufferedReader in = null;
		String message = "";
		boolean reading = true;

		try {
        	in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
			System.out.println("Couldn't get an inputStream from the client");
			reading = false;
		}

		try {
			while(reading && (message = in.readLine()) != null) {
				System.out.println("running");
				if (message.matches(".*(GET|HEAD).*")) {
					System.out.println(message);
				} else if (message.matches(".*Host:\\w*:?.*")) {
					System.out.println(message);
					System.out.println("Close socket and return execution to main");
					s.close();
					reading = false;
				}
			}
		} catch (IOException e) {
			System.out.println("No I/O");
			reading = false;
		}
	}
}