package edu.mcw.rgd.nlp.utils.ncbi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadWrite {

	public static String read(String path) {
		try{
			File f=new File(path);
			Scanner file= new Scanner(f);
			StringBuffer out=new StringBuffer();
			while(file.hasNextLine()){
				String l=file.nextLine();
				out.append(l+"\n");
			}
			return out.toString();
		}
		catch(IOException e){
			System.out.println("File "+path+" error");
			throw new RuntimeException(e);
		}
	}
	
	public static ArrayList<String> readAll(String path, String ext) {
		try{
			ArrayList<String> list=new ArrayList<String>();
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
			    if (file.isFile() && file.getName().contains("."+ext)) {
			    	String fileName=file.getName();
			    	list.add(read(path+"/"+fileName));
			    	fileName=fileName.replace("."+ext, "");
			    	list.add(fileName);
			    }
			}
			System.out.println(list.size()+ " files have been read");
			return list;
		}
		catch(Exception e){
			System.out.println("File "+path+" error");
			throw new RuntimeException(e);
		}
	}
	
	public static ArrayList<String> readAll(String path) {
		try{
			ArrayList<String> list=new ArrayList<String>();
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
			    if (file.isFile()) {
			    	list.add(read(path+"/"+file.getName()));
			    }
			}
			System.out.println(list.size()+ " files have been read");
			return list;
		}
		catch(Exception e){
			System.out.println("File "+path+" error");
			throw new RuntimeException(e);
		}
	}
	public static void write(String text, String path){
		try{
			FileWriter fw=new FileWriter(path);
			fw.write(text);
			fw.close();
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}

}
