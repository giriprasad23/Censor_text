package meltwater;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeltWater_Censor_Indexing {

	public static void main(String args[]) throws IOException
	{
		final String inputfile ="Input.txt";
		final String censoredfile = "Censored.txt";
		final String outputFile = "outputFile.txt";
		String final_Output;

		Map<Integer, String> document_Input_Strings = Collections.synchronizedMap(new LinkedHashMap<Integer, String>());
		
		Map<Integer, String> censored_Input_Strings = Collections.synchronizedMap(new LinkedHashMap<Integer, String>());
		
		LinkedList<Integer> global_index = new LinkedList<Integer>();
		
		MeltWater_Censor_Indexing mwObj = new MeltWater_Censor_Indexing();
		
		//Read file function
		censored_Input_Strings = MeltWater_Censor_Indexing.readWriteFile(censoredfile, "readCensored", global_index,  null);
		
		document_Input_Strings = MeltWater_Censor_Indexing.readWriteFile(inputfile, "readDocument", global_index, null);
		
		final_Output = MeltWater_Censor_Indexing.censor_File(censored_Input_Strings, document_Input_Strings, global_index );
		
		MeltWater_Censor_Indexing.readWriteFile(outputFile, "write",null,  final_Output);
		
		System.out.println("The final output string : " + final_Output);
		
		System.out.println("Indexed file : "+ global_index);
		
		System.out.println("Censored words : " + censored_Input_Strings);

	}
	
	private static Map<Integer, String> readWriteFile(String fileName, String operation,LinkedList<Integer> file_index, String data) throws IOException
	{
		List<String> output_String_List = new ArrayList<String>();
		Map<Integer, String> output_String_Table = Collections.synchronizedMap(new LinkedHashMap<Integer, String>());
	
		if(operation.equalsIgnoreCase("readDocument"))
		{

		try {
			
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			byte[] stringdata = new byte[(int) file.length()];
			fis.read(stringdata);
			fis.close();

			String process_data = new String(stringdata, "UTF-8");
			
				Matcher m = Pattern.compile("(\\S*([^\"|\'])\\S*|\".+?\"|\')(\\s*)").matcher(process_data);
				while (m.find())
					output_String_List.add(m.group(1)); 

				
				for(int i=0;i<output_String_List.size();i++)
				{
					
					 String clean = output_String_List.get(i);
					 clean = clean.replaceAll("[^a-zA-Z ]", "");

					String[] temp_comma_sep = clean.replaceAll("^[,\\s]+", "").split("[,\\s]+");
					 
					 for(int k=0;k<temp_comma_sep.length;k++)
					 {
							 Random rand = new Random(); 
							 int random_num = rand.nextInt(1000000); 
							 if (output_String_Table.containsValue(temp_comma_sep[k]))
							 {
								 file_index.add(get_key_value(output_String_Table, temp_comma_sep[k]));
							 }
							 else
							 {
								 output_String_Table.put(random_num, temp_comma_sep[k]);
								 file_index.add(random_num);
							 }
					 }
				}
				

		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		
		else if(operation.equalsIgnoreCase("readCensored"))
		{
			BufferedReader br = null;

			try {
				
				String sCurrentLine;

				br = new BufferedReader(new FileReader(fileName));
				
				while ((sCurrentLine = br.readLine()) != null) {
					Matcher m = Pattern.compile("(\\S*([^\"|\'])\\S*|\".+?\"|\')(\\s*)").matcher(sCurrentLine);
					while (m.find())
						output_String_List.add(m.group(1)); 

					
					for(int i=0;i<output_String_List.size();i++)
					{
						
						 String clean = output_String_List.get(i);
							clean = clean.replaceAll("[^a-zA-Z ]", "");

						String[] temp_comma_sep = clean.replaceAll("^[,\\s]+", "").split("[,\\s]+");
						 
						 for(int k=0;k<temp_comma_sep.length;k++)
						 {
							  Random rand = new Random(); 
							  int random_num = rand.nextInt(1000000);
							  output_String_Table.put(random_num, temp_comma_sep[k]);
						 }
					}
					
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		else if(operation.equalsIgnoreCase("write"))
		{
			
			 BufferedWriter bw = null;
		      try {

		         //Specify the file name and path here
			 File file = new File(fileName);

			  if (!file.exists()) {
			     file.createNewFile();
			  }

			  FileWriter fw = new FileWriter(file);
			  bw = new BufferedWriter(fw);
			  bw.write(data);
		          System.out.println("File written Successfully");

		      } catch (IOException ioe) {
			   ioe.printStackTrace();
			}
			finally
			{ 
			   try{
			      if(bw!=null)
				 bw.close();
			   }catch(Exception ex){
			       System.out.println("Error in closing the BufferedWriter"+ex);
			    }
			}
		}
		else
		{
			System.out.println("Invalid File operation");
		}
		
		return output_String_Table;
	}
	
	private static String censor_File(Map<Integer, String> censored_File, Map<Integer, String> main_Document, LinkedList<Integer> index_file){
		
		StringBuffer final_Output = new StringBuffer("");
		int counter = 0;
		
		//Iterate through document to find any censored words
		
		for(int i=0;i<index_file.size();i++)
		{
			
			if (censored_File.containsValue(main_Document.get(index_file.get(i))))
			{
				String censored_String = main_Document.get(index_file.get(i)).replaceAll("(?s).", "X");
				final_Output.append(" "+censored_String+" ");
				counter++;
			}
			else
			{
				final_Output.append(" "+ main_Document.get(index_file.get(i)) + " ");
			}
			
		}
		
		System.out.println("Number of words replaced here " + counter);
		return final_Output.toString();
	}
	
	private static Integer get_key_value(Map<Integer, String> input_map, String str_value)
	{
		
	    Integer key= 0;
	    String value= str_value;
		
		for(Map.Entry entry: input_map.entrySet()){
            if(value.equals(entry.getValue())){
                key = (Integer)entry.getKey();
                break; //breaking because its one to one map
            }
        }
		
		return key;
	}

}
