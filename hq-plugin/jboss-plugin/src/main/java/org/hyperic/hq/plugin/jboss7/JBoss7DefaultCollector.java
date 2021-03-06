/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004-2011], Hyperic, Inc.
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
package org.hyperic.hq.plugin.jboss7;

import java.util.Properties;
import org.apache.commons.logging.Log;
import org.hyperic.hq.product.Collector;
import org.hyperic.hq.product.PluginException;

public abstract class JBoss7DefaultCollector extends Collector {

    private JBossAdminHttp admin;

    @Override
    protected final void init() throws PluginException {
        super.init();
        Properties props = getProperties();
        if (getLog().isDebugEnabled()) {
            getLog().debug("[init] props=" + props);
        }

        admin = new JBossAdminHttp(props);
        admin.testConnection();
    }

    public final void collect() {
        try {
            if (admin == null) {
                admin = new JBossAdminHttp(getProperties());
            }
            if (admin != null) {
                collect(admin);
            }
        } catch (Throwable ex) {
            setAvailability(false);
            getLog().debug(ex.getMessage(), ex);
            admin = null;
        }

        boolean https = "true".equals(getProperties().getProperty(JBossStandaloneDetector.HTTPS));
        if (https) {
            admin = null;
        }
    }

    public abstract void collect(JBossAdminHttp admin);

    public abstract Log getLog();
}
