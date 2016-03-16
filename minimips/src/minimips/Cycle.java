package minimips;

public class Cycle {

	// Attributes
	private String IF_ID_IR;
	private String IF_ID_NPC;
	private String PC;
	private String ID_EX_A;
	private String ID_EX_B;
	private String ID_EX_IMM;
	private String ID_EX_IR;
	private String ID_EX_NPC;
	private String EX_MEM_IR;
	private String EX_MEM_ALU;
	private String EX_MEM_COND;
	private String MEM_WB_IR;
	private String MEM_WB_LMD;
	private String inst;
	private String opcode;
	private int num;
	
	// Getters and Setters
	public String getInst() {
		return inst;
	}
	public void setInst(String inst) {
		this.inst = inst;
	}
	public int getNum() {
		return this.num;
	}
	public String getOpcode() {
		return opcode;
	}
	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}
	public String getIF_ID_IR() {
		return IF_ID_IR;
	}
	public String getIF_ID_NPC() {
		return IF_ID_NPC;
	}
	public String getID_EX_A() {
		return ID_EX_A;
	}
	public String getID_EX_B() {
		return ID_EX_B;
	}
	public String getID_EX_IMM() {
		return ID_EX_IMM;
	}
	public String getID_EX_IR() {
		return ID_EX_IR;
	}
	public String getID_EX_NPC() {
		return ID_EX_NPC;
	}
	public String getEX_MEM_IR() {
		return EX_MEM_IR;
	}
	public String getEX_MEM_ALU() {
		return EX_MEM_ALU;
	}
	public String getEX_MEM_COND() {
		return EX_MEM_COND;
	}
	public String getMEM_WB_IR() {
		return MEM_WB_IR;
	}
	public String getMEM_WB_LMD() {
		return MEM_WB_LMD;
	}
	public String getPC() {
		return PC;
	}
	
	// Methods
	public void generateCycle()
	{
		
	}
	
	
}
