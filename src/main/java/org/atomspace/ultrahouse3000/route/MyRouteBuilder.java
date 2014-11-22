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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyRouteBuilder extends RouteBuilder {
    
    @Value("${autoStartup:false}")
    private boolean autoStartup;
    
    @Value("${tinkerforgeRemoteHouseCode:0}")
    private int houseCode;
    
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
            .autoStartup(autoStartup)
            .process((Exchange exchange) ->  {
                exchange.getOut().setBody(new Date().getTime());
            });

        // http://0.0.0.0:8080/camel/workroom/lighton
        from("servlet:///workroom/lighton")
            .id("route-workroom-lighton")
            .autoStartup(autoStartup)
            .to("tinkerforge://192.168.3.28:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=1&switchTo2=1");

        // http://0.0.0.0:8080/camel/workroom/lightoff
        from("servlet:///workroom/lightoff")
            .id("route-workroom-lightoff")
            .autoStartup(autoStartup)
            .to("tinkerforge://192.168.3.28:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=1&switchTo2=0");

        // http://0.0.0.0:8080/camel/bedroom/lighton
        from("servlet:///bedroom/lighton")
            .id("route-bedroom-lighton")
            .autoStartup(autoStartup)
            .to("tinkerforge://192.168.3.28:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=4&switchTo2=1");

        // http://0.0.0.0:8080/camel/bedroom/lighton
        from("servlet:///bedroom/lightoff")
            .id("route-bedroom-lightoff")
            .autoStartup(autoStartup)
            .to("tinkerforge://192.168.3.28:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=4&switchTo2=0");
        
        // http://0.0.0.0:8080/camel/corridor/lighton
        from("servlet:///corridor/lighton")
            .id("route-corridor-lighton")
            .autoStartup(autoStartup)
            .to("tinkerforge://192.168.3.28:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=8&switchTo2=1");

        // http://0.0.0.0:8080/camel/corridor/lighton
        from("servlet:///corridor/lightoff")
            .id("route-corridor-lightoff")
            .autoStartup(autoStartup)
            .to("tinkerforge://192.168.3.28:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=8&switchTo2=0");
        
        
        // GAS USAGE
        from("tinkerforge://192.168.3.28:4223/line?uid=mRC&period=1000&init=setReflectivityCallbackPeriod&callback=ReflectivityListener")
            .id("route-gas-flip-flop")
            .autoStartup(autoStartup)
            .filter().method(flipFlopFilter3000)
                .setHeader("type").constant("line")    
                .bean(message2DocumentTranslator)
                .to("couchdb:http://192.168.3.28:5984/ultrahouse3000?deletes=false")
                .to("tinkerforge://192.168.3.28:4223/DualRelay?uid=kPu&function=setMonoflop&relay=2&state=true&time=500")
                .end();
         
    
        // WEATHER OUTSIDE
        from("weather://foo?location=Werder (Havel),DE&units=METRIC")
            .id("route-temperatur-outside")
            .autoStartup(autoStartup)
            .to("log:weather?showAll=true")
            .setHeader("type").constant("weather")
            .bean(weather2DocumentTranslator)
            .to("couchdb:http://192.168.3.28:5984/ultrahouse3000?deletes=false");
        
        
        // TEMPERATUR INSIDE
        from("tinkerforge://192.168.3.21:4223/Temperature?uid=qao&init=setTemperatureCallbackPeriod&period=5000&callback=TemperatureListener")
            .id("route-temperatur-inside")
            .autoStartup(autoStartup)
            .setHeader("type").constant("temperature")
            .bean(message2DocumentTranslator)
            .to("couchdb:http://192.168.3.28:5984/ultrahouse3000?deletes=false");
        
        
        // AMBIENT LIGHT INSIDE
        from("tinkerforge://192.168.3.21:4223/AmbientLight?uid=map&init=setIlluminanceCallbackPeriod&period=5000&callback=IlluminanceListener")
            .id("route-ambientlight-inside")
            .autoStartup(autoStartup)
            .setHeader("type").constant("ambientlight")
            .bean(message2DocumentTranslator)
            .to("couchdb:http://192.168.3.28:5984/ultrahouse3000?deletes=false");
        
        
        // MOTION DETECTOR INSIDE
        from("tinkerforge://192.168.3.21:4223/MotionDetector?uid=oTu&callback=MotionDetectedListener")
            .id("route-motiondetector-inside")
            .autoStartup(autoStartup)
            .setHeader("type").constant("motiondetector")
            .bean(message2DocumentTranslator)
            .to("couchdb:http://192.168.3.28:5984/ultrahouse3000?deletes=false")
            .to("tinkerforge://192.168.3.21:4223/PiezoSpeaker?uid=mKu&function=morseCode&morse=.... .- .-.. .-.. ---&frequency2=600");
    }
}
