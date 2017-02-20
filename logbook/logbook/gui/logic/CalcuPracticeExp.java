package logbook.gui.logic;

import logbook.gui.logic.data.ShipExpMap;

public class CalcuPracticeExp {

	public static int calcu(int lv1, int lv2, boolean flagship, boolean mvp, double eval) {
		double exp = Math.floor(ShipExpMap.getExp(lv1) / 100.0 + ShipExpMap.getExp(lv2) / 300.0);

		if (exp > 500) exp = Math.floor(500 + Math.sqrt(exp - 500));
		exp = Math.floor(exp * eval);
		if (flagship) exp *= 1.5;
		if (mvp) exp *= 2;

		return (int) Math.floor(exp);
	}

}
