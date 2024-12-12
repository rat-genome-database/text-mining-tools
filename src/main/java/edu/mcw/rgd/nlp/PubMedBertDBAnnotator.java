package edu.mcw.rgd.nlp;

import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.nlp.datamodel.ResearchArticle;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedJSoupDoc;
import edu.mcw.rgd.nlp.utils.python.HugRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PubMedBertDBAnnotator extends Thread{

    public String rootDir;
    public String articleFile;
    public String articleDir;
    public HugRunner hg;
    public int threads = 0;
    public String llm;
    public String ontomateEndpoint;

    public static AtomicInteger totalProcessed=new AtomicInteger(0);

    public PubMedBertDBAnnotator(String rootDir, String articleDir, String articleFile, String llm, String ontomateEndpoint) {
        this.rootDir = rootDir;
        this.articleDir = articleDir;
        this.articleFile = articleFile;
        this.hg = new HugRunner(rootDir);
        this.llm = llm;
        this.ontomateEndpoint = ontomateEndpoint;
    }

   /* public PubMedBertAnnotator(String rootDir, String articleDir, int threads, String llm) {
        this.rootDir = rootDir;
        this.articleFile = articleDir;
        this.hg = new HugRunner(rootDir);
        this.threads=threads;
        this.llm = llm;
    }
*/
    public void run() {

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
                GeneDAO gdao = new GeneDAO();

                Connection conn = gdao.getConnection();

                String query = "select * from pubmed_article where pmid in (38314234,38314233,38314230)";

                Statement s = conn.createStatement();
                ResultSet rs = s.executeQuery(query);



                int count = 1;
                while (rs.next()) {

                    String pmid = rs.getString("pmid");
                    System.out.println(sdf.format(new Date()) + " processing " + pmid + " **************************************************");

                    String abstractText = rs.getString("abstract");
                    String meshTerms = rs.getString("mesh_terms");

                    count++;
                    //ra = this.loadGenes(ra);

                    HashMap<String, ArrayList<String>> modValues = this.runModel("Gene", pmid, abstractText, meshTerms);
                    System.out.println("gene counts = " + modValues.get("counts"));
                    System.out.println("gene Terms = " + modValues.get("terms"));
                    System.out.println("gene pos = " + modValues.get("pos"));
                }

                    /*
                    ra = this.loadDO(ra);
                    ra = this.loadBP(ra);
                    ra= this.loadCC(ra);
                    ra = this.loadChebi(ra);
                    ra = this.loadMA(ra);
                    ra = this.loadMMO(ra);
                    ra = this.loadMP(ra);
                    ra = this.loadSO(ra);
                    ra = this.loadHP(ra);
                    ra = this.loadNBO(ra);
                    ra = this.loadMF(ra);
                    ra = this.loadOrganism(ra);

                    ra = this.loadCT(ra);
                 */

                //  ra = this.loadCMO(ra);
                // ra = this.loadPW(ra);
                // ra = this.loadXCO(ra);
                // ra = this.loadZFA(ra);


                    /*
                    FileWriter fw = new FileWriter(rootDir + "/bert/pubmed_scripts/pubmed-output/" + ra.getPmid().get(0));
                    fw.write(ra.toJSON());
                    fw.close();

                    //conda run -n ai thon genes.py
                    ProcessBuilder processBuilder = new ProcessBuilder(rootDir + "/bert/pubmed_scripts/run_indexer_for_pmid.sh", ra.getPmid().get(0), this.ontomateEndpoint);
                    Process process = processBuilder.start();
                    process.waitFor();

                    BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String ou = "";
                    while ((ou = stdout.readLine()) != null) {
                         System.out.println(ou);
                    }

                    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    String err = "";
                    while ((err = stdout.readLine()) != null) {
                        System.out.println(err);
                    }
*/



                }catch(Exception e) {
                    e.printStackTrace();
                }
        }


    public static void main (String[] args) throws Exception {
        List<Thread> threads = Collections.synchronizedList(new ArrayList<Thread>());

        //we need to multithread  args[2] is number of threads
        if (args.length==5) {
            int threadCount = 1;

            List<String> files = listFiles(args[1]);
            int filesProcessed = 0;

            String llm = args[3];

            ForkJoinPool customThreadPool = new ForkJoinPool(Integer.parseInt(args[2]));
            customThreadPool.submit(() -> files.parallelStream().forEach(file->{
                System.out.println("starting thread");
                PubMedBertDBAnnotator pmb = new PubMedBertDBAnnotator(args[0], args[1] , file,llm, args[4]);
                pmb.run();

            })).get();

          //  PubMedBertAnnotator pmb = new PubMedBertAnnotator(args[0], args[1] , files.get(filesProcessed),llm);
          //  pmb.run();

               // if (threads.size()<threadCount) {
               //     //System.out.println("processing " + files.get(filesProcessed));
               //     Thread t = new Thread(new PubMedBertAnnotator(args[0], args[1] , files.get(filesProcessed),llm));
               //     threads.add(t);
               //     filesProcessed++;
               //     t.start();
               // }






        }else {
            System.out.println("args not = 3");
            //new Thread(new PubMedBertAnnotator(args[0], args[1])).start();
            //new Thread(new PubMedBertAnnotator("/Users/jdepons","/Users/jdepons/git/dev/pubmed-crawler2/2015/2015_10_17_138.xml")).start();
        }
    }

    public static List<String> listFiles(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toList());
    }

    private HashMap<String,ArrayList<String>> runModel(String model, String pmid, String abstractText, String meshTerms) throws Exception {
        //System.out.println("runing Model: " + model);

        HashMap<String,ArrayList<String>> retMap = new HashMap<String,ArrayList<String>>();

        HashMap<String, ArrayList<String>> hm = this.hg.runParsed(model, pmid, abstractText + " " + meshTerms,this.llm);

        ArrayList<String> bps = new ArrayList<String>();
        ArrayList<String> bpPos = new ArrayList<String>();
        ArrayList<String> bpCounts = new ArrayList<String>();
        ArrayList<String> ontIds = new ArrayList<String>();

        for (String key : hm.keySet()) {
            if (key.startsWith("none")) {
                continue;
            }
            if (key.endsWith("-ontId")) continue;
            ArrayList<String> ontIdList = hm.get(key + "-ontId");

            if (ontIdList.size() > 0) {
                //System.out.println("adding Ont: " + ontIdList.get(0));
                ontIds.add(ontIdList.get(0));
            }else {
                ontIds.add("DOID:0000004");
            }

            bps.add(key);
            //ontIds.add("MP:0006087");
            bpCounts.add(hm.get(key).size() + "");
            for (String val : hm.get(key)) {
                bpPos.add(val);
            }
        }

        retMap.put("counts",bpCounts);
        retMap.put("pos",bpPos);
        retMap.put("terms",bps);
        retMap.put("ids",ontIds);

        return retMap;

    }


    private HashMap<String,ArrayList<String>> runModel(String model, ResearchArticle ra) throws Exception {
        //System.out.println("runing Model: " + model);

        String pmid = ra.getPmid().get(0);
        String abstractText = ra.getAbstractText().get(0);
        String meshTerms = ra.getMeshTerms().get(0);

        HashMap<String,ArrayList<String>> retMap = new HashMap<String,ArrayList<String>>();

        HashMap<String, ArrayList<String>> hm = this.hg.runParsed(model, pmid, abstractText + " " + meshTerms,this.llm);

        ArrayList<String> bps = new ArrayList<String>();
        ArrayList<String> bpPos = new ArrayList<String>();
        ArrayList<String> bpCounts = new ArrayList<String>();
        ArrayList<String> ontIds = new ArrayList<String>();

        for (String key : hm.keySet()) {
            if (key.startsWith("none")) {
                continue;
            }
            if (key.endsWith("-ontId")) continue;
            ArrayList<String> ontIdList = hm.get(key + "-ontId");

            if (ontIdList.size() > 0) {
                //System.out.println("adding Ont: " + ontIdList.get(0));
                ontIds.add(ontIdList.get(0));
            }else {
                ontIds.add("DOID:0000004");
            }

            bps.add(key);
            //ontIds.add("MP:0006087");
            bpCounts.add(hm.get(key).size() + "");
            for (String val : hm.get(key)) {
                bpPos.add(val);
            }
        }

        retMap.put("counts",bpCounts);
        retMap.put("pos",bpPos);
        retMap.put("terms",bps);
        retMap.put("ids",ontIds);

        return retMap;

    }

    private ResearchArticle loadOrganism(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("Organism",ra);
            ra.setOrganismCount(modValues.get("counts"));
            ra.setOrganismTerm(modValues.get("terms"));
            ra.setOrganismPos(modValues.get("pos"));
            ra.setOrganismId(modValues.get("ids"));
        }
        return ra;
    }
    private ResearchArticle loadNBO(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("NBO",ra);
            ra.setNboCount(modValues.get("counts"));
            ra.setNboTerm(modValues.get("terms"));
            ra.setNboPos(modValues.get("pos"));
            ra.setNboId(modValues.get("ids"));
        }
        return ra;
    }

    /*
    private ResearchArticle loadZFA(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("ZFA",ra);
            ra.setZfaCount(modValues.get("counts"));
            ra.setZfaTerm(modValues.get("terms"));
            ra.setZfaPos(modValues.get("pos"));
            ra.setZfaId(modValues.get("ids"));
        }
        return ra;
    }
    */


    private ResearchArticle loadCT(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("CT",ra);
            ra.setCtCount(modValues.get("counts"));
            ra.setCtTerm(modValues.get("terms"));
            ra.setCtPos(modValues.get("pos"));
            ra.setCtId(modValues.get("ids"));
        }
        return ra;
    }
    private ResearchArticle loadMF(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("MF",ra);
            ra.setMfCount(modValues.get("counts"));
            ra.setMfTerm(modValues.get("terms"));
            ra.setMfPos(modValues.get("pos"));
            ra.setMfId(modValues.get("ids"));
        }
        return ra;
    }

    private ResearchArticle loadHP(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("HP",ra);
            ra.setHpCount(modValues.get("counts"));
            ra.setHpTerm(modValues.get("terms"));
            ra.setHpPos(modValues.get("pos"));
            ra.setHpId(modValues.get("ids"));
        }
        return ra;
    }
   /*
    private ResearchArticle loadXCO(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("XCO",ra);
            ra.setXcoCount(modValues.get("counts"));
            ra.setXcoTerm(modValues.get("terms"));
            ra.setXcoPos(modValues.get("pos"));
            ra.setXcoId(modValues.get("ids"));
        }
        return ra;
    }
**/
 /*
    private ResearchArticle loadCMO(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("CMO",ra);
            ra.setCmoCount(modValues.get("counts"));
            ra.setCmoTerm(modValues.get("terms"));
            ra.setCmoPos(modValues.get("pos"));
            ra.setCmoId(modValues.get("ids"));
        }
        return ra;
    }
    */

    private ResearchArticle loadMP(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("MP",ra);
            ra.setMpCount(modValues.get("counts"));
            ra.setMpTerm(modValues.get("terms"));
            ra.setMpPos(modValues.get("pos"));
            ra.setMpId(modValues.get("ids"));
        }
        return ra;
    }
    /*
    private ResearchArticle loadPW(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("PW",ra);
            ra.setPwCount(modValues.get("counts"));
            ra.setPwTerm(modValues.get("terms"));
            ra.setPwPos(modValues.get("pos"));
            ra.setPwId(modValues.get("ids"));
        }
        return ra;
    }
**/
    private ResearchArticle loadSO(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("SO",ra);
            ra.setSoCount(modValues.get("counts"));
            ra.setSoTerm(modValues.get("terms"));
            ra.setSoPos(modValues.get("pos"));
            ra.setSoId(modValues.get("ids"));
        }
        return ra;
    }

    private ResearchArticle loadMMO(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("MMO",ra);
            ra.setMmoCount(modValues.get("counts"));
            ra.setMmoTerm(modValues.get("terms"));
            ra.setMmoPos(modValues.get("pos"));
            ra.setMmoId(modValues.get("ids"));
        }
        return ra;
    }

    private ResearchArticle loadChebi(ResearchArticle ra) throws Exception {
;
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("CHEBI",ra);
            ra.setChebiCount(modValues.get("counts"));
            ra.setChebiTerm(modValues.get("terms"));
            ra.setChebiPos(modValues.get("pos"));
            ra.setChebiId(modValues.get("ids"));
        }
        return ra;
    }

    private ResearchArticle loadMA(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("MA",ra);
            ra.setMaCount(modValues.get("counts"));
            ra.setMaTerm(modValues.get("terms"));
            ra.setMaPos(modValues.get("pos"));
            ra.setMaId(modValues.get("ids"));
        }
        return ra;
    }



    private ResearchArticle loadCC(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("CC",ra);
            ra.setCcCount(modValues.get("counts"));
            ra.setCcTerm(modValues.get("terms"));
            ra.setCcPos(modValues.get("pos"));
            ra.setCcId(modValues.get("ids"));
        }
        return ra;
    }


    private ResearchArticle loadBP(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("BP",ra);
            ra.setBpCount(modValues.get("counts"));
            ra.setBpTerm(modValues.get("terms"));
            ra.setBpPos(modValues.get("pos"));
            ra.setBpId(modValues.get("ids"));
        }
        return ra;
    }


    private ResearchArticle loadDO(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);
        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("DO",ra);
                ra.setRdoCount(modValues.get("counts"));
                ra.setRdoTerm(modValues.get("terms"));
                ra.setRdoPos(modValues.get("pos"));
                ra.setRdoId(modValues.get("ids"));

                ra.setMpCount(modValues.get("counts"));
                ra.setMpTerm(modValues.get("terms"));
                ra.setMpPos(modValues.get("pos"));
                ra.setMpId(modValues.get("ids"));
        }
        return ra;
    }

    private ResearchArticle loadGenes(ResearchArticle ra) throws Exception {
        String abstractText = ra.getAbstractText().get(0);
        if (!abstractText.equals("")) {
            HashMap<String,ArrayList<String>> modValues= this.runModel("Gene",ra);
            ra.setGeneCount(modValues.get("counts"));
            ra.setGene(modValues.get("terms"));
            //System.out.println(modValues.get("terms"));
            ra.setGenePos(modValues.get("pos"));
        }
        return ra;
    }

    private static ArrayList<String> toList(String val) {
        ArrayList<String> al = new ArrayList<String>();
        al.add(val);
        return al;
    }

    private static ArrayList<String> toListOfSizeOne(List<String> vals) {
        ArrayList<String> al = new ArrayList<String>();

        String retVal = "";
        boolean first = true;
        for (String val: vals) {
            if (first) {
                retVal=val;
                first=false;
            }else {
                retVal += "," + val;
            }
            }

        al.add(retVal);
        return al;
    }


    private static ArrayList<Integer> toList(int val) {
        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add(val);
        return al;
    }

    public ArrayList<String> getArticles(String pubmedXMLFile) throws Exception{

            String content = new String(Files.readAllBytes(Paths.get(pubmedXMLFile)));
        ArrayList<String> articles = PubMedJSoupDoc.getArticles(content);
            return articles;

    }

}
