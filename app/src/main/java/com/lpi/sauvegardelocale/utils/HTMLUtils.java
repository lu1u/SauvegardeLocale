package com.lpi.sauvegardelocale.utils;

public class HTMLUtils
{
	public static String filtreHTML(final String value)
	{
		if (value == null)
			return "";
		return value
				.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;")
				.replaceAll("&", "&amp;")
				.replaceAll("\"", "&quot;")
				.replaceAll("'", "&apos;")
				.replaceAll("€", "&euro;")
				.replaceAll("à", "&agrave;")
				.replaceAll("é", "&eacute;")
				.replaceAll("è", "&egrave;")
				.replaceAll("ç", "&ccedil;")
				.replaceAll("\n", "</P>");
	}
}
