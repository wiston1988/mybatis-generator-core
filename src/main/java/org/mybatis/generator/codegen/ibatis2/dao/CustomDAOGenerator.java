/*
 *  Copyright 2008 The Apache Software Foundation
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
package org.mybatis.generator.codegen.ibatis2.dao;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.ibatis2.dao.elements.AbstractDAOElementGenerator;
import org.mybatis.generator.codegen.ibatis2.dao.elements.CountByExampleMethodGenerator;
import org.mybatis.generator.codegen.ibatis2.dao.elements.SelectByExampleWithoutBLOBsMethodGenerator;
import org.mybatis.generator.codegen.ibatis2.dao.templates.CustomDAOTemplate;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class CustomDAOGenerator extends DAOGenerator {


	public CustomDAOGenerator() {
		super(new CustomDAOTemplate(), true);
	}
}
