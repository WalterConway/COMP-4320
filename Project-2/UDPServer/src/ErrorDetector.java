import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * This is the Server side Error Detection
 * It generates the checksum using MD5 
 * 
 * @author Walter Conway
 * @author Matt Garmon
 * 
 */
public class ErrorDetector {

	public ErrorDetector(){

	}

	/**
	 * Takes a fragment object as parameter and recreates with a checksum in the header
	 * 
	 * @param fragment
	 * @return new fragment with new Checksum entered into the header
	 */
	public Fragment generateCheckSum(Fragment fragment){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			int dataBytesLength = fragment.getDataBytes().length;
			byte[] byteArrayToHash = Arrays.copyOf(fragment.getDataBytes(), dataBytesLength+2);
			byteArrayToHash[dataBytesLength] = fragment.getmHeader().getSequenceID();
			byteArrayToHash[dataBytesLength+1] = fragment.getmHeader().getmEndOfSequence();
			md.update(byteArrayToHash);
			byte[] MD5bytes = md.digest();
			fragment.getmHeader().setCheckSum(MD5bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return fragment;
	}

	/**
	 * Validates checksum
	 * @param fragment 
	 * @return true if the checksum matches the input fragment checksum false otherwise
	 */
	public boolean validateCheckSum(Fragment fragment){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			int dataBytesLength = fragment.getDataBytes().length;
			byte[] byteArrayToHash = Arrays.copyOf(fragment.getDataBytes(), dataBytesLength+2);
			byteArrayToHash[dataBytesLength] = fragment.getmHeader().getSequenceID();
			byteArrayToHash[dataBytesLength+1] = fragment.getmHeader().getmEndOfSequence();
			md.update(byteArrayToHash);
			byte[] ExpectedMD5Bytes = md.digest();
			byte[] ActualMD5Bytes = fragment.getmHeader().getCheckSum();
			return MessageDigest.isEqual(ExpectedMD5Bytes, ActualMD5Bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}
}