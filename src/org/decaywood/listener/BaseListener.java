package org.decaywood.listener;

import org.decaywood.EntityDefinition;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
public class BaseListener implements ListenerDef{

    @Override
    public void createAction(EntityDefinition entity) {}

    @Override
    public void deleteAction(EntityDefinition entity) {}

    @Override
    public void updateAction(EntityDefinition entity) {}

    @Override
    public void retrieveAction(EntityDefinition entity) {}

}
