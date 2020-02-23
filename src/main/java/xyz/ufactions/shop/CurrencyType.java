package xyz.ufactions.shop;

public enum CurrencyType {
	MONEY('$');

	private Character symbol;

	CurrencyType(Character symbol) {
		this.symbol = symbol;
	}

	public Character getSymbol() {
		return symbol;
	}
}