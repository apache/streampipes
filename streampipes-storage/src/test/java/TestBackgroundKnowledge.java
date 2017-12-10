import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.repository.RepositoryException;

import org.streampipes.storage.controller.StorageManager;


public class TestBackgroundKnowledge {

	public static void main(String[] args)
	{
		try {
			StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getClassHierarchy();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
