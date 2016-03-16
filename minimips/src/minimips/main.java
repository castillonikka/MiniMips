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
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import java.awt.Font;
import java.awt.Toolkit;

public class main extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private JTable table_1;
	private int[] pc;
	private String[] code;
	private String[] pcHex;
	private String[] labels;
	private String[] registers = new String[32]; 
	private boolean error;

	public String[] getRegisters() {
		return registers;
	}

	public void setRegisters(String[] registers) {
		this.registers = registers;
	}
	
	public void setValue (int index, String value)
	{
		this.registers[index] = value;
	}

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
	
	public void initializeRegisters()
	{
		for (int x = 0; x < 32; x++)
			this.registers[x] = "0000000000000000";
	}
	
	public void initializeRegister(int index)
	{
		this.registers[index] = "0000000000000000";
	}

	/**
	 * Create the frame.
	 */
	public main() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Grace\\Documents\\OTHERS\\Block\\A\\J\\z4.jpg"));
		
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
		JScrollPane scrollPane_1 = new JScrollPane();
		JPanel panel_1 = new JPanel();
		JLabel lblOpcode = new JLabel("Code:");
		JButton btnNewButton = new JButton("Single-Step");
		JButton btnFullExecution = new JButton("Full Execution");
		JLabel lblPipelineMap = new JLabel("Pipeline Map:");
		JTextArea txtrPipeline = new JTextArea();
		JLabel lblMipsRegisters = new JLabel("MIPS64 Registers:");
		JTextArea txtrRegisters = new JTextArea();
		JButton btnInitializeValues = new JButton("Initialize Values");
		
		this.initializeRegisters();
		
		btnInitializeValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				error = false;
				registers[0] = "0000000000000000";
				table.getModel().setValueAt(registers[0], 0, 1);
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
						System.out.println("Initialized R" + a + " to " + registers[a]);
						JOptionPane.showMessageDialog(new JFrame(), "Error!");
						table.getModel().setValueAt(registers[a], a, 1);
						break;
					}
					else
					{
						registers[a] = table.getModel().getValueAt(a, 1).toString();
						table.getModel().setValueAt(registers[a], a, 1);
					}
					//table.getModel().setValueAt(registers[a], a, 1);
					
					
				}
				
				for (int c = 0; c < 32; c++)
				{
					System.out.println("R" + c + ": " + registers[c]);
				}
			}
		});
		
		
		lblOpcode_1.setBounds(316, 43, 124, 14);
		label_2.setBounds(31, 243, 49, 14);
		label_3.setBounds(316, 243, 42, 14);
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
		txtrPipeline.setBounds(305, 51, 272, 122);
		lblMipsRegisters.setBounds(305, 198, 131, 14);
		txtrRegisters.setBounds(305, 223, 272, 128);
		
		
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		panel.setToolTipText("");
		tabbedPane.addTab("Tab 1", null, panel, null);
		
		txtrOpcode.setFont(new Font("Courier New", Font.PLAIN, 13));
		txtrCode.setFont(new Font("Courier New", Font.PLAIN, 13));
		
		txtrCode2.setEditable(false);
		
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try
				{
					pc = new int[100];
					pcHex = new String[100];
					labels = new String[100];
					
					txtrCode2.setText(txtrCode.getText());
					code = txtrCode.getText().split("\\n");
					txtrOpcode.setText("");
					
					for (int a = 0; a < code.length; a++)
					{
						pc[a] = 4 * a;
						String tempBin = this.convertToBinary(pc[a]);
						pcHex[a] = this.convertToHex(tempBin);
						
						System.out.println(code[a] + "       " + pcHex[a]);
						String instruction = code[a].split(" ")[0];
						System.out.println(instruction);
						
						
						if (instruction.equals("BC") || instruction.equals("bc"))
						{
							String label = code[a].split(" ")[1];
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
									
									System.out.println("Opcode is: " + outputOpc);
									if (opc.isError() == true)
									{
										txtrOpcode.setText(opc.getErrorMessage());
										JOptionPane.showMessageDialog(new JFrame(), opc.getErrorMessage());
									}
									else txtrOpcode.setText(txtrOpcode.getText() + outputOpc + "\n");
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
									
									System.out.println("Opcode is: " + outputOpc);
									if (opc.isError() == true)
									{
										txtrOpcode.setText(opc.getErrorMessage());
										JOptionPane.showMessageDialog(new JFrame(), opc.getErrorMessage());
									}
									else txtrOpcode.setText(txtrOpcode.getText() + outputOpc + "\n");
								}
							}
						}
						else
						{
							Opcode opc = new Opcode(code[a]);
							String outputOpc = opc.firstWord();
							
							System.out.println("Opcode is: " + outputOpc);
							if (opc.isError() == true)
							{
								txtrOpcode.setText(opc.getErrorMessage());
								JOptionPane.showMessageDialog(new JFrame(), opc.getErrorMessage());
							}
							else txtrOpcode.setText(txtrOpcode.getText() + outputOpc + "\n");
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
		
		
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"R0 =", this.registers[0]},
				{"R1 =", this.registers[1]},
				{"R2 =", this.registers[2]},
				{"R3 =", this.registers[3]},
				{"R4 =", this.registers[4]},
				{"R5 =", this.registers[5]},
				{"R6 =", this.registers[6]},
				{"R7 =", this.registers[7]},
				{"R8 =", this.registers[8]},
				{"R9 =", this.registers[9]},
				{"R10 =", this.registers[10]},
				{"R11 =", this.registers[11]},
				{"R12 =", this.registers[12]},
				{"R13 =", this.registers[13]},
				{"R14 =", this.registers[14]},
				{"R15 =", this.registers[15]},
				{"R16 =", this.registers[16]},
				{"R17 =", this.registers[17]},
				{"R18 =", this.registers[18]},
				{"R19 =", this.registers[19]},
				{"R20 =", this.registers[20]},
				{"R21 =", this.registers[21]},
				{"R22 =", this.registers[22]},
				{"R23 =", this.registers[23]},
				{"R24 =", this.registers[24]},
				{"R25 =", this.registers[25]},
				{"R26 =", this.registers[26]},
				{"R27 =", this.registers[27]},
				{"R28 =", this.registers[28]},
				{"R29 =", this.registers[29]},
				{"R30 =", this.registers[30]},
				{"R31 =", this.registers[31]}
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
		
		
		
		table_1.setModel(new DefaultTableModel(
			new Object[][] {
				{"0013", null},
				{"0012", null},
				{"0011", null},
				{"0010", null},
				{"000F", null},
				{"000E", null},
				{"000D", null},
				{"000C", null},
				{"000B", null},
				{"000A", null},
				{"0009", null},
				{"0008", null},
				{"0007", null},
				{"0006", null},
				{"0005", null},
				{"0004", null},
				{"0003", null},
				{"0002", null},
				{"0001", null},
				{"0000", null},
			},
			new String[] {
				"Address", "Value"
			}
		));
		
		scrollPane_1.setViewportView(txtrCode);
		scrollPane_2.setViewportView(table_1);
		scrollPane_3.setViewportView(txtrOpcode);
		
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
		panel_1.add(txtrPipeline);
		panel_1.add(txtrRegisters);
		panel_1.add(lblMipsRegisters);
		
		tabbedPane.addTab("Execute", null, panel_1, null);
		
		txtrOpcode.setEditable(false);
		txtrRegisters.setEditable(false);
		txtrPipeline.setEditable(false);
	}
}

