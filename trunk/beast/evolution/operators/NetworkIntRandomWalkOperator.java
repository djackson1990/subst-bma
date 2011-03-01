package beast.evolution.operators;

import beast.core.*;
import beast.core.parameter.IntegerParameter;
import beast.evolution.substitutionmodel.NtdBMA;
import beast.util.Randomizer;

import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 *
 */
public class NetworkIntRandomWalkOperator extends Operator {


    public Input<ArrayList<Vertex>> vertices = new Input<ArrayList<Vertex>>("vertex", "Unique id number of the vertex.");
    public Input<IntegerParameter> parameter = new Input<IntegerParameter>("parameter", "Vertex indicator which indicates the current vertex in the network.");
    double[][] logHastingsRatios;
    boolean[][] permittedRoutes;
    int[][] neighbours;


    public void initAndValidate() throws Exception {
        ArrayList<Vertex> vertices = this.vertices.get();
        hasSingleComponent(vertices);
        neighbours = new int[vertices.size()][];

        for(int i = 0; i < neighbours.length;i++){
            neighbours[i] = vertices.get(i).getNeighbours();
        }

        logHastingsRatios = new double[vertices.size()][vertices.size()];
        permittedRoutes = new boolean[vertices.size()][vertices.size()];
        int vertexCount = vertices.size();
        for(int i = 0; i < vertexCount;i++){
            for(int j = 0; j < neighbours.length; j++){
                permittedRoutes[i][neighbours[i][j]]= true;
            }
        }

        for(int i = 0; i < vertexCount; i++){
            for(int j = i+1; j < vertexCount;j++){
                if(permittedRoutes[i][j] != permittedRoutes[j][i]){
                    System.err.println("Route specified is not symmetrical");
                    System.err.println((permittedRoutes[i][j]) ? "Can ":"Cannot "+"get from "+i+" to "+ j);
                    System.err.println((permittedRoutes[j][i]) ? "Can ":"Cannot "+"get from "+j+" to "+ i);
                    throw new RuntimeException();
                }else{
                    logHastingsRatios[i][j] = Math.log(neighbours[i].length)-Math.log(neighbours[j].length);
                    logHastingsRatios[j][i] = Math.log(neighbours[j].length)-Math.log(neighbours[i].length);

                }
            }
        }

    }

    public double proposal(){

        IntegerParameter parameter = this.parameter.get(this);
        int currVertex = parameter.getValue(0);
        int nextVertex = neighbours[currVertex][Randomizer.nextInt()*neighbours[currVertex].length];
        parameter.setValue(0,nextVertex);

        return logHastingsRatios[currVertex][nextVertex];

    }

    
    public void hasSingleComponent(ArrayList<Vertex> vertices){

        ArrayList<Vertex> compQ = new ArrayList<Vertex>();
        compQ.add(vertices.get(0));
        ArrayList<Integer> markedQ = new ArrayList<Integer>();
        markedQ.add(vertices.get(0).getId());

        while(compQ.size() >0){

            Vertex v = compQ.remove(0);
            int[] neighbours = v.getNeighbours();

            for(int j = 0; j < neighbours.length; j++){

                if(markedQ.indexOf(neighbours[j]) == -1){

                    markedQ.add(neighbours[j]);
                    compQ.add(vertices.get(neighbours[j]));

                }
            }

        }

        if(markedQ.size() != vertices.size()){
            throw new RuntimeException("There are disjointed components in the network");
        }
    }





    /** basic implementation of a SubstitutionModel bringing together relevant super class**/
    public class Vertex extends Plugin {
        public static final String ID_NUM = "idNum";
        public static final String NEIGHBOURS = "neighbours";
        public Input<Integer> idNum = new Input<Integer>(ID_NUM, "Unique id number of the vertex.");
        public Input<String> neighbours = new Input<String>(NEIGHBOURS, "A string of the id number of adjacent nodes separated by a while space");

        private int id;
        private int[] neighbourIds;

        public void initAndValidate() throws Exception {
            id = idNum.get();
            String[] neighboursStr = neighbours.get().split("\\s+");
            neighbourIds = new int[neighboursStr.length];
            for(int i = 0; i < neighboursStr.length;i++){
                neighbourIds[i] = Integer.parseInt(neighboursStr[i]);
            }

        }

   

        public int getId(){
            return id;
        }

        public int[] getNeighbours(){
            return neighbourIds;
        }

        public String toString(){
            return "Id: "+id+ ", neighbours: "+neighbours.get() ;
        }


    }


    public static void main(String[] args){
        NetworkIntRandomWalkOperator op = new NetworkIntRandomWalkOperator();
        try{
            op.testHasSingleComponent();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public void testHasSingleComponent() throws Exception{
        Vertex jc = new Vertex();
        jc.initByName(
                Vertex.ID_NUM, NtdBMA.JC,
                Vertex.NEIGHBOURS, "1 2"
        );

        Vertex k80 = new Vertex();
        k80.initByName(
                Vertex.ID_NUM, NtdBMA.K80,
                Vertex.NEIGHBOURS, "0 3"
        );

        Vertex f81 = new Vertex();
        f81.initByName(
                Vertex.ID_NUM, NtdBMA.K80,
                Vertex.NEIGHBOURS, "0 3"
        );

        Vertex hky85 = new Vertex();
        hky85.initByName(
                Vertex.ID_NUM, NtdBMA.HKY,
                Vertex.NEIGHBOURS, "1 2 4"
        );

        Vertex tn93 = new Vertex();
        tn93.initByName(
                Vertex.ID_NUM, NtdBMA.TN,
                Vertex.NEIGHBOURS, "4 5"
        );

        Vertex gtr = new Vertex();
        gtr.initByName(
                Vertex.ID_NUM, NtdBMA.GTR,
                Vertex.NEIGHBOURS, "4"
        );

        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        vertices.add(jc);
        vertices.add(k80);
        vertices.add(f81);
        vertices.add(hky85);
        vertices.add(tn93);
        vertices.add(gtr);

        for(int i = 0; i < vertices.size(); i++){
            System.out.println(vertices.get(i));
        }

        hasSingleComponent(vertices);


    }


}
