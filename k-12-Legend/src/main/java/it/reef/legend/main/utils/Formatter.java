package it.reef.legend.main.utils;

import java.text.DecimalFormat;

public interface Formatter {
	static DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	static DecimalFormat timedf = new DecimalFormat("0.####"); // 4 dp

}
