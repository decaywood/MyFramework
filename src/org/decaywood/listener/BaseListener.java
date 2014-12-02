package org.decaywood.listener;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

import org.decaywood.EntityDefinition;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
public class BaseListener implements ListenerDef{
    
    protected static final Logger log = Logger.getLogger(BaseListener.class.getName());
    

    protected List<Field> groupReferenceFields;
    protected List<Field> referenceFields;
    
   
    public BaseListener() {}
    
    public BaseListener(List<Field> groupRefFields, List<Field> refFields){
        this.groupReferenceFields = groupRefFields;
        this.referenceFields = refFields;
    }
    
    @Override
    public void createAction(EntityDefinition entity) {}

    @Override
    public void deleteAction(EntityDefinition entity) {}

    @Override
    public void updateAction(EntityDefinition entity) {}

    @Override
    public void retrieveAction(EntityDefinition entity) {}

}
