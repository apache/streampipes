/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.model.client.user;

import com.google.gson.annotations.SerializedName;
import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@TsModel
public class User {

	protected @SerializedName("_id") String userId;
	protected @SerializedName("_rev") String rev;
	protected String email;

	protected String username;
	protected String fullName;
	protected String password;

	protected List<Element> ownSources;
	protected List<Element> ownSepas;
	protected List<Element> ownActions;

	protected List<String> preferredSources;
	protected List<String> preferredSepas;
	protected List<String> preferredActions;

	protected List<UserApiToken> userApiTokens;

	protected boolean hideTutorial;
	protected boolean darkMode = false;

	protected Set<Role> roles;

	public User() {
		this.hideTutorial = false;
		this.userApiTokens = new ArrayList<>();
	}
	
	public User(String email, String password, Set<Role> roles, List<Element> ownSources, List<Element> ownSepas, List<Element> ownActions) {
		super();
		this.email = email;
		
		this.password = password;
		this.roles = roles;
		
		this.ownSources = ownSources;
		this.ownSepas = ownSepas;
		this.ownActions = ownActions;

		this.hideTutorial = false;
	}
	
	public User(String email, String password, Set<Role> roles)
	{ 
		this.email = email;
		this.password = password;
		this.roles = roles;

		this.ownActions = new ArrayList<>();
		this.ownSepas = new ArrayList<>();
		this.ownSources = new ArrayList<>();
		
		this.preferredActions = new ArrayList<>();
		this.preferredSepas = new ArrayList<>();
		this.preferredSources = new ArrayList<>();

		this.hideTutorial = false;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public List<UserApiToken> getUserApiTokens() {
		return userApiTokens;
	}

	public void setUserApiTokens(List<UserApiToken> userApiTokens) {
		this.userApiTokens = userApiTokens;
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

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public boolean isDarkMode() {
		return darkMode;
	}

	public void setDarkMode(boolean darkMode) {
		this.darkMode = darkMode;
	}
}
