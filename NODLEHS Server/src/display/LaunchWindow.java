package display;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JLayeredPane;
import java.awt.FlowLayout;
import javax.swing.UIManager;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;
import java.awt.Font;

public class LaunchWindow extends JFrame {

	private JLayeredPane contentPane;
	JTextArea board;
	JTextArea console;
	JTextArea inputTextArea;
	private JScrollPane inputPane;
	
	public JTextArea getBoard(){return this.board;}
	public JTextArea getConsole(){return this.console;}
	public JTextArea getInputTextArea(){return this.inputTextArea;}
	
	public void setBoard(String boardIn){this.board.setText(boardIn);}
	public void setConsole(String consoleIn){this.console.setText(consoleIn);}
	public void setInputTextArea(String inputTextIn){this.console.setText(inputTextIn);}


	/**
	 * Create the frame.
	 */
	public LaunchWindow() {
		
		initComponents();
		createEvents();
		
	}

	private void initComponents() 
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 846, 517);
		contentPane = new JLayeredPane();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane boardPane = new JScrollPane();
		boardPane.setAutoscrolls(true);
		
		JScrollPane consolePane = new JScrollPane();
		consolePane.setAutoscrolls(true);
		
		inputPane = new JScrollPane();
		inputPane.setAutoscrolls(true);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(boardPane, GroupLayout.DEFAULT_SIZE, 829, Short.MAX_VALUE)
				.addComponent(consolePane, GroupLayout.DEFAULT_SIZE, 829, Short.MAX_VALUE)
				.addComponent(inputPane, GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(boardPane, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(consolePane, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(inputPane, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
					.addGap(0))
		);
		
		inputTextArea = new JTextArea();
		inputPane.setViewportView(inputTextArea);
		
		console = new JTextArea();
		console.setEditable(false);
		consolePane.setViewportView(console);
		
		board = new JTextArea();
		board.setFont(new Font("Monospaced", Font.PLAIN, 11));
		board.setEditable(false);
		boardPane.setViewportView(board);
		contentPane.setLayout(gl_contentPane);
		
	}
	private void createEvents() 
	{
		// TODO Auto-generated method stub
		
	}
}
