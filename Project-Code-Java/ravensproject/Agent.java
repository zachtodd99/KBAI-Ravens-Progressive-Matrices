package ravensproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

//
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.FlowLayout;
//

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
	
	JFrame frame;
	double comparabilityThreshhold = 0.02;
	String[] listedTransforms = new String[] {"Fill","Add","Delete","Match","Horizontal", "Vertical","45","90","135","180","225","270","315"};
	
	
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
    public Agent() {
        
    }
    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return an int representing its
     * answer to the question: 1, 2, 3, 4, 5, or 6. Strings of these ints 
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName(). Return a negative number to skip a problem.
     * 
     * Make sure to return your answer *as an integer* at the end of Solve().
     * Returning your answer as a string may cause your program to crash.
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public int Solve(RavensProblem problem) {
    	frame = new JFrame();
    	frame.getContentPane().setLayout(new FlowLayout());
    	int answer;
    	
    	if(problem.getProblemType().equals("2x2")){
    		return -1;
//    		answer = solve2x2(problem);
    	}else{
    		answer = solve3x3(problem);
    	}
    	//frame.dispose();
        return answer;
    }
    
    
    final class ConfidencePairing{
    	public int answerIndex;
    	public double confidence;
    	
    	public ConfidencePairing(int a, double c){
    		this.answerIndex = 1;
    		this.confidence = c;
    	}
    }
    
    final class Coordinate {
		public double xVal;
		public double yVal;
		
		public Coordinate(double d, double e){
			this.xVal = d;
			this.yVal = e;
		}
    }
    
    
    final class AnswerFrame{
    	public double confidence;
    	public ImageFrame imageFrame;
    	public int index;
    	private int confidenceParameters = 0;
    	
    	public AnswerFrame(BufferedImage image, int index){
    		this.imageFrame = new ImageFrame(image, "" + index);
    		this.index = index;
    	}
    	
    	public double addConfidenceRating(double conf){
    		this.confidenceParameters++;
    		this.confidence = Math.sqrt((Math.pow(this.confidence, 2)*(this.confidenceParameters-1) + Math.pow(conf, 2))/this.confidenceParameters);
    		
    		return this.confidence;
    	}
    	
    }
    
    
    final class ImageFrame{
    	public LabelClass lc;
    	public BufferedImage image;
    	public ArrayList<BufferedImage> subImages = new ArrayList<BufferedImage>();
    	public ArrayList<Integer> subImageLabels = new ArrayList<Integer>();
    	public String name;
    	public ImageFrame(BufferedImage image, String name){
    		this.image = image;
    		this.lc = new LabelClass(this.image);
    		this.name = name;
//    		generateSubImages();
    	}
    	
    	
	    public void generateSubImages(){
	
	    		lc.removeUnusedLabels();
	    		for(Integer key : this.lc.labelColor.keySet()){
	    			if(this.lc.labelColor.get(key).equals("b")){
	
	    				this.subImages.add(new BufferedImage(this.image.getHeight(),this.image.getWidth(), this.image.getType()));
	//    				System.out.println("color:::::::  " + this.subImages.get(this.subImages.size()-1).getRGB(10, 10));
	    				this.subImageLabels.add(key);
	
	    			}
	    		}
	    		
	    		for(int y = 0; y<this.image.getHeight(); y++){
	        		for(int x = 0; x<this.image.getWidth();x++){
	        			int thisLabel = -1;
	        			if(this.subImageLabels.contains(this.lc.labels[x][y])){
	        				thisLabel = this.lc.labels[x][y];
	        				this.subImages.get(this.subImageLabels.indexOf(thisLabel)).setRGB(x,y,-16777216);
	        			}	
	        			
	        			for(int i = 0; i<this.subImages.size(); i++){
	        				if(thisLabel!=this.subImageLabels.get(i)){
	        					this.subImages.get(i).setRGB(x,y,-1);
	        				}
	        			}
	        			
	        			
	        		}
	    		}
//	    		BufferedImage[] buff = new BufferedImage[this.subImages.size() + 1];
//	    		buff[0] = this.image;
//	    		for(int x = 0; x<this.subImages.size(); x++){
//	    			buff[x+1] = this.subImages.get(x);
//	    		}
//	    		System.out.print("\nKeys: ");
//	    		for(int key : this.lc.labelColor.keySet()){
//	    			System.out.print(key + ": " + this.lc.labelColor.get(key) + ", ");
//	    		}
//	    		
//	    		displayImages(buff);
	    	}
	    	
	    	
	    public int getCenterImageLabel(){
	    		
	    		int label = this.lc.labels[(int) (this.image.getWidth()/2)][(int) (this.image.getHeight()/2)];
	    		
	    		if(!this.lc.labelColor.get(label).equals("b")){
	    			return -1;
	    		}
	    		
	    		return label;
	    	}
	    }
    
    
    final class LabelClass{
    	public Hashtable<Integer, Integer> labelCount = new Hashtable<Integer,Integer>();
    	public Hashtable<Integer, String> labelColor= new Hashtable<Integer,String>();
    	
    	public int[][] labels;
    	
    	public LabelClass(){
    		
    	}

    	public LabelClass(int imageX, int imageY){
    		labels = new int[imageX][imageY];
    	}
    	
    	public LabelClass(BufferedImage image){
    		generateLabels(image);
    	}
    	
    	public void generateLabels(BufferedImage image){

        	this.labels = new int[image.getHeight()][image.getWidth()];
        	ArrayList<ArrayList<Integer>> connected = new ArrayList<ArrayList<Integer>>();
        	ArrayList<Integer> roots = new ArrayList<Integer>();
        	
        	ArrayList<Integer> neighborsX = new ArrayList<Integer>();
        	ArrayList<Integer> neighborsY = new ArrayList<Integer>();
        	int currentSet = 1;
        	

        	for(int y = 0; y<image.getHeight(); y++){
        		for(int x = 0; x<image.getWidth();x++){
        			
        			//get neighbors
        			int thisColor = image.getRGB(x, y);
    				neighborsX = new ArrayList<Integer>();
    				neighborsY = new ArrayList<Integer>();
    				
    				if(x-1>=0 && y-1>=0){
    					if(image.getRGB(x-1, y-1) == thisColor){
    						neighborsX.add(x-1);
    						neighborsY.add(y-1);
    					}
    				}
    				
    				if(y-1>=0){
    					if(image.getRGB(x, y-1) == thisColor){
    						neighborsX.add(x);
    						neighborsY.add(y-1);
    					}
    				}
    				
    				if(x+1<image.getWidth() && y-1>=0){
    					if(image.getRGB(x+1, y-1) == thisColor){
    						neighborsX.add(x+1);
    						neighborsY.add(y-1);
    					}
    				}
    				
    				if(x-1>=0){
    					if(image.getRGB(x-1, y) == thisColor){
    						neighborsX.add(x-1);
    						neighborsY.add(y);
    					}
    				}
    				

        			//check neighbors and assign label
        			if(neighborsX.isEmpty()){
        				connected.add(new ArrayList<Integer>());
        				connected.get(connected.size()-1).add(currentSet);
        				this.labels[x][y] = currentSet;
        				if(thisColor == -16777216){
        					this.labelColor.put(currentSet, "b");
        				}else{
        					this.labelColor.put(currentSet, "w");
        				}
        				currentSet++;
        			}else{
        				int minLabel = currentSet+1;
        				ArrayList<Integer> L = new ArrayList<Integer>();
//    	    				System.out.println(x + " " + y);
//    	    				System.out.println("NeighborsX " + neighborsX.toString() + " NeighborsY " + neighborsY.toString());
        				for(int count = 0; count<neighborsX.size(); count++){
        					if(this.labels[neighborsX.get(count)][neighborsY.get(count)]!=0){ //label is 0 if it hasnt been visited yet, also wont check itself
        						L.add(this.labels[neighborsX.get(count)][neighborsY.get(count)]);
    	    					if(L.get(L.size()-1) < minLabel){
    	    						minLabel = L.get(L.size()-1);
    	    					}
        					}
        				}
        				//System.out.println("labels: " + L.toString());
        				if(L.isEmpty()){
        					connected.add(new ArrayList<Integer>());
            				connected.get(connected.size()-1).add(currentSet);
            				this.labels[x][y] = currentSet;
            				if(thisColor == -16777216){
            					this.labelColor.put(currentSet, "b");
            				}else{
            					this.labelColor.put(currentSet, "w");
            				}
            				currentSet++;
        				}else{
        					this.labels[x][y] = minLabel;
        					for(int lab : L){
    	    					for(int lab2 : L){
    	    						if(!connected.get(lab-1).contains(lab2) && this.labelColor.get(lab) == this.labelColor.get(lab2)){
    	    							connected.get(lab-1).add(lab2);
    	    						}
    		    						
    	    }}}}}}
        	
        	for(int count = 0; count < connected.size(); count++){
        		int min = connected.size()+2;
        		//System.out.println(connected.get(count));
        		for(int myInt : connected.get(count)){
        			min = myInt < min ? myInt : min;
        		}
        		roots.add(min);
        	}

        	for(int x = 0; x < roots.size(); x++){
        		int root = x;
        		
        		while(roots.get(root) != root+1){
        			root = roots.get(root)-1;
        		}
        		
        		while(roots.get(x)-1 != root){
        			int parent = roots.get(x)-1;//This line is the worst. Took me 3 hours to debug that I needed a "-1" here, but it works!!!!!
        			roots.set(x, root+1);
        			x = parent;
        		}
        		
        		
        	}
//        	System.out.println("sdjfkln" + roots.toString());

        	for(int y = 0; y<image.getHeight(); y++){
        		for(int x = 0; x<image.getWidth();x++){	
        			
    				this.labels[x][y] = roots.get(this.labels[x][y]-1); // set the label to the lowest label this is connected to
    				//can increment label count here
//        			System.out.print(labels[x][y]);
        		}
//        		System.out.println();
        	}
          	//this.removeUnusedLabels();


    	}
    	
    	public int getNumShapes(){
    		
    		this.removeUnusedLabels();
    		
    		int count = 0;
    		for(int key : labelColor.keySet()){
    			if(labelColor.get(key).equals("b")){ //check how many labels are black
    				count++;
    			}
    		}
    		return count;
    	}
    	
    	
    	
    	public void removeUnusedLabels(){
    		ArrayList<Integer> foundLabels = new ArrayList<Integer>();
    		
    		for(int row = 0; row<labels.length; row++){
    			for(int col = 0; col<labels[0].length; col++){
    				if(!foundLabels.contains(labels[row][col])){
    					foundLabels.add(labels[row][col]);
    				}
    			}
    		}
    		
    		ArrayList<Integer> toRemove = new ArrayList<Integer>();
    		
    		for(Integer key : labelColor.keySet()){
    			if(!foundLabels.contains(key)){
    				toRemove.add(key);
    			}
    		}
    		
    		for(Integer key : toRemove){
    			labelColor.remove(key);
    		}
    	}
    	
    	
    }
    
    final class SolutionOrTransforms{
    	public int answer;
    	public ArrayList<String> horizontalTransforms;
    	public ArrayList<String> verticalTransforms;
    	public double[] answerConfidence;
    	public SolutionOrTransforms(int answer, String[] h, String[] v){
    		this.answer = answer;
    		for (String s : h){
    			this.horizontalTransforms.add(s);
    		}
    		for (String s : v){
    			this.verticalTransforms.add(s);
    		}
    		
    	}
    	public SolutionOrTransforms(){
    		this.answer = -1;
    		this.horizontalTransforms = new ArrayList<String>();
    		this.verticalTransforms = new ArrayList<String>();
    		this.answerConfidence = new double[6];
    		this.horizontalTransforms.add("None");
    		this.verticalTransforms.add("None");
    	}
    	
    	public void addHorizontal(String h){
    		if(this.horizontalTransforms.size()>0 && this.horizontalTransforms.get(0).equals("None")){
    			this.horizontalTransforms.remove(0);
    		}
    		this.horizontalTransforms.add(h);    
    	}
    	
    	
    	public void addVertical(String v){
    		if(this.verticalTransforms.size()>0 && this.verticalTransforms.get(0).equals("None")){
    			this.verticalTransforms.remove(0);
    		}
    		this.verticalTransforms.add(v);
    	}
    }
    
	
	private int solve2x2(RavensProblem problem) {
		
//		return -1;
		
////		if(!problem.getName().equals("Basic Problem B-10")){
////		return -1;
////		}
		System.out.println("Problem " + problem.getName());
		HashMap<String, RavensFigure> figures = problem.getFigures();
		ArrayList<String> knownHorizontalTransforms;
		ArrayList<String> knownVerticalTransforms;
		ArrayList<RavensFigure> myFigures = new ArrayList<RavensFigure>();
		myFigures.add(figures.get("A"));
		myFigures.add(figures.get("B"));
		myFigures.add(figures.get("C"));
		
		RavensFigure[] answers = new RavensFigure[6];
		
		for(int x = 0; x<6; x++){
			answers[x] = figures.get("" + (x+1));
		}
		//System.out.println(problem.getName());
		SolutionOrTransforms simpleCheckAnswer = simpleCheck2(myFigures, answers);
		if(simpleCheckAnswer.answer != -1){
			return simpleCheckAnswer.answer;
		}
		
		knownHorizontalTransforms = simpleCheckAnswer.horizontalTransforms;
		knownVerticalTransforms = simpleCheckAnswer.verticalTransforms;
		
		
		double[] confidence = new double[6];
		double maxConfidence = 0.0;
		int maxIndex = -1;
		for(int x = 0; x<6; x++){
			RavensFigure currentAnswer = figures.get("" + (x+1));
			if(confidence[x] > maxConfidence){
				maxConfidence = confidence[x];
				maxIndex = x;
			}
		}
		
		return maxIndex;
		
	}
	
	
    private int solve3x3(RavensProblem problem) {
//		if(!problem.getName().equals("Basic Problem D-04")){
//			return -1;
//		}
    	System.out.println("\n" + problem.getName());
    	HashMap<String, RavensFigure> figures = problem.getFigures();
    	
    	BufferedImage[] given = new BufferedImage[8];
    	ImageFrame[] givenFrames = new ImageFrame[8];
    	ArrayList<AnswerFrame> answerFrames = new ArrayList<AnswerFrame>();
    	problem = null;
    	try{
    	
	    	given[0] = makeBandW(ImageIO.read(new File(figures.get("A").getVisual())));
	    	given[1] = makeBandW(ImageIO.read(new File(figures.get("B").getVisual())));
	    	given[2] = makeBandW(ImageIO.read(new File(figures.get("C").getVisual())));
	    	given[3] = makeBandW(ImageIO.read(new File(figures.get("D").getVisual())));
	    	given[4] = makeBandW(ImageIO.read(new File(figures.get("E").getVisual())));
	    	given[5] = makeBandW(ImageIO.read(new File(figures.get("F").getVisual())));
	    	given[6] = makeBandW(ImageIO.read(new File(figures.get("G").getVisual())));
	    	given[7] = makeBandW(ImageIO.read(new File(figures.get("H").getVisual())));
	    	
			
			for(int x = 0; x<8; x++){
				BufferedImage im = makeBandW(ImageIO.read(new File(figures.get("" + (x+1)).getVisual())));
				answerFrames.add(new AnswerFrame(im,x+1));

			}
		
		}catch (IOException e) {
			System.out.println("no image available for some figure in 3x3: " + e.toString());
			return -1;
		}
		
		
		//determine shape count and try to see if a pattern exists
		BufferedImage[] answerArray = new BufferedImage[8];
		for(int x = 0; x<8; x++){
			answerArray[x] = answerFrames.get(x).imageFrame.image;
			givenFrames[x] = new ImageFrame(given[x], "g" + x);
		}

		double[] shapeConfidence = getShapeConfidence(getShapeCount(given), getShapeCount(answerArray));
		ArrayList<Integer> goodShapeCounts = new ArrayList<Integer>();
		for(int x = 7; x>=0; x--){
			if(shapeConfidence[x] == 0.0){
//				answerFrames.remove(x);
				goodShapeCounts.add(x);
			}
		}
		answerArray = null;
		//if only one answer contains the correct number of shapes, return it.
		if(answerFrames.size()==1){
		//if(goodShapeCounts.size()==1){
			System.out.println("found answer from shape check");
			return goodShapeCounts.get(0);
//			return answerFrames.get(0).index;
		}
		
		//answers have been eliminated, which greatly speeds up execution time.
		
		//diagonal sameness check
//		System.out.println("diagonal comparability: " + );
		if(comparability3(given[0],given[4]) < this.comparabilityThreshhold){
			
			double bestComparibility = 1.0;
			int bestIndex = answerFrames.get(0).index;
			
			for(int x = 0; x < answerFrames.size(); x++){
				double comp1 = comparability3(given[4], answerFrames.get(x).imageFrame.image);
				double comp2 = comparability3(given[0], answerFrames.get(x).imageFrame.image);
				if(Math.max(comp1, comp2)<bestComparibility){
					bestComparibility = Math.max(comp1, comp2);
					bestIndex = answerFrames.get(x).index;
				}
				
			}
			System.out.println("Diagonals are the same!!!!~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + bestComparibility);
			if(bestComparibility<0.02){
				System.out.println("found answer from diagonals " + bestComparibility);
				return bestIndex;
			}
		}
		

		ArrayList<BufferedImage> answers = new ArrayList<BufferedImage>();
		for(AnswerFrame frame : answerFrames){
			answers.add(frame.imageFrame.image);
		}
		int count = 0;
		
		double[] centerImageConf =  centerImageCountCheck(givenFrames, answerFrames);
		System.out.println("center image count check: " + Arrays.toString(centerImageConf));
		if(centerImageConf[0]!=-1){
			count = 0;
			int numLessThan1 = 0;
			for(AnswerFrame f : answerFrames){
				f.addConfidenceRating(centerImageConf[count]);
				if(centerImageConf[count]<1.0){
					numLessThan1++;
				}
				count++;
			}
			
			if(numLessThan1==1){
				count = 0;
				for(AnswerFrame f : answerFrames){
					if(centerImageConf[count]<1.0){
						System.out.println("Found only one answer from centerImage");
						return f.index;
					}
					count++;
				}
			}
			
//			if(getBestConfidenceIndex(answerFrames) != -1){
//				System.out.println("Found answer from Center Image Check");
//				return getBestConfidenceIndex(answerFrames);
//			}
		}

		
		//check unary confidence
		double[] unaryConf = checkUnary(given,answers);
		System.out.println("Unary conf: " + Arrays.toString(unaryConf));
		if(unaryConf[0]!=-1){
			count = 0;
			for(AnswerFrame f : answerFrames){
				if(unaryConf[count] == 0.0){
					System.out.println("Found exact answer from Unary");
					return f.index;
				}
				f.addConfidenceRating(unaryConf[count]);
				count++;
			}
			

		}
		
		//check binary confidence
		double[] binaryVConf = checkVBinary(given,answers);
		double[] binaryHConf = checkHBinary(given,answers);
		System.out.println("binary V conf = " + Arrays.toString(binaryVConf));
		System.out.println("binary H conf = " + Arrays.toString(binaryHConf));
		if(binaryVConf[0]!=-1){
			count = 0;
			int numLessThan1 = 0;
			for(AnswerFrame f : answerFrames){
				if(binaryVConf[count] == 0.0){
					System.out.println("Found exact answer from V Binary");
					return f.index;
				}
				if(binaryVConf[count]<1.0){
					numLessThan1++;
				}
				f.addConfidenceRating(binaryVConf[count]);
				count++;
			}
			if(numLessThan1==1){
				count = 0;
				for(AnswerFrame f : answerFrames){
					if(binaryVConf[count]<1.0){
						System.out.println("Found only one answer from V Binary");
						return f.index;
					}
					count++;
				}
			}
			
		}
		
		if(binaryHConf[0]!=-1){
			count = 0;
			int numLessThan1 = 0;
			for(AnswerFrame f : answerFrames){
				if(binaryHConf[count] == 0.0){
					System.out.println("Found exact answer from H Binary");
					return f.index;
				}
				if(binaryHConf[count]<1.0){
					numLessThan1++;
				}
				f.addConfidenceRating(binaryHConf[count]);
				count++;
			}
			if(numLessThan1==1){
				count = 0;
				for(AnswerFrame f : answerFrames){
					if(binaryHConf[count]<1.0){
						System.out.println("Found only one answer from H Binary");
						return f.index;
					}
					count++;
				}
			}
			
		}
		
//		if(getBestConfidenceIndex(answerFrames) != -1){
//			System.out.println("Found answer from Binary");
//			return getBestConfidenceIndex(answerFrames);
//		}
		
		
		double[] sameHFractalConf = getSameHFractalCheck(givenFrames,answerFrames);
		double[] sameVFractalConf = getSameVFractalCheck(givenFrames,answerFrames);
		double[] sameHFractalConf2 = getSameHFractalCheck2(givenFrames,answerFrames);
		double[] sameVFractalConf2 = getSameVFractalCheck2(givenFrames,answerFrames);
		
		System.out.println("H Fractal conf: " + Arrays.toString(sameHFractalConf));
		System.out.println("V Fractal conf: " + Arrays.toString(sameVFractalConf));
		System.out.println("H2 Fractal conf: " + Arrays.toString(sameHFractalConf2));
		System.out.println("V2 Fractal conf: " + Arrays.toString(sameVFractalConf2));
		if(sameHFractalConf[0]!=-1){
			count = 0;
			int numLessThan1 = 0;
			for(AnswerFrame f : answerFrames){
				f.addConfidenceRating(sameHFractalConf[count]);
				if(sameHFractalConf[count]<1.0){
					numLessThan1++;
				}
				count++;
			}
			
			if(numLessThan1==1){
				count = 0;
				for(AnswerFrame f : answerFrames){
					if(sameHFractalConf[count]<1.0){
						System.out.println("Found only one answer from sameHFractal");
						return f.index;
					}
					count++;
				}
			}
		}
		if(sameVFractalConf[0]!=-1){
			count = 0;
			int numLessThan1 = 0;
			for(AnswerFrame f : answerFrames){
				f.addConfidenceRating(sameVFractalConf[count]);
				if(sameVFractalConf[count]<1.0){
					numLessThan1++;
				}
				count++;
			}
			
			if(numLessThan1==1){
				count = 0;
				for(AnswerFrame f : answerFrames){
					if(sameVFractalConf[count]<1.0){
						System.out.println("Found only one answer from sameVFractal");
						return f.index;
					}
					count++;
				}
			}
		}
		
		if(sameHFractalConf2[0]!=-1){
			count = 0;
			int numLessThan1 = 0;
			for(AnswerFrame f : answerFrames){
				f.addConfidenceRating(sameHFractalConf2[count]);
				if(sameHFractalConf2[count]<1.0){
					numLessThan1++;
				}
				count++;
			}
			
			if(numLessThan1==1){
				count = 0;
				for(AnswerFrame f : answerFrames){
					if(sameHFractalConf2[count]<1.0){
						System.out.println("Found only one answer from sameHFractal2");
						return f.index;
					}
					count++;
				}
			}
		}
		
		if(sameVFractalConf2[0]!=-1){
			count = 0;
			int numLessThan1 = 0;
			for(AnswerFrame f : answerFrames){
				f.addConfidenceRating(sameVFractalConf2[count]);
				if(sameVFractalConf2[count]<1.0){
					numLessThan1++;
				}
				count++;
			}
			
			if(numLessThan1==1){
				count = 0;
				for(AnswerFrame f : answerFrames){
					if(sameVFractalConf2[count]<1.0){
						System.out.println("Found only one answer from sameVFractal2");
						return f.index;
					}
					count++;
				}
			}
		}
		
		if(getBestConfidenceIndex(answerFrames) != -1){
			System.out.println("Found answer from same fractal check");
			return getBestConfidenceIndex(answerFrames);
		}
		//check pixel confidence
		double[] pixelConf = checkPixels(given, answers);
		System.out.println("Pixel Conf: " + Arrays.toString(pixelConf));
		
		if(pixelConf[0]!=-1){
			count = 0;
			for(AnswerFrame f : answerFrames){
				f.addConfidenceRating(pixelConf[count]);
				count++;
			}
		}
		
		
		
		
		
		
		double lowest = answerFrames.get(0).confidence;
		int lowestIndex = 1;
		for(AnswerFrame f : answerFrames){
			if(f.confidence < lowest){
				lowest = f.confidence;
				lowestIndex = f.index;
			}
		}
		System.out.println("Guessing " + lowestIndex);
		return lowestIndex;
//		return getBestConfidenceIndex(answerFrames);
		
	}
    

	private double[] getSameHFractalCheck(ImageFrame[] givenFrames, ArrayList<AnswerFrame> answerFrames) {
		double[] retVal = new double[answerFrames.size()];
		Arrays.fill(retVal, -1);
		boolean horizontalSame1 = false;
		boolean definitePattern = false;
		
		for(int label1 : givenFrames[0].lc.labelColor.keySet()){
			if(givenFrames[0].lc.labelColor.get(label1).equals("b")){
				for(int label2 : givenFrames[1].lc.labelColor.keySet()){
					if(givenFrames[1].lc.labelColor.get(label2).equals("b")){
						if(comparabilityByLabel(givenFrames[0].lc, givenFrames[1].lc, label1, label2) < this.comparabilityThreshhold){ //found a shape in 1 that is the same as a shape in 2
							for(int label3 : givenFrames[2].lc.labelColor.keySet()){
								if(givenFrames[2].lc.labelColor.get(label3).equals("b")){
									if(comparabilityByLabel(givenFrames[0].lc, givenFrames[2].lc, label1, label3) < this.comparabilityThreshhold){
										horizontalSame1 = true; //found a horizontal check on the first row
										//TODO check to see if there are multiple similar shapes
										System.out.println("First row has a fractal same shape");
										break;
									}
								}
							}
							if(horizontalSame1){
								break;
							}
						}
					}
				}
				if(horizontalSame1){
					break;
				}
			}
		}
		boolean horizontalSame2 = false;
		if(horizontalSame1){
			for(int label1 : givenFrames[3].lc.labelColor.keySet()){
				if(givenFrames[3].lc.labelColor.get(label1).equals("b")){
					for(int label2 : givenFrames[4].lc.labelColor.keySet()){
						if(givenFrames[4].lc.labelColor.get(label2).equals("b")){
							if(comparabilityByLabel(givenFrames[3].lc, givenFrames[4].lc, label1, label2) < this.comparabilityThreshhold){ //found a shape in 1 that is the same as a shape in 2
								for(int label3 : givenFrames[5].lc.labelColor.keySet()){
									if(givenFrames[5].lc.labelColor.get(label3).equals("b")){
										if(comparabilityByLabel(givenFrames[3].lc, givenFrames[5].lc, label1, label3) < this.comparabilityThreshhold){
											horizontalSame2 = true; //found a horizontal check on the first row
											//TODO check to see if there are multiple similar shapes
											System.out.println("Second row has a fractal same shape");
											break;
										}
									}
								}
								if(horizontalSame2){
									break;
								}
							}
						}
					}
					if(horizontalSame2){
						break;
					}
				}
			}
		}
		
		if(horizontalSame2){
			for(int label1 : givenFrames[6].lc.labelColor.keySet()){
				if(givenFrames[6].lc.labelColor.get(label1).equals("b")){
					for(int label2 : givenFrames[7].lc.labelColor.keySet()){
						if(givenFrames[7].lc.labelColor.get(label2).equals("b")){
							if(comparabilityByLabel(givenFrames[6].lc, givenFrames[7].lc, label1, label2) < this.comparabilityThreshhold){ //found a shape in 1 that is the same as a shape in 2
								if(!definitePattern){
									Arrays.fill(retVal, 1.0);
									definitePattern = true;
								}
								for(int x = 0; x<answerFrames.size();x++){
									for(int label3 : answerFrames.get(x).imageFrame.lc.labelColor.keySet()){
										if(answerFrames.get(x).imageFrame.lc.labelColor.get(label3).equals("b")){
											double comp = comparabilityByLabel(givenFrames[6].lc, answerFrames.get(x).imageFrame.lc, label1, label3);
											
											if(comp < retVal[x]){
												retVal[x] = comp;
												
											}
											
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		
		
		
		return retVal;
		
	}
	
	private double[] getSameVFractalCheck(ImageFrame[] givenFrames, ArrayList<AnswerFrame> answerFrames){
		
		double[] retVal = new double[answerFrames.size()];
		Arrays.fill(retVal, -1);
		boolean definitePattern = false;
		
		boolean verticalSame1 = false;
		
		for(int label1 : givenFrames[0].lc.labelColor.keySet()){
			if(givenFrames[0].lc.labelColor.get(label1).equals("b")){
				for(int label2 : givenFrames[3].lc.labelColor.keySet()){
					if(givenFrames[3].lc.labelColor.get(label2).equals("b")){
						if(comparabilityByLabel(givenFrames[0].lc, givenFrames[3].lc, label1, label2) < this.comparabilityThreshhold){ //found a shape in 1 that is the same as a shape in 2
							for(int label3 : givenFrames[6].lc.labelColor.keySet()){
								if(givenFrames[6].lc.labelColor.get(label3).equals("b")){
									if(comparabilityByLabel(givenFrames[0].lc, givenFrames[6].lc, label1, label3) < this.comparabilityThreshhold){
										verticalSame1 = true; //found a vertical check on the first row
										//TODO check to see if there are multiple similar shapes
										break;
									}
								}
							}
							if(verticalSame1){
								break;
							}
						}
					}
				}
				if(verticalSame1){
					break;
				}
			}
		}
		boolean verticalSame2 = false;
		if(verticalSame1){
			for(int label1 : givenFrames[1].lc.labelColor.keySet()){
				if(givenFrames[1].lc.labelColor.get(label1).equals("b")){
					for(int label2 : givenFrames[4].lc.labelColor.keySet()){
						if(givenFrames[4].lc.labelColor.get(label2).equals("b")){
							if(comparabilityByLabel(givenFrames[1].lc, givenFrames[4].lc, label1, label2) < this.comparabilityThreshhold){ //found a shape in 1 that is the same as a shape in 2
								for(int label3 : givenFrames[7].lc.labelColor.keySet()){
									if(givenFrames[7].lc.labelColor.get(label3).equals("b")){
										if(comparabilityByLabel(givenFrames[1].lc, givenFrames[7].lc, label1, label3) < this.comparabilityThreshhold){
											verticalSame2 = true; //found a horizontal check on the first row
											//TODO check to see if there are multiple similar shapes
											break;
										}
									}
								}
								if(verticalSame2){
									break;
								}
							}
						}
					}
					if(verticalSame2){
						break;
					}
				}
			}
		}
		
		if(verticalSame2){
			for(int label1 : givenFrames[2].lc.labelColor.keySet()){
				if(givenFrames[2].lc.labelColor.get(label1).equals("b")){
					for(int label2 : givenFrames[5].lc.labelColor.keySet()){
						if(givenFrames[5].lc.labelColor.get(label2).equals("b")){
							if(comparabilityByLabel(givenFrames[2].lc, givenFrames[5].lc, label1, label2) < this.comparabilityThreshhold){ //found a shape in 1 that is the same as a shape in 2
								if(!definitePattern){
									Arrays.fill(retVal, 1.0);
									definitePattern = true;
								}
								for(int x = 0; x<answerFrames.size();x++){
									for(int label3 : answerFrames.get(x).imageFrame.lc.labelColor.keySet()){
										if(answerFrames.get(x).imageFrame.lc.labelColor.get(label3).equals("b")){
											double comp = comparabilityByLabel(givenFrames[2].lc, answerFrames.get(x).imageFrame.lc, label1, label3);
						
											if(comp < retVal[x]){
												retVal[x] = comp;
											}
											
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return retVal;
	}
	
	private double[] getSameHFractalCheck2(ImageFrame[] givenFrames, ArrayList<AnswerFrame> answerFrames){ //A:G
		
		double[] retVal = new double[answerFrames.size()];
		Arrays.fill(retVal, -1);
		boolean definitePattern = false;
		
		boolean horizontalSame1 = false;
		
		for(int label1 : givenFrames[0].lc.labelColor.keySet()){
			if(givenFrames[0].lc.labelColor.get(label1).equals("b")){
				for(int label2 : givenFrames[2].lc.labelColor.keySet()){
					if(givenFrames[2].lc.labelColor.get(label2).equals("b")){
						if(comparabilityByLabel(givenFrames[0].lc, givenFrames[2].lc, label1, label2) < this.comparabilityThreshhold){ //found a shape in 1 that is the same as a shape in 2
							horizontalSame1 = true; //found a horizontal check on the first row
							//TODO check to see if there are multiple similar shapes
							break;
						}
					}
				}
				if(horizontalSame1){
					break;
				}
			}
		}
		boolean horizontalSame2 = false;
		if(horizontalSame1){
			for(int label1 : givenFrames[3].lc.labelColor.keySet()){
				if(givenFrames[3].lc.labelColor.get(label1).equals("b")){
					for(int label2 : givenFrames[5].lc.labelColor.keySet()){
						if(givenFrames[5].lc.labelColor.get(label2).equals("b")){
							if(comparabilityByLabel(givenFrames[3].lc, givenFrames[5].lc, label1, label2) < this.comparabilityThreshhold){ //found a shape in 1 that is the same as a shape in 2
								horizontalSame2 = true; //found a horizontal check on the second col
								//TODO check to see if there are multiple similar shapes
								break;
							}
						}
					}
					if(horizontalSame2){
						break;
					}
				}
			}
		}
		
		if(horizontalSame2){
			for(int label1 : givenFrames[6].lc.labelColor.keySet()){
				if(givenFrames[6].lc.labelColor.get(label1).equals("b")){
					if(!definitePattern){
						Arrays.fill(retVal, 1.0);
						definitePattern = true;
					}
					for(int x = 0; x<answerFrames.size();x++){
						for(int label2 : answerFrames.get(x).imageFrame.lc.labelColor.keySet()){
							if(answerFrames.get(x).imageFrame.lc.labelColor.get(label2).equals("b")){
								double comp = comparabilityByLabel(givenFrames[6].lc, answerFrames.get(x).imageFrame.lc, label1, label2);
			
								if(comp < retVal[x]){
									retVal[x] = comp;
								}
								
							}
						}
					}	
				}
			}
		}
		
		return retVal;
	}
	
	private double[] getSameVFractalCheck2(ImageFrame[] givenFrames, ArrayList<AnswerFrame> answerFrames){ //A:G
		
		double[] retVal = new double[answerFrames.size()];
		Arrays.fill(retVal, -1);
		boolean definitePattern = false;
		
		boolean verticalSame1 = false;
		
		for(int label1 : givenFrames[0].lc.labelColor.keySet()){
			if(givenFrames[0].lc.labelColor.get(label1).equals("b")){
				for(int label2 : givenFrames[6].lc.labelColor.keySet()){
					if(givenFrames[6].lc.labelColor.get(label2).equals("b")){
						if(comparabilityByLabel(givenFrames[0].lc, givenFrames[6].lc, label1, label2) < this.comparabilityThreshhold){ //found a shape in 1 that is the same as a shape in 2
							verticalSame1 = true; //found a vertical check on the first row
							//TODO check to see if there are multiple similar shapes
							break;
						}
					}
				}
				if(verticalSame1){
					break;
				}
			}
		}
		boolean verticalSame2 = false;
		if(verticalSame1){
			for(int label1 : givenFrames[1].lc.labelColor.keySet()){
				if(givenFrames[1].lc.labelColor.get(label1).equals("b")){
					for(int label2 : givenFrames[7].lc.labelColor.keySet()){
						if(givenFrames[7].lc.labelColor.get(label2).equals("b")){
							if(comparabilityByLabel(givenFrames[1].lc, givenFrames[7].lc, label1, label2) < this.comparabilityThreshhold){ //found a shape in 1 that is the same as a shape in 2
								verticalSame2 = true; //found a horizontal check on the second col
								//TODO check to see if there are multiple similar shapes
								break;
							}
						}
					}
					if(verticalSame2){
						break;
					}
				}
			}
		}
		
		if(verticalSame2){
			for(int label1 : givenFrames[2].lc.labelColor.keySet()){
				if(givenFrames[2].lc.labelColor.get(label1).equals("b")){
					if(!definitePattern){
						Arrays.fill(retVal, 1.0);
						definitePattern = true;
					}
					for(int x = 0; x<answerFrames.size();x++){
						for(int label2 : answerFrames.get(x).imageFrame.lc.labelColor.keySet()){
							if(answerFrames.get(x).imageFrame.lc.labelColor.get(label2).equals("b")){
								double comp = comparabilityByLabel(givenFrames[2].lc, answerFrames.get(x).imageFrame.lc, label1, label2);
			
								if(comp < retVal[x]){
									retVal[x] = comp;
								}
								
							}
						}
					}	
				}
			}
		}
		
		return retVal;
	}
	
	private double[] centerImageCountCheck(ImageFrame[] givenFrames, ArrayList<AnswerFrame> answerFrames) {
		
		double[] retVal = new double[answerFrames.size()];
		Arrays.fill(retVal, -1);
		LabelClass rogueShape = new LabelClass();
		int rogueLabel = -1;
		int rogueCount = 0;
		boolean sameInside = false;
		
		for(ImageFrame g : givenFrames){
			if(g.getCenterImageLabel()!=-1){
				int count = 0;
				int centerLabel1 = g.getCenterImageLabel();
				for(ImageFrame other : givenFrames){
					if(!other.name.equals(g.name)){
						if(other.getCenterImageLabel()!=-1){
							int centerLabel2 = other.getCenterImageLabel();
							
							if(comparabilityByLabel(g.lc, other.lc, centerLabel1, centerLabel2) < this.comparabilityThreshhold/2){
								//System.out.println("comparable! " + comparability3(center1, center2));
								count++;
							}	
						}
					}
				}
				
				if(count == 1){
					rogueShape = g.lc;
					rogueLabel = centerLabel1;
					rogueCount++;
				}
				if(count == 7){
					sameInside = true;
					rogueShape = g.lc;
					rogueLabel = centerLabel1;
					break;
				}
//				System.out.println("same as this image: " + count);
			}
			
		}
//		System.out.println("rogueCount " + rogueCount);
		if(rogueCount==2 || sameInside){ //rogue condition exists.
			System.out.println("found rogue!: " + rogueCount + " " + sameInside);
			int count = 0;
			for(AnswerFrame ansF : answerFrames){
				if(ansF.imageFrame.getCenterImageLabel()!=-1){
					int ansCenterLabel = ansF.imageFrame.getCenterImageLabel();
					double comp = comparabilityByLabel(rogueShape, ansF.imageFrame.lc, rogueLabel, ansCenterLabel);
					retVal[count] = comp < this.comparabilityThreshhold/2? comp : 1.0;
				}else{
					retVal[count] = 1.0;
				}
				
				
				count++;
			}
			return retVal;
		}

		return retVal;
	}
	
	
	private double comparabilityByLabel(LabelClass lc1, LabelClass lc2, int centerLabel1, int centerLabel2) {
		
		double incorrectCount = 0;
		double pixels1 = 0;
		double pixels2 = 0;
		double xMass1 = 0;
		double xMass2 = 0;
		double yMass1 = 0;
		double yMass2 = 0;
//		System.out.println("labels: " + centerLabel1 + " " + centerLabel2);
		
		if(centerLabel1 != -1 && centerLabel2 != -1){		
			for(int row = 0; row < lc1.labels.length; row++){
				for(int col = 0; col < lc1.labels[0].length; col++){
					double one = lc1.labels[row][col] == centerLabel1 ? 1.0 : 0.0;
					double two = lc2.labels[row][col] == centerLabel2 ? 1.0 : 0.0;
//					System.out.print(one);
					if(one + two == 1.0){
						incorrectCount++;
					}
					
					if(one == 1.0){
						xMass1+=col;
						yMass1+=row;
						pixels1++;
					}
					
					if(two == 1.0){
						xMass2+=col;
						yMass2+=row;
						pixels2++;
					}
					
				}
//				System.out.println();
			}
		}else{
			for(int row = 0; row < lc1.labels.length; row++){
				for(int col = 0; col < lc1.labels[0].length; col++){
					double one = lc1.labelColor.get(lc1.labels[row][col]).equals("b") ? 1.0 : 0.0;
					double two = lc2.labelColor.get(lc2.labels[row][col]).equals("b") ? 1.0 : 0.0;
					
					if(one + two == 1.0){
						incorrectCount++;
					}
					
					if(one == 1.0){
						xMass1+=col;
						yMass1+=row;
						pixels1++;
					}
					
					if(two == 1.0){
						xMass2+=col;
						yMass2+=row;
						pixels2++;
					}
					
				}
			}
		}
		
		double incorrectPercentage = incorrectCount / (lc1.labels.length * lc1.labels[0].length); //pixels which are different in the images relative to location
    	double percentageDifPix;
    	if(pixels1 + pixels2 > 0){
    		percentageDifPix = Math.abs((pixels1 - pixels2) / ((pixels1 + pixels2)/2)); //total number of pixels in each image, regardless of location
    	}else
    		percentageDifPix = 0.0;
		
		Coordinate cm1 = new Coordinate(xMass1/pixels1, yMass1/pixels1);
		Coordinate cm2 = new Coordinate(xMass2/pixels2, yMass2/pixels2);
		
		double distCenterOfMass = Math.sqrt(Math.pow(cm1.xVal - cm2.xVal,2) + Math.pow(cm1.yVal - cm2.yVal,2))/((lc1.labels[0].length+lc1.labels.length)/2); 
//		System.out.println("first: "  + cm1.xVal + " " + cm1.yVal + "     two: " + cm2.xVal + " " + cm2.yVal);
//		System.out.println("inc: " + incorrectPercentage + " pix: " + percentageDifPix + " Mass: " + distCenterOfMass);
		return Math.sqrt((Math.pow(incorrectPercentage, 2) + Math.pow(percentageDifPix, 2) + Math.pow(distCenterOfMass, 2))/3);
	}
	
	private double[] checkUnary(BufferedImage[] given, ArrayList<BufferedImage> answers) {
		
    	double[] retVal = new double[answers.size()];
//    	Arrays.fill(retVal, 0);
    	
    	ArrayList<String> H1 = new ArrayList<String>(); //A:B::B:C
    	ArrayList<String> H2 = new ArrayList<String>(); //A:C::D:F
    	ArrayList<String> V1 = new ArrayList<String>(); //A:D::D:G
    	ArrayList<String> V2 = new ArrayList<String>(); //A:G::B:H
    	ArrayList<String> possibleTransforms = new ArrayList<String>();
    	possibleTransforms.add("Match");
    	possibleTransforms.add("Horizontal");
    	possibleTransforms.add("Vertical");
    	possibleTransforms.add("45");
    	possibleTransforms.add("90");
    	possibleTransforms.add("135");
    	possibleTransforms.add("180");
    	possibleTransforms.add("225");
    	possibleTransforms.add("270");
    	possibleTransforms.add("315");
    	double unaryThreshhold = 0.01;
    	for(String s : possibleTransforms){
    		if(comparability3(simpleTransform(given[0], s), given[1]) < unaryThreshhold &&
	    		comparability3(simpleTransform(given[1], s), given[2]) < unaryThreshhold &&
	    		comparability3(simpleTransform(given[3], s), given[4]) < unaryThreshhold &&
	    		comparability3(simpleTransform(given[4], s), given[5]) < unaryThreshhold &&
	    		comparability3(simpleTransform(given[6], s), given[7]) < unaryThreshhold ){
	    		H1.add(s);
    		}
    		
    		if(comparability3(simpleTransform(given[0],s),given[2]) < unaryThreshhold &&
    			comparability3(simpleTransform(given[3],s),given[5]) < unaryThreshhold){
    			H2.add(s);
    		}
    	
    		if(comparability3(simpleTransform(given[0], s), given[3]) < unaryThreshhold &&
	    		comparability3(simpleTransform(given[1], s), given[4]) < unaryThreshhold &&
	    		comparability3(simpleTransform(given[2], s), given[5]) < unaryThreshhold &&
	    		comparability3(simpleTransform(given[3], s), given[6]) < unaryThreshhold &&
	    		comparability3(simpleTransform(given[4], s), given[7]) < unaryThreshhold ){
	    		V1.add(s);
    		}
    	
    		if(comparability3(simpleTransform(given[0],s),given[6]) < unaryThreshhold &&
    			comparability3(simpleTransform(given[1],s),given[7]) < unaryThreshhold){
    			V2.add(s);
    		}
    	
    	}
//    	System.out.println("Unary H1: " + H1.toString());
//    	System.out.println("Unary H2: " + H2.toString());
//    	System.out.println("Unary V1: " + V1.toString());
//    	System.out.println("Unary V2: " + V2.toString());
    	double numTransforms = H1.size() + H2.size() + V1.size() + V2.size();
    	
    	if(numTransforms==0){
    		Arrays.fill(retVal, -1);
    		//System.out.println("No transforms found: " + Arrays.toString(retVal));
    		return retVal;
    	}
    	double temp;
    	
    	
		for(int x=0;x<retVal.length;x++){
			
			for(String s : H1){
    			temp = comparability3(simpleTransform(given[7],s),answers.get(x));
    			retVal[x] += Math.pow(temp, 2);
    		}
			
			for(String s : H2){
    			temp = comparability3(simpleTransform(given[6],s),answers.get(x));
    			retVal[x] += Math.pow(temp, 2);
    		}
			
			for(String s : V1){
    			temp = comparability3(simpleTransform(given[5],s),answers.get(x));
    			retVal[x] += Math.pow(temp, 2);
    		}
			
			for(String s : V2){
    			temp = comparability3(simpleTransform(given[2],s),answers.get(x));
    			retVal[x] += Math.pow(temp, 2);
    		}
			
    	}

    	for(int x=0; x<retVal.length;x++){
    		retVal[x] = Math.sqrt(retVal[x]/numTransforms);
    		if(retVal[x] == 0.0){
    			System.out.println("found exact unary match:");
    			System.out.println("Unary H1: " + H1.toString());
    	    	System.out.println("Unary H2: " + H2.toString());
    	    	System.out.println("Unary V1: " + V1.toString());
    	    	System.out.println("Unary V2: " + V2.toString());
    			
    		}
    	}
    	
    	System.out.println("unary conf: " + Arrays.toString(retVal));
		return retVal;
	}
    
    private double[] checkVBinary(BufferedImage[] given, ArrayList<BufferedImage> answers) {
    	
    	double[] retVal = new double[answers.size()];
    	
    	ArrayList<String> V = new ArrayList<String>(); //A+D:G::B+E:H
    	ArrayList<String> possibleTransforms = new ArrayList<String>();
    	possibleTransforms.add("union");
    	possibleTransforms.add("intersection");
    	possibleTransforms.add("subtractionL");
    	possibleTransforms.add("subtractionR");
    	possibleTransforms.add("xor");

    	for(int col = 0; col<2; col++){
//    			System.out.print("Col: " + col);
    			ArrayList<String> temp = findBinaryTransforms(given[col], given[col + 3], given[col + 6]);
//    			System.out.println("V: " + temp.toString());
    			ArrayList<String> toRemove = new ArrayList<String>();
				for(String s : possibleTransforms){
					if(!temp.contains(s)){
						toRemove.add(s);
					}
				}
				for(String s : toRemove){
					possibleTransforms.remove(possibleTransforms.indexOf(s));
				}
			
		}
    	
    	
    	V = possibleTransforms;
    	
    	
    	
//    	System.out.println("V: " + V.toString());
    	
    	
    	if(V.size() == 0){
    		Arrays.fill(retVal, -1);
    		//System.out.println("No transforms found: " + Arrays.toString(retVal));
    		return retVal;
    	}else{
    		Arrays.fill(retVal, 1);
    	}
    	
    	for(int ans = 0; ans<answers.size(); ans++){
    		ArrayList<String> VAnsTrans = findBinaryTransforms(given[2], given[5], answers.get(ans));
//    		System.out.println("answer " + ans + ": " + VAnsTrans.toString());
    		ArrayList<String> validVTrans = new ArrayList<String>();
    		
    		for(String s : VAnsTrans){
    			if(V.contains(s)){
    				validVTrans.add(s);
    			}
    		}
    		
    		double lowest = 1.0;

	    	for(String s2 : validVTrans){
	    		double percentageV = getSpecificBinaryConfidence(given[2],given[5],answers.get(ans),s2);
	    		if(percentageV<lowest){
	    			lowest = percentageV;
	    		}
	    	}
    		
    		retVal[ans] = lowest;
    		//System.out.println(lowest);
    		
    	}
    	
    	return retVal;
    }
    private double[] checkHBinary(BufferedImage[] given, ArrayList<BufferedImage> answers) {
    	
    	double[] retVal = new double[answers.size()];

    	ArrayList<String> H = new ArrayList<String>(); //A+B:C::D+E:F
    	ArrayList<String> possibleTransforms = new ArrayList<String>();
    	possibleTransforms.add("union");
    	possibleTransforms.add("intersection");
    	possibleTransforms.add("subtractionL");
    	possibleTransforms.add("subtractionR");
    	possibleTransforms.add("xor");

    	for(int row = 0; row<2; row++){
//				System.out.print("Row: " + row);
				ArrayList<String> temp = findBinaryTransforms(given[row*3], given[row*3 + 1], given[row*3 + 2]);
				
//				System.out.println("H: " + temp.toString());
				//displayImages(new BufferedImage[] {given[row*3], given[row*3 + 1], given[row*3 + 2]});
				ArrayList<String> toRemove = new ArrayList<String>();
				for(String s : possibleTransforms){
					if(!temp.contains(s)){
						toRemove.add(s);
					}
				}
				for(String s : toRemove){
					possibleTransforms.remove(possibleTransforms.indexOf(s));
				}
			
    	}
    	
    	H = possibleTransforms;

//    	System.out.println("H: " + H.toString());
    	
    	
    	if(H.size() == 0){
    		Arrays.fill(retVal, -1);
    		//System.out.println("No transforms found: " + Arrays.toString(retVal));
    		return retVal;
    	}else{
    		Arrays.fill(retVal, 1);
    	}
    	
    	for(int ans = 0; ans<answers.size(); ans++){
    		ArrayList<String> HAnsTrans = findBinaryTransforms(given[6], given[7], answers.get(ans));
//    		System.out.println("answer " + ans + ": " + HAnsTrans.toString());
    		ArrayList<String> validHTrans = new ArrayList<String>();

    		
    		for(String s : HAnsTrans){
    			if(H.contains(s)){
    				validHTrans.add(s);
    			}
    		}
    		
    		double lowest = 1.0;
    		
    		for(String s : validHTrans){
    			
	    			double percentageH = getSpecificBinaryConfidence(given[6],given[7],answers.get(ans),s);
	    			
	    			if(percentageH<lowest){
	    				lowest = percentageH;
	    			}
    			
    		}
	    	
    		
    		retVal[ans] = lowest;
    		//System.out.println(lowest);
    		
    	}
    	
    	return retVal;
    }
    
	
    private double getSpecificBinaryConfidence(BufferedImage image1, BufferedImage image2, BufferedImage image3, String trans){
    	
		image2 = shiftImage(image2, image1);
		image3 = shiftImage(image3, image1);
		//displayImages(new BufferedImage[] {image1, image2, image3});
		BufferedImage checkImage = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		for(int row = 0; row < image1.getHeight(); row++){

			for(int col = 0; col < image1.getWidth(); col++){
				int a  = image1.getRGB(col, row);
				int b  = image2.getRGB(col, row);
				int c  = image3.getRGB(col, row);
				
				a = (a + 1) / -16777215; // convert to white = 0, black = 1
				b = (b + 1) / -16777215;
				c = (c + 1) / -16777215;
				
				if(trans.equals("union") && (a|b)==1){   //union "or"
					checkImage.setRGB(col, row, -16777216);
				}else
				if(trans.equals("intersection") && (a&b)==1){   //intersection "and"
					checkImage.setRGB(col, row, -16777216);;
				}else
				if(trans.equals("subtractionL") && (a-b)==1){   //subtractionL "a-b" left has priority
					checkImage.setRGB(col, row, -16777216);
				}else
				if(trans.equals("subtractionR") && (b-a)==1){  //subtractionR "b-a" right has priority
					checkImage.setRGB(col, row, -16777216);
				}else
				if(trans.equals("xor") && (a^b)==1){  //xor 
					checkImage.setRGB(col, row, -16777216);
				}else{
					checkImage.setRGB(col, row, -1);
				}
				
			}
		}				
		checkImage = deleteResidue(checkImage);
//		displayImages(new BufferedImage[] {image1, image2, image3, checkImage});
		return comparability3(checkImage, image3);
    	
    	
    	
    }

    private int getBestConfidenceIndex(ArrayList<AnswerFrame> frames) {
    	double best = 1.0;
    	double closestDistance = 1.0;
    	int bestIndex = frames.get(0).index;
    	double[] confidences = new double[frames.size()];
    	for(int x = 0; x<frames.size(); x++){
    		confidences[x] = frames.get(x).confidence;
    		if(frames.get(x).confidence < best){
    			closestDistance = best - frames.get(x).confidence;
    			best = frames.get(x).confidence;
    			bestIndex = frames.get(x).index;
    		}else if(Math.abs(frames.get(x).confidence - best) < closestDistance){
    			closestDistance = Math.abs(frames.get(x).confidence - best);
    		}
    	}
//    	System.out.println("Best confidence: " + best + " " + bestIndex);
    	System.out.println("Overall conf: " + Arrays.toString(confidences)) ;
    	if(best>0.15){
//    		System.out.println("Not guessing because unsurity is too high.");
    		return -1;
    	}else if(closestDistance < best  || closestDistance < 0.001){
//    		System.out.println("Not guessing because next best guess is too close. " + closestDistance);
    		return -1;
//    		System.out.println("next best guess is close!");
    	}
		System.out.println("Found answer from Confidence: " + Arrays.toString(confidences));
		return bestIndex;
	}

    private double[] checkPixels(BufferedImage[] given, ArrayList<BufferedImage> answers) {
		double[] retVal = new double[answers.size()];
		double[] massMetric = new double[retVal.length];
    	double[] pixelCount = new double[given.length];
    	Arrays.fill(retVal, -1);
    	double[] pixelCountAns = new double[answers.size()];

    	for(int x = 0; x<given.length;x++){
    		for(int row = 0; row < given[0].getHeight(); row++){
    			for(int col = 0; col < given[0].getWidth(); col++){
    				pixelCount[x] += (given[x].getRGB(col, row) == -16777216) ? 1 : 0;
    			}
    		}
    	}
    	
    	for(int x = 0; x<answers.size();x++){
    		retVal[x] = 1.0;
    		massMetric[x] = 1.0;
    		for(int row = 0; row < given[0].getHeight(); row++){
    			for(int col = 0; col < given[0].getWidth(); col++){
    				pixelCountAns[x] += (answers.get(x).getRGB(col, row) == -16777216) ? 1 : 0;

    			}
    		}

    	}

    	if( ((pixelCount[1] - pixelCount[0]) + pixelCount[1] - pixelCount[2]) /(pixelCount[0] + pixelCount[1] + pixelCount[2]) < comparabilityThreshhold &&
    		((pixelCount[4] - pixelCount[3]) + pixelCount[4] - pixelCount[5]) /(pixelCount[3] + pixelCount[4] + pixelCount[3]) < comparabilityThreshhold	){
//    		System.out.println("horizontal pixel addition/subtraction");
    		for(int x = 0; x< answers.size(); x++){
    			
    			retVal[x]= Math.abs((pixelCount[7] - pixelCount[6]) + pixelCount[7] - pixelCountAns[x]) /(pixelCount[6] + pixelCount[7] + pixelCountAns[x]);
    		}
    	}

    	if( ((pixelCount[3] - pixelCount[0]) + pixelCount[3] - pixelCount[6]) /(pixelCount[0] + pixelCount[3] + pixelCount[6]) < comparabilityThreshhold &&
        		((pixelCount[4] - pixelCount[1]) + pixelCount[4] - pixelCount[7]) /(pixelCount[1] + pixelCount[4] + pixelCount[7]) < comparabilityThreshhold	){
//        		System.out.println("vertical pixel addition/subtraction");
    		
        		for(int x = 0; x< answers.size(); x++){
        			
        			double newVal = Math.abs((pixelCount[5] - pixelCount[2]) + pixelCount[5] - pixelCountAns[x]) /(pixelCount[2] + pixelCount[5] + pixelCountAns[x]);
        			
        			 if(newVal<retVal[x] || retVal[x] == -1){
        				 retVal[x] = newVal;
        			 }
        			
        		}	
        }

		return retVal;
	}

	// can return union, intersection, subtractionL, subtractionR, xor
	private ArrayList<String> findBinaryTransforms(BufferedImage image1, BufferedImage image2, BufferedImage image3) {
		ArrayList<String> retVal = new ArrayList<String>();
		retVal.add("union");
		retVal.add("intersection");
		retVal.add("subtractionL");
		retVal.add("subtractionR");
		retVal.add("xor");
		
		image1 = shiftImage(image1,image2);
		image3 = shiftImage(image3,image2);


		BufferedImage[] binaryImages = new BufferedImage[5];
		for(int x = 0; x< 5; x++){
			binaryImages[x] = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
		}
		
		for(int row = 0; row < image1.getHeight(); row++){

			for(int col = 0; col < image1.getWidth(); col++){
				int a  = image1.getRGB(col, row);
				int b  = image2.getRGB(col, row);
				int c  = image3.getRGB(col, row);
				
				a = (a + 1) / -16777215; // convert to white = 0, black = 1
				b = (b + 1) / -16777215;
				c = (c + 1) / -16777215;

				if((a|b)==1){   //union "or"
					binaryImages[0].setRGB(col, row, -16777216);
				}else{
					binaryImages[0].setRGB(col, row, -1);
				}
				if((a&b)==1){   //intersection "and"
					binaryImages[1].setRGB(col, row, -16777216);
				}else{
					binaryImages[1].setRGB(col, row, -1);
				}
				if((a-b)==1){   //subtractionL "a-b" left has priority
					binaryImages[2].setRGB(col, row, -16777216);
				}else{
					binaryImages[2].setRGB(col, row, -1);
				}
				if((b-a)==1){  //subtractionR "b-a" right has priority
					binaryImages[3].setRGB(col, row, -16777216);
				}else{
					binaryImages[3].setRGB(col, row, -1);
				}
				if((a^b)==1){  //xor 
					binaryImages[4].setRGB(col, row, -16777216);
				}else{
					binaryImages[4].setRGB(col, row, -1);
				}
				
			}
		}				
		
		
		for(int x = 4; x>=0; x--){
			binaryImages[x] = deleteResidue(binaryImages[x]);
//			System.out.println(comparability3(binaryImages[x],image3));
			if(comparability3(binaryImages[x],image3) > this.comparabilityThreshhold){ 
				retVal.remove(x);
			}
			binaryImages[x] = null;
		}
		//displayImages(new BufferedImage[] {image1, image2, image3, binaryImages[0]});
//		System.out.println();
		
		return retVal;
	}
	
	private boolean diagonalSamenessCheck(BufferedImage image1, BufferedImage image2) {
		double comp = comparability3(image1,image2);
		//System.out.println("comparibility of diagonals: " + comp);
		return comp < comparabilityThreshhold;

	}
	
	private int[] getShapeCount(BufferedImage[] images) {
    	int[] shapeCount = new int[images.length];
    	
    	for(int x = 0; x < images.length; x++){
    		
    		shapeCount[x] = getBlobLabels(images[x]).getNumShapes();
    		
    	}

		return shapeCount;
	}
    
    private double[] getShapeConfidence(int[] shapeCountProblem, int[] shapeCountAnswers) {
		
    	
    	ArrayList<String> patterns = new ArrayList<String>();
    	ArrayList<Integer> patternAnswerShapeCounts = new ArrayList<Integer>();
    	
    	if(shapeCountProblem[0] == shapeCountProblem[1] && shapeCountProblem[1] == shapeCountProblem[2] && shapeCountProblem[3] == shapeCountProblem[4] && shapeCountProblem[4] == shapeCountProblem[5]){
    		patterns.add("sameH");
    		patternAnswerShapeCounts.add(shapeCountProblem[7]);
    	}
    	
    	if(shapeCountProblem[0] == shapeCountProblem[3] && shapeCountProblem[3] == shapeCountProblem[6] && shapeCountProblem[1] == shapeCountProblem[4] && shapeCountProblem[4] == shapeCountProblem[7]){
    		patterns.add("sameV");
    		patternAnswerShapeCounts.add(shapeCountProblem[5]);
    	}
    	
    	
    	
    	if(shapeCountProblem[0] - shapeCountProblem[1] == shapeCountProblem[2] && shapeCountProblem[3] - shapeCountProblem[4] == shapeCountProblem[5]){
    		patterns.add("subH");
    		patternAnswerShapeCounts.add(shapeCountProblem[6] - shapeCountProblem[7]);
    	}
    	if(shapeCountProblem[0] - shapeCountProblem[3] == shapeCountProblem[6] && shapeCountProblem[1] - shapeCountProblem[4] == shapeCountProblem[7]){
    		patterns.add("subV");
    		patternAnswerShapeCounts.add(shapeCountProblem[2] - shapeCountProblem[5]);
    	}
    	if(shapeCountProblem[0] + shapeCountProblem[1] == shapeCountProblem[2] && shapeCountProblem[3] + shapeCountProblem[4] == shapeCountProblem[5]){
    		patterns.add("addH");
    		patternAnswerShapeCounts.add(shapeCountProblem[6] + shapeCountProblem[7]);
    	}
    	if(shapeCountProblem[0] + shapeCountProblem[3] == shapeCountProblem[6] && shapeCountProblem[1] + shapeCountProblem[4] == shapeCountProblem[7]){
    		patterns.add("addV");
    		patternAnswerShapeCounts.add(shapeCountProblem[2] + shapeCountProblem[5]);
    	}
    	
    	
    	
    	
    	if(shapeCountProblem[1] - shapeCountProblem[0] == shapeCountProblem[2] - shapeCountProblem[1] && shapeCountProblem[4] - shapeCountProblem[3] == shapeCountProblem[5] - shapeCountProblem[4]){
    		patterns.add("diffH");
    		patternAnswerShapeCounts.add((shapeCountProblem[7] - shapeCountProblem[6]) + shapeCountProblem[7]);
    	}
    	if(shapeCountProblem[3] - shapeCountProblem[0] == shapeCountProblem[6] - shapeCountProblem[3] && shapeCountProblem[4] - shapeCountProblem[1] == shapeCountProblem[7] - shapeCountProblem[4]){
    		patterns.add("diffV");
    		patternAnswerShapeCounts.add( (shapeCountProblem[5] - shapeCountProblem[2]) + shapeCountProblem[5]);
    	}
    	
    	
    	
    	double[] shapeConfidence = new double[shapeCountAnswers.length];
//		System.out.println(patternAnswerShapeCounts.toString());
//		System.out.println(patterns.toString());
		
		int count = 0;
		//TODO if this finds multiple patterns with different answers, how do I pick the best transform?
		
		int chosenAnswerCount = -1; 
		
		for(Integer s : patternAnswerShapeCounts){
			if(chosenAnswerCount==-1){
				chosenAnswerCount = s;
				if(chosenAnswerCount>100)
					return (new double[] {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0} );
		
			}else if(s!=chosenAnswerCount){
				//System.out.println("Conflict in determining shape pattern!");
				return (new double[] {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0} );
			}
		}
		
		
		
		boolean atLeastOne = false;
    	for(Integer shape : shapeCountAnswers){
    		if(shape == chosenAnswerCount){
    			shapeConfidence[count] = 1.0; 
    			atLeastOne = true;
    		}
    		count++;
    	}
//    	System.out.println(Arrays.toString(shapeConfidence));
		return atLeastOne ? shapeConfidence : new double[] {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0};
	}
    
	private void displayImages(BufferedImage[] images){
    	
    	frame.dispose();
    	frame.getContentPane().removeAll();
    	for(BufferedImage image : images){
    		frame.getContentPane().add(new JLabel(new ImageIcon(image)));
    	}
    	
    	
    	frame.pack();
    	frame.setVisible(true);
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
    }
    
    
    private BufferedImage deleteResidue(BufferedImage image){
    	BufferedImage temp = copyImage(image);
    	int[][] labels = new int[image.getHeight()][image.getWidth()];
    	ArrayList<ArrayList<Integer>> connected = new ArrayList<ArrayList<Integer>>();
    	ArrayList<Integer> roots = new ArrayList<Integer>();
    	
    	ArrayList<Integer> neighborsX = new ArrayList<Integer>();
    	ArrayList<Integer> neighborsY = new ArrayList<Integer>();
    	int currentSet = 1;
    	
    	
    	for(int y = 0; y<image.getHeight(); y++){
    		for(int x = 0; x<image.getWidth();x++){
    			
    			//get neighbors
    			if(image.getRGB(x, y)!=-1){
    				neighborsX = new ArrayList<Integer>();
    				neighborsY = new ArrayList<Integer>();
    				for(int j = y-1; j<y+2; j++){
    					if(j>=0 && j<image.getHeight()){
    						for(int i = x-1; i<x+2; i++){
    							if(i>=0 && i<image.getWidth()){
    								if(image.getRGB(i, j) != -1){
    									neighborsX.add(i);
    									neighborsY.add(j);
    				}}}}}

	    			//check neighbors and assign label
	    			if(neighborsX.isEmpty()){
	    				connected.add(new ArrayList<Integer>());
	    				connected.get(connected.size()-1).add(currentSet);
	    				labels[x][y] = currentSet;
	    				currentSet++;
	    			}else{
	    				int minLabel = connected.size();
	    				ArrayList<Integer> L = new ArrayList<Integer>();
//	    				System.out.println(x + " " + y);
//	    				System.out.println("NeighborsX " + neighborsX.toString() + " NeighborsY " + neighborsY.toString());
	    				for(int count = 0; count<neighborsX.size(); count++){
	    					if(labels[neighborsX.get(count)][neighborsY.get(count)]!=0){ //label is 0 if it hasnt been visited yet
	    						L.add(labels[neighborsX.get(count)][neighborsY.get(count)]);
		    					if(L.get(L.size()-1) < minLabel){
		    						minLabel = L.get(L.size()-1);
		    					}
	    					}
	    				}
	    				//System.out.println("labels: " + L.toString());
	    				if(L.isEmpty()){
	    					connected.add(new ArrayList<Integer>());
	        				connected.get(connected.size()-1).add(currentSet);
	        				labels[x][y] = currentSet;
	        				currentSet++;
	    				}else{
	    					labels[x][y] = minLabel;
	    					for(int lab : L){
		    					for(int lab2 : L){
		    						if(!connected.get(lab-1).contains(lab2)){
		    							connected.get(lab-1).add(lab2);
		    						}
		    						
	    }}}}}}}
    	
    	for(int count = 0; count < connected.size(); count++){
    		int min = connected.size();
    		for(int myInt : connected.get(count)){
    			min = myInt < min ? myInt : min;
    		}
    		roots.add(min);
    	}
//    	System.out.println(roots.toString());
    	for(int x = 0; x < roots.size(); x++){
    		int root = x;
    		
    		while(roots.get(root) != root+1){
    			root = roots.get(root)-1;
    		}
    		
    		while(roots.get(x) != root+1){
    			int parent = roots.get(x);
    			roots.set(x, root+1);
    			x = parent;
    		}
    		
    		
    	}
//    	System.out.println("sdjfkln" + roots.toString());
    	
    	double[] labelCounts = new double[roots.size()];
    	
    	
    	
    	for(int y = 0; y<image.getHeight(); y++){
    		for(int x = 0; x<image.getWidth();x++){
    			
    			if(labels[x][y]!=0){
    				labels[x][y] = roots.get(labels[x][y]-1); // set the label to the lowest label this is connected to
    				labelCounts[roots.get(labels[x][y]-1)-1]++;
    			}
//    			System.out.print(labels[x][y]);
    		}
//    		System.out.println();
    	}
    	ArrayList<Integer> labelsToDelete = new ArrayList<Integer>();
    	for(int x = 0; x<labelCounts.length; x++){
    		if(labelCounts[x] != 0 && labelCounts[x]/(image.getHeight()*image.getWidth()) < 0.005){ //residue threshhold
    			labelsToDelete.add(x+1);
    		}
    	}
//    	System.out.println(image.getHeight() + " " + image.getWidth());
//    	System.out.println(Arrays.toString(labelCounts));
//    	System.out.println(labelsToDelete.toString());
//    	try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			
//			e.printStackTrace();
//		}
    	for(int y = 0; y<image.getHeight(); y++){
    		for(int x = 0; x<image.getWidth();x++){
    			if(labelsToDelete.contains(labels[x][y])){
    				temp.setRGB(x,y,-1);
    			}
    		}
    	}
    	
    	
    	return temp;
    }
    
    
    private LabelClass getBlobLabels(BufferedImage image){
    	image = makeBandW(copyImage(image));
    	LabelClass lc = new LabelClass(image.getWidth(), image.getHeight());
    	lc.generateLabels(image);
    	return lc;
    }
    
    
    private BufferedImage shiftImage(BufferedImage image1, BufferedImage image2) {
		
    	int height = image1.getHeight();
    	int width = image1.getWidth();
    	BufferedImage shifted = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	// Create the graphics
    	Graphics2D g = shifted.createGraphics();
    	// Draw original with shifted coordinates
    	
//    	displayImages(new BufferedImage[] {image1});
    	Coordinate c1 = getCenterOfMass(image1);
    	Coordinate c2 = getCenterOfMass(image2);

    	if(Math.sqrt(Math.pow((c2.xVal-c1.xVal),2) + Math.pow((c2.yVal-c1.yVal), 2)) < 10){
//    	System.out.println((c2.xVal-c1.xVal) + " "  + (c2.yVal-c1.yVal));

    	g.drawImage(image1,(int) (c2.xVal-c1.xVal),(int) (c2.yVal-c1.yVal), null);

    	}
    	g.dispose();
//    	displayImages(new BufferedImage[] {image1});
    	
		return image1;
	}

	
	private boolean isSameImage(BufferedImage image1, BufferedImage image2){
    	
    	
    	return comparability(image1,image2) < comparabilityThreshhold;
    	
    }
	
	
    private double comparability(BufferedImage image1, BufferedImage image2){
    	
//    	displayImages(new BufferedImage[] {image1,image2});
    	image1 = deleteResidue(image1);
    	image2 = deleteResidue(image2);
//    	displayImages(new BufferedImage[] {image1,image2});
    	
    	int[] shapes = getShapeCount(new BufferedImage[] {image1, image2});
    	if(shapes[0] != shapes[1]){
    		return 1.0;
    	}
    	
    	
    	
    	
    	int height = image1.getHeight();
    	int width = image1.getWidth();

    	double incorrectCount = 0;
    	
    	if(height!=image2.getHeight() || width!=image2.getWidth()){
//    		System.out.println("Height mismatch!");
    		return 1.0;
    	}
    	
    	for(int x = 0; x<height;x++){
    		for(int y = 0; y<width; y++){
    			if(image1.getRGB(x, y) != image2.getRGB(x, y)){
    				incorrectCount++;
    			}
    		}
    	}
    	double incorrectPercentage = incorrectCount / (height*width); //pixels which are different in the images relative to location

    	
    	//try shifting
//    	if((incorrectCount / (height*width))>comparabilityThreshhold){
//    		image1 = shiftImage(image1,image2);
//    	}
//    	
//    	for(int x = 0; x<height;x++){
//    		for(int y = 0; y<width; y++){
//    			if(image1.getRGB(x, y) != image2.getRGB(x, y)){
//    				incorrectCount++;
//    			}
//    		}
//    	}

    	return incorrectPercentage;
    	
    }
    
    private double comparability3(BufferedImage image1, BufferedImage image2){
//    	displayImages(new BufferedImage[] {image1,image2});
//    	image1 = deleteResidue(image1);
//    	image2 = deleteResidue(image2);
//    	displayImages(new BufferedImage[] {image1,image2});
    	
//    	int[] shapes = getShapeCount(new BufferedImage[] {image1, image2});
//    	if(shapes[0] != shapes[1]){
//    		System.out.println("Different shapes");
//    		return 1.0;
//    	}
//    	displayImages(new BufferedImage[] {image1,image2});
    	image1 = shiftImage(image1,image2);
//    	displayImages(new BufferedImage[] {image1,image2});
    	
    	int height = image1.getHeight();
    	int width = image1.getWidth();
    	int numPix1 = 0;
    	int numPix2 = 0;
    	double incorrectCount = 0;
    	
    	if(height!=image2.getHeight() || width!=image2.getWidth()){
//    		System.out.println("Height mismatch!");
    		return 1.0;
    	}
    	
    	for(int x = 0; x<height;x++){
    		for(int y = 0; y<width; y++){
    			if(image1.getRGB(x, y) != image2.getRGB(x, y)){
    				incorrectCount++;
    			}
    			if(image1.getRGB(x, y)!=-1){
    				numPix1++;
    			}
    			if(image2.getRGB(x, y)!=-1){
    				numPix2++;
    			}
    		}
    	}
    	
    	int[] shapeCount = getShapeCount(new BufferedImage[] {image1, image2});
    	double shapePercentage = Math.abs(shapeCount[0]-shapeCount[1])/20;
    	
    	double incorrectPercentage = incorrectCount / (height*width); //pixels which are different in the images relative to location
    	double percentageDifPix;
    	if(numPix1 + numPix2 > 0){
    		percentageDifPix = Math.abs((numPix1 - numPix2) / ((numPix1 + numPix2)/2)); //total number of pixels in each image, regardless of location
    	}else
    		percentageDifPix = 0.0;

    	
//    	Coordinate crd1 = getCenterOfMass(image1);
//    	Coordinate crd2 = getCenterOfMass(image2);
//
//    	
//    	double distCenterOfMass = Math.sqrt(Math.pow(crd1.xVal - crd2.xVal,2) + Math.pow(crd1.yVal - crd2.yVal,2))/((image1.getWidth()+image1.getHeight())/2); //the denominator normalizes the distance to be a percentage of the mean of the width and height
    	
    	//System.out.println(Math.sqrt( (Math.pow(incorrectPercentage,2) + Math.pow(percentageDifPix,2))/2 ));
    	return Math.sqrt( (Math.pow(incorrectPercentage,2) + Math.pow(percentageDifPix,2) + Math.pow(shapePercentage, 2))/3 );
    	
    }
    
    private Coordinate getCenterOfMass(BufferedImage image) {
		double xVal = 0;
		double yVal = 0;
		int count = 0;
    	for(int row = 0; row<image.getHeight();row++){
    		for(int col = 0; col<image.getWidth();col++){
    			if(image.getRGB(row, col)!=-1){
    				xVal+=col;
    				yVal+=row;
    				count++;
    			}
    		
    		}
    	}
    	Coordinate crd = new Coordinate(xVal/count, yVal/count);
    	
		return crd;
	}
	
    private ArrayList<String> simpleImageCompare(BufferedImage image1, BufferedImage image2){
    	
    	ArrayList<String> possibleTransforms = new ArrayList<String>();

    	if(comparability(image1, image2)<=comparabilityThreshhold){
    		possibleTransforms.add("Match");
    	}
    	if(comparability(mirrorImage(image1,"Horizontal"),image2)<=comparabilityThreshhold){
    		possibleTransforms.add("Horizontal");
    	}
    	if(comparability(mirrorImage(image1,"Vertical"),image2)<=comparabilityThreshhold){
    		possibleTransforms.add("Vertical");
    	}
    	//displayImages(new BufferedImage[] {makeBandW(rotateImage(image1,45)), image2});
    	if(comparability(rotateImage(image1,45),image2)<=comparabilityThreshhold){
    		possibleTransforms.add("45");
    	}
    	if(comparability(rotateImage(image1,90),image2)<=comparabilityThreshhold){
    		
    		possibleTransforms.add("90");
    	}
    	if(comparability(rotateImage(image1,135),image2)<=comparabilityThreshhold){
    		possibleTransforms.add("135");
    	}
    	if(comparability(rotateImage(image1,180),image2)<=comparabilityThreshhold){
    		possibleTransforms.add("180");
    	}
    	if(comparability(rotateImage(image1,225),image2)<=comparabilityThreshhold){
    		possibleTransforms.add("225");
    	}
    	if(comparability(rotateImage(image1,270),image2)<=comparabilityThreshhold){
    		possibleTransforms.add("270");
    	}
    	if(comparability(rotateImage(image1,315),image2)<=comparabilityThreshhold){
    		possibleTransforms.add("315");
    	}
    	double addOrSub = checkForOnlyAdditionOrSubtraction(image1, image2);
    	
    	//System.out.println("add or sub found " + addOrSub);
    	if(addOrSub > 2*comparabilityThreshhold){
    		possibleTransforms.add("Add");
//    		possibleTransforms.add("Fill");
    	}
    	if(addOrSub < -2*comparabilityThreshhold){
    		possibleTransforms.add("Delete");
    	}
    	
    	
    	
    	return possibleTransforms;
    }
    
    /*
     * 
     * returns int[][] with -1,0,1 in each index for a deleted, unchanged, or added pixel.
     * 
     * 
     */
    private int[][] getAdditionsAndSubtractions(BufferedImage image1, BufferedImage image2){
//    	image1 = copyImage(image1);
//    	image2 = copyImage(image2);
    	int[][] retVal = new int[image1.getHeight()][image1.getWidth()];
    	
    	for(int x = 0; x< image1.getHeight(); x++){
    		for(int y = 0; y<image1.getWidth(); y++){
    			retVal[x][y] = ((image2.getRGB(x, y) & 0x00FFFFFF) - (image1.getRGB(x, y) & 0x00FFFFFF))/16777215;
    			//System.out.print(retVal[x][y]);
    		}
    		//System.out.println("");
    	}
    	return retVal;
    }
    
    /*
     * 
     * checks if image1 only added pixels to get image2, or if image1 only deleted pixels to get image2
     * 
     */
    
    private double checkForOnlyAdditionOrSubtraction(BufferedImage image1, BufferedImage image2){
    	double changeCount = 0;
    	double coloredPixels = 0;
    	for(int x = 0; x<image1.getHeight(); x++){
    		for(int y = 0; y<image1.getWidth(); y++){
    			if(image1.getRGB(x, y)!=-1){
    				coloredPixels++;
    			}
    			if( ((image2.getRGB(x, y) & 0x00FFFFFF) - (image1.getRGB(x, y) & 0x00FFFFFF))/16777215 == -1) {
    				
    				changeCount++;
    			}
    			
    			if( ((image2.getRGB(x, y) & 0x00FFFFFF) - (image1.getRGB(x, y) & 0x00FFFFFF))/16777215 == 1) {
    				changeCount--;
    			}
    		}
    	}
    	//System.out.println(changeCount);
    	return changeCount/coloredPixels;
    }
    
    private BufferedImage simpleAdditionAndSubtraction(BufferedImage image, int[][] changes){
//    	displayImages(new BufferedImage[] {image});
    	BufferedImage retImage = copyImage(image);
//    	displayImages(new BufferedImage[] {retImage});

    	for(int x = 0; x<changes.length; x++){
    		for(int y = 0; y<changes[0].length; y++){
    			if(changes[x][y]!=0){
    				if(changes[x][y]==-1){
    					retImage.setRGB(x, y, -16777216);
    				}else if(changes[x][y] == 1){
    					retImage.setRGB(x, y, -1);
    				}
    			}
    			
    		}
    	}
    	
    	return retImage;
    }
    
    private BufferedImage simpleTransform(BufferedImage image, String transform){
    	switch(transform){
    	case "Match":
    		return image;
    	case "45":
    		return makeBandW(rotateImage(image,45));
    	case "90":
    		return rotateImage(image,90);
    	case "135":
    		return makeBandW(rotateImage(image,135));
    	case "180":
    		return rotateImage(image,180);
    	case "225":
    		return makeBandW(rotateImage(image,225));
    	case "270":
    		return rotateImage(image,270);
    	case "315":
    		return makeBandW(rotateImage(image,315));
//    	case "Add":
//    		return simpleAdditionAndSubtraction(image, additionOrSubtraction);
//    	case "Delete":
//    		return simpleAdditionAndSubtraction(image, additionOrSubtraction);
    	case "Horizontal":
    		return mirrorImage(image, transform);
    	case "Vertical":
    		return mirrorImage(image, transform);
    	default:
    		System.out.println("Transform " + transform + " is not accounted for.");
    	}
    	return image;
    }
    
    /*
     * Returns a copy of a buffered image
     * 
     * code source: url - "https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage" dated may 15 2014
     * 
     * 
     * BEGIN CODE
     */
    
    private BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D g = b.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
    
    /*
     * END CODE FROM url - "https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage" dated may 15 2014
     */
    
    private BufferedImage makeBandW(BufferedImage image){
    	BufferedImage retImage = copyImage(image);
    	int color;
    	for(int x = 0; x<image.getHeight(); x++){
    		for(int y = 0; y<image.getWidth(); y++){
    			color = retImage.getRGB(x, y);
    		
    			if(color != -1 && color != -16777216){
    				color = color < -8388607.5 ? -16777216 : -1;
    				retImage.setRGB(x, y, color);
    			}
    		}
    	}
    	
    	
    	return retImage;
    }
    
    private BufferedImage rotateImage(BufferedImage image, double ang){
    	BufferedImage temp = new BufferedImage(image.getHeight(),image.getWidth(), image.getType());
    	Graphics2D g2d = temp.createGraphics();
    	AffineTransform at = new AffineTransform();
    	at.rotate(Math.toRadians(ang), image.getWidth()/2, image.getHeight()/2);
    	g2d.transform(at);
    	g2d.drawImage(image,null,0,0);
    	g2d.dispose();
    	return temp;
    }
    
    private BufferedImage mirrorImage(BufferedImage image, String direction){
    	
    	BufferedImage temp = new BufferedImage(image.getHeight(),image.getWidth(), image.getType());
    	Graphics2D g2d = temp.createGraphics();
    	AffineTransform at = new AffineTransform();
    	switch(direction){
    		case "Horizontal":
    	        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
    	        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0));
    	        
    			break;
    		case "Vertical":
    			at.concatenate(AffineTransform.getScaleInstance(1, -1));
    	        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
    			break;		
    	}
    	g2d.transform(at);
        g2d.drawImage(image, 0, 0, null);
    	g2d.dispose();
    	return temp;
    }
    
    /*
     * figures - an ArrayList of figures representing a 2x2 Raven's Problem
     * answers - array of possible answers
     * 
     * compares given figures for simple transformations (i.e. rotation, reflection, scaling, translation)
     * 
     * return - confidence in answer
     */
    private SolutionOrTransforms simpleCheck2(ArrayList<RavensFigure> figures, RavensFigure[] answers){
    	
    	//Perform all of below to A, compare to B. If match, add to horizontal transform
    	//Perform all of below to A, compare to C. If match, add to Vertical transform
    	
    	//Rotate (0,45,90,135,180,225,270,315)
    	//mirror (vertical, horizontal)
    	//translate 
    	//Scaling
    	BufferedImage aImage, bImage, cImage;
    	SolutionOrTransforms retVal = new SolutionOrTransforms();
//    	BufferedImage[] myImages = new BufferedImage[9];
    	try {
			aImage = ImageIO.read(new File(figures.get(0).getVisual()));
			bImage = ImageIO.read(new File(figures.get(1).getVisual()));
			cImage = ImageIO.read(new File(figures.get(2).getVisual()));
			
//			myImages[0] = aImage;
//			myImages[1] = bImage;
//			myImages[2] = cImage;
			
//			for(int x = 0; x<6; x++){
//				myImages[3+x] = ImageIO.read(new File(answers[x].getVisual()));
//			}
		} catch (IOException e) {
			//System.out.println("no image available for some figure in simplecheck2");
			return retVal;
		}
//    	displayImages(myImages);
//    	displayImages(new BufferedImage[] {makeBandW(aImage)});
//    	LabelClass temp = getBlobLabels(aImage);
//    	for(int x = 0; x<aImage.getWidth(); x++){
//    		for(int y = 0; y< aImage.getHeight(); y++){
//    			aImage.setRGB(x,y, (new Color((int)Math.pow(5*temp.labels[x][y],2))).getRGB());
//    		}
//    	}
//    	displayImages(new BufferedImage[] {aImage});
    	//find possible simple transforms.
    	
    	ArrayList<String> horizontalTransform = simpleImageCompare(aImage,bImage);
    	ArrayList<String> verticalTransform = simpleImageCompare(aImage,cImage);
    	
    	
    	retVal.horizontalTransforms = horizontalTransform;
    	retVal.verticalTransforms = verticalTransform;
    	
    	
    	
    	
    	
    	if(horizontalTransform.isEmpty() && verticalTransform.isEmpty()){
    		//System.out.println("Couldn't find a simple transform. ");
    		return retVal;
    	}
    	
    	BufferedImage guess, guess2;
    	
//    	System.out.println(horizontalTransform.toString());
//    	System.out.println(verticalTransform.toString());
    	if(!horizontalTransform.isEmpty() || !verticalTransform.isEmpty()){

			for(String trans : listedTransforms){
//				System.out.println("Trying " + trans);
				
				if(horizontalTransform.contains(trans)){					
					if(trans.equals("Add") || trans.equals("Delete")){
	    				//displayImages(new BufferedImage[] {bImage});
	    				guess = simpleAdditionAndSubtraction(cImage, getAdditionsAndSubtractions(aImage,bImage));
	    				//displayImages(new BufferedImage[] {aImage,bImage,cImage,deleteResidue(guess)});
	    			}else{
	    				guess = simpleTransform(cImage, trans);
	    			}
					
					
					for(String otherTrans : verticalTransform){
						if(otherTrans.equals("Add") || otherTrans.equals("Delete")){
		    				guess2 = simpleAdditionAndSubtraction(bImage, getAdditionsAndSubtractions(aImage,cImage));
		    			}else{
		    				guess2 = simpleTransform(bImage, otherTrans);
			    			if(isSameImage(guess,guess2)){
			    				ConfidencePairing con = tryAnswer(guess,answers);
				    			if(con.answerIndex!=-1 && retVal.answerConfidence[con.answerIndex-1]<con.confidence){
				    				retVal.answerConfidence[con.answerIndex-1] = con.confidence;
				    			}
//			    				if(con.answerIndex!=-1 && con.confidence >= 1-comparabilityThreshhold){
//			    					
//			    					retVal.answer = con.answerIndex;
//			    					System.out.println("Found answer for " + " h " + trans + " v " + otherTrans + " "  + retVal.answer);
//			    					return retVal;
//			    				}
			    			}
		    			}
					}
				}
				
				if(horizontalTransform.contains(trans)){
					//use horizontal transforms to find answer
					horizontalTransform.remove(horizontalTransform.indexOf(trans));

	    			if(trans.equals("Add") || trans.equals("Delete")){
	    				//displayImages(new BufferedImage[] {bImage});
	    				guess = simpleAdditionAndSubtraction(cImage, getAdditionsAndSubtractions(aImage,bImage));
	    				//displayImages(new BufferedImage[] {aImage,bImage,cImage,deleteResidue(guess)});
	    			}else{
	    				guess = simpleTransform(cImage, trans);
	    			}
	    			
	    			ConfidencePairing con = tryAnswer(guess,answers);
	    			if(con.answerIndex!=-1 && retVal.answerConfidence[con.answerIndex-1]<con.confidence){
	    				retVal.answerConfidence[con.answerIndex-1] = con.confidence;
	    			}
//    				if(con.answerIndex!=-1 && con.confidence >= 1-comparabilityThreshhold){
//    					
//    					retVal.answer = con.answerIndex;
//    					System.out.println("Found answer for " + " h " + trans + " "  + retVal.answer);
//    					return retVal;
//    				}
				}
	
		    	
		    	if(verticalTransform.contains(trans)){
		    		verticalTransform.remove(verticalTransform.indexOf(trans));
		    		//use vertical transforms to find answer

	    			if(trans.equals("Add") || trans.equals("Delete")){
	    				guess = simpleAdditionAndSubtraction(bImage, getAdditionsAndSubtractions(aImage,cImage));
	    				//displayImages(new BufferedImage[] {aImage,cImage,bImage,guess});
	    			}else{
	    				guess = simpleTransform(bImage, trans);
	    			}
	    			ConfidencePairing con = tryAnswer(guess,answers);
	    			if(con.answerIndex!=-1 && retVal.answerConfidence[con.answerIndex-1]<con.confidence){
	    				retVal.answerConfidence[con.answerIndex-1] = con.confidence;
	    			}
//    				if(con.answerIndex!=-1 && con.confidence >= 1-comparabilityThreshhold){
//    					
//    					retVal.answer = con.answerIndex;
//    					System.out.println("Found answer for " + " v " + trans + " "  + retVal.answer);
//    					return retVal;
//    				}
		    	}
			}

    	}
	
    	//displayImages(new BufferedImage[] {aImage,mirrorImage(aImage,"Vertical")});
    	
		//System.out.println("Found no answer");
    	double highestConfidence = 0;
    	for(int x = 0; x<retVal.answerConfidence.length;x++){
    		if(retVal.answerConfidence[x]>highestConfidence){
    			highestConfidence = retVal.answerConfidence[x];
    			retVal.answer = x+1;
    		}
    	}
    	
    	if(highestConfidence<1-comparabilityThreshhold){
    		retVal.answer = -1;
    	}
    	
    	return retVal;
    }
    
    private ConfidencePairing tryAnswer(BufferedImage guess, RavensFigure[] answers) {
    	int counter = 1;
    	double lowestError = 1.5;
    	int lowestIndex = -1;
    	ConfidencePairing p = new ConfidencePairing(1,1);
		for(RavensFigure fig : answers){
			try{
				BufferedImage ansImage = ImageIO.read(new File(fig.getVisual()));
				//System.out.println("Comparability: " + comparability(guess, ansImage));
				double c = comparability(guess,ansImage);
				if(c < lowestError){
					lowestError = c;
					lowestIndex = counter;
				}
			}catch(IOException e){
				//System.out.println("no image available for answer " + fig.getName() + " in simplecheck2");
				return p;
			}
			counter++;
		}
		p.answerIndex = lowestIndex;
		p.confidence = 1-lowestError;
		return p;
		
	}
	/*
     * figures - an ArrayList of figures representing a 2x2 Raven's Problem
     * answer - given answer to check
     * 
     * compares given figures individual shape transformations (i.e. rotation, reflection, scaling, translation)
     * 
     * return - confidence in answer
     */

    
//	private void displayImages(BufferedImage[] images){
//    	
//    	frame.dispose();
//    	frame.getContentPane().removeAll();
//    	for(BufferedImage image : images){
//    		frame.getContentPane().add(new JLabel(new ImageIcon(image)));
//    	}
//    	
//    	
//    	frame.pack();
//    	frame.setVisible(true);
//    	try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//
//			e.printStackTrace();
//		}
//    }
    
    
    
}
