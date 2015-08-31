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
package org.mybatis.generator.codegen.ibatis2;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyRegistry;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class CustomIntrospectedTableImpl extends IntrospectedTableIbatis2Java5Impl {
    @Override
    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        List<GeneratedJavaFile> answer = new ArrayList<GeneratedJavaFile>();

        for (AbstractJavaGenerator javaGenerator : javaModelGenerators) {
            List<CompilationUnit> compilationUnits = javaGenerator
                    .getCompilationUnits();
            for (CompilationUnit compilationUnit : compilationUnits) {
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                        context.getJavaModelGeneratorConfiguration()
                                .getTargetProject(),
                                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                                context.getJavaFormatter());
                answer.add(gjf);
            }
        }

        for (AbstractJavaGenerator javaGenerator : daoGenerators) {
            List<CompilationUnit> compilationUnits = javaGenerator
                    .getCompilationUnits();
            for (CompilationUnit compilationUnit : compilationUnits) {
				if(compilationUnit.isJavaInterface()){
					continue;
				}
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                        context.getJavaClientGeneratorConfiguration()
                                .getTargetProject(),
                                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                                context.getJavaFormatter());
                answer.add(gjf);
            }
        }

        return answer;
    }
	
	protected void calculateJavaClientAttributes() {
		if (context.getJavaClientGeneratorConfiguration() == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(calculateJavaClientImplementationPackage());
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("Dao"); //$NON-NLS-1$
		setDAOImplementationType(sb.toString());

		sb.setLength(0);
		sb.append(calculateJavaClientInterfacePackage());
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("DaoInterface"); //$NON-NLS-1$
		setDAOInterfaceType(sb.toString());

		sb.setLength(0);
		sb.append(calculateJavaClientInterfacePackage());
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("Mapper"); //$NON-NLS-1$
		setMyBatis3JavaMapperType(sb.toString());

		sb.setLength(0);
		sb.append(calculateJavaClientInterfacePackage());
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("SqlProvider"); //$NON-NLS-1$
		setMyBatis3SqlProviderType(sb.toString());
	}
	protected void calculateModelAttributes() {
		String pakkage = calculateJavaModelPackage();

		StringBuilder sb = new StringBuilder();
		sb.append(pakkage);
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("Key"); //$NON-NLS-1$
		setPrimaryKeyType(sb.toString());

		sb.setLength(0);
		sb.append(pakkage);
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		setBaseRecordType(sb.toString());

		sb.setLength(0);
		sb.append(pakkage);
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("WithBLOBs"); //$NON-NLS-1$
		setRecordWithBLOBsType(sb.toString());

		sb.setLength(0);
		sb.append(pakkage);
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("Example"); //$NON-NLS-1$
		setExampleType(sb.toString());
	}
	protected String calculateIbatis2SqlMapFileName() {
		StringBuilder sb = new StringBuilder();
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("Mapper.xml"); //$NON-NLS-1$
		return sb.toString();
	}
}
