/**
 * 
 */
package proj411;

import java.io.*;
import java.util.*;

enum eInstructionType 
{
	INSTRUCTION, DATA, MIPSINSTRUCTION, MIPSPARAMETERS, MIPSLABEL
}

/**
 * @author Patrick - testing for commit
 * Last Commit Test
 *
 */
public class simulator {
	
	public ArrayList<String> InstructionsArray, DataArray, MIPSInstructionsArray, MIPSParametersArray, MIPSLabelsArray;
	
	// constructor
	public simulator()
	{
		InstructionsArray = new ArrayList<String>();		// holds assembly instructions
		DataArray = new ArrayList<String>();				// holds data
		MIPSInstructionsArray = new ArrayList<String>();	// holds the MIPS instructions
		MIPSParametersArray = new ArrayList<String>();		// holds the MIPS parameters
		MIPSLabelsArray = new ArrayList<String>();			// holds the MIPS labels
	}
	
	
	/**
	 * Handles the analysis of a text/assembly file
	 * Stores the desired data to the appropriate data structure (based on boolean argument)
	 * @param sFilename
	 * @param bIsAssembly
	 * @throws IOException
	 */
	void readInputFile(String sFilename, eInstructionType eType) throws IOException
	{
		ArrayList<String> parsedArray = null;
		if (eType == eInstructionType.INSTRUCTION)
		{
			parsedArray = InstructionsArray;
		}
		else if (eType == eInstructionType.DATA)
		{
			parsedArray = DataArray;
		}
		
		File file = new File(sFilename);
		FileInputStream inputStream = null;
		//DataInputStream dataStream = null;	<--- pmiller - don't think i need this
		BufferedReader buffReader = null;
		
		try
		{
			String sCurrentLine;
			inputStream = new FileInputStream(file);
			//dataStream = new DataInputStream(inputStream);	<--- pmiller - don't think i need this
			buffReader = new BufferedReader(new FileReader(sFilename));

			while ((sCurrentLine = buffReader.readLine()) != null)
			{
				parsedArray.add(parseLine(sCurrentLine));
			}
//			for (int i = 0; i < parsedArray.size(); ++i)
//			{
//				System.out.println(parsedArray.get(i).toString());
//			}
			inputStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	
//	static void writeToOutputFile()		<--- pmiller - write this at the very end
//	{
//		
//	}
	
	
	/**
	 * Parses a line of text
	 * 	- removes tabs
	 * 	- removes all spaces <---- pmiller - take this out
	 * 	- if a comment exists, it cuts it off
	 * @param currentLine
	 * @return a cleaner/simplified currentLine
	 */
	String parseLine(String currentLine)
	{
		currentLine = currentLine.replaceAll("\t", "");	// removing all tabs
		//currentLine = currentLine.replaceAll(" ", "");	// removing all spaces
		if (currentLine.contains("#"))
		{
			int commentIndex = currentLine.indexOf("#");
			currentLine = currentLine.substring(0, commentIndex);
		}
		return currentLine.trim();
	}
	
	void decodeInstructions(eInstructionType eType)
	{
		//ArrayList<String> TemporaryArray = new ArrayList<String>();
//		switch (eType) 
//		{
//			case MIPSINSTRUCTION:
//				TemporaryArray = MIPSInstructionsArray;
//			case MIPSPARAMETERS:
//				TemporaryArray = MIPSParametersArray;
//			case MIPSLABEL:
//				TemporaryArray = MIPSLabelsArray;
//			default: 
//		}
		
		String sCurrentLine;
		for (int i = 0; i < InstructionsArray.size(); i++)
		{
			sCurrentLine = InstructionsArray.get(i);
			if (sCurrentLine.contains(":"))
			{
				addLabels(sCurrentLine);	// adding the label to the current index
				String temp = sCurrentLine.substring(sCurrentLine.indexOf(":") + 1, sCurrentLine.length());
				addInstruction(temp);
			}
			else
			{
				MIPSLabelsArray.add("");	// adding nothing to the current index
				addInstruction(sCurrentLine);
			}
			//addInstruction(sCurrentLine);
			//addParameters(sCurrentLine);
		}
	}
	
	void addInstruction(String line)
	{
		String temp = "";
		line = line.trim();
		if (line.indexOf(" ") != -1)	// dealing with spaces
		{
			temp = line.substring(0, line.indexOf(" "));
		}
		if (line.indexOf("\t") != -1)	// dealing with tab characters
		{
			temp = line.substring(0, line.indexOf("\t"));
		}
		if (isValidInstruction(temp))
		{
			MIPSInstructionsArray.add(temp);
			addParameters(temp.length(), line);
		}
		else if (line.equalsIgnoreCase("HLT"))
		{
			MIPSInstructionsArray.add(line);
			MIPSParametersArray.add("");	// adding nothing just to keep the array sizes uniform
		}
	}
	
	
	static boolean isValidInstruction(String instruction)
	{
		if (instruction.equalsIgnoreCase("AND") ||
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
				instruction.equalsIgnoreCase("ADDI") ||
				instruction.equalsIgnoreCase("SUB") ||
				instruction.equalsIgnoreCase("SUBI") ||
				instruction.equalsIgnoreCase("MULT") ||
				instruction.equalsIgnoreCase("MULTI"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	void addParameters(int startingIndex, String line)
	{
		String temp;
		temp = line.substring(startingIndex, line.length());
		temp = temp.trim();
		MIPSParametersArray.add(temp);
	}
	
	void addLabels(String line)
	{
		String temp = "";
		if (line.indexOf(" ") != -1)	// dealing with spaces
		{
			temp = line.substring(0, line.indexOf(" "));
		}
		if (line.indexOf("\t") != -1)	// dealing with tab characters
		{
			temp = line.substring(0, line.indexOf("\t"));
		}
		MIPSLabelsArray.add(temp);	// adding the label
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException 
	{
		if (args.length != 3)
		{
			System.out.println("Must enter correct number of arguments");
		}
		else
		{
			simulator parser = new simulator();
			parser.readInputFile(args[0], eInstructionType.INSTRUCTION);	// parsing the assembly instructions
			parser.readInputFile(args[1], eInstructionType.DATA);	// parsing the data
			parser.decodeInstructions(eInstructionType.MIPSINSTRUCTION);
//			for (int i = 0; i < parser.MIPSInstructionsArray.size(); ++i)
//			{
//				System.out.println(parser.MIPSInstructionsArray.get(i).toString());
//			}
//			for (int i = 0; i < parser.MIPSParametersArray.size(); ++i)
//			{
//				System.out.println(parser.MIPSParametersArray.get(i).toString());
//			}
//			for (int i = 0; i < parser.MIPSLabelsArray.size(); ++i)
//			{
//				if (parser.MIPSLabelsArray.get(i) == null)
//				{
//					System.out.println("null");
//				}
//				else
//				{
//					System.out.println(parser.MIPSLabelsArray.get(i).toString());
//				}
//			}
			
			instruction[] testArray = new instruction[11];

			for (int i = 0; i < 11; i++)
			{
				testArray[i] = new instruction(parser.MIPSInstructionsArray.get(i).toString(),
						parser.MIPSParametersArray.get(i).toString(),
						parser.MIPSLabelsArray.get(i).toString());
				System.out.print(testArray[i].getInstruction() + " ");
				System.out.print(testArray[i].getFirstParameter() + " ");
				System.out.print(testArray[i].getSecondParameter() + " ");
				System.out.println(testArray[i].getThirdParameter());
			}
//			System.out.println(parser.MIPSInstructionsArray.size());
//			System.out.println(parser.MIPSParametersArray.size());
//			System.out.println(parser.MIPSLabelsArray.size());
//			System.out.println(parser.InstructionsArray.size());
//			System.out.println(parser.DataArray.size());
		}
	}
}
