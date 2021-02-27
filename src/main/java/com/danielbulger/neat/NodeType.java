package com.danielbulger.neat;

public enum NodeType {
	INPUT(false, 0, false),
	HIDDEN(true, 1, true),
	OUTPUT(false, 2, true);

	private final int order;
	private final boolean sameTypeConnectionAllowed;
	private final boolean activate;

	NodeType(boolean sameTypeConnectionAllowed, int order, boolean activate) {
		this.sameTypeConnectionAllowed = sameTypeConnectionAllowed;
		this.order = order;
		this.activate = activate;
	}

	public int getOrder() {
		return order;
	}

	public boolean isSameTypeConnectionAllowed() {
		return sameTypeConnectionAllowed;
	}

	public boolean shouldActivate() {
		return activate;
	}
}