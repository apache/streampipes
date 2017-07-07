package org.streampipes.model.client.pipeline;

import com.google.gson.annotations.SerializedName;

public class PipelineCategory {

	private String categoryName;
	private String categoryDescription;
	
	private @SerializedName("_id") String categoryId;
	private @SerializedName("_rev") String rev;
	
	public PipelineCategory() {
		
	}
	
	public PipelineCategory(String categoryName, String categoryDescription) {
		super();
		this.categoryName = categoryName;
		this.categoryDescription = categoryDescription;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}
	
	
}
