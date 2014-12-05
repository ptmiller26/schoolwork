/**
 * 
 */
package proj411;

import java.util.*;

/**
 * @author Patrick
 *
 */
public class instruction {
	
	public enum Stage {
		NONE, IF, ID, EX1, EX2, EX3, EX4, MEM, WB, DONE
	};

	int numExInstructions;
	public String instruction;
	public String parameters;
	public String param1;
	public String param2;
	public String param3;
	public String label;
	public Stage eStage;
	
	int iLeavesIF;
	int iLeavesID;
	int iLeavesEX;
	int iLeavesMEM;
	int iLeavesWB;
	
	public instruction(String inst, String params, String lab)
	{
		eStage = Stage.NONE;
		instruction = inst;
		parameters = params;
		parseParameters();
		if (lab != null)
		{
			label = lab;
		}
		numExInstructions = getExecInstructionCount();
	}
	
	public void initStageEnum()
	{
		eStage = Stage.IF;
	}
	
	public void incrementStageEnum(int cycleCount)
	{
		if (!eStage.equals(Stage.DONE))
		{
			switch (eStage)
			{
			case IF:
				iLeavesIF = cycleCount;
				break;
			case ID:
				iLeavesID = cycleCount;
				break;
			case EX1:
				iLeavesIF = cycleCount;
				break;
			case EX2:
				iLeavesIF = cycleCount;
				break;
			case EX3:
				iLeavesIF = cycleCount;
				break;
			case EX4:
				iLeavesIF = cycleCount;
				break;
			case MEM:
				iLeavesIF = cycleCount;
				break;
			case WB:
				iLeavesIF = cycleCount;
				break;
			}
			eStage = Stage.values()[eStage.ordinal() + 1];
		}
	}
	
	public String getStageToString()
	{
		if (eStage == Stage.NONE)
		{
			return "NONE";
		}
		else if (eStage == Stage.IF)
		{
			return "IF";
		}
		else if (eStage == Stage.ID)
		{
			return "ID";
		}
		else if (eStage == Stage.EX1)
		{
			return "EX1";
		}
		else if (eStage == Stage.EX2)
		{
			return "EX2";
		}
		else if (eStage == Stage.EX3)
		{
			return "EX3";
		}
		else if (eStage == Stage.EX4)
		{
			return "EX4";
		}
		else if (eStage == Stage.MEM)
		{
			return "MEM";
		}
		else if (eStage == Stage.WB)
		{
			return "WB";
		}
		else if (eStage == Stage.DONE)
		{
			return "DONE";
		}
		else
		{
			return "";
		}
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
	
	public Stage getStage()
	{
		return eStage;
	}
	
	// returns true if it allows forwarding
	// returns false if it does not allow forwarding
	public boolean requiresForwarding(String previousLocation)
	{
		if (previousLocation.equalsIgnoreCase(param2) || previousLocation.equalsIgnoreCase(param3))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	 
	// pmiller - might not need this in the end
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
