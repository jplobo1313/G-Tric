package com.gtric.types;

public enum TimeProfile {
	RANDOM ("Random"), 
	UP_REGULATED ("Up-Regulated"), 
	DOWN_REGULATED ("Down-Regulated");
	
	private final String name;
	
	TimeProfile(String type) {
		name = type;
	}
	
	public boolean equalsName(String otherName) {
 
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
}
