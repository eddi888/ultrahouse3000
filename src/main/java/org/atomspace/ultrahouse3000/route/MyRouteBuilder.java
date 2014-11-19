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
package org.atomspace.ultrahouse3000.route;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.atomspace.ultrahouse3000.filter.FlipFlopFilter3000;
import org.atomspace.ultrahouse3000.translator.Message2DocumentTranslator;
import org.atomspace.ultrahouse3000.translator.Weather2DocumentTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyRouteBuilder extends RouteBuilder {
    
    @Autowired
    Weather2DocumentTranslator weather2DocumentTranslator;
    
    @Autowired
    Message2DocumentTranslator message2DocumentTranslator;
    
    @Autowired
    FlipFlopFilter3000 flipFlopFilter3000;
    
    
    @Override
    public void configure() throws Exception {
        
        // http://0.0.0.0:8080/camel/datetime
        from("servlet:///datetime")
            .id("route-datetime")
            .autoStartup("true")
            .process((Exchange exchange) ->  {
                exchange.getOut().setBody(new Date().getTime());
            });

        // GAS USAGE
        from("tinkerforge://192.168.3.28:4223/line?uid=mRC&period=1000&init=setReflectivityCallbackPeriod&callback=ReflectivityListener")
            .id("route-gas-flip-flop")
            .autoStartup("true")
            .filter().method(flipFlopFilter3000)
                .bean(message2DocumentTranslator)
                .setHeader("type").constant("line")
                .to("couchdb:http://192.168.3.28:5984/ultrahouse3000?deletes=false")
                .end();
         
    
        // WEATHER OUTSIDE
        from("weather://foo?location=Werder (Havel),DE&units=METRIC")
            .id("route-temperatur-outside")
            .autoStartup("true")
            .to("log:weather?showAll=true")
            .setHeader("type").constant("weather")
            .bean(weather2DocumentTranslator)
            .to("log:json?showAll=true")
            .to("couchdb:http://192.168.3.28:5984/ultrahouse3000?deletes=false");
    }
}
