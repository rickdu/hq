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

package org.hyperic.hq.plugin.vim;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.measurement.MeasurementConstants;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.collection.IntHashMap;

import com.vmware.vim.ManagedObjectReference;
import com.vmware.vim.PerfCounterInfo;
import com.vmware.vim.PerfEntityMetric;
import com.vmware.vim.PerfEntityMetricBase;
import com.vmware.vim.PerfMetricId;
import com.vmware.vim.PerfMetricIntSeries;
import com.vmware.vim.PerfMetricSeries;
import com.vmware.vim.PerfQuerySpec;

public class VimHostCollector extends VimCollector {

    static final String TYPE = "HostSystem";

    private static final Log _log =
        LogFactory.getLog(VimHostCollector.class.getName());

    private static final Set VALID_UNITS =
        new HashSet(Arrays.asList(MeasurementConstants.VALID_UNITS));

    private static final HashMap UNITS_MAP = new HashMap();
    private static final String[][] UNITS_ALIAS = {
        { "Second", MeasurementConstants.UNITS_SECONDS },
        { "Millisecond", MeasurementConstants.UNITS_MILLIS },
        { "Percent", MeasurementConstants.UNITS_PERCENT }
    };
    
    static {
        for (int i=0; i<UNITS_ALIAS.length; i++) {
            UNITS_MAP.put(UNITS_ALIAS[i][0], UNITS_ALIAS[i][1]);
        }
    }

    protected ManagedObjectReference getRoot() {
        return null;
    }

    protected String getType() {
        return TYPE;
    }

    protected String getName() {
        return getHostname();
    }

    protected void printXml(PerfCounterInfo info, String key) {
        String rollup = info.getRollupType().getValue();
        String name = info.getNameInfo().getLabel();
        String units = info.getUnitInfo().getLabel();

        if (rollup.equals("minimum") || rollup.equals("maximum")) {
            return;
        }
        if (name.indexOf("peak") != -1) {
            return;
        }
        if (UNITS_MAP.containsKey(units)) {
            units = (String)UNITS_MAP.get(units);
        }

        if (!VALID_UNITS.contains(units)) {
            System.out.println("    <!-- units=" + units + " -->");
            units = MeasurementConstants.UNITS_NONE;
        }
        System.out.println("    <metric name=\"" + name + "\"");
        System.out.println("            alias=\"" + key + "\"");
        System.out.println("            units=\"" + units + "\"/>\n");
    }

    protected void init() throws PluginException {
        super.init();
        VimUtil vim = new VimUtil();

        try {
            vim.init(getProperties());
            //validate config
            getManagedObjectReference(vim);
        } catch (Exception e) {
            throw new PluginException(e.getMessage(), e);
        } finally {
            vim.dispose();
        }
    }

    protected void collect(VimUtil vim)
        throws Exception {

        final boolean printMetric =
            "true".equals(System.getProperty("vim.xml"));
        ManagedObjectReference mor = getManagedObjectReference(vim);
        ManagedObjectReference perfManager = vim.getPerfManager();
        IntHashMap counters = getCounterInfo(vim, perfManager);
        PerfMetricId[] ids = getPerfMetricIds(vim, perfManager, mor);

        PerfQuerySpec spec = new PerfQuerySpec();
        spec.setEntity(mor);
        spec.setMetricId(ids);
        spec.setMaxSample(new Integer(1));
        spec.setIntervalId(new Integer(20));
       
        PerfQuerySpec[] query = new PerfQuerySpec[] { spec };      
        PerfEntityMetricBase[] values =
            vim.getConn().getService().queryPerf(perfManager, query);
        if (values == null) {
            _log.error("No performance metrics available for: " +
                       getName() + " " + getType());
            return;
        }
        PerfEntityMetric metric = (PerfEntityMetric)values[0];
        PerfMetricSeries[] vals = metric.getValue();

        for (int i=0; i<vals.length; i++) {
            PerfCounterInfo info =
                (PerfCounterInfo)counters.get(vals[i].getId().getCounterId());

            if (info == null) {
                continue;
            }

            if (!(vals[i] instanceof PerfMetricIntSeries)) {
                continue;
            }
            PerfMetricIntSeries series = (PerfMetricIntSeries)vals[i];
            String key = getCounterKey(info);
            String instance = series.getId().getInstance();
            if (instance.length() != 0) {
                continue;
            }
            long val = series.getValue()[0];
            if (info.getStatsType().getValue().equals("absolute")) {
                setValue(key, val);
                if (printMetric) printXml(info, key);
            }
        }
    }
}
