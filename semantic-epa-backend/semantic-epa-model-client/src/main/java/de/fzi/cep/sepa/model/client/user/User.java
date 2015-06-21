package de.fzi.cep.sepa.model.client.user;

import com.google.gson.annotations.SerializedName;

import javax.persistence.Entity;
import java.util.List;
import java.util.Set;

@Entity
public class User {

	protected @SerializedName("_id") String userId;
	protected @SerializedName("_rev") String rev;
	protected String username;
	protected String email;
	protected String password;
	protected List<String> pipelines;
	
	private Set<Role> roles;	
	
	public User(String username, String email, String password, Set<Role> roles, List<String> pipelines) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.pipelines = pipelines;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public List<String> getPipelines() {
		return pipelines;
	}

	public void setPipelines(List<String> pipelines) {
		this.pipelines = pipelines;
	}

	public void addPipeline(String pipelineId) {
		this.pipelines.add(pipelineId);
	}

	public void deletePipeline(String pipelineId) {
		pipelines.remove(pipelineId);
	}
}
