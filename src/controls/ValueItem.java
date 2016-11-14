package controls;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.w3c.dom.Element;

import app.Window;

import values.IValue;
import values.ValueFactory;

public abstract class ValueItem extends RulesItem
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	public IValue Value;
	public JComponent ValueComponent;
	
	public boolean deserialize(Element element, ActionListener listener, List<String> messages)
	{		
		Value = ValueFactory.createValue(element, messages);
		if (Value != null)
		{
			setToolTipText(Value.getTip());
			return super.deserialize(element, listener, messages);	
		}
		return false;
	}	
	
	protected abstract void initialize(Element element, ActionListener listener);	
	public abstract boolean updateValue();	
	

	@Override
	public boolean isValidItem()
	{	
		boolean result = updateValue();
		if (!result)
		{
			Window.addMsgToStatusArea("Invalid: " + Value.Name + ": " +  Value.getTip() + "\n");			
		}
		return result;
	}

	@Override
	public void export(BufferedWriter out) throws IOException
	{
		if (ValueComponent.isEnabled() && ValueComponent.isVisible())
		{
			out.write(Value.Name + " " + Value.getValue() + "\n");
		}
	}

	@Override
	public IValue findValue(String param)
	{
		if (param.equalsIgnoreCase(Id))
		{
			updateValue();
			return Value;
		}
		return null;
	}

	@Override
	public void enforceRules(ArrayList<RulesItem> items)
	{
		super.checkRules(items);		
	}

	@Override
	public void setActivity(boolean valid)
	{	
		Component[] components = getComponents();
		for (Component comp : components)
		{
			comp.setEnabled(valid);
		}
	}

	@Override
	public void setVisibility(boolean valid)
	{
		Component[] components = getComponents();
		for (Component comp : components)
		{
			comp.setVisible(valid);
		}
	}	
}
