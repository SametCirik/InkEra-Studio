package inkera.ui.placeholders;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PlaceholderPhone 
	extends JTextField
{
	private final String placeholder = " Phone Number";
	private final String mask = "+--- (---) --- -- --";
	private boolean showingMask= false;
	
	public PlaceholderPhone()
	{
		setMargin(new Insets(0, 
							 6, 
							 0, 
							 0));
		
		addFocusListener(new FocusListener() 
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				if(getText().isEmpty())
				{
					setText(mask);
					showingMask = true;
					setCaretPosition(firstEmptyPosition());
				}
			}
			
			@Override 
			public void focusLost(FocusEvent e)
			{
				if(getDigitsOnly().isEmpty())
				{
					setText("");
					showingMask = false;
				}
			}
		});
		
		getDocument().addDocumentListener(new DocumentListener() 
		{
			private boolean updating = false;
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				formatPhone();
			}
			
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				formatPhone();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				formatPhone();
			}
			
			private void formatPhone()
			{
				if(updating || !showingMask) 
				{
					return;
				}
				
				updating = true;
				
				String digits = getDigitsOnly();
				StringBuilder formatted = new StringBuilder(mask);
				
				int digitalIndex = 0;
				for(int i = 0; i < formatted.length(); i++) 
				{
					if(formatted.charAt(i) == '-')
					{
						if(digitalIndex < digits.length())
						{
							formatted.setCharAt(i, 
												digits.charAt(digitalIndex));
							digitalIndex++;
						}
					}
				}
				final String formattedText = formatted.toString();
				javax.swing.SwingUtilities.invokeLater(() -> 
				{
					setText(formattedText);
					setCaretPosition(firstEmptyPosition());
					updating = false;
				});
			}
		});
	}
	
	private String getDigitsOnly()
	{
		return getText().replaceAll("\\D", 
									"");
	}
	
	private int firstEmptyPosition()
	{
		String text = getText();
		
		for(int i = 0; i < text.length(); i++)
		{
			if(text.charAt(i) == '-')
			{
				return i;
			}
		}
		return text.length(); 
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		
		if(!showingMask && getText().isEmpty())
		{
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(Color.GRAY);
			g2.setFont(getFont().deriveFont(Font.ITALIC));
			g2.drawString(placeholder, 
						  5, 
						  getHeight() / 2 + getFont().getSize() / 2 - 2);
			g2.dispose();
		}
	}
	
	public void setCountryCode(String code)
	{
	    if(code == null) return;
	    
	    showingMask = true;
	    
	    String digits = getDigitsOnly();
	    setText(code + " " + mask); // Ülke kodu + maskeyi birleştir
	    
	    javax.swing.SwingUtilities.invokeLater(() -> 
	    {
	        setCaretPosition((code + " ").length()); // Kodu geçip yazmaya başlasın
	    });
	}
}
