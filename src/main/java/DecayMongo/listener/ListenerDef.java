package DecayMongo.listener;

import org.decaywood.EntityDefinition;


public interface ListenerDef {
    
    public void createAction(EntityDefinition entity);
    
    public void deleteAction(EntityDefinition entity);
    
    public void updateAction(EntityDefinition entity);
    
    public void retrieveAction(EntityDefinition entity);
    
}
