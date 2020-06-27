import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class HelloWorld {
	
	static String Pname = null;
	
	static boolean shouldWait = true;
	
	public static void main(final String[] args) {

	 HelloWorld helloObject = new HelloWorld();
	 

		System.out.println(helloObject.toString());
		
		final JFrame parent = new JFrame();
		
		JButton button = new JButton();
	    
		helloObject.setString("Hello World. ");
		
		int x = 4;
		String s = "hello";
		
		button.setText("Click me to show dialog!");
		parent.add(button);
		parent.pack();
		parent.setVisible(true);

				String name = JOptionPane.showInputDialog(parent, "What is your name?", null);
				
				Pname = name;
				shouldWait = false;
				System.out.println("Should wait changed to false");
				
				if(name.equals("password"))
				{
                    shouldWait = true;
                    s="secrect";
					System.out.println("Should wait changed to true");
					helloObject.setString("The secret password was entered");
					x=100;
				}
				else if (name.equals("exeption"))
				{
					helloObject.throwExeption();
                    x=6;
                    s="Exeption";
				}
				else
				{
                    helloObject.setString("The password was entered");
                    s=name;
					x=0;
				}
				
				JOptionPane.showMessageDialog(parent, helloObject.getString() + Pname);
				
				System.out.println(helloObject.toString());
				
				
				System.exit(0);
				
	}

	private String textVal = null;
	
	public HelloWorld(){
	}

	public void setString(String str)
	{
		this.textVal = str;
	}
	
	public String getString()
	{
		return this.textVal;
	}
	
	public boolean getShouldWait()
	{
		return shouldWait;
	}
	
	public String toString()
	{
		return "HelloWorld object: " + "shouldWait = " + getShouldWait() + " helloWorld = " + getString();
	}
	
	public void throwExeption()
	{
		 try {  
               Thread t = new Thread(new Runnable() {  
                               public void run() {  
                                   try  
                                   {  
                                     System.out.println( new NoClassDefFoundError().toString());  
                                   }  
                                   catch (Throwable ex) {  
      
                                   }  
                               }  
                          });  
               t.start();  
               t.join();  
           } catch (Throwable th) {  
               // Eat this and don't tell anybody  
           }  
	}
	
}