package uk.co.ElmHoe.Prison.Utilities;

import java.text.DecimalFormat;

public class StringUtility {

	public static String formatMoney(double money)
	{
		DecimalFormat formatter = new DecimalFormat("$###,###,###");
		return formatter.format(money);
	}
}
