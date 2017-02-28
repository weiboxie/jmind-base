package jmind.base.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class WordToSpell {

	//  国标码和区位码转换常量
	private static final int GB_SP_DIFF = 160;

	// 存放国标一级汉字不同读音的起始区位码
	private static final int[] secPosvalueList = { 1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212,
			3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5600 };

	// 存放国标一级汉字不同读音的起始区位码对应读音
	private static final char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o',
			'p', 'q', 'r', 's', 't', 'w', 'x', 'y', 'z' };

	private static final Map<Character, Character> unKnowChar = new HashMap<Character, Character>();
	static {
		unKnowChar.put('卦', 'g');
		unKnowChar.put('瞭', 'l');
	}

	// 获取一个字符串的拼音码
	public static String getFirstLetter(String oriStr) {
		String str = oriStr.toLowerCase();
		StringBuffer buffer = new StringBuffer();
		char ch;
		char[] temp;
		for (int i = 0; i < str.length(); i++) { //依次处理str中每个字符
			ch = str.charAt(i);
			if (unKnowChar.containsKey(ch)) {
				buffer.append(unKnowChar.get(ch));
				continue;
			}
			temp = new char[] { ch };
			byte[] uniCode = null;
			try {
				uniCode = new String(temp).getBytes("GB2312");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (uniCode[0] > 0 && uniCode[0] < 128) { // 非汉字
				buffer.append(temp);
			} else {
				buffer.append(convert(uniCode));
			}
		}
		return buffer.toString();
	}

	/** 获取一个汉字的拼音首字母。
	 * GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
	 * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
	 * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
	 */

	private static char convert(byte[] bytes) {

		char result = '-';
		int secPosvalue = 0;
		int i;
		for (i = 0; i < bytes.length; i++) {
			bytes[i] -= GB_SP_DIFF;
		}
		secPosvalue = bytes[0] * 100 + bytes[1];
		for (i = 0; i < 23; i++) {
			if (secPosvalue >= secPosvalueList[i] && secPosvalue < secPosvalueList[i + 1]) {
				result = firstLetter[i];
				break;
			}
		}
		return result;
	}

	public static String getBarcode(String isbn) {
		String barcode = "978";
		int a = 0, b = 0;
		int m = 0;
		int c = 0; //偶数和为a，计数和为b
		//获取中间9位数字
		isbn = isbn.substring(isbn.indexOf("-") - 1, isbn.lastIndexOf("-"));
		isbn = isbn.replaceAll("-", ""); //去掉第一个-
		isbn = isbn.replaceAll("-", ""); //去掉第二个-
		barcode += isbn;
		for (int i = 0; i < barcode.length(); i++) {
			if (i % 2 == 0) {
				b += Integer.parseInt(barcode.substring(m, m + 1));
				m++;
			} else {
				a += Integer.parseInt(barcode.substring(m, m + 1));
				m++;
			}
		}
		a = a * 3; //偶数和乘3
		c = a + b; //a+b
		int last = 0;
		String str = c + ""; //获取c的字符串
		last = 10 - Integer.parseInt(str.substring(str.length() - 1, str.length()));
		if (last == 10) {
			last = 0;
		}
		barcode = barcode + last;
		return barcode;
	}

	public static void main(String[] arg) {

		String name = getFirstLetter("108IT业界 129享乐生活");

		System.out.println(name);
		// System.out.println(getBarcode("-20265"));
	}

	public static boolean hasFullSize(String inStr) {
		if (inStr.getBytes().length != inStr.length()) {
			return true;
		}
		return false;
	}
}