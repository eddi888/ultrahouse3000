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

    @Value("${couchDbServer:localhost}")
    private String couchDbServer;

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
            .to("tinkerforge://pinkpi:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=1&switchTo2=1");
        
        // http://0.0.0.0:8080/camel/workroom/lightoff
        from("servlet:///workroom/lightoff")
            .id("route-workroom-lightoff")
            .autoStartup(autoStartup)
            .to("tinkerforge://pinkpi:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=1&switchTo2=0");
        
        // http://0.0.0.0:8080/camel/bedroom/ventilator-on
        from("servlet:///bedroom/ventilator-on")
            .id("route-bedroom-ventilator-on")
            .autoStartup(autoStartup)
            .to("tinkerforge://pinkpi:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=4&switchTo2=1");
        
        // http://0.0.0.0:8080/camel/bedroom/ventilator-off
        from("servlet:///bedroom/ventilator-off")
            .id("route-bedroom-ventilator-off")
            .autoStartup(autoStartup)
            .to("tinkerforge://pinkpi:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=4&switchTo2=0");
        
        // http://0.0.0.0:8080/camel/lobby/lighton
        from("servlet:///lobby/lighton")
            .id("route-string-of-lights-on")
            .autoStartup(autoStartup)
            .to("tinkerforge://pinkpi:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=8&switchTo2=1");
        
        // http://0.0.0.0:8080/camel/lobby/lightoff
        from("servlet:///lobby/lightoff")
            .id("route-string-of-lights-off")
            .autoStartup(autoStartup)
            .to("tinkerforge://pinkpi:4223/RemoteSwitch?uid=oiZ&function=switchSocketA&houseCode2="+houseCode+"&receiverCode2=8&switchTo2=0");
        
        // http://0.0.0.0:8080/camel/corridor/swtich
        from("servlet:///corridor/swtich")
            .id("route-corridor-switch")
            .autoStartup(autoStartup)
            .to("tinkerforge://greypi:4223/IndustrialQuadRelay?uid=rp9&function=setMonoflop&selectionMask="+0b1000+"&valueMask2="+0b0000+"&time=100");

        // http://0.0.0.0:8080/camel/kitchen/domelight/swtich
        from("servlet:///kitchen/domelight/swtich")
            .id("route-kitchen-domelight-switch")
            .autoStartup(autoStartup)
            .to("tinkerforge://greypi:4223/IndustrialQuadRelay?uid=rp9&function=setMonoflop&selectionMask="+0b0100+"&valueMask2="+0b0000+"&time=100");

        // http://0.0.0.0:8080/camel/kitchen/cabinetlight/swtich
        from("servlet:///kitchen/cabinetlight/swtich")
            .id("route-kitchen-cabinetlight-switch")
            .autoStartup(autoStartup)
            .to("tinkerforge://greypi:4223/IndustrialQuadRelay?uid=rp9&function=setMonoflop&selectionMask="+0b0010+"&valueMask2="+0b0000+"&time=100");

        // http://0.0.0.0:8080/camel/kitchen/auxiliarylight/swtich
        from("servlet:///kitchen/auxiliarylight/swtich")
            .id("route-kitchen-auxiliarylight-switch")
            .autoStartup(autoStartup)
            .to("tinkerforge://greypi:4223/IndustrialQuadRelay?uid=rp9&function=setMonoflop&selectionMask="+0b0001+"&valueMask2="+0b0000+"&time=100");

        // http://0.0.0.0:8080/camel/lounge/domelight/swtich
        from("servlet:///lounge/domelight/switch")
            .id("route-lounge-domelight-switch")
            .autoStartup(autoStartup)
            .to("tinkerforge://greypi:4223/IndustrialQuadRelay?uid=rpM&function=setMonoflop&selectionMask="+0b1000+"&valueMask2="+0b0000+"&time=100");

        // http://0.0.0.0:8080/camel/lounge/uplight/swtich
        from("servlet:///lounge/uplight/switch")
            .id("route-lounge-uplight-switch")
            .autoStartup(autoStartup)
            .to("tinkerforge://greypi:4223/IndustrialQuadRelay?uid=rpM&function=setMonoflop&selectionMask="+0b0100+"&valueMask2="+0b0000+"&time=100");
        
        // http://0.0.0.0:8080/camel/bedroom/domelight/swtich
        from("servlet:///bedroom/domelight/switch")
            .id("route-bedroom-domelight-switch")
            .autoStartup(autoStartup)
            .to("tinkerforge://greypi:4223/IndustrialQuadRelay?uid=rpM&function=setMonoflop&selectionMask="+0b0010+"&valueMask2="+0b0000+"&time=100");

        // http://0.0.0.0:8080/camel/bedroom/uplight/swtich
        from("servlet:///bedroom/uplight/switch")
            .id("route-bedroom-uplight-switch")
            .autoStartup(autoStartup)
            .to("tinkerforge://greypi:4223/IndustrialQuadRelay?uid=rpM&function=setMonoflop&selectionMask="+0b0001+"&valueMask2="+0b0000+"&time=100");
        
        // DOORBELL
        from("tinkerforge://greypi:4223/IO4?uid=h4K&callback=interrupt")
            .id("route-doorbell")
            .autoStartup(autoStartup)
            .filter().method(flipFlopFilter3000)
                .setHeader("type").constant("line")    
                .bean(message2DocumentTranslator)
                .to("tinkerforge://pinkpi:4223/DualRelay?uid=kPu&function=setMonoflop&relay=1&state=true&time=500")
                .end();
        
        
        
        // GAS USAGE
        from("tinkerforge://pinkpi:4223/line?uid=mRC&period=1000&init=setReflectivityCallbackPeriod&callback=ReflectivityListener")
            .id("route-gas-flip-flop")
            .autoStartup(autoStartup)
            .filter().method(flipFlopFilter3000)
                .setHeader("type").constant("line")    
                .bean(message2DocumentTranslator)
                .to("couchdb:http://"+couchDbServer+":5984/ultrahouse3000?deletes=false")
                .to("tinkerforge://pinkpi:4223/DualRelay?uid=kPu&function=setMonoflop&relay=2&state=true&time=500")
                .end();
         
        
        // WEATHER OUTSIDE
        from("weather://foo?location=Glindow,DE&units=METRIC&consumer.delay=3600000")
            .id("route-temperatur-outside")
            .autoStartup(autoStartup)
            .to("log:weather?showAll=true")
            .setHeader("type").constant("weather")
            .bean(weather2DocumentTranslator)
            .to("couchdb:http://"+couchDbServer+":5984/ultrahouse3000?deletes=false");
        
        
        // TEMPERATUR INSIDE
//        from("tinkerforge://192.168.3.21:4223/Temperature?uid=qao&init=setTemperatureCallbackPeriod&period=5000&callback=TemperatureListener")
//            .id("route-temperatur-inside")
//            .autoStartup(autoStartup)
//            .setHeader("type").constant("temperature")
//            .bean(message2DocumentTranslator)
//            .to("couchdb:http://"+couchServer+":5984/ultrahouse3000?deletes=false");
        
        
        // AMBIENT LIGHT INSIDE
//        from("tinkerforge://192.168.3.21:4223/AmbientLight?uid=map&init=setIlluminanceCallbackPeriod&period=5000&callback=IlluminanceListener")
//            .id("route-ambientlight-inside")
//            .autoStartup(autoStartup)
//            .setHeader("type").constant("ambientlight")
//            .bean(message2DocumentTranslator)
//            .to("couchdb:http://"+couchServer+":5984/ultrahouse3000?deletes=false");
        
        
        // MOTION DETECTOR INSIDE
        from("tinkerforge://greypi:4223/MotionDetector?uid=oTu&callback=MotionDetectedListener")
            .id("route-motiondetector-inside")
            .autoStartup(autoStartup)
            .setHeader("type").constant("motiondetector")
            .bean(message2DocumentTranslator)
            .to("couchdb:http://"+couchDbServer+":5984/ultrahouse3000?deletes=false")
            .to("tinkerforge://pinkpi:4223/PiezoSpeaker?uid=mKu&function=morseCode&morse=.... .- .-.. .-.. ---&frequency2=600");
        
        
        
    }
}
