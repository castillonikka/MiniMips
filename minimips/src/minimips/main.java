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
	private ArrayList<Cycle> cycles = new ArrayList<Cycle>();
	private ArrayList<Code> codes = new ArrayList<Code>();
	private boolean dependency;
	private String display;

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
			this.data.get(a).setValue("0000000000000000");
			value++;
		}
	}
	
	// NOTE: Used for error checking; Yung maling in-initialize na memory address, binabalik sa 0
	public void initializeData(int index)
	{
		this.data.get(index).setValue("0000000000000000");
	}
	
	// NOTE: Pipeline without hazards
	public void pipeline(ArrayList<Code> codes)
	{
		int cols = codes.size() + 4;					// Number of cycles
		cycles.clear(); 								// NOTE: Inaalis lahat ng laman ng "cycles", parang reset
		System.out.println("Size is: " + codes.size()); // NOTE: Pinapakita kung ilang cycles na
		for (int x = 0; x < 50; x++)					// NOTE: Inaalis lahat ng laman ng pipeline map table, parang reset
		{
			for (int y = 0; y < 50; y++)
				table_2.setValueAt("", x, y);
		}
		
		for (int a = 0; a < codes.size(); a++)			// NOTE: Pini-print yung pipeline map (without hazards)
		{
			if (a == 0 || dependency == false)
			{
				table_2.setValueAt("IF", a, a+0);
				table_2.setValueAt("ID", a, a+1);
				table_2.setValueAt("EX", a, a+2);
				table_2.setValueAt("MEM", a, a+3);
				table_2.setValueAt("WB", a, a+4);
			}
		}
		
		for (int c = 0; c < cols; c++)					// NOTE: Gumagawa ng array of cycles
			cycles.add(new Cycle());
		
		System.out.println("Num of cycles: " + cols);
		
		for (int x = 0; x < cols; x++)
		{
			for (int y = 0; y < codes.size(); y++)
			{
				String reg = table_2.getValueAt(y, x).toString();		// NOTE: Pipeline register (kung IF, ID, etc)
				switch(reg)
				{
					// NOTE: Ito yung mga gagawin pag IF na 
					case "IF":	
						System.out.println("LINDT");
						
						// NOTE: IR gets opcode of instruction
						cycles.get(x).setIF_ID_IR(codes.get(y).getOpcode());
						System.out.println("OPCODE!!! " + codes.get(y).getOpcode());
						
						// NOTE: temp is PC + 4
						int temp = Integer.parseInt(codes.get(y).getPcHex(), 16) + 4;
						
						cycles.get(x).setIF_ID_NPC(Integer.toHexString(temp));
						
						cycles.get(x).setPC(codes.get(y).getPcHex());
						break;
						
					// NOTE: Ito yung mga gagawin pag ID na 
					case "ID":
						System.out.println("HERSHEYS");
						
						// NOTE: Kinukuha laman ni RS (aka register A)
						int temp2 = Integer.parseInt(codes.get(y).getRs(), 2);
						System.out.println(temp2);
						String laman = registers.get(temp2).getValue();
						cycles.get(x).setID_EX_A(laman);
						System.out.println(cycles.get(x).getID_EX_A());
						
						// NOTE: Kinukuha laman ni RT (aka register B)
						temp2 = Integer.parseInt(codes.get(y).getRt(), 2);
						System.out.println(temp2);
						laman = registers.get(temp2).getValue();
						cycles.get(x).setID_EX_B(laman);
						System.out.println(cycles.get(x).getID_EX_B());
						
						// NOTE: Kinukuha yung immediate value sa opcode
						String hexImm2 = codes.get(y).getOpcode().substring(4);
						
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
						//hexImm2 = "000000000000" + hexImm2;
						System.out.println(hexImm2);
						cycles.get(x).setID_EX_IMM(hexImm2);
						System.out.println("Immediate is " + cycles.get(x).getID_EX_IMM());
						
						// NOTE: Gets previous IR
						cycles.get(x).setID_EX_IR(cycles.get(x-1).getIF_ID_IR());
						
						// NOTE: Gets previous NPC
						cycles.get(x).setID_EX_NPC(cycles.get(x-1).getIF_ID_NPC());
						break;
						
					case "EX":
						switch(codes.get(y).getInst().toUpperCase())
						{
							case "DADDU":
								int regA = Integer.parseInt(cycles.get(x-1).getID_EX_A(), 16);
								int regB = Integer.parseInt(cycles.get(x-1).getID_EX_B(), 16);
								int ans = regA + regB;
								cycles.get(x).setEX_MEM_ALU(Integer.toHexString(ans));
								while (cycles.get(x).getEX_MEM_ALU().length() < 16)
								{
									cycles.get(x).setEX_MEM_ALU("0" + cycles.get(x).getEX_MEM_ALU());
								}
								cycles.get(x).setEX_MEM_COND("0");
								break;
								
							case "DADDIU":
								BigInteger regA2 = new BigInteger(cycles.get(x-1).getID_EX_A(), 16);
								System.out.println("reg A: " + regA2);
								BigInteger tempImm = new BigInteger(cycles.get(x-1).getID_EX_IMM(), 16);
								System.out.println("tempImm: " + tempImm);
								BigInteger ans2 = regA2.add(tempImm);
								System.out.println("ans: "+ ans2);
								cycles.get(x).setEX_MEM_ALU(tempImm.toString(16));
								while (cycles.get(x).getEX_MEM_ALU().length() < 16)
								{
									cycles.get(x).setEX_MEM_ALU("0" + cycles.get(x).getEX_MEM_ALU());
								}
								cycles.get(x).setEX_MEM_COND("0");
								break;
								
							case "SLT":
								regA = Integer.parseInt(cycles.get(x-1).getID_EX_A(), 16);
								regB = Integer.parseInt(cycles.get(x-1).getID_EX_B(), 16);
								if (regA < regB)
									ans = 1;
								else ans = 0;
								cycles.get(x).setEX_MEM_ALU(Integer.toHexString(ans));
								while (cycles.get(x).getEX_MEM_ALU().length() < 16)
								{
									cycles.get(x).setEX_MEM_ALU("0" + cycles.get(x).getEX_MEM_ALU());
								}
								cycles.get(x).setEX_MEM_COND("0");
								break;
								
							case "SELEQZ":
								regA = Integer.parseInt(cycles.get(x-1).getID_EX_A(), 16);
								regB = Integer.parseInt(cycles.get(x-1).getID_EX_B(), 16);
								int num = Integer.parseInt(codes.get(y).getRs(), 2);
								if (regB == 0)
									ans = Integer.parseInt(registers.get(num).getValue(), 16);
								else ans = 0;
								cycles.get(x).setEX_MEM_ALU(Integer.toHexString(ans));
								while (cycles.get(x).getEX_MEM_ALU().length() < 16)
								{
									cycles.get(x).setEX_MEM_ALU("0" + cycles.get(x).getEX_MEM_ALU());
								}
								cycles.get(x).setEX_MEM_COND("0");
								break;
							
							case "DMULU":
							case "DMUHU":
								regA = Integer.parseInt(cycles.get(x-1).getID_EX_A(), 16);
								regB = Integer.parseInt(cycles.get(x-1).getID_EX_B(), 16);
								ans = regA * regB;
								if (codes.get(y).getInst().toUpperCase().equals("DMULU"))
								{
									cycles.get(x).setEX_MEM_ALU(Integer.toHexString(ans));
									while (cycles.get(x).getEX_MEM_ALU().length() < 16)
									{
										cycles.get(x).setEX_MEM_ALU("0" + cycles.get(x).getEX_MEM_ALU());
									}
								}
								else
								{
									cycles.get(x).setEX_MEM_ALU(Integer.toHexString(ans));
									while (cycles.get(x).getEX_MEM_ALU().length() < 32)
									{
										cycles.get(x).setEX_MEM_ALU("0" + cycles.get(x).getEX_MEM_ALU());
									}
									cycles.get(x).setEX_MEM_ALU(cycles.get(x).getEX_MEM_ALU().substring(0, 16));
								}
								cycles.get(x).setEX_MEM_COND("0");
								break;
								
							case "LD":
							case "SD":
								regA2 = new BigInteger(cycles.get(x-1).getID_EX_A(), 16);
								System.out.println("reg A: " + regA2);
								tempImm = new BigInteger(cycles.get(x-1).getID_EX_IMM(), 16);
								System.out.println("tempImm: " + tempImm);
								BigInteger ans2A = regA2.add(tempImm);
								System.out.println("ans: "+ ans2A);
								cycles.get(x).setEX_MEM_ALU(ans2A.toString(16));
								System.out.println(tempImm.toString(16));
								cycles.get(x).setEX_MEM_ALU(String.format("%16s", cycles.get(x).getEX_MEM_ALU()).replace(' ', '0'));
								System.out.println(cycles.get(x).getEX_MEM_ALU());
								cycles.get(x).setEX_MEM_B(cycles.get(x-1).getID_EX_B());
								cycles.get(x).setEX_MEM_COND("0");
								break;
						}
						
						cycles.get(x).setEX_MEM_IR(cycles.get(x-1).getID_EX_IR());
						
						break;
						
					case "MEM":
						switch(codes.get(y).getInst().toUpperCase())
						{
							case "DADDU":
							case "DADDIU":
							case "SLT":
							case "SELEQZ":
							case "DMULU":
							case "DMUHU":
								cycles.get(x).setMEM_ALUoutput(cycles.get(x-1).getEX_MEM_ALU());
								break;
								
							case "LD":
								String address = cycles.get(x-1).getEX_MEM_ALU().substring(12);
								System.out.println(address);
								int a = 0;
								for (a = 0; a < data.size(); a++)
								{
									if (data.get(a).getAddressHex().equals(address))
										break;
								}
								System.out.println("Value: " + data.get(a).getValue());
								cycles.get(x).setMEM_WB_LMD(data.get(a).getValue());
								break;
								
							case "SD":
								address = cycles.get(x-1).getEX_MEM_ALU().substring(12);
								/*System.out.println("ADDRESS " + address);
								System.out.println("INDEX OF " + data.indexOf(address));*/
								for (a = 0; a < data.size(); a++)
								{
									if (data.get(a).getAddressHex().equals(address))
										break;
								}
								data.get(a).setValue(cycles.get(x-1).getEX_MEM_B());
								table_1.getModel().setValueAt(cycles.get(x-1).getEX_MEM_B(), a, 1);
								cycles.get(x).setWbDisplay("Memory " + address + " = " + cycles.get(x).getEX_MEM_B());
								break;
						}
						cycles.get(x).setMEM_WB_IR(cycles.get(x-1).getEX_MEM_IR());
						break;
						
					case "WB":
						switch(codes.get(y).getInst().toUpperCase())
						{
							case "DADDU":
							case "SLT":
							case "SELEQZ":
							case "DMULU":
							case "DMUHU":
								int reg2 = Integer.parseInt(codes.get(y).getRd(), 2);
								registers.get(reg2).setValue(cycles.get(x-1).getMEM_ALUoutput());
								System.out.println("WB TO REG " + reg2);
								table.getModel().setValueAt(registers.get(reg2).getValue(), reg2, 1);
								cycles.get(x).setWbDisplay("R" + reg2 + " = " + registers.get(reg2).getValue());
								break;
							case "DADDIU":
								reg2 = Integer.parseInt(codes.get(y).getRt(), 2);
								registers.get(reg2).setValue(cycles.get(x-1).getMEM_ALUoutput());
								System.out.println("WB TO REG " + reg2);
								table.getModel().setValueAt(registers.get(reg2).getValue(), reg2, 1);
								cycles.get(x).setWbDisplay("R" + reg2 + " = " + registers.get(reg2).getValue());
								break;
							case "LD":
								reg2 = Integer.parseInt(codes.get(y).getRt(), 2);
								registers.get(reg2).setValue(cycles.get(x-1).getMEM_WB_LMD());
								System.out.println("WB TO REG " + reg2);
								table.getModel().setValueAt(registers.get(reg2).getValue(), reg2, 1);
								cycles.get(x).setWbDisplay("R" + reg2 + " = " + registers.get(reg2).getValue());
								break;
						}
				}
			}
			
			
		}
	}

	/**
	 * Create the frame.
	 */
	public main() {
		
		setTitle("mH1nh1m1pSxZc");
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
		for (int b = 0; b < 8192; b++)
		{
			model.addRow(new Object[]{data.get(b).getAddressHex(), data.get(b).getValue()});
		}
		
		btnInitializeValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				error = false;
				//registers[0] = "0000000000000000";
				registers.get(0).setValue("0000000000000000");
				table.getModel().setValueAt(registers.get(0).getValue(), 0, 1);
				for (int a = 1; a < 32; a++)
				{
					//registers[a] = table.getModel().getValueAt(a, 1).toString();
					//if (registers[a].length() != 16)
					if (table.getModel().getValueAt(a, 1).toString().length() != 16)
					{
						error = true;
						System.out.println("ERROR A");
					}
					else
					{
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
						registers.get(a).setValue(table.getModel().getValueAt(a, 1).toString());
						table.getModel().setValueAt(registers.get(a).getValue(), a, 1);
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
					codes.clear();
					pc = new int[100];
					pcHex = new String[100];
					
					txtrCode2.setText(txtrCode.getText());
					code = txtrCode.getText().split("\\n");
					txtrOpcode.setText("");
					
					for (int a = 0; a < code.length; a++)
					{
						codes.add(new Code());
						
						pc[a] = 4 * a;
						String tempBin = this.convertToBinary(pc[a]);
						pcHex[a] = this.convertToHex(tempBin);
						
						System.out.println(code[a] + "       " + pcHex[a]);
						String instruction = code[a].split(" ")[0];
						System.out.println(instruction);
						
						codes.get(a).setPcHex(pcHex[a]);
						codes.get(a).setLine(code[a]);
						codes.get(a).setInst(instruction);
						
						if (instruction.equals("BC") || instruction.equals("bc"))
						{
							String label = code[a].split(" ")[1];
							System.out.println(label);
							codes.get(a).setLabel(label);
							for (int b = 0; b < code.length; b++)
							{
								// STOPPED HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
								String lbl = code[b].split(" ")[0];
								lbl = lbl.substring(0, lbl.length()-1);
								System.out.println(lbl);
								if (lbl.equals(label))
								{
									int target = 4 * b;
									int pcBranch = pc[a];
									System.out.println(target + " and " + pcBranch + " and " + code[a]);
									Opcode opc = new Opcode (code[a], pcBranch, target);
									String outputOpc = opc.firstWord();
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
							String label = code[a].split(" ")[3];
							System.out.println(label);
							for (int b = 0; b < code.length; b++)
							{
								String lbl = code[b].split(" ")[0];
								lbl = lbl.substring(0, lbl.length()-1);
								System.out.println(lbl);
								if (lbl.equals(label))
								{
									int target = 4 * b;
									int pcBranch = pc[a];
									System.out.println(target + " and " + pcBranch + " and " + code[a]);
									Opcode opc = new Opcode (code[a], pcBranch, target);
									String outputOpc = opc.firstWord();
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
							Opcode opc = new Opcode(code[a]);
							String outputOpc = opc.firstWord();
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
					/*System.out.println("OPCODES ARE:");
					for (int x = 0; x < code.length; x++)
						System.out.println("Line " + (x+1) + " -- " + codes.get(x).getOpcode());*/
					
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
				/*Cycle cycles.get(x) = new Cycle();
				String display = "CYCLE " + cycles.get(x).getNum() + "\nInstruction Fetch\nIF/ID.IR = " + cycles.get(x).getIF_ID_IR() + "\nIF/ID.NPC = " + cycles.get(x).getIF_ID_NPC() + "\nPC = " + cycles.get(x).getPC() + "\n\nInstruction Decode\nID/EX.A = " + cycles.get(x).getID_EX_A() + "\nID/EX.B = " + cycles.get(x).getID_EX_B() + "\nID/EX.IMM = " + cycles.get(x).getID_EX_IMM() + "\nID/EX.IR = " + cycles.get(x).getID_EX_IR() + "\nID/EX.NPC = " + cycles.get(x).getID_EX_NPC() + "\n\nExecution\nEX/MEM.ALUoutput = " + cycles.get(x).getEX_MEM_ALU() + "\nEX/MEM.IR = " + cycles.get(x).getEX_MEM_IR() + "\nEX/MEM.Cond = " + cycles.get(x).getEX_MEM_COND() + "\n\nMemory Access\nMEM/WB.LMD = " + cycles.get(x).getMEM_WB_LMD() + "\nMEM/WB.IR = " + cycles.get(x).getMEM_WB_IR() + "\n\nWrite-Back\n\n~~~~~~~~~~~~~";
				textArea.append(display);*/
				//code = txtrCode.getText().split("\\n");
				//opcode = txtrOpcode.getText().split("\\n");
				pipeline(codes);
				
				for (int x = 0; x < cycles.size(); x++)
				{
					display = "\nCYCLE " + (x+1) + "\nInstruction Fetch\nIF/ID.IR = " + cycles.get(x).getIF_ID_IR() + "\nIF/ID.NPC = " + cycles.get(x).getIF_ID_NPC() + "\nPC = " + cycles.get(x).getPC() + "\n\nInstruction Decode\nID/EX.A = " + cycles.get(x).getID_EX_A() + "\nID/EX.B = " + cycles.get(x).getID_EX_B() + "\nID/EX.IMM = " + cycles.get(x).getID_EX_IMM() + "\nID/EX.IR = " + cycles.get(x).getID_EX_IR() + "\nID/EX.NPC = " + cycles.get(x).getID_EX_NPC() + "\n\nExecution\nEX/MEM.ALUoutput = " + cycles.get(x).getEX_MEM_ALU() + "\nEX/MEM.IR = " + cycles.get(x).getEX_MEM_IR() + "\nEX/MEM.Cond = " + cycles.get(x).getEX_MEM_COND() + "\n\nMemory Access\nMEM/WB.LMD = " + cycles.get(x).getMEM_WB_LMD() + "\nMEM/WB.IR = " + cycles.get(x).getMEM_WB_IR() + "\n\nWrite-Back\n" + cycles.get(x).getWbDisplay() + "\n~~~~~~~~~~~~~";
					textArea.append(display);
				}
				
			}
		});
		
		
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
		btnInitializeData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				memError = false;
				for (int a = 1; a < 8192; a++)
				{
					//registers[a] = table_1.getModel().getValueAt(a, 1).toString();
					//if (registers[a].length() != 16)
					if (table_1.getModel().getValueAt(a, 1).toString().length() != 16)
					{
						memError = true;
						System.out.println("memError A");
					}
					else
					{
						for (int b = 0; b < 16; b++)
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
						// STOPPED HERE!!!!!!!!!!!!!!!!!!!!!!! pt2
						initializeData(a);
						System.out.println("Initialized Memory Address" + a + " to " + data.get(a).getValue());
						JOptionPane.showMessageDialog(new JFrame(), "Error!!");
						table_1.getModel().setValueAt(data.get(a).getValue(), a, 1);
						break;
					}
					else
					{
						data.get(a).setValue(table_1.getModel().getValueAt(a, 1).toString());
						table_1.getModel().setValueAt(data.get(a).getValue(), a, 1);
					}
				}
				
				/*for (int c = 0; c < 8192; c++)
				{
					System.out.println("Memory" + c + ": " + data.get(c).getValue());
				}*/
			}
		});
		btnInitializeData.setBounds(377, 420, 137, 23);
		panel.add(btnInitializeData);
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(305, 51, 272, 128);
		panel_1.add(scrollPane_4);
		
		table_2 = new JTable();
		table_2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		DefaultTableModel tableModel = new DefaultTableModel() {

		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		
		String[] columns = new String[50];
		for (int b = 0; b < 50; b++)
		{
			columns[b] = Integer.toString(b + 1);
		}
		
		table_2.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
			}, columns
		) {
			boolean[] columnEditables = new boolean[] {
				false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true
			};
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		
		for (int a = 0; a < 50; a++)
		{
			table_2.getColumnModel().getColumn(a).setResizable(false);
		}
		table_2.setFillsViewportHeight(true);
		scrollPane_4.setViewportView(table_2);
		
		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(305, 223, 262, 128);
		panel_1.add(scrollPane_5);
		
		textArea.setTabSize(3);
		textArea.setEditable(false);
		scrollPane_5.setViewportView(textArea);
	}
}

