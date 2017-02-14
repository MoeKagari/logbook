package logbook.gui.logic;

import logbook.gui.logic.data.ShipExpTable;

public class CalcuPracticeExp {

	public static int calcu(int lv1, int lv2, boolean flagship, boolean mvp, double eval) {
		double exp = Math.floor(ShipExpTable.getExp(lv1) / 100.0 + ShipExpTable.getExp(lv2) / 300.0);

		if (exp > 500) exp = Math.floor(500 + Math.sqrt(exp - 500));
		exp = Math.floor(exp * eval);
		if (flagship) exp *= 1.5;
		if (mvp) exp *= 2;

		return (int) Math.floor(exp);
	}

}
