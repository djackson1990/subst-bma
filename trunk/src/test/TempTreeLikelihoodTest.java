package test;

import junit.framework.TestCase;
import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.AlignmentSubset;
import beast.evolution.tree.Tree;
import beast.evolution.substitutionmodel.HKY;
import beast.evolution.substitutionmodel.Frequencies;
import beast.evolution.substitutionmodel.DPNtdBMA;
import beast.evolution.substitutionmodel.NtdBMA;
import beast.evolution.sitemodel.SiteModel;
import beast.evolution.likelihood.TreeLikelihood;
import beast.evolution.likelihood.TempTreeLikelihood;
import beast.core.parameter.RealParameter;
import test.beast.BEASTTestCase;

/**
 * @author Chieh-Hsi Wu
 */
public class TempTreeLikelihoodTest extends TestCase {

    interface Instance {
        Double[] getQParamVal();
        Double[] getModelCodeVal();
        Double[] getFreqsVal();
        double getExpectedLogP(int iSite) throws Exception;
    }



    Instance test0 = new Instance(){
        public SiteModel getSiteModel() throws Exception{
            RealParameter qParam = new RealParameter(getQParamVal());
            RealParameter modelCode = new RealParameter(getModelCodeVal());
            RealParameter freqs = new RealParameter(getFreqsVal());

            NtdBMA ntdBMA = DPNtdBMA.createNtdBMA(
                    qParam,modelCode,freqs);
            SiteModel siteModel = new SiteModel();
            siteModel.initByName(
                    "substModel", ntdBMA
            );

            return siteModel;

        }

        public Double[] getQParamVal(){
            return new Double[]{0.1,0.1,0.1,0.1,0.1,0.1};
        }

        public Double[] getModelCodeVal(){
            return new Double[]{5.0};
        }

        public Double[] getFreqsVal(){
            return new Double[]{0.25,0.25,0.25,0.25};
        }

        public double getExpectedLogP(int iSite) throws Exception{
            Alignment data = BEASTTestCase.getAlignment();
		    Tree tree = BEASTTestCase.getTree(data);
            AlignmentSubset subset = new AlignmentSubset(data, iSite);
            SiteModel siteModel = getSiteModel();
            TreeLikelihood treeLik = new TreeLikelihood();
            treeLik.initByName(
                    "data", subset,
                    "siteModel", siteModel,
                    "tree", tree
            );

            return treeLik.calculateLogP();
        }

    };



    Instance test1 = new Instance(){
        public SiteModel getSiteModel() throws Exception{
            RealParameter qParam = new RealParameter(getQParamVal());
            RealParameter modelCode = new RealParameter(getModelCodeVal());
            RealParameter freqs = new RealParameter(getFreqsVal());

            NtdBMA ntdBMA = DPNtdBMA.createNtdBMA(
                    qParam,modelCode,freqs);
            SiteModel siteModel = new SiteModel();
            siteModel.initByName(
                    "substModel", ntdBMA
            );

            return siteModel;

        }

        public Double[] getQParamVal(){
            return new Double[]{3.0,0.01,0.2,0.1,0.15,-0.1};
        }

        public Double[] getModelCodeVal(){
            return new Double[]{5.0};
        }

        public Double[] getFreqsVal(){
            return new Double[]{0.30,0.30,0.20,0.20};
        }



        public double getExpectedLogP(int iSite) throws Exception{
            Alignment data = BEASTTestCase.getAlignment();
		    Tree tree = BEASTTestCase.getTree(data);
            AlignmentSubset subset = new AlignmentSubset(data, iSite);
            SiteModel siteModel = getSiteModel();
            TreeLikelihood treeLik = new TreeLikelihood();
            treeLik.initByName(
                    "data", subset,
                    "siteModel", siteModel,
                    "tree", tree
            );

            return treeLik.calculateLogP();
        }

    };



    Instance test2 = new Instance(){
        public SiteModel getSiteModel() throws Exception{
            RealParameter qParam = new RealParameter(getQParamVal());
            RealParameter modelCode = new RealParameter(getModelCodeVal());
            RealParameter freqs = new RealParameter(getFreqsVal());

            NtdBMA ntdBMA = DPNtdBMA.createNtdBMA(
                    qParam,modelCode,freqs);
            SiteModel siteModel = new SiteModel();
            siteModel.initByName(
                    "substModel", ntdBMA
            );

            return siteModel;

        }

        public Double[] getQParamVal(){
            return new Double[]{3.0,0.01,0.2,0.1,0.15,-0.1};
        }

        public Double[] getModelCodeVal(){
            return new Double[]{3.0};
        }

        public Double[] getFreqsVal(){
            return new Double[]{0.10,0.20,0.30,0.40};
        }



        public double getExpectedLogP(int iSite) throws Exception{
            Alignment data = BEASTTestCase.getAlignment();
		    Tree tree = BEASTTestCase.getTree(data);
            AlignmentSubset subset = new AlignmentSubset(data, iSite);
            SiteModel siteModel = getSiteModel();
            TreeLikelihood treeLik = new TreeLikelihood();
            treeLik.initByName(
                    "data", subset,
                    "siteModel", siteModel,
                    "tree", tree
            );

            return treeLik.calculateLogP();
        }

    };



    Instance[] all = new Instance[]{test0,test1, test2};
    public void testTreeLikelihoodTest(){
        try{

            Alignment data = BEASTTestCase.getAlignment();
		    Tree tree = BEASTTestCase.getTree(data);

            Frequencies initFreqs = new Frequencies();
		    initFreqs.initByName(
                    "data", data,
                    "estimate", false
            );

            HKY hky = new HKY();
		    hky.initByName(
                    "kappa", "1.0",
                    "frequencies",initFreqs
            );

		    SiteModel siteModel = new SiteModel();
		    siteModel.initByName(
                    "mutationRate", "1.0",
                    "gammaCategoryCount", 1,
                    "substModel", hky
            );

            TempTreeLikelihood ttl = new TempTreeLikelihood();
            ttl.initByName(
                    "data", data,
                    "siteModel", siteModel,
                    "tree", tree
            );
            for(Instance test:all){
                for(int iSite = 0; iSite < data.getSiteCount();iSite++){
                RealParameter qParam = new RealParameter(test.getQParamVal());
                RealParameter modelCode = new RealParameter(test.getModelCodeVal());
                RealParameter freqs = new RealParameter(test.getFreqsVal());
                assertEquals(test.getExpectedLogP(iSite), ttl.calculateLogP(qParam,modelCode,freqs,iSite),1e-10);

                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);
            
        }
    }
}
