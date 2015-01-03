import java.net.DatagramPacket;
import java.util.Arrays;


/**
 * @author Matthew Garmon
 * @author WalterC
 *
 */
public class Tool {
	
	/**
	 * This tool is used to convert the Datagram received by the client into a Fragment object
	 * that is used by the rest of the Client program. This tool also determines if the fragment
	 * is the last fragment
	 * @param datagram
	 * @return
	 */
	public static Fragment dataGramToFragment(DatagramPacket datagram){
		if(datagram != null){
		byte[] data = datagram.getData();
		Fragment tempFragment = new Fragment(Arrays.copyOfRange(data, 18, data.length));
		tempFragment.getmHeader().setCheckSum(Arrays.copyOfRange(data, 0, 16));
		tempFragment.getmHeader().setSequenceID(data[16]);
		//Set fragment's end of Sequence value
		tempFragment.getmHeader().setmEndOfSequence(data[17]);
		return tempFragment;
	}
		return null;
	}
	
}
