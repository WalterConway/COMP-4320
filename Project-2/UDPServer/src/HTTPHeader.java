
/**
 * @author Matthew Garmon
 * @author WalterC
 *
 */
public class HTTPHeader {
	private String mContentLength;
	private String mContentType;
	private int mStatusCode;
	private final String mHTTPversion = "HTTP/1.0";

	/**
	 * @param contentLength
	 * @param statusCode
	 * @param contentType
	 */
	public HTTPHeader(String contentLength, int statusCode, String contentType){
		mContentLength = contentLength;
		mStatusCode = statusCode;
		mContentType = contentType;
	}

	/**
	 * @param code
	 * @return
	 */
	public String statusCodePhraseGenerator(int code){
		switch(code){
		case 200:
			return "Document Follows";
		case 404:
			return "File Not Found.";
		case 400:
			return "Bad Request";
		default:
			return "Invalid Status Code";
		}
	}

	/**
	 * @return
	 */
	public int getmStatusCode() {
		return mStatusCode;
	}

	/**
	 * @param mStatusCode
	 */
	public void setmStatusCode(int mStatusCode) {
		this.mStatusCode = mStatusCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String header = mHTTPversion + " " + mStatusCode + " "+statusCodePhraseGenerator(mStatusCode) + "\r\n" +
				"Content-Type: " +mContentType+"\r\n"+"Content-Length: " + mContentLength+"\r\n";
		return header;
	}
}
