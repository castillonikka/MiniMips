package minimips;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JEditorPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import java.awt.Font;
import java.awt.Toolkit;

public class main extends JFrame {

	// NOTE: Attributes
	private JPanel contentPane;
	private JTable table;
	private JTable table_1;
	private int[] pc;
	private String[] code;
	private String[] pcHex;
	private String[] opcode;
	private boolean error;
	private boolean memError;
	private JTable table_2;
	private ArrayList<Memory> data = new ArrayList<Memory>();
	private ArrayList<Register> registers = new ArrayList<Register>();
	private ArrayList<Cycle> cycles = new ArrayList<Cycle>(100);
	private ArrayList<Code> codes = new ArrayList<Code>();
	private boolean dependency;
	private String display;
	private boolean stall;
	private boolean jumpCheck;
	private int tempCol, tempA;
	private int jump;
	private int arvin;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					main frame = new main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	// NOTE: Initializes all registers to 0
	public void initializeRegisters()
	{
		for (int x = 0; x < 32; x++)
		{
			this.registers.add(new Register());
			this.registers.get(x).setValue("0000000000000000");
			this.registers.get(x).setNum(x);
		}
			//this.registers[x] = "0000000000000000";
	}
	
	// NOTE: Used for error checking; Yung maling in-initialize na register, binabalik sa 0
	public void initializeRegister(int index)
	{
		this.registers.get(index).setValue("0000000000000000");
		//this.registers[index] = "0000000000000000";
	}
	
	// NOTE: Converts to binary
	public String convertToBinary(int num)
	{
		String binary;
		
		return binary = Integer.toBinaryString(num); 
	}
	
	// NOTE: Converts to hex
	public String convertToHex(String num)
	{
		String hex;
		
		return hex = (Integer.toHexString(Integer.parseInt(num, 2))).toUpperCase();
	}
	
	// NOTE: Initializes all memory addresses to 0
	public void initializeMemory()
	{
		System.out.println("ENTERED");
		int value = 8192;
		System.out.println(value);
		for (int a = 0; a < 8192; a++)
		{
			this.data.add(new Memory());
			this.data.get(a).setAddressHex(value);
			this.data.get(a).setValue("00");
			value++;
		}
	}
	
	// NOTE: Used for error checking; Yung maling in-initialize na memory address, binabalik sa 0
	public void initializeData(int index)
	{
		this.data.get(index).setValue("00");
	}
	
	
	public void ifPipeline(int codeIndex, int numCycles)
	{
		cycles.get(numCycles).setIF_ID_IR(codes.get(codeIndex).getOpcode());
		System.out.println("OPCODE!!! " + codes.get(codeIndex).getOpcode());
		
		// NOTE: temp is PC + 4
		int temp = Integer.parseInt(codes.get(codeIndex).getPcHex(), 16) + 4;
		
		cycles.get(numCycles).setIF_ID_NPC(Integer.toHexString(temp));
		
		cycles.get(numCycles).setPC(codes.get(codeIndex).getPcHex());
	}
	
	public void idPipeline(int codeIndex, int numCycles)
	{
		//System.out.println("HERSHEYS " + numCycles);
		
		// NOTE: Kinukuha laman ni RS (aka register A)
		System.out.println("CODE INDEX " + codeIndex);
		
		if (codes.get(codeIndex).getInst().toUpperCase().equals("BC"))
		{
			String opcodeBin = new BigInteger (codes.get(codeIndex).getOpcode(), 16).toString(2);
			String rsBin = opcodeBin.substring(6, 11);
			String rtBin = opcodeBin.substring(11, 16);
			cycles.get(numCycles).setID_EX_A(rsBin);
			cycles.get(numCycles).setID_EX_B(rtBin);
		}
		else
		{
			int temp2 = Integer.parseInt(codes.get(codeIndex).getRs(), 2);
			System.out.println(temp2);
			String laman = registers.get(temp2).getValue();
			System.out.println(numCycles);
			System.out.println(laman);
			cycles.get(numCycles).setID_EX_A(laman);
			System.out.println(cycles.get(numCycles).getID_EX_A());
			
			// NOTE: Kinukuha laman ni RT (aka register B)
			temp2 = Integer.parseInt(codes.get(codeIndex).getRt(), 2);
			System.out.println(temp2);
			laman = registers.get(temp2).getValue();
			cycles.get(numCycles).setID_EX_B(laman);
			System.out.println(cycles.get(numCycles).getID_EX_B());
		}
			
		// NOTE: Kinukuha yung immediate value sa opcode
		String hexImm2 = codes.get(codeIndex).getOpcode().substring(4);
		
		// NOTE: Converts first digit of immediate to 4-bit binary
		char first = hexImm2.charAt(0);
		String first2 = Character.toString(first);
		System.out.println("IMM 1: " + first2);
		int firstDigit = Integer.parseInt(first2, 16);
		String firstBin = String.format("%4s", Integer.toBinaryString(firstDigit)).replace(' ', '0');
		System.out.println("BIN 1  " + firstBin);
		
		// NOTE: Converts second digit of immediate to 4-bit binary
		char second = hexImm2.charAt(1);
		String second2 = Character.toString(second);
		System.out.println("IMM 2: " + second2);
		int secondDigit = Integer.parseInt(second2, 16);
		String secondBin = String.format("%4s", Integer.toBinaryString(secondDigit)).replace(' ', '0');
		System.out.println("BIN 2  " + secondBin);
		
		// NOTE: Converts third digit of immediate to 4-bit binary
		char third = hexImm2.charAt(2);
		String third2 = Character.toString(third);
		System.out.println("IMM 3: " + third2);
		int thirdDigit = Integer.parseInt(third2, 16);
		String thirdBin = String.format("%4s", Integer.toBinaryString(thirdDigit)).replace(' ', '0');
		System.out.println("BIN 3  " + thirdBin);
		
		// NOTE: Converts fourth digit of immediate to 4-bit binary
		char fourth = hexImm2.charAt(3);
		String fourth2 = Character.toString(fourth);
		System.out.println("IMM 4: " + fourth2);
		int fourthDigit = Integer.parseInt(fourth2, 16);
		String fourthBin = String.format("%4s", Integer.toBinaryString(fourthDigit)).replace(' ', '0');
		System.out.println("BIN 4  " + fourthBin);
		
		// NOTE: Immediate in binary
		String binImm = firstBin + secondBin + thirdBin + fourthBin;
		
		// NOTE: Sign extend immediate
		while (binImm.length() < 64)
		{
			binImm = binImm.charAt(0) + binImm;
		}
		
		// NOTE: Converts binary immediate to hex
		BigInteger big = new BigInteger (binImm, 2);
		hexImm2 = big.toString(16);
		hexImm2 = String.format("%16s", hexImm2).replace(' ', '0');
		System.out.println(hexImm2);
		cycles.get(numCycles).setID_EX_IMM(hexImm2);
		System.out.println("Immediate is " + cycles.get(numCycles).getID_EX_IMM());
		
		// NOTE: Gets previous IR
		int prev = numCycles-1;
		while (cycles.get(prev).getIF_ID_IR() == null)
			prev--;
		cycles.get(numCycles).setID_EX_IR(cycles.get(prev).getIF_ID_IR());
		
		// NOTE: Gets previous NPC changed prev
		cycles.get(numCycles).setID_EX_NPC(cycles.get(prev).getIF_ID_NPC());
	}
	
	public void exPipeline(int codeIndex, int numCycles)
	{
		//System.out.println("EXECUTE THIS INSTRUCTIOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOON " + codes.get(codeIndex).getInst().toUpperCase());
		switch(codes.get(codeIndex).getInst().toUpperCase())
		{
			case "BC":
			case "BEQC":
				/*BigInteger tempImm1 = new BigInteger(cycles.get(numCycles-1).getID_EX_IMM(), 16);
				String tempImm2 = tempImm1.toString(2);
				int tempImm3 = Integer.parseInt(tempImm2, 2) << 2;
				
				System.out.println("hello " + tempImm3 + " and " + cycles.get(numCycles-1).getID_EX_NPC());
				
				BigInteger tempNPC1 = new BigInteger(cycles.get(numCycles-1).getID_EX_NPC(), 16);
				String tempNPC2 = tempNPC1.toString(2);
				int tempNPC3 = Integer.parseInt(tempNPC2, 2);*/
				
				//int sum = tempImm3 + tempNPC3;
				//String sumHex = Integer.toHexString(sum);
				
				int x = 0;
				for (x = 0; x < codes.size(); x++)
				{
					if (codes.get(x).getLabel() != null)
						if (codes.get(x).getLabel().equals(codes.get(codeIndex).getBranchLabel()))
							break;
				}
				int sum = x * 4;
				String sumHex = Integer.toHexString(sum);
				
				System.out.println("Address: " + sumHex);
				
				cycles.get(numCycles).setEX_MEM_ALU(sumHex);
				
				switch (codes.get(codeIndex).getInst().toUpperCase())
				{
					case "BC":
						cycles.get(numCycles).setEX_MEM_COND("1");
						break;
					case "BEQC":
						String prevA = cycles.get(numCycles-1).getID_EX_A();
						String prevB = cycles.get(numCycles-1).getID_EX_B();
						BigInteger pA = new BigInteger(prevA, 16);
						BigInteger pB = new BigInteger(prevB, 16);
						
						System.out.println("pA " + pA + " and pB " + pB);
						
						if (pA.equals(pB))
							cycles.get(numCycles).setEX_MEM_COND("1");
						else cycles.get(numCycles).setEX_MEM_COND("0");
						break;
				}
				
				
				break;
			case "DADDU":
				// NOTE: Converts ID/EX.A to int
				BigInteger regA = new BigInteger(cycles.get(numCycles-1).getID_EX_A(), 16);
				// NOTE: Converts ID/EX.B to int
				BigInteger regB = new BigInteger(cycles.get(numCycles-1).getID_EX_B(), 16);
				// NOTE: A + B
				BigInteger ans = regA.add(regB);
				System.out.println("Answer is " + ans.toString(16));
				cycles.get(numCycles).setEX_MEM_ALU(ans.toString(16));
				// NOTE: Zero extend if needed
				while (cycles.get(numCycles).getEX_MEM_ALU().length() < 16)
				{
					cycles.get(numCycles).setEX_MEM_ALU("0" + cycles.get(numCycles).getEX_MEM_ALU());
				}
				// NOTE: Truncate leading digits if ALU is more than 16 bits
				if (cycles.get(numCycles).getEX_MEM_ALU().length() > 16)
					cycles.get(numCycles).setEX_MEM_ALU(cycles.get(numCycles).getEX_MEM_ALU().substring(Math.max(cycles.get(numCycles).getEX_MEM_ALU().length() - 16, 0)));
				
				// NOTE: EX/MEM.Cond = 0
				cycles.get(numCycles).setEX_MEM_COND("0");
				break;
				
			case "DADDIU":
				// NOTE: Converts ID/EX.A to big integer
				BigInteger regA2 = new BigInteger(cycles.get(numCycles-1).getID_EX_A(), 16);
				System.out.println("reg A: " + regA2);
				// NOTE: Converts ID/EX.Imm to big integer
				BigInteger tempImm = new BigInteger(cycles.get(numCycles-1).getID_EX_IMM(), 16);
				System.out.println("tempImm: " + tempImm);
				// NOTE: Answer gets A + Imm
				BigInteger ans2 = regA2.add(tempImm);
				System.out.println("ans: "+ ans2);
				// NOTE: Converts answer to hex -> ALU gets answer
				cycles.get(numCycles).setEX_MEM_ALU(ans2.toString(16));
				// NOTE: Zero extend if needed
				while (cycles.get(numCycles).getEX_MEM_ALU().length() < 16)
				{
					cycles.get(numCycles).setEX_MEM_ALU("0" + cycles.get(numCycles).getEX_MEM_ALU());
				}
				// NOTE: Truncates leading digits if ALU is more than 16 bits
				if (cycles.get(numCycles).getEX_MEM_ALU().length() > 16)
					cycles.get(numCycles).setEX_MEM_ALU(cycles.get(numCycles).getEX_MEM_ALU().substring(Math.max(cycles.get(numCycles).getEX_MEM_ALU().length() - 16, 0)));
				
				// NOTE: Cond = 0
				cycles.get(numCycles).setEX_MEM_COND("0");
				break;
				
			case "SLT":
				//System.out.println("YAAAAAAAAAAAAAAAAAS SLT");
				// NOTE: Converts ID/EX.A to int
				regA = new BigInteger(cycles.get(numCycles-1).getID_EX_A(), 16);
				// NOTE: Converts ID/EX.B to in
				regB = new BigInteger(cycles.get(numCycles-1).getID_EX_B(), 16);
				// NOTE: Answer is 1 if A < B
				
				int regAA = new BigInteger(cycles.get(numCycles-1).getID_EX_A(), 16).intValue();
				int regBB = new BigInteger(cycles.get(numCycles-1).getID_EX_B(), 16).intValue();
				
				int answer = 0;
				/*if (regA.compareTo(regB) == -1)
					answer = 1;*/
				
				if (regAA < regBB)
					answer = 1;
				//System.out.println("REG A IS " + regAA + "\nREG B IS " + regBB + "\nANSWERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR " + answer);
				// NOTE: Converted ans to hex -> EX/MEM.ALU gets ans
				cycles.get(numCycles).setEX_MEM_ALU(Integer.toHexString(answer));
				// NOTE: Zero extend if needed
				while (cycles.get(numCycles).getEX_MEM_ALU().length() < 16)
				{
					cycles.get(numCycles).setEX_MEM_ALU("0" + cycles.get(numCycles).getEX_MEM_ALU());
				}
				// NOTE: Truncate leading digits if ALU is more than 16 bits
				if (cycles.get(numCycles).getEX_MEM_ALU().length() > 16)
					cycles.get(numCycles).setEX_MEM_ALU(cycles.get(numCycles).getEX_MEM_ALU().substring(Math.max(cycles.get(numCycles).getEX_MEM_ALU().length() - 16, 0)));
				
				//System.out.println("ALUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU " + cycles.get(numCycles).getEX_MEM_ALU());
				
				// NOTE: EX/MEM.Cond = 0
				cycles.get(numCycles).setEX_MEM_COND("0");
				break;
				
			case "SELEQZ":
				// NOTE: Converts ID/EX.A to int
				regA = new BigInteger(cycles.get(numCycles-1).getID_EX_A(), 16);
				// NOTE: Converts ID/EX.B to int
				regB = new BigInteger(cycles.get(numCycles-1).getID_EX_B(), 16);
				// NOTE: Converted the register number to int
				int num = Integer.parseInt(codes.get(codeIndex).getRs(), 2);
				// NOTE: If ID/EX.B == 0, answer gets RS
				BigInteger zero = new BigInteger("0");
				ans = new BigInteger("0");
				if (regB.compareTo(zero) == 0)
					ans = new BigInteger(registers.get(num).getValue(), 16);
				// NOTE: Converts answer to hex -> EX/MEM.ALU gets answer
				cycles.get(numCycles).setEX_MEM_ALU(ans.toString(16));
				// NOTE: Zero extend if needed
				while (cycles.get(numCycles).getEX_MEM_ALU().length() < 16)
				{
					cycles.get(numCycles).setEX_MEM_ALU("0" + cycles.get(numCycles).getEX_MEM_ALU());
				}
				// NOTE: Truncate leading digits if ALU is more than 16 bits
				if (cycles.get(numCycles).getEX_MEM_ALU().length() > 16)
					cycles.get(numCycles).setEX_MEM_ALU(cycles.get(numCycles).getEX_MEM_ALU().substring(Math.max(cycles.get(numCycles).getEX_MEM_ALU().length() - 16, 0)));
				
				// NOTE: EX.MEM/Cond = 0
				cycles.get(numCycles).setEX_MEM_COND("0");
				break;
			
			case "DMULU":
			case "DMUHU":
				// NOTE: Converts ID/EX.A to int
				regA = new BigInteger(cycles.get(numCycles-1).getID_EX_A(), 16);
				// NOTE: Converts ID/EX.B to int
				regB = new BigInteger(cycles.get(numCycles-1).getID_EX_B(), 16);
				// NOTE: answer gets A * B
				ans = regA.multiply(regB);
				// NOTE: Converts answer to hex
				String ansHex = ans.toString(16);
				System.out.println("Ans in hex: " + ansHex + " -- " + ansHex.length());
				// NOTE: Truncate leading digits if answer is more than 16 bits OR zero extend if less than 16 bits
				if (ansHex.length() > 16)
					ansHex = ansHex.substring(Math.max(ansHex.length() - 16, 0));
				else ansHex = String.format("%16s", ansHex).replace(' ', '0');
				if (codes.get(codeIndex).getInst().toUpperCase().equals("DMULU"))
				{
					// NOTE: EX/MEM.ALU gets ansHex (LO)
					cycles.get(numCycles).setEX_MEM_ALU(ansHex);
					char oneDig = ansHex.charAt(0);
					String oneDig2 = Character.toString(oneDig);
					System.out.println("DIG 1: " + oneDig2);
					int firstDig = Integer.parseInt(oneDig2, 16);
					String oneBin = String.format("%4s", Integer.toBinaryString(firstDig)).replace(' ', '0');
					System.out.println("BIN 1  " + oneBin);
					char sign = '0';
					if (oneBin.charAt(0) == '1')
						sign = 'F';
					
					while (cycles.get(numCycles).getEX_MEM_ALU().length() < 16)
					{
						cycles.get(numCycles).setEX_MEM_ALU(Character.toString(sign) + cycles.get(numCycles).getEX_MEM_ALU());
					}
				}
				// NOTE: if DMUHU
				else
				{
					cycles.get(numCycles).setEX_MEM_ALU(ansHex);
					// NOTE: Zero extend if ALU is less than 32 digits
					while (cycles.get(numCycles).getEX_MEM_ALU().length() < 32)
					{
						cycles.get(numCycles).setEX_MEM_ALU(ansHex.charAt(0) + cycles.get(numCycles).getEX_MEM_ALU());
					}
					// NOTE: EX/MEM.ALU gets upper 16 bits of ansHex (HI)
					cycles.get(numCycles).setEX_MEM_ALU(cycles.get(numCycles).getEX_MEM_ALU().substring(0, 16));
				}
				
				// NOTE: EX/MEM.Cond = 0
				cycles.get(numCycles).setEX_MEM_COND("0");
				break;
				
			case "LD":
			case "SD":
				// NOTE: Converts ID/EX.A to big integer
				regA2 = new BigInteger(cycles.get(numCycles-1).getID_EX_A(), 16);
				System.out.println("reg A: " + regA2);
				// NOTE: Converts ID/EX.Imm to big integer
				tempImm = new BigInteger(cycles.get(numCycles-1).getID_EX_IMM(), 16);
				System.out.println("tempImm: " + tempImm);
				// NOTE: answer gets A + Imm 
				BigInteger ans2A = regA2.add(tempImm);
				System.out.println("ans: "+ ans2A);
				// NOTE: Converts answer to hex -> EX/MEM.ALU gets answer
				cycles.get(numCycles).setEX_MEM_ALU(ans2A.toString(16));
				System.out.println(tempImm.toString(16));
				// NOTE: Zero extend if needed
				cycles.get(numCycles).setEX_MEM_ALU(String.format("%16s", cycles.get(numCycles).getEX_MEM_ALU()).replace(' ', '0'));
				System.out.println(cycles.get(numCycles).getEX_MEM_ALU());
				
				// NOTE: EX/MEM.B = ID/EX.B
				cycles.get(numCycles).setEX_MEM_B(cycles.get(numCycles-1).getID_EX_B());
				
				// NOTE: EX/MEM.Cond = 0
				cycles.get(numCycles).setEX_MEM_COND("0");
				break;
		}
		
		// NOTE: EX/MEM.IR = ID/EX.IR
		int prev = numCycles-1;
		while (cycles.get(prev).getID_EX_IR() == null)
			prev--;
		cycles.get(numCycles).setEX_MEM_IR(cycles.get(prev).getID_EX_IR());
	}
	
	public void memPipeline(int codeIndex, int numCycles)
	{
		switch(codes.get(codeIndex).getInst().toUpperCase())
		{
			case "DADDU":
			case "DADDIU":
			case "SLT":
			case "SELEQZ":
			case "DMULU":
			case "DMUHU":
				// NOTE: MEM/WB.ALU = EX/MEM.ALU
				cycles.get(numCycles).setMEM_ALUoutput(cycles.get(numCycles-1).getEX_MEM_ALU());
				break;
				
			case "LD":
				// NOTE: Gets the memory address
				String address = cycles.get(numCycles-1).getEX_MEM_ALU().substring(cycles.get(numCycles-1).getEX_MEM_ALU().length()-4);
				System.out.println("ADDRESS FOR LD AT " + address);
				int a = 0;
				// NOTE: Looks for the index of memory address from ArrayList data
				for (a = 0; a < data.size(); a++)
				{
					if (data.get(a).getAddressHex().equals(address))
						break;
				}
				System.out.println("Value: " + data.get(a).getValue());
				String value8 = data.get(a+7).getValue();
				String value7 = data.get(a+6).getValue();
				String value6 = data.get(a+5).getValue();
				String value5 = data.get(a+4).getValue();
				String value4 = data.get(a+3).getValue();
				String value3 = data.get(a+2).getValue();
				String value2 = data.get(a+1).getValue();
				String value1 = data.get(a).getValue();
				// NOTE: MEM/WB.LMD gets value of memory address
				cycles.get(numCycles).setMEM_WB_LMD(value8 + value7 + value6 + value5 + value4 + value3 + value2 + value1);
				break;
				
			case "SD":
				// NOTE: Gets the memory address
				address = cycles.get(numCycles-1).getEX_MEM_ALU().substring(12);
				System.out.println("ADDRESS " + address);
				System.out.println("INDEX OF " + data.indexOf(address));
				// NOTE: Looks for the index of memory address from ArrayList data
				for (a = 0; a < data.size(); a++)
				{
					if (data.get(a).getAddressHex().equals(address))
						break;
				}
				
				String pair1 = cycles.get(numCycles-1).getEX_MEM_B().substring(0, 2);
				String pair2 = cycles.get(numCycles-1).getEX_MEM_B().substring(2, 4);
				String pair3 = cycles.get(numCycles-1).getEX_MEM_B().substring(4, 6);
				String pair4 = cycles.get(numCycles-1).getEX_MEM_B().substring(6, 8);
				String pair5 = cycles.get(numCycles-1).getEX_MEM_B().substring(8, 10);
				String pair6 = cycles.get(numCycles-1).getEX_MEM_B().substring(10, 12);
				String pair7 = cycles.get(numCycles-1).getEX_MEM_B().substring(12, 14);
				String pair8 = cycles.get(numCycles-1).getEX_MEM_B().substring(14, 16);
				
				// NOTE: Sets value of memory address to EX/MEM.B
				data.get(a).setValue(pair8);
				data.get(a+1).setValue(pair7);
				data.get(a+2).setValue(pair6);
				data.get(a+3).setValue(pair5);
				data.get(a+4).setValue(pair4);
				data.get(a+5).setValue(pair3);
				data.get(a+6).setValue(pair2);
				data.get(a+7).setValue(pair1);
				// NOTE: Updates memory table
				table_1.getModel().setValueAt(data.get(a).getValue(), a, 1);
				table_1.getModel().setValueAt(data.get(a+1).getValue(), a+1, 1);
				table_1.getModel().setValueAt(data.get(a+2).getValue(), a+2, 1);
				table_1.getModel().setValueAt(data.get(a+3).getValue(), a+3, 1);
				table_1.getModel().setValueAt(data.get(a+4).getValue(), a+4, 1);
				table_1.getModel().setValueAt(data.get(a+5).getValue(), a+5, 1);
				table_1.getModel().setValueAt(data.get(a+6).getValue(), a+6, 1);
				table_1.getModel().setValueAt(data.get(a+7).getValue(), a+7, 1);
				// NOTE: Assigns display message
				cycles.get(numCycles).setWbDisplay("Memory " + address + " = " + cycles.get(numCycles).getEX_MEM_B());
				break;
		}
		
		// NOTE: MEM/WB.IR = EX/MEM.IR
		int prev = numCycles-1;
		while (cycles.get(prev).getEX_MEM_IR() == null)
			prev--;
		cycles.get(numCycles).setMEM_WB_IR(cycles.get(prev).getEX_MEM_IR());
	}
	
	public void wbPipeline(int codeIndex, int numCycles)
	{
		switch(codes.get(codeIndex).getInst().toUpperCase())
		{
			case "DADDU":
			case "SLT":
			case "SELEQZ":
			case "DMULU":
			case "DMUHU":
				// NOTE: Gets the register number of the dest 
				int reg2 = Integer.parseInt(codes.get(codeIndex).getRd(), 2);
				// NOTE: Dest register gets ALU
				registers.get(reg2).setValue(cycles.get(numCycles-1).getMEM_ALUoutput());
				System.out.println("WB TO REG " + reg2);
				
				// NOTE: Updates registers table
				table.getModel().setValueAt(registers.get(reg2).getValue(), reg2, 1);
				// NOTE: Assigns display message for WB
				cycles.get(numCycles).setWbDisplay("R" + reg2 + " = " + registers.get(reg2).getValue());
				break;
			case "DADDIU":
				// NOTE: Gets the register number of the dest
				reg2 = Integer.parseInt(codes.get(codeIndex).getRt(), 2);
				// NOTE: Dest register gets ALU
				registers.get(reg2).setValue(cycles.get(numCycles-1).getMEM_ALUoutput());
				System.out.println("WB TO REG " + reg2);
				
				// NOTE: Updates registers table
				table.getModel().setValueAt(registers.get(reg2).getValue(), reg2, 1);
				// NOTE: Assigns display message for WB
				cycles.get(numCycles).setWbDisplay("R" + reg2 + " = " + registers.get(reg2).getValue());
				break;
			case "LD":
				// NOTE: Gets the register number of the dest
				reg2 = Integer.parseInt(codes.get(codeIndex).getRt(), 2);
				// NOTE: Dest register gets ALU
				registers.get(reg2).setValue(cycles.get(numCycles-1).getMEM_WB_LMD());
				System.out.println("WB TO REG " + reg2);
				
				// NOTE: Updates registers table
				table.getModel().setValueAt(registers.get(reg2).getValue(), reg2, 1);
				// NOTE: Assigns display message for WB
				cycles.get(numCycles).setWbDisplay("R" + reg2 + " = " + registers.get(reg2).getValue());
				break;
		}
	}

	public void pipeline(ArrayList<Code> codes)
	{
		int numCycles = 0;
		//System.out.println("NUM CYCLES!!!!!!!! INITIAL " + numCycles);
		cycles.clear(); 								// NOTE: Inaalis lahat ng laman ng "cycles", parang reset
		for (int x = 0; x < 100; x++)					// NOTE: Inaalis lahat ng laman ng pipeline map table, parang reset
		{
			for (int y = 0; y < 100; y++)
				table_2.setValueAt("", x, y);
		}
		System.out.println("Size is: " + codes.size()); // NOTE: Pinapakita kung ilang cycles na
		
		
		for (int a = 0; a < codes.size(); a++)
		{
			dependency = false;
			stall = false;
			if (a == 0)
			{
				// NOTE: For IF
				table_2.setValueAt("IF", a, a+0);
				cycles.add(new Cycle());
				ifPipeline(a, numCycles);
						
				///////////////////////////////		
				// NOTE: For ID
				table_2.setValueAt("ID", a, a+1);
				cycles.add(new Cycle());
				numCycles++;
				//System.out.println("NUM CYCLES!!!!!!!! ID " + numCycles);
				idPipeline(a, numCycles);
				
				///////////////////////////////
				// NOTE: for EX
				table_2.setValueAt("EX", a, a+2);
				cycles.add(new Cycle());
				numCycles++;
				exPipeline(a, numCycles);
				
				///////////////////////////////
				//NOTE: for MEM
				table_2.setValueAt("MEM", a, a+3);
				cycles.add(new Cycle());
				numCycles++;
				memPipeline(a, numCycles);
				
				///////////////////////////////
				// NOTE: for WB
				table_2.setValueAt("WB", a, a+4);
				cycles.add(new Cycle());
				numCycles++;
				wbPipeline(a, numCycles);
				
				//System.out.println("PRINTED FIRST INST");
			}
			else if (jumpCheck == true)
			{
		
				System.out.println("PREV A: " + a);
				System.out.println("JUMP: " + jump);
				a = jump;
				int col = 0;
				int temp = 0;
				
				for (temp = 0; temp < codes.size(); temp++)
				{
					if (codes.get(temp).getBranchLabel() != null)
						if (codes.get(temp).getBranchLabel().equals(codes.get(a).getLabel()))
							break;
						
				}
				
				
				System.out.println("A IS NOW: " + a);
				System.out.println("TEMP IS: " + temp);
				System.out.println("COL IS: " + col);
				System.out.println("LABEL IS: " + codes.get(a).getLabel());
				
				while (table_2.getValueAt(temp, col).toString().equals("WB") == false)
					col++;
				table_2.setValueAt("IF", a, col);
				cycles.add(new Cycle());
				ifPipeline(a, col);
				arvin = col;
			}
			else if (a != 0 && table_2.getValueAt(a-1, a).toString().equals("*") == false)
			{
				tempA = a;
				while (table_2.getValueAt(a-1, tempA).toString().equals("IF") || table_2.getValueAt(a-1, tempA).toString().equals(""))
				{
					tempA++;
					
				}
				System.out.println("TempA is " + tempA);
				table_2.setValueAt("IF", a, tempA);
				ifPipeline(a, tempA);
				stall = false;
				System.out.println("No stalls before me!");
			}
			else if (a != 0 && table_2.getValueAt(a-1, a+0).toString().equals("*") == true)
			{
				System.out.println("There is a stall before me!");
				stall = true;
				tempCol = a+0;
				while (table_2.getValueAt(a-1, tempCol).toString().equals("*"))
				{
					table_2.setValueAt("*", a, tempCol);
					tempCol++;
				}
				//System.out.println("Printed stalls!");
				while (table_2.getValueAt(a-1, tempCol).toString().equals("IF") || table_2.getValueAt(a-1, tempCol).toString().equals("") || table_2.getValueAt(a-1, tempCol).toString().equals("*"))
				{
					tempCol++;
				
				}
				table_2.setValueAt("IF", a, tempCol);
				ifPipeline(a, tempCol);
				System.out.println("Fetch! TempCol is " + tempCol);
			}
			//System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO " + codes.get(a).getInst().toUpperCase());
			int column = 0;
			switch(codes.get(a).getInst().toUpperCase())
			{
				case "BC":
					if (stall == false)
						column = tempA;
					else
						column = tempCol;
					//jumpCheck = true;
					
					table_2.setValueAt("ID", a, column+1);
					cycles.add(new Cycle());
					idPipeline(a, column+1);
					table_2.setValueAt("EX", a, column+2);
					cycles.add(new Cycle());
					exPipeline(a, column+2);
					table_2.setValueAt("MEM", a, column+3);
					cycles.add(new Cycle());
					memPipeline(a, column+3);
					table_2.setValueAt("WB", a, column+4);
					cycles.add(new Cycle());
					wbPipeline(a, column+4);
					break;
				case "DADDU":
				case "SLT":
				case "SELEQZ":
				case "DMULU":
				case "DMUHU":
					if (a != 0 && (codes.get(a-1).getInst().toUpperCase().equals("BC") || codes.get(a-1).getInst().toUpperCase().equals("BEQC")))
					{
						int col = 0;
						while (table_2.getValueAt(a-1, col).toString().equals("ID") == false)
							col++;
						col++;
						table_2.setValueAt("*", a, col);
						cycles.add(new Cycle());
						col++;
						table_2.setValueAt("*", a, col);
						cycles.add(new Cycle());
						
						int condCol = 0;
						while (table_2.getValueAt(a-1, condCol).toString().equals("EX") == false)
							condCol++;
						
						if (cycles.get(condCol).getEX_MEM_COND().equals("0"))
						{
							table_2.setValueAt("ID", a, col+1);
							cycles.add(new Cycle());
							table_2.setValueAt("EX", a, col+2);
							cycles.add(new Cycle());
							table_2.setValueAt("MEM", a, col+3);
							cycles.add(new Cycle());
							table_2.setValueAt("WB", a, col+4);
							cycles.add(new Cycle());
							
							idPipeline(a, col+1);
							exPipeline(a, col+2);
							memPipeline(a, col+3);
							wbPipeline(a, col+4);
							
							jump = 0;
							jumpCheck = false;
						}
						else
						{
							jumpCheck = true;
							String opcodeBin = new BigInteger(codes.get(a-1).getOpcode(), 16).toString(2);
							int offset = 0;
							switch(codes.get(a-1).getInst().toUpperCase())
							{
								case "BC":
									String offsetBin = opcodeBin.substring(6);
									offset = Integer.parseInt(offsetBin, 2);
									break;
								case "BEQC":
									offsetBin = opcodeBin.substring(16);
									offset = Integer.parseInt(offsetBin, 2);
									break;
							}
							
							
							int i = 0;
							for (i = 0; i < codes.size(); i++)
							{
								if (codes.get(a-1).getBranchLabel() != null)
									if (codes.get(a-1).getBranchLabel().equals(codes.get(i).getLabel()))
										break;
							}
							jump = i;
							if (offset == 0)
							{
								table_2.setValueAt("ID", a, col+1);
								cycles.add(new Cycle());
								table_2.setValueAt("EX", a, col+2);
								cycles.add(new Cycle());
								table_2.setValueAt("MEM", a, col+3);
								cycles.add(new Cycle());
								table_2.setValueAt("WB", a, col+4);
								cycles.add(new Cycle());
								
								idPipeline(a, col+1);
								exPipeline(a, col+2);
								memPipeline(a, col+3);
								wbPipeline(a, col+4);
							}
						}
						
						column = col;
					}
					else
					{
						if (jumpCheck == true)
						{
							jumpCheck = false;
							
							System.out.println("ARVIN :) " + arvin);
							
							table_2.setValueAt("ID", a, arvin+1);
							cycles.add(new Cycle());
							table_2.setValueAt("EX", a, arvin+2);
							cycles.add(new Cycle());
							table_2.setValueAt("MEM", a, arvin+3);
							cycles.add(new Cycle());
							table_2.setValueAt("WB", a, arvin+4);
							cycles.add(new Cycle());
							
							column = arvin;
							
							idPipeline(a, column+1);
							exPipeline(a, column+2);
							memPipeline(a, column+3);
							wbPipeline(a, column+4);
						}
						else
						{
							jump = 0;
							for (int b = 0; b < a; b++)
							{
								//System.out.println("Entered loop!");
								System.out.println("Dependency is " + dependency);
								System.out.println("Stall is " + stall);
								System.out.println("Current RS is " + codes.get(a).getRs());
								if 
								(
									(
										(codes.get(a).getRs().equals(codes.get(b).getRd()) 
										|| codes.get(a).getRt().equals(codes.get(b).getRd())) 
										&& 
										(codes.get(b).getInst().toUpperCase().equals("DADDU") 
										|| codes.get(b).getInst().toUpperCase().equals("SLT")
										|| codes.get(b).getInst().toUpperCase().equals("SELEQZ") 
										|| codes.get(b).getInst().toUpperCase().equals("DMULU") 
										|| codes.get(b).getInst().toUpperCase().equals ("DMUHU"))
									)
									||
									( 
										(codes.get(a).getRs().equals(codes.get(b).getRt()))
										&& 
										(codes.get(b).getInst().toUpperCase().equals("DADDIU") 
										|| codes.get(b).getInst().toUpperCase().equals("LD"))
									)
									||
									(
										(codes.get(a).getRt().equals(codes.get(b).getRd()) 
										|| codes.get(a).getRt().equals(codes.get(b).getRd())) 
										&& 
										(codes.get(b).getInst().toUpperCase().equals("DADDU") 
										|| codes.get(b).getInst().toUpperCase().equals("SLT")
										|| codes.get(b).getInst().toUpperCase().equals("SELEQZ") 
										|| codes.get(b).getInst().toUpperCase().equals("DMULU") 
										|| codes.get(b).getInst().toUpperCase().equals ("DMUHU"))
									)
									||
									( 
										(codes.get(a).getRt().equals(codes.get(b).getRt()))
										&& 
										(codes.get(b).getInst().toUpperCase().equals("DADDIU") 
										|| codes.get(b).getInst().toUpperCase().equals("LD"))
									)
								)
								{
									System.out.println("Dependent on R" + codes.get(b).getRt());
									System.out.println("I am dependent! And I am instruction #" + a + " depending on instruction #" + b);
									dependency = true;
									for (int c = 0; c < 100; c++)
									{
										int write, start = a;
										int numStalls = 0;
										int d = 0;
										
										if (table_2.getValueAt(b, c).toString().equals("WB"))
										{
											for (int x = 0; x < 100; x++)
											{
												if (table_2.getValueAt(a, x).toString().equals("IF"))
													start = x;
											}
											write = c;
											System.out.println("WB of primary inst is at " + write);
											System.out.println("IF of dependent inst is at " + start);
											if (dependency == true)
											{
												numStalls = write - start;
												d = numStalls;
												System.out.println("Number of stalls should be " + d);
											
												if (numStalls > 0)
												{
													while (numStalls > 0)
													{
														table_2.setValueAt("*", a, (c - numStalls + 1));
														numStalls--;
													}
													table_2.setValueAt("ID", a, write+1);
													cycles.add(new Cycle());
													
													column = write;
													
													table_2.setValueAt("EX", a, write+2);
													cycles.add(new Cycle());
												
													
													table_2.setValueAt("MEM", a, write+3);
													cycles.add(new Cycle());
												
													
													table_2.setValueAt("WB", a, write+4);
													cycles.add(new Cycle());
												
												}
												else
												{
													int lookupID = 0;
													for (int e = 0; e < 100; e++)
													{
														if (table_2.getValueAt(a-1, e).toString().equals("ID"))
															lookupID = e;
													}
													table_2.setValueAt("IF", a, lookupID);
													cycles.add(new Cycle());
													ifPipeline(a, lookupID);
													column = lookupID;
													
													table_2.setValueAt("ID", a, lookupID+1);
													cycles.add(new Cycle());
												
													
													table_2.setValueAt("EX", a, lookupID+2);
													cycles.add(new Cycle());
													
													
													table_2.setValueAt("MEM", a, lookupID+3);
													cycles.add(new Cycle());
												
													
													table_2.setValueAt("WB", a, lookupID+4);
													cycles.add(new Cycle());
													
												}
												
											}
											else if (stall == true && tempCol > write)
											{
												System.out.println("TempCol is " + tempCol);
												table_2.setValueAt("ID", a, tempCol+1);
												cycles.add(new Cycle());
												column = tempCol;
												
												
												table_2.setValueAt("EX", a, tempCol+2);
												cycles.add(new Cycle());
												
												
												table_2.setValueAt("MEM", a, tempCol+3);
												cycles.add(new Cycle());
												
												
												table_2.setValueAt("WB", a, tempCol+4);
												cycles.add(new Cycle());
												
											}
										}
									}
									//System.out.println("Printed everything else!");
								}
								else if (dependency == false && stall == false)
								{
									System.out.println("I am not dependent!");
									table_2.setValueAt("ID", a, tempA+1);
									cycles.add(new Cycle());
									
									column = tempA;
									
									table_2.setValueAt("EX", a, tempA+2);
									cycles.add(new Cycle());
								
									
									table_2.setValueAt("MEM", a, tempA+3);
									cycles.add(new Cycle());
									
									
									table_2.setValueAt("WB", a, tempA+4);
									cycles.add(new Cycle());
									
								}
								else if (dependency == false && stall == true)
								{
									System.out.println("I am not dependent! B");
									table_2.setValueAt("ID", a, tempCol+1);
									cycles.add(new Cycle());
									
									column = tempCol;
									
									table_2.setValueAt("EX", a, tempCol+2);
									cycles.add(new Cycle());
									
									
									table_2.setValueAt("MEM", a, tempCol+3);
									cycles.add(new Cycle());
									
									
									table_2.setValueAt("WB", a, tempCol+4);
									cycles.add(new Cycle());
									
								}
							}
							idPipeline(a, column+1);
							exPipeline(a, column+2);
							memPipeline(a, column+3);
							wbPipeline(a, column+4);
						}
					}
					
					
					break;
				case "LD":
				case "DADDIU":
					for (int b = 0; b < a; b++)
					{
						//System.out.println("Entered loop!");
						System.out.println("Dependency is " + dependency);
						System.out.println("Stall is " + stall);
						if
						(
							(
								codes.get(a).getRs().equals(codes.get(b).getRt()) 
								&& 
								(codes.get(b).getInst().toUpperCase().equals("DADDIU") 
								|| codes.get(b).getInst().toUpperCase().equals("LD"))
							)
							||
							( 
								(codes.get(a).getRs().equals(codes.get(b).getRd()))
								&&  
								(codes.get(b).getInst().toUpperCase().equals("DADDU") 
								|| codes.get(b).getInst().toUpperCase().equals("SLT")
								|| codes.get(b).getInst().toUpperCase().equals("SELEQZ") 
								|| codes.get(b).getInst().toUpperCase().equals("DMULU") 
								|| codes.get(b).getInst().toUpperCase().equals ("DMUHU"))
							)
						)
						{
							System.out.println("I am dependent! And I am instruction #" + a);
							dependency = true;
							for (int c = 0; c < 100; c++)
							{
								int write, start = a;
								int numStalls = 0;
								int d = 0;
								
								if (table_2.getValueAt(b, c).toString().equals("WB"))
								{
									for (int x = 0; x < 100; x++)
									{
										if (table_2.getValueAt(a, x).toString().equals("IF"))
											start = x;
									}
									write = c;
									System.out.println("WB of primary inst is at " + write);
									if (stall == false || (stall == true && dependency == true))
									{
										numStalls = write - start;
										d = numStalls;
										System.out.println("Number of stalls should be " + d);
										while (numStalls != 0)
										{
											table_2.setValueAt("*", a, (c - numStalls + 1));
											numStalls--;
										}
										table_2.setValueAt("ID", a, write+1);
										cycles.add(new Cycle());
									
										column = write;
										
										table_2.setValueAt("EX", a, write+2);
										cycles.add(new Cycle());
								
										
										table_2.setValueAt("MEM", a, write+3);
										cycles.add(new Cycle());
										
										
										table_2.setValueAt("WB", a, write+4);
										cycles.add(new Cycle());
										
									}
									else if (stall == true && tempCol > write)
									{
										System.out.println("TempCol is " + tempCol);
										table_2.setValueAt("ID", a, tempCol+1);
										cycles.add(new Cycle());
										
										column = tempCol;
										
										table_2.setValueAt("EX", a, tempCol+2);
										cycles.add(new Cycle());
										
										
										table_2.setValueAt("MEM", a, tempCol+3);
										cycles.add(new Cycle());
										
										
										table_2.setValueAt("WB", a, tempCol+4);
										cycles.add(new Cycle());
										
									}
								}
								
								//System.out.println("Printed everything else!");
							}
							//System.out.println("Printed everything else!");
						}
						else if (dependency == false && stall == false)
						{
							System.out.println("I am not dependent!");
							table_2.setValueAt("ID", a, tempA+1);
							cycles.add(new Cycle());
							column = tempA;
							
							
							table_2.setValueAt("EX", a, tempA+2);
							cycles.add(new Cycle());
							
							
							table_2.setValueAt("MEM", a, tempA+3);
							cycles.add(new Cycle());
							
							
							table_2.setValueAt("WB", a, tempA+4);
							cycles.add(new Cycle());
							
						}
						else if (dependency == false && stall == true)
						{
							System.out.println("I am not dependent! B");
							table_2.setValueAt("ID", a, tempCol+1);
							cycles.add(new Cycle());
							column = tempCol;
							
							
							table_2.setValueAt("EX", a, tempCol+2);
							cycles.add(new Cycle());
							
							
							table_2.setValueAt("MEM", a, tempCol+3);
							cycles.add(new Cycle());
							
							
							table_2.setValueAt("WB", a, tempCol+4);
							cycles.add(new Cycle());
							
						}
					}
					
					idPipeline(a, column+1);
					exPipeline(a, column+2);
					memPipeline(a, column+3);
					wbPipeline(a, column+4);
					
					break;
				case "SD":
				case "BEQC":
					for (int b = 0; b < a; b++)
					{
						//System.out.println("Entered loop!");
						System.out.println("Dependency is " + dependency);
						System.out.println("Stall is " + stall);
						if
						(
							(
								(codes.get(a).getRs().equals(codes.get(b).getRd()) 
								|| codes.get(a).getRt().equals(codes.get(b).getRd())) 
								&& 
								(codes.get(b).getInst().toUpperCase().equals("DADDU") 
								|| codes.get(b).getInst().toUpperCase().equals("SLT")
								|| codes.get(b).getInst().toUpperCase().equals("SELEQZ") 
								|| codes.get(b).getInst().toUpperCase().equals("DMULU") 
								|| codes.get(b).getInst().toUpperCase().equals ("DMUHU"))
							)
							||
							(
								(codes.get(a).getRs().equals(codes.get(b).getRt()) 
								|| codes.get(a).getRt().equals(codes.get(b).getRt())) 
								&& 
								(codes.get(b).getInst().toUpperCase().equals("DADDIU") 
								|| codes.get(b).getInst().toUpperCase().equals("LD"))
							)
							||
							(
								(codes.get(a).getRt().equals(codes.get(b).getRd()) 
								|| codes.get(a).getRt().equals(codes.get(b).getRd())) 
								&& 
								(codes.get(b).getInst().toUpperCase().equals("DADDU") 
								|| codes.get(b).getInst().toUpperCase().equals("SLT")
								|| codes.get(b).getInst().toUpperCase().equals("SELEQZ") 
								|| codes.get(b).getInst().toUpperCase().equals("DMULU") 
								|| codes.get(b).getInst().toUpperCase().equals ("DMUHU"))
							)
							||
							(
								(codes.get(a).getRt().equals(codes.get(b).getRt()) 
								|| codes.get(a).getRt().equals(codes.get(b).getRt())) 
								&& 
								(codes.get(b).getInst().toUpperCase().equals("DADDIU") 
								|| codes.get(b).getInst().toUpperCase().equals("LD"))
							)
						)
						{
							System.out.println("I am dependent! And I am instruction #" + a);
							dependency = true;
							for (int c = 0; c < 100; c++)
							{
								int write, start = a;
								int numStalls = 0;
								int d = 0;
								
								if (table_2.getValueAt(b, c).toString().equals("WB"))
								{
									for (int x = 0; x < 100; x++)
									{
										if (table_2.getValueAt(a, x).toString().equals("IF"))
											start = x;
									}
									write = c;
									System.out.println("WB of primary inst is at " + write);
									if (stall == false || (stall == true && dependency == true))
									{
										numStalls = write - start;
										d = numStalls;
										System.out.println("Number of stalls should be " + d);	
										while (numStalls != 0)
										{
											table_2.setValueAt("*", a, (c - numStalls + 1));
											numStalls--;
										}
										table_2.setValueAt("ID", a, write+1);
										cycles.add(new Cycle());
										column = write;
										
										table_2.setValueAt("EX", a, write+2);
										cycles.add(new Cycle());
										
										table_2.setValueAt("MEM", a, write+3);
										cycles.add(new Cycle());
										
										table_2.setValueAt("WB", a, write+4);
										cycles.add(new Cycle());
									}
									else if (stall == true && tempCol > write)
									{
										System.out.println("TempCol is " + tempCol);
										table_2.setValueAt("ID", a, write+1);
										cycles.add(new Cycle());
										column = write;
										
										
										table_2.setValueAt("EX", a, write+2);
										cycles.add(new Cycle());
										
										
										table_2.setValueAt("MEM", a, write+3);
										cycles.add(new Cycle());
									
										
										table_2.setValueAt("WB", a, write+4);
										cycles.add(new Cycle());
										
									}
								}
								
							}
							//System.out.println("Printed everything else!");
						}
						else if (dependency == false && stall == false)
						{
							System.out.println("I am not dependent!");
							table_2.setValueAt("ID", a, tempA+1);
							cycles.add(new Cycle());
							column = tempA;
							
							
							table_2.setValueAt("EX", a, tempA+2);
							cycles.add(new Cycle());
							
							
							table_2.setValueAt("MEM", a, tempA+3);
							cycles.add(new Cycle());
							
							
							table_2.setValueAt("WB", a, tempA+4);
							cycles.add(new Cycle());
							
						}
						else if (dependency == false && stall == true)
						{
							System.out.println("I am not dependent! B");
							table_2.setValueAt("ID", a, tempCol+1);
							cycles.add(new Cycle());
							column = tempCol;
							
							
							table_2.setValueAt("EX", a, tempCol+2);
							cycles.add(new Cycle());
						
							
							table_2.setValueAt("MEM", a, tempCol+3);
							cycles.add(new Cycle());
							
							
							table_2.setValueAt("WB", a, tempCol+4);
							cycles.add(new Cycle());
							
						}
					}
					
					idPipeline(a, column+1);
					exPipeline(a, column+2);
					memPipeline(a, column+3);
					wbPipeline(a, column+4);
					
					break;
			}
		}
		
		// NOTE: Pipeline Cycles
		
		int cols = 0;
		
		// NOTE: Gets the number of columns by searching for the WB of the last instruction
		for (cols = 0; cols < 100; cols++)
		{
			if (table_2.getValueAt(codes.size() - 1, cols).toString().equals("WB"))
				break;
		}
		cols++;
		
		// NOTE: Creates cycles
		for (int j = 0; j < cols; j++)
			cycles.add(new Cycle());
			
	}

	
	/**
	 * Create the frame.
	 */
	public main() {
		setResizable(false);
		
		setTitle("MiniMips");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 631, 551);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		contentPane.setLayout(new BorderLayout(0, 0));
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		JPanel panel = new JPanel();
		JLabel lblOpcode_1 = new JLabel("Opcode:");
		JLabel label = new JLabel("Code:");
		JLabel label_2 = new JLabel("Registers:");
		JLabel label_3 = new JLabel("Memory:");
		JScrollPane scrollPane = new JScrollPane();
		JScrollPane scrollPane_2 = new JScrollPane();
		JScrollPane scrollPane_3 = new JScrollPane();
		JTextArea txtrOpcode = new JTextArea();
		JTextArea txtrCode = new JTextArea();
		JTextArea txtrCode2 = new JTextArea();
		JButton btnEnter = new JButton("Enter Program");
		table = new JTable();
		table_1 = new JTable();
		table_1.setFillsViewportHeight(true);
		DefaultTableModel model = new DefaultTableModel(new Object[]{"Memory Address", "Data"}, 0)
		{
			@Override
 		   public boolean isCellEditable(int row, int column) {
				// NOTE: First column of memory table is non-editable
 		       if (column == 0)
 		    	   return false;
 		       else return true;
 		   }
		};
		JScrollPane scrollPane_1 = new JScrollPane();
		JPanel panel_1 = new JPanel();
		JLabel lblOpcode = new JLabel("Code:");
		JButton btnNewButton = new JButton("Single-Step");
		JButton btnFullExecution = new JButton("Full Execution");
		JLabel lblPipelineMap = new JLabel("Pipeline Map:");
		JLabel lblMipsRegisters = new JLabel("MIPS64 Registers:");
		JButton btnInitializeValues = new JButton("Initialize Values");
		JTextArea textArea = new JTextArea();
		
		table_1.setModel(model);
		
		scrollPane_1.setViewportView(txtrCode);
		scrollPane_2.setViewportView(table_1);
		scrollPane_3.setViewportView(txtrOpcode);
		
		this.initializeRegisters();
		this.initializeMemory();
		// NOTE: Adds ArrayList data to the table
		for (int b = 0; b < 8192; b++)
		{
			model.addRow(new Object[]{data.get(b).getAddressHex(), data.get(b).getValue()});
		}
		
		// NOTE: For initializing values to registers
		btnInitializeValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				error = false;
				registers.get(0).setValue("0000000000000000");
				table.getModel().setValueAt(registers.get(0).getValue(), 0, 1);
				for (int a = 1; a < 32; a++)
				{
					// NOTE: Checks if input is 16-bits long
					if (table.getModel().getValueAt(a, 1).toString().length() != 16)
					{
						error = true;
						System.out.println("ERROR A");
					}
					else
					{
						// NOTE: Checks if all characters from the input are hex
						for (int b = 0; b < 16; b++)
						{
							if (table.getModel().getValueAt(a, 1).toString().charAt(b) != '0' && table.getModel().getValueAt(a, 1).toString().charAt(b) != '1' && table.getModel().getValueAt(a, 1).toString().charAt(b) != '2' && table.getModel().getValueAt(a, 1).toString().charAt(b) != '3' && 
									table.getModel().getValueAt(a, 1).toString().charAt(b) != '4' && table.getModel().getValueAt(a, 1).toString().charAt(b) != '5' && 
									table.getModel().getValueAt(a, 1).toString().charAt(b) != '6' && table.getModel().getValueAt(a, 1).toString().charAt(b) != '7' && table.getModel().getValueAt(a, 1).toString().charAt(b) != '8' && table.getModel().getValueAt(a, 1).toString().charAt(b) != '9' && table.getModel().getValueAt(a, 1).toString().charAt(b) != 'A' && 
									table.getModel().getValueAt(a, 1).toString().charAt(b) != 'B' && table.getModel().getValueAt(a, 1).toString().charAt(b) != 'C' && table.getModel().getValueAt(a, 1).toString().charAt(b) != 'D' && table.getModel().getValueAt(a, 1).toString().charAt(b) != 'E' && table.getModel().getValueAt(a, 1).toString().charAt(b) != 'F')
							{
								error = true;
								System.out.println("ERROR B");
								break;
							}
						}
					}
					if (error)
					{
						initializeRegister(a);
						System.out.println("Initialized R" + a + " to " + registers.get(a).getValue());
						JOptionPane.showMessageDialog(new JFrame(), "Error!");
						table.getModel().setValueAt(registers.get(a).getValue(), a, 1);
						break;
					}
					else
					{
						// NOTE: Updates values and table if error = 0
						registers.get(a).setValue(table.getModel().getValueAt(a, 1).toString());
						table.getModel().setValueAt(registers.get(a).getValue(), a, 1);
						System.out.println("Initialization of register values was successful!");
					}
				}
				
				for (int c = 0; c < 32; c++)
				{
					System.out.println("R" + c + ": " + registers.get(c).getValue());
				}
			}
		});
		
		lblOpcode_1.setBounds(316, 43, 124, 14);
		label_2.setBounds(31, 243, 49, 14);
		label_3.setBounds(316, 243, 109, 14);
		scrollPane.setBounds(31, 263, 263, 146);
		scrollPane_2.setBounds(316, 263, 263, 146);
		scrollPane_3.setBounds(316, 68, 263, 120);
		txtrCode2.setBounds(30, 51, 246, 300);
		btnEnter.setBounds(93, 199, 137, 23);
		label.setBounds(31, 43, 49, 14);
		scrollPane_1.setBounds(31, 68, 263, 120);
		btnInitializeValues.setBounds(93, 420, 137, 23);
		lblOpcode.setBounds(30, 26, 63, 14);
		btnNewButton.setBounds(30, 362, 121, 23);
		btnFullExecution.setBounds(161, 362, 115, 23);
		lblPipelineMap.setBounds(305, 26, 115, 14);
		lblMipsRegisters.setBounds(305, 198, 131, 14);
		
		
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		panel.setToolTipText("");
		tabbedPane.addTab("Initialize", null, panel, null);
		
		txtrOpcode.setFont(new Font("Courier New", Font.PLAIN, 13));
		txtrCode.setFont(new Font("Courier New", Font.PLAIN, 13));
		
		txtrCode2.setEditable(false);
		
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try
				{
					// NOTE: Deletes all previous elements in ArrayList codes, parang reset
					codes.clear();
					// NOTE: Deletes all previous values in Pipeline Map
					for (int y = 0; y < 100; y++)
					{
						for (int z = 0; z < 100; z++)
							table_2.setValueAt("", y, z);
					}
					pc = new int[100];
					pcHex = new String[100];
					
					// NOTE: Bawat element ng Array na code, kinukuha per line sa text box
					txtrCode2.setText(txtrCode.getText());
					code = txtrCode.getText().split("\\n");
					txtrOpcode.setText("");
					
					for (int a = 0; a < code.length; a++)
					{
						// NOTE: Every instruction line is added to ArrayList codes
						codes.add(new Code());
						
						// NOTE: Assignment of PC to instruction line
						pc[a] = 4 * a;
						String tempBin = this.convertToBinary(pc[a]);
						// NOTE: PC in hex
						pcHex[a] = this.convertToHex(tempBin);
						
						System.out.println(code[a] + "       " + pcHex[a]);
						// NOTE: Gets instruction
						String instruction = code[a].split(" ")[0];
						System.out.println(instruction);
						
						// NOTE: Assigns PC
						codes.get(a).setPcHex(pcHex[a]);
						codes.get(a).setLine(code[a]);
						//codes.get(a).setInst(instruction);
						
						if (instruction.equals("BC") || instruction.equals("bc"))
						{
							// NOTE: Gets label of branch
							String label = code[a].split(" ")[1];
							System.out.println(label);
							codes.get(a).setBranchLabel(label);
							
							// NOTE: Assigns label and instruction to code
							codes.get(a).setInst(instruction);
							for (int b = 0; b < code.length; b++)
							{
								// NOTE: Hinahanap yung label sa lahat ng instructions
								String lbl = code[b].split(" ")[0];
								// NOTE: Inalis yung :
								lbl = lbl.substring(0, lbl.length()-1);
								System.out.println(lbl);
								if (lbl.equals(label))
								{
									// NOTE: Gets PC of label
									int target = 4 * b;
									int pcBranch = pc[a];
									System.out.println(target + " and " + pcBranch + " and " + code[a]);
									// NOTE: Opcode!
									Opcode opc = new Opcode (code[a], pcBranch, target);
									String outputOpc = opc.firstWord();
									// NOTE: Assigns opcode
									codes.get(a).setOpcode(outputOpc);
									
									System.out.println("Opcode is: " + outputOpc);
									if (opc.isError() == true)
									{
										txtrOpcode.setText(opc.getErrorMessage());
										JOptionPane.showMessageDialog(new JFrame(), opc.getErrorMessage());
									}
									else 
									{
										txtrOpcode.setText(txtrOpcode.getText() + outputOpc + "\n");
										codes.get(a).setImm(opc.getImm());
										codes.get(a).setRd(opc.getRd());
										codes.get(a).setRs(opc.getRs());
										codes.get(a).setRt(opc.getRt());
										codes.get(a).printAttributes();
									}
								}
							}
						}
						else if (instruction.equals("BEQC") || instruction.equals("beqc"))
						{
							// NOTE: Gets label of branch
							String label = code[a].split(" ")[3];
							System.out.println(label);
							codes.get(a).setBranchLabel(label);
							for (int b = 0; b < code.length; b++)
							{
								// NOTE: Hinahanap yung label sa lahat ng instructions
								String lbl = code[b].split(" ")[0];
								// NOTE: Inalis yung :
								lbl = lbl.substring(0, lbl.length()-1);
								System.out.println(lbl);
								if (lbl.equals(label))
								{
									codes.get(a).setInst(instruction);
									// NOTE: Gets PC of label
									int target = 4 * b;
									int pcBranch = pc[a];
									System.out.println(target + " and " + pcBranch + " and " + code[a]);
									// NOTE: Opcode!
									Opcode opc = new Opcode (code[a], pcBranch, target);
									String outputOpc = opc.firstWord();
									// NOTE: Assigns opcode
									codes.get(a).setOpcode(outputOpc);
									
									System.out.println("Opcode is: " + outputOpc);
									if (opc.isError() == true)
									{
										txtrOpcode.setText(opc.getErrorMessage());
										JOptionPane.showMessageDialog(new JFrame(), opc.getErrorMessage());
									}
									else 
									{
										txtrOpcode.setText(txtrOpcode.getText() + outputOpc + "\n");
										codes.get(a).setImm(opc.getImm());
										codes.get(a).setRd(opc.getRd());
										codes.get(a).setRs(opc.getRs());
										codes.get(a).setRt(opc.getRt());
										codes.get(a).printAttributes();
									}
								}
							}
						}
						else
						{
							// NOTE: Opcode!
							Opcode opc = new Opcode(code[a]);
							String outputOpc = opc.firstWord();
							
							// NOTE: Assigns instruction and opcode
							if (opc.getLabel() != null)
							{
								codes.get(a).setLabel(opc.getLabel());
								codes.get(a).setInst(opc.getInst());
							}
							else codes.get(a).setInst(opc.getInst());
							codes.get(a).setOpcode(outputOpc);
							
							System.out.println("Opcode is: " + outputOpc);
							if (opc.isError() == true)
							{
								txtrOpcode.setText(opc.getErrorMessage());
								JOptionPane.showMessageDialog(new JFrame(), opc.getErrorMessage());
							}
							else 
							{
								// NOTE: Assigns all the other attributes
								txtrOpcode.setText(txtrOpcode.getText() + outputOpc + "\n");
								codes.get(a).setImm(opc.getImm());
								codes.get(a).setRd(opc.getRd());
								codes.get(a).setRs(opc.getRs());
								codes.get(a).setRt(opc.getRt());
								codes.get(a).printAttributes();
							}
						}
					}
					
				} catch (StringIndexOutOfBoundsException e)
				{
					txtrOpcode.setText("Error!\n");
					JOptionPane.showMessageDialog(new JFrame(), "Error!");
				}
			}
			
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
		});
		
		btnFullExecution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				pipeline(codes);
				
				// NOTE: Para sa pag-display ng pipeline registers
				int a = 0;
				for (a = 0; a < 100; a++)
				{
					if (table_2.getValueAt(codes.size()-1, a).toString().equals("WB"))
						break;
				}
			
				display = "\nCYCLE " + (a+1) + "\nInstruction Fetch\nIF/ID.IR = " + cycles.get(a).getIF_ID_IR() + "\nIF/ID.NPC = " + cycles.get(a).getIF_ID_NPC() + "\nPC = " + cycles.get(a).getPC() + "\n\nInstruction Decode\nID/EX.A = " + cycles.get(a).getID_EX_A() + "\nID/EX.B = " + cycles.get(a).getID_EX_B() + "\nID/EX.IMM = " + cycles.get(a).getID_EX_IMM() + "\nID/EX.IR = " + cycles.get(a).getID_EX_IR() + "\nID/EX.NPC = " + cycles.get(a).getID_EX_NPC() + "\n\nExecution\nEX/MEM.ALUoutput = " + cycles.get(a).getEX_MEM_ALU() + "\nEX/MEM.IR = " + cycles.get(a).getEX_MEM_IR() + "\nEX/MEM.Cond = " + cycles.get(a).getEX_MEM_COND() + "\n\nMemory Access\nMEM/WB.LMD = " + cycles.get(a).getMEM_WB_LMD() + "\nMEM/WB.IR = " + cycles.get(a).getMEM_WB_IR() + "\n\nWrite-Back\n" + cycles.get(a).getWbDisplay() + "\n~~~~~~~~~~~~~";
				textArea.setCaretPosition(0);
				textArea.setText(display);
				
			}
		});
		
		// NOTE: Register table
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"R0 =", this.registers.get(0).getValue()},
				{"R1 =", this.registers.get(1).getValue()},
				{"R2 =", this.registers.get(2).getValue()},
				{"R3 =", this.registers.get(3).getValue()},
				{"R4 =", this.registers.get(4).getValue()},
				{"R5 =", this.registers.get(5).getValue()},
				{"R6 =", this.registers.get(6).getValue()},
				{"R7 =", this.registers.get(7).getValue()},
				{"R8 =", this.registers.get(8).getValue()},
				{"R9 =", this.registers.get(9).getValue()},
				{"R10 =", this.registers.get(10).getValue()},
				{"R11 =", this.registers.get(11).getValue()},
				{"R12 =", this.registers.get(12).getValue()},
				{"R13 =", this.registers.get(13).getValue()},
				{"R14 =", this.registers.get(14).getValue()},
				{"R15 =", this.registers.get(15).getValue()},
				{"R16 =", this.registers.get(16).getValue()},
				{"R17 =", this.registers.get(17).getValue()},
				{"R18 =", this.registers.get(18).getValue()},
				{"R19 =", this.registers.get(19).getValue()},
				{"R20 =", this.registers.get(20).getValue()},
				{"R21 =", this.registers.get(21).getValue()},
				{"R22 =", this.registers.get(22).getValue()},
				{"R23 =", this.registers.get(23).getValue()},
				{"R24 =", this.registers.get(24).getValue()},
				{"R25 =", this.registers.get(25).getValue()},
				{"R26 =", this.registers.get(26).getValue()},
				{"R27 =", this.registers.get(27).getValue()},
				{"R28 =", this.registers.get(28).getValue()},
				{"R29 =", this.registers.get(29).getValue()},
				{"R30 =", this.registers.get(30).getValue()},
				{"R31 =", this.registers.get(31).getValue()}
			},
			new String[] {
				"Registers", "Values"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				false, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table.setFillsViewportHeight(true);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		scrollPane.setViewportView(table);
		
		panel.setLayout(null);
		panel.add(btnEnter);
		panel.add(label_2);
		panel.add(scrollPane);
		panel.add(label_3);
		panel.add(lblOpcode_1);
		panel.add(scrollPane_3);
		panel.add(scrollPane_2);
		panel.add(label);
		panel.add(scrollPane_1);
		panel.add(btnInitializeValues);
		
		panel_1.setLayout(null);
		panel_1.add(btnNewButton);
		panel_1.add(btnFullExecution);
		panel_1.add(txtrCode2);
		panel_1.add(lblOpcode);
		panel_1.add(lblPipelineMap);
		panel_1.add(lblMipsRegisters);
		
		tabbedPane.addTab("Execute", null, panel_1, null);
		
		txtrOpcode.setEditable(false);
		
		JButton btnInitializeData = new JButton("Initialize Data");
		
		// NOTE: Initialization of memory data
		btnInitializeData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				memError = false;
				for (int a = 0; a < 8192; a++)
				{
					// NOTE: If memory data is not 16 bits long
					if (table_1.getModel().getValueAt(a, 1).toString().length() != 2)
					{
						memError = true;
						System.out.println("memError A");
					}
					else
					{
						// NOTE: Checks if all characters from input are hex
						for (int b = 0; b < 2; b++)
						{
							if (table_1.getModel().getValueAt(a, 1).toString().charAt(b) != '0' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != '1' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != '2' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != '3' && 
									table_1.getModel().getValueAt(a, 1).toString().charAt(b) != '4' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != '5' && 
									table_1.getModel().getValueAt(a, 1).toString().charAt(b) != '6' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != '7' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != '8' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != '9' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != 'A' && 
									table_1.getModel().getValueAt(a, 1).toString().charAt(b) != 'B' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != 'C' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != 'D' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != 'E' && table_1.getModel().getValueAt(a, 1).toString().charAt(b) != 'F')
							{
								memError = true;
								System.out.println("memError B");
								break;
							}
						}
					}
					if (memError)
					{
						// NOTE: Binabalik sa 0 yung data pag mali input 
						initializeData(a);
						System.out.println("Initialized Memory Address" + a + " to " + data.get(a).getValue());
						JOptionPane.showMessageDialog(new JFrame(), "Error!!");
						table_1.getModel().setValueAt(data.get(a).getValue(), a, 1);
						break;
					}
					else
					{
						// NOTE: Updates values and table
						data.get(a).setValue(table_1.getModel().getValueAt(a, 1).toString());
						table_1.getModel().setValueAt(data.get(a).getValue(), a, 1);
					}
				}
			}
		});
		btnInitializeData.setBounds(377, 420, 137, 23);
		panel.add(btnInitializeData);
		
		JButton btnNewButton_1 = new JButton("Go");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String input = textField.getText().toUpperCase();
				boolean goError = false;
				
				if (input.length() != 4)
					goError = true;
				else 
				{
					for (int b = 0; b < 4; b++)	
					{
						if (input.charAt(b) != '0' && input.charAt(b) != '1' && input.charAt(b) != '2' && input.charAt(b) != '3' && input.charAt(b) != '4' && input.charAt(b) != '5' && input.charAt(b) != '6' &&
								input.charAt(b) != '7' && input.charAt(b) != '8' && input.charAt(b) != '9' && input.charAt(b) != 'A' && input.charAt(b) != 'B' && input.charAt(b) != 'C' && input.charAt(b) != 'D' &&
								input.charAt(b) != 'E' && input.charAt(b) != 'F')
						{
							goError = true;
							break;
						}
					}
					
					if (goError == false)
					{
						int min = Integer.parseInt("2000", 16);
						int max = Integer.parseInt("3FFF", 16);
						int inp = Integer.parseInt(input, 16);
						
						if (inp < min)
							goError = true;
						else if (inp > max)
							goError = true;
					}
					
				}
				
				if (goError == true)
					JOptionPane.showMessageDialog(new JFrame(), "Error!");
				else
				{
					int a = 0;
					for (a = 0; a < 8192; a++)
					{
						if (data.get(a).getAddressHex().equals(input))
							break;
					}
					table_1.scrollRectToVisible(table_1.getCellRect(a, 0, true));
				}
				
			}
		});
		btnNewButton_1.setBounds(511, 234, 57, 23);
		panel.add(btnNewButton_1);
		
		textField = new JTextField();
		textField.setBounds(377, 235, 124, 20);
		panel.add(textField);
		textField.setColumns(10);
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(305, 51, 272, 128);
		panel_1.add(scrollPane_4);
		
		table_2 = new JTable();
		table_2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		String[] columns = new String[100];
		for (int b = 0; b < 100; b++)
		{
			columns[b] = Integer.toString(b + 1);
		}
		
		table_2.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
			},
			columns
		) {
			boolean[] columnEditables = new boolean[] {
				false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true
			};
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		
		for (int a = 0; a < 100; a++)
		{
			table_2.getColumnModel().getColumn(a).setResizable(false);
		}
		table_2.setFillsViewportHeight(true);
		scrollPane_4.setViewportView(table_2);
		
		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(305, 223, 262, 162);
		panel_1.add(scrollPane_5);
		
		textArea.setTabSize(3);
		textArea.setEditable(false);
		scrollPane_5.setViewportView(textArea);
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				codes.clear();
				cycles.clear();
				
				for (int a = 0; a < 100; a++)
				{
					for (int b = 0; b < 100; b++)
					{
						table_2.setValueAt("", a, b);
					}
				}
				
				initializeRegisters();
				for (int a = 0; a < 32; a++)
					table.setValueAt(registers.get(a).getValue(), a, 1);
				
				initializeMemory();
				for (int a = 0; a < 8192; a++)
					table_1.setValueAt(data.get(a).getValue(), a, 1);
				
				txtrCode.setText("");
				txtrCode2.setText("");
				textArea.setText("");
				txtrOpcode.setText("");
				
			}
		});
		btnReset.setBounds(30, 404, 246, 23);
		panel_1.add(btnReset);
		
		JButton btnPrevious = new JButton("Previous");
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String[] cycle = new String[50];
				cycle = textArea.getText().split("\\n");
				String[] line = cycle[1].split(" ");
				
				int cycleNum = Integer.parseInt(line[1]);
				System.out.println("Cycle Number: " + cycleNum);
				
				int x = cycleNum - 2;
				
				if (x > -1)
				{
					display = "\nCYCLE " + (x+1) + "\nInstruction Fetch\nIF/ID.IR = " + cycles.get(x).getIF_ID_IR() + "\nIF/ID.NPC = " + cycles.get(x).getIF_ID_NPC() + "\nPC = " + cycles.get(x).getPC() + "\n\nInstruction Decode\nID/EX.A = " + cycles.get(x).getID_EX_A() + "\nID/EX.B = " + cycles.get(x).getID_EX_B() + "\nID/EX.IMM = " + cycles.get(x).getID_EX_IMM() + "\nID/EX.IR = " + cycles.get(x).getID_EX_IR() + "\nID/EX.NPC = " + cycles.get(x).getID_EX_NPC() + "\n\nExecution\nEX/MEM.ALUoutput = " + cycles.get(x).getEX_MEM_ALU() + "\nEX/MEM.IR = " + cycles.get(x).getEX_MEM_IR() + "\nEX/MEM.Cond = " + cycles.get(x).getEX_MEM_COND() + "\n\nMemory Access\nMEM/WB.LMD = " + cycles.get(x).getMEM_WB_LMD() + "\nMEM/WB.IR = " + cycles.get(x).getMEM_WB_IR() + "\n\nWrite-Back\n" + cycles.get(x).getWbDisplay() + "\n~~~~~~~~~~~~~";
					textArea.setText(display);
					textArea.setCaretPosition(0);
				}
			}
		});
		btnPrevious.setBounds(305, 404, 89, 23);
		panel_1.add(btnPrevious);
		
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String[] cycle = new String[50];
				cycle = textArea.getText().split("\\n");
				String[] line = cycle[1].split(" ");
				
				int cycleNum = Integer.parseInt(line[1]);
				System.out.println("Cycle Number: " + cycleNum);
				
				int x = cycleNum;
				System.out.println("X is: " + x);
				
				int a = 0;
				for (a = 0; a < 100; a++)
				{
					if (table_2.getValueAt(codes.size()-1, a).toString().equals("WB"))
						break;
				}
				
				System.out.println("A is: " + a);
				
				if (x <= a)
				{
					display = "\nCYCLE " + (x+1) + "\nInstruction Fetch\nIF/ID.IR = " + cycles.get(x).getIF_ID_IR() + "\nIF/ID.NPC = " + cycles.get(x).getIF_ID_NPC() + "\nPC = " + cycles.get(x).getPC() + "\n\nInstruction Decode\nID/EX.A = " + cycles.get(x).getID_EX_A() + "\nID/EX.B = " + cycles.get(x).getID_EX_B() + "\nID/EX.IMM = " + cycles.get(x).getID_EX_IMM() + "\nID/EX.IR = " + cycles.get(x).getID_EX_IR() + "\nID/EX.NPC = " + cycles.get(x).getID_EX_NPC() + "\n\nExecution\nEX/MEM.ALUoutput = " + cycles.get(x).getEX_MEM_ALU() + "\nEX/MEM.IR = " + cycles.get(x).getEX_MEM_IR() + "\nEX/MEM.Cond = " + cycles.get(x).getEX_MEM_COND() + "\n\nMemory Access\nMEM/WB.LMD = " + cycles.get(x).getMEM_WB_LMD() + "\nMEM/WB.IR = " + cycles.get(x).getMEM_WB_IR() + "\n\nWrite-Back\n" + cycles.get(x).getWbDisplay() + "\n~~~~~~~~~~~~~";
					textArea.setText(display);
					textArea.setCaretPosition(0);
				}
			}
		});
		btnNext.setBounds(478, 404, 89, 23);
		panel_1.add(btnNext);
	}
}

