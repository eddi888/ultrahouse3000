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
package org.atomspace.ultrahouse3000.translator;

import java.math.BigDecimal;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Translate Weather Incomming Message to CouchDB Document of Type JSON 
 *
 */
@Component
public class Weather2DocumentTranslator extends Message2DocumentTranslator implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        
        JsonObject weather = (JsonObject) new JsonParser().parse(exchange.getIn().getBody(String.class));
        BigDecimal temperatur = weather.get("main").getAsJsonObject().get("temp").getAsBigDecimal();
        temperatur = temperatur.subtract(new BigDecimal("272.15"));
        exchange.getIn().setHeader("temperatur", temperatur);
        
        super.process(exchange);
       
    }
    
}
