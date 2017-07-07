package org.streampipes.model.client.user;

import com.google.gson.annotations.SerializedName;
import org.streampipes.model.client.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

@Entity
public class User {

	private @SerializedName("_id") String userId;
	protected @SerializedName("_rev") String rev;
	protected String username;
	protected String email;
	private String password;
		
	private List<Pipeline> pipelines;
	
	private List<Element> ownSources;
	private List<Element> ownSepas;
	private List<Element> ownActions;
	
	private List<String> preferredSources;
	private List<String> preferredSepas;
	private List<String> preferredActions;

	private boolean hideTutorial;

	private Set<Role> roles;	

	public User() {
		this.hideTutorial = false;
	}
	
	public User(String username, String email, String password, Set<Role> roles, List<Pipeline> pipelines, List<Element> ownSources, List<Element> ownSepas, List<Element> ownActions) {
		super();
		this.username = username;
		this.email = email;
		
		this.password = password;
		this.roles = roles;
		this.pipelines = pipelines;
		
		this.ownSources = ownSources;
		this.ownSepas = ownSepas;
		this.ownActions = ownActions;

		this.hideTutorial = false;
	}
	
	public User(String username, String email, String password, Set<Role> roles)
	{ 
		this.username = username;
		this.email = email;
		this.password = password;
		this.roles = roles;
		
		this.pipelines = new ArrayList<>();
		
		this.ownActions = new ArrayList<>();
		this.ownSepas = new ArrayList<>();
		this.ownSources = new ArrayList<>();
		
		this.preferredActions = new ArrayList<>();
		this.preferredSepas = new ArrayList<>();
		this.preferredSources = new ArrayList<>();

		this.hideTutorial = false;
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


	public List<Pipeline> getPipelines() {
		return pipelines;
	}

	public void setPipelines(List<Pipeline> pipelines) {
		this.pipelines = pipelines;
	}

	public void addOwnPipeline(Pipeline pipeline) {
		if (this.pipelines == null) this.pipelines = new ArrayList<>();
		this.pipelines.add(pipeline);
	}

	public void deletePipeline(String pipelineId) {
		pipelines.remove(pipelineId);
	}

	public List<Element> getOwnSources() {
		return ownSources;
	}

	public void addOwnSource(String source, boolean publicElement) {
		if (this.ownSources == null) this.ownSources = new ArrayList<>();
		this.ownSources.add(new Element(source, publicElement));
	}

	public List<Element> getOwnSepas() {
		return ownSepas;
	}

	public void addOwnSepa(String sepa, boolean publicElement) {
		if (this.ownSepas == null) this.ownSepas = new ArrayList<>();
		this.ownSepas.add(new Element(sepa, publicElement));
	}

	public List<Element> getOwnActions() {
		return ownActions;
	}

	public void addOwnAction(String action, boolean publicElement) {
		this.ownActions.add(new Element(action, publicElement));
	}

	public void removeAction(String action) {
		this.ownActions.remove(find(action, ownActions));
	}

	public void removeSepa(String sepa) {
		this.ownSepas.remove(find(sepa, ownSepas));
	}

	public void removeSource(String source) {
		this.ownSources.remove(find(source, ownSources));
	}
	
	public List<String> getPreferredSources() {
		return preferredSources;
	}

	public void setPreferredSources(List<String> preferredSources) {
		this.preferredSources = preferredSources;
	}

	public List<String> getPreferredSepas() {
		return preferredSepas;
	}

	public void setPreferredSepas(List<String> preferredSepas) {
		this.preferredSepas = preferredSepas;
	}

	public List<String> getPreferredActions() {
		return preferredActions;
	}

	public void setPreferredActions(List<String> preferredActions) {
		this.preferredActions = preferredActions;
	}
	
	public void addPreferredSource(String elementId)
	{
		this.preferredSources.add(elementId);
	}
	
	public void addPreferredSepa(String elementId)
	{
		this.preferredSepas.add(elementId);
	}
	
	public void addPreferredAction(String elementId)
	{
		this.preferredActions.add(elementId);
	}
	
	public void removePreferredSource(String elementId)
	{
		this.preferredSources.remove(elementId);
	}
	
	public void removePreferredSepa(String elementId)
	{
		this.preferredSepas.remove(elementId);
	}
	
	public void removePreferredAction(String elementId)
	{
		this.preferredActions.remove(elementId);
	}

	private Element find(String elementId, List<Element> source)
	{
		return source.stream().filter(f -> f.getElementId().equals(elementId)).findFirst().orElseThrow(IllegalArgumentException::new);
	}

	public boolean isHideTutorial() {
		return hideTutorial;
	}

	public void setHideTutorial(boolean hideTutorial) {
		this.hideTutorial = hideTutorial;
	}

}
