<%@ page language="java" %>
<%@ page errorPage="/common/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/hq.tld" prefix="hq" %>
<%@ taglib uri="/WEB-INF/tld/display.tld" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/jsUtils" prefix="jsu" %>
<%--
  NOTE: This copyright does *not* cover user programs that use HQ
  program services by normal system calls through the application
  program interfaces provided as part of the Hyperic Plug-in Development
  Kit or the Hyperic Client Development Kit - this is merely considered
  normal use of the program, and does *not* fall under the heading of
  "derived work".
  
  Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
  This file is part of HQ.
  
  HQ is free software; you can redistribute it and/or modify
  it under the terms version 2 of the GNU General Public License as
  published by the Free Software Foundation. This program is distributed
  in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  PARTICULAR PURPOSE. See the GNU General Public License for more
  details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
  USA.
 --%>


<!-- NOT SURE
<tiles:importAttribute name="resource" ignore="true"/>
-->

<hq:constant
    classname="org.hyperic.hq.appdef.shared.AppdefEntityConstants" 
    symbol="APPDEF_TYPE_PLATFORM" var="CONST_PLATFORM" />
<hq:constant
    classname="org.hyperic.hq.appdef.shared.AppdefEntityConstants" 
    symbol="APPDEF_TYPE_SERVER" var="CONST_SERVER" />
<hq:constant
    classname="org.hyperic.hq.appdef.shared.AppdefEntityConstants" 
    symbol="APPDEF_TYPE_SERVICE" var="CONST_SERVICE" />
<hq:constant
    classname="org.hyperic.hq.appdef.shared.AppdefEntityConstants" 
    symbol="APPDEF_TYPE_APPLICATION" var="CONST_APPLICATION" />
<hq:constant
    classname="org.hyperic.hq.appdef.shared.AppdefEntityConstants" 
    symbol="APPDEF_TYPE_GROUP" var="CONST_GROUP" />

<hq:constant
    classname="org.hyperic.hq.ui.Constants" 
    symbol="CONTROL_ENABLED_ATTR" var="CONST_CONTROLLABLE" /> 

<c:set var="canControl" value="${requestScope[CONST_CONTROLLABLE]}"/>
<jsu:importScript path="/js/listWidget.js" />
<c:set var="widgetInstanceName" value="listAlertDefinitions"/>
<jsu:script>
	function setActiveInactive() {
	    document.RemoveConfigForm.setActiveInactive.value='y';
	    document.RemoveConfigForm.submit();
	}
	
	var pageData = new Array();
	initializeWidgetProperties('<c:out value="${widgetInstanceName}"/>');
	widgetProperties = getWidgetProperties('<c:out value="${widgetInstanceName}"/>');
</jsu:script>
<c:set var="entityId" value="${Resource.entityId}"/>
<c:url var="pnAction" value="/alerts/Config.do">
  <c:param name="mode" value="list"/>
  <c:param name="rid" value="${Resource.id}"/>
  <c:param name="type" value="${entityId.type}"/>
  <c:if test="${not empty param.ps}">
    <c:param name="ps" value="${param.ps}"/>
  </c:if>
  <c:if test="${not empty param.so}">
    <c:param name="so" value="${param.so}"/>
  </c:if>
  <c:if test="${not empty param.sc}">
    <c:param name="sc" value="${param.sc}"/>
  </c:if>
</c:url>
<c:url var="psAction" value="/alerts/Config.do">
  <c:param name="mode" value="list"/>
  <c:param name="rid" value="${Resource.id}"/>
  <c:param name="type" value="${entityId.type}"/>
  <c:if test="${not empty param.ps}">
    <c:param name="pn" value="${param.pn}"/>
  </c:if>
  <c:if test="${not empty param.so}">
    <c:param name="so" value="${param.so}"/>
  </c:if>
  <c:if test="${not empty param.sc}">
    <c:param name="sc" value="${param.sc}"/>
  </c:if>
</c:url>
<c:url var="sortAction" value="/alerts/Config.do">
  <c:param name="mode" value="list"/>
  <c:param name="rid" value="${Resource.id}"/>
  <c:param name="type" value="${entityId.type}"/>
  <c:if test="${not empty param.pn}">
    <c:param name="pn" value="${param.pn}"/>
  </c:if>
  <c:if test="${not empty param.ps}">
    <c:param name="ps" value="${param.ps}"/>
  </c:if>
</c:url>
<c:url var="newAction" value="/alerts/Config.do">
	<c:param name="mode" value="new"/>
	<c:param name="eid" value="${entityId.appdefKey}"/>
</c:url>

<c:set var="entityId" value="${Resource.entityId}"/>


<c:if test="${ CONST_PLATFORM == entityId.type}">
	<tiles:insert  definition=".page.title.events.list.platform">
	    <tiles:put name="resource" beanName="Resource"/>
	    <tiles:put name="resourceOwner" beanName="ResourceOwner"/>
	    <tiles:put name="resourceModifier" beanName="ResourceModifier"/>
	    <tiles:put name="eid" beanName="entityId" beanProperty="appdefKey" />
	</tiles:insert>
    <c:choose>
        <c:when test="${ canControl }">
			<tiles:insert definition =".tabs.resource.platform.alert.configAlerts">
			    <tiles:put name="resourceId" beanName="Resource" beanProperty="id"/>
			    <tiles:put name="resourceType" beanName="entityId" beanProperty="type"/>
			</tiles:insert>
        </c:when>
        <c:otherwise>
            <tiles:insert definition =".tabs.resource.platform.alert.configAlerts.nocontrol">
                    <tiles:put name="resourceId" beanName="Resource" beanProperty="id"/>
                    <tiles:put name="resourceType" beanName="entityId" beanProperty="type"/>
            </tiles:insert>
        </c:otherwise>
    </c:choose>
</c:if>
<c:if test="${ CONST_SERVER == entityId.type}">
	<tiles:insert  definition=".page.title.events.list.server">
	    <tiles:put name="resource" beanName="Resource"/>
	    <tiles:put name="resourceOwner" beanName="ResourceOwner"/>
	    <tiles:put name="resourceModifier" beanName="ResourceModifier"/>
	    <tiles:put name="eid" beanName="entityId" beanProperty="appdefKey" />
	</tiles:insert>
	
	<c:choose>
	 <c:when test="${ canControl }">
	  <tiles:insert definition=".tabs.resource.server.alert.configAlerts">
	   <tiles:put name="resourceId" beanName="Resource" beanProperty="id"/>
	   <tiles:put name="resourceType" beanName="entityId" beanProperty="type"/>
	  </tiles:insert>
	 </c:when>
	 <c:otherwise>
	  <tiles:insert definition=".tabs.resource.server.alert.configAlerts.nocontrol">
	   <tiles:put name="resourceId" beanName="Resource" beanProperty="id"/>
	   <tiles:put name="resourceType" beanName="entityId" beanProperty="type"/>
	  </tiles:insert>
	 </c:otherwise>
	</c:choose>
</c:if>
<c:if test="${ CONST_SERVICE == entityId.type}">
<tiles:insert  definition=".page.title.events.list.service">
    <tiles:put name="resource" beanName="Resource"/>
    <tiles:put name="resourceOwner" beanName="ResourceOwner"/>
    <tiles:put name="resourceModifier" beanName="ResourceModifier"/>
    <tiles:put name="eid" beanName="entityId" beanProperty="appdefKey" />
</tiles:insert>

<c:choose>
 <c:when test="${ canControl }">
  <tiles:insert definition=".tabs.resource.service.alert.configAlerts">
   <tiles:put name="resourceId" beanName="Resource" beanProperty="id"/>
   <tiles:put name="resourceType" beanName="entityId" beanProperty="type"/>
  </tiles:insert>
 </c:when>
 <c:otherwise>
  <tiles:insert definition=".tabs.resource.service.alert.configAlerts.nocontrol">
   <tiles:put name="resourceId" beanName="Resource" beanProperty="id"/>
   <tiles:put name="resourceType" beanName="entityId" beanProperty="type"/>
  </tiles:insert>
 </c:otherwise>
</c:choose>

</c:if>
<c:if test="${ CONST_APPLICATION == entityId.type}">
<tiles:insert  definition=".page.title.events.list.application">
    <tiles:put name="titleName" beanName="Resource" beanProperty="name"/>
    <tiles:put name="resource" beanName="Resource"/>
    <tiles:put name="resourceOwner" beanName="ResourceOwner"/>
    <tiles:put name="resourceModifier" beanName="ResourceModifier"/>
</tiles:insert>
<tiles:insert definition =".tabs.resource.application.monitor.configAlerts">
    <tiles:put name="resourceId" beanName="Resource" beanProperty="id"/>
    <tiles:put name="resourceType" beanName="entityId" beanProperty="type"/>
</tiles:insert>
</c:if>
<c:if test="${ CONST_GROUP == entityId.type}">
    <tiles:insert  definition=".page.title.events.list.group">
        <tiles:put name="titleName" beanName="Resource" beanProperty="name"/>
        <tiles:put name="resource" beanName="Resource"/>
        <tiles:put name="resourceOwner" beanName="ResourceOwner"/>
        <tiles:put name="resourceModifier" beanName="ResourceModifier"/>
    </tiles:insert>
    <c:choose>
        <c:when test="${ canControl }">
            <tiles:insert definition =".tabs.resource.group.alert.configAlerts">
                <tiles:put name="resourceId" beanName="Resource" beanProperty="id"/>
                <tiles:put name="resourceType" beanName="entityId" beanProperty="type"/>
            </tiles:insert>
        </c:when>
        <c:otherwise>
            <tiles:insert definition =".tabs.resource.group.alert.configAlerts.nocontrol">
                <tiles:put name="resourceId" beanName="Resource" beanProperty="id"/>
                <tiles:put name="resourceType" beanName="entityId" beanProperty="type"/>
            </tiles:insert>
        </c:otherwise>
    </c:choose>
</c:if>

<!-- FORM -->
<html:form action="/alerts/RemoveConfig">
<html:hidden property="rid" value="${Resource.id}"/>
<html:hidden property="type" value="${Resource.entityId.type}"/>

<tiles:insert definition=".portlet.confirm"/>
<display:table cellspacing="0" cellpadding="0" width="100%"
               action="${sortAction}" items="${Definitions}" var="def" >
  <display:column width="1%" property="id" 
                  title="<input type=\"checkbox\" onclick=\"ToggleAll(this, widgetProperties)\" name=\"listToggleAll\">"  
                   isLocalizedTitle="false" styleClass="ListCellCheckbox" headerStyleClass="ListHeaderCheckbox" >
    <display:checkboxdecorator name="definitions" onclick="ToggleSelection(this,widgetProperties)" styleClass="listMember"/>
  </display:column>
  <display:column width="1%" property="parentId"
                  title="nbsp" styleClass="redTableCell">
    <display:equalsdecorator flagKey="alerts.config.service.DefinitionList.isResourceAlert" value="null"/>
  </display:column>

  <display:column width="18%" property="name" sort="true" sortAttr="1"
                  defaultSort="true" title="alerts.config.DefinitionList.ListHeader.AlertDefinition" href="/alerts/Config.do?mode=viewDefinition&eid=${Resource.entityId.appdefKey}" paramId="ad" paramProperty="id"/>
    
  <display:column width="20%" property="description"
                  title="common.header.Description" />

  <display:column width="15%" property="ctime" sort="true" sortAttr="2"
                  defaultSort="false" title="alerts.config.DefinitionList.ListHeader.DateCreated" >
    <display:datedecorator/>
  </display:column>

   <display:column width="15%" property="mtime" sort="true" sortAttr="2"
                  defaultSort="false" title="resource.common.monitor.visibility.metricmetadata.collection.lastModified" >
    <display:datedecorator/>
  </display:column>
                  
  <display:column width="10%" property="active" title="alerts.config.DefinitionList.ListHeader.Active">
	
	<display:alertdefstatedecorator active="${def.active}" disabled="${def.active && !def.enabled}" />

  </display:column>

</display:table>

<tiles:insert definition=".toolbar.list">
<!-- only show new alert def link if user can see it -->
<hq:userResourcePermissions debug="false" resource="${Resource}"/>
<c:choose>
       <c:when test="${canModify}" >
 	 		<tiles:put name="listNewUrl" beanName="newAction"/>
 	 		<tiles:put name="goButtonLink" value="javascript:setActiveInactive()"/>
 	 	</c:when>
 	 	<c:otherwise>
 	 		<tiles:put name="noButtons" value="true"/>
 	 	</c:otherwise>
</c:choose>
  <tiles:put name="listItems" beanName="Definitions"/>
  <tiles:put name="listSize" beanName="Definitions" beanProperty="totalSize"/>
  <tiles:put name="pageNumAction" beanName="pnAction"/>
  <tiles:put name="pageSizeAction" beanName="psAction"/>
  <tiles:put name="defaultSortColumn" value="1"/>
  <tiles:put name="widgetInstanceName" beanName="widgetInstanceName"/>
</tiles:insert>

<br>
<html:hidden property="setActiveInactive"/>
</html:form>

<!-- /  -->
