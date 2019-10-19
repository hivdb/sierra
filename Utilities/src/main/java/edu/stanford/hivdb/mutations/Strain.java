package edu.stanford.hivdb.mutations;

public enum Strain {
	HIV1("HIV-1", "hiv1b"),
	HIV2A("HIV-2 Group A", "hiv2a"),
	HIV2B("HIV-2 Group B", "hiv2b");
	
	private final String displayText;
	private final String nucaminoProfile;
	
	private Strain(String displayText, String nucaminoProfile) {
		this.displayText = displayText;
		this.nucaminoProfile = nucaminoProfile;
	}
	
	public String getDisplayText() {
		return displayText;
	}
	
	public String getNucaminoProfile() {
		return nucaminoProfile;
	}
	
	public Gene[] getGenes() {
		return Gene.values(this);
	}
}
