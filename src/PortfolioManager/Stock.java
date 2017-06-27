package PortfolioManager;

public class Stock extends Asset{

	public Stock(double value, double variation, String ticker) {
		super(value, variation, ticker);
	}
	
	public Stock(double value, String ticker) {
		super(value, 0, ticker);
	}
	
	public Stock() {
		super(0, 0, "");
	}
}
