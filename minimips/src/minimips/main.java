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
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.DefaultComboBoxModel;

public class main extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private JTable table_1;
	private int[] pc;
	private String[] code;

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
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setToolTipText("");
		tabbedPane.addTab("Tab 1", null, panel, null);
		
		JLabel lblOpcode_1 = new JLabel("Opcode:");
		lblOpcode_1.setBounds(316, 43, 124, 14);
		
		JLabel label_2 = new JLabel("Registers:");
		label_2.setBounds(31, 243, 49, 14);
		
		JLabel label_3 = new JLabel("Memory:");
		label_3.setBounds(316, 243, 42, 14);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 263, 263, 146);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(316, 263, 263, 146);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(316, 68, 263, 120);
		
		JTextArea txtrOpcode = new JTextArea();
		JTextArea txtrCode = new JTextArea();
		
		JButton btnEnter = new JButton("Enter Program");
		btnEnter.setBounds(102, 199, 118, 23);
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println(textPane.getText());
				
				
				
				code = txtrCode.getText().split("\\n");
				txtrOpcode.setText("");
				for (int a = 0; a < code.length; a++)
				{
					System.out.println(code[a]);
					Opcode opc = new Opcode(code[a]);
					String outputOpc = opc.generateOpcode();
					System.out.println("Opcode is: " + outputOpc);
					txtrOpcode.setText(txtrOpcode.getText() + outputOpc + "\n");
				}
			}
		});
		
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"R0 =", "0000000000000000"},
				{"R1 =", null},
				{"R2 =", null},
				{"R3 =", null},
				{"R4 =", null},
				{"R5 =", null},
				{"R6 =", null},
				{"R7 =", null},
				{"R8 =", null},
				{"R9 =", null},
				{"R10 =", null},
				{"R11 =", null},
				{"R12 =", null},
				{"R13 =", null},
				{"R14 =", null},
				{"R15 =", null},
				{"R16 =", null},
				{"R17 =", null},
				{"R18 =", null},
				{"R19 =", null},
				{"R20 =", null},
				{"R21 =", null},
				{"R22 =", null},
				{"R23 =", null},
				{"R24 =", null},
				{"R25 =", null},
				{"R26 =", null},
				{"R27 =", null},
				{"R28 =", null},
				{"R29 =", null},
				{"R30 =", null},
			},
			new String[] {
				"Registers", "Values"
			}
		));
		table.setFillsViewportHeight(true);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		scrollPane.setViewportView(table);
		
		table_1 = new JTable();
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
		scrollPane_2.setViewportView(table_1);
		panel.setLayout(null);
		panel.add(btnEnter);
		panel.add(label_2);
		panel.add(scrollPane);
		panel.add(label_3);
		panel.add(lblOpcode_1);
		panel.add(scrollPane_3);
		
		
		scrollPane_3.setViewportView(txtrOpcode);
		txtrOpcode.setEditable(false);
		panel.add(scrollPane_2);
		
		JLabel label = new JLabel("Code:");
		label.setBounds(31, 43, 49, 14);
		panel.add(label);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(31, 68, 263, 120);
		panel.add(scrollPane_1);
		
		
		
		scrollPane_1.setViewportView(txtrCode);
		txtrCode.setEditable(false);
		txtrCode.setText("Code");
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Tab 2", null, panel_1, null);
		
		JLabel lblOpcode = new JLabel("Code:");
		lblOpcode.setBounds(30, 26, 63, 14);
		
		JTextArea txtrAsd = new JTextArea();
		txtrAsd.setBounds(30, 51, 246, 300);
		txtrAsd.setEditable(false);
		txtrAsd.setText("asd");
		
		JButton btnNewButton = new JButton("Single-Step");
		btnNewButton.setBounds(30, 362, 121, 23);
		
		JButton btnFullExecution = new JButton("Full Execution");
		btnFullExecution.setBounds(161, 362, 115, 23);
		
		JLabel lblPipelineMap = new JLabel("Pipeline Map:");
		lblPipelineMap.setBounds(305, 26, 115, 14);
		
		JTextArea txtrAsdf = new JTextArea();
		txtrAsdf.setBounds(305, 51, 272, 122);
		txtrAsdf.setEditable(false);
		txtrAsdf.setText("asdf");
		panel_1.setLayout(null);
		panel_1.add(btnNewButton);
		panel_1.add(btnFullExecution);
		panel_1.add(txtrAsd);
		panel_1.add(lblOpcode);
		panel_1.add(lblPipelineMap);
		panel_1.add(txtrAsdf);
		
		JLabel lblMipsRegisters = new JLabel("MIPS64 Registers:");
		lblMipsRegisters.setBounds(305, 198, 131, 14);
		panel_1.add(lblMipsRegisters);
		
		JTextArea txtrDfg = new JTextArea();
		txtrDfg.setBounds(305, 223, 272, 128);
		txtrDfg.setEditable(false);
		txtrDfg.setText("dfg");
		panel_1.add(txtrDfg);
	}
}
