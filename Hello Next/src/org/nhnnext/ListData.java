package org.nhnnext;

public class ListData {
	String name;
	String imgURL;
	int village;
	String subject;
	
	public ListData(String name, String imgURL, int village, String subject) {
		this.name = name;
		this.imgURL = imgURL;
		this.village = village;
		this.subject = subject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgURL() {
		return imgURL;
	}

	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}

	public void setVillage(int village) {
		this.village = village;
	}
	
	public int getVillage() {
		return village;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}


