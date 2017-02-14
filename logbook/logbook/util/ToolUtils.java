package logbook.util;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ToolUtils {

	public static <S, T> void forEach(S[] ss, T[] ts, BiConsumer<S, T> bc) {
		if (ss.length != ts.length) return;
		int len = ss.length;
		for (int i = 0; i < len; i++) {
			bc.accept(ss[i], ts[i]);
		}
	}

	public static <T> void forEach(T[] ts, Consumer<T> consu) {
		for (T t : ts) {
			consu.accept(t);
		}
	}

	public static void forEach(int[] intArray, Consumer<Integer> consu) {
		for (int i : intArray) {
			consu.accept(i);
		}
	}

	public static int[] arrayCopy(int[] ts) {
		return Arrays.copyOf(ts, ts.length);
	}

	public static <T> void forEach(T[] ts, int[] is, BiConsumer<T, Integer> bc) {
		if (ts.length != is.length) return;
		int len = ts.length;
		for (int i = 0; i < len; i++) {
			bc.accept(ts[i], is[i]);
		}
	}

	public static int[] doubleToInteger(double[] ds, Function<Double, Integer> fun) {
		int len = ds.length;
		int[] is = new int[len];
		for (int index = 0; index < len; index++) {
			is[index] = fun.apply(ds[index]);
		}
		return is;
	}

}
