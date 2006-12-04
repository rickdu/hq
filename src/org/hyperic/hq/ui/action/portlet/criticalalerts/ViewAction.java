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

package org.hyperic.hq.ui.action.portlet.criticalalerts;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.bizapp.shared.EventsBoss;
import org.hyperic.hq.bizapp.shared.uibeans.DashboardAlertBean;
import org.hyperic.hq.ui.Constants;
import org.hyperic.hq.ui.WebUser;
import org.hyperic.hq.ui.exception.ParameterNotFoundException;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.util.ContextUtils;
import org.hyperic.hq.ui.util.DashboardUtils;
import org.hyperic.hq.ui.util.RequestUtils;
import org.hyperic.util.pager.PageControl;
import org.hyperic.util.pager.PageList;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONObject;

/**
 * This action class is used by the Critical Alerts portlet.  It's main
 * use is to generate the JSON objects required for display into the UI.
 */
public class ViewAction extends BaseAction {

    static final String RESOURCES_KEY = ".dashContent.criticalalerts.resources";

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws Exception
    {
        ServletContext ctx = getServlet().getServletContext();
        EventsBoss eventBoss = ContextUtils.getEventsBoss(ctx);
        WebUser user = (WebUser) request.getSession().getAttribute(
            Constants.WEBUSER_SES_ATTR);
        
        String token;

        try {
            token = RequestUtils.getStringParameter(request, "token");
        } catch (ParameterNotFoundException e) {
            token = null;
        }

        // For multi-portlet configurations
        String resKey = ViewAction.RESOURCES_KEY;
        String countKey = PropertiesForm.ALERT_NUMBER;
        String priorityKey = PropertiesForm.PRIORITY;
        String timeKey = PropertiesForm.PAST;
        String selOrAllKey = PropertiesForm.SELECTED_OR_ALL;
        if (token != null) {
            resKey += token;
            countKey += token;
            priorityKey += token;
            timeKey += token;
            selOrAllKey += token;
        }

        List entityIds = DashboardUtils.preferencesAsEntityIds(resKey, user);
        AppdefEntityID[] arrayIds =
            (AppdefEntityID[])entityIds.toArray(new AppdefEntityID[0]);

        int count = Integer.parseInt(user.getPreference(countKey));
        int priority = Integer.parseInt(user.getPreference(priorityKey));
        long timeRange = Long.parseLong(user.getPreference(timeKey));
        boolean all = "all".equals(user.getPreference(selOrAllKey));

        int sessionID = user.getSessionId().intValue();
        PageControl pc = new PageControl();
        pc.setPagesize(10);

        if (all) {
            arrayIds = null;
        }

        PageList criticalAlerts =
            eventBoss.findAlerts(sessionID, count, priority, timeRange,
                                 arrayIds, pc);

        JSONObject alerts = new JSONObject();
        List a = new ArrayList();
        for (Iterator i = criticalAlerts.iterator(); i.hasNext(); ) {
            DashboardAlertBean bean = (DashboardAlertBean)i.next();
            DateFormat df = DateFormat.getDateTimeInstance();
            String date = df.format(new Date(bean.getCtime()));

            JSONObject alert = new JSONObject();
            alert.put("alertId", bean.getAlertId());
            alert.put("appdefKey",
                      bean.getResource().getEntityId().getAppdefKey());
            alert.put("resourceName", bean.getResource().getName());
            alert.put("alertDefName", bean.getAlertDefName());
            alert.put("cTime", date);

            a.add(alert);
        }

        alerts.put("criticalAlerts", a);

        response.getWriter().write(alerts.toString());

        return null;
    }
}
