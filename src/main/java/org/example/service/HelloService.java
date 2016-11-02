/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.service;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.ServiceInfo;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.xml.namespace.QName;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * This is the Microservice resource class.
 * See <a href="https://github.com/wso2/msf4j#getting-started">https://github.com/wso2/msf4j#getting-started</a>
 * for the usage of annotations.
 *
 * @since 0.1-SNAPSHOT
 */
@Path("/")
public class HelloService {

    private static final QName SERVICE_NAME
            = new QName("http://Company.com/Application",
            "Company_ESB_Application_Biztalk_AgentDetails_4405_AgentDetails_Prt");

    @GET
    @Path("/")
    public String get() throws Exception {


        URL wsdlURL;
        File wsdlFile = new File("http://localhost:9000/Complex?wsdl");
        if (wsdlFile.exists()) {
            wsdlURL = wsdlFile.toURI().toURL();
        } else {
            wsdlURL = new URL("http://localhost:9000/Complex?wsdl");
        }

        System.out.println(wsdlURL);

        JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
        Client client = factory.createClient(wsdlURL.toExternalForm(), SERVICE_NAME);
        ClientImpl clientImpl = (ClientImpl) client;
        Endpoint endpoint = clientImpl.getEndpoint();
        ServiceInfo serviceInfo = endpoint.getService().getServiceInfos().get(0);
        QName bindingName = new QName("http://Company.com/Application",
                "Company_ESB_Application_Biztalk_AgentDetails_4405_AgentDetails_PrtSoap");
        BindingInfo binding = serviceInfo.getBinding(bindingName);
        //{
        QName opName = new QName("http://Company.com/Application", "GetAgentDetails");
        BindingOperationInfo boi = binding.getOperation(opName);
        BindingMessageInfo inputMessageInfo = boi.getInput();
        List<MessagePartInfo> parts = inputMessageInfo.getMessageParts();
        // only one part.
        MessagePartInfo partInfo = parts.get(0);
        Class<?> partClass = partInfo.getTypeClass();
        System.out.println(partClass.getCanonicalName()); // GetAgentDetails
        Object inputObject = partClass.newInstance();
        // Unfortunately, the slot inside of the part object is also called 'part'.
        // this is the descriptor for get/set part inside the GetAgentDetails class.
        PropertyDescriptor partPropertyDescriptor = new PropertyDescriptor("part", partClass);
        // This is the type of the class which really contains all the parameter information.
        Class<?> partPropType = partPropertyDescriptor.getPropertyType(); // AgentWSRequest
        System.out.println(partPropType.getCanonicalName());
        Object inputPartObject = partPropType.newInstance();
        partPropertyDescriptor.getWriteMethod().invoke(inputObject, inputPartObject);
        PropertyDescriptor numberPropertyDescriptor = new PropertyDescriptor("agentNumber", partPropType);
        numberPropertyDescriptor.getWriteMethod().invoke(inputPartObject, new Integer(314159));

        Object[] result = client.invoke(opName, inputObject);
        Class<?> resultClass = result[0].getClass();
        System.out.println(resultClass.getCanonicalName()); // GetAgentDetailsResponse
        PropertyDescriptor resultDescriptor = new PropertyDescriptor("agentWSResponse", resultClass);
        Object wsResponse = resultDescriptor.getReadMethod().invoke(result[0]);
        Class<?> wsResponseClass = wsResponse.getClass();
        System.out.println(wsResponseClass.getCanonicalName());
        PropertyDescriptor agentNameDescriptor = new PropertyDescriptor("agentName", wsResponseClass);
        String agentName = (String) agentNameDescriptor.getReadMethod().invoke(wsResponse);
        System.out.println("Agent name: " + agentName);


        return "Agent name: " + agentName;
    }
}
