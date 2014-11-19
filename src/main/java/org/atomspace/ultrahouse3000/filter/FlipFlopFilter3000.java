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
package org.atomspace.ultrahouse3000.filter;

import org.apache.camel.Header;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * FILTER for get only single values when change upper 3000
 */
@Component
public class FlipFlopFilter3000 {
    
    private Logger log = Logger.getLogger(FlipFlopFilter3000.class);
    
    public static boolean flipFlop = false;
    
    public boolean isFliped(@Header("reflectivity") int reflectivity) {
        if(flipFlop){
            if(reflectivity>3000){
                log.debug("FLIP-Reflectivity: " + reflectivity);
                flipFlop=false;
                return true;
            }
            
        }else {
            if(reflectivity<3000){
                log.debug("FLOP-Reflectivity: " + reflectivity);
                flipFlop=true;
            }
        }

        return false;
    }
}
