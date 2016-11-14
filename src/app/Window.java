/**
 * 
 */
package app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.concurrent.TimeoutException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import rules.Rule;

import controls.RulesItem;
import controls.RulesItem.ControlType;

/**
 * @author Giannis
 *
 */
public class Window extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JTabbedPane TabbedPane = new JTabbedPane();
	protected static JTextArea s_statusArea = null;
	protected boolean m_useGeneratedInputForExecution = true;
	protected boolean m_enforceRuntimeRulesValidationBeforeExecuting = true;
	protected boolean m_useProvidedExecutable = true;	

	public Window()
	{	
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	public static void addMsgToStatusArea(String msg)
	{
		if (s_statusArea != null)
		{			
			s_statusArea.append(msg + "\n");
		}
	}
	
	public static void clearStatusArea()
	{
		if (s_statusArea != null)
		{
			s_statusArea.setText("");
		}
	}	
	
	public boolean initialize(final WindowData data)
	{
		boolean result = true;
		
		setTitle(data.Title);	
		
		TabbedPane = new JTabbedPane();
		for (int i=0; i<data.Items.size(); ++i)
		{
			RulesItem item = data.Items.get(i);
			if (item.Control == ControlType.TAB)
			{
				if (item.HelperComponent != null)
				{
					TabbedPane.addTab(item.Label, item.HelperComponent);
				}
				else
				{
					TabbedPane.addTab(item.Label, item);
				}
			}
			else
			{
				TabbedPane.add(item);
			}
		}
        JButton executeButton = new JButton("Execute");
        executeButton.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent e)
			{	
				execute(data);				
			}
		});
        
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent e)
			{	
				clearStatusArea();	
			}
		});
        
        // status
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));        
        s_statusArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(s_statusArea);
        s_statusArea.setEditable(false); 
        s_statusArea.setLineWrap(true);
        s_statusArea.setWrapStyleWord(true);        
        panel.add(scrollPane);
        
        // Buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));       
        buttonsPanel.add(executeButton);
        buttonsPanel.add(clearButton);
        panel.add(buttonsPanel);
        panel.setBorder(new TitledBorder("Status"));
        
        addMsgToStatusArea("version: " + data.Version + "\n");
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, TabbedPane, panel);
        splitPane.setDividerLocation(600);
        add(splitPane);
        
        JMenuBar menuBar = initializeMenuBar(data);        
        setJMenuBar(menuBar);
        
        pack();
		centerWindow();		
		
		return result;
	}
	
	private void execute(final WindowData data)
	{
		String executable = data.Execute;
		if (!m_useProvidedExecutable)
		{	
			JFileChooser fc = new JFileChooser(new File("."));
			fc.setDialogTitle("Select executable to use...");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);		
            int result = fc.showOpenDialog(this);  
            switch (result) 
            {
	            case JFileChooser.APPROVE_OPTION:
	            	File file = fc.getSelectedFile();
	                if (file != null)
	                {
	                	executable = file.getAbsolutePath();
	                }
	              break;	            
	            case JFileChooser.ERROR_OPTION:
	            case JFileChooser.CANCEL_OPTION:
	            	return;	              
            }            
		}
		
		String inputFile = "";
		if (!m_useGeneratedInputForExecution)
		{
			JFileChooser fc = new JFileChooser(new File("."));
			fc.setDialogTitle("Select input file to use...");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);		
            int result = fc.showOpenDialog(this);  
            switch (result) 
            {
	            case JFileChooser.APPROVE_OPTION:
	            	File file = fc.getSelectedFile();
	                if (file != null)
	                {
	                	inputFile = file.getAbsolutePath();
	                }
	              break;	            
	            case JFileChooser.ERROR_OPTION:
	            case JFileChooser.CANCEL_OPTION:
	            	return;	              
            }            
		}
		else if (generate(data, false, data.OutputFilename))	
		{
			inputFile = data.OutputFilename;
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Could not generate input file", "Info", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (checkRuntimeRules(data))
		{	
			clearStatusArea();
			try
			{
				String lineToExecute = (executable + " " + inputFile);
				s_statusArea.setText("Executing: " + lineToExecute);
				RuntimeExecutor executor = new RuntimeExecutor(data.Timeout);
				String result = executor.execute(lineToExecute, null);			
				String str = "\nOutput: ";			
				addMsgToStatusArea(str + result);
			}
			catch (TimeoutException e) 
			{ 
				s_statusArea.setText(s_statusArea.getText() + "\nError: The application timed out!");
				e.printStackTrace();
			}
			catch (IOException e) 
			{		
				s_statusArea.setText(s_statusArea.getText() + "\nError: Cannot run " + executable);
				e.printStackTrace();
			}
			catch (Exception e)
			{ 
				e.printStackTrace();
				s_statusArea.setText(s_statusArea.getText() + "\nError: Cannot run " + executable);
			}
		}
	}
	
	private boolean checkRuntimeRules(final WindowData data)
	{
		boolean result = true;
		if (m_enforceRuntimeRulesValidationBeforeExecuting && (data.RunTimeRules.size() > 0))
		{
			for (Rule rule : data.RunTimeRules)
			{
				if (rule.isValid(data.Items))
				{
					if (JOptionPane.showConfirmDialog(null, "Rule: '" + rule.Msg + "' was flagged as invalid, \nwould you like to continue with execution?", "Runtime rule validation", JOptionPane.YES_NO_OPTION) != 0)
					{
						result = false;
						break;
					}
				}
			}
		}		
		return result;
	}

	private boolean generate(final WindowData data, boolean showResult, String filename)
	{
		 boolean result = true;
		 Exporter exporter = new Exporter();
         if (exporter.isValid(data))
         { 
         	if (exporter.export(data, filename))
         	{
	         	if (showResult)
	          	{
	         		JOptionPane.showMessageDialog(null, "Export finished", "Info", JOptionPane.INFORMATION_MESSAGE);
	          	}         	
	         	result = true;
         	}
         	else         		
         	{
         		result = false;
         	}
         }
         else
         {	
        	if (showResult)
           	{
        		 JOptionPane.showMessageDialog(null, "Invalid values", "Info", JOptionPane.ERROR_MESSAGE);
           	}
         	result = false;
         }
         return result;
	}
	
	protected void saveConfiguration(WindowData data)
	{		
		JFileChooser fc = new JFileChooser(new File("."));		
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);		
        int result = fc.showDialog(this, "Save");  
        switch (result) 
        {
            case JFileChooser.APPROVE_OPTION:
            	File file = fc.getSelectedFile();
                if (file != null)
                {
                	if (file.exists())
                	{
                		final int answer = JOptionPane.showConfirmDialog(null, "Override existing file '" + file.getName() + "'?"); 
                		if (answer == 1) // NO
                		{                			
                			String newFileName = JOptionPane.showInputDialog(null, "Please enter new name:", file.getName());
                			File newFile = new File(newFileName);
                			if (newFile.exists())
                			{
                				JOptionPane.showMessageDialog(null, "File already exists!");
                				return;
                			}
                			else
                			{
                				// use the new file
                				file = newFile;
                			}
                		}
                		else if (answer == 2) // Cancel
                		{
                			return;
                		}
                	}
                	String inputFile = file.getAbsolutePath();
                	generate(data, true, inputFile);            		
                }
              break;	            
            case JFileChooser.ERROR_OPTION:
            case JFileChooser.CANCEL_OPTION:
            	return;	              
        }
	}
	
	protected void loadConfiguration(WindowData data)
	{
		JFileChooser fc = new JFileChooser(new File("."));		
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);		
        int result = fc.showDialog(this, "Load");  
        switch (result) 
        {
            case JFileChooser.APPROVE_OPTION:
            	File file = fc.getSelectedFile();            	
                if (file != null)
                {	
                	String inputFile = file.getAbsolutePath();
                	final Importer importer = new Importer();
            		importer.importConfiguration(data, inputFile);            		
                }
              break;	            
            case JFileChooser.ERROR_OPTION:
            case JFileChooser.CANCEL_OPTION:
            	return;	              
        }		
	}
	
	private JMenuBar initializeMenuBar(final WindowData data)
	{
		// Menu 
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem item = new JMenuItem("Load input file");
        item.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				loadConfiguration(data);
			}
		});
        menu.add(item);
        menu.add(new JSeparator());
        item = new JMenuItem("Save configuration...");
        item.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveConfiguration(data);		
			}
		});
        menu.add(item);
        item = new JMenuItem("Execute");
        item.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				execute(data);				
			}
		});
        menu.add(item);
        menu.add(new JSeparator());
        item = new JMenuItem("Exit");
        item.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);				
			}
		});
        menu.add(item);
        menuBar.add(menu);
        
        menu = new JMenu("Options");
        item = new JCheckBoxMenuItem("Use generated input for execution");
        item.setSelected(m_useGeneratedInputForExecution);
        item.addItemListener(new ItemListener()
		{	
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				m_useGeneratedInputForExecution = (e.getStateChange() == ItemEvent.SELECTED);
			}
		});
        menu.add(item);
        
        item = new JCheckBoxMenuItem("Enforce runtime rules validation before executing");
        item.setSelected(m_enforceRuntimeRulesValidationBeforeExecuting);
        item.addItemListener(new ItemListener()
		{	
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				m_enforceRuntimeRulesValidationBeforeExecuting = (e.getStateChange() == ItemEvent.SELECTED);
			}
		});
        menu.add(item);
        
        item = new JCheckBoxMenuItem("Use provided executable");
        item.setSelected(m_useProvidedExecutable);
        item.addItemListener(new ItemListener()
		{	
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				m_useProvidedExecutable = (e.getStateChange() == ItemEvent.SELECTED);
			}
		});
        menu.add(item);
        
        menuBar.add(menu);
        
        menu = new JMenu("Help");
        item = new JMenuItem("About");
        item.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(null, data.AboutStr, "About", JOptionPane.INFORMATION_MESSAGE);				
			}
		});
        menu.add(item);
        menuBar.add(menu);
        
		return menuBar;
	}

	protected void centerWindow()
	{
		Toolkit tk = Toolkit.getDefaultToolkit();
	    Dimension screenSize = tk.getScreenSize();	    	    
	    setLocation((screenSize.width / 2) - (getWidth() / 2), 0);
	}
}
