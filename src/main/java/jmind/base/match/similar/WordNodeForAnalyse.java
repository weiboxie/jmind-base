package jmind.base.match.similar;

import java.util.HashMap;

public class WordNodeForAnalyse {
	private HashMap<Character, WordNodeForAnalyse> m_CharNodes;
	private String m_Word;

	public WordNodeForAnalyse(String wordInit, String allWord) {
		this.m_CharNodes = new HashMap<Character, WordNodeForAnalyse>();
		insertWord(wordInit, allWord);
	}

	public void insertWord(String wordInit, String allWord) {
		if (allWord == null)
			return;
		if ((wordInit == null) || (wordInit.length() == 0)) {
			this.m_Word = allWord;
		} else {
			char key = wordInit.charAt(wordInit.length() - 1);
			String value = null;
			if (wordInit.length() > 1) {
				value = wordInit.substring(0, wordInit.length() - 1);
			}
			WordNodeForAnalyse node = this.m_CharNodes.get(Character
					.valueOf(key));
			if (node != null) {
				node.insertWord(value, allWord);
			} else {
				node = new WordNodeForAnalyse(value, allWord);
				this.m_CharNodes.put(Character.valueOf(key), node);
			}
		}
	}

	public WordNodeForAnalyse get(char key) {
		return this.m_CharNodes.get(Character.valueOf(key));
	}

	public String getWord() {
		return this.m_Word;
	}

	public boolean remove(String word) {
		if (word == null)
			return false;
		if (word.length() > 0) {
			String key = word.substring(word.length() - 1, word.length());
			WordNodeForAnalyse node = this.m_CharNodes.get(key);
			if (node != null) {
				if (node.remove(word.substring(0, word.length() - 1)))
					this.m_CharNodes.remove(key);
				if ((this.m_CharNodes.size() == 0) && (this.m_Word == null)) {
					return true;
				}
			}
		} else if (this.m_Word != null) {
			if (this.m_CharNodes.size() > 0) {
				this.m_Word = null;
				return false;
			}
			return true;
		}

		return false;
	}
}
