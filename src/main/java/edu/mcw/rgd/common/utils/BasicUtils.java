package edu.mcw.rgd.common.utils;


import java.io.PrintWriter;
import java.io.StringWriter;


public class BasicUtils {

	public static String strExceptionStackTrace(Exception e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}
	public static String removeComments(String inputStr) {
		String returnStr = inputStr.replaceAll("(?ms)/\\*.+?\\*/", "");
		returnStr = returnStr.replaceAll("(?ms)^\\s*//.+?$", "");
		return returnStr;
	}

	public static String removeEmptyLines(String inputStr) {
		String returnStr = inputStr.replaceAll("(?ms)^\\s*", "");
		return returnStr;
	}

	public static void main(String[] args) {
//		String a = "db=gene db=gene Cmd=Go Cmd=Go Term=(pc12 bdnf) AND \"Rattus norvegicus\"[porgn:__txid10116] Term=(pc12+bdnf)+AND+\"Rattus+norvegicus\"[porgn:__txid10116]";
//		System.out.println(BasicUtils.getNCBIQueryPara(a, "Db"));
		String a = "adfdf\r\n";
		a += "/* dkfjdkf \r\n";
		a += "kf*/ \r\n";
		a += "122333 \r\n";
		a += " \r\n";
		a += "  //122333 \r\n";
		a += "  wweehttp://122333 \r\n";
		a += "//122333 \r\n";
		a += "wwww122333 \r\n";
		a += " \r\n";
		a += "/* dkfjdkf \r\n";
		a += "kf*/ \r\n";
		a += "fffff122333 \r\n";
		a += " \r\n";
		a += " \r\n";
		System.out.println("-----");
		System.out.println(BasicUtils.removeEmptyLines(BasicUtils.removeComments(a)));
		System.out.println("-----");
	}
}
