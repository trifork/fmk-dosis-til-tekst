package dk.medicinkortet.dosisstructuretext;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import dk.medicinkortet.dosisstructuretext.dailydosis.CalculationException;
import dk.medicinkortet.dosisstructuretext.dailydosis.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.longtext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

public class TestApplication extends JFrame implements TableModel {
	
	private ArrayList<TableModelListener> tableModelListeners = new ArrayList<TableModelListener>();
	private ArrayList<DosisStructureText> dosisStructureTexts = new ArrayList<DosisStructureText>(); 
	private String[] COLUMN_NAMES = new String[] {"Takstkode", "Takst-tekst", "Kort tekst", "Lang tekst", "Gns. dagsdosis", "Enhed"};
	private JTable table; 
	private Properties properties = new Properties();
	private static DecimalFormat FORMAT = new DecimalFormat("########0.#########");
	private ErrorFrame errorFrame;
	private JButton errorButton;
	
	public TestApplication() {
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			properties.load(new FileInputStream("dosisstructuretext-testapplication.properties"));
		}
		catch(Exception e) {
			System.out.println("Error loading propeties: "+e.getMessage());
		}
		initComponents();	
		showCentered(new Dimension(1000, 800));
		errorFrame = new ErrorFrame(this); 
		
		try {
			initModel(new File[]{new File(properties.getProperty("input"))});
		}
		catch(Exception e) {
			System.out.println("Error loading data on startup: "+e.getMessage());
		}
	}
	
	private void initComponents() {
		setTitle("Doseringsstruktur-overs\u00E6tter");
		getContentPane().setLayout(new GridBagLayout());
				
		JPanel topPanel = new JPanel(new GridBagLayout());		
		
		JScrollPane scrollPane = new JScrollPane();
		
		table = new JTable(this) {
            public String getToolTipText(MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int row = rowAtPoint(p);
                int col = columnAtPoint(p);
                return "<html>"+table.getModel().getValueAt(row, convertColumnIndexToModel(col)).toString().replaceAll("\n", "<br>")+"</html>";
            }			
		};
		
		scrollPane.getViewport().add(table);		
		
		getContentPane().add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 1., 1., GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		JButton souceButton = new JButton("\u00C5bn...");
		souceButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					JFileChooser fc = new JFileChooser(properties.getProperty("input"));
					fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					fc.setMultiSelectionEnabled(true);
					fc.showOpenDialog(null);
					try {				
						initModel(fc.getSelectedFiles());
						if(fc.getSelectedFiles()!=null&&fc.getSelectedFiles().length>0) {
							properties.put("input", fc.getSelectedFiles()[0].getAbsolutePath());
							properties.store(new FileOutputStream("dosisstructuretext-testapplication.properties"), null);
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		);
		topPanel.add(souceButton, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		
		JButton destinationButton = new JButton("Gem...");
		destinationButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					JFileChooser fc = new JFileChooser(properties.getProperty("output"));
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setMultiSelectionEnabled(false);
					fc.showOpenDialog(null);
					try {				
						if(fc.getSelectedFile()!=null) {
							properties.put("output", fc.getSelectedFile().getAbsolutePath());
							properties.store(new FileOutputStream("dosisstructuretext-testapplication.properties"), null);
							save(fc.getSelectedFile());
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		);
		topPanel.add(destinationButton, new GridBagConstraints(1, 0, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				
		JButton deleteButton = new JButton("Ryd listen");
		deleteButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					dosisStructureTexts.clear();
					table.updateUI(); 
					errorFrame.clear();
				}
			}
		);
		topPanel.add(deleteButton, new GridBagConstraints(2, 0, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		errorButton = new JButton("Vis fejl...");
		errorButton.setVisible(false);
		errorButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					errorFrame.setVisible(true);
					errorFrame.toFront();
				}
			}
		);
		topPanel.add(errorButton, new GridBagConstraints(3, 0, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		getContentPane().add(topPanel, new GridBagConstraints(0, 1, 1, 1, 1., 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));						
	}

	private void initModel(File[] fs) throws IOException, XPathException, DosageValidationException {
		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			for(File f: fs) {
				initModel(f);
			}
			System.out.println(""+dosisStructureTexts.size()+" dosis structures in list");
			Collections.sort(dosisStructureTexts, new Comparator<DosisStructureText>() {
				public int compare(DosisStructureText d1, DosisStructureText d2) {
					return Integer.parseInt(d1.getPriceListDosageCode()) - Integer.parseInt(d2.getPriceListDosageCode());
				}});
			table.updateUI(); 
		}
		finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	private void initModel(File f) throws XPathException {
		System.out.println("Loading data from "+f.getAbsolutePath());
		if(f.isDirectory()) {
			String[] filenames = f.list(new FilenameFilter() {
				public boolean accept(File folder, String filename) {
					return filename.toLowerCase().endsWith(".xml");
				}
			});
			Arrays.sort(filenames);
			for(int i=0; i<filenames.length; i++) {  			
				initModelFromFile(new File(f, filenames[i]));
			}			
		}
		else if(f.isFile()) {
			initModelFromFile(f);
		}
		else {
			throw new RuntimeException("Cannot load \""+f.getAbsolutePath()+"\"");
		}		
	}
	
	private void initModelFromFile(File f) throws XPathException {
		try {
			System.out.println("Loading "+f.getAbsolutePath());
			StringBuffer xml = load(f);
			DosisStructureText result = convert(xml, f.getAbsolutePath());
			dosisStructureTexts.add(result);
		}
		catch(DosageValidationException e) {
			errorFrame.addError(e.getMessage());
		}
		catch(IOException e) {
			errorFrame.addError(e.getMessage());
		}
		
	}
	
	private static String Q = "\"";
	private static String S = ";";
	private static String NL = System.getProperty("line.separator");
	
	private void save(File file) throws IOException {		
		BufferedWriter w = new BufferedWriter(new FileWriter(file));
		StringBuffer b = new StringBuffer();
		b.append(Q).append(COLUMN_NAMES[0]).append(Q).append(S);
		b.append(Q).append(COLUMN_NAMES[1]).append(Q).append(S);
		b.append(Q).append(COLUMN_NAMES[2]).append(Q).append(S);
		b.append(Q).append(COLUMN_NAMES[3]).append(Q).append(S);
		b.append(Q).append(COLUMN_NAMES[4]).append(Q).append(S);
		b.append(Q).append(COLUMN_NAMES[5]).append(Q).append(NL);
		w.write(b.toString());
		for(DosisStructureText d: this.dosisStructureTexts) {
			b = new StringBuffer();
			b.append(Q).append(d.getPriceListDosageCode()).append(Q).append(S);
			b.append(Q).append(d.getPriceListDosageText()).append(Q).append(S);
			b.append(Q).append(d.getShortText()).append(Q).append(S);
			b.append(Q).append(d.getLongText().endsWith("\n")?d.getLongText().substring(0, d.getLongText().length()-1):d.getLongText()).append(Q).append(S);
			b.append(Q).append(formatDosis(d)).append(Q).append(S);
			b.append(Q).append(d.getUnit()).append(Q).append(NL);
			w.write(b.toString());
		}
		w.close();
	}
	
	private static StringBuffer load(File file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		StringBuffer xml = new StringBuffer();
		while(r.ready()) {
			xml.append(r.readLine()).append('\n');
		}
		return xml;
	}
	
	private static DosisStructureText convert(StringBuffer xml, String filename) throws XPathException, DosageValidationException {
		
		Nodes nodes = new Nodes(xml);		
		DosisStructureText result = new DosisStructureText();
		
		// This limits the test to XML documents with one root element
		if(nodes.size()==0)
			throw new RuntimeException("No root element");		
		if(nodes.size()>1)
			throw new RuntimeException("More than one root element");
		Node root = nodes.getNode(0);

		// Performs validation of business rules
		try {
			Validator.validate(root);
		}
		catch(DosageValidationException e) {
			throw new DosageValidationException("Error in file "+filename+": "+e.getMessage());
		}
		
		// Make the long and short text, and calculate the avg. daily dosis.
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		try {
			DailyDosisCalculator.calculateAvg(root, result);
		}
		catch(CalculationException e) {
			e.printStackTrace();
		}
		return result;
	}	
	
	private void showCentered(Dimension d) {
		setPreferredSize(d);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width-d.width)/2, (screenSize.height-d.height)/2);
		pack();
		setVisible(true);
	}
	

	
	public static void main(String[] args) {
		try {
		    try {
		    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    } 
		    catch(Exception e) {
		    	System.out.println("Error setting native LAF: " + e);
		    }
			TestApplication t = new TestApplication();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void addTableModelListener(TableModelListener listener) {
		tableModelListeners.add(listener);
	}

	public Class<?> getColumnClass(int column) {
		return String.class;
	}

	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	public String getColumnName(int column) {
		return COLUMN_NAMES[column];		
	}

	public int getRowCount() {
		return dosisStructureTexts.size();
	}

	public Object getValueAt(int row, int column) {
		switch(column) {
			case 0: return dosisStructureTexts.get(row).getPriceListDosageCode();
			case 1: return dosisStructureTexts.get(row).getPriceListDosageText();
			case 2: return dosisStructureTexts.get(row).getShortText();
			case 3: return dosisStructureTexts.get(row).getLongText();
			case 4: return formatDosis(dosisStructureTexts.get(row)); 
			default: return dosisStructureTexts.get(row).getUnit();
		}
	}

	private String formatDosis(DosisStructureText d) {
		return 
			(d.getAvgDailyDosis()!=null ? 
				FORMAT.format(d.getAvgDailyDosis()) : 
					(d.getMinAvgDailyDosis()!=null ? FORMAT.format(d.getMinAvgDailyDosis()) : "" ) + 
					" - " + 
					(d.getMaxAvgDailyDosis()!=null ? FORMAT.format(d.getMaxAvgDailyDosis()) : ""));		
	}
	
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	public void removeTableModelListener(TableModelListener listener) {
		tableModelListeners.remove(listener);
	}

	public void setValueAt(Object arg0, int arg1, int arg2) {
		throw new RuntimeException();
	}
	
	class ErrorFrame extends JFrame {
		
		private String EMPTY = "<html><head></head><body></body></html>";
		private JEditorPane editor = new JEditorPane("text/html", EMPTY);
		
		private ErrorFrame(JFrame parent) {
			initComponents();
			pack();
			setLocationRelativeTo(parent);
		}
		
		private void initComponents() {
			setTitle("Doseringsstruktur-overs\u00E6tter - Fejl");
			editor.setEditable(false);
			getContentPane().setLayout(new BorderLayout());			
			getContentPane().add(editor, BorderLayout.CENTER);
			getContentPane().setPreferredSize(new Dimension(1000, 400));
		}	
		
		private void clear() {
			editor.setText(EMPTY);
			setVisible(false);
			errorButton.setVisible(false);
		}
		
		private void addError(String message) {
			StringBuffer b = new StringBuffer(editor.getText());
			int i = b.lastIndexOf("</body>");
			b.insert(i, message+"<hr>");
			editor.setText(b.toString().replaceAll(":", "<br>"));
			if(!isVisible())
				setVisible(true);
			if(!errorButton.isVisible())
				errorButton.setVisible(true);
		}
	}
	
}
