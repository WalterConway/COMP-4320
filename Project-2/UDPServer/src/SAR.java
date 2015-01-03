import java.util.ArrayList;
import java.util.Arrays;


/** 
 * Server Side SAR
 * Segments the object into fragments
 * 
 * @author Matthew Garmon
 * @author WalterC
 *
 */
public class SAR {

	ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
	ErrorDetector ed = new ErrorDetector();
	byte[] mData;
	public SAR(byte[] totalData){
		mData = totalData;
		if(mData !=null || mData.length != 0){
			SegmentObject();
		}
	}

	/**
	 * Using the Error Detector this separates the file into fragments with headers and stores them
	 * in the Servers Fragment List
	 */
	public void SegmentObject(){
		int start = 0;
		int end = 110;
		for(int i =0; i<(double)mData.length/110; i++){
			Fragment newFragment = new Fragment(Arrays.copyOfRange(mData, start, end));
			newFragment.getmHeader().setSequenceID((byte)(i%32));
			if((i+1)<(double)mData.length/110){
				newFragment.getmHeader().setmEndOfSequence((byte)0);
			} else{
				newFragment.getmHeader().setmEndOfSequence((byte)1);
			}
			addFragment(ed.generateCheckSum(newFragment));
			start = end;
			end = end+110;
		}
	}

	/**
	 * @return
	 */
	public boolean hasNext(){
		return (fragmentList.size()==0)?false:true;
	}

	/**
	 * @return
	 */
	public Fragment next(){
		return fragmentList.remove(0);
	}

	/**
	 * @param fragment
	 */
	public void addFragment(Fragment fragment){
		fragmentList.add(fragment);
	}
}