package com.personal.datamining;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.io.File;
import java.io.IOException;
import java.lang.Math;


/**
 *given two double array, calculate the correlation hierarchet struture of the data
 *constructor:
 *  CorHierarchet(double[] data1, double[] date2)
 *  CorHierarchet()
 *
 *public methods:
 *  claStufeOne(int sizeOfWindow): despite the data durch the time window and calculate 
 *                                 the correaltion coeffizient in jede window
 *  calStufeNext(List<ArrayList<Double>> old, double threshold): calulate the cor in next 
 *                                 edage base on the item in current edage. return List<ArrayList<Double>>
 *  getStufeOne(): get the data in stufe one, return List<ArrayList<Double>>
 *  getLongestWindowInStufe(List<ArrayList<Double>> stufe): get the longest window in the given stufe
 *  getSizeOfData(): return the size of the data
 *  showStufe(List<ArrayList<Double>> stufe): display the data in gegeben stufe in screen.
 *  showStufeOne()
 *  showItem(ArrayList<Double>): display the given item in the screen
 *  readData(String name1, String name2): read data from the given file name. bedingthaft!!!
 *  getBestCW():
 *
 *private attrbute:
 *  int sizeOfData = 200;
 *  double[] data1;
 *  double[] data2;
 *  List<ArrayList<Double> stufe1 = new ArrayList<ArrayList<Double>>();
 *  PearsonsCorrelation cor = new PearsonsCorrelation();
 *
 *attention:
 * data.length % sizeOfWindow == 0 otherwise may throws out of range exception 
 * data1 and data2 should have same size
 */
class CorHierarchet{
    public CorHierarchet(double[] data1, double[] data2){
	sizeOfData = data1.length;
        this.data1 = new double[sizeOfData];
	this.data2 = new double[sizeOfData];
	this.data1 = data1;
	this.data2 = data2;
    }
    public CorHierarchet(){
    
    }
    public void calStufeOne(int sizeOfWindow){
	/**
	  *calculate the correlation coeffizient in stufe one
	  *every ten jear data will be used to calculate the correlation
	  *and store the result in the arrayList stufe1
          *sizeOfWindwo should smaller as sizeOfData and sizeOfData % sizeOfWindow == 0
	**/
	
        int sow = sizeOfWindow;
	for(int i=0 ; i< sizeOfData ; i+=sow){
	    //calculate cor for data window
            if(i+sow<sizeOfData){
		    double[] tmp1 = Arrays.copyOfRange(data1, i, i + sow);
		    double[] tmp2 = Arrays.copyOfRange(data2, i, i + sow);
		    double tmpCor = cor.correlation(tmp1, tmp2);

		    //add data in stufe 1 
		    ArrayList<Double> tmpL = new ArrayList<Double>();
		    tmpL.add(tmpCor);
		    tmpL.add((double)i);
		    int a = i + sow;
		    tmpL.add((double)a);
		    stufe1.add(tmpL);
	    }
	}

    }
    public List<ArrayList<Double>> calStufeNext(List<ArrayList<Double>> old, double threshold){
	List<ArrayList<Double>> stufeOld = new ArrayList<ArrayList<Double>>(old);
	List<ArrayList<Double>> stufeNew = new ArrayList<ArrayList<Double>>();
        boolean isUpdated = true;
	int counter = 0;//counter for update item. when counter == 2, the last 2 items in newList will be combined
	while(isUpdated){
	    //iterate until no more update
	    isUpdated = false;
	    counter = 0;
	    stufeNew.add(stufeOld.get(0));
            for(int i = 1; i < stufeOld.size(); i++){
		//when there has only one item, will not step in the loop
	        stufeOld.get(i).get(0);
		stufeNew.add(stufeOld.get(i));
                counter++;
		if(counter == 1){
		    boolean hasCombined = combine(stufeNew, threshold);
		    counter = 0;
		    isUpdated = hasCombined;
		}
	    }
	    if(isUpdated){
	        stufeOld = new ArrayList<ArrayList<Double>>(stufeNew);
		stufeNew.clear();
	    }
	}
	return stufeNew;
    }
    private boolean combine(List<ArrayList<Double>> stufeNew, double threshold){
        /**
	 *get the last two items from the list and calculate the cor
	 *if cor larger as threshold then save the cor
	 *otherwise drop it.
	 */
	ArrayList<Double> item1 = stufeNew.get(stufeNew.size()-2);
	ArrayList<Double> item2 = stufeNew.get(stufeNew.size()-1);
	int from = item1.get(1).intValue();
	int to = item2.get(2).intValue();
	double[] tmp1 = Arrays.copyOfRange(data1, from, to);
	double[] tmp2 = Arrays.copyOfRange(data2, from, to);
	double tmpCor = cor.correlation(tmp1, tmp2);
	if(tmpCor > threshold){
	    stufeNew.remove(stufeNew.size()-1);
	    stufeNew.remove(stufeNew.size()-1);
	    ArrayList<Double> tmpL = new ArrayList<Double>();
	    tmpL.add(tmpCor);
	    tmpL.add((double)from);
	    tmpL.add((double)to);
	    stufeNew.add(tmpL);
	    return true;
	}
	return false;
    }
    public List<ArrayList<Double>> getStufeOne(){
        return stufe1;
    }
    public void showStufeOne(){
        for(List<Double> item: stufe1){
	    System.out.println("=============");
	    System.out.println("cor: "+item.get(0).toString());
	    System.out.println("ab: "+item.get(1).toString());
	    System.out.println("bis: "+item.get(2).toString());
	}
    }
    public void showStufe(List<ArrayList<Double>> stufe){
        for(List<Double> item: stufe){
	    System.out.println("=============");
	    System.out.println("cor: " + item.get(0).toString());
	    System.out.println("ab: " + item.get(1).toString());
	    System.out.println("bis: " + item.get(2).toString());
	}
    }
    public void readData(String name1, String name2){
        /**
	 *the form of the name is xxx.data
	 */
	try{
	   File d1 = new File("/Users/ihuangyiran/Documents/Workplace_Maven/CorrelationWindow/data/war.data");
	   File d2 = new File("/Users/ihuangyiran/Documents/Workplace_Maven/CorrelationWindow/data/german.data");
	   Scanner s1 = new Scanner(d1);
	   Scanner s2 = new Scanner(d2);
	   int counter = 0;
	   while(s1.hasNext()){
	       if(s1.hasNextDouble()){
	           data1[counter] = s1.nextDouble();
	       }else{
		   System.out.println(s1.next());
	       }
	       if(s2.hasNextDouble()){
	           data2[counter] = s2.nextDouble();
	       }else{
	           s2.next();
	       }
	       counter++;
	       System.out.println(data1[counter]);
	   }
	   s1.close();
	   s2.close();
	}catch(IOException e){
	    e.printStackTrace();
	}catch(InputMismatchException e){
	    
	}
    }
    public ArrayList<Double> getLongestWindowInStufe(List<ArrayList<Double>> stufe){
        ArrayList<Double> longestW = new ArrayList<Double>();
	longestW = stufe.get(0);
	for(int i=1; i < stufe.size(); i++){
	   if((longestW.get(2) - longestW.get(1)) - (stufe.get(i).get(2) - stufe.get(i).get(1)) < 0){
	       longestW = stufe.get(i);
	   }
	}
	return longestW;
    }
    public void showItem(ArrayList<Double> item){
        System.out.println("cor: " + item.get(0).toString());
	System.out.println("ab: " + item.get(1).toString());
	System.out.println("bis: " + item.get(2).toString());
    }
    public int getSizeOfData(){
        return sizeOfData;
    }
    public ArrayList<Double> getBestCW(){
        //get the list of the candidaten
	clearStufeOne();
	calStufeOne(3);
	List<ArrayList<Double>> candidaten = new ArrayList<ArrayList<Double>>();//candidaten for chosing the best
	List<ArrayList<Double>> st = getStufeOne();//data in each etage
	ArrayList<Double> lw = new ArrayList<Double>();//longest item in the same stufe
	for(double i=0.99; i > 0.75; i-=0.05){
	    st = calStufeNext(st, i);
            lw = getLongestWindowInStufe(st);
	    if(lw.get(0) > 0.75){
		//only the item, which cor larger as 0.75, will be accepted.
		if(!candidaten.contains(lw)){
		    candidaten.add(lw);
		}
	    }
	}


	//scale the cor value from [0.75, 1] to [-2,2]
	for(ArrayList<Double> item: candidaten){
	    item.set(0, 4*(item.get(0)-0.75)/0.25-2);
	}
	//compare: when w2 > 2w1-w1*f(x) change the best candidat
	ArrayList<Double> best = candidaten.get(0); //take the item, which largest cor hat, as best
	ArrayList<Double> compareItem = new ArrayList<Double>(); 
	boolean isChanged = true; //when no more change be made to 'best', finish the chosen stage
	int counter = 0; // count the 'best' item
        while(isChanged){
	    isChanged = false;
	    int goDownTo = 4;
	    for(int i = 1; i <= goDownTo; i++){
		//the value of i deside the number of item that will be compared with the 'best'
		if(counter + i >= candidaten.size()){
		    break;
		}
		compareItem = candidaten.get(counter + i);
	    	if(obNeedChange(best, compareItem)){
	            best = compareItem;
		    isChanged = true;
		    counter = counter + i;
		    break;
	    	}
	    }
	}
	best.set(0,(best.get(0)+2)*0.25/4 + 0.75);
	return best;
    }
    private boolean obNeedChange(ArrayList<Double> item1, ArrayList<Double> item2){
        //item1 is the candidat and item2 is the compare item
	//calculate the f(x)
	double x = item1.get(0);
	double fx = x/Math.sqrt(1+x*x);
	//compare
	double w1 = item1.get(2) - item1.get(1);
	double w2 = item2.get(2) - item2.get(1);

	if(w2 > 2*w1 - w1*fx){
	    return true;
	}else{
	    return false;
	}
    }
    private void clearStufeOne(){
        stufe1.clear();
    }
    

    private int sizeOfData = 200;
    private double[] data1;
    private double[] data2;
    private List<ArrayList<Double>> stufe1 = new ArrayList<ArrayList<Double>>(); //ArrayList<Double>: cor + ab + bis
    private List<ArrayList<Double>> stufe2 = new ArrayList<ArrayList<Double>>();
    private PearsonsCorrelation cor = new PearsonsCorrelation();
}
