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
	private String EX_MEM_B;
	private String MEM_WB_IR;
	private String MEM_WB_ALU;
	private String MEM_ALUoutput;
	private String MEM_WB_LMD;
	private String wbDisplay;
	private String code;
	private String opcode;
	private int num;
	
	// Getters and Setters
	
	public void setIF_ID_IR(String iF_ID_IR) {
		IF_ID_IR = iF_ID_IR;
	}
	public String getWbDisplay() {
		return wbDisplay;
	}
	public void setWbDisplay(String wbDisplay) {
		this.wbDisplay = wbDisplay;
	}
	public void setPC(String pC) {
		PC = pC;
	}
	public void setIF_ID_NPC(String iF_ID_NPC) {
		IF_ID_NPC = iF_ID_NPC;
	}
	public void setID_EX_A(String iD_EX_A) {
		ID_EX_A = iD_EX_A;
	}
	public void setID_EX_B(String iD_EX_B) {
		ID_EX_B = iD_EX_B;
	}
	public void setID_EX_IMM(String iD_EX_IMM) {
		ID_EX_IMM = iD_EX_IMM;
	}
	public void setID_EX_IR(String iD_EX_IR) {
		ID_EX_IR = iD_EX_IR;
	}
	public void setID_EX_NPC(String iD_EX_NPC) {
		ID_EX_NPC = iD_EX_NPC;
	}
	public void setEX_MEM_IR(String eX_MEM_IR) {
		EX_MEM_IR = eX_MEM_IR;
	}
	public void setEX_MEM_ALU(String eX_MEM_ALU) {
		EX_MEM_ALU = eX_MEM_ALU;
	}
	public void setEX_MEM_COND(String eX_MEM_COND) {
		EX_MEM_COND = eX_MEM_COND;
	}
	public void setMEM_WB_IR(String mEM_WB_IR) {
		MEM_WB_IR = mEM_WB_IR;
	}
	public void setMEM_WB_LMD(String mEM_WB_LMD) {
		MEM_WB_LMD = mEM_WB_LMD;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String inst) {
		this.code = inst;
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
	public String getEX_MEM_B() {
		return EX_MEM_B;
	}
	public void setEX_MEM_B(String eX_MEM_B) {
		EX_MEM_B = eX_MEM_B;
	}
	public String getMEM_WB_ALU() {
		return MEM_WB_ALU;
	}
	public void setMEM_WB_ALU(String mEM_WB_ALU) {
		MEM_WB_ALU = mEM_WB_ALU;
	}
	public String getMEM_ALUoutput() {
		return MEM_ALUoutput;
	}
	public void setMEM_ALUoutput(String mEM_ALUoutput) {
		MEM_ALUoutput = mEM_ALUoutput;
	}
}
