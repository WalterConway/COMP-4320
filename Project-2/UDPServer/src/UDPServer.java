import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Matthew Garmon
 * @author Walter Conway
 */
class UDPServer {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		boolean traceOPT = false;

		if(args != null && args.length == 1){
			if(args[0] != null){
				traceOPT = args[0].equalsIgnoreCase("trace");
				if(traceOPT){
					System.out.println("Trace output is Active");
				}else {
					System.out.println("Trace output is Not Active");
				}
			}
		}else{

			System.out.println("Trace output is Not Active");
		}
		
		final int PORT_NUMBER = 10030;
		final int BUFFER_AMT = 128;
		DatagramSocket serverSocket = new DatagramSocket(PORT_NUMBER);
		
		HTTPHeader header = null;
		final String ERRORFILE = "error.html";
		byte[] receiveData = new byte[BUFFER_AMT];
		byte[] emptyDataSet = new byte[BUFFER_AMT]; //used to reset the incoming data.
		SAR sar;
		
		
		while(true) {
			System.out.println("Waiting for connection...");
			//Receiving packet from client
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			
			//extracting data from the packet
			String sentence = new String(receivePacket.getData());

			//resetting the receive Data variable
			receiveData = null;
			receiveData = emptyDataSet.clone();

			//splitting the request into separate parts to check if it is valid.
			String[] request = sentence.split("[ ]");
			
			boolean fileExist;
			boolean methodTokenValid;
			String htmlDocumentBuffer = readFile(ERRORFILE);
			
			//checking if the response is valid and constructing a header in response to the invalid request
			if(request.length !=3){
				//error not a proper length of a request
				//send 400 as a status code
				header = new HTTPHeader(Integer.toString(contentLengthCalculator(ERRORFILE)), 400, MIMETypeGenerator(ERRORFILE));
			}
			else {
				//proper length now, need to check  if each element is authentic
				methodTokenValid = isMethodTokenValid(request[0]);
				fileExist = checkFileExistence(request[1]);
				if (methodTokenValid && fileExist){
					header = new HTTPHeader(Integer.toString(contentLengthCalculator(request[1])), 200, MIMETypeGenerator(request[1]));
					htmlDocumentBuffer = readFile(request[1]);
				} 
				if(methodTokenValid && !fileExist){
					header = new HTTPHeader(Integer.toString(contentLengthCalculator(ERRORFILE)), 404, MIMETypeGenerator(ERRORFILE));
				}
				if(!methodTokenValid){
					header = new HTTPHeader(Integer.toString(contentLengthCalculator(ERRORFILE)), 400, MIMETypeGenerator(ERRORFILE));
				}
			} 

			//getting the return address and port number
			InetAddress client_IPAddress = receivePacket.getAddress();
			int client_port = receivePacket.getPort();
			String headerInformation = header.toString();
			//joining the header and data together
			String headerAndData = new String(headerInformation + "\r\n"+ htmlDocumentBuffer);
			byte[] headerAndDataByteArray = headerAndData.getBytes();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(headerAndDataByteArray);
			outputStream.write(emptyDataSet);
			/*
			 * This is where New stuff will go
			 */
			//breaks the header and file and null transmission into Fragments and adds headers to each fragment
			sar = new SAR(outputStream.toByteArray());
			outputStream.close();
			serverSocket.close();
			SelectiveRepeater sr = new SelectiveRepeater(sar,client_port,client_IPAddress,traceOPT);
			sr.start();
			break;
		}
	}


	/**
	 * @param fileNameAndPath
	 * @return
	 */
	private  static boolean checkFileExistence(String fileNameAndPath){
		if(fileNameAndPath == null){
			return false;
		}
		File file = new File(System.getProperty("user.dir") + "/www/"+fileNameAndPath);
		return file.exists();
	}

	/**
	 * @param requestMethodToken
	 * @return
	 */
	private  static boolean isMethodTokenValid(String requestMethodToken){
		String[] MethodTokenList = {"GET","HEAD"};
		for(String methodToken : MethodTokenList){
			if(requestMethodToken.toUpperCase().equalsIgnoreCase(methodToken)){
				return true;
			}	
		}
		return false;
	}

	/**
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("unused")
	private static int contentLengthCalculator(String fileName){
		if(fileName == null){
			return 0;
		}		
		File file = new File(System.getProperty("user.dir") + "/www/"+fileName);
		int count =0;
		int c;
		try{
			//encoded = Files.readAllBytes(file.toPath());
			FileInputStream in = new FileInputStream(file);
			while((c = in.read()) != -1){
				count++;
			}
					in.close();
		} catch(IOException e){
			return 0;
		}
		return count;
	}
	
	/**
	 * @param fileName
	 * @return
	 */
	private static String readFile(String fileName){
		int fileSize = contentLengthCalculator(fileName);
		if(fileSize != 0){
			byte[] unencoded = new byte[fileSize];
			File file = new File(System.getProperty("user.dir") + "/www/"+fileName);
			int c;
			int count=0;
			FileInputStream in=null;
			
			try {
				in = new FileInputStream(file);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			try {
				while((c = in.read()) != -1){
					unencoded[count] = (byte)c;
					count++;
				}
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				return new String(unencoded,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * @param fileName
	 * @return
	 */
	public static String MIMETypeGenerator(String fileName){
		String mimeType="text/plain";
		if (fileName.endsWith(".html") || fileName.endsWith(".htm"))
			mimeType="text/html";
		else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
			mimeType="image/jpeg";
		else if (fileName.endsWith(".gif"))
			mimeType="image/gif";
		else if (fileName.endsWith(".class"))
			mimeType="application/octet-stream";
		return mimeType;
	}

	
	public static HTTPHeader headerConstructor(String[] request){
		return null;
	}

}
