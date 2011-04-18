/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */

package org.hyperic.hq.grouping.critters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.hyperic.hq.grouping.Critter;
import org.hyperic.hq.grouping.CritterType;
import org.hyperic.hq.grouping.GroupException;
import org.hyperic.hq.grouping.prop.BasicCritterPropDescription;
import org.hyperic.hq.grouping.prop.CritterProp;
import org.hyperic.hq.grouping.prop.CritterPropDescription;
import org.hyperic.hq.grouping.prop.CritterPropType;
import org.hyperic.hq.grouping.prop.EnumCritterPropDescription;
import org.hyperic.util.HypericEnum;

/**
 * BaseCritterType provides common routines for setting up type descriptions, 
 * getting localized strings, and validating creation parameters.
 * 
 * Subclasses should call initialize() in the constructor.
 */
public abstract class BaseCritterType
    implements CritterType
{
    private ResourceBundle _bundle;
    private String _propPrefix;
    private String _name;
    private String _desc;
    private Map    _propDescs;
    
    public BaseCritterType() {
    }
    
    protected String getResourceProperty(String propSuffix) {
        return _bundle.getString(_propPrefix + propSuffix).trim();
    }
   
    /**
     * Initialize the name and description of the critter as well as 
     * internal storage for prop descriptions.  
     * 
     * @param bundleName Name of the resource bundle (org.hyperc.hq...Resources)
     * @param propPrefix Prefix to use before all properties.
     * 
     * The BaseCritterType will load properties for in the following form:
     *     propPrefix.critter.name=
     *     propPrefix.critter.desc=
     *     propPrefix.critterProp.propId.name=
     *     propPrefix.critterProp.propId.purpose=
     *     
     * Where propId can be specified for all the different properties a
     * resource supports.  propId must match the value returned from
     * CritterProp.getId()
     */
    protected void initialize(String bundleName, String propPrefix) {
        _bundle     = ResourceBundle.getBundle(bundleName);
        _propPrefix = propPrefix + ".";
        _name       = getResourceProperty("critter.name"); 
        _desc       = getResourceProperty("critter.desc");
        _propDescs  = new HashMap();
    }
    
    /**
     * Adds a prop description which will get returned via 
     * getPropDescriptions().
     * 
     * @param propName The property name to use when looking up the localized
     *                 value.
     * @param type     The critter prop type
     * @param required If true, the property is required to create the critter
     *                 
     * propName is used in conjunction with the propPrefix (from initialize())
     * The properties loaded from the resource bundle will be of the following
     * form: 
     *     propPrefix.critterProp.propName.name
     *     propPrefix.critterProp.propName.purpose
     *     
     * Each call to addPropDescription adds a new property, and thus will
     * require 2 more localized properties. 
     */
    protected void addPropDescription(String propId, CritterPropType type,
                                      boolean required) 
    { 
        String propName    = getPropName(propId);
        String propPurpose = getPropPurpose(propId);
        _propDescs.put(propId, 
                       new BasicCritterPropDescription(type, propId, propName,
                                                       propPurpose, required));
    }
    
    protected void addEnumPropDescription(String propId, Class enumClass, 
                                          boolean required)  
    {
        if (HypericEnum.class.isAssignableFrom(enumClass) == false) {
            throw new IllegalArgumentException("enumClass must extend " + 
                                               "HypericEnum");
        }
        String propName    = getPropName(propId);
        String propPurpose = getPropPurpose(propId);
        EnumCritterPropDescription d = 
            new EnumCritterPropDescription(propId, propName,
                                           propPurpose, enumClass, required);
        _propDescs.put(propId, d); 
    }

    private String getPropPurpose(String propId) {
        return getResourceProperty("critterProp." + propId + ".purpose");
    }

    private String getPropName(String propId) {
        return getResourceProperty("critterProp." + propId + ".name");
    }

    protected void addPropDescription(String propId, CritterPropType type) {
        addPropDescription(propId, type, true);
    }

    public List getPropDescriptions() {
        return Collections.unmodifiableList(new ArrayList(_propDescs.values()));
    }

    public String getDescription() {
        return _desc;
    }

    public String getName() {
        return _name;
    }
    
    public ResourceBundle getBundle() {
        return _bundle;
    }

    /**
     * Validate a list of {@link CritterPropDescription}s against the 
     * previously defined descriptions.
     * 
     * Calls to addPropDescription() pre-populate the critter type with the
     * props that are valid.  This method ensures that a list of 
     * {@link CritterPropDescription}s match the valid types.
     * 
     * @param props a map of propIds onto CritterProps
     */
    protected void validate(Map props) 
        throws GroupException
    {
        for (Iterator i=_propDescs.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry ent = (Map.Entry)i.next();
            String propId = (String)ent.getKey();
            CritterPropDescription desc = 
                (CritterPropDescription)ent.getValue(); 

            if (!desc.isRequired())
                continue;
            
            CritterProp prop = (CritterProp)props.get(propId);
            if (prop == null) {
                throw new GroupException("Required property [" + propId + 
                                         "] not specified");
            } else if (prop.getType().equals(desc.getType()) == false) {
                throw new GroupException("Property [" + propId + "] must " +
                                         "be of type " + 
                                         desc.getType().getDescription() + 
                                         " (was " + 
                                         prop.getType().getDescription() + ")");
            }
        }
        
        for (Iterator i=props.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry ent = (Map.Entry)i.next();
            String propId = (String)ent.getKey();
            
            if (!_propDescs.containsKey(propId)) {
                throw new GroupException("Specified a property which is " +
                                         "unknown by the critter [" + propId + 
                                         "]"); 
            }
        }
    }
    
    /**
     * Returns a localized MessageFormat, useful for returning the 
     * critter config.
     * 
     * @see Critter#getConfig()
     */
    public MessageFormat getInstanceConfig() {
        return new MessageFormat(_bundle.getString(_propPrefix + 
                                                   "critter.config"));
    }
}