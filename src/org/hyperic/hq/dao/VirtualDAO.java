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

package org.hyperic.hq.dao;

import java.util.Collection;

import org.hibernate.Session;
import org.hyperic.hq.authz.Virtual;
import org.hyperic.hq.authz.Resource;
import org.hyperic.hq.authz.shared.ResourceValue;

/**
 * CRUD methods, finders, etc. for Virtual
 */
public class VirtualDAO extends HibernateDAO {
    public VirtualDAO(Session session) {
        super(Virtual.class, session);
    }

    public void evict(Virtual entity) {
        super.evict(entity);
    }

    public Virtual merge(Virtual entity) {
        return (Virtual)super.merge(entity);
    }

    public void save(Virtual entity) {
        super.save(entity);
    }

    public void remove(Virtual entity) {
        super.remove(entity);
    }
    
    public void createVirtual(ResourceValue res, Integer processId) {
        Resource resource = new ResourceDAO(getSession()).findById(res.getId());
        Virtual virt = new Virtual();
        virt.setResource(resource);
        virt.setProcessId(processId);
    }
    
    public Virtual findByResource(Integer resourceId) {
        String sql = "from Virtual where resourceId = ?";
        return (Virtual)getSession()
            .createQuery(sql)
            .setInteger(0, resourceId.intValue())
            .uniqueResult();

    }

    Collection findVirtualByPysicalId(Integer id, String rtName) {
        String sql = "select v from Virtual v join fetch v.resource r " +
                "where r.resourceType.name = ? and v.physicalId = ?";
        return getSession().createQuery(sql)
                .setString(0, rtName)
                .setInteger(1, id.intValue())
                .list();
    }

    Collection findVirtualByProcessId(Integer id, String rtName) {
        String sql = "select v from Virtual v join v.resource r " +
                "where r.resourceType.name = ? and v.processId = ?";
        return getSession().createQuery(sql)
                .setString(0, rtName)
                .setInteger(1, id.intValue())
                .list();
    }

    Resource findVirtualByInstanceId(Integer id, String rtName) {
        String sql = "select v from Virtual v join v.resource r " +
                "where r.resourceType.name = ? and r.instanceId = ?";
        return (Resource) getSession().createQuery(sql)
                .setString(0, rtName)
                .setInteger(1, id.intValue())
                .uniqueResult();
    }
}
