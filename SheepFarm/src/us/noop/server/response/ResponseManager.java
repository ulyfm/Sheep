package us.noop.server.response;

import java.util.ArrayList;
import java.util.HashMap;

import us.noop.server.pages.Page;
import us.noop.server.pages.Page404;

/**
 * A class to manage the Response Threads generated by the server, mainly closing them and removing excess.
 * Also organizes responses based on pages.
 * 
 * Possibly a bad idea.
 * @author Ulysses
 *
 */
public class ResponseManager {
	
	private Page404 fffp = new Page404();
	
	private ArrayList<ResponseThread> availableThreads = new ArrayList<ResponseThread>();
	
	public void cleanResponders(){
		for(int i = 0; i < availableThreads.size(); ++i){
			ResponseThread rt = availableThreads.get(i);
			if(System.currentTimeMillis() - rt.getLastTime() > 10000l){
				rt.setDestroy(true);
				availableThreads.remove(rt);
			}
		}
	}
	
	public void addResponse(Response r){
		if(availableThreads.size() > 0){
			availableThreads.remove(availableThreads.size() - 1).setResponse(r);
		}else{
			ResponseThread ct = new ResponseThread(this, r);
			ct.start();
		}
	}
	
	private HashMap<String, Page> pages = new HashMap<String, Page>();
	
	/**
	 * Returns an appropriate response to the address.
	 * Pages are registered in Server
	 * @param requestData the address passed by the client
	 * @return a page
	 */
	public byte[] getResponse(RequestData requestData){
		int i = 0;
		String currentR = null;
		for(String s : pages.keySet()){
			if(requestData.getAddress().startsWith(s)){
				if(s.split("/").length >= i){
					i = s.split("/").length;
					currentR = s;
				}
			}
		}
		byte[] r;
		if(currentR == null) r = fffp.getResponse(requestData);
		else r = pages.get(currentR).getResponse(requestData);
		return r;
	}
	
	public static String generateHeader(int code, String ctext, String content, String MIME){
		return "HTTP/1.1 " + code + " " + ctext + "\r\nContent-Length: " + content.length() + "\r\nConnection: Closed\r\nContent-Type: " + MIME + "\r\n\r\n" + content;
	}
	
	/**
	 * Adds a page that will be a valid request
	 * @param p the page to add (the address should be contained in the class)
	 */
	public void addPage(Page p){
		pages.put(p.getAddress(), p);
	}
	
	private int id = 0;
	
	/**
	 * A utility method to make Responses easier to display in console.
	 * @return the next unused id
	 */
	public int nextId(){
		id++;
		return id - 1;
		
	}

	public synchronized void renewResponse(ResponseThread responseThread) {
		availableThreads.add(responseThread);
	}
}
