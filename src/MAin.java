
public class MAin {

	public static void main(String[] args) {
		
		
		MyLinearRegression regression = new MyLinearRegression();
		
		regression.getDataset();
		regression.getLearnDataset();
		regression.getTestDataset();
		regression.learnRegression();
		regression.testLearnedRegression();
		regression.draw();

	}

}
