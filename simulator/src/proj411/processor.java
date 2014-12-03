/**
 * 
 */
package proj411;

import java.util.*;
import stages.InstructionFetch;
import stages.InstructionDecode;
import stages.Execute;
import stages.Memory;
import stages.WriteBack;

/**
 * @author Patrick
 *
 */
public class processor {
	
	int numInstructions;
	private final InstructionFetch IF;
	private final InstructionDecode ID;
	private final Execute EX;
	private final Memory MEM;
	private final WriteBack WB;
	

	public processor(ArrayList<String> instructions, ArrayList<String> parameters, 
			ArrayList<String> data, ArrayList<String> labels)
	{
		numInstructions = 0;
		IF = new InstructionFetch();
		ID = new InstructionDecode();
		EX = new Execute();
		MEM = new Memory();
		WB = new WriteBack();
	}
	
	public void Tick()
	{
		while (WB.isFinished() == false)
		{
			IF.Tick();
			ID.Tick();
			EX.Tick();
			MEM.Tick();
			WB.Tick();
		}
	}
	
	
}
