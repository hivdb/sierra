package edu.stanford.hivdb.mutations;

public enum Strain {
	HIV1("HIV-1"),
	HIV2A("HIV-2 Group A"),
	HIV2B("HIV-2 Group B");
	
	private final String displayText; 
	
	private Strain(String displayText) {
		this.displayText = displayText;
	}
	
	public String getDisplayText() {
		return displayText;
	}
}
