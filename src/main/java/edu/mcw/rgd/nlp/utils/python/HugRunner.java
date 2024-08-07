package edu.mcw.rgd.nlp.utils.python;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HugRunner {

    public String rootDir = "";

    public HugRunner(String rootDirectory) {

        this.rootDir = rootDirectory;
    }

    public  String getTermAccession(String term) throws Exception {
        //System.out.println("processing " + type);

        ProcessBuilder processBuilder = new ProcessBuilder(rootDir + "/bertEnv/bin/python", rootDir + "/bert/term_n_synonym_match_query.py", term);
        Process process = processBuilder.start();
        process.waitFor();

        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String e = "";
               while ((e = stdError.readLine()) != null) {
                System.out.println(e);
             }

        BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String sout = "";
        String terms = "";
               while ((sout = stdOut.readLine()) != null) {
                   terms+=sout;
             }

        return terms;

    }


    public String runRaw(String type, String pubmedId, String text) throws Exception {
        System.out.println("processing " + type);

        String fileId = pubmedId;
        if (!Files.exists(Paths.get(rootDir + "/bert/tmp/" + fileId))) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(rootDir + "/bert/tmp/" + fileId));
            writer.write(text);
            writer.close();
        }


//        ProcessBuilder processBuilder = new ProcessBuilder("conda", "run", "-n", "ai", "python", rootDir + "/ai/bert/annotate.py", pubmedId, type, rootDir);
        ProcessBuilder processBuilder = new ProcessBuilder(rootDir + "/bertEnv/bin/python", rootDir + "/bert/annotate.py", pubmedId, type, rootDir,"1");
        Process process = processBuilder.start();
        process.waitFor();

        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String er = "";
               while ((er = stdError.readLine()) != null) {
                System.out.println(er);
             }

        BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String or = "";
        while ((or = stdOut.readLine()) != null) {
            System.out.println(or);
        }


        String fileData = "";
        try {
            File myObj = new File(rootDir + "/bert/tmp/" + fileId + "." + type + ".out");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                fileData += myReader.nextLine() + "\n";
            }
            myReader.close();
        }catch(Exception e) {

        }
        return fileData;

    }


    public HashMap<String, ArrayList<String>> runParsed(String type, String pubmedId, String text) throws Exception {

        String fileData = runRaw(type, pubmedId, text);

        LinkedHashMap<String, ArrayList<String>> hm = new LinkedHashMap<String, ArrayList<String>>();
        if (fileData.startsWith("NA")) {
            return hm;
        }

        String[] rows = fileData.split("\\n");

        for (int i = 0; i < rows.length; i++) {

            String row = rows[i];

            String[] cols = row.split("\\|\\|");

            if (cols.length < 2) continue;

            String term = cols[0];
            String start = cols[1];
            String end = cols[2];
            ArrayList<String> ontIds = new ArrayList<String>();

            if (cols.length > 3) {
                String accIdString = cols[3];
                String[] accIds = accIdString.split("!");
                if (accIds.length > 0) {
                    for (String acc: accIds) {
                        String accType = acc.substring(0,acc.indexOf(":"));

                        if (type.equals("Disease") && (accType.equals("DOID") || accType.equals("RDO"))) {
                            ontIds.add(acc);
                        } else if (type.equals("Phenotype") && accType.equals("MP")) {
                            ontIds.add(acc);

                        } else if (type.equals("Cell-Type") && accType.equals("CL")) {
                            ontIds.add(acc);

                        } else if (type.equals("Chemical") && accType.equals("CHEBI")) {
                            ontIds.add(acc);

                        } else if (type.equals("Anatomical") && accType.equals("MA")) {
                            ontIds.add(acc);

                        } else if (type.equals("Cell-Component") && accType.equals("GO")) {
                            ontIds.add(acc);

                        } else if (type.equals("Bioprocess") && accType.equals("GO")) {
                            ontIds.add(acc);

                        } else if (type.equals("Gene")) {

                        } else if (type.equals("Organism")) {

                        }
                    }
                }
            }

            if (hm.containsKey(term)) {
                ArrayList tmp = hm.get(term);
                tmp.add(start + "-" + end);
                hm.put(term, tmp);
                hm.put(term + "-ontId",ontIds);
            } else {
                ArrayList tmp = new ArrayList();
                tmp.add(start + "-" + end);
                //System.out.println("adding " + term + " with start stop " + start + "-" + end);
                hm.put(term,tmp);
                hm.put(term + "-ontId",ontIds);
            }

        }
        return hm;
    }



    public HashMap<String, ArrayList<String>> runStructured(String type, String pubmedId, String text) throws Exception {

        String fileData = runRaw(type, pubmedId, text);

        String[] rows = fileData.split("\\n");

        String term = "";
        String start= "";
        String end = "";
        int lastStop=0;


        String rowInit = rows[0];
        String[] colsInit = rowInit.split("!!");
        boolean first=true;

        LinkedHashMap<String, ArrayList<String>> hm = new LinkedHashMap<String, ArrayList<String>>();
        if (rowInit != null && !rowInit.trim().equals("")) {
            term = colsInit[3];
            start = colsInit[4];
            end = colsInit[5];
        }

        String row = rows[0];


        for (int i = 1; i < rows.length; i++) {
            //System.out.println("term " + term + " start " + start + " end " + end + " laststop " + lastStop);

            String[] cols = row.split("!!");
            if (cols.length < 2) continue;
            if (cols[0].equals("I") || cols[3].indexOf("#") != -1 || Integer.parseInt(cols[4]) == lastStop) {
                if (Integer.parseInt(cols[4]) == lastStop) {
                    term +=cols[3].replaceAll("#","");
                }else {
                    term +=" " + cols[3].replaceAll("#","");
                }

            } else if (cols[0].equals("B")) {

                if (!first) {
                    if (hm.containsKey(term)) {
                        ArrayList tmp = hm.get(term);
                        tmp.add(start + "-" + end);
                        hm.put(term, tmp);
                    } else {
                        ArrayList tmp = new ArrayList();
                        tmp.add(start + "-" + end);
                        //System.out.println("adding " + term + " with start stop " + start + "-" + end);
                        hm.put(term,tmp);
                    }

                }
                first=false;
                term=cols[3];
                start = cols[4];
                end= cols[5];
            }
            lastStop=Integer.parseInt(cols[5]);
            row = rows[i];

        }

        return hm;

    }



    public Set<String> runJob(String type, String pubmedId, String text) throws Exception{


        String fileData = runRaw(type, pubmedId, text);

        String[] rows = fileData.split("\\n");

        String term="";

        LinkedHashMap<String,String> hm = new LinkedHashMap<String,String>();
        for (int i=0;i<rows.length;i++) {
            String row = rows[i];

            String[] cols = row.split("!!");
            if (cols.length < 2) continue;
            if (cols[0].equals("I") || cols[3].indexOf("#") != -1) {
                if (cols[3].indexOf("#") == -1) {
                    term += " " + cols[3];
                }else {
                    term +=cols[3].replaceAll("#","");
                }


            }else if (cols[0].equals("B")) {
                hm.put(term,null);
                term=cols[3];
            }
        }

        return hm.keySet();

    }
}
