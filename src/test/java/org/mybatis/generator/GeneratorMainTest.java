/*
 *  Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.mybatis.generator;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.logging.LogFactory;

/**
 * This class allows the code generator to be run from the command line.
 * 
 * @author Jeff Butler
 */
public class GeneratorMainTest {

    public static void main(String[] args) {
    	 List<String> warnings = new ArrayList<String>();  
         ConfigurationParser cp = new ConfigurationParser(warnings);  
   
         boolean overwrite = true;  
         //staticTableConfig.xml,dynamicTableConfig.xml  
         File configFile = new File("E:\\Workspaces\\CorpProjects\\mybatis-generator-core\\src\\test\\resources\\generatorConfigIbatis2.xml");      
         Set<String> contexts = new HashSet<String>();
         Set<String> fullyqualifiedTables = new HashSet<String>();
         try {  
//             Configuration config = cp.parseConfiguration(new GeneratorMainTest().getClass().getClassLoader().getResourceAsStream("generatorConfigIbatis2.xml"));  
        	 Configuration config = cp.parseConfiguration(configFile);  
             DefaultShellCallback callback = new DefaultShellCallback(overwrite);  
             MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,  
                     callback, warnings);  
             myBatisGenerator.generate(null, contexts, fullyqualifiedTables);  
         } catch (Exception e) {  
             e.printStackTrace();  
         }  
      
    }

}
