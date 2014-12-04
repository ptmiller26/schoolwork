package proj411;

public class register {

	public int value;
	public boolean bLocked;
	public int newValue;
	
	
	public register()
	{
		value = 0;
		bLocked = false;
		newValue = -1;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setValue(int incomingValue)
	{
		newValue = incomingValue;
	}
	
	public void lock()
	{
		bLocked = true;
	}
	
	public void unlock()
	{
		bLocked = false;
	}
	
	public void Tick()
	{
		if (bLocked == false)
		{
			if (newValue != -1)
			{
				value = newValue;
				newValue = -1;
			}
		}
	}
}
