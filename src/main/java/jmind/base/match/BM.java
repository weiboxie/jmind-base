package jmind.base.match;



/**
* BM，基于后缀的滑动。
 * 
 * 单模式搜索
 * @author weibo.xie
 * 2011-11-29
 */
public class BM {

	public static int search(String content, String pattern) {
		int patternLength = pattern.length();
		int contentLength = content.length();
		char[] patternChars = pattern.toCharArray();
		char[] contentChars = content.toCharArray();

		int patternPoint = patternLength - 1;// pattern起始点,从pattern的末位开始匹配
		int contentPointer = patternPoint;// 同上

		// ----------------------------------
		// 开始处理
		while (contentPointer < contentLength) {

			// 向左匹配，向右滑动
			OutLoop: for (int i = patternPoint; i >= 0; i--) {

				char contentChar = contentChars[contentPointer];// contentPointer总是变
				char pat = patternChars[i];// patternChar总是不变
				if (pat == contentChar) {
					if (i == 0) {// 如果能i进行到0，代表找到字符了。
						return contentPointer;
					} else {
						contentPointer--;// 如果找到相同的字符，指针往左移动
					}

				} else {

					// 如果第一个就不一样
					if (i != patternPoint) {// 末m位都匹配上了，到i这个位置就匹配不上了

						char _l = patternChars[i + 1];
						if (contentChar == _l) {
							contentPointer += patternLength - i + 1;
							break OutLoop;
						}

						for (int j = i; j >= 0; j--) {
							char _k = patternChars[j];
							if (contentChar == _k) {
								contentPointer += patternLength - j;
								break OutLoop;
							}
						}

						contentPointer += patternLength - i;
						break OutLoop;

					} else {
						for (int j = i; j >= 0; j--) {
							char _k = patternChars[j];
							if (contentChar == _k) {
								contentPointer += patternPoint - j;
								break OutLoop;
							}
						}

						contentPointer += patternLength;
						break OutLoop;
					}
				}

			}

		}

		return -1;
	}
	
	public static void main(String[] args){
		int k= BM.search("sadfdsz中国人真是太好了", "中国");
		System.out.println(k);
	}
}

