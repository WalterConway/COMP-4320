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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

class UDPClient {
	public static void main(String args[]) throws Exception {

		//Reads in the keyboard stream by bytes and converts them into ASCII.
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = null;
		byte[] sendData = new byte[128];
		byte[] emptyData = new byte[128];
		byte[] receiveData = new byte[128];
		String serverIP;
		int serverPortNumber;
		Scanner scan = new Scanner(System.in);
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

		File file = new File(System.getProperty("user.dir"), request[1]);

		//sending the data to the server.
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPortNumber);
		clientSocket.send(sendPacket); 
		scan.close();

		DatagramPacket receivePacket;

		receivePacket = new DatagramPacket(receiveData, receiveData.length); 
		clientSocket.setSoTimeout(2000);
		try{
		clientSocket.receive(receivePacket);
		} catch(SocketTimeoutException e){
			System.out.println("Too much time has gone by, connection is closed.");
			clientSocket.close();
		}
		if(!clientSocket.isClosed()){			
		

		String receivedPacketData = new String(receivePacket.getData());
		String[] receivedPacketHeaderData = receivedPacketData.split("[ ]");
		if(receivedPacketHeaderData.length > 3){
			System.out.println(receivedPacketData);

			//Receiving the header information
			int indexOfcrlf=0;
	
			for(int i=0; i < 4; i++){
				indexOfcrlf = receivedPacketData.indexOf('\n', indexOfcrlf+1);
			}
			String fileData = receivedPacketData.substring(indexOfcrlf+1);
			int eos=-1;
			while(eos == -1){

				receivePacket = new DatagramPacket(receiveData, receiveData.length); 
				clientSocket.receive(receivePacket);
				receivedPacketData = new String(receivePacket.getData());
				System.out.println(receivedPacketData);
				//clear the received data
				receiveData = emptyData.clone();				

				//eos - end of stream, which indicates the first occurrence of an null byte 
				eos = receivedPacketData.indexOf(0);
				fileData = fileData + receivedPacketData;
			}
			clientSocket.close();
			Writer writer = null;
			try {
				fileData = fileData.trim();
				writer = new BufferedWriter(new OutputStreamWriter(	new FileOutputStream(file), "UTF-8"));
				writer.write(fileData);
				writer.close();

			} catch (IOException ex) {
			} finally {
				try {
					writer.close();
				} catch (Exception ex) {
				}
			}
		} else {
			file =null;
			System.out.println("Invalid response from Server.");
		}
	} 
	}
} 