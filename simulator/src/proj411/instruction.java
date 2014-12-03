/**
 * 
 */
package proj411;

import java.io.*;
import java.util.*;

/**
 * @author Patrick
 *
 */
public class instruction {

	int numExInstructions;
	public String instruction;
	public String parameters;
	public String param1;
	public String param2;
	public String param3;
	public String label;
	
	public instruction(String inst, String params, String lab)
	{
		instruction = inst;
		parameters = params;
		parseParameters();
		if (lab != null)
		{
			label = lab;
		}
		numExInstructions = getExecInstructionCount();
	}
	
	private void parseParameters()
	{
		String temp = parameters;
		List<Integer> commaIndexes = new ArrayList<>();
		int index = temp.indexOf(",");
		while (index >= 0)
		{
			commaIndexes.add(index);
			index = temp.indexOf(",", index+1);
		}
		if (commaIndexes.size() == 0)	// SRL or SRA
		{
			param1 = temp.trim();
		}
		else if (commaIndexes.size() == 1)
		{
			param1 = temp.substring(0, commaIndexes.get(0)).trim();
			param2 = temp.substring(commaIndexes.get(0)+1, parameters.length()).trim();
			param3 = null;
		}
		else if (commaIndexes.size() == 2)
		{
			param1 = temp.substring(0, commaIndexes.get(0)).trim();
			param2 = temp.substring(commaIndexes.get(0)+1,commaIndexes.get(1)).trim();
			param3 = temp.substring(commaIndexes.get(1)+1, parameters.length()).trim();
		}
	}
	
	public String getInstruction()
	{
		return instruction;
	}
	
	public String getFirstParameter()
	{
		return param1;
	}
	
	public String getSecondParameter()
	{
		return param2;
	}
	
	public String getThirdParameter()
	{
		return param3;
	}
	
	// returns true if it allows forwarding
	// returns false if it does not allow forwarding
	public boolean allowsForwarding()
	{
		return true;
	}
	
	private int getExecInstructionCount()
	{
		if (instruction.equalsIgnoreCase("MULTI"))
		{
			return 4;
		}
		else if (instruction.equalsIgnoreCase("ADDI") ||
				instruction.equalsIgnoreCase("SUB") ||
				instruction.equalsIgnoreCase("SUBI"))
		{
			return 2;
		}
		else if (instruction.equalsIgnoreCase("AND") ||
				instruction.equalsIgnoreCase("ANDI") ||
				instruction.equalsIgnoreCase("OR") ||
				instruction.equalsIgnoreCase("ORI") ||
				instruction.equalsIgnoreCase("SLL") ||
				instruction.equalsIgnoreCase("SRI") ||
				instruction.equalsIgnoreCase("SRA") ||
				instruction.equalsIgnoreCase("SLLI") ||
				instruction.equalsIgnoreCase("SRLI") ||
				instruction.equalsIgnoreCase("SRAI") ||
				instruction.equalsIgnoreCase("BEQ") ||
				instruction.equalsIgnoreCase("BNE") ||
				instruction.equalsIgnoreCase("J") ||
				instruction.equalsIgnoreCase("LW") ||
				instruction.equalsIgnoreCase("SW") ||
				instruction.equalsIgnoreCase("LI") ||
				instruction.equalsIgnoreCase("ADD") ||
				instruction.equalsIgnoreCase("MULT"))
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
