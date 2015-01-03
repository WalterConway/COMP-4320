import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author Matthew Garmon
 * @author WalterC
 *
 */
class UDPClient {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		double probabilityLoss;
		double probabilityCorrupt;
		boolean trace = false;
		if(args == null || args.length < 2 || (Double.parseDouble(args[0])+Double.parseDouble(args[1])) >=1){
			System.out.println("Resorting to default Gremlin params");
			System.out.println("Probability Loss: .2, Probability Corruption: .4");
			System.out.println("Trace is OFF");
			probabilityLoss = .2;
			probabilityCorrupt = .4;
		}else{			
			probabilityLoss = Double.parseDouble(args[0]);
			probabilityCorrupt = Double.parseDouble(args[1]);
			System.out.println("Probability Loss: " + probabilityLoss + ", Probability Corruption: " + probabilityCorrupt);
			if(!(args.length < 3) || (args[2] != null)){
				trace = args[2].equalsIgnoreCase("trace");
				if (trace) {
					System.out.println("Trace output is Active");
				}
			}
		}
		Gremlin gremlin = new Gremlin(probabilityLoss,probabilityCorrupt);
		
		
		//Reads in the keyboard stream by bytes and converts them into ASCII.
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = null;
		byte[] sendData = new byte[128];
		String serverIP;
		int serverPortNumber;
		Scanner scan = new Scanner(System.in);
		//Prompt the user for server details
		while(true){		
			System.out.print("Enter the server IP address: ");
			serverIP = scan.nextLine();
			try{
				IPAddress = InetAddress.getByName(serverIP);
			} catch(UnknownHostException e){
				System.out.println("Invalid host name or the host name is unreachable.");
				continue;
			}
			break;
		}

		System.out.print("Enter the server port number: ");
		serverPortNumber = scan.nextInt();
		String sentence;
		String[] request;
		do{
			System.out.println("Type your HTTP Request and press Enter:");
			sentence = inFromUser.readLine();
			request = sentence.split("[ ]");
		}while(request.length != 3);
		//Create a new destination file
		File file = new File(System.getProperty("user.dir"), request[1]);

		//sending the data to the server.
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPortNumber);
		clientSocket.send(sendPacket); 
		scan.close();
		///The new stuff
		int localPort = clientSocket.getLocalPort();
		clientSocket.close();
		SelectiveRepeater sr = new SelectiveRepeater(localPort,IPAddress, serverPortNumber,gremlin,trace);
		sr.start();
		SAR sar = new SAR();
		
		//load the ordered Fragment list from SR to SAR so it can be defragmented
		sar.setFragmentList(sr.getFragmentList());

		sar.unSegmentFile();
		//get the defragmented Data and store it so we can get the data
		byte[] temp = sar.getData();
			String receivedSARData = new String(temp);
			System.out.println(receivedSARData);
		///old stuff
			//finding the fourth new line in the header
				int indexOfcrlf=0;
				for(int i=0; i < 4; i++){
					indexOfcrlf = receivedSARData.indexOf('\n', indexOfcrlf+1);
				}
				//everything after the fourth new line char
				//inclusive to the end exclusive.
				String fileData = receivedSARData.substring(indexOfcrlf+1);
				int nullIndex=receivedSARData.indexOf(0);
				String DataFromFileData="";
				if(nullIndex != -1){
					DataFromFileData =fileData.subSequence(0, nullIndex).toString();
				}else{
					System.out.println("Didn't find a null char");
				}
				//Write the transfered data to the new file
				Writer writer = null;
				try {
					DataFromFileData = fileData.trim();
					writer = new BufferedWriter(new OutputStreamWriter(	new FileOutputStream(file), "UTF-8"));
					writer.write(DataFromFileData);
					writer.close();

				} catch (IOException ex) {
				} finally {
					try {
						writer.close();
					} catch (Exception ex) {
					}
				}

		
	}
} 