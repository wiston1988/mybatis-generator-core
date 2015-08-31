package org.mybatis.generator.plugins;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.ibatis2.Ibatis2FormattingUtilities;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.rules.CustomRulesDelegate;
import org.mybatis.generator.internal.rules.Rules;

/**
 * 增加公用条件查询类 Criteria
 * 
 * @author chenhui
 * @date 2014-12-16 下午03:33:48
 */
public class CustomNeteaseCodePlugin extends PluginAdapter {
	/**
	 * Logger for this class
	 */
	private static final String CRITERIA_WHERE_CLAUSE = "Criteria_Where_Clause";
	private FullyQualifiedJavaType criteria;
	/** 数据库类型 */
	private String databaseType;
	
	private String superClass;
	private String DBRouteClass;
	private String DBRouteMethod;
	private String selectByCriteria;
	private String countByCriteria;
	
	public void initialized(IntrospectedTable introspectedTable) {
		CustomRulesDelegate customRules = new CustomRulesDelegate(introspectedTable.getRules());
		 introspectedTable.setRules(customRules);
		 countByCriteria= "count"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"ByCriteria";
		 selectByCriteria= "select"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"ByCriteria";
	}
	public boolean validate(List<String> warnings) {
		databaseType = context.getJdbcConnectionConfiguration().getDriverClass();
		String criterias = context.getJavaModelGeneratorConfiguration().getTargetPackage() + ".Criteria";
		criteria = new FullyQualifiedJavaType(criterias);
		this.superClass=properties.getProperty("superClass");
		this.DBRouteClass=properties.getProperty("DBRouteClass");
		this.DBRouteMethod=properties.getProperty("DBRouteMethod");
		return true;
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

		List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
		TopLevelClass topLevelClass = new TopLevelClass(criteria);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		addClassComment(topLevelClass, "公用条件查询类");
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewMapInstance());
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewHashMapInstance());

		FullyQualifiedJavaType types = new FullyQualifiedJavaType("java.util.Map<java.lang.String, java.lang.Object>");
		Rules rules = introspectedTable.getRules();
		if (rules.generateUpdateByExampleSelective() || rules.generateUpdateByExampleWithBLOBs()
				|| rules.generateUpdateByExampleWithoutBLOBs()) {
			Method method = new Method();
			method.setVisibility(JavaVisibility.PROTECTED);
			method.setConstructor(true);
			method.setName(criteria.getShortName());
			method.addParameter(new Parameter(criteria, "example")); //$NON-NLS-1$
			method.addBodyLine("this.orderByClause = example.orderByClause;"); //$NON-NLS-1$
			method.addBodyLine("this.condition = example.condition;"); //$NON-NLS-1$
			method.addBodyLine("this.distinct = example.distinct;"); //$NON-NLS-1$
			if (databaseType.contains("oracle")) {
				method.addBodyLine("this.oracleStart = example.oracleStart;"); //$NON-NLS-1$
				method.addBodyLine("this.oracleEnd = example.oracleEnd;"); //$NON-NLS-1$
			}
			if (databaseType.contains("mysql")) {
				method.addBodyLine("this.mysqlLength = example.mysqlLength;"); //$NON-NLS-1$
				method.addBodyLine("this.mysqlOffset = example.mysqlOffset;"); //$NON-NLS-1$
			}
			topLevelClass.addMethod(method);
		}

		Field field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(types);
		field.setName("condition"); //$NON-NLS-1$
		addFieldComment(field, "存放条件查询值");
		topLevelClass.addField(field);

		// add field, getter, setter for distinct
		field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setName("distinct"); //$NON-NLS-1$
		addFieldComment(field, "是否相异");
		topLevelClass.addField(field);

		// add field, getter, setter for orderby clause
		field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		field.setType(FullyQualifiedJavaType.getStringInstance());
		field.setName("orderByClause"); //$NON-NLS-1$
		addFieldComment(field, "排序字段");
		topLevelClass.addField(field);

		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setConstructor(true);
		method.setName("Criteria"); //$NON-NLS-1$
		method.addBodyLine("condition = new HashMap<String, Object>();"); //$NON-NLS-1$
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("clear"); //$NON-NLS-1$
		method.addBodyLine("condition.clear();"); //$NON-NLS-1$
		method.addBodyLine("orderByClause = null;"); //$NON-NLS-1$
		method.addBodyLine("distinct = false;"); //$NON-NLS-1$
		if (databaseType.contains("oracle")) {
			method.addBodyLine("this.oracleStart=null;"); //$NON-NLS-1$
			method.addBodyLine("this.oracleEnd=null;"); //$NON-NLS-1$
		}
		if (databaseType.contains("mysql")) {
			method.addBodyLine("this.mysqlOffset = null;"); //$NON-NLS-1$
			method.addBodyLine("this.mysqlLength = null;"); //$NON-NLS-1$
		}
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(criteria);
		method.setName("put"); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value")); //$NON-NLS-1$
		method.addBodyLine("this.condition.put(condition, value);"); //$NON-NLS-1$
		method.addBodyLine("return (Criteria) this;"); //$NON-NLS-1$
		addSetterComment(method, OutputUtilities.lineSeparator+"\t *            查询的条件名称"+OutputUtilities.lineSeparator+"\t * @param value"+OutputUtilities.lineSeparator+"\t *            查询的值", "condition");
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("setOrderByClause"); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "orderByClause")); //$NON-NLS-1$
		method.addBodyLine("this.orderByClause = orderByClause;"); //$NON-NLS-1$
		addSetterComment(method, OutputUtilities.lineSeparator+"\t *            排序字段", "orderByClause");
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("setDistinct"); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), "distinct")); //$NON-NLS-1$
		method.addBodyLine("this.distinct = distinct;"); //$NON-NLS-1$
		addSetterComment(method, OutputUtilities.lineSeparator+"\t *            是否相异", "distinct");
		topLevelClass.addMethod(method);

		if (databaseType.contains("oracle")) {
			// 增加开始处
			// 增加oracleStart、oracleEnd、mysqlOffset、mysqlLength
			// add field, getter, setter for oracleStart clause
			field = new Field();
			field.setVisibility(JavaVisibility.PRIVATE);
			field.setType(FullyQualifiedJavaType.getInteger());
			field.setName("oracleStart"); //$NON-NLS-1$
			topLevelClass.addField(field);

			method = new Method();
			method.setVisibility(JavaVisibility.PUBLIC);
			method.setName("setOracleStart"); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getInteger(), "oracleStart")); //$NON-NLS-1$
			method.addBodyLine("this.oracleStart = oracleStart;"); //$NON-NLS-1$
			addSetterComment(method, "开始记录数", "oracleStart");
			topLevelClass.addMethod(method);

			// add field, getter, setter for oracleEnd clause
			field = new Field();
			field.setVisibility(JavaVisibility.PRIVATE);
			field.setType(FullyQualifiedJavaType.getInteger());
			field.setName("oracleEnd"); //$NON-NLS-1$
			topLevelClass.addField(field);

			method = new Method();
			method.setVisibility(JavaVisibility.PUBLIC);
			method.setName("setOracleEnd"); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getInteger(), "oracleEnd")); //$NON-NLS-1$
			method.addBodyLine("this.oracleEnd = oracleEnd;"); //$NON-NLS-1$
			addSetterComment(method, "结束记录数", "oracleEnd");
			topLevelClass.addMethod(method);

		} else if (databaseType.contains("mysql")) {
			// add field, getter, setter for mysqlOffset clause
			field = new Field();
			field.setVisibility(JavaVisibility.PRIVATE);
			field.setType(FullyQualifiedJavaType.getInteger());
			field.setName("mysqlOffset"); //$NON-NLS-1$
			topLevelClass.addField(field);

			method = new Method();
			method.setVisibility(JavaVisibility.PUBLIC);
			method.setName("setMysqlOffset"); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getInteger(), "mysqlOffset")); //$NON-NLS-1$
			method.addBodyLine("this.mysqlOffset = mysqlOffset;"); //$NON-NLS-1$
			addSetterComment(method, OutputUtilities.lineSeparator+"\t *            指定返回记录行的偏移量<br>"+OutputUtilities.lineSeparator+"\t *            "
					+ "mysqlOffset= 5,mysqlLength=10;  // 检索记录行 6-15", "mysqlOffset");
			topLevelClass.addMethod(method);

			// add field, getter, setter for mysqlLength clause
			field = new Field();
			field.setVisibility(JavaVisibility.PRIVATE);
			field.setType(FullyQualifiedJavaType.getInteger());
			field.setName("mysqlLength"); //$NON-NLS-1$
			topLevelClass.addField(field);

			method = new Method();
			method.setVisibility(JavaVisibility.PUBLIC);
			method.setName("setMysqlLength"); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getInteger(), "mysqlLength")); //$NON-NLS-1$
			method.addBodyLine("this.mysqlLength = mysqlLength;"); //$NON-NLS-1$
			addSetterComment(method, OutputUtilities.lineSeparator+"\t *            指定返回记录行的最大数目"
					+ "<br>"+OutputUtilities.lineSeparator+"\t *            mysqlOffset= 5,mysqlLength=10;  // 检索记录行 6-15", "mysqlLength");
			topLevelClass.addMethod(method);

			// 增加结束处
		}

		GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, context.getJavaModelGeneratorConfiguration()
				.getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());
		files.add(file);
		return files;
	}

	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		Parameter parameter = new Parameter(criteria, "example");
		// 实现类
		topLevelClass.getSuperInterfaceTypes().clear();//清空接口
		topLevelClass.setSuperClass(superClass);
		topLevelClass.addImportedType(new FullyQualifiedJavaType(superClass));
		topLevelClass.addImportedType(new FullyQualifiedJavaType(DBRouteClass));
		topLevelClass.addImportedType(criteria);
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewListInstance());
		
		//增加2个方法
		Method method1 = new Method();
		method1.setVisibility(JavaVisibility.PUBLIC);
		method1.setReturnType(FullyQualifiedJavaType.getIntInstance());
		method1.setName(countByCriteria);
		method1.addParameter(new Parameter(criteria, "criteria")); 
		StringBuilder sb1 = new StringBuilder();
		sb1.append("Integer count = (Integer)  "); //$NON-NLS-1$
		sb1.append(MessageFormat.format("getQueryDelegate().queryForObject(\"{0}.{1}\", {2}, [DB_Route]);", new Object[] { introspectedTable.getIbatis2SqlMapNamespace(), countByCriteria,
		"criteria" })); //$NON-NLS-1$
		method1.addBodyLine(sb1.toString());
		method1.addBodyLine("return count;"); //$NON-NLS-1$
		topLevelClass.addMethod(method1);
		
		Method method2 = new Method();
		method2.setVisibility(JavaVisibility.PUBLIC);
		method2.setReturnType(FullyQualifiedJavaType.getNewListInstance());
		method2.setName(selectByCriteria);
		method2.addParameter(new Parameter(criteria, "criteria")); 
		StringBuilder sb2 = new StringBuilder();
		sb2.append(method2.getReturnType().getShortName());
		sb2.append(" list = "); //$NON-NLS-1$
		sb2.append(MessageFormat.format("getQueryDelegate().queryForList(\"{0}.{1}\", {2}, [DB_Route]);", new Object[] { introspectedTable.getIbatis2SqlMapNamespace(), selectByCriteria,
		"criteria" })); //$NON-NLS-1$
		method2.addBodyLine(sb2.toString());
		method2.addBodyLine("return list;"); //$NON-NLS-1$
		topLevelClass.addMethod(method2);
		
		List<Method> methods = topLevelClass.getMethods();
		for (int i = 0; i < methods.size(); i++) {
			Method method = methods.get(i);
			if (method.getFormattedContent(0, true).contains("[DB_Route]")) {
				List<String> bodyLines = method.getBodyLines();
				List<String> bodyLinesTemp = new ArrayList<String>();
				for(String line:bodyLines){
					String temp=line.replace("[DB_Route]", DBRouteMethod);
					bodyLinesTemp.add(temp);
				}
				method.removeAllBodyLines();
				method.addBodyLines(bodyLinesTemp);
			}
			
		}
		// 内部类
		if(topLevelClass.getInnerClasses()!=null&&topLevelClass.getInnerClasses().size()>0){
			InnerClass in = topLevelClass.getInnerClasses().get(0);
			in.setSuperClass(criteria);
			Method method = in.getMethods().get(0);
			method.removeParameter(1);
			method.addParameter(1, parameter);
		}

		return true;
	}

	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		List<Element> list = document.getRootElement().getElements();
		//增加count by criteria
		XmlElement element1 = new XmlElement("select"); //$NON-NLS-1$

		element1.addAttribute(new Attribute("id", countByCriteria)); //$NON-NLS-1$
		element1.addAttribute(new Attribute("parameterClass", criteria.getFullyQualifiedName())); //$NON-NLS-1$
		element1.addAttribute(new Attribute("resultClass", "java.lang.Integer")); //$NON-NLS-1$ //$NON-NLS-2$
		context.getCommentGenerator().addComment(element1);
		StringBuilder sb1 = new StringBuilder();
		sb1.append("select count(*) from "); //$NON-NLS-1$
		sb1.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
		element1.addElement(new TextElement(sb1.toString()));
		XmlElement includeElement = new XmlElement("include"); //$NON-NLS-1$
		sb1.setLength(0);
		sb1.append(introspectedTable.getIbatis2SqlMapNamespace());
		sb1.append('.');
		sb1.append(CRITERIA_WHERE_CLAUSE);
		includeElement.addAttribute(new Attribute("refid", //$NON-NLS-1$
				sb1.toString()));
		element1.addElement(includeElement);
		document.getRootElement().addElement(2, element1);

		//增加select by criteria
		XmlElement element2 = new XmlElement("select"); //$NON-NLS-1$
		element2.addAttribute(new Attribute("id", selectByCriteria));
		element2.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId())); //$NON-NLS-1$
		element2.addAttribute(new Attribute("parameterClass", criteria.getFullyQualifiedName())); //$NON-NLS-1$
		context.getCommentGenerator().addComment(element2);
		element2.addElement(new TextElement("select")); //$NON-NLS-1$
//		XmlElement isParameterPresent = new XmlElement("isParameterPresent"); //$NON-NLS-1$
//		XmlElement isEqualElement = new XmlElement("isEqual"); //$NON-NLS-1$
//		isEqualElement.addAttribute(new Attribute("property", "distinct")); //$NON-NLS-1$ //$NON-NLS-2$
//		isEqualElement.addAttribute(new Attribute("compareValue", "true")); //$NON-NLS-1$ //$NON-NLS-2$
//		isEqualElement.addElement(new TextElement("distinct")); //$NON-NLS-1$
//		isParameterPresent.addElement(isEqualElement);
//		element2.addElement(isParameterPresent);
		XmlElement baseColumnElement = new XmlElement("include"); //$NON-NLS-1$
		baseColumnElement.addAttribute(new Attribute("refid", //$NON-NLS-1$
				introspectedTable.getIbatis2SqlMapNamespace() + "." + introspectedTable.getBaseColumnListId()));
		element2.addElement(baseColumnElement);
		StringBuilder sb2 = new StringBuilder();
		sb2.append("from "); //$NON-NLS-1$
		sb2.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
		element2.addElement((new TextElement(sb2.toString())));
		XmlElement isParameterPresenteElement = new XmlElement("isParameterPresent"); //$NON-NLS-1$
		element2.addElement(isParameterPresenteElement);
//		XmlElement includeElement = new XmlElement("include"); //$NON-NLS-1$
//		includeElement.addAttribute(new Attribute("refid", //$NON-NLS-1$
//				introspectedTable.getIbatis2SqlMapNamespace() + "." + introspectedTable.getExampleWhereClauseId())); //$NON-NLS-1$
		isParameterPresenteElement.addElement(includeElement);
		XmlElement isNotNullElement = new XmlElement("isNotNull"); //$NON-NLS-1$
		isNotNullElement.addAttribute(new Attribute("property", "orderByClause")); //$NON-NLS-1$ //$NON-NLS-2$
		isNotNullElement.addElement(new TextElement("order by $orderByClause$")); //$NON-NLS-1$
		isParameterPresenteElement.addElement(isNotNullElement);
		

		XmlElement mysqlLimitIncludeElement = new XmlElement("include");
		mysqlLimitIncludeElement.addAttribute(new Attribute("refid", introspectedTable.getIbatis2SqlMapNamespace()+".Mysql_Pagination_Limit"));
		// 在最后增加
		element2.addElement(element2.getElements().size(), mysqlLimitIncludeElement);
		document.getRootElement().addElement(2, element2);
		
		document.getRootElement().addElement(2, createCriteriaWhereElement(introspectedTable));
		if (databaseType.contains("oracle")) {
			document.getRootElement().addElement(2,getOracleHead());
			document.getRootElement().addElement(2,getOracleTail());
		} else if (databaseType.contains("mysql")) {
			document.getRootElement().addElement(2,getMysqlLimit());
		}
		return true;
	}
	private XmlElement createCriteriaWhereElement(IntrospectedTable introspectedTable){
		//增加 criteria where
		XmlElement criteriaWhereElement = new XmlElement("sql"); //$NON-NLS-1$
		criteriaWhereElement.addAttribute(new Attribute("id", CRITERIA_WHERE_CLAUSE)); //$NON-NLS-1$
		context.getCommentGenerator().addComment(criteriaWhereElement);
		StringBuilder sb = new StringBuilder();
		XmlElement dynamicElement = new XmlElement("dynamic"); //$NON-NLS-1$
		dynamicElement.addAttribute(new Attribute("prepend", "where")); //$NON-NLS-1$ //$NON-NLS-2$
		criteriaWhereElement.addElement(dynamicElement);

		for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
			XmlElement isNotNullElement = new XmlElement("isNotNull"); //$NON-NLS-1$
			isNotNullElement.addAttribute(new Attribute("prepend", "and")); //$NON-NLS-1$ //$NON-NLS-2$
			isNotNullElement.addAttribute(new Attribute("property", introspectedColumn.getJavaProperty("condition."))); //$NON-NLS-1$
			dynamicElement.addElement(isNotNullElement);

			sb.setLength(0);
			sb.append(Ibatis2FormattingUtilities.getEscapedColumnName(introspectedColumn));
			sb.append(" = "); //$NON-NLS-1$
			sb.append(Ibatis2FormattingUtilities.getParameterClause(introspectedColumn, "condition."));
			isNotNullElement.addElement(new TextElement(sb.toString()));
		}
		return criteriaWhereElement;
	}
//	@Override
//	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
//			IntrospectedTable introspectedTable) {
//		if (databaseType.contains("oracle")) {
//			XmlElement oracleHeadIncludeElement = new XmlElement("include");
//			oracleHeadIncludeElement.addAttribute(new Attribute("refid", "common.Oracle_Pagination_Head"));
//			// 在第一个地方增加
//			element.addElement(0, oracleHeadIncludeElement);
//
//			XmlElement oracleTailIncludeElement = new XmlElement("include");
//			oracleTailIncludeElement.addAttribute(new Attribute("refid", "common.Oracle_Pagination_Tail"));
//			// 在最后增加
//			element.addElement(element.getElements().size(), oracleTailIncludeElement);
//		} else if (databaseType.contains("mysql")) {
//			XmlElement mysqlLimitIncludeElement = new XmlElement("include");
//			mysqlLimitIncludeElement.addAttribute(new Attribute("refid", "common.Mysql_Pagination_Limit"));
//			// 在最后增加
//			element.addElement(element.getElements().size(), mysqlLimitIncludeElement);
//		}
//		return true;
//	}
//
//	@Override
//	public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element,
//			IntrospectedTable introspectedTable) {
//		if (databaseType.contains("oracle")) {
//			XmlElement oracleHeadIncludeElement = new XmlElement("include");
//			oracleHeadIncludeElement.addAttribute(new Attribute("refid", "common.Oracle_Pagination_Head"));
//			// 在第一个地方增加
//			element.addElement(0, oracleHeadIncludeElement);
//
//			XmlElement oracleTailIncludeElement = new XmlElement("include");
//			oracleTailIncludeElement.addAttribute(new Attribute("refid", "common.Oracle_Pagination_Tail"));
//			// 在最后增加
//			element.addElement(element.getElements().size(), oracleTailIncludeElement);
//		} else if (databaseType.contains("mysql")) {
//			XmlElement mysqlLimitIncludeElement = new XmlElement("include");
//			mysqlLimitIncludeElement.addAttribute(new Attribute("refid", "common.Mysql_Pagination_Limit"));
//			// 在最后增加
//			element.addElement(element.getElements().size(), mysqlLimitIncludeElement);
//		}
//		return true;
//	}

//	@Override
//	public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
//		Document document = new Document(XmlConstants.IBATIS2_SQL_MAP_PUBLIC_ID, XmlConstants.IBATIS2_SQL_MAP_SYSTEM_ID);
//		XmlElement answer = new XmlElement("sqlMap"); //$NON-NLS-1$
//		document.setRootElement(answer);
//		answer.addAttribute(new Attribute("namespace", //$NON-NLS-1$
//				"common"));
//
//		if (databaseType.contains("oracle")) {
//			answer.addElement(getOracleHead());
//			answer.addElement(getOracleTail());
//		} else if (databaseType.contains("mysql")) {
//			answer.addElement(getMysqlLimit());
//		}
//
//		GeneratedXmlFile gxf = new GeneratedXmlFile(document, properties.getProperty("fileName", "CommonMapper.xml"), //$NON-NLS-1$ //$NON-NLS-2$
//				context.getSqlMapGeneratorConfiguration().getTargetPackage(), //$NON-NLS-1$
//				context.getSqlMapGeneratorConfiguration().getTargetProject(), //$NON-NLS-1$
//				false);
//
//		List<GeneratedXmlFile> files = new ArrayList<GeneratedXmlFile>(1);
//		files.add(gxf);
//		return files;
//	}

	private XmlElement getOracleHead() {
		XmlElement answer = new XmlElement("sql"); //$NON-NLS-1$

		answer.addAttribute(new Attribute("id", "Oracle_Pagination_Head")); //$NON-NLS-1$

		XmlElement dynamicElement = new XmlElement("dynamic");
		XmlElement outerisNotEmptyElement = new XmlElement("isNotNull");
		outerisNotEmptyElement.addAttribute(new Attribute("property", "oracleStart"));
		XmlElement innerisNotEmptyElement = new XmlElement("isNotNull");
		innerisNotEmptyElement.addAttribute(new Attribute("property", "oracleEnd"));
		innerisNotEmptyElement.addElement(new TextElement("<![CDATA[ select * from ( select row_.*, rownum rownum_ from ( ]]>"));
		outerisNotEmptyElement.addElement(innerisNotEmptyElement);
		dynamicElement.addElement(outerisNotEmptyElement);
		answer.addElement(dynamicElement);
		return answer;
	}

	private XmlElement getOracleTail() {
		XmlElement answer = new XmlElement("sql"); //$NON-NLS-1$

		answer.addAttribute(new Attribute("id", "Oracle_Pagination_Tail")); //$NON-NLS-1$

		XmlElement dynamicElement = new XmlElement("dynamic");
		XmlElement outerisNotEmptyElement = new XmlElement("isNotNull");
		outerisNotEmptyElement.addAttribute(new Attribute("property", "oracleStart"));
		XmlElement innerisNotEmptyElement = new XmlElement("isNotNull");
		innerisNotEmptyElement.addAttribute(new Attribute("property", "oracleEnd"));
		innerisNotEmptyElement.addElement(new TextElement(
				"<![CDATA[ ) row_ where rownum <= #oracleEnd# ) where rownum_ > #oracleStart# ]]>"));
		outerisNotEmptyElement.addElement(innerisNotEmptyElement);
		dynamicElement.addElement(outerisNotEmptyElement);
		answer.addElement(dynamicElement);
		return answer;
	}

	private XmlElement getMysqlLimit() {
		XmlElement answer = new XmlElement("sql"); //$NON-NLS-1$

		answer.addAttribute(new Attribute("id", "Mysql_Pagination_Limit")); //$NON-NLS-1$

		XmlElement dynamicElement = new XmlElement("dynamic");
		XmlElement outerisNotEmptyElement = new XmlElement("isNotNull");
		outerisNotEmptyElement.addAttribute(new Attribute("property", "mysqlOffset"));
		XmlElement innerisNotEmptyElement = new XmlElement("isNotNull");
		innerisNotEmptyElement.addAttribute(new Attribute("property", "mysqlLength"));
		innerisNotEmptyElement.addElement(new TextElement("<![CDATA[ limit #mysqlOffset# , #mysqlLength# ]]>"));
		outerisNotEmptyElement.addElement(innerisNotEmptyElement);
		dynamicElement.addElement(outerisNotEmptyElement);
		answer.addElement(dynamicElement);
		return answer;
	}
}
