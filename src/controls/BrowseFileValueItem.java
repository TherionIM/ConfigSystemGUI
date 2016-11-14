package controls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.w3c.dom.Element;

import app.Importer;
import app.Window;


public class BrowseFileValueItem extends ValueItem
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void initialize(Element element, ActionListener listener)
	{
		add(new JLabel(Label));
		final JTextField field = new JTextField(Value.getValue(), Size);
		field.setHorizontalAlignment(JTextField.RIGHT);
		field.addActionListener(listener);								
		add(field);
		ValueComponent = field;
		JButton button = new JButton("Browse");
		final Component frame = this;
		button.addActionListener(new ActionListener()
		{					
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				final JFileChooser fc = new JFileChooser(new File("."));				
	            fc.showOpenDialog(frame);
	            File file = fc.getSelectedFile();
	            if (file != null)
	            {
	            	field.setText(file.getAbsolutePath());
	            }
			}
		});
		add(button);
	}

	@Override
	public boolean updateValue()
	{
		String value = ((JTextField)ValueComponent).getText();		
		return Value.update(value);
	}
	
	@Override
	public boolean importValue(ArrayList<Importer.DataItem> items)
	{		
		boolean result = false;
		if (ValueComponent.isVisible() && ValueComponent.isEnabled())
		{
			for (Importer.DataItem item : items)
			{
				if (item.m_name.equalsIgnoreCase(Value.Name))
				{
					item.m_imported = true;
					((JTextField)ValueComponent).setText(item.m_value);
					if (updateValue())
					{					
						result = true;
					}
					else
					{
						Window.addMsgToStatusArea("Import: Cannot load '" + Value.Name + "' \n");
					}	
					break;
				}
			}
			if (!result)
			{
				Window.addMsgToStatusArea("Import: Cannot find a value for '" + Value.Name + "' \n");
			}
		}
		else			
		{
			result = true;
		}
		return result;
	}

}
