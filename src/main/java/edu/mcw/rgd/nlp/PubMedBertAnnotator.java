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
                    ra.setCitation(toList("need to add"));
                    ra.setMeshTerms(toListOfSizeOne(PubMedJSoupDoc.meshHeadingList(article)));
                    ra.setpYear(toList(Integer.parseInt(ra.getpDate().get(0).substring(0, 3))));
                    ra.setIssn(toList(PubMedJSoupDoc.issn(article)));
                    ra.setOrganismNCBIId(toList("10090"));
                    ra = this.loadGenes(ra);
                    ra = this.loadRDO(ra);
                    ra = this.loadBP(ra);
                    ra = this.loadMa(ra);
                    ra = this.loadChebi(ra);

                    System.out.println(totalProcessed++ + " " + count++  + ". PMID:" + ra.getPmid().get(0) + " (" + ra.getTitle() + ")");

                    FileWriter fw = new FileWriter(rootDir + "/bert/pubmed_scripts/pubmed-output/" + ra.getPmid().get(0));
                    fw.write(ra.toJSON());
                    fw.close();

                    //conda run -n ai python genes.py
                    ProcessBuilder processBuilder = new ProcessBuilder(rootDir + "/bert/pubmed_scripts/run_indexer_for_pmid.sh", ra.getPmid().get(0));
                    Process process = processBuilder.start();
                    process.waitFor();

                    BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String e = "";
                    while ((e = stdout.readLine()) != null) {
                        // System.out.println(e);
                    }

                    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

//            System.out.println(ra.toJSON());

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

            Set<String> files = listFiles(args[1]);

            for (String fileName: files) {
                if (threads.size()<threadCount) {
                    Thread t = new Thread(new PubMedBertAnnotator(args[0], args[1] + "/" + fileName));
                    threads.add(t);
                    t.start();
                    System.out.println("added thread");
                }else {

                    while (true) {
                        for (Thread t: threads) {
                            if (!t.isAlive()) {
                                System.out.println("thread dead");
                                threads.remove(t);
                                t = new Thread(new PubMedBertAnnotator(args[0], fileName));
                                threads.add(t);
                                t.start();
                                System.out.println("added thread");
                            }
                        }
                    }

                }

            }


        }else {
            new Thread(new PubMedBertAnnotator(args[0], args[1])).start();
            //new Thread(new PubMedBertAnnotator("/Users/jdepons","/Users/jdepons/git/dev/pubmed-crawler2/2015/2015_10_17_138.xml")).start();
        }
    }

    public static Set<String> listFiles(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }


    private ResearchArticle loadChebi(ResearchArticle ra) throws Exception {

        String pmid = ra.getPmid().get(0);
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String, ArrayList<String>> hm = this.hg.runStructured("Chemical", pmid,abstractText);

            List<String> obj = new ArrayList<String>();
            List<String> pos = new ArrayList<String>();
            List<String> counts = new ArrayList<String>();
            List<String> ontIds = new ArrayList<String>();
            for (String key : hm.keySet()) {
                obj.add(key);
                ontIds.add("MP:0006087");

                counts.add(hm.get(key).size() + "");
                for (String val : hm.get(key)) {
                    pos.add(val);
                }
            }

            ra.setChebiCount(counts);
            ra.setChebiTerm(obj);
            ra.setChebiPos(pos);
            ra.setChebiId(ontIds);
            System.out.println("chebi count = " + obj.size());


        }
        return ra;
    }

    private ResearchArticle loadMa(ResearchArticle ra) throws Exception {
        String pmid = ra.getPmid().get(0);
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String, ArrayList<String>> hm = this.hg.runStructured("Anatomical", pmid, abstractText);

            List<String> mas = new ArrayList<String>();
            List<String> maPos = new ArrayList<String>();
            List<String> maCounts = new ArrayList<String>();
            List<String> ontIds = new ArrayList<String>();

            for (String key : hm.keySet()) {
                mas.add(key);
                ontIds.add("MP:0006087");
                maCounts.add(hm.get(key).size() + "");
                for (String val : hm.get(key)) {
                    maPos.add(val);
                }
            }

            ra.setMaCount(maCounts);
            ra.setMaTerm(mas);
            ra.setMaPos(maPos);
            ra.setMaId(ontIds);
            System.out.println("ma count = " + mas.size());

        }
        return ra;
    }



    private ResearchArticle loadBP(ResearchArticle ra) throws Exception {
        String pmid = ra.getPmid().get(0);
        String abstractText = ra.getAbstractText().get(0);

        if (!abstractText.equals("")) {
            HashMap<String, ArrayList<String>> hm = this.hg.runStructured("Bioprocess", pmid, abstractText);

            List<String> bps = new ArrayList<String>();
            List<String> bpPos = new ArrayList<String>();
            List<String> bpCounts = new ArrayList<String>();
            List<String> ontIds = new ArrayList<String>();

            for (String key : hm.keySet()) {
                bps.add(key);
                ontIds.add("MP:0006087");
                bpCounts.add(hm.get(key).size() + "");
                for (String val : hm.get(key)) {
                    bpPos.add(val);
                }
            }

            ra.setBpCount(bpCounts);
            ra.setBpTerm(bps);
            ra.setBpPos(bpPos);
            ra.setBpId(ontIds);
            System.out.println("bp count = " + bps.size());

        }
        return ra;
    }


    private ResearchArticle loadRDO(ResearchArticle ra) throws Exception {
        String pmid = ra.getPmid().get(0);
        String abstractText = ra.getAbstractText().get(0);
        if (!abstractText.equals("")) {
                HashMap<String, ArrayList<String>> hm = this.hg.runStructured("Disease", pmid, abstractText);

                List<String> diseases = new ArrayList<String>();
                List<String> diseasePos = new ArrayList<String>();
                List<String> diseaseCounts = new ArrayList<String>();
            List<String> ontIds = new ArrayList<String>();

                for (String key : hm.keySet()) {
                    diseases.add(key);
                    //HugRunner.getTermAccession(key);
                    ontIds.add("MP:0006087");
                    diseaseCounts.add(hm.get(key).size() + "");
                    for (String val : hm.get(key)) {
                        diseasePos.add(val);
                    }
                }

                ra.setRdoCount(diseaseCounts);
                ra.setRdoTerm(diseases);
                ra.setRdoPos(diseasePos);
                ra.setRdoId(ontIds);

                ra.setHpCount(diseaseCounts);
                ra.setHpTerm(diseases);
                ra.setHpPos(diseasePos);
                ra.setHpId(ontIds);
            System.out.println("disease count = " + diseases.size());
        }
        return ra;
    }

    private ResearchArticle loadGenes(ResearchArticle ra) throws Exception {
        String pmid = ra.getPmid().get(0);
        String abstractText = ra.getAbstractText().get(0);
        if (!abstractText.equals("")) {
            HashMap<String, ArrayList<String>> hm = this.hg.runStructured("Gene", pmid, abstractText);

            List<String> genes = new ArrayList<String>();
            List<String> genePos = new ArrayList<String>();
            List<String> geneCounts = new ArrayList<String>();
            List<String> ontIds = new ArrayList<String>();
            for (String key : hm.keySet()) {
                genes.add(key);
                ontIds.add("2004");
                geneCounts.add(hm.get(key).size() + "");
                for (String val : hm.get(key)) {
                    genePos.add(val);
                }
            }

            ra.setGeneCount(geneCounts);
            ra.setGene(genes);
            ra.setGenePos(genePos);
            System.out.println("gene count = " + genes.size());
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
