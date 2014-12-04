/**
 * 
 */
package proj411;

import java.math.BigInteger;
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
	
	int instructionCounter;
	private final InstructionFetch IF;
	private final InstructionDecode ID;
	private final Execute EX;
	private final Memory MEM;
	private final WriteBack WB;
	
	ArrayList<instruction> instructionHolder;
	ArrayList<String> dataHolder;
	//ArrayList<String> parameterHolder;
	//ArrayList<String> labelHolder;
	
	HashMap<String, register> registers; 
	HashMap<Integer, Integer> dataMap;
	//register[] registerArray = new register[32];	// represent r1-r31
	

//	public processor(ArrayList<String> instructions, ArrayList<String> parameters, 
//			ArrayList<String> data, ArrayList<String> labels)
	public processor(ArrayList<instruction> instructionArray, ArrayList<String> data)
	{
		instructionHolder = instructionArray;
		dataHolder = data;
		//parameterHolder = parameters;
		//labelHolder = labels;
		
		instructionCounter = 0;
		registers = new HashMap<String, register>(); // represent r1-r31
		dataMap = new HashMap<Integer, Integer>();
		
		initRegisters();
		initData();
		
		IF = new InstructionFetch();
		ID = new InstructionDecode();
		EX = new Execute();
		MEM = new Memory();
		WB = new WriteBack();
		
		Tick();
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
			dataMap.put(i+100, temp);
		}
	}
	
	public void Tick()
	{
		int cycleCount = 0;
		
		addInstructionToPipeline();
		
		while (WB.isFinished() == false)
		{
			IF.Tick();
			ID.Tick();
			EX.Tick();
			MEM.Tick();
			WB.Tick();
		}
		
		cycleCount++;
		RegistersTick();
	}
	
//	public int getDecimalValue(String hexValue)
//	{
//		
//	}
	
	public void addInstructionToPipeline()
	{
		instruction temp = instructionHolder.get(instructionCounter);
		if (temp.getInstruction().equalsIgnoreCase("LI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			temp.initStageEnum();
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
			reg.setValue(decValue);
		}
		else if (temp.getInstruction().equalsIgnoreCase("LW"))
		{
			
		}
		else if (temp.getInstruction().equalsIgnoreCase("SW"))
		{
			
		}
		else if (temp.getInstruction().equalsIgnoreCase("ADD"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal + thirdRegVal);
		}
		else if (temp.getInstruction().equalsIgnoreCase("ADDI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
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
		}
		else if (temp.getInstruction().equalsIgnoreCase("SUB"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal - thirdRegVal);
		}
		else if (temp.getInstruction().equalsIgnoreCase("SUBI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
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
		}
		else if (temp.getInstruction().equalsIgnoreCase("AND"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal & thirdRegVal);
		}
		else if (temp.getInstruction().equalsIgnoreCase("ANDI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
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
		}
		else if (temp.getInstruction().equalsIgnoreCase("OR"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal | thirdRegVal);
		}
		else if (temp.getInstruction().equalsIgnoreCase("ORI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
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
		}
		else if (temp.getInstruction().equalsIgnoreCase("SLL"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal << thirdRegVal);
		}
		else if (temp.getInstruction().equalsIgnoreCase("SRL"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal >> thirdRegVal);
		}
		else if (temp.getInstruction().equalsIgnoreCase("SRA"))
		{
			// what is this arithmetic bullshit?
		}
		else if (temp.getInstruction().equalsIgnoreCase("SLLI"))
		{
			// what is shamt?
		}
		else if (temp.getInstruction().equalsIgnoreCase("SRLI"))
		{
			// what is shamt?
		}
		else if (temp.getInstruction().equalsIgnoreCase("SRAI"))
		{
			// what is shamt?
		}
		else if (temp.getInstruction().equalsIgnoreCase("BEQ"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
			register firstReg = registers.get(param1);
			register secondReg = registers.get(param2);
			int iReg1 = firstReg.getValue();
			int iReg2 = secondReg.getValue();
			if (iReg1 == iReg2)
			{
				// what do we put here?
			}
		}
		else if (temp.getInstruction().equalsIgnoreCase("BNE"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
			register firstReg = registers.get(param1);
			register secondReg = registers.get(param2);
			int iReg1 = firstReg.getValue();
			int iReg2 = secondReg.getValue();
			if (iReg1 != iReg2)
			{
				// what do we put here?
			}
		}
		else if (temp.getInstruction().equalsIgnoreCase("J"))
		{
			
		}
		else if (temp.getInstruction().equalsIgnoreCase("MULT"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
			int secondRegVal = registers.get(param2).getValue();
			int thirdRegVal = registers.get(param3).getValue();
			register reg = registers.get(param1);
			reg.setValue(secondRegVal * thirdRegVal);
		}
		else if (temp.getInstruction().equalsIgnoreCase("MULTI"))
		{
			String param1 = temp.getFirstParameter();
			String param2 = temp.getSecondParameter();
			String param3 = temp.getThirdParameter();
			temp.initStageEnum();
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
		}
		else
		{
			
		}
		
	}
	
	public void RegistersTick()
	{
		
	}
	
	
}
