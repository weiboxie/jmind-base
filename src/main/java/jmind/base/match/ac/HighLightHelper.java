package jmind.base.match.ac;



import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;





/**
 * 对文本进行高亮
 * 
 * @author weibo.xie
 * 
 */
public class HighLightHelper {

	/**
	 * 对文本中出现的关键词进行标注 prefix 加在关键词前，sufix加在关键词后
	 * 
	 * @param content
	 * @param results
	 * @param prefex
	 * @param sufex
	 * @return 调用 toString 方法。
	 */
	public static String hightLight(String content, List<SearchResult> results,
			String prefix, String sufix) {
		StringDecorator dec = new StringDecorator(content);
		for (SearchResult searchResult : results) {
			dec.addOn(prefix, searchResult.getStartPosition());
			dec.addOn(sufix, searchResult.getEndPosition());
		}
		return dec.toString();
	}

	/**
	 * @param content
	 * @param searchResult
	 * @param link
	 *            关联词
	 * @param prefix1
	 *            非关联词前缀
	 * @param sufix1
	 *            非关联词后缀
	 * @param prefix2
	 *            关联词前缀
	 * @param sufix2
	 *            关联词后缀
	 * @return
	 */
	public static String hightLightWithLinkWords(String content,
			List<SearchResult> searchResult, Map<String, Set<String>> link,
			String prefix1, String sufix1, String prefix2, String sufix2) {
		StringDecorator dec = new StringDecorator(content);
		if (searchResult.size() != 0) {
			for (SearchResult keyWord : searchResult) {
				if (link.containsKey(keyWord.getKeyword())) {
					for (SearchResult keyWord1 : searchResult) {
						if (link.get(keyWord.getKeyword()).contains(
								keyWord1.getKeyword())) {
							dec.addOn(prefix2, keyWord.getStartPosition());
							dec.addOn(sufix2, keyWord.getEndPosition());
						}
					}
				} else {
					dec.addOn(prefix1, keyWord.getStartPosition());
					dec.addOn(sufix1, keyWord.getEndPosition());
				}
			}
		}
		return dec.toString();
	}

	private static class StringDecorator {

		private String content;
		private TreeMap<Integer, String> addons;
 
		public StringDecorator() {
			content = "";
			addons = new TreeMap<Integer, String>(new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}

			});
		}

		public StringDecorator(String content) {
			this();
			this.content = content;
		}

		@SuppressWarnings("unused")
		public void setContent(String content) {
			this.content = content;
		}

		public void addOn(String addOn, int position) {
			if (!addons.containsKey(position)) {
				addons.put(position, addOn);
				return;
			}
			addons.put(position, addons.get(position) + addOn);
		}

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			int last = 0;
			for (Integer i : addons.keySet()) {
				buffer.append(content.substring(last, i));
				String addString = addons.get(i);
				last = i;
				buffer.append(addString);
			}
			buffer.append(content.substring(last));
			return buffer.toString();
		}

	}

}
