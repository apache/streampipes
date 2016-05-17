package de.fzi.proasense.demonstrator.adapter;

import java.math.BigDecimal;
import java.math.BigInteger;

public class UtilsValue {
	
	/**
	 * Converts the hexadecimal number from the String s to the Float value When
	 * there is a problem with the convertion Float.MIN_VALUE is returned so
	 * check for that
	 */
	public static float hexToDouble(String s) {
		float j = Float.MIN_VALUE;

		try {
			 String binary = hexToBin(s);
			 if (binary.length() == 32) {
				return -getFloat32(binary.substring(1)); 
			 } else {
				return getFloat32(binary); 
			 }
					} catch (NumberFormatException e) {
//			System.out.println("Couldn't transform: " + s);
		}

		return j;
	}

	private static String hexToBin(String s) {
		return new BigInteger(s, 16).toString(2);
	}

	private static float getFloat32(String Binary) {
		int intBits = Integer.parseInt(Binary, 2);
		float myFloat = Float.intBitsToFloat(intBits);
		return myFloat;
	}

	public static String getFourByteString(String hex, int offset) {
		return getByteString(hex, offset, 8);
	}

	public static String getOneByteString(String hex, int offset) {
		return getByteString(hex, offset, 2);
	}

	public static String getByteString(String hex, int offset, int size) {
		if (offset + size <= hex.length()) {
			return hex.substring(offset, offset + size);
		} else {
			return "";
		}
	}

	public static float getFourValue(String hex, int offset) {
		String l = getFourByteString(hex, offset);
		return hexToDouble(getFourByteString(hex, offset));
	}

	public static float getOneValue(String hex, int offset) {
		return hexToDouble(getOneByteString(hex, offset));
	}
	
	 public static BigDecimal round(float d, int decimalPlace) {
	        BigDecimal bd = new BigDecimal(Float.toString(d));
	        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	        return bd;
	    }

	 public static BigDecimal round(double d, int decimalPlace) {
	        BigDecimal bd = new BigDecimal(Double.toString(d));
	        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	        return bd;
	    }

}
