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
	private boolean stall;
	private int tempCol, tempA;

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
	
	// NOTE: Pipeline
	public void pipeline(ArrayList<Code> codes)
	{
		cycles.clear(); 								// NOTE: Inaalis lahat ng laman ng "cycles", parang reset
		for (int x = 0; x < 100; x++)					// NOTE: Inaalis lahat ng laman ng pipeline map table, parang reset
		{
			for (int y = 0; y < 100; y++)
				table_2.setValueAt("", x, y);
		}
		System.out.println("Size is: " + codes.size()); // NOTE: Pinapakita kung ilang cycles na
		for (int x = 0; x < 100; x++)					// NOTE: Inaalis lahat ng laman ng pipeline map table, parang reset
		{
			for (int y = 0; y < 100; y++)
				table_2.setValueAt("", x, y);
		}
		
		for (int a = 0; a < codes.size(); a++)
		{
			dependency = false;
			stall = false;
			if (a == 0)
			{
				table_2.setValueAt("IF", a, a+0);
				table_2.setValueAt("ID", a, a+1);
				table_2.setValueAt("EX", a, a+2);
				table_2.setValueAt("MEM", a, a+3);
				table_2.setValueAt("WB", a, a+4);
				System.out.println("PRINTED FIRST INST");
			}
			else if (a != 0 && table_2.getValueAt(a-1, a+0).toString().equals("*") == false)
			{
				tempA = a;
				while (table_2.getValueAt(a-1, tempA).toString().equals("IF") || table_2.getValueAt(a-1, tempA).toString().equals(""))
				{
					tempA++;
					//table_2.setValueAt("IF", a, tempA+1);
				}
				System.out.println("TempA is " + tempA);
				table_2.setValueAt("IF", a, tempA);
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
				System.out.println("Printed stalls!");
				while (table_2.getValueAt(a-1, tempCol).toString().equals("IF") || table_2.getValueAt(a-1, tempCol).toString().equals("") || table_2.getValueAt(a-1, tempCol).toString().equals("*"))
				{
					tempCol++;
					//table_2.setValueAt("IF", a, tempCol);
				}
				table_2.setValueAt("IF", a, tempCol);
				System.out.println("Fetch! TempCol is " + tempCol);
			}
			System.out.println(codes.get(a).getInst().toUpperCase());
			switch(codes.get(a).getInst().toUpperCase())
			{
				case "DADDU":
				case "SLT":
				case "SELEQZ":
				case "DMULU":
				case "DMUHU":
					for (int b = 0; b < a; b++)
					{
						System.out.println("Entered loop!");
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
								(codes.get(a).getRs().equals(codes.get(b).getRt()))
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
									System.out.println("IF of dependent inst is at " + start);
									if (dependency == true)
									{
										numStalls = write - start;
										d = numStalls;
										System.out.println("Number of stalls should be " + d);
										/*for (int x = a; x <= c; x++)
										{
											table_2.setValueAt("*", a, x);
										}*/
										if (numStalls > 0)
										{
											while (numStalls > 0)
											{
												table_2.setValueAt("*", a, (c - numStalls + 1));
												numStalls--;
											}
											table_2.setValueAt("ID", a, write+1);
											table_2.setValueAt("EX", a, write+2);
											table_2.setValueAt("MEM", a, write+3);
											table_2.setValueAt("WB", a, write+4);
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
											table_2.setValueAt("ID", a, lookupID+1);
											table_2.setValueAt("EX", a, lookupID+2);
											table_2.setValueAt("MEM", a, lookupID+3);
											table_2.setValueAt("WB", a, lookupID+4);
										}
										
									}
									else if (stall == true && tempCol > write)
									{
										System.out.println("TempCol is " + tempCol);
										table_2.setValueAt("ID", a, tempCol+1);
										table_2.setValueAt("EX", a, tempCol+2);
										table_2.setValueAt("MEM", a, tempCol+3);
										table_2.setValueAt("WB", a, tempCol+4);
									}
								}
								
								//System.out.println("Printed everything else!");
							}
							System.out.println("Printed everything else!");
						}
						else if (dependency == false && stall == false)
						{
							System.out.println("I am not dependent!");
							table_2.setValueAt("ID", a, tempA+1);
							table_2.setValueAt("EX", a, tempA+2);
							table_2.setValueAt("MEM", a, tempA+3);
							table_2.setValueAt("WB", a, tempA+4);
						}
						else if (dependency == false && stall == true)
						{
							System.out.println("I am not dependent! B");
							table_2.setValueAt("ID", a, tempCol+1);
							table_2.setValueAt("EX", a, tempCol+2);
							table_2.setValueAt("MEM", a, tempCol+3);
							table_2.setValueAt("WB", a, tempCol+4);
						}
					}
					break;
				case "LD":
				case "DADDIU":
					for (int b = 0; b < a; b++)
					{
						System.out.println("Entered loop!");
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
										/*for (int x = a; x <= c; x++)
										{
											table_2.setValueAt("*", a, x);
										}*/
										while (numStalls != 0)
										{
											table_2.setValueAt("*", a, (c - numStalls + 1));
											numStalls--;
										}
										table_2.setValueAt("ID", a, write+1);
										table_2.setValueAt("EX", a, write+2);
										table_2.setValueAt("MEM", a, write+3);
										table_2.setValueAt("WB", a, write+4);
									}
									else if (stall == true && tempCol > write)
									{
										System.out.println("TempCol is " + tempCol);
										table_2.setValueAt("ID", a, tempCol+1);
										table_2.setValueAt("EX", a, tempCol+2);
										table_2.setValueAt("MEM", a, tempCol+3);
										table_2.setValueAt("WB", a, tempCol+4);
									}
								}
								
								//System.out.println("Printed everything else!");
							}
							System.out.println("Printed everything else!");
						}
						else if (dependency == false && stall == false)
						{
							System.out.println("I am not dependent!");
							table_2.setValueAt("ID", a, tempA+1);
							table_2.setValueAt("EX", a, tempA+2);
							table_2.setValueAt("MEM", a, tempA+3);
							table_2.setValueAt("WB", a, tempA+4);
						}
						else if (dependency == false && stall == true)
						{
							System.out.println("I am not dependent! B");
							table_2.setValueAt("ID", a, tempCol+1);
							table_2.setValueAt("EX", a, tempCol+2);
							table_2.setValueAt("MEM", a, tempCol+3);
							table_2.setValueAt("WB", a, tempCol+4);
						}
					}
					break;
				case "SD":
					for (int b = 0; b < a; b++)
					{
						System.out.println("Entered loop!");
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
										/*for (int x = a; x <= c; x++)
										{
											table_2.setValueAt("*", a, x);
										}*/
										while (numStalls != 0)
										{
											table_2.setValueAt("*", a, (c - numStalls + 1));
											numStalls--;
										}
										table_2.setValueAt("ID", a, write+1);
										table_2.setValueAt("EX", a, write+2);
										table_2.setValueAt("MEM", a, write+3);
										table_2.setValueAt("WB", a, write+4);
									}
									else if (stall == true && tempCol > write)
									{
										System.out.println("TempCol is " + tempCol);
										table_2.setValueAt("ID", a, tempCol+1);
										table_2.setValueAt("EX", a, tempCol+2);
										table_2.setValueAt("MEM", a, tempCol+3);
										table_2.setValueAt("WB", a, tempCol+4);
									}
								}
								
								//System.out.println("Printed everything else!");
							}
							System.out.println("Printed everything else!");
						}
						else if (dependency == false && stall == false)
						{
							System.out.println("I am not dependent!");
							table_2.setValueAt("ID", a, tempA+1);
							table_2.setValueAt("EX", a, tempA+2);
							table_2.setValueAt("MEM", a, tempA+3);
							table_2.setValueAt("WB", a, tempA+4);
						}
						else if (dependency == false && stall == true)
						{
							System.out.println("I am not dependent! B");
							table_2.setValueAt("ID", a, tempCol+1);
							table_2.setValueAt("EX", a, tempCol+2);
							table_2.setValueAt("MEM", a, tempCol+3);
							table_2.setValueAt("WB", a, tempCol+4);
						}
					}
					break;
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
				for (int x = 0; x < cycles.size(); x++)
				{
					display = "\nCYCLE " + (x+1) + "\nInstruction Fetch\nIF/ID.IR = " + cycles.get(x).getIF_ID_IR() + "\nIF/ID.NPC = " + cycles.get(x).getIF_ID_NPC() + "\nPC = " + cycles.get(x).getPC() + "\n\nInstruction Decode\nID/EX.A = " + cycles.get(x).getID_EX_A() + "\nID/EX.B = " + cycles.get(x).getID_EX_B() + "\nID/EX.IMM = " + cycles.get(x).getID_EX_IMM() + "\nID/EX.IR = " + cycles.get(x).getID_EX_IR() + "\nID/EX.NPC = " + cycles.get(x).getID_EX_NPC() + "\n\nExecution\nEX/MEM.ALUoutput = " + cycles.get(x).getEX_MEM_ALU() + "\nEX/MEM.IR = " + cycles.get(x).getEX_MEM_IR() + "\nEX/MEM.Cond = " + cycles.get(x).getEX_MEM_COND() + "\n\nMemory Access\nMEM/WB.LMD = " + cycles.get(x).getMEM_WB_LMD() + "\nMEM/WB.IR = " + cycles.get(x).getMEM_WB_IR() + "\n\nWrite-Back\n" + cycles.get(x).getWbDisplay() + "\n~~~~~~~~~~~~~";
					textArea.append(display);
				}
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
				for (int a = 1; a < 8192; a++)
				{
					// NOTE: If memory data is not 16 bits long
					if (table_1.getModel().getValueAt(a, 1).toString().length() != 16)
					{
						memError = true;
						System.out.println("memError A");
					}
					else
					{
						// NOTE: Checks if all characters from input are hex
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
			new String[] {
				"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV", "BW", "BX", "BY", "BZ", "CA", "CB", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK", "CL", "CM", "CN", "CO", "CP", "CQ", "CR", "CS", "CT", "CU", "CV"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		
		for (int a = 0; a < 100; a++)
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

