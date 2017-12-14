import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable
{
    private Socket s;

	public ClientHandler(Socket _Socket)
	{
	    this.s = _Socket;
	}

	public byte[] getResponse(int response, String file, String host) {
		String statusLine = "HTTP/1.1 ";
		String server = "Server :kyleserver/1.0.1\r\n";
		String contentLength = "Content-Length: ";
		String contentType = "Content-Type: ";
		String ext;

		if (file != null && !file.isEmpty()) {
			file = file.split(" ")[1].substring(1);
			System.out.println(file);
			ext = file.split("\\.")[1];
		} else {
			ext = "";
		}

		switch(response) {
			case 1:
				switch(ext) {
				case "html":
				case "htm":
					contentType += "text/html\r\n\r\n";
					break;
				case "gif":
					contentType += "image/gif\r\n\r\n";
					break;
				case "jpg":
				case "jpeg":
					contentType += "image/jpeg\r\n\r\n";
					break;
				case "pdf":
					contentType += "application/pdf\r\n\r\n";
					break;
				default:
					contentType += "error\r\n\r\n";
					break;
				}
				break;
			case 2:
				statusLine += "200 OK\r\n";
				break;
			case 3:
				statusLine += "501 Not Implemented\r\n";
				break;
			case 4:
			default:
				statusLine += "400 Bad Request\r\n";
				break;
		}

		if(response == 1) {
			try{
				File rFile = new File("public_html/" + file);
				FileInputStream stream = new FileInputStream(rFile);
				statusLine += "200 OK\r\n";
				contentLength += stream.getChannel().size() + "\r\n";

				String headerStr = statusLine + server + contentLength + contentType;
				System.out.println(headerStr);
				byte[] header = headerStr.getBytes();

				byte[] fBytes = new byte[(int)stream.getChannel().size()];
				stream.read(fBytes);

				byte[] message = new byte[fBytes.length + header.length];
				for (int i = 0; i < message.length; i++) {
					if (i < header.length) {
						message[i] = header[i];
					} else {
						message[i] = fBytes[i-header.length];
					}
				}

				return message;
			} catch (IOException e){
				statusLine += "404 Not Found\r\n";
				contentLength += "0\r\n";
				String headerStr = statusLine + server + contentLength + contentType;
				System.out.println(headerStr);
				return headerStr.getBytes();
			}
		} else {
			System.out.println("other response");
			contentLength += "0\r\n";
			String headerStr = statusLine + server + contentLength + contentType;
			System.out.println(headerStr);
			return headerStr.getBytes();
		}
	}

	public void run() {
		BufferedReader in = null;
		String message = "";
		Pattern hostPattern = Pattern.compile("([\\w.]*(?=:\\w))");
		String fileName = "";
		String host = "";
		int responseCode = 0;
		
		try {
        	in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
			System.out.println("Couldn't get an inputStream from the client");
		}

		try {
			int count = 1;
			while(in.ready()) {
				Matcher matcher;
				message = in.readLine();
				if (count == 1) {
					if (message.matches(".*GET.*")) {
						responseCode = 1;
						fileName = message;
					} else if (message.matches(".*HEAD.*")) {
						responseCode = 2;
					} else if (message.matches(".*(OPTIONS|POST|PUT|DELETE|TRACE|CONNECT).*")) {
						responseCode = 3;
					} else {
						responseCode = 4;
					}
				} else if (message.matches(".*Host:\\w*:?.*")) {
					matcher = hostPattern.matcher(message);
					if (matcher.find()) {
						host = matcher.group(1);
					}
				}
				count++;
			}
			byte[] bResponse = getResponse(responseCode, fileName, host);
			s.getOutputStream().write(bResponse);
			s.close();
		} catch (IOException e) {
			System.out.println("No I/O");
		}
	}
}