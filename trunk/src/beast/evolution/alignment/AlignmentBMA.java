package beast.evolution.alignment;

import beast.core.parameter.IntegerParameter;
import beast.core.Input;
import beast.core.Description;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class allows flexible paritition accross an nucleotide alignment.")
public class AlignmentBMA extends Alignment{
    public Input<IntegerParameter> partitionIndicesInput =
            new Input<IntegerParameter>("partitionIndices", "The indiced of partitions in which the sites are allocated",Input.Validate.REQUIRED);

    private int[][] weightMatrix;
    private int[] partitionWeight;
    private int[] patternIndices;

    public void initAndValidate() throws Exception {
        super.initAndValidate();
        computeWeightsInPartitions();
    }


    public void computeWeightsInPartitions(){
        IntegerParameter partitionIndices = this.partitionIndicesInput.get();
        int patternCount = getPatternCount();
        int siteCount = getSiteCount();
        weightMatrix = new int[siteCount][patternCount];
        partitionWeight = new int[siteCount];
        for(int i = 0; i < siteCount;i++){
            int partitionIndex = partitionIndices.getValue(i);
            weightMatrix[partitionIndex][m_nPatternIndex[i]]++;
            partitionWeight[partitionIndex]++;
        }
    }

    /** CalculationNode implementation **/
    @Override
    protected boolean requiresRecalculation() {
        boolean recalculates = false;
        if(partitionIndicesInput.get().somethingIsDirty()){
            recalculates = true;
            computeWeightsInPartitions();
        }

    	return recalculates;
    }

    public void restore(){
        computeWeightsInPartitions();
        super.restore();
    }

    public int[] getPartitionWeight(){
        return partitionWeight;
    }

    public int[][] getWeightMatrix(){
        return weightMatrix;
    }
        /** SiteComparator is used for ordering the sites,
     * which makes it easy to identify patterns.
     */
    class SpecificSiteComparator extends SiteComparator{

        public int compare(int start, int end, int[] o1, int[] o2) {
            if(start < 0||end >= o1.length ||o1.length != o2.length){
                throw new RuntimeException("Specific site comparator error!");
            }
            for (int i = start; i < end; i++) {
                if (o1[i] > o2[i]) {
                    return 1;
                }
                if (o1[i] < o2[i]) {
                    return -1;
                }
            }
            return 0;
        }
    } // class SiteComparator

    protected void calcPatterns() {
        int nTaxa = m_counts.size();
        int nSites = m_counts.get(0).size();

        // convert data to transposed int array
        int[][] nData = new int[nSites][nTaxa+1];
        for (int i = 0; i < nTaxa; i++) {
            List<Integer> sites = m_counts.get(i);
            for (int j = 0; j < nSites; j++) {
                nData[j][i] = sites.get(j);
            }
        }

        for(int i = 0; i < nSites;i++){
            nData[i][nTaxa] = i;
        }

        // sort data
        SpecificSiteComparator comparator = new SpecificSiteComparator();
        Arrays.sort(nData, comparator);

        // count patterns in sorted data
        int nPatterns = 1;
        int[] weights = new int[nSites];
        patternIndices = new int[nSites];
        weights[0] = 1;

        for (int i = 1; i < nSites; i++) {
            int siteIndex = nData[i][nTaxa];
            //System.out.println(siteIndex);
            if (comparator.compare(0, nTaxa-1,nData[i - 1], nData[i]) != 0) {
                nPatterns++;
                nData[nPatterns - 1] = nData[i];
            }
            weights[nPatterns - 1]++;
            patternIndices[siteIndex]=nPatterns - 1;
        }


        // reserve memory for patterns
        m_nWeight = new int[nPatterns];
        m_nPatterns = new int[nPatterns][nTaxa];
        for (int i = 0; i < nPatterns; i++) {
            m_nWeight[i] = weights[i];
            for(int j = 0; j < nTaxa; j++){
                m_nPatterns[i][j] = nData[i][j];
            }
        }

        // find patterns for the sites
        m_nPatternIndex = new int[nSites];
        for (int i = 0; i < nSites; i++) {
        	int [] sites = new int[nTaxa];
            for (int j = 0; j < nTaxa; j++) {
            	sites[j] = m_counts.get(j).get(i);
            }
            m_nPatternIndex[i] = Arrays.binarySearch(m_nPatterns, sites, comparator);
        }

        // determine maximum state count
        // Usually, the state count is equal for all sites,
        // though for SnAP analysis, this is typically not the case.
        m_nMaxStateCount = 0;
        for(int m_nStateCount1 : m_nStateCounts) {
            m_nMaxStateCount = Math.max(m_nMaxStateCount, m_nStateCount1);
        }
        // report some statistics
        for (int i = 0; i < m_sTaxaNames.size(); i++) {
            System.err.println(m_sTaxaNames.get(i) + ": " + m_counts.get(i).size() + " " + m_nStateCounts.get(i));
        }
        System.err.println(getNrTaxa() + " taxa");
        System.err.println(getSiteCount() + " sites");
        System.err.println(getPatternCount() + " patterns");
    } // calcPatterns

    public int[] getPatternIndices(){
        return patternIndices;
    }

    public int countClusters(){
        ArrayList<Integer> clusterList = new ArrayList<Integer>();
        IntegerParameter partitionIndices = partitionIndicesInput.get();
        int dim  = partitionIndices.getDimension();
        clusterList.add(partitionIndices.getValue(0));
        for(int i = 1; i < dim; i++){
            int partitionIndex = partitionIndices.getValue(i);
            if(!clusterList.contains(partitionIndices.getValue(i))){
                clusterList.add(partitionIndex);
            }
        }

        return clusterList.size();

    }
  

}
