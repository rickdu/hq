/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004-2008], Hyperic, Inc.
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

package org.hyperic.hq.ui.action.resource.server.inventory;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.bizapp.shared.AppdefBoss;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.action.resource.RemoveResourceForm;
import org.hyperic.hq.ui.util.RequestUtils;

/**
 * Action which deletes services from the services list within a server
 * 
 * 
 * 
 */
public class RemoveServiceAction
    extends BaseAction {

    private AppdefBoss appdefBoss;

    /**
     * Removes a server identified by the value of the request parameter
     * <code>Constants.SERVER_PARAM</code> from the BizApp.
     * @return
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        Log log = LogFactory.getLog(RemoveServiceAction.class.getName());

        RemoveResourceForm nwForm = (RemoveResourceForm) form;

        AppdefEntityID aeid = new AppdefEntityID(nwForm.getEid());
        Integer[] resources = nwForm.getResources();
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put(Constants.ENTITY_ID_PARAM, aeid);

        if (aeid.isPlatform()) {
            params.put(Constants.ACCORDION_PARAM, "3");
        } else {
            params.put(Constants.ACCORDION_PARAM, "1");
        }

        if (resources == null || resources.length == 0) {
            returnSuccess(request, mapping, params);
        }

        Integer sessionId = RequestUtils.getSessionId(request);

        log.trace("removing resource");

        for (int i = 0; i < resources.length; i++) {
            appdefBoss.removeAppdefEntity(sessionId.intValue(), AppdefEntityID.newServiceID(resources[i]), false);
        }

        return returnSuccess(request, mapping, params);
    }

}