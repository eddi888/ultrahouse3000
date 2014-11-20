/**
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements. See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.atomspace.ultrahouse3000;

import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelContext;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.jolokia.http.AgentServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

    @Autowired
    private CamelContext camelContext;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean camelServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/camel/*");
        registration.setName("CamelServlet");
        return registration;
    }
    
    @Bean
    public ServletRegistrationBean jolokiaServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new AgentServlet(), "/jolokia/*");
        registration.setName("JolokiaServlet");
        return registration;
    }

//    NOT WORKING JET, its SNAPSHOT
//    @Bean
//    public CamelContextConfiguration contextConfiguration() {
//        return (CamelContext camelContext) -> { 
//            ((DefaultCamelContext)camelContext).setName("camel-ultrahouse3000");
//        };
//    }

    
    @Bean
    public BrokerService broker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600");
        broker.addConnector("mqtt://localhost:1883?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600");
        broker.setPersistent(false);
        broker.setBrokerName("ultrahouse3000-broker");

        // broker.addNetworkConnector("multicast://tcp:");

        return broker;
    }
    
}
