package minimips;

public class Memory {

	private String addressHex;
	private String value;
	
	// Getters and Setters
	public String getAddressHex() {
		return addressHex;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setAddressHex(int num) {
		this.addressHex = (Integer.toHexString(Integer.parseInt(Integer.toBinaryString(num), 2))).toUpperCase();
	}
}
