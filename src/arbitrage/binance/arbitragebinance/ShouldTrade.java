package arbitragebinance;

public class ShouldTrade {
	double amount;
	boolean shouldTrade;
	boolean isNewTrade;
	public ShouldTrade(boolean shouldTrade, boolean isNewTrade) {
		this.shouldTrade = shouldTrade;
		this.isNewTrade = isNewTrade;
	}
	public ShouldTrade(boolean shouldTrade, boolean isNewTrade, double amt) {
		this.shouldTrade = shouldTrade;
		this.isNewTrade = isNewTrade;
		this.amount = amt;
	}
	public boolean isNewTrade() {
		return isNewTrade;
	}
	public void setNewTrade(boolean isNewTrade) {
		this.isNewTrade = isNewTrade;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmt(double amt) {
		this.amount = amt;
	}
	public boolean isShouldTrade() {
		return shouldTrade;
	}
	public void setShouldTrade(boolean shouldTrade) {
		this.shouldTrade = shouldTrade;
	}
}
