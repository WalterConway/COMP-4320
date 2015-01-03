import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Client Side SAR
 * 
 * @author Matthew Garmon
 * @author WalterC
 *
 */
public class SAR {

	ArrayList<Fragment> fragmentList;
	ErrorDetector ed = new ErrorDetector();
	byte[] mData;
	
	/**
	 * Constructor for the SAR object
	 */
	public SAR(){
		 fragmentList = new ArrayList<Fragment>();
	}

	/**
	 * @return mData is the all the data recompiled from the fragments
	 */
	public byte[] getData() {
		return mData;
	}

	/**
	 * @param fragment
	 */
	public void addFragment(Fragment fragment){
		fragmentList.add(fragment);
	}
	
	/**
	 * @param fragList
	 */
	public void setFragmentList(ArrayList<Fragment> fragList){
		fragmentList = fragList;
	}

	/**
	 * converts the arraylist of fragments into an array of bytes  
	 */
	public void unSegmentFile(){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		
		while(!fragmentList.isEmpty()){
			Fragment temp = fragmentList.remove(0);
			try {
				outputStream.write(temp.getDataBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mData = outputStream.toByteArray();
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}