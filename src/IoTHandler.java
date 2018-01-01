
public class IoTHandler implements Handler {

	@Override
	public void handleMessage(String m) {
		// TODO Auto-generated method stub
		if (m.equals("Hello")) {
			System.out.println("Hello my boy");
		}
	}

}
