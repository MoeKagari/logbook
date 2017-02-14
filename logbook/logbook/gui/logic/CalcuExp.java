package logbook.gui.logic;

public class CalcuExp {

	public static int calcu(int base, boolean flagship, boolean mvp, double eval) {
		double exp = base;

		if (flagship) exp *= 1.5;
		if (mvp) exp *= 2;
		exp *= eval;

		return (int) exp;
	}

}
