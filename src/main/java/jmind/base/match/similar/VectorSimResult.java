package jmind.base.match.similar;

public class VectorSimResult {

	private int aLength;  // a长度
	private int bLength; // b长度
	private int simCount; //相同单词长度

	public int getaLength() {
		return aLength;
	}

	public void setaLength(int aLength) {
		this.aLength = aLength;
	}

	public int getbLength() {
		return bLength;
	}

	public void setbLength(int bLength) {
		this.bLength = bLength;
	}

	public int getSimCount() {
		return simCount;
	}

	public void setSimCount(int simCount) {
		this.simCount = simCount;
	}

}
