package edu.stanford.hivdb.mutations;

public class AAPercent {

	private final Character aa;
	private final Double percent;
	private final Boolean isDRM;
	private final Boolean isUnusual;
	private final Boolean isApobecMutation;
	private final Boolean isApobecDRM;

	public AAPercent(
		char aa, double percent, Boolean isDRM,
		Boolean isUnusual, Boolean isApobecMutation,
		Boolean isApobecDRM
	) {
		this.aa = aa;
		this.percent = percent;
		this.isDRM = isDRM;
		this.isUnusual = isUnusual;
		this.isApobecMutation = isApobecMutation;
		this.isApobecDRM = isApobecDRM;
	}

	public Character getAA() { return aa; }
	public Double getPercent() { return percent; }
	public Boolean isDRM() { return isDRM; }
	public Boolean isUnusual() { return isUnusual; }
	public Boolean isApobecMutation() { return isApobecMutation; }
	public Boolean isApobecDRM() { return isApobecDRM; }
	public Boolean isStop() { return aa == '*'; }
}
