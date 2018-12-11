package edu.mcw.rgd.nlp.utils.gnat;

import gnat.ISGNProperties;
import gnat.client.Run;
import gnat.filter.RunAdditionalFilters;
import gnat.filter.nei.AlignmentFilter;
import gnat.filter.nei.GeneRepositoryLoader;
import gnat.filter.nei.IdentifyAllFilter;
import gnat.filter.nei.ImmediateContextFilter;
import gnat.filter.nei.LeftRightContextFilter;
import gnat.filter.nei.MultiSpeciesDisambiguationFilter;
import gnat.filter.nei.NameValidationFilter;
import gnat.filter.nei.RecognizedEntityUnifier;
import gnat.filter.nei.StopWordFilter;
import gnat.filter.nei.UnambiguousMatchFilter;
import gnat.filter.nei.UnspecificNameFilter;
import gnat.filter.ner.DefaultSpeciesRecognitionFilter;
import gnat.filter.ner.GnatServiceNer;
import gnat.filter.ner.LinnaeusSpeciesServiceNer;
import gnat.filter.ner.RunDictionaries;
import gnat.preprocessing.NameRangeExpander;
import gnat.representation.IdentifiedGene;
import gnat.representation.RecognizedEntity;
import gnat.representation.Text;
import gnat.representation.TextFactory;
import gnat.server.GnatService;
import gnat.utils.AlignmentHelper;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A simple example pipeline for Gene Mention Normalization, which runs a fixed set of filters.
 * <br><br>
 * Uses GnatService for remote NER of species and genes, also obtaining candidate IDs, and
 * a remote GeneService to obtain information on particular genes.<br>
 * - To use the GnatService, the property <tt>gnatServiceUrl</tt> incl. a port has to be set in ISGNProperties (via its XML file).<br>
 * - To use the GeneService, the property <tt>geneRepositoryServer</tt> has to point to the address and port of such a service.<br>
 * <br>
 * 
 * TODO read the list of filters to load from ISGNProperties: 'pipeline' entry
 * 
 * @author J&ouml;rg Hakenberg &lt;jhakenberg@users.sourceforge.net&gt;
 */
public class BasicExamplePipeline {

	/**
	 * 
	 * @param args
	 */
	public static void main (String[] args) {
        System.out.println("Testing a locally running pipeline: human dictionary and accessible database.");          
        System.out.println("Reading configuration from " + ISGNProperties.getPropertyFilename());

        ISGNProperties.loadProperties(new File("/shared/users/wliu/Tools/gnat/isgn_properties.xml"));
        Run run = new Run();
        run.verbosity = 0;

        if (args.length > 0) {
                // the only parameter is -v to regulate verbosity at runtime
                if (args[0].matches("\\-v=\\d+"))
                        run.verbosity = Integer.parseInt(args[0].replaceFirst("^\\-v=(\\d+)$", "$1"));
        }


        //
//        run.setFilenamesAsPubMedId();

        // Pre-processing filter here:
//        run.addFilter(new NameRangeExpander());

        // NER filters here:
        // default species NER: spots human, mouse, rat, yeast, and fly only
        run.addFilter(new DefaultSpeciesRecognitionFilter());
        // genes via a GnatService
      GnatServiceNer gnatServiceNer = new GnatServiceNer(GnatService.Tasks.GENE_NORM);
//      // tell the remote service to run only for a few species:
      gnatServiceNer.setLimitedToTaxa(9606, 10090, 10116); // only human genes
      gnatServiceNer.useDefaultSpecies = true;
      run.addFilter(gnatServiceNer);
        // construct a dictionary for human genes only
        RunDictionaries humanDictionaryFilter = new RunDictionaries();
        humanDictionaryFilter.addLimitToTaxon(9606);
        run.addFilter(humanDictionaryFilter);

        // NER post-processing filters here:
        run.addFilter(new RecognizedEntityUnifier());

        // include a few disambiguation filters that do not need specific information on each candidate gene
        // thus, these work on the gene's name and its context in the text
//        run.addFilter(new ImmediateContextFilter());

        // strictFPs_2_2_context_all.object contains data on the context defined by two tokens left and two tokens right of a gene name
//        run.addFilter(new LeftRightContextFilter("/shared/users/wliu/Tools/gnat/data/strictFPs_2_2_context_all.object", "/shared/users/wliu/Tools/gnat/data/nonStrictFPs_2_2_context_all.object", 0d, 2, 2));

        // load the gene repository to obtain information on each gene (if only the species)
        // not loading gene repository will produce an empty result at the end
        run.addFilter(new GeneRepositoryLoader(GeneRepositoryLoader.RetrievalMethod.DATABASE));

        //
//        run.addFilter(new StopWordFilter(ISGNProperties.get("stopWords")));
        //
//        run.addFilter(new UnambiguousMatchFilter());
        //
//        run.addFilter(new UnspecificNameFilter());

        //
//        run.addFilter(new AlignmentFilter(AlignmentHelper.globalAlignment, 0.7f));

//        run.addFilter(new MultiSpeciesDisambiguationFilter(
//                Integer.parseInt(ISGNProperties.get("disambiguationThreshold")),
//                Integer.parseInt(ISGNProperties.get("maxIdsForCandidatePrediction"))));

        //
        run.addFilter(new IdentifyAllFilter());

        run.addText(new Text("1", "FAM109A"));
        // run all filters, changing run.context, run.textRepository, and run.geneRepository

//      List<RecognizedEntity> sortedREs =
//              run.context.sortRecognizedEntities(run.context.getRecognizedEntities());
//      for (RecognizedEntity re: sortedREs)
//              System.out.println(re.getText().getID() + "\t" + re.getName() + "\t" + re.getBegin() + "\t" + re.getEnd());
//      if (true) return;

        // get the results for each text, in BioCreative tab-separated format
        
        run.addText(new Text("Test-1", "ApoB"));
		run.addText(new Text("Test-2", "VEGF"));
		run.addText(new Text("Test-3", "PAVMs"));
		run.addText(new Text("Test-4", "SES"));
		run.addText(new Text("Test-5", "ApoB"));
		run.addText(new Text("Test-6", "VEGF"));
		run.addText(new Text("Test-7", "PAVMs"));
		run.addText(new Text("Test-8", "SES"));
		run.addText(new Text("Test-9", "ApoB"));
		run.addText(new Text("Test-10", "VEGF"));
		run.addText(new Text("Test-11", "VEGF"));
		run.addText(new Text("Test-12", "VEGF"));
		run.addText(new Text("Test-13", "VEGF"));
		run.addText(new Text("Test-14", "VEGF"));
		run.addText(new Text("Test-15", "VEGF"));
		run.addText(new Text("Test-16", "VEGF"));
        // run all filters, changing run.context, run.textRepository, and run.geneRepository
        run.runFilters();
        
//      List<RecognizedEntity> sortedREs =
//              run.context.sortRecognizedEntities(run.context.getRecognizedEntities());
//      for (RecognizedEntity re: sortedREs)
//              System.out.println(re.getText().getID() + "\t" + re.getName() + "\t" + re.getBegin() + "\t" + re.getEnd());
//      if (true) return;

        // get the results for each text, in BioCreative tab-separated format
        
        Iterator<IdentifiedGene>igi = run.context.getIdentifiedGenesIterator();
        while (igi.hasNext()) {
        	IdentifiedGene igGene = igi.next();
        	System.out.println(igGene.getName() + " " + igGene.getGene().getID() + " " + igGene.getConfidenceScore());
        }
        // get expected results from saved file

	}

	static void addExampleTexts(Run run) {
		// add some individual mocked-up texts:
		// #1
//		Text text = new Text("Test-1", "Selective TRAIL-triggered apoptosis due to overexpression of TRAIL death receptor 5 (DR5) in P-glycoprotein-bearing multidrug resistant CEM/VBL1000 human leukemia cells.");
//		text.setPMID(20953314);
//		// also add a species that is relevant to the text
//		//		text.taxonIDs.add(9606);
//		run.addText(text);
//
//		// #2, without species
//		run.addText(new Text("Test-2", "Fas and a simple test with FADD, both involved in apoptosis."));
//
//		// #3, with two species, one (9606) is irrelevant for the gene 'Fas' mentioned here
//		Text text2 = new Text("Test-3", "Another simple test with murine Fas.");
//		//		text2.taxonIDs.add(9606);
//		//		text2.taxonIDs.add(10090);
//		run.addText(text2);
//
		// #3, with two species, one (9606) is irrelevant for the gene 'Fas' mentioned here
//		Text text3 = new Text("Test-4", "ApoB");
		//		text3.taxonIDs.add(7227);
		run.addText(new Text("Test-1", "ApoB"));
		run.addText(new Text("Test-2", "VEGF"));
		run.addText(new Text("Test-3", "PAVMs"));
		run.addText(new Text("Test-4", "SES"));
		
//		Text text3 = new Text("Test-4", "mTOR");
//		//		text3.taxonIDs.add(7227);
//		run.addText(text3);
	}
}