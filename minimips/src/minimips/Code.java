package minimips;

public class Code {

	private String pcHex;
	private String opcode;
	private String rs;
	private String rt;
	private String rd;
	private String label;
	private String imm;
	private String inst;
	private String line;
	private int pcTarget;
	private int pcInt;
	
	// Getters and Setters
	public String getPcHex() {
		return pcHex;
	}
	public void setPcHex(String pcHex) {
		this.pcHex = pcHex;
	}
	public String getRs() {
		return rs;
	}
	public void setRs(String rs) {
		this.rs = rs;
	}
	public String getRt() {
		return rt;
	}
	public void setRt(String rt) {
		this.rt = rt;
	}
	public String getRd() {
		return rd;
	}
	public void setRd(String rd) {
		this.rd = rd;
	}
	public String getOpcode() {
		return opcode;
	}
	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getImm() {
		return imm;
	}
	public void setImm(String imm) {
		this.imm = imm;
	}
	public String getInst() {
		return inst;
	}
	public void setInst(String inst) {
		this.inst = inst;
	}
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public int getPcInt() {
		return pcInt;
	}
	public void setPcInt(int pcInt) {
		this.pcInt = pcInt;
	}
	public int getPcTarget() {
		return pcTarget;
	}
	public void setPcTarget(int pcTarget) {
		this.pcTarget = pcTarget;
	}
	
	// Methods
	
	public void printAttributes()
	{
		System.out.println("DETAILS ARE:");
		System.out.println("LINE -- " + this.line);
		System.out.println("INST -- " + this.inst);
		System.out.println("RD -- " + this.rd);
		System.out.println("RS -- " + this.rs);
		System.out.println("RT -- " + this.rt);
		System.out.println("IMM -- " + this.imm);
		System.out.println("LABEL -- " + this.label);
		System.out.println("PC -- " + this.pcHex);
	}
}
