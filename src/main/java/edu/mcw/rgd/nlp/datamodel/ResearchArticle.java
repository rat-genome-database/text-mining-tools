package edu.mcw.rgd.nlp.datamodel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResearchArticle {
    private List<String> geneCount;
    private List<String> mpId;
    private List<String> keywords;
    private List<String> mmoCount;
    private List<String> doiS;
    private List<String> chebiPos;
    private List<String> maId;
    private List<String> pmcIdS;
    private List<String> chebiTerm;
    private List<String> pDate;
    private List<String> affiliation;
    private List<String> meshTerms;
    private List<String> chebiCount;
    private List<String> chemicals;
    private List<String> mpTerm;
    private List<String> mmoTerm;
    private List<String> rdoId;
    private List<String> nboPos;
    private List<String> gene;
    private List<String> soTerm;
    private List<String> maPos;
    private List<String> mpCount;
    private List<String> issn;
    private List<String> mmoPos;
    private List<String> pType;
    private List<String> nboCount;
    private List<Integer> pYear;
    private List<String> authors;
    private List<String> mmoId;
    private List<String> maCount;
    private List<String> jDateS;
    private List<String> maTerm;
    private List<String> rdoCount;
    private List<String> title;
    private List<String> nboTerm;
    private List<String> hpPos;
    private List<String> nboId;
    private List<String> soCount;
    private List<String> hpTerm;
    private List<String> soId;
    private List<String> hpId;
    private List<String> citation;
    private List<String> rdoPos;
    private List<String> abstractText;
    private List<String> pmid;
    private List<String> mpPos;
    private List<String> hpCount;
    private List<String> genePos;
    private List<String> soPos;
    private List<String> rdoTerm;
    private List<String> chebiId;
    private List<String> organismNCBIId;
    private List<String> organismCommonName;
    private List<String> bpTerm;

    public List<String> getOrganismCommonName() {
        return organismCommonName;
    }

    public void setOrganismCommonName(List<String> organismCommonName) {
        this.organismCommonName = organismCommonName;
    }

    private List<String> bpId;
    private List<String> bpCount;
    private List<String> bpPos;
    private List<String> ccTerm;
    private List<String> ccId;
    private List<String> ccCount;
    private List<String> ccPos;
    private List<String> mfTerm;
    private List<String> mfId;
    private List<String> mfCount;
    private List<String> mfPos;
    private List<String> ctTerm;
    private List<String> ctId;
    private List<String> ctCount;
    private List<String> ctPos;
    private List<String> organismTerm;
    private List<String> organismId;
    private List<String> organismCount;
    private List<String> organismPos;

    public List<String> getCtTerm() {
        return ctTerm;
    }

    public void setCtTerm(List<String> ctTerm) {
        this.ctTerm = ctTerm;
    }

    public List<String> getCtId() {
        return ctId;
    }

    public void setCtId(List<String> ctId) {
        this.ctId = ctId;
    }

    public List<String> getCtCount() {
        return ctCount;
    }

    public void setCtCount(List<String> ctCount) {
        this.ctCount = ctCount;
    }

    public List<String> getCtPos() {
        return ctPos;
    }

    public void setCtPos(List<String> ctPos) {
        this.ctPos = ctPos;
    }

    public List<String> getOrganismTerm() {
        return organismTerm;
    }

    public void setOrganismTerm(List<String> organismTerm) {
        this.organismTerm = organismTerm;
    }

    public List<String> getOrganismId() {
        return organismId;
    }

    public void setOrganismId(List<String> organismId) {
        this.organismId = organismId;
    }

    public List<String> getOrganismCount() {
        return organismCount;
    }

    public void setOrganismCount(List<String> organismCount) {
        this.organismCount = organismCount;
    }

    public List<String> getOrganismPos() {
        return organismPos;
    }

    public void setOrganismPos(List<String> organismPos) {
        this.organismPos = organismPos;
    }

    public List<String> getMfTerm() {
        return mfTerm;
    }

    public void setMfTerm(List<String> mfTerm) {
        this.mfTerm = mfTerm;
    }

    public List<String> getMfId() {
        return mfId;
    }

    public void setMfId(List<String> mfId) {
        this.mfId = mfId;
    }

    public List<String> getMfCount() {
        return mfCount;
    }

    public void setMfCount(List<String> mfCount) {
        this.mfCount = mfCount;
    }

    public List<String> getMfPos() {
        return mfPos;
    }

    public void setMfPos(List<String> mfPos) {
        this.mfPos = mfPos;
    }

    public List<String> getCcTerm() {
        return ccTerm;
    }

    public void setCcTerm(List<String> ccTerm) {
        this.ccTerm = ccTerm;
    }

    public List<String> getCcId() {
        return ccId;
    }

    public void setCcId(List<String> ccId) {
        this.ccId = ccId;
    }

    public List<String> getCcCount() {
        return ccCount;
    }

    public void setCcCount(List<String> ccCount) {
        this.ccCount = ccCount;
    }

    public List<String> getCcPos() {
        return ccPos;
    }

    public void setCcPos(List<String> ccPos) {
        this.ccPos = ccPos;
    }


// Constructors, getters, and setters for each field would go here.
    // Due to the extensive list, I'll provide a pattern for a few fields as an example.

    public ResearchArticle() {
        // Initialization can be done here if necessary
    }

    public List<String> getOrganismNCBIId() {
        return organismNCBIId;
    }

    public void setOrganismNCBIId(List<String> organismNCBIId) {
        this.organismNCBIId = organismNCBIId;
    }

    public List<String> getGeneCount() {
        return geneCount;
    }

    public void setGeneCount(List<String> geneCount) {
        this.geneCount = geneCount;
    }

    public List<String> getMpId() {
        return mpId;
    }

    public void setMpId(List<String> mpId) {
        this.mpId = mpId;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getMmoCount() {
        return mmoCount;
    }

    public void setMmoCount(List<String> mmoCount) {
        this.mmoCount = mmoCount;
    }

    public List<String> getDoiS() {
        return doiS;
    }

    public void setDoiS(List<String> doiS) {
        this.doiS = doiS;
    }

    public List<String> getChebiPos() {
        return chebiPos;
    }

    public void setChebiPos(List<String> chebiPos) {
        this.chebiPos = chebiPos;
    }

    public List<String> getMaId() {
        return maId;
    }

    public void setMaId(List<String> maId) {
        this.maId = maId;
    }

    public List<String> getPmcIdS() {
        return pmcIdS;
    }

    public void setPmcIdS(List<String> pmcIdS) {
        this.pmcIdS = pmcIdS;
    }

    public List<String> getBpTerm() {
        return bpTerm;
    }

    public void setBpTerm(List<String> bpTerm) {
        this.bpTerm = bpTerm;
    }

    public List<String> getChebiTerm() {
        return chebiTerm;
    }

    public void setChebiTerm(List<String> chebiTerm) {
        this.chebiTerm = chebiTerm;
    }

    public List<String> getpDate() {
        return pDate;
    }

    public void setpDate(List<String> pDate) {
        this.pDate = pDate;
    }

    public List<String> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<String> affiliation) {
        this.affiliation = affiliation;
    }

    public List<String> getMeshTerms() {
        return meshTerms;
    }

    public void setMeshTerms(List<String> meshTerms) {
        this.meshTerms = meshTerms;
    }

    public List<String> getChebiCount() {
        return chebiCount;
    }

    public void setChebiCount(List<String> chebiCount) {
        this.chebiCount = chebiCount;
    }

    public List<String> getChemicals() {
        return chemicals;
    }

    public void setChemicals(List<String> chemicals) {
        this.chemicals = chemicals;
    }

    public List<String> getMpTerm() {
        return mpTerm;
    }

    public void setMpTerm(List<String> mpTerm) {
        this.mpTerm = mpTerm;
    }

    public List<String> getMmoTerm() {
        return mmoTerm;
    }

    public void setMmoTerm(List<String> mmoTerm) {
        this.mmoTerm = mmoTerm;
    }

    public List<String> getRdoId() {
        return rdoId;
    }

    public void setRdoId(List<String> rdoId) {
        this.rdoId = rdoId;
    }

    public List<String> getNboPos() {
        return nboPos;
    }

    public void setNboPos(List<String> nboPos) {
        this.nboPos = nboPos;
    }

    public List<String> getGene() {
        return gene;
    }

    public void setGene(List<String> gene) {
        this.gene = gene;
    }

    public List<String> getSoTerm() {
        return soTerm;
    }

    public void setSoTerm(List<String> soTerm) {
        this.soTerm = soTerm;
    }

    public List<String> getMaPos() {
        return maPos;
    }

    public void setMaPos(List<String> maPos) {
        this.maPos = maPos;
    }

    public List<String> getMpCount() {
        return mpCount;
    }

    public void setMpCount(List<String> mpCount) {
        this.mpCount = mpCount;
    }

    public List<String> getIssn() {
        return issn;
    }

    public void setIssn(List<String> issn) {
        this.issn = issn;
    }

    public List<String> getMmoPos() {
        return mmoPos;
    }

    public void setMmoPos(List<String> mmoPos) {
        this.mmoPos = mmoPos;
    }

    public List<String> getBpId() {
        return bpId;
    }

    public void setBpId(List<String> bpId) {
        this.bpId = bpId;
    }

    public List<String> getpType() {
        return pType;
    }

    public void setpType(List<String> pType) {
        this.pType = pType;
    }

    public List<String> getNboCount() {
        return nboCount;
    }

    public void setNboCount(List<String> nboCount) {
        this.nboCount = nboCount;
    }

    public List<Integer> getpYear() {
        return pYear;
    }

    public void setpYear(List<Integer> pYear) {
        this.pYear = pYear;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<String> getMmoId() {
        return mmoId;
    }

    public void setMmoId(List<String> mmoId) {
        this.mmoId = mmoId;
    }

    public List<String> getMaCount() {
        return maCount;
    }

    public void setMaCount(List<String> maCount) {
        this.maCount = maCount;
    }

    public List<String> getjDateS() {
        return jDateS;
    }

    public void setjDateS(List<String> jDateS) {
        this.jDateS = jDateS;
    }

    public List<String> getMaTerm() {
        return maTerm;
    }

    public void setMaTerm(List<String> maTerm) {
        this.maTerm = maTerm;
    }

    public List<String> getRdoCount() {
        return rdoCount;
    }

    public void setRdoCount(List<String> rdoCount) {
        this.rdoCount = rdoCount;
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public List<String> getNboTerm() {
        return nboTerm;
    }

    public void setNboTerm(List<String> nboTerm) {
        this.nboTerm = nboTerm;
    }

    public List<String> getHpPos() {
        return hpPos;
    }

    public void setHpPos(List<String> hpPos) {
        this.hpPos = hpPos;
    }

    public List<String> getNboId() {
        return nboId;
    }

    public void setNboId(List<String> nboId) {
        this.nboId = nboId;
    }

    public List<String> getSoCount() {
        return soCount;
    }

    public void setSoCount(List<String> soCount) {
        this.soCount = soCount;
    }

    public List<String> getHpTerm() {
        return hpTerm;
    }

    public void setHpTerm(List<String> hpTerm) {
        this.hpTerm = hpTerm;
    }

    public List<String> getSoId() {
        return soId;
    }

    public void setSoId(List<String> soId) {
        this.soId = soId;
    }

    public List<String> getHpId() {
        return hpId;
    }

    public void setHpId(List<String> hpId) {
        this.hpId = hpId;
    }

    public List<String> getCitation() {
        return citation;
    }

    public void setCitation(List<String> citation) {
        this.citation = citation;
    }

    public List<String> getRdoPos() {
        return rdoPos;
    }

    public void setRdoPos(List<String> rdoPos) {
        this.rdoPos = rdoPos;
    }

    public List<String> getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(List<String> abstractText) {
        this.abstractText = abstractText;
    }

    public List<String> getPmid() {
        return pmid;
    }

    public void setPmid(List<String> pmid) {
        this.pmid = pmid;
    }

    public List<String> getBpCount() {
        return bpCount;
    }

    public void setBpCount(List<String> bpCount) {
        this.bpCount = bpCount;
    }

    public List<String> getMpPos() {
        return mpPos;
    }

    public void setMpPos(List<String> mpPos) {
        this.mpPos = mpPos;
    }

    public List<String> getHpCount() {
        return hpCount;
    }

    public void setHpCount(List<String> hpCount) {
        this.hpCount = hpCount;
    }

    public List<String> getBpPos() {
        return bpPos;
    }

    public void setBpPos(List<String> bpPos) {
        this.bpPos = bpPos;
    }

    public List<String> getGenePos() {
        return genePos;
    }

    public void setGenePos(List<String> genePos) {
        this.genePos = genePos;
    }

    public List<String> getSoPos() {
        return soPos;
    }

    public void setSoPos(List<String> soPos) {
        this.soPos = soPos;
    }

    public List<String> getRdoTerm() {
        return rdoTerm;
    }

    public void setRdoTerm(List<String> rdoTerm) {
        this.rdoTerm = rdoTerm;
    }

    public List<String> getChebiId() {
        return chebiId;
    }

    public void setChebiId(List<String> chebiId) {
        this.chebiId = chebiId;
    }

    public String toJSON() {
        JSONObject json = new JSONObject();

        json.put("keywords", new JSONArray(this.getKeywords()));
        json.put("doi_s", new JSONArray(this.getDoiS()));
        json.put("pmc_ids", new JSONArray(this.getPmcIdS()));
        //json.put("p_date", new JSONArray(this.getpDate()));
        json.put("affiliation", new JSONArray(this.getAffiliation()));
        json.put("mesh_terms", new JSONArray(this.getMeshTerms()));
        json.put("chemicals", new JSONArray(this.getChemicals()));
        json.put("issn", new JSONArray(this.getIssn()));
        json.put("p_type", new JSONArray(this.getpType()));
        json.put("p_year", new JSONArray(this.getpYear()));
        json.put("authors", new JSONArray(this.getAuthors()));
        json.put("j_date_s", new JSONArray(this.getjDateS()));
        json.put("title", new JSONArray(this.getTitle()));
        json.put("citation", new JSONArray(this.getCitation()));
        json.put("abstract", new JSONArray(this.getAbstractText()));
        json.put("pmid", new JSONArray(this.getPmid()));

        json.put("mmo_id", new JSONArray(this.getMmoId()));
        json.put("mmo_pos", new JSONArray(this.getMmoPos()));
        json.put("mmo_term", new JSONArray(this.getMmoTerm()));
        json.put("mmo_count", new JSONArray(this.getMmoCount()));

        json.put("ma_term", new JSONArray(this.getMaTerm()));
        json.put("ma_count", new JSONArray(this.getMaCount()));
        json.put("ma_pos", new JSONArray(this.getMaPos()));
        json.put("ma_id", new JSONArray(this.getMaId()));

        json.put("nbo_term", new JSONArray(this.getNboTerm()));
        json.put("nbo_id", new JSONArray(this.getNboId()));
        json.put("nbo_count", new JSONArray(this.getNboCount()));
        json.put("nbo_pos", new JSONArray(this.getNboPos()));

        json.put("hp_term", new JSONArray(this.getHpTerm()));
        json.put("hp_count", new JSONArray(this.getHpCount()));
        json.put("hp_id", new JSONArray(this.getHpId()));
        json.put("hp_pos", new JSONArray(this.getHpPos()));

        json.put("rdo_term", new JSONArray(this.getRdoTerm()));
        json.put("rdo_pos", new JSONArray(this.getRdoPos()));
        json.put("rdo_id", new JSONArray(this.getRdoId()));
        json.put("rdo_count", new JSONArray(this.getRdoCount()));

        json.put("so_pos", new JSONArray(this.getSoPos()));
        json.put("so_id", new JSONArray(this.getSoId()));
        json.put("so_term", new JSONArray(this.getSoTerm()));
        json.put("so_count", new JSONArray(this.getSoCount()));

        json.put("gene_count", new JSONArray(this.getGeneCount()));
        json.put("gene", new JSONArray(this.getGene()));
        json.put("gene_pos", new JSONArray(this.getGenePos()));

        json.put("mp_id", new JSONArray(this.getMpId()));
        json.put("mp_term", new JSONArray(this.getMpTerm()));
        json.put("mp_count", new JSONArray(this.getMpCount()));
        json.put("mp_pos", new JSONArray(this.getMpPos()));

        json.put("chebi_pos", new JSONArray(this.getChebiPos()));
        json.put("chebi_term", new JSONArray(this.getChebiTerm()));
        json.put("chebi_count", new JSONArray(this.getChebiCount()));
        json.put("chebi_id", new JSONArray(this.getChebiId()));

        json.put("bp_pos", new JSONArray(this.getBpPos()));
        json.put("bp_count", new JSONArray(this.getBpCount()));
        json.put("bp_id", new JSONArray(this.getBpId()));
        json.put("bp_term", new JSONArray(this.getBpTerm()));

        json.put("cc_pos", new JSONArray(this.getCcPos()));
        json.put("cc_count", new JSONArray(this.getCcCount()));
        json.put("cc_id", new JSONArray(this.getCcId()));
        json.put("cc_term", new JSONArray(this.getCcTerm()));

        json.put("mf_pos", new JSONArray(this.getMfPos()));
        json.put("mf_count", new JSONArray(this.getMfCount()));
        json.put("mf_id", new JSONArray(this.getMfId()));
        json.put("mf_term", new JSONArray(this.getMfTerm()));

        json.put("organism_pos", new JSONArray(this.getOrganismPos()));
         json.put("organism_count", new JSONArray(this.getOrganismCount()));
        // json.put("organism_id", new JSONArray(this.getOrganismId()));
         //json.put("organism_term", new JSONArray(this.getOrganismTerm()));
        json.put("organism_term", new JSONArray(this.getOrganismTerm()));
        //json.put("organism_common_name", new JSONArray(this.getOrganismTerm()));
        //json.put("organism_ncbi_id", new JSONArray(this.getOrganismNCBIId()));
        //json.put("organism_common_name", new JSONArray(this.getOrganismCommonName()));

         json.put("ct_pos", new JSONArray(this.getOrganismPos()));
         json.put("ct_count", new JSONArray(this.getOrganismCount()));
         json.put("ct_id", new JSONArray(this.getOrganismId()));
         json.put("ct_term", new JSONArray(this.getOrganismTerm()));

        // Returns the JSON string
        return json.toString();
    }

    public static void main(String[] args) {
        String jsonData = "{\n" +
                "\"gene_count\":[\"7\",\"4\",\"2\"],\n" +
                "\"mp_id\":[\"MP:0001872\"],\n" +
                "\"keywords\":[\"3D printer; cadaver surgery; endoscopic surgery; surgical education; surgical training; \"],\n" +
                "\"mmo_count\":[\"2\"],\n" +
                "\"doi_s\":[\"10.1002/lio2.873\"],\n" +
                "\"chebi_pos\":[\"1;1072-1077|1;1163-1168|1;1184-1189|1;1718-1723|1;1941-1946\"],\n" +
                "\"ma_id\":[\"MA:0002473\"],\n" +
                "\"pmc_id_s\":[\"PMC9392405\"],\n" +
                "\"bp_term\":[\"develop\",\"learn\"],\n" +
                "\"chebi_term\":[\"group\"],\n" +
                "\"p_date\":[\"2022-08-01\"],\n" +
                "\"affiliation\":[\"Department of Otolaryngology-Head and Neck Surgery, Faculty of Medicine and Graduate School of Medicine Hokkaido University Sapporo Hokkaido Japan.\"],\n" +
                "\"mesh_terms\":[\"\"],\n" +
                "\"chebi_count\":[\"5\"],\n" +
                "\"chemicals\":[\"\"],\n" +
                "\"mp_term\":[\"sinus\"],\n" +
                "\"mmo_term\":[\"CT scan\"],\n" +
                "\"rdo_id\":[\"DOID:0050127\",\"DOID:9002290\"],\n" +
                "\"nbo_pos\":[\"0;59-64|1;97-102|1;175-180|1;624-629|1;1357-1362|1;2094-2099\",\"0;0-7|1;1246-1253\"],\n" +
                "\"gene\":[\"<ns1\",\"<\\/ns1\",\"FESS\"],\n" +
                "\"so_term\":[\"score\",\"intermedi\",\"construct\",\"initi\",\"Repeat\"],\n" +
                "\"ma_pos\":[\"1;930-934|1;1316-1320\"],\n" +
                "\"mp_count\":[\"5\"],\"issn\":[\"2378-8038\"],\n" +
                "\"mmo_pos\":[\"1;665-684|1;898-905\"],\n" +
                "\"bp_id\":[\"GO:0032502\",\"GO:0007612\"],\n" +
                "\"p_type\":[\"Journal Article\"],\n" +
                "\"nbo_count\":[\"6\",\"2\"],\n" +
                "\"p_year\":[2022],\n" +
                "\"authors\":[\"Suzuki, Masanobu;  Miyaji, Kou;  Watanabe, Ryosuke;  Suzuki, Takayoshi;  Matoba, Kotaro;  Nakazono, Akira;  Nakamaru, Yuji;  Konno, Atsushi;  Psaltis, Alkis James;  Abe, Takashige;  Homma, Akihiro;  Wormald, Peter-John;  \"],\n" +
                "\"mmo_id\":[\"MMO:0000128\"],\n" +
                "\"ma_count\":[\"2\"],\n" +
                "\"j_date_s\":[\"2022 8 25\"],\n" +
                "\"ma_term\":[\"face\"],\n" +
                "\"rdo_count\":[\"5\",\"4\"],\n" +
                "\"title\":[\"Repetitive simulation training with novel 3D-printed sinus models for functional endoscopic sinus surgeries.\"],\n" +
                "\"nbo_term\":[\"model\",\"repetit\"],\n" +
                "\"hp_pos\":[\"0;53-58|0;92-97|1;91-96|1;208-213|1;2045-2050\"],\n" +
                "\"nbo_id\":[\"NBO:0000215\",\"NBO:0000201\"],\"so_count\":[\"3\",\"2\",\"1\",\"1\",\"1\"],\n" +
                "\"hp_term\":[\"sinus\"],\n" +
                "\"so_id\":[\"SO:0001685\",\"SO:0000933\",\"SO:0000804\",\"SO:0000014\",\"SO:0001068\"],\n" +
                "\"hp_id\":[\"HP:0000246\"],\n" +
                "\"citation\":[\"Laryngoscope investigative otolaryngology, 2022 8 25, 7(4): 943-954\"],\n" +
                "\"rdo_pos\":[\"0;53-58|0;92-97|1;91-96|1;208-213|1;2045-2050\",\"1;2037-2042|1;2037-2042|3;12-17|3;12-17\"],\n" +
                "\"abstract\":[\"Background\\tThe purpose of this study was to find a utility of a newly developed 3D-printed sinus model and to evaluate the educational benefit of simulation training with the models for functional endoscopic sinus surgery (FESS).\\r\\nMaterial and methods\\tForty-seven otolaryngologists were categorized as experts (board-certified physicians with ???200 experiences of FESS, \\n<ns1:i>\\n  n \\n<\\/ns1:i>??=???9), intermediates (board-certified physicians with &lt;200 experiences of FESS, \\n<ns1:i>\\n  n \\n<\\/ns1:i>??=???19), and novices (registrars, \\n<ns1:i>\\n  n \\n<\\/ns1:i>??=???19). They performed FESS simulation training on 3D-printed models manufactured from DICOM images of computed tomography (CT) scan of real patients. Their surgical performance was assessed with the objective structured assessment of technical skills (OSATS) score and dissection quality evaluated radiologically with a postdissection CT scan. First we evaluated the face, content, and constructive values. Second we evaluated the educational benefit of the training. Ten novices underwent training (training group) and their outcomes were compared to the remaining novices without training (control group). The training group performed cadaveric FESS surgeries before and after the repetitive training.\\r\\nResults\\tThe feedback from experts revealed high face and content value of the 3D-printed models. Experts, intermediates, and novices demonstrated statistical differences in their OSATS scores (74.7????????3.6, 58.3????????10.1, and 43.1????????11.1, respectively, \\n<ns1:i>\\n  p \\n<\\/ns1:i>??&lt;???.001), and dissection quality (81.1????????13.1, 93.7????????15.1, and 126.4????????25.2, respectively, \\n<ns1:i>\\n  p \\n<\\/ns1:i>??&lt;???.001). The training group improved their OSATS score (41.1????????8.0 to 61.1????????6.9, \\n<ns1:i>\\n  p \\n<\\/ns1:i>??&lt;???.001) and dissection quality (122.1????????22.2 to 90.9????????10.3, \\n<ns1:i>\\n  p \\n<\\/ns1:i>??=???.013), while the control group not. After training, 80% of novices with no prior FESS experiences completed surgeries on cadaver sinuses.\\r\\nConclusion\\tRepeated training using the models revealed an initial learning curve in novices, which was confirmed in cadaveric mock FESS surgeries.\\r\\nLevel of evidence\\tN/A.\\r\\n\"],\n" +
                "\"pmid\":[\"36000044\"],\n" +
                "\"bp_count\":[\"1\",\"1\"],\n" +
                "\"mp_pos\":[\"0;53-58|0;92-97|1;91-96|1;208-213|1;2045-2050\"],\n" +
                "\"hp_count\":[\"5\"],\"bp_pos\":[\"1;70-77\",\"1;2121-2126\"],\n" +
                "\"gene_pos\":[\"1;372-376|1;480-484|1;538-542|1;1533-1537|1;1668-1672|1;1789-1793|1;1889-1893\",\"1;1546-1551|1;1681-1686|1;1802-1807|1;1902-1907\",\"1;365-369|1;473-477\"],\n" +
                "\"so_pos\":[\"1;822-827|1;1453-1458|1;1745-1750\",\"1;403-412|1;1374-1383\",\"1;949-958\",\"1;2113-2118\",\"1;2066-2072\"],\n" +
                "\"rdo_term\":[\"sinus\",\"cadav\"],\n" +
                "\"chebi_id\":[\"CHEBI:24433\"]\n" +
                "}";

        JSONObject jsonObj = new JSONObject(jsonData);

        ResearchArticle ra = new ResearchArticle();

        // Set gene_count
        ra.setGeneCount(jsonArrayToList(jsonObj.getJSONArray("gene_count")));

        // Set mp_id
        ra.setMpId(jsonArrayToList(jsonObj.getJSONArray("mp_id")));

        // Set keywords
        ra.setKeywords(jsonArrayToList(jsonObj.getJSONArray("keywords")));

        // Set mmo_count
        ra.setMmoCount(jsonArrayToList(jsonObj.getJSONArray("mmo_count")));

        // Set doi_s
        ra.setDoiS(jsonArrayToList(jsonObj.getJSONArray("doi_s")));

        // Set chebi_pos
        ra.setChebiPos(jsonArrayToList(jsonObj.getJSONArray("chebi_pos")));

        // Set ma_id
        ra.setMaId(jsonArrayToList(jsonObj.getJSONArray("ma_id")));

        // Set pmc_id_s
        ra.setPmcIdS(jsonArrayToList(jsonObj.getJSONArray("pmc_id_s")));

        // Set bp_term
        ra.setBpTerm(jsonArrayToList(jsonObj.getJSONArray("bp_term")));

        // Set chebi_term
        ra.setChebiTerm(jsonArrayToList(jsonObj.getJSONArray("chebi_term")));

        // Set p_date
        ra.setpDate(jsonArrayToList(jsonObj.getJSONArray("p_date")));

        // Set affiliation
        ra.setAffiliation(jsonArrayToList(jsonObj.getJSONArray("affiliation")));

        // Set mesh_terms (empty array handling)
        JSONArray meshTermsJson = jsonObj.optJSONArray("mesh_terms");
        List<String> meshTerms = meshTermsJson == null ? new ArrayList<>() : jsonArrayToList(meshTermsJson);
        ra.setMeshTerms(meshTerms);

        // Set chebi_count
        ra.setChebiCount(jsonArrayToList(jsonObj.getJSONArray("chebi_count")));

        // Set chemicals (empty array handling)
        JSONArray chemicalsJson = jsonObj.optJSONArray("chemicals");
        List<String> chemicals = chemicalsJson == null ? new ArrayList<>() : jsonArrayToList(chemicalsJson);
        ra.setChemicals(chemicals);

        // Set mp_term
        ra.setMpTerm(jsonArrayToList(jsonObj.getJSONArray("mp_term")));

        // Set mmo_term
        ra.setMmoTerm(jsonArrayToList(jsonObj.getJSONArray("mmo_term")));

        // Set rdo_id
        ra.setRdoId(jsonArrayToList(jsonObj.getJSONArray("rdo_id")));
        // Set nbo_pos
        ra.setNboPos(jsonArrayToList(jsonObj.getJSONArray("nbo_pos")));

        // Set gene
        ra.setGene(jsonArrayToList(jsonObj.getJSONArray("gene")));

        // Set so_term
        ra.setSoTerm(jsonArrayToList(jsonObj.getJSONArray("so_term")));

        // Set ma_pos
        ra.setMaPos(jsonArrayToList(jsonObj.getJSONArray("ma_pos")));

        // Set mp_count
        ra.setMpCount(jsonArrayToList(jsonObj.getJSONArray("mp_count")));

        // Set issn
        ra.setIssn(jsonArrayToList(jsonObj.getJSONArray("issn")));

        // Set mmo_pos
        ra.setMmoPos(jsonArrayToList(jsonObj.getJSONArray("mmo_pos")));

        // Set bp_id
        ra.setBpId(jsonArrayToList(jsonObj.getJSONArray("bp_id")));

        // Set p_type
        ra.setpType(jsonArrayToList(jsonObj.getJSONArray("p_type")));

        // Set nbo_count
        ra.setNboCount(jsonArrayToList(jsonObj.getJSONArray("nbo_count")));

        // Set p_year
        ra.setpYear(jsonArrayToIntList(jsonObj.getJSONArray("p_year")));

        // Set authors
        ra.setAuthors(jsonArrayToList(jsonObj.getJSONArray("authors")));

        // Set mmo_id
        ra.setMmoId(jsonArrayToList(jsonObj.getJSONArray("mmo_id")));

        // Set ma_count
        ra.setMaCount(jsonArrayToList(jsonObj.getJSONArray("ma_count")));

        // Set j_date_s
        ra.setjDateS(jsonArrayToList(jsonObj.getJSONArray("j_date_s")));

        // Set ma_term
        ra.setMaTerm(jsonArrayToList(jsonObj.getJSONArray("ma_term")));

        // Set rdo_count
        ra.setRdoCount(jsonArrayToList(jsonObj.getJSONArray("rdo_count")));

        // Set title
        ra.setTitle(jsonArrayToList(jsonObj.getJSONArray("title")));

        // Set nbo_term
        ra.setNboTerm(jsonArrayToList(jsonObj.getJSONArray("nbo_term")));

        // Set hp_pos
        ra.setHpPos(jsonArrayToList(jsonObj.getJSONArray("hp_pos")));

        // Set nbo_id
        ra.setNboId(jsonArrayToList(jsonObj.getJSONArray("nbo_id")));

        // Set so_count
        ra.setSoCount(jsonArrayToList(jsonObj.getJSONArray("so_count")));

        // Set hp_term
        ra.setHpTerm(jsonArrayToList(jsonObj.getJSONArray("hp_term")));

        // Set so_id
        ra.setSoId(jsonArrayToList(jsonObj.getJSONArray("so_id")));

        // Set hp_id
        ra.setHpId(jsonArrayToList(jsonObj.getJSONArray("hp_id")));

        // Set citation
        ra.setCitation(jsonArrayToList(jsonObj.getJSONArray("citation")));

        // Set rdo_pos
        ra.setRdoPos(jsonArrayToList(jsonObj.getJSONArray("rdo_pos")));

        // Set abstractText
        ra.setAbstractText(jsonArrayToList(jsonObj.getJSONArray("abstract")));

        // Set pmid
        ra.setPmid(jsonArrayToList(jsonObj.getJSONArray("pmid")));

        // Set bp_count
        ra.setBpCount(jsonArrayToList(jsonObj.getJSONArray("bp_count")));

        // Set mp_pos
        ra.setMpPos(jsonArrayToList(jsonObj.getJSONArray("mp_pos")));

        // Set hp_count
        ra.setHpCount(jsonArrayToList(jsonObj.getJSONArray("hp_count")));

        // Set bp_pos
        ra.setBpPos(jsonArrayToList(jsonObj.getJSONArray("bp_pos")));

        // Set gene_pos
        ra.setGenePos(jsonArrayToList(jsonObj.getJSONArray("gene_pos")));

        // Set so_pos
        ra.setSoPos(jsonArrayToList(jsonObj.getJSONArray("so_pos")));

        // Set rdo_term
        ra.setRdoTerm(jsonArrayToList(jsonObj.getJSONArray("rdo_term")));

        // Set chebi_id
        ra.setChebiId(jsonArrayToList(jsonObj.getJSONArray("chebi_id")));

        System.out.println(ra.toJSON());

    }

    private static List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }

    private static List<Integer> jsonArrayToIntList(JSONArray jsonArray) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getInt(i));
        }
        return list;
    }
}


/*
{
"gene_count":["7","4","2"],
"mp_id":["MP:0001872"],
"keywords":["3D printer; cadaver surgery; endoscopic surgery; surgical education; surgical training; "],
"mmo_count":["2"],
"doi_s":["10.1002/lio2.873"],
"chebi_pos":["1;1072-1077|1;1163-1168|1;1184-1189|1;1718-1723|1;1941-1946"],
"ma_id":["MA:0002473"],
"pmc_id_s":["PMC9392405"],
"bp_term":["develop","learn"],
"chebi_term":["group"],
"p_date":["2022-08-01"],
"affiliation":["Department of Otolaryngology-Head and Neck Surgery, Faculty of Medicine and Graduate School of Medicine Hokkaido University Sapporo Hokkaido Japan."],
"mesh_terms":[""],
"chebi_count":["5"],
"chemicals":[""],
"mp_term":["sinus"],
"mmo_term":["CT scan"],
"rdo_id":["DOID:0050127","DOID:9002290"],
"nbo_pos":["0;59-64|1;97-102|1;175-180|1;624-629|1;1357-1362|1;2094-2099","0;0-7|1;1246-1253"],
"gene":["<ns1","<\/ns1","FESS"],
"so_term":["score","intermedi","construct","initi","Repeat"],
"ma_pos":["1;930-934|1;1316-1320"],
"mp_count":["5"],"issn":["2378-8038"],
"mmo_pos":["1;665-684|1;898-905"],
"bp_id":["GO:0032502","GO:0007612"],
"p_type":["Journal Article"],
"nbo_count":["6","2"],
"p_year":[2022],
"authors":["Suzuki, Masanobu;  Miyaji, Kou;  Watanabe, Ryosuke;  Suzuki, Takayoshi;  Matoba, Kotaro;  Nakazono, Akira;  Nakamaru, Yuji;  Konno, Atsushi;  Psaltis, Alkis James;  Abe, Takashige;  Homma, Akihiro;  Wormald, Peter-John;  "],
"mmo_id":["MMO:0000128"],
"ma_count":["2"],
"j_date_s":["2022 8 25"],
"ma_term":["face"],
"rdo_count":["5","4"],
"title":["Repetitive simulation training with novel 3D-printed sinus models for functional endoscopic sinus surgeries."],
"nbo_term":["model","repetit"],
"hp_pos":["0;53-58|0;92-97|1;91-96|1;208-213|1;2045-2050"],
"nbo_id":["NBO:0000215","NBO:0000201"],"so_count":["3","2","1","1","1"],
"hp_term":["sinus"],
"so_id":["SO:0001685","SO:0000933","SO:0000804","SO:0000014","SO:0001068"],
"hp_id":["HP:0000246"],
"citation":["Laryngoscope investigative otolaryngology, 2022 8 25, 7(4): 943-954"],
"rdo_pos":["0;53-58|0;92-97|1;91-96|1;208-213|1;2045-2050","1;2037-2042|1;2037-2042|3;12-17|3;12-17"],
"abstract":["Background\tThe purpose of this study was to find a utility of a newly developed 3D-printed sinus model and to evaluate the educational benefit of simulation training with the models for functional endoscopic sinus surgery (FESS).\r\nMaterial and methods\tForty-seven otolaryngologists were categorized as experts (board-certified physicians with ???200 experiences of FESS, \n<ns1:i>\n  n \n<\/ns1:i>??=???9), intermediates (board-certified physicians with &lt;200 experiences of FESS, \n<ns1:i>\n  n \n<\/ns1:i>??=???19), and novices (registrars, \n<ns1:i>\n  n \n<\/ns1:i>??=???19). They performed FESS simulation training on 3D-printed models manufactured from DICOM images of computed tomography (CT) scan of real patients. Their surgical performance was assessed with the objective structured assessment of technical skills (OSATS) score and dissection quality evaluated radiologically with a postdissection CT scan. First we evaluated the face, content, and constructive values. Second we evaluated the educational benefit of the training. Ten novices underwent training (training group) and their outcomes were compared to the remaining novices without training (control group). The training group performed cadaveric FESS surgeries before and after the repetitive training.\r\nResults\tThe feedback from experts revealed high face and content value of the 3D-printed models. Experts, intermediates, and novices demonstrated statistical differences in their OSATS scores (74.7????????3.6, 58.3????????10.1, and 43.1????????11.1, respectively, \n<ns1:i>\n  p \n<\/ns1:i>??&lt;???.001), and dissection quality (81.1????????13.1, 93.7????????15.1, and 126.4????????25.2, respectively, \n<ns1:i>\n  p \n<\/ns1:i>??&lt;???.001). The training group improved their OSATS score (41.1????????8.0 to 61.1????????6.9, \n<ns1:i>\n  p \n<\/ns1:i>??&lt;???.001) and dissection quality (122.1????????22.2 to 90.9????????10.3, \n<ns1:i>\n  p \n<\/ns1:i>??=???.013), while the control group not. After training, 80% of novices with no prior FESS experiences completed surgeries on cadaver sinuses.\r\nConclusion\tRepeated training using the models revealed an initial learning curve in novices, which was confirmed in cadaveric mock FESS surgeries.\r\nLevel of evidence\tN/A.\r\n"],
"pmid":["36000044"],
"bp_count":["1","1"],
"mp_pos":["0;53-58|0;92-97|1;91-96|1;208-213|1;2045-2050"],
"hp_count":["5"],"bp_pos":["1;70-77","1;2121-2126"],
"gene_pos":["1;372-376|1;480-484|1;538-542|1;1533-1537|1;1668-1672|1;1789-1793|1;1889-1893","1;1546-1551|1;1681-1686|1;1802-1807|1;1902-1907","1;365-369|1;473-477"],
"so_pos":["1;822-827|1;1453-1458|1;1745-1750","1;403-412|1;1374-1383","1;949-958","1;2113-2118","1;2066-2072"],
"rdo_term":["sinus","cadav"],
"chebi_id":["CHEBI:24433"]
}
 */