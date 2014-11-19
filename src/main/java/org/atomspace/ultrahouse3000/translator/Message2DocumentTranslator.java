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

import java.util.Calendar;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

/**
 * Translate Camel Incomming Message to CouchDB Document of Type JSON 
 *
 */
@Component
public class Message2DocumentTranslator implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        addCalenderInformation(exchange.getIn());
        
        JsonObject doc = new JsonObject();
        Set<Entry<String, Object>> entrys = exchange.getIn().getHeaders().entrySet();
        for (Entry<String, Object> entry : entrys) {
            doc.addProperty(entry.getKey(), entry.getValue().toString());
        }
        doc.addProperty("body", exchange.getIn().getBody(String.class));
        exchange.getOut().setBody(doc);
    }
    
    void addCalenderInformation(Message message ){
        Calendar cal = Calendar.getInstance();
        message.setHeader("timestamp", cal.getTimeInMillis());
        message.setHeader("calendarYear", cal.get(Calendar.YEAR));
        message.setHeader("calendarDayOfMonth",     cal.get(Calendar.DAY_OF_MONTH));
        message.setHeader("calendarMonth", cal.get(Calendar.MONTH));
        message.setHeader("calendarDayOfYear", cal.get(Calendar.DAY_OF_YEAR));
        message.setHeader("calendarHourOfDay", cal.get(Calendar.HOUR_OF_DAY));
        message.setHeader("calendarMinute", cal.get(Calendar.MINUTE));
        message.setHeader("calendarSecond", cal.get(Calendar.SECOND));
        message.setHeader("calendarWeekOfYear", cal.get(Calendar.WEEK_OF_YEAR));
        message.setHeader("calendarDayOfWeek", cal.get(Calendar.DAY_OF_WEEK));
        
        
    }
    
}
