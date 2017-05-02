import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.LinearRegression.Interface.LinearRegression;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class MyLinearRegression implements LinearRegression {

	private List<ArrayRealVector> allData = new ArrayList<ArrayRealVector>();
	private List<ArrayRealVector> learnData = new ArrayList<ArrayRealVector>();
	private List<ArrayRealVector> testData = new ArrayList<ArrayRealVector>();
	
	private XYSeriesCollection dataset = new XYSeriesCollection();
	
	
	private List<ArrayRealVector> learnDataFeatures = new ArrayList<ArrayRealVector>();
	private List<ArrayRealVector> learnDataResult = new ArrayList<ArrayRealVector>();
	private List<ArrayRealVector> testDataFeatures = new ArrayList<ArrayRealVector>();
	private List<ArrayRealVector> testDataResult = new ArrayList<ArrayRealVector>();
	private List<ArrayRealVector> thetaList = new ArrayList<ArrayRealVector>();
	
	private List<Double> errorList = new ArrayList<Double>();
	
	private int size;
	private ArrayRealVector theta;
	private double alpha;
	private double hypothesis;
	private double error;
	
	@Override
	public List<ArrayRealVector> getDataset() {
		
		File file = new File("winequality-red.csv");
		
			try {
				CSVParser CSVdata = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.DEFAULT.withDelimiter(';'));
				List<CSVRecord> CSVList = CSVdata.getRecords();
				
				Iterator<CSVRecord> iterator = CSVList.iterator();
				
				/*for(CSVRecord x: CSVList)
				{
					System.out.println(x);
				}*/
				
				
				iterator.next();
				
				while(iterator.hasNext()) {
					ArrayRealVector temp = new ArrayRealVector();
					
					for(String string : iterator.next()) {
							temp=(ArrayRealVector)temp.append(Double.parseDouble(string));

					}
					
					allData.add(temp);
				
					
				}
				//System.out.println(allData.size());
				
				
				//System.out.println(allData.get(0));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
		return allData;
	}

	@Override
	public List<ArrayRealVector> getLearnDataset() {
		
		size = (int) (allData.size()*0.9);
		
		for(int i = 0; i < size ; i++) {
			
			learnData.add(allData.get(i));
			
		}
		
		//System.out.println(learnData.size());
		
		return learnData;
	}

	@Override
	public List<ArrayRealVector> getTestDataset() 
	{
		
		for(int i = size; i<allData.size(); i++)
		{
			testData.add(allData.get(i));
		}
		//System.out.println(testData.size());
		return testData;
	}

	@Override
	public void learnRegression() {
		//podzielenie zbioru uczacego na cechy i ocene
		for(int i = 0; i < size; i++){
			
			ArrayRealVector tempF = new ArrayRealVector();
			ArrayRealVector tempR = new ArrayRealVector();
		
			for(int j = 0; j < 11; j++){
				
				tempF = (ArrayRealVector) tempF.append(learnData.get(i).getEntry(j));
			}
				
			tempR = (ArrayRealVector) tempR.append(learnData.get(i).getEntry(11));
			
			learnDataFeatures.add(tempF);
			learnDataResult.add(tempR);
			
			}
		//dla zbioru testujacego
		for(int i = 0; i < testData.size(); i++){
			
			ArrayRealVector tempF = new ArrayRealVector();
			ArrayRealVector tempR = new ArrayRealVector();
		
			for(int j = 0; j < 11; j++){
				
				tempF = (ArrayRealVector) tempF.append(testData.get(i).getEntry(j));
			}
				tempR = (ArrayRealVector) tempR.append(testData.get(i).getEntry(11));
			
				testDataFeatures.add(tempF);
				testDataResult.add(tempR);
				
		}
		
		generatingAlpha();
		generatingTheta();
		
		for(int j = 0; j<100; j++)
		{
			for(int i = 0; i<size; i++)
			{
				hypothesis = multiplyMatrix(theta, learnDataFeatures.get(i));
				hypothesis = alpha*(hypothesis - learnDataResult.get(i).getEntry(0));
				theta = theta.subtract(learnDataFeatures.get(i).mapMultiply(hypothesis));
			}
			
			//System.out.println(theta);
			thetaList.add(theta);
		}
		
		//System.out.println("dziala");
	}

	@Override
	public void testLearnedRegression() {
		
		double e = 0;
		double temp = 0;
		
		for(int i = 0; i<100; i++) 
		{
			
			
			for(int j = 0; j < testData.size(); j++) 
			{
				
				e = multiplyMatrix(thetaList.get(i), testDataFeatures.get(j));
				e = e - testDataResult.get(j).getEntry(0);
				e = (Math.pow(e, 2))/2;
				
				
				temp += e;
			}
			
			error = temp/testData.size();
			temp = 0;
			errorList.add(error);
		
			
			//System.out.println(error);
		}
		
		
		XYSeries series = new XYSeries("Blad");
		
		for(int i = 0; i<errorList.size(); i++)
		{
			series.add(i, errorList.get(i));
		}
		
		dataset.addSeries(series);
		
		
	}
	
	public ArrayRealVector generatingTheta(){
		Random random = new Random();
		 theta = new ArrayRealVector();
		 
		 for(int i = 0; i < 11; i++)
		 {
			 theta = (ArrayRealVector) theta.append(random.nextDouble()*2-1);
		 }
		 
		
		return theta;
	}
	
	public void generatingAlpha(){
		Random random = new Random();
		alpha = random.nextDouble()*0.0001;
		
	}
	
	public static double multiplyMatrix(ArrayRealVector a, ArrayRealVector b) {
		
		double result = 0;
		
		for(int i = 0; i<a.getDimension(); i++)
		{
			 result = result + a.getEntry(i)*b.getEntry(i);
		}
		
		return result;
	}
	
	


	
	public void draw()
	{
		JFrame ramka = new JFrame();

		JPanel chartPanel = createChartPanel();
		ramka.add(chartPanel, BorderLayout.CENTER);

		ramka.setSize(640, 480);
		ramka.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ramka.setVisible(true);
	}
	
	private JPanel createChartPanel() {

		String chartTitle = "Wykres";
		String xAxisLabel = "X";
		String yAxisLabel = "Y";

		XYDataset data = dataset;

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, data, PlotOrientation.VERTICAL ,
		         true , true , false);

		ChartPanel chartPanel = new ChartPanel( chart );
		 chartPanel.setPreferredSize( new java.awt.Dimension( 640 , 480 ) );
		
		return new ChartPanel(chart);
	}

	

}
