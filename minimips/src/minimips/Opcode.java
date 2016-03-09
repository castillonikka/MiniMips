package minimips;

import java.util.Scanner;

public class Opcode {

	// Attributes
	private Scanner sc = new Scanner(System.in);
	private String input;
	private Scanner word;
	private String inst;
	private String opcodeHex;
	private String rs;
	private String rd;
	private String rt;
	private String func1;
	private String func2;
	private String opc;
	private String par1;
	private String par2;
	private String par3;
	private String imm;
	private int num1, num2, num3, num4, num5, num6;
	
	
	// Constructor
	public Opcode(String line)
	{
		this.input = line;
	}
	
	
	// Getters and Setters
	public String getOpcodeHex() {
		return opcodeHex;
	}
	public void setOpcodeHex(String opcodeHex) {
		this.opcodeHex = opcodeHex;
	}
	
	
	// Methods
	public String convertToBinary(int num)
	{
		String binary;
		
		return binary = Integer.toBinaryString(num); 
	}
	
	public String convertToHex(String num)
	{
		String hex;
		
		return hex = Integer.toHexString(Integer.parseInt(num, 2));
	}
	
	public String concatZero(String register, int num)
	{
		String result = "";
		
		if (num > 7 && num < 16)
			result = "0" + register;
		else if (num < 8 && num > 3)
			result = "00" + register;
		else if (num < 4 && num > 1)
			result = "000" + register;
		else if (num < 2)
			result = "0000" + register;
		
		return result;
	}
	
	public String concatZero2(String register, int num)
	{
		String imm = "";
		
		if (num < 8 && num > 3)
			imm = "0" + register;
		else if (num < 4 && num > 1)
			imm = "00" + register;
		else if (num < 2)
			imm = "000" + register;
		
		return imm;
	}
	
	public String extend (String num)
	{
		String output = "";
		
		output = ("00000000" + num).substring(num.length());
		
		return output;
	}
	
	public String generateOpcode()
	{
		/*this.input = this.sc.nextLine();*/
		this.word = new Scanner(this.input).useDelimiter(" ");
		this.inst = this.word.next().toString();
		
		switch(inst)
		{
			case "DADDU":
			case "daddu":
			case "DMULU":
			case "dmulu":
			case "DMUHU":
			case "dmuhu":
			case "SLT":
			case "slt":
			case "SELEQZ":
			case "seleqz":
				// FIRST PARAMETER = REG
				this.par1 = this.word.next().toString();
				this.num1 = Character.getNumericValue(this.par1.charAt(1));
				
				// PARAMETER 1 in binary
				String reg1 = this.convertToBinary(this.num1);
				this.rd = this.concatZero(reg1, this.num1);
				
				
				// SECOND PARAMETER = REG
				this.par2 = this.word.next().toString();
				this.num2 = Character.getNumericValue(this.par2.charAt(1));
				 
				// PARAMETER 2 in binary
				String reg2 = this.convertToBinary(this.num2);
				this.rs = this.concatZero(reg2, this.num2);
				
				
				// PARAMETER 3
				this.par3 = this.word.next().toString();
				this.num3 = Character.getNumericValue(this.par3.charAt(1));
				
				// PARAMETER 3 in binary
				String reg3 = this.convertToBinary(this.num3);
				this.rt = this.concatZero(reg3, this.num3);
				
				
				// OPCODE GENERATION
				this.opc = "000000";
				switch (inst)
				{
					case "DADDU":
					case "daddu":
						this.func1 = "00000";
						this.func2 = "101101";
						break;
					case "DMULU":
					case "dmulu":
						this.func1 = "00010";
						this.func2 = "011101";
						break;
					case "DMUHU":
					case "dmuhu":
						this.func1 = "00011";
						this.func2 = "011101";
						break;
					case "SLT":
					case "slt":
						this.func1 = "00000";
						this.func2 = "101010";
						break;
					case "SELEQZ":
					case "seleqz":
						this.func1 = "00000";
						this.func2 = "110101";
						break;
				}
				
				//String opcode = this.opc + " " + this.rs + " " + this.rt + " " + this.rd + " " + this.func1 + " " + this.func2;
				this.opcodeHex = this.convertToHex(this.opc + this.rs + this.rt + this.rd + this.func1 + this.func2);
				this.opcodeHex = this.extend(this.opcodeHex);
				
				break;
			
			case "BEQC":
			case "beqc":
			case "DADDIU":
			case "daddiu":
				// FIRST PARAMETER = REG
				this.par1 = word.next().toString();
				this.num1 = Character.getNumericValue(this.par1.charAt(1));
				
				// PARAMETER 1 in binary
				reg1 = this.convertToBinary(this.num1);
				switch (inst)
				{
					case "BEQC":
					case "beqc":
						this.rs = this.concatZero(reg1, this.num1);
						break;
					case "DADDIU":
					case "daddiu":
						this.rt = this.concatZero(reg1, this.num1);
						break;
				}
				
				
				// SECOND PARAMETER = REG
				this.par2 = word.next().toString();
				this.num2 = Character.getNumericValue(this.par2.charAt(1));
				
				// PARAMETER 2 in binary
				reg2 = this.convertToBinary(this.num2);
				switch (inst)
				{
					case "BEQC":
					case "beqc":
						this.rt = this.concatZero(reg2, this.num2);
						break;
					case "DADDIU":
					case "daddiu":
						this.rs = this.concatZero(reg2, this.num2);
						break;
				}
				
				
				// THIRD PARAMETER = IMMEDIATE FOR DADDIU
				if (this.inst.equals("DADDIU") || this.inst.equals("daddiu"))
				{
					String par3 = word.next().toString();
					this.num3 = Character.getNumericValue(par3.charAt(0));
					String imm1 = this.convertToBinary(this.num3);
					
					this.num4 = Character.getNumericValue(par3.charAt(1));
					String imm2 = this.convertToBinary(this.num4);
					
					this.num5 = Character.getNumericValue(par3.charAt(2));
					String imm3 = this.convertToBinary(this.num5);
					
					this.num6 = Character.getNumericValue(par3.charAt(3));
					String imm4 = this.convertToBinary(this.num6);
					
					this.opc = "011001";
					
					this.imm = this.concatZero2(imm1, num3);
					this.imm = this.imm + this.concatZero2(imm2, num4);
					this.imm = this.imm + this.concatZero2(imm3, num5);
					this.imm = this.imm + this.concatZero2(imm4, num6);
					
				}
				else
				{
					// OFFSET FOR BEQC
					
					this.opc = "001000";
				}
				
				//opcode = opc + " " + rs + " " + rt + " " + imm;
				this.opcodeHex = this.convertToHex(this.opc + this.rs + this.rt + this.imm);
				this.opcodeHex = this.extend(this.opcodeHex);
				
				break;
				
				
			case "LD":
			case "ld":
			case "SD":
			case "sd":
				// FIRST PARAMETER = REG
				this.par1 = word.next().toString();
				this.num1 = Character.getNumericValue(this.par1.charAt(1));
				
				// PARAMETER 1 in binary
				reg1 = this.convertToBinary(this.num1);
				this.rt = this.concatZero(reg1, this.num1);
				
				
				// SECOND PARAMETER = IMMEDIATE/MEMORY (offset)
				
				
				
				// THIRD PARAMETER = REG (base)
				
				
				
				// GENERATE OPCODE
				switch (inst)
				{
					case "LD":
					case "ld":
						this.opc = "110111";
						break;
					case "SD":
					case "sd":
						this.opc = "111111";
						break;
				}
				
				// opcode with base and offset
				
				break;
			
				
			case "BC":
			case "bc":
				// FIRST PARAMETER = OFFSET
				String label = "";
				
				
				// GENERATE OPCODE
				this.opc = "110010";
				this.opcodeHex = this.convertToHex(opc + label);
				this.opcodeHex = this.extend(this.opcodeHex);
				
				break;
			
			
			default:
				System.out.println("ERROR INSTRUCTION");
		}
		
		return this.opcodeHex;
	}
	
}
