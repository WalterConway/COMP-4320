
public class HTTPHeader {
	private String mContentLength;
	private String mContentType;
	private int mStatusCode;
	private final String mHTTPversion = "HTTP/1.0";

	public HTTPHeader(String contentLength, int statusCode, String contentType){
		mContentLength = contentLength;
		mStatusCode = statusCode;
		mContentType = contentType;
	}

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
	

	
	public int getmStatusCode() {
		return mStatusCode;
	}

	public void setmStatusCode(int mStatusCode) {
		this.mStatusCode = mStatusCode;
	}

	public String toString(){
		String header = mHTTPversion + " " + mStatusCode + " "+statusCodePhraseGenerator(mStatusCode) + "\r\n" +
	"Content-Type: " +mContentType+"\r\n"+"Content-Length: " + mContentLength+"\r\n";
		return header;
	}
	
	
	
}
