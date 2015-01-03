import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author Matthew Garmon
 * @author WalterC
 *
 */
public class SelectiveRepeater {
	//list of acknowledgments
	String[] acknowledgmentBuffer = new String[32];
	//ordered Fragments
	ArrayList<Fragment> FragmentList = new ArrayList<Fragment>();
	//unordered Fragments more or a buffer
	Fragment[] FragmentWindow = new Fragment[32];


	int recieve_base = 0;
	int window_base = 3;

	int sucessfully_received=0;
	int unsucessfully_received=0; 
	//constants in the SR
	int localPortNumber;
	int serverPortNumber;
	InetAddress serverIP;

	boolean lastFragmentRecieved = false;

	DatagramSocket clientSocket;

	Gremlin gremlin;

	boolean trace = false;

	/**
	 * @param local_Port - Port on client Side
	 * @param server_IP - The Server IP
	 * @param remote_Port - Port on Server side
	 * @param grem is the Gremlin Used inside SR
	 */
	public SelectiveRepeater(int local_Port,InetAddress server_IP, int remote_Port, Gremlin grem, boolean traceOPT){
		gremlin = grem;

		for(int i =0; i<acknowledgmentBuffer.length; i++){
			acknowledgmentBuffer[i] = "N"+i;
		}
		localPortNumber = local_Port;
		serverPortNumber = remote_Port;
		serverIP = server_IP;
		try {
			clientSocket = new DatagramSocket(localPortNumber);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		trace = traceOPT;
	}

	/**
	 * @return
	 */
	public ArrayList<Fragment> getFragmentList(){
		return FragmentList;
	}

	/**
	 * The run function of the sr layer. Loops through until the last fragment is
	 * received from the server. Stores the fragments in the fragment list
	 */
	public void start(){
		//loop until the last fragment is recieved
		while(!lastFragmentRecieved){
			if(trace){
				System.out.println("Fragment: "+ sucessfully_received +" "+"Recieved -- intact" );
				System.out.println("Fragment: "+ unsucessfully_received +" "+"Recieved -- damaged" );
			}
			System.out.println(Arrays.toString(acknowledgmentBuffer));

			byte[] receiveData = new byte[128];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				clientSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			////			
			//Corrupting the recievedPacket Layer
			receivePacket = gremlin.filter(receivePacket);

			//Error Detection layer
			if(receivePacket!=null){
				Fragment temp = Tool.dataGramToFragment(receivePacket);
				int sequenceID = temp.getmHeader().getSequenceID();
				//Use Checksum to validate fragment
				System.out.println("Seq no considered: "+sequenceID);
				boolean behindrcvbase =(sequenceID >=(recieve_base-4)%32 && sequenceID <= (recieve_base-1)%32); 
				boolean inwindow =(sequenceID >= recieve_base && sequenceID <=window_base);
				boolean inwindowmodulo = (sequenceID <= 31 && recieve_base>window_base);
				boolean validfragment = ErrorDetector.validateCheckSum(temp);
				System.out.println("Is Fragment Valid: "+ validfragment);
				
					if(validfragment){
						if(behindrcvbase || inwindow || inwindowmodulo){

						//Update acknowledgementBuffer and send ACK to Server
						acknowledgmentBuffer[sequenceID]= "A"+sequenceID;
						FragmentWindow[sequenceID] = temp;
						sendAcknowledgements();
						//If this is the first Sequence ID in the pane then we can move the window down one
						if(sequenceID == recieve_base){
							incrementWindowPosition();
						}
						sucessfully_received++;
					}else{
						//send acknowledgment if the fragment was corrupted
						sendAcknowledgements();
						unsucessfully_received++;
					}
						
				}
			}
		}
	}

	/**
	 * Transmits a vector of ACks and NAKs back to the server after each fragment is received
	 */
	public void sendAcknowledgements(){
		ArrayList<String> ackVector = new ArrayList<String>();
		for (int i = 0; i < 4; i++) {
			ackVector.add(acknowledgmentBuffer[(recieve_base + i)%32]);
		}

		byte[] tes = null;

		ByteArrayOutputStream objbytetest = new ByteArrayOutputStream();
		ObjectOutputStream objout;

		String[] tempAckVector = new String[ackVector.size()];
		tempAckVector = ackVector.toArray(tempAckVector);
		if(trace){
			System.out.println(Arrays.toString(tempAckVector) + " Transmitted");
		}
		try {
			objout = new ObjectOutputStream(objbytetest);
			objout.writeObject(tempAckVector);
			objout.flush();
			objout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		tes = objbytetest.toByteArray();
		try {
			objbytetest.flush();
			objbytetest.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//send Acknowledgment buffer to the server
		DatagramPacket sendPacket = new DatagramPacket(tes, tes.length, serverIP, serverPortNumber);
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Moves the receive window over one position
	 */
	public void incrementWindowPosition() {		
		while(acknowledgmentBuffer[recieve_base].startsWith("A")){
			//This will be used to decide when to close the connection
			lastFragmentRecieved = (FragmentWindow[recieve_base].getmHeader().getmEndOfSequence() == 1)?true:false;
			if(lastFragmentRecieved){
				System.out.println("sf");
			}
			//Adds the Ordered Fragment into the FragementList
			FragmentList.add(FragmentWindow[recieve_base]);
			recieve_base = (recieve_base + 1) % 32;
			window_base = (window_base + 1) % 32;
			//Always keeps the area outside of the pane ready
			acknowledgmentBuffer[window_base] = "N"+window_base;
			FragmentWindow[window_base] = null;
		}
	}


}
