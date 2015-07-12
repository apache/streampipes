package de.fzi.cep.sepa.model.client.user;

import com.google.gson.annotations.SerializedName;

import javax.persistence.Entity;
import java.util.ArrayList;
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
	protected List<String> sources;
	protected List<String> sepas;
	protected List<String> actions;
	
	private Set<Role> roles;	
	
	public User(String username, String email, String password, Set<Role> roles, List<String> pipelines, List<String> sources, List<String> sepas, List<String> actions) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.pipelines = pipelines;
		this.sources = sources;
		this.sepas = sepas;
		this.actions = actions;
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
		if (this.pipelines == null) return;
		this.pipelines.add(pipelineId);
	}

	public void deletePipeline(String pipelineId) {
		pipelines.remove(pipelineId);
	}

	public List<String> getSources() {
		return sources;
	}

	public void addSource(String source) {
		if (this.sources == null) return;
		this.sources.add(source);
	}

	public List<String> getSepas() {
		return sepas;
	}

	public void addSepa(String sepa) {
		if (this.sepas == null) return;
		this.sepas.add(sepa);
	}

	public List<String> getActions() {
		return actions;
	}

	public void addAction(String action) {
		this.actions.add(action);
	}

	public void removeAction(String action) {
		this.actions.remove(action);
	}

	public void removeSepa(String sepa) {
		this.sepas.remove(sepa);
	}

	public void removeSource(String source) {
		this.sources.remove(source);
	}

}
