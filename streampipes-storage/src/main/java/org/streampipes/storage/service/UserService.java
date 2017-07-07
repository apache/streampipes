package org.streampipes.storage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lightcouch.CouchDbClient;

import org.streampipes.commons.exceptions.ElementNotFoundException;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.client.user.User;
import org.streampipes.storage.controller.StorageManager;
import org.streampipes.storage.impl.UserStorage;
import org.streampipes.storage.util.Utils;

public class UserService {

	private UserStorage userStorage;
	
	public UserService(UserStorage userStorage)
	{
		this.userStorage = userStorage;
	}
	
	public List<Pipeline> getPublicPipelines(String username)
	{
		List<Pipeline> pipelines = new ArrayList<>();
		userStorage()
				.getAllUsers()
				.stream()
				.map(u -> u.getPipelines().stream().filter(p -> p.isPublicElement()).collect(Collectors.toList())).forEach(pipelines::addAll);
		return pipelines;
	}
	
	public List<Pipeline> getOwnPipelines(String username)
	{
		return StorageManager.INSTANCE.getPipelineStorageAPI().getAllPipelines().stream().filter(p -> p.getCreatedByUser().equals(username)).collect(Collectors.toList());
	}
	
	public Pipeline getPipeline(String username, String pipelineId) throws ElementNotFoundException
	{
		return StorageManager.INSTANCE.getPipelineStorageAPI().getAllPipelines().stream().filter(p -> p.getPipelineId().equals(pipelineId)).findFirst().orElseThrow(ElementNotFoundException::new);
	}	

    /**
     * 	Own Elements
     * 
     */
		
    public void addOwnPipeline(String username, Pipeline pipeline) {
     
        if (!checkUser(username)) return;
//        User user = userStorage.getUser(username);
//        user.addOwnPipeline(pipeline);
//        userStorage.updateUser(user);
        StorageManager.INSTANCE.getPipelineStorageAPI().storePipeline(pipeline);
    }

    public void addOwnSource(String username, String elementId, boolean publicElement) {
        if (!checkUser(username)) return;
        User user = userStorage.getUser(username);
        user.addOwnSource(elementId, publicElement);
        userStorage.updateUser(user);
    }

    public void addOwnAction(String username, String elementId, boolean publicElement) {
        if (!checkUser(username)) return;
        User user = userStorage.getUser(username);
        user.addOwnAction(elementId, publicElement);
        userStorage.updateUser(user);
    }

    public void addOwnSepa(String username, String elementId, boolean publicElement) {
        if (!checkUser(username)) return;
        User user = userStorage.getUser(username);
        user.addOwnSepa(elementId, publicElement);
        userStorage.updateUser(user);
    }
    
    /**
     * Remove pipeline reference from user.
     * @param username
     * @param pipelineId
     */
	
	public void deleteOwnPipeline(String username, String pipelineId) {
        if (checkUser(username)) {
            User user = userStorage.getUser(username);
            user.deletePipeline(pipelineId);
            userStorage.updateUser(user);
        }
    }
	
	public void deleteOwnAction(String username, String actionId) {
        if (checkUser(username)) {
            User user = userStorage.getUser(username);
            user.getOwnActions().removeIf(a -> a.getElementId().equals(actionId));
            userStorage.updateUser(user);
            //TODO remove actions from other users
        }
    }
	
	public void deleteOwnSource(String username, String sourceId) {
        if (checkUser(username)) {
            User user = userStorage.getUser(username);
            user.getOwnSources().removeIf(a -> a.getElementId().equals(sourceId));
            userStorage.updateUser(user);
        }
    }
	
	public void deleteOwnSepa(String username, String sepaId) {
        if (checkUser(username)) {
            User user = userStorage.getUser(username);
            user.getOwnSepas().removeIf(a -> a.getElementId().equals(sepaId));
            userStorage.updateUser(user);
        }
    }
    
    /**
     * 	Favorites
     * 
     */
    
    public void addSepaAsFavorite(String username, String elementId)
    {
        if (!checkUser(username)) return;
        User user = userStorage.getUser(username);
        user.addPreferredSepa(elementId);
        userStorage.updateUser(user);
    }
    
    public void addActionAsFavorite(String username, String elementId)
    {
        if (!checkUser(username)) return;
        User user = userStorage.getUser(username);
        user.addPreferredAction(elementId);
        userStorage.updateUser(user);
    }
    
    public void addSourceAsFavorite(String username, String elementId)
    {
        if (!checkUser(username)) return;
        User user = userStorage.getUser(username);
        user.addPreferredSource(elementId);
        userStorage.updateUser(user);
    }
    
    public void removeSepaFromFavorites(String username, String elementId)
    {
    	CouchDbClient dbClient = Utils.getCouchDbUserClient();
        if (!checkUser(username)) return;
        User user = userStorage.getUser(username);
        user.removePreferredSepa(elementId);
        dbClient.update(user);
        dbClient.shutdown();
    }
    
    public void removeActionFromFavorites(String username, String elementId)
    {
    	CouchDbClient dbClient = Utils.getCouchDbUserClient();
        if (!checkUser(username)) return;
        User user = userStorage.getUser(username);
        user.removePreferredAction(elementId);
        dbClient.update(user);
        dbClient.shutdown();
    }
    
    public void removeSourceFromFavorites(String username, String elementId)
    {
    	CouchDbClient dbClient = Utils.getCouchDbUserClient();
        if (!checkUser(username)) return;
        User user = userStorage.getUser(username);
        user.removePreferredSource(elementId);
        dbClient.update(user);
        dbClient.shutdown();
    }

    /**
     * 
     * Get actions/sepas/sources
     * 
     */
    
    public List<String> getOwnActionUris(String username)
    {
    	return userStorage.getUser(username).getOwnActions().stream().map(r -> r.getElementId()).collect(Collectors.toList());
    }
    
    public List<String> getFavoriteActionUris(String username)
    {
    	return userStorage.getUser(username).getPreferredActions();
    }
    
    public List<String> getAvailableActionUris(String email)
    {
    	List<String> actions = new ArrayList<>(getOwnActionUris(email));
    	System.out.println(userStorage.getAllUsers().size());
		userStorage
				.getAllUsers()
				.stream()
				.filter(u -> !(u.getEmail().equals(email)))
				.map(u -> u.getOwnActions().stream().filter(p -> p.isPublicElement()).map(p -> p.getElementId()).collect(Collectors.toList())).forEach(actions::addAll); 
		return actions;
    }
    
    public List<String> getOwnSepaUris(String username)
    {
    	return userStorage.getUser(username).getOwnSepas().stream().map(r -> r.getElementId()).collect(Collectors.toList());
    }
    
    public List<String> getAvailableSepaUris(String username)
    {
    	List<String> sepas = new ArrayList<>(getOwnSepaUris(username));
		userStorage
				.getAllUsers()
				.stream()
				.filter(u -> !(u.getUsername().equals(username)))
				.map(u -> u.getOwnSepas().stream().filter(p -> p.isPublicElement()).map(p -> p.getElementId()).collect(Collectors.toList())).forEach(sepas::addAll); 
		return sepas;
    }
    
    public List<String> getFavoriteSepaUris(String username)
    {
    	return userStorage.getUser(username).getPreferredSepas();
    }
    
    public List<String> getOwnSourceUris(String username)
    {
    	return userStorage.getUser(username).getOwnSources().stream().map(r -> r.getElementId()).collect(Collectors.toList());
    }
    
    public List<String> getAvailableSourceUris(String username)
    {
    	List<String> sources = new ArrayList<>(getOwnSepaUris(username));
		userStorage
				.getAllUsers()
				.stream()
				.filter(u -> !(u.getUsername().equals(username)))
				.map(u -> u.getOwnSources().stream().filter(p -> p.isPublicElement()).map(p -> p.getElementId()).collect(Collectors.toList())).forEach(sources::addAll); 
		return sources;
    }
    
    public List<String> getFavoriteSourceUris(String username)
    {
    	return userStorage.getUser(username).getPreferredSources();
    }

    private UserStorage userStorage() {
        return StorageManager.INSTANCE.getUserStorageAPI();
    }
    
    /**
    *
    * @param username
    * @return True if user exists exactly once, false otherwise
    */
   public boolean checkUser(String username) {
      return userStorage.checkUser(username);
   }
   
   public static void main(String[] args)
   {
	   System.out.println(new UserService(StorageManager.INSTANCE.getUserStorageAPI()).getAvailableActionUris("riemer@fzi.de"));
   }

}
