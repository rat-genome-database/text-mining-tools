package edu.mcw.rgd.common.mapreduce.ncbi.pubmed;

import edu.mcw.rgd.database.ncbi.pubmed.PmcArticleDAO;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsingTest {
    private static Pattern articlePattern = Pattern.compile("(<ns1:article.+?>.+?</ns1:article>)");
    private static Pattern pmidPattern = Pattern.compile("<ns1:article-id pub-id-type=\"pmc\">(.+?)</ns1:article-id>");
    public static boolean getPmcSections(String xmlStr){
        if (xmlStr == null || xmlStr.length() == 0) return false;
        Document xmlDoc= Jsoup.parse(xmlStr, "", Parser.xmlParser());
        List<String> sectionTitles = new ArrayList<>();

        for(Element e:xmlDoc.getElementsByTag("ns1:article")){
            for (Element e1:e.getElementsByTag("ns1:body")){
                for (Element e2:e1.getElementsByTag("ns1:sec")){
                    sectionTitles.add(e2.getElementsByTag("ns1:title").toString());
                }
            }
        }
//        for (String i: sectionTitles){
//            System.out.println(i);
//        }
        //System.out.println(sectionTitles.size());
        return true;
    }

    //func that returns section (start, end) string

    public static void main(String[] args){

    }

    //THESE FUNCTIONS NOT FINISHED
    public static String sectionColumn(Document xmlDoc){
        int charCounter = 0;
        String section = "";
        int count = 0;
        int length;

        StringBuffer out = new StringBuffer();

        return out.toString();
    }
    public static String bodySections(Document xmlDoc){
        StringBuffer sectionText = new StringBuffer();
        String section;
        int num_p_tags;
        int num_p_sections;
        int count = 0;
        int pcounter;
        int scounter;
        int length;
        for (Element e : xmlDoc.getElementsByTag("ns1:article")){
            for (Element e1: e.getElementsByTag("ns1:body")){
                for (Element e2: e1.children()){
                    System.out.println(e2.tagName());
                    if (e2.tagName().equals("ns1:p")){
                        section = "Body";
                        length = PmcArticleDAO.textSifter(e2).length();
                        //System.out.println(out);
                        if (length > 0){
                            sectionText.append(section + "{"+count+", "+ (count+length) +"}, ");
                            System.out.println("BODY TEXT ADDED");
                            count += (length+1);
                        }

                    } else if (e2.tagName().equals("ns1:sec")){
                        length = 0;
                        section = "Body";
                        num_p_tags = e2.getElementsByTag("ns1:p").size();

                        System.out.println(num_p_tags);
                        pcounter = 0;

                        for (Element e3: e2.children()){
                            if (e3.tagName().equals("ns1:title")){
                                section = e3.ownText();
                            }
                            length += (PmcArticleDAO.textSifter(e3).length()-1);
                            if (e3.tagName().equals("ns1:p") ){
                                pcounter++;
                                System.out.println(pcounter);
                                if (pcounter == num_p_tags){

                                    sectionText.append(section + "{" + count + ", " + (count+length) + "}, ");
                                    count += (length + 1);
                                    length = 0;

                                }
                            }
                            else if (e3.tagName().equals("ns1:sec")){
                                num_p_sections = e3.getElementsByTag("ns1:p").size();
                                System.out.println(num_p_tags);
                                scounter = 0;

                                for (Element e4: e3.children()){
                                    length += (PmcArticleDAO.textSifter(e4).length()-1);
                                    if (e4.tagName().equals("ns1:p") ){
                                        scounter++;
                                        System.out.println(scounter);
                                        if (scounter == num_p_sections){
                                            //sectionText.append(section + "{" + count + ", " + (count+length) + "}, ");
                                            //count += (length + 1);
                                            //length = 0;
                                        }
                                    }

                                }
                                sectionText.append(section + "{" + count + ", " + (count+length) + "}, ");
                                count += (length + 1);
                                length = 0;
                            }

                        }
                    }
                }
            }
        }
        int lastIndex = sectionText.toString().lastIndexOf(", ");

        System.out.println(PmcArticleDAO.getArticleBody(xmlDoc).length());
        if (lastIndex >= 0) {
            return sectionText.toString().substring(0, lastIndex) + sectionText.substring(lastIndex + 2);
        }

        return sectionText.toString();
    }

    /*
     public static String abstractSections(Document xmlDoc){
//        return "abstract: {" + 0 + ", " + PmcArticleDAO.getArticleAbstract(xmlDoc).length() + "}";
//    }
    public static String abstractSections(Document xmlDoc) {
    StringBuffer out = new StringBuffer();
    StringBuffer sectionText = new StringBuffer();
    String section;
    int count = 0;
    int length = 0;
        for (Element e : xmlDoc.getElementsByTag("ns1:article"))
            for (Element e1 : e.getElementsByTag("ns1:abstract")){
                for (Element e2: e1.children()){
                    section = "Abstract";
                    length = PmcArticleDAO.textSifter(e2).replaceAll("\\(\\s*[,-;–\\s]*\\)","").replaceAll("\\[\\s*[,-;–\\s]*\\]","").trim().length()-count-1;
                    //System.out.println(out);
                    if (length > 0){
                        sectionText.append(section + "{"+count+", "+ (count+length) +"}, ");
                        System.out.println("ABSRACT ADDED");
                        count += (length+1);
                    }
                    if (e1.getElementsByTag("ns1:sec").size() > 0) {
                        length = 0;
                        for (Element e3: e2.children()){
                            if (e3.tagName().equals("ns1:title")){
                                section = e3.ownText();
                            }
                            length += (PmcArticleDAO.textSifter(e3).replaceAll("\\(\\s*[,-;–\\s]*\\)","").replaceAll("\\[\\s*[,-;–\\s]*\\]","").length()-count-1);
                            //System.out.println(out);
                            if (e3.tagName().equals("ns1:p")){
                                sectionText.append(section + "{" + count + ", " + (count+length) + "}, ");
                                count += (length + 1);
                                length = 0;
                            }
                        }
                    }
                }
            }
        int lastIndex = sectionText.toString().lastIndexOf(", ");

        System.out.println(PmcArticleDAO.getArticleAbstract(xmlDoc).length());
        if (lastIndex >= 0) {
            // Replace the last comma with an empty string
            return sectionText.toString().substring(0, lastIndex) + sectionText.substring(lastIndex + 2);
        }

    return sectionText.toString();
    } */

    public static String tableSections(Document xmlDoc){
        return null;
    }
    public static String figureSections(Document xmlDoc){
        return null;
    }


}
