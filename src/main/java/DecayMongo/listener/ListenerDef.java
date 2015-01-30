package DecayMongo.listener;

import DecayMongo.EntityDefinition;


public interface ListenerDef {
    
    public void createAction(EntityDefinition entity);
    
    public void deleteAction(EntityDefinition entity);
    
    public void updateAction(EntityDefinition entity);
    
    public void retrieveAction(EntityDefinition entity);
    
}
