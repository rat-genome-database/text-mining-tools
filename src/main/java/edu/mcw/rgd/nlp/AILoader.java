package edu.mcw.rgd.nlp;

import dev.langchain4j.model.ollama.OllamaChatModel;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.nlp.datamodel.ResearchArticle;
import edu.mcw.rgd.nlp.utils.ncbi.PubMedJSoupDoc;
import edu.mcw.rgd.nlp.utils.python.HugRunner;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AILoader extends Thread{

    public String rootDir;
    public String articleFile;
    public String articleDir;
    public HugRunner hg;
    public int threads = 0;
    public String llm;
    public String ontomateEndpoint;
    public String pdate;

    public static AtomicInteger totalProcessed=new AtomicInteger(0);

    public AILoader(String rootDir, String articleDir, String articleFile, String llm, String ontomateEndpoint, String pdate) {
        this.rootDir = rootDir;
        this.articleDir = articleDir;
        this.articleFile = articleFile;
        this.hg = new HugRunner(rootDir);
        this.llm = llm;
        this.ontomateEndpoint = ontomateEndpoint;
        this.pdate = pdate;
    }

    public void run() {

            System.out.println("running for " + this.pdate);
            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
                GeneDAO gdao = new GeneDAO();

                Connection conn = gdao.getConnection();

                String query = "select * from pubmed_article where pdate='" + this.pdate + "' and ai_genes is null";

                Statement s = conn.createStatement();
                ResultSet rs = s.executeQuery(query);

                int count = 1;
                while (rs.next()) {

                    ResearchArticle ra = new ResearchArticle();
                    ArrayList<String> it = new ArrayList<String>();

                    String pmid = rs.getString("pmid");

                    ra.setPmid(toList(rs.getString("pmid")));


                    System.out.println(sdf.format(new Date())+ " processing " + ra.getPmid() + " **************************************************");
                    String abstractText = "";
                    Clob clob = rs.getClob("abstract");
                    if (clob != null) {
                        long length = clob.length();
                        abstractText = clob.getSubString(1, (int) length); // indexing starts at 1
                        // Now you have the CLOB contents as a String
                    }

                    ra.setAbstractText(toList(abstractText));


                    ra.setMeshTerms(toList(rs.getString("mesh_terms")));
                    count++;

                    OllamaChatModel model = OllamaChatModel.builder()
                            .baseUrl("http://localhost:11434") // Ollama's default port
                            .modelName("curatorModel") // Replace with your downloaded model
                            .build();

                    String prompt = "Extract the <symbol> for any gene discussed in the following abstract. <abstract>" + abstractText + "</abstract> respond with a comma delimited list of <symbol> and no other output";

                    String response = model.generate(prompt);
                    System.out.println(response);
                    this.update("gene",llm,response,pmid);
                    if (response.equals("none")) continue;

                    prompt = "Extract all <disease terms> for any disease explicitly or implicitly discussed in the following abstract. <abstract>" + abstractText + "</abstract> Respond with a comma delimited list of <disease terms> and no other output. If the abstact is unrelated to any disease, return NA";

                    response = model.generate(prompt);

                    this.update("disease",llm,response,pmid);

                    prompt = "Extract all <rat strains> from the following abstract. <abstract>" + abstractText + "</abstract> Respond with a comma delimited list of <rat strains> and no other output. If the abstact is unrelated to rat strains, return NA";

                    response = model.generate(prompt);

                    this.update("strain",llm,response,pmid);

                    //model = OllamaChatModel.builder()
                    //        .baseUrl("http://localhost:11434") // Ollama's default port
                    //        .modelName("llama3") // Replace with your downloaded model
                    //        .build();

                    //prompt = "I am a curator trying to make disease to gene annotations. List out any possible gene to disease annotations in the following abstract and state your reasoning.  If you do not fine disease to gene associations respond with 'none found' and do not give your reasoning.  <abstract>" + abstractText + "</abstract>";
                    //response = model.generate(prompt);

                    //System.out.println("response annotation = " + response);



                }


                }catch(Exception e) {
                e.printStackTrace();
            }
        }

    public void update(String entity, String terms, String entityPos, String entityCount, String pmid) throws Exception {
        GeneDAO gdao = new GeneDAO();

        Connection conn = gdao.getConnection();

        String query = "update pubmed_article set ai_" + entity + "_pos='" + entityPos + "', ai_" + entity + "_counts='" + entityCount + "', ai_" + entity + "='" + terms + "' where pmid=" + pmid;

        Statement s = conn.createStatement();

        s.executeUpdate(query);

        conn.close();

    }
    public void update(String entity, ArrayList<String> terms, ArrayList<String> entityPos, ArrayList<String> entityCount, String pmid) throws Exception {
        this.update(entity, this.listToString(terms),this.listToString(entityPos),this.listToString(entityCount),pmid);
    }

    public void update(String entity, String llm, String text, String pmid) throws Exception {
        GeneDAO gdao = new GeneDAO();

        Connection conn = gdao.getConnection();

        String query = "update pubmed_article set ai_" + entity + "_llm=?, ai_" + entity + "_response=? where pmid=" + pmid;

        PreparedStatement s = conn.prepareStatement(query);

        s.setString(1,llm);
        s.setString(2,text);


        s.executeQuery();

        conn.close();

    }

    public void updateGenes(String genes, String genePos, String geneCount, String pmid) throws Exception {
        GeneDAO gdao = new GeneDAO();

        Connection conn = gdao.getConnection();

        String query = "update pubmed_article set ai_gene_pos='" + genePos + "', ai_gene_counts='" + geneCount + "', ai_genes='" + genes + "' where pmid=" + pmid;

        Statement s = conn.createStatement();

        s.executeUpdate(query);

        conn.close();

    }


    public void updateGenes(ArrayList<String> genes, ArrayList<String> genePos, ArrayList<String> geneCount, String pmid) throws Exception {

        this.updateGenes(this.listToString(genes),this.listToString(genePos),this.listToString(geneCount),pmid);
   }

   public String listToString(List<String> lst) {
       return String.join(",", lst);
   }


    public static void main (String[] args) throws Exception {
            int threadCount = 1;

    //public AILoader(String rootDir, String articleDir, String articleFile, String llm, String ontomateEndpoint) {

            //int filesProcessed = 0;
            //String llm = args[3];

        /*
        if (true) {
            AILoader pmb = new AILoader(args[0], "not used" , "not used",args[1], "not sure", "2009-09-01");
            pmb.run();

        }
*/

        GeneDAO gdao = new GeneDAO();
        Connection conn = gdao.getConnection();

        String query = "select distinct pdate from pubmed_article where ai_gene_response not null";

        Statement s = conn.createStatement();

        ResultSet rs = s.executeQuery(query);

        ExecutorService pool = Executors.newFixedThreadPool(5); // 5 concurrent tasks max
        List<Callable<Void>> tasks = new ArrayList<>();

        while (rs.next()) {

            AILoader pmb = new AILoader(args[0], "not used" , "not used",args[1], "not sure", rs.getString("pdate"));
            // Add a task that will run the AILoader when executed
            tasks.add(() -> {
                pmb.run();
                return null;
            });
        }

        // Invoke all tasks in parallel
        // This will block until all tasks are completed.
        List<Future<Void>> results = pool.invokeAll(tasks);

        // Check for exceptions if needed
        for (Future<Void> f : results) {
            try {
                f.get(); // This will throw if any task encountered an exception
            } catch (Exception e) {
                e.printStackTrace(); // Handle exceptions from tasks
            }
        }


        conn.close();


            //AILoader pmb = new AILoader("/Users/jdepons/ai", "not used" , "not used","curatorModel:latest", "not sure");

    }

    public static List<String> listFiles(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toList());
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
        String res = this.hg.runRaw("DO", ra.getPmid().get(0), abstractText, this.llm);
        System.out.println("abstract = " + abstractText);
        this.update("disease", llm, res,ra.getPmid().get(0));

        return ra;


    }

    private ResearchArticle loadGenes(ResearchArticle ra) throws Exception {

        String abstractText = ra.getAbstractText().get(0);
        String res = this.hg.runRaw("Gene", ra.getPmid().get(0), abstractText, this.llm);
        System.out.println("abstract = " + abstractText);
        this.update("gene", llm, res,ra.getPmid().get(0));

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
