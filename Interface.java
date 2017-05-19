import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;

public class Interface {

	private JFrame frame;
	final JFileChooser fc = new JFileChooser();

	boolean b_pressed = false;

	Runner runner = new Runner();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface window = new Interface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 635, 428);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton BOTAO = new JButton(".THETA");
		BOTAO.setBackground(Color.RED);
		BOTAO.setFont(new Font("Script MT Bold", Font.PLAIN, 21));
		BOTAO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fs = new JFileChooser(new File("c:\\"));
				fs.setDialogTitle("Opens a File");
				int result = fs.showSaveDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						File fi = fs.getSelectedFile();
						BufferedReader br = new BufferedReader(new FileReader(fi.getPath()));
						String line = "";
						String s = "";
						while ((s = br.readLine()) != null) {
							s += line;
						}
						if (br != null)
							br.close();
						// CODIGO
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(null, e2.getMessage());
					}
				}
			}
		});
		BOTAO.setBounds(372, 21, 200, 150);
		frame.getContentPane().add(BOTAO);

		JButton BUTAOcsv = new JButton(".CSV");
		BUTAOcsv.setBackground(Color.GREEN);
		BUTAOcsv.setFont(new Font("Script MT Bold", Font.PLAIN, 21));
		BUTAOcsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fs = new JFileChooser(new File("c:\\"));
				fs.setDialogTitle("Opens a File");
				int result = fs.showSaveDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						File fi = fs.getSelectedFile();
						Amostra amostra = ReadAmostra.read(fi.getAbsolutePath());
						runner.amostra = amostra;

						BufferedReader br = new BufferedReader(new FileReader(fi.getPath()));
						String line = "";
						String s = "";
						while ((s = br.readLine()) != null) {
							s += line;
						}
						if (br != null)
							br.close();
						// CODIGO
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(null, e2.getMessage());
					}
				}
			}
		});
		BUTAOcsv.setBounds(68, 21, 200, 150);
		frame.getContentPane().add(BUTAOcsv);
		
		JButton btnNewButton = new JButton("Run And SAVE");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fs = new JFileChooser(new File ("c:\\"));
				fs.setDialogTitle("Save a File");
				//fs.setFileFilter(new FileTypeFilter(".txt", "Text File"));
				int result = fs.showSaveDialog(null);
				if(result == JFileChooser.APPROVE_OPTION) {
					File fi = fs.getSelectedFile();
					try{
						runner.runAndSave(fi);
						FileWriter fw = new FileWriter(fi.getPath());
						fw.flush();
						fw.close();
						//CODIGO//
					}catch(Exception e2) {
						JOptionPane.showMessageDialog(null, e2.getMessage());
					}
				}
			}
		});
		btnNewButton.setFont(new Font("Script MT Bold", Font.PLAIN, 21));
		btnNewButton.setBackground(Color.YELLOW);
		btnNewButton.setBounds(226, 186, 200, 150);
		frame.getContentPane().add(btnNewButton);
	}
}
