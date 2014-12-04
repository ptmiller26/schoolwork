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
	
	// the king of them all
	ArrayList<instruction> InstructionsInPipeline;
	
	boolean bHitFirstHLT;
	boolean bHitSecondHLT;
	int instructionCounter;
//	private final InstructionFetch IF;
//	private final InstructionDecode ID;
//	private final Execute EX;
//	private final Memory MEM;
//	private final WriteBack WB;
	
	ArrayList<instruction> instructionHolder;
	ArrayList<String> dataHolder;
	
	HashMap<String, register> registers; 
	HashMap<Integer, Integer> dataMap;
	
	public processor(ArrayList<instruction> instructionArray, ArrayList<String> data)
	{
		InstructionsInPipeline = new ArrayList<instruction>();
		
		bHitFirstHLT = false;
		bHitSecondHLT = false;
		
		instructionHolder = instructionArray;
		dataHolder = data;
		
		instructionCounter = 0;
		registers = new HashMap<String, register>(); // represent r1-r31
		dataMap = new HashMap<Integer, Integer>();
		
		initRegisters();
		initData();
		
//		IF = new InstructionFetch();
//		ID = new InstructionDecode();
//		EX = new Execute();
//		MEM = new Memory();
//		WB = new WriteBack();
		
		while(!bHitSecondHLT)	// pmiller <----- need to update this condition
		{
			Tick();
		}
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
		int cycleCount = 0;
		
		if (addInstructionToPipeline())
		{
			instruction instructionToAdd = instructionHolder.get(instructionCounter);
			InstructionsInPipeline.add(instructionToAdd);
			instructionCounter++;
		}
		else
		{
			// we need to stall
		}
		
//		while (WB.isFinished() == false)
//		{
//			IF.Tick();
//			ID.Tick();
//			EX.Tick();
//			MEM.Tick();
//			WB.Tick();
//		}
		
		cycleCount++;
		//RegistersTick();
	}
	
	// returns false if it don't work and we need to stall like a muthafucka
	public boolean addInstructionToPipeline()
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
				temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
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
			temp.initStageEnum();
			return true;
		}
		else
		{
			return false;
		}
	}
	
//	public void RegistersTick()
//	{
//		
//	}
	
	
}
