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
	private String label;
	private String output;
	private boolean error;
	private String errorMessage;
	
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
	
	public boolean isError() {
		return error;
	}


	public void setError(boolean error) {
		this.error = error;
	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
	
	public void setErrorMessage(String error)
	{
		this.errorMessage = error;
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
		else if (num >= 16)
			result = register;
		
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
	
	public int convertToNum (char a)
	{
		int num = 0;
			switch (a)
			{
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					num = Character.getNumericValue(a);
					break;
				case 'A':
					num = 10;
					break;
				case 'B':
					num = 11;
					break;
				case 'C':
					num = 12;
					break;
				case 'D':
					num = 13;
					break;
				case 'E':
					num = 14;
					break;
				case 'F':
					num = 15;
					break;
			}
		
		System.out.println(num);
		return num;
	}
	
	public String generateOpcode(String inst)
	{
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
					if (this.num1 == 0)
					{
						this.error = true;
						this.errorMessage = "Invalid register.";
						break;
					}
					else
					{
						// if reg is from 10 to 31
						if (Character.getNumericValue(this.par1.charAt(2)) >= 0 && Character.getNumericValue(this.par1.charAt(2)) <= 9)
						{
							System.out.println(Character.getNumericValue(this.par1.charAt(1)));
							System.out.println(Character.getNumericValue(this.par1.charAt(2)));
							int temp = Character.getNumericValue(this.par1.charAt(2));
							this.num1 = (this.num1 * 10) + temp;
							System.out.println(this.num1);
							
							// PARAMETER 1 in binary
							String reg1 = this.convertToBinary(this.num1);
							this.rd = this.concatZero(reg1, this.num1);
							System.out.println("RD is " + rd);
							if (this.num1 > 31)
							{
								this.error = true;
								this.errorMessage = "Invalid register.";
								break;
							}
						}
						else
						{
							// PARAMETER 1 in binary
							String reg1 = this.convertToBinary(this.num1);
							this.rd = this.concatZero(reg1, this.num1);
							System.out.println("RD is " + rd);
						}
					}
					
					
					
					// SECOND PARAMETER = REG
					this.par2 = this.word.next().toString();
					this.num2 = Character.getNumericValue(this.par2.charAt(1));
					// if reg is from 10 to 31
					if (Character.getNumericValue(this.par2.charAt(2)) >= 0 && Character.getNumericValue(this.par2.charAt(2)) <= 9)
					{
						System.out.println(Character.getNumericValue(this.par2.charAt(1)));
						System.out.println(Character.getNumericValue(this.par2.charAt(2)));
						int temp = Character.getNumericValue(this.par2.charAt(2));
						this.num2 = (this.num2 * 10) + temp;
						System.out.println(this.num2);
						
						// PARAMETER 2 in binary
						String reg2 = this.convertToBinary(this.num2);
						this.rs = this.concatZero(reg2, this.num2);
						System.out.println("RS is " + rs);
						if (this.num2 > 31)
						{
							this.error = true;
							this.errorMessage = "Invalid register.";
							break;
						}
					}
					else
					{
						// PARAMETER 2 in binary
						String reg2 = this.convertToBinary(this.num2);
						this.rs = this.concatZero(reg2, this.num2);
						System.out.println("RS is " + rs);
					}
					
					
					try
					{
						// THIRD PARAMETER = REG
						this.par3 = this.word.next().toString();
						String register = par3.substring(1);
						System.out.println(register);
						//this.num3 = Character.getNumericValue(this.par3.charAt(1));
						int tempC = Integer.valueOf(register).intValue();
						System.out.println("hello " + tempC);
						
						this.num3 = tempC;
						if (this.num3 > 31)
						{
							this.error = true;
							this.errorMessage = "Invalid register.";
							break;
						}
						else
						{
							System.out.println("hi " + this.num3);
							String reg3 = this.convertToBinary(this.num3);
							System.out.println("Register: " + reg3);
							this.rt = this.concatZero(reg3, this.num3);
							System.out.println("RT is " + this.rt);
						}
					}
					catch (NumberFormatException e)
					{
						this.error = true;
						this.errorMessage = "Error!";
						break;
					}
					 
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
					
					String opcode = this.opc + " " + this.rs + " " + this.rt + " " + this.rd + " " + this.func1 + " " + this.func2;
					System.out.println(opcode);
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
					if (this.num1 == 0 && (this.inst.equals("DADDIU") || this.inst.equals("daddiu")))
					{
						this.error = true;
						this.errorMessage = "Invalid register.";
						break;
					}
					else
					{
						// if reg is from 10 to 31
						if (Character.getNumericValue(this.par1.charAt(2)) >= 0 && Character.getNumericValue(this.par1.charAt(2)) <= 9)
						{
							System.out.println(Character.getNumericValue(this.par1.charAt(1)));
							System.out.println(Character.getNumericValue(this.par1.charAt(2)));
							int temp = Character.getNumericValue(this.par1.charAt(2));
							this.num1 = (this.num1 * 10) + temp;
							System.out.println(this.num1);
							
							if (this.num1 > 31)
							{
								this.error = true;
								this.errorMessage = "Invalid register.";
								break;
							}
						}
						
						// PARAMETER 1 in binary
						String reg1 = this.convertToBinary(this.num1);
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
					}
					
					// SECOND PARAMETER = REG
					this.par2 = word.next().toString();
					this.num2 = Character.getNumericValue(this.par2.charAt(1));
					// if reg is from 10 to 31
					if (Character.getNumericValue(this.par2.charAt(2)) >= 0 && Character.getNumericValue(this.par2.charAt(2)) <= 9)
					{
						System.out.println(Character.getNumericValue(this.par2.charAt(1)));
						System.out.println(Character.getNumericValue(this.par2.charAt(2)));
						int temp = Character.getNumericValue(this.par2.charAt(2));
						this.num2 = (this.num2 * 10) + temp;
						System.out.println(this.num2);
						
						if (this.num2 > 31)
						{
							this.error = true;
							this.errorMessage = "Invalid register.";
							break;
						}
					}
					
					// PARAMETER 2 in binary
					String reg2 = this.convertToBinary(this.num2);
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
						
						this.opc = "011001";
						this.imm = par3;
						
					}
					else
					{
						// OFFSET FOR BEQC
						
						this.opc = "001000";
					}
					
					//opcode = opc + " " + rs + " " + rt + " " + imm;
					/*this.opcodeHex = this.convertToHex(this.opc + this.rs + this.rt + this.par3);
					this.opcodeHex = this.extend(this.opcodeHex);*/
					
					this.opcodeHex = this.convertToHex(this.opc + this.rs + this.rt) + this.imm;
					
					break;
					
					
				case "LD":
				case "ld":
				case "SD":
				case "sd":
					// FIRST PARAMETER = REG
					this.par1 = word.next().toString();
					this.num1 = Character.getNumericValue(this.par1.charAt(1));
					if (this.num1 == 0 && (this.inst.equals("LD") || this.inst.equals("ld")))
					{
						this.error = true;
						this.errorMessage = "Invalid register.";
						break;
					}
					else
					{
						// if reg is from 10 to 31
						if (Character.getNumericValue(this.par1.charAt(2)) >= 0 && Character.getNumericValue(this.par1.charAt(2)) <= 9)
						{
							System.out.println(Character.getNumericValue(this.par1.charAt(1)));
							System.out.println(Character.getNumericValue(this.par1.charAt(2)));
							int temp = Character.getNumericValue(this.par1.charAt(2));
							this.num1 = (this.num1 * 10) + temp;
							System.out.println(this.num1);
							
							if (this.num1 > 31)
							{
								this.error = true;
								this.errorMessage = "Invalid register.";
								break;
							}
						}
						
						// PARAMETER 1 in binary
						String reg1 = this.convertToBinary(this.num1);
						this.rt = this.concatZero(reg1, this.num1);
					}
					
					// SECOND PARAMETER = IMMEDIATE/MEMORY (offset)
					this.par2 = word.next().toString();
					this.imm = par2.substring(0, 4);
					System.out.println(imm);
					
					// THIRD PARAMETER = REG (base)
					this.par3 = par2.substring(6, 7);
					System.out.println(par3);
					this.num2 = Character.getNumericValue(this.par3.charAt(0));
					if (Character.getNumericValue(this.par2.charAt(7)) >= 0 && Character.getNumericValue(this.par2.charAt(7)) <= 9)
					{
						System.out.println(Character.getNumericValue(this.par2.charAt(6)));
						System.out.println(Character.getNumericValue(this.par2.charAt(7)));
						int temp = Character.getNumericValue(this.par2.charAt(7));
						this.num2 = (this.num2 * 10) + temp;
						System.out.println(this.num2);
						
						if (this.num2 > 31)
						{
							this.error = true;
							this.errorMessage = "Invalid register.";
							break;
						}
					}
					reg2 = this.convertToBinary(this.num2);
					this.rs = this.concatZero(reg2, num2);
					
					
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
					this.opcodeHex = this.convertToHex(opc + rs + rt) + imm;
					System.out.println(opcodeHex);
					
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
					System.out.println("ERROR");
					this.error = true;
					this.errorMessage = "Error!";
					
			}
		return this.opcodeHex;
	}
	
	public String firstWord()
	{
		/*this.input = this.sc.nextLine();*/
		this.word = new Scanner(this.input).useDelimiter(" ");
		//this.inst = this.word.next().toString();
		
		String[] codeLine = this.input.split(" ");
		//System.out.println(codeLine[0]);
		if (codeLine[0].charAt(codeLine[0].length()-1) == ':')
		{
			this.label = codeLine[0].substring(0, codeLine[0].length()-1);
			System.out.println(this.label);
			this.inst = this.word.next().toString();
			this.inst = this.word.next().toString();
			System.out.println(this.inst);
			this.output = this.generateOpcode(this.inst);
			System.out.println(this.output);
		}
		else 
		{
			this.inst = this.word.next().toString();
			this.output = this.generateOpcode(this.inst);
			System.out.println(this.output);
		}
		return this.output;
	}
	
}
