/**
 * 
 */
package proj411;

import java.util.*;

/**
 * @author Patrick
 *
 */
public class processor {
	
	// the king of them all
	ArrayList<instruction> InstructionsInPipeline;

	boolean bHitFirstHLT;
	boolean bHitSecondHLT;
	boolean bIFOpen;
	boolean bIDOpen;
	boolean bEX1Open;
	boolean bEX2Open;
	boolean bEX3Open;
	boolean bEX4Open;
	boolean bMEMOpen;
	boolean bWBOpen;
	
	int instructionCounter;	// keeps track of current instruction
	int programCounter;		// keeps track of current line
	int cycleCount;
	
	ArrayList<instruction> instructionHolder;
	ArrayList<String> dataHolder;
	
	HashMap<String, register> registers; 
	HashMap<Integer, Integer> dataMap;
	
	public processor(ArrayList<instruction> instructionArray, ArrayList<String> data)
	{
		InstructionsInPipeline = new ArrayList<instruction>();
		
		bIFOpen = true;
		bIDOpen = true;
		bEX1Open = true;
		bEX2Open = true;
		bEX3Open = true;
		bEX4Open = true;
		bMEMOpen = true;
		bWBOpen = true;
		
		bHitFirstHLT = false;
		bHitSecondHLT = false;
		
		instructionHolder = instructionArray;
		dataHolder = data;
		
		instructionCounter = 0;
		programCounter = 0;
		registers = new HashMap<String, register>(); // represent r1-r31
		dataMap = new HashMap<Integer, Integer>();
		
		initRegisters();
		initData();
		
		cycleCount = 0;
		for (int i = 0; i < 10; i++)
		{
			Tick();
		}
//		for (int k = 0; k < registers.size(); k++)
//		{
//			String key = "R" + k;
//			System.out.println(key + ": " + registers.get(key).getValue());
//		}
//		while(!bHitSecondHLT)	// pmiller <----- need to update this condition
//		{
//			Tick();
//		}
	}
	
	public void initRegisters()
	{
		for (int i = 0; i < 32; i++)
		{
			String key = "R" + i;
			registers.put(key, new register());
		}
	}
	
	public void initData()
	{
		for (int i = 0; i < dataHolder.size(); i++)
		{
			Integer temp;
			temp = Integer.parseInt(dataHolder.get(i), 2);
			dataMap.put((4*i)+256, temp);
		}
//		Iterator i = dataMap.entrySet().iterator();
//		while (i.hasNext())
//		{
//			String key = i.next().toString();
//			String value = i.next().toString();
//			System.out.println(key + " " + value);
//		}
	}
	
	public void Tick()
	{	
		if (loadNextInstructionIntoPipeline())	// if there are no stalls/problems, this returns true
		{
			if (instructionHolder.size() <= instructionCounter)
			{
				// there are no instructions left to load
			}
			else
			{
				instruction instructionToAdd = instructionHolder.get(instructionCounter);
				InstructionsInPipeline.add(instructionToAdd);
				runPipelineOneCycle();
				instructionCounter++;
				printCurrentCycleInfo();
			}
		}
		else
		{
			// we need to stall
		}
		cycleCount++;
	}
	
	public void printCurrentCycleInfo()
	{
		if (cycleCount == 0)
		{
			System.out.println(" 		IF		ID		EX1		EX2		EX3		EX4		MEM		WB" + "\n");
		}
		String [] tempArr = getCycleDetailsForPrintOut();	
		System.out.print((cycleCount+1) + ": ");
		System.out.println(" 		" + tempArr[0] + "		" + tempArr[1] + " 		" + tempArr[2] +
						" 		" + tempArr[3] + " 		" + tempArr[4] + " 		" + tempArr[5] + 
						" 		" + tempArr[6] + " 		" + tempArr[7]);
	}
	
	public String[] getCycleDetailsForPrintOut()
	{
		String[] instructsInArray = new String[8];
		instructsInArray[0] = "-";
		instructsInArray[1] = "-";
		instructsInArray[2] = "-";
		instructsInArray[3] = "-";
		instructsInArray[4] = "-";
		instructsInArray[5] = "-";
		instructsInArray[6] = "-";
		instructsInArray[7] = "-";
		for (int i = 0; i < InstructionsInPipeline.size(); i++)
		{
			instruction temp = InstructionsInPipeline.get(i);
			switch (temp.getStageToString())
			{
			case "IF":
				instructsInArray[0] = temp.getInstruction();
				break;
			case "ID":
				instructsInArray[1] = temp.getInstruction();
				break;
			case "EX1":
				instructsInArray[2] = temp.getInstruction();
				break;
			case "EX2":
				instructsInArray[3] = temp.getInstruction();
				break;
			case "EX3":
				instructsInArray[4] = temp.getInstruction();
				break;
			case "EX4":
				instructsInArray[5] = temp.getInstruction();
				break;
			case "MEM":
				instructsInArray[6] = temp.getInstruction();
				break;
			case "WB":
				instructsInArray[7] = temp.getInstruction();
				break;
			}
		}
		return instructsInArray;
	}
	
	public void runPipelineOneCycle()
	{
		// incrementing the eStages for each instruction currently in the pipeline
		for (int i = 0; i < InstructionsInPipeline.size(); i++)
		{
			instruction temp = InstructionsInPipeline.get(i);
			int currentStageVal = temp.getStage().ordinal();
			if (currentStageVal == 0)		// currently in NONE
			{
				temp.initStageEnum();
			}
			else if (currentStageVal == 1)	// currently in IF
			{
				if (bIDOpen)
				{
					temp.incrementStageEnum();
					bIFOpen = true;
				}
			}
			else if (currentStageVal == 2)	// currently in ID
			{
				if (bEX1Open)
				{
					temp.incrementStageEnum();
					bIDOpen = true;
				}
			}
			else if (currentStageVal == 3)	// currently in EX1
			{
				if (bEX2Open)
				{
					temp.incrementStageEnum();
					bEX1Open = true;
				}
			}
			else if (currentStageVal == 4)	// currently in EX2
			{
				if (bEX3Open)
				{
					temp.incrementStageEnum();
					bEX2Open = true;
				}
			}
			else if (currentStageVal == 5)	// currently in EX3
			{
				if (bEX4Open)
				{
					temp.incrementStageEnum();
					bEX3Open = true;
				}
			}
			else if (currentStageVal == 6)	// currently in EX4
			{
				if (bMEMOpen)
				{
					temp.incrementStageEnum();
					bEX4Open = true;
				}
			}
			else if (currentStageVal == 7)	// currently in MEM
			{
				if (bWBOpen)
				{
					temp.incrementStageEnum();
					bMEMOpen = true;
				}
			}
			else if (currentStageVal == 8)	// currently in WB
			{
				temp.incrementStageEnum();
				bWBOpen = true;
				// instruction is finished in pipeline
				// we need to remove it somehow and let the system know
			}
		}
		updateStageBooleans();
	}
	
	public void resetStageBooleans()
	{
		bIFOpen = true;
		bIDOpen = true;
		bEX1Open = true;
		bEX2Open = true;
		bEX3Open = true;
		bEX4Open = true;
		bMEMOpen = true;
		bWBOpen = true;
	}
	
	public void updateStageBooleans()
	{
		resetStageBooleans();
		for (int i = 0; i < InstructionsInPipeline.size(); i++)
		{
			instruction temp = InstructionsInPipeline.get(i);
			if (temp.getStage().ordinal() == 1)			// IF
			{
				bIFOpen = false;
			}
			else if (temp.getStage().ordinal() == 2)	// ID
			{
				bIDOpen = false;
			}
			else if (temp.getStage().ordinal() == 3)	// EX1
			{
				bEX1Open = false;
			}
			else if (temp.getStage().ordinal() == 4)	// EX2
			{
				bEX2Open = false;
			}
			else if (temp.getStage().ordinal() == 5)	// EX3
			{
				bEX3Open = false;
			}
			else if (temp.getStage().ordinal() == 6)	// EX4
			{
				bEX4Open = false;
			}
			else if (temp.getStage().ordinal() == 7)	// MEM
			{
				bMEMOpen = false;
			}
			else if (temp.getStage().ordinal() == 8)	// WB
			{
				bWBOpen = false;
			}
		}
	}
	
	// returns false if it don't work and we need to stall
	// returns true if we are allowed to load instruction
	public boolean loadNextInstructionIntoPipeline()
	{
		instruction temp = instructionHolder.get(instructionCounter);
		if (temp.getInstruction().equalsIgnoreCase("LI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			if (param2.indexOf("h") != -1)
			{
				int index = param2.indexOf("h");
				param2 = param2.substring(0, index);
			}
			else if (param2.contains("H"))
			{
				int index = param2.indexOf("H");
				param2 = param2.substring(0, index);
			}
			Integer decValue = Integer.parseInt(param2, 16);
			register reg = registers.get(param1);
			if (!reg.isLocked())
			{
				reg.setValue(decValue);
				reg.lock();
				return true;
			}
			else
			{
				return false;
			}
			
		}
		else if (temp.getInstruction().equalsIgnoreCase("LW"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String sourceRegString;
			String offsetString;
			register destReg = registers.get(param1);
			if (param2.indexOf("(") != -1)
			{
				int parensIdx = param2.indexOf("(");
				sourceRegString = param2.substring(parensIdx +1, param2.length()-1);
				offsetString = param2.substring(0, parensIdx);
				Integer offsetVal = Integer.parseInt(offsetString);
				int sourceRegVal = registers.get(sourceRegString).getValue();
				if ((offsetVal + sourceRegVal) % 4 == 0)
				{
					destReg.setValue(dataMap.get(offsetVal + sourceRegVal));
					return true;
				}
				else
				{
					return false;
				}
			}
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("SW"))	// not sure if this is right at all
		{														// i wrote it when i was half asleep at 1:13AM
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String destRegString;
			String offsetString;
			register sourceReg = registers.get(param1);
			if (param2.indexOf("(") != -1)
			{
				int parensIdx = param2.indexOf("(");
				destRegString = param2.substring(parensIdx +1, param2.length()-1);
				offsetString = param2.substring(0, parensIdx);
				Integer offsetVal = Integer.parseInt(offsetString);
				int destRegVal = registers.get(destRegString).getValue();
				if ((offsetVal + destRegVal) % 4 == 0)
				{
					dataMap.put(offsetVal + destRegVal, sourceReg.getValue());
					//sourceReg.setValue(dataMap.get(offsetVal + destRegVal));
					return true;
				}
				else
				{
					return false;
				}
			}
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("ADD"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal + thirdRegVal);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("ADDI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			if (param3.indexOf("h") != -1)
			{
				int index = param3.indexOf("h");
				param3 = param3.substring(0, index);
			}
			else if (param3.contains("H"))
			{
				int index = param3.indexOf("H");
				param3 = param3.substring(0, index);
			}
			Integer decValue = Integer.parseInt(param3, 16);
			register firstReg = registers.get(param1);
			register secondReg = registers.get(param2);
			firstReg.setValue(decValue + secondReg.getValue());
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("SUB"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal - thirdRegVal);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("SUBI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			if (param3.indexOf("h") != -1)
			{
				int index = param3.indexOf("h");
				param3 = param3.substring(0, index);
			}
			else if (param3.contains("H"))
			{
				int index = param3.indexOf("H");
				param3 = param3.substring(0, index);
			}
			Integer decValue = Integer.parseInt(param3, 16);
			register firstReg = registers.get(param1);
			register secondReg = registers.get(param2);
			firstReg.setValue(secondReg.getValue() - decValue);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("AND"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal & thirdRegVal);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("ANDI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			if (param3.indexOf("h") != -1)
			{
				int index = param3.indexOf("h");
				param3 = param3.substring(0, index);
			}
			else if (param3.contains("H"))
			{
				int index = param3.indexOf("H");
				param3 = param3.substring(0, index);
			}
			Integer decValue = Integer.parseInt(param3, 16);
			register firstReg = registers.get(param1);
			register secondReg = registers.get(param2);
			firstReg.setValue(secondReg.getValue() & decValue);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("OR"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal | thirdRegVal);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("ORI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			if (param3.indexOf("h") != -1)
			{
				int index = param3.indexOf("h");
				param3 = param3.substring(0, index);
			}
			else if (param3.contains("H"))
			{
				int index = param3.indexOf("H");
				param3 = param3.substring(0, index);
			}
			Integer decValue = Integer.parseInt(param3, 16);
			register firstReg = registers.get(param1);
			register secondReg = registers.get(param2);
			firstReg.setValue(secondReg.getValue() | decValue);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("SLL"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal << thirdRegVal);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("SRL"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal >> thirdRegVal);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("SRA"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal >>> thirdRegVal);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("SLLI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			if (param3.indexOf("h") != -1)
			{
				int index = param3.indexOf("h");
				param3 = param3.substring(0, index);
			}
			else if (param3.contains("H"))
			{
				int index = param3.indexOf("H");
				param3 = param3.substring(0, index);
			}
			Integer decValue = Integer.parseInt(param3, 16);
			int secondRegVal = registers.get(param2).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal << decValue);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("SRLI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			if (param3.indexOf("h") != -1)
			{
				int index = param3.indexOf("h");
				param3 = param3.substring(0, index);
			}
			else if (param3.contains("H"))
			{
				int index = param3.indexOf("H");
				param3 = param3.substring(0, index);
			}
			Integer decValue = Integer.parseInt(param3, 16);
			int secondRegVal = registers.get(param2).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal >> decValue);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("SRAI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			if (param3.indexOf("h") != -1)
			{
				int index = param3.indexOf("h");
				param3 = param3.substring(0, index);
			}
			else if (param3.contains("H"))
			{
				int index = param3.indexOf("H");
				param3 = param3.substring(0, index);
			}
			Integer decValue = Integer.parseInt(param3, 16);
			int secondRegVal = registers.get(param2).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal >>> decValue);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("BEQ"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			register firstReg = registers.get(param1);
			register secondReg = registers.get(param2);
			int iReg1 = firstReg.getValue();
			int iReg2 = secondReg.getValue();
			if (iReg1 == iReg2)
			{
				// what do we put here?
			}
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("BNE"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			register firstReg = registers.get(param1);
			register secondReg = registers.get(param2);
			int iReg1 = firstReg.getValue();
			int iReg2 = secondReg.getValue();
			if (iReg1 != iReg2)
			{
				// what do we put here?
			}
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("J"))
		{
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("MULT"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal * thirdRegVal);
			return true;
		}
		else if (temp.getInstruction().equalsIgnoreCase("MULTI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			if (param3.indexOf("h") != -1)
			{
				int index = param3.indexOf("h");
				param3 = param3.substring(0, index);
			}
			else if (param3.contains("H"))
			{
				int index = param3.indexOf("H");
				param3 = param3.substring(0, index);
			}
			Integer decValue = Integer.parseInt(param3, 16);
			register firstReg = registers.get(param1);
			register secondReg = registers.get(param2);
			firstReg.setValue(secondReg.getValue() * decValue);
			return true;
		}
		else
		{
			return false;
		}
	}
}
