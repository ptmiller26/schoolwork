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
	
	public ArrayList<String> RawInstructions, DataArray, justInstructionsArr, justParametersArr, justLabelsArr;
	public ArrayList<instruction> instructionArray;
	
	
	// constructor
	public simulator()
	{
		RawInstructions = new ArrayList<String>();		// holds assembly instructions
		DataArray = new ArrayList<String>();				// holds data
		justInstructionsArr = new ArrayList<String>();	// holds the MIPS instructions
		justParametersArr = new ArrayList<String>();		// holds the MIPS parameters
		justLabelsArr = new ArrayList<String>();			// holds the MIPS labels
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
			parsedArray = RawInstructions;
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
			buffReader = new BufferedReader(new FileReader(sFilename));

			while ((sCurrentLine = buffReader.readLine()) != null)
			{
				parsedArray.add(parseLine(sCurrentLine));
			}
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
		if (currentLine.contains("#"))
		{
			int commentIndex = currentLine.indexOf("#");
			currentLine = currentLine.substring(0, commentIndex);
		}
		return currentLine.trim();
	}
	
	void parseInstructions(eInstructionType eType)
	{
		String sCurrentLine;
		for (int i = 0; i < RawInstructions.size(); i++)
		{
			sCurrentLine = RawInstructions.get(i);
			if (sCurrentLine.contains(":"))
			{
				addLabels(sCurrentLine);	// adding the label to the current index
				String temp = sCurrentLine.substring(sCurrentLine.indexOf(":") + 1, sCurrentLine.length());
				addInstruction(temp);
			}
			else
			{
				justLabelsArr.add("");	// adding nothing to the current index
				addInstruction(sCurrentLine);
			}
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
			justInstructionsArr.add(temp);
			addParameters(temp.length(), line);
		}
		else if (line.equalsIgnoreCase("HLT"))
		{
			justInstructionsArr.add(line);
			justParametersArr.add("");	// adding nothing just to keep the array sizes uniform
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
		justParametersArr.add(temp);
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
		justLabelsArr.add(temp);	// adding the label
	}
	
	public void initInstructions()
	{
		instructionArray = new ArrayList<instruction>();
		for (int i = 0; i < justInstructionsArr.size(); i++)
		{
			String inst = justInstructionsArr.get(i);
			String params = justParametersArr.get(i);
			String label = justLabelsArr.get(i);
			
			instruction temp = new instruction(inst, params, label);
			instructionArray.add(temp);
		}
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
			parser.parseInstructions(eInstructionType.MIPSINSTRUCTION);
			parser.initInstructions();
			processor theProcessor = new processor(parser.instructionArray, parser.DataArray);
		}
	}
}
