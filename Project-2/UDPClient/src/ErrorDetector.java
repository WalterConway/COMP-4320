import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Client Side Error Detector. Generates Checksum on a received fragment and compares to value
 * in the fragments header
 * @author Matthew Garmon
 * @author WalterC
 *
 */
public class ErrorDetector {

	/**
	 * @param fragment
	 * @return
	 */
	public static Fragment generateCheckSum(Fragment fragment){
		if(fragment != null){
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
		return null;

	}

	/**
	 * @param fragment
	 * @return true if the data is valid false otherwise
	 */
	public static boolean validateCheckSum(Fragment fragment){
		if(fragment != null){
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
				return Arrays.equals(ActualMD5Bytes, ExpectedMD5Bytes);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return false;
			}
		} 
		return false;
	}
}