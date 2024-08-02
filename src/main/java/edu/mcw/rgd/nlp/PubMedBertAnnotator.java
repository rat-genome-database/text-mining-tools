package edu.mcw.rgd.nlp;

import edu.mcw.rgd.nlp.utils.ncbi.*;
import edu.mcw.rgd.database.ncbi.pubmed.ArticleDAO;
import edu.mcw.rgd.nlp.datamodel.ResearchArticle;
import edu.mcw.rgd.nlp.utils.python.HugRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PubMedBertAnnotator extends Thread{

    public String rootDir;
    public String articleFile;
    public HugRunner hg;
    public int threads = 0;

    public static int totalProcessed=0;

    public PubMedBertAnnotator(String rootDir, String articleFile) {
        this.rootDir = rootDir;
        this.articleFile = articleFile;
        this.hg = new HugRunner(rootDir);
    }

    public PubMedBertAnnotator(String rootDir, String articleDir, int threads) {
        this.rootDir = rootDir;
        this.articleFile = articleDir;
        this.hg = new HugRunner(rootDir);
        this.threads=threads;
    }

    public void run() {
            try {

                ArrayList<String> articles = this.getArticles(this.articleFile);

                System.out.println("Total Articles: " + articles.size());
                int count = 1;
                for (String article : articles) {
                    ResearchArticle ra = new ResearchArticle();
                    ArrayList<String> it = new ArrayList<String>();
                    ra.setDoiS(toList(PubMedJSoupDoc.doi(article)));
                    ra.setTitle(toList(PubMedJSoupDoc.articleTitle(article)));
                    ra.setKeywords(toListOfSizeOne(PubMedJSoupDoc.keywordList(article)));
                    ra.setAbstractText(toList(PubMedJSoupDoc.abstractText(article)));
                    ra.setAffiliation(toList(PubMedJSoupDoc.affiliation(article)));
                    ra.setAuthors(toListOfSizeOne(PubMedJSoupDoc.authorList(article)));
                    ra.setpDate(toList(PubMedJSoupDoc.pubArticleDate(article).replace("/", "-")));
                    ra.setjDateS(toList(PubMedJSoupDoc.pubJournalDate(article)));
                    ra.setPmid(toList(PubMedJSoupDoc.pmId(article)));

                    String citation = PubMedJSoupDoc.journalTitle(article) + "," + PubMedJSoupDoc.pubJournalDate(article) + ", " +
                            PubMedJSoupDoc.journalVolume(article) + "(" + PubMedJSoupDoc.journalIssue(article) + "): " +
                            PubMedJSoupDoc.journalPage(article) ;

                    ra.setCitation(toList(citation));
                    ra.setMeshTerms(toListOfSizeOne(PubMedJSoupDoc.meshHeadingList(article)));
                    ra.setpYear(toList(Integer.parseInt(ra.getpDate().get(0).substring(0, 3))));
                    ra.setIssn(toList(PubMedJSoupDoc.issn(article)));
                    ra.setOrganismNCBIId(toList("10090"));
                    ra.setOrganismCommonName(toList("homo sapiens"));

                    ra = this.loadGenes(ra);
                    if (ra.getGene() == null || ra.getGene().size() == 0) {
                        System.out.println("No Genes Found");
                        continue;
                    }

                    ra = this.loadDO(ra);
                    ra = this.loadBP(ra);
                    ra= this.loadCC(ra);
                    ra = this.loadChebi(ra);
                    ra = this.loadMA(ra);
                    ra = this.loadMMO(ra);
                    ra = this.loadMP(ra);
                    ra = this.loadSO(ra);
                   // ra = this.loadCMO(ra);
                    ra = this.loadHP(ra);
                    ra = this.loadNBO(ra);
                   // ra = this.loadPW(ra);
                   // ra = this.loadXCO(ra);
                    ra = this.loadMF(ra);
                   // ra = this.loadZFA(ra);
                    //ra = this.loadOrganism(ra);
                    ra = this.loadCT(ra);


                    System.out.println(totalProcessed++ + " " + count++  + ". PMID:" + ra.getPmid().get(0) + " (" + ra.getTitle() + ")");

                    FileWriter fw = new FileWriter(rootDir + "/bert/pubmed_scripts/pubmed-output/" + ra.getPmid().get(0));
                    fw.write(ra.toJSON());
                    fw.close();

                    //conda run -n ai python genes.py
                    //ProcessBuilder processBuilder = new ProcessBuilder(rootDir + "/bert/pubmed_scripts/run_indexer_for_pmid.sh", ra.getPmid().get(0));
                    //Process process = processBuilder.start();
                    //process.waitFor();

 //                   BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    //String e = "";
                    //while ((e = stdout.readLine()) != null) {
                        // System.out.println(e);
                    //}

                    //BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));


                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }


    public static void main (String[] args) throws Exception {
        ArrayList<Thread> threads = new ArrayList<Thread>();

        //we need to multithread  args[2] is number of threads
        if (args.length==3) {
            int threadCount = Integer.parseInt(args[2]);

            List<String> files = listFiles(args[1]);
            int filesProcessed = 0;

            while (filesProcessed < files.size()) {

                if (threads.size()<threadCount) {
                    System.out.println("processing " + files.get(filesProcessed));
                    Thread t = new Thread(new PubMedBertAnnotator(args[0], args[1] + "/" + files.get(filesProcessed)));
                    threads.add(t);
                    filesProcessed++;
                    t.start();
                }else {
                    for (Thread t: threads) {
                        if (!t.isAlive()) {
                            threads.remove(t);
                            System.out.println("removing thread");
                                //t = new Thread(new PubMedBertAnnotator(args[0], fileName));
                                //threads.add(t);
                                //t.start();
                            }
                        }

                }


            }




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


    private HashMap<String,ArrayList<String>> runModel(String model, ResearchArticle ra) throws Exception {
        String pmid = ra.getPmid().get(0);
        String abstractText = ra.getAbstractText().get(0);
        String meshTerms = ra.getMeshTerms().get(0);

        HashMap<String,ArrayList<String>> retMap = new HashMap<String,ArrayList<String>>();

        HashMap<String, ArrayList<String>> hm = this.hg.runParsed(model, pmid, abstractText + " " + meshTerms);

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
            HashMap<String,ArrayList<String>> modValues= this.runModel("Chebi",ra);
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
            System.out.println(modValues.get("terms"));
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
