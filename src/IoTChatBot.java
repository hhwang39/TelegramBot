import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IoTChatBot implements Runnable {
	private static final String TOKEN = ;
	private static final String BASEURL = ;
	private static final int CHAT_ID = 433283462;
	private static final int SUCCESS_CODE = 200;
	private Handler mHandler;
	private static int message_id;
	private Gson gson;
	private enum METHOD {
		GETME,
		GETUPDATES,
		SENDMESSAGE,
		SENDMESSAGE_JSON
	};
	public IoTChatBot() {
		message_id = 0;
		// mHandler = h;
		gson = new Gson();
	}
	public void notifyUser()  {
		// TODO notify user when schedule is before 
	}
	// not yet implemnted
	public void getMe() {
		
	}
	@Override
	public void run() {
		while (true) {
			String res = runMethod(METHOD.GETUPDATES, null);
			System.out.println(res);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public String runMethod(METHOD m, HashMap<String, String> map) {
		String s = null;
		boolean isGet = false;
		switch (m) {
			case GETME:
				s = "getMe";
				isGet = true;
				break;
			case GETUPDATES:
				s = "getUpdates";
				isGet = true;
				break;
			case SENDMESSAGE:
				s = "sendMessage";
				isGet = false;
				break;
		}
		URL url = createURL(s, map);
		String res = null;
		try {
			// TODO keep the connection (reuse the connection instead of making more) 
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			if (isGet) conn.setRequestMethod("GET");
			else conn.setRequestMethod("POST");
			conn.connect();
			if (conn.getResponseCode() == SUCCESS_CODE) {
				System.out.println("Sucessfully connected");
				InputStream in = conn.getInputStream();	
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = reader.readLine();
				StringBuilder sb = new StringBuilder();
				while (line != null) {
					sb.append(line);
					line = reader.readLine();
				}
				// Debug
				res = sb.toString();
				System.out.println(res);
				res = parseAndProcessData(m, sb.toString());
				System.out.println(res);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
		return res;
	}
	public static void main(String[] args) {
		IoTChatBot iot = new IoTChatBot();
		iot.sendKeyBoard();
	}
	private String parseAndProcessData(METHOD m, String data) {
		String res = null;
		switch (m) {
			case GETUPDATES:
				// System.out.println("string is : " + data);
				Result rObj = new Gson().fromJson(data, Result.class);
				// System.out.println(rObj == null);
				if ((rObj.ok) && (rObj.result.size() > 0)) {
					Update uObj = rObj.result.get(rObj.result.size() - 1);
					Message mObj = uObj.message;
					System.out.println("Message id is " + String.valueOf(mObj.message_id));
					System.out.println("Current id is " + String.valueOf(message_id));
					if (mObj.message_id > message_id) {
						System.out.println("Recieved Text is " + mObj.text);
						res = mObj.text;
						// start command
						System.out.println("res equals /start " + String.valueOf("/start".equals(res)));
						// options to take given user input
						if (res.equals("/start")) displayOptions();
						else displayUnsupportedOptionChosen();
						// System.out.println(mess.text);
						message_id = mObj.message_id;
					}						
				}
				break;
		}
		return null;
	}
	/**
	 * This method will send a message (string) to the user with given text
	 * @param replyText A reply text Machine wants to send to the user 
	 */
	private void sendMessage(String replyText) {
		mHandler.handleMessage("Hello");
		// encode data
		try {
			replyText =  URLEncoder.encode(replyText, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Reply r = new Reply(CHAT_ID, replyText, "Markdown");
		HashMap<String, String> map = createReply(r);
		String res = runMethod(METHOD.SENDMESSAGE, map);
		System.out.println("response from reply was " + res);
	}
	/**
	 * Upon receiving /start command it will display options that user can take
	 */
	private void displayOptions() {
		System.out.println("Displying Options");
		String replyText = "";
		replyText = "Options\n"
					+ "/turn_AC_on\n"
					+ "/turn_TV_on\n"
					+ "/play_music";
		sendMessage(replyText);
	}
	private void displayUnsupportedOptionChosen() {
		System.out.println("Displaying supported options");
		String replyText = "Not a Supported Option\n"
							+ "You may want to type /start\n"
							+ "to display options";
		sendMessage(replyText);
	}
	private void sendKeyBoard() {
		HashMap<String, String> map = new HashMap<>();
		ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
		KeyboardButton key1 = new KeyboardButton();
		KeyboardButton key2 = new KeyboardButton();
		key1.text = "English";
		key2.text = "ÇÑ±¹¾î";
		keyboard.keyboard = new KeyboardButton[2][2];
		keyboard.keyboard[0][0] = key1;
		keyboard.keyboard[0][1] = key2;
		keyboard.keyboard[1][0] = key1;
		keyboard.keyboard[1][1] = key2;
		map.put("chat_id", String.valueOf(CHAT_ID));
		map.put("text", "Hello");
		System.out.println(gson.toJson(keyboard).toString());
		map.put("reply_markup", gson.toJson(keyboard).toString());
		String result = runMethod(METHOD.SENDMESSAGE, map);
		System.out.println(result);
	}
	private HashMap<String, String> createReply(Reply r) {
		// Field[] f = r.getClass().getFields();
		LinkedHashMap<String, String> tmpMap = new LinkedHashMap<>();
		// esssential elements
		// todo 
		
		tmpMap.put("chat_id", String.valueOf(r.chat_id));
		tmpMap.put("text", r.text);
//		private boolean disable_web_page_preview;
//		private boolean disable_notification;
		
		// optional elements
		if ((r.parse_mode != null) && (!r.parse_mode.isEmpty())) tmpMap.put("parse_mode", r.parse_mode);
		if ((r.disable_web_page_preview)) tmpMap.put("disable_web_page_preview", 
													String.valueOf(r.disable_web_page_preview));
		if (r.disable_notification) tmpMap.put("disable_notification", 
												String.valueOf(r.disable_notification));
		
		return tmpMap;
	}
	private URL createURL(String method, HashMap<String, String> map) {
		URL retURL = null;
		String url = BASEURL + TOKEN + "/" + method;
		if (map != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(url);
			sb.append('?');
			for (String key : map.keySet()) {
				sb.append(key);
				sb.append('=');
				sb.append(map.get(key));
				sb.append('&');
			}
			url = sb.substring(0, sb.length() - 1);
		}
		try {
			retURL = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(url);
		return retURL;
	}
	private static class Result {
		private boolean ok;
		private List<Update> result;
	}
	private static class Reply {
		private int chat_id;
		private String text;
		private String parse_mode;
		private boolean disable_web_page_preview;
		private boolean disable_notification;
		public Reply(int id, String text) {
			this(id, text, null);
		}
		public Reply(int id, String text, String parse_mode) {
			this(id, text, parse_mode, false);
		}
		public Reply(int id, String text, String parse_mode, boolean disable) {
			this(id, text, parse_mode, disable, false);
		}
		public Reply(int id, String text, String parse_mode, boolean disable_web, boolean disable_not) {
			this.chat_id = id;
			this.text = text;
			this.parse_mode = parse_mode;
			this.disable_web_page_preview = disable_web;
			this.disable_web_page_preview = disable_not;
		}
	}
	private static class Message {
		private int message_id;
		private User from; //optional
		private Chat chat;
		private int date;
		private String text;
		// optionals
	}
	private static class Chat {
		private double id;
		private String first_name;
		private String last_name;
		private String type;
	}
	private static class Update {
		private int update_id;
		private Message message;
	}
	private static class User {
		private int id;
		private String first_name;
		private String last_name;
		private String language_code;
	}
	private static class ReplyKeyboardMarkup {
		KeyboardButton[][] keyboard;
		boolean resize_keyboard;
		boolean one_time_keyboard;
		boolean selective;
	}
	private static class KeyboardButton {
		String text;
		boolean request_contact;
		boolean request_location;
	}
	
}
