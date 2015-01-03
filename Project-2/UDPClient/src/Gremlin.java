import java.net.DatagramPacket;
import java.util.Random;


/**
 * @author Matthew Garmon
 * @author Walter Conway
 * Corrupts the Data comming in from the server
 */
public class Gremlin {
	Random rand;
	double mLossProbability;
	double mCorruptProbablility;
	double mPassProbability;
	int numCorrupt = 0;
	int numLoss = 0;
	int numPass = 0;
	DatagramPacket packet;

	/**
	 * params can not both exceed more than 1 if both are added together
	 * @param lossProbability can not exceed more than 1
	 * @param corruptProbability can not exceed more than 1
	 */
	public Gremlin(double lossProbability, double corruptProbability){
		rand = new Random();
		mLossProbability = lossProbability;
		mCorruptProbablility = corruptProbability;
		mPassProbability = 1 - (lossProbability + corruptProbability);
	}

	/**
	 * @param datagram
	 */
	private void loadDatagramPacket(DatagramPacket datagram){
		packet = datagram;
	}
	/**
	 * @return
	 */
	public DatagramPacket getDatagramPacket(){
		return packet;
	}

	/**
	 * Corrupts the Data in the Datagram according to specifications
	 * @author WalterC
	 * @return DatagramPacket with the corrupted data
	 */
	private DatagramPacket corruptDatagramPacket(){
		double sample = rand.nextDouble();
		byte[] data = getDatagramPacket().getData();


		
		if(sample <=.5){
			//randomly selects a byte from the data and then from that byte selects a bit and flips it
			//does this once
			int randomIndex = rand.nextInt(getDatagramPacket().getData().length);
			byte byteVar = data[randomIndex];
			int byteToInt = byteVar >= 0?byteVar:256 + byteVar;
			//Selects a random bit from the  randomly selected byte from the data to flip
			int x = rand.nextInt(8-(1-1)) % 8;
			int flippedInt = byteToInt ^ 1 << x;
			//converts the int back into a byte
			data[randomIndex]=(byte)flippedInt;
			
		}else if(sample <= (.3 + .5)){
			//randomly selects a byte from the data and then from that byte selects a bit and flips it
			//does this twice
			for(int i = 0; i<2; i++){
			int randomIndex = rand.nextInt(getDatagramPacket().getData().length);
			byte byteVar = data[randomIndex];
			int byteToInt = byteVar >= 0?byteVar:256 + byteVar;
			int x = rand.nextInt(8-(1-1)) % 8;
			int flippedInt = byteToInt ^ 1 << x;
			//converts the int back into a byte
			data[randomIndex]=(byte)flippedInt;
			}
			
		} else{
			//randomly selects a byte from the data and then from that byte selects a bit and flips it
			//does this three times
			for(int i = 0; i<3; i++){
			int randomIndex = rand.nextInt(getDatagramPacket().getData().length);
			byte byteVar = data[randomIndex];
			int byteToInt = byteVar >= 0?byteVar:256 + byteVar;
			int x = rand.nextInt(8-(1-1)) % 8;
			int flippedInt = byteToInt ^ 1 << x;
			//converts the int back into a byte
			data[randomIndex]=(byte)flippedInt;
			}
		}
		getDatagramPacket().setData(data);
		return getDatagramPacket();
	

	}

	/**
	 * @return
	 */
	public DatagramPacket looseDatagramPacket(){
		return null;
	}

	/**
	 * @return
	 */
	public DatagramPacket passDatagramPacket(){
		return packet;
	}

	/**
	 * @author Walter Conway
	 * @param datagram Uncorrupted Datagram packet
	 * @return DatagramPacket of the corrupted Data in the Datagram packet
	 */
	public DatagramPacket filter(DatagramPacket datagram){
		loadDatagramPacket(datagram);
		double sample = rand.nextDouble();
		if(sample <= getmPassProbability()){
			numPass++;

			return passDatagramPacket();
		} else if( sample <= getmPassProbability() + getmCorruptProbablility()){
			numCorrupt++;

			return corruptDatagramPacket();
		} else{
			numLoss++;

			return looseDatagramPacket();
		}
	}

	/**
	 * @return
	 */
	public double getmLossProbability() {
		return mLossProbability;
	}

	/**
	 * @param mLossProbability
	 */
	public void setmLossProbability(double mLossProbability) {
		this.mLossProbability = mLossProbability;
	}

	/**
	 * @return
	 */
	public double getmCorruptProbablility() {
				
		return mCorruptProbablility;
	}

	/**
	 * @param mCorruptProbablility
	 */
	public void setmCorruptProbablility(double mCorruptProbablility) {
		this.mCorruptProbablility = mCorruptProbablility;
	}

	/**
	 * @return
	 */
	public double getmPassProbability() {
		return mPassProbability;
	}

	/**
	 * @param mPassProbability
	 */
	public void setmPassProbability(double mPassProbability) {
		this.mPassProbability = mPassProbability;
	}




}
