import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class Fragment {
	private FragmentHeader mHeader;	
	private byte[] mData;

	public Fragment(byte[] data){
		mData = data;
		mHeader = new FragmentHeader();
	}

	public FragmentHeader getmHeader() {
		return mHeader;
	}

	public void setDataBytes(byte[] Data) {
		mData = Data;
	}

	public byte[] getDataBytes(){
		return mData;
	}
	
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

	public class FragmentHeader{
		private byte[] mCheckSum;
		private byte mSequenceID;
		private byte mEndOfSequence;

		public byte getmEndOfSequence() {
			return mEndOfSequence;
		}
		public void setmEndOfSequence(byte endOfSequence) {
			mEndOfSequence = endOfSequence;
		}
		public byte[] getCheckSum() {
			return mCheckSum;
		}
		public void setCheckSum(byte[] checkSum) {
			mCheckSum = checkSum;
		}
		public byte getSequenceID() {
			return mSequenceID;
		}
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