import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Matthew Garmon
 * @author WalterC
 *
 */
public class Fragment {
	private FragmentHeader mHeader;	
	private byte[] mData;

	/**
	 * @param data
	 */
	public Fragment(byte[] data){
		mData = data;
		mHeader = new FragmentHeader();
	}

	/**
	 * @return
	 */
	public FragmentHeader getmHeader() {
		return mHeader;
	}

	/**
	 * @param Data
	 */
	public void setDataBytes(byte[] Data) {
		mData = Data;
	}

	/**
	 * @return
	 */
	public byte[] getDataBytes(){
		return mData;
	}
	
	/**
	 * Gets the data that is being held in the entire fragment
	 * @return data being stored in the fragment object
	 */
	public byte[] getFragmentBytes(){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(getmHeader().getHeaderBytes());
			outputStream.write(getDataBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return outputStream.toByteArray();
	}

	/**
	 * @author Matthew Garmon
	 * @author WalterC
	 */
	public class FragmentHeader{
		private byte[] mCheckSum;
		private byte mSequenceID;
		private byte mEndOfSequence;

		/**
		 * @return
		 */
		public byte getmEndOfSequence() {
			return mEndOfSequence;
		}
		/**
		 * @param endOfSequence
		 */
		public void setmEndOfSequence(byte endOfSequence) {
			mEndOfSequence = endOfSequence;
		}
		/**
		 * @return
		 */
		public byte[] getCheckSum() {
			return mCheckSum;
		}
		/**
		 * @param checkSum
		 */
		public void setCheckSum(byte[] checkSum) {
			mCheckSum = checkSum;
		}
		/**
		 * @return
		 */
		public byte getSequenceID() {
			return mSequenceID;
		}
		/**
		 * @param sequenceID
		 */
		public void setSequenceID(byte sequenceID) {
			mSequenceID = sequenceID;
		}

		/**
		 * @return first 16bytes are checksum next byte is sequenceid the last byte is end of sequence flag
		 */
		public byte[] getHeaderBytes(){
			byte[] headerByteArray = Arrays.copyOf(mCheckSum, mCheckSum.length + 2);
			headerByteArray[mCheckSum.length] = getSequenceID();
			headerByteArray[mCheckSum.length+1] = getmEndOfSequence();
			return headerByteArray;
		}
		
	}
}