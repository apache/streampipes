package org.streampipes.pe.mixed.flink.samples.wordcount;

public class Word {

	private String word;
	private int count;
	
	public Word() {
		
	}

	public Word(String word, int count) {
		super();
		this.word = word;
		this.count = count;
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}



	
	
}
