package com.danielbulger.neat;

public enum NodeType {
	INPUT(false, 0),

	HIDDEN(true, 1),

	OUTPUT(false, 2);

	private final int order;

	private final boolean sameTypeConnectionAllowed;

	NodeType(boolean sameTypeConnectionAllowed, int order) {
		this.sameTypeConnectionAllowed = sameTypeConnectionAllowed;
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	public boolean isSameTypeConnectionAllowed() {
		return sameTypeConnectionAllowed;
	}
}